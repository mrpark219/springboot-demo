package com.example.springbootdemo.global.security;

import com.example.springbootdemo.domain.entity.Member;
import com.example.springbootdemo.domain.member.MemberJpaRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component("userDetailsService")
public class CustomUserDetailsService implements UserDetailsService {

	private final MemberJpaRepository memberJpaRepository;

	public CustomUserDetailsService(MemberJpaRepository memberJpaRepository) {
		this.memberJpaRepository = memberJpaRepository;
	}

	@Override
	@Transactional
	public UserDetails loadUserByUsername(final String email) {
		return memberJpaRepository.findMemberWithAuthoritiesByEmail(email)
			.map(member -> createUser(email, member))
			.orElseThrow(() -> new UsernameNotFoundException(email + " -> 데이터베이스에서 찾을 수 없습니다."));
	}

	private org.springframework.security.core.userdetails.User createUser(String email, Member member) {
		List<GrantedAuthority> grantedAuthorities = member.getMemberAuthorities().stream()
			.map(memberAuthority -> new SimpleGrantedAuthority(memberAuthority.getAuthority().getName()))
			.collect(Collectors.toList());
		return new org.springframework.security.core.userdetails.User(member.getEmail(),
			member.getPassword(),
			grantedAuthorities);
	}
}
