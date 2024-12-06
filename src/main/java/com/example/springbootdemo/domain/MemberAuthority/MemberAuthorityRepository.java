package com.example.springbootdemo.domain.MemberAuthority;

import com.example.springbootdemo.domain.entity.MemberAuthority;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberAuthorityRepository extends JpaRepository<MemberAuthority, Long> {
}
