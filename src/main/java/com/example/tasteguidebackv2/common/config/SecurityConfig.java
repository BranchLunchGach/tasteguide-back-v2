package com.example.tasteguidebackv2.common.config;

import com.example.tasteguidebackv2.common.jwt.JwtAuthenticationEntryPoint;
import com.example.tasteguidebackv2.common.jwt.JwtFilter;
import com.example.tasteguidebackv2.common.oauth2.CustomOAuth2UserService;
import com.example.tasteguidebackv2.common.oauth2.OAuth2LoginSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

	private final JwtFilter jwtFilter;
	private final CustomOAuth2UserService customOAuth2UserService;
	private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	/**
	 * Creates and returns a {@link PasswordEncoder} that uses BCrypt hashing for encoding passwords.
	 *
	 * @return a BCrypt-based password encoder
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Configures and returns the application's security filter chain.
	 *
	 * Sets up stateless session management, disables CSRF protection, and defines authorization rules for public, refresh, and admin endpoints. Integrates JWT authentication, OAuth2 login with custom user service and success handler, and configures a custom entry point for authentication exceptions.
	 *
	 * @param http the HttpSecurity to configure
	 * @return the configured SecurityFilterChain
	 * @throws Exception if an error occurs during configuration
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		http
			.csrf(AbstractHttpConfigurer::disable)
			.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(auth -> auth
				.requestMatchers(SecurityUrlMatcher.PUBLIC_URLS).permitAll()
				.requestMatchers(SecurityUrlMatcher.REFRESH_URL).authenticated()
				.requestMatchers(SecurityUrlMatcher.ADMIN_URLS).hasRole("ADMIN")
				.anyRequest().authenticated()
			)
			.oauth2Login(oauth -> oauth
				.userInfoEndpoint(user -> user.userService(customOAuth2UserService))
				.successHandler(oAuth2LoginSuccessHandler)
			)
			.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
			.exceptionHandling(exception -> exception
					.authenticationEntryPoint(jwtAuthenticationEntryPoint)
			);

		return http.build();
	}

}