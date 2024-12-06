package com.example.springbootdemo.domain.member;

import com.example.springbootdemo.domain.MemberAuthority.MemberAuthorityRepository;
import com.example.springbootdemo.domain.authority.AuthorityRepository;
import com.example.springbootdemo.domain.entity.Authority;
import com.example.springbootdemo.domain.entity.Member;
import com.example.springbootdemo.domain.entity.MemberAuthority;
import com.example.springbootdemo.domain.member.dto.LoginRequestDto;
import com.example.springbootdemo.domain.member.dto.SignUpRequestDto;
import com.example.springbootdemo.domain.member.dto.SignUpResponseDto;
import com.example.springbootdemo.domain.member.dto.TokenDto;
import com.example.springbootdemo.global.Exception.ApiException;
import com.example.springbootdemo.global.redis.RefreshToken;
import com.example.springbootdemo.global.redis.RefreshTokenRepository;
import com.example.springbootdemo.global.security.TokenProvider;
import com.example.springbootdemo.global.security.TokenType;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MemberService {

	private final PasswordEncoder passwordEncoder;
	private final MemberRepository memberRepository;
	private final RefreshTokenRepository refreshTokenRepository;
	private final AuthorityRepository authorityRepository;
	private final MemberAuthorityRepository memberAuthorityRepository;
	private final TokenProvider tokenProvider;
	private final AuthenticationManagerBuilder authenticationManagerBuilder;

	public MemberService(PasswordEncoder passwordEncoder, MemberRepository memberRepository, RefreshTokenRepository refreshTokenRepository, AuthorityRepository authorityRepository, MemberAuthorityRepository memberAuthorityRepository, TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder) {
		this.passwordEncoder = passwordEncoder;
		this.memberRepository = memberRepository;
		this.refreshTokenRepository = refreshTokenRepository;
		this.authorityRepository = authorityRepository;
		this.memberAuthorityRepository = memberAuthorityRepository;
		this.tokenProvider = tokenProvider;
		this.authenticationManagerBuilder = authenticationManagerBuilder;
	}

	@Transactional
	public SignUpResponseDto signup(SignUpRequestDto signupRequestDto) {

		if(memberRepository.findByEmail(signupRequestDto.getEmail()).orElse(null) != null) {
			throw new ApiException("이미 가입되어 있는 유저입니다.", HttpStatus.CONFLICT);
		}
		Authority authority = authorityRepository.findByName("ROLE_USER")
			.orElseThrow(() -> new RuntimeException("Role Not Found"));

		Member member = new Member(signupRequestDto.getEmail(),
			passwordEncoder.encode(signupRequestDto.getPassword()),
			signupRequestDto.getEtc());

		MemberAuthority memberAuthority = new MemberAuthority();
		memberAuthority.changeMember(member);
		memberAuthority.changeAuthority(authority);

		Member saveMember = memberRepository.save(member);
		memberAuthorityRepository.save(memberAuthority);

		return SignUpResponseDto.form(saveMember);
	}

	@Transactional
	public TokenDto login(LoginRequestDto loginRequestDto) {

		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword());

		Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

		String accessToken = tokenProvider.createToken(authentication, TokenType.ACCESS);
		String refreshToken = tokenProvider.createToken(authentication, TokenType.REFRESH);

		//refresh 토큰 관련 처리
		Optional<RefreshToken> savedRefreshToken = refreshTokenRepository.findById(loginRequestDto.getEmail());
		if(savedRefreshToken.isPresent()) {
			refreshTokenRepository.save(savedRefreshToken.get().updateToken(refreshToken));
		}
		else {
			RefreshToken newRefreshToken = new RefreshToken(loginRequestDto.getEmail(), refreshToken);
			refreshTokenRepository.save(newRefreshToken);
		}

		return TokenDto.builder()
			.accessToken(accessToken)
			.refreshToken(refreshToken)
			.build();
	}

	@Transactional
	public void logout(String currentMemberEmail) {

		refreshTokenRepository.findById(currentMemberEmail)
			.orElseThrow(() -> new ApiException("유효한 Refresh 토큰이 없습니다.", HttpStatus.UNAUTHORIZED));

		refreshTokenRepository.deleteById(currentMemberEmail);
		SecurityContextHolder.clearContext();
	}
}
