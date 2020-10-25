package com.example.springboot.jwt;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;
import org.springframework.stereotype.Component;

@Component
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http
				.cors()
				.and()
				.csrf().disable()
				.authorizeRequests(configurer ->
						configurer
								.antMatchers(
										"/error",
										"/login"
								)
								.permitAll()
								.anyRequest()
								.authenticated()
				)
				.exceptionHandling().disable()
				.oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
	}
}
