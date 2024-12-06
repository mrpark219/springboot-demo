package com.example.springbootdemo.domain.member;

import com.example.springbootdemo.domain.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

	Optional<Member> findByEmail(String email);

	Optional<Member> findByEmailAndPassword(String email, String password);
}
