package com.example.springboot.jwt;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Component;

@Component
@EnableGlobalMethodSecurity(
        prePostEnabled = true
)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final String AUTHORITIES_CLAIM_NAME = "roles";

    private final PasswordEncoder passwordEncoder;

    public WebSecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors()
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests(configurer ->
                        configurer
                                .antMatchers(
                                        "/error",
                                        "/login"
                                )
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                );

        // JWT Validation Configuration
        http.oauth2ResourceServer()
                .jwt()
                .jwtAuthenticationConverter(authenticationConverter());
    }

    @Bean
    @Override
    protected UserDetailsService userDetailsService() {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();

        UserDetails user1 = User
                .withUsername("user1")
                .authorities("ADMIN", "STAFF_MEMBER")
                .passwordEncoder(passwordEncoder::encode)
                .password("1234")
                .build();
        manager.createUser(user1);

        UserDetails user2 = User
                .withUsername("user2")
                .authorities("STAFF_MEMBER")
                .passwordEncoder(passwordEncoder::encode)
                .password("1234")
                .build();
        manager.createUser(user2);

        UserDetails user3 = User
                .withUsername("user3")
                .authorities("ASSISTANT_MANAGER", "STAFF_MEMBER")
                .passwordEncoder(passwordEncoder::encode)
                .password("1234")
                .build();
        manager.createUser(user3);

        UserDetails user4 = User
                .withUsername("user4")
                .authorities("MANAGER", "STAFF_MEMBER")
                .passwordEncoder(passwordEncoder::encode)
                .password("1234")
                .build();
        manager.createUser(user4);

        return manager;
    }

    protected JwtAuthenticationConverter authenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("");
        authoritiesConverter.setAuthoritiesClaimName(AUTHORITIES_CLAIM_NAME);

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }
}
