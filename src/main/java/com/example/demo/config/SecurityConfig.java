package com.example.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				// 1. CSRF保護を無効化（POST/PUTなどの403を回避）
				.csrf(AbstractHttpConfigurer::disable)
				// 2. 全てのリクエストに対して認証・認可のチェックをパスさせる)

				.authorizeHttpRequests(authz -> authz
						// 今回は全てのリクエストを許可する。
						// CSRP保護下に置いた構造設計とならなかったことは反省点
						.requestMatchers("/**").permitAll()
						.anyRequest().permitAll()
				// デフォルトのフォームログインなどを有効にする場合
				);

		return http.build();
	}
}
