package com.example.springbootdemo.global.redis;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash("RefreshToken")
@Getter
public class RefreshToken {

	@Id
	private String email;

	private String refreshToken;

	public RefreshToken() {
	}

	public RefreshToken(String email, String refreshToken) {
		this.email = email;
		this.refreshToken = refreshToken;
	}

	public RefreshToken updateToken(String refreshToken) {
		this.refreshToken = refreshToken;
		return this;
	}
}
