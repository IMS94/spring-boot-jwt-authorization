package com.example.springboot.jwt;

import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;

import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class WebSecurityConfigAuthConverterTests {

    private final WebSecurityConfig webSecurityConfig = new WebSecurityConfig(
            mock(PasswordEncoder.class), "http://localhost:3000");

    @Test
    void rolesClaimIsMappedToAuthoritiesWithoutPrefix() {
        JwtAuthenticationConverter converter = webSecurityConfig.jwtAuthenticationConverter();
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("roles", "ADMIN STAFF_MEMBER")
                .build();

        AbstractAuthenticationToken authentication = converter.convert(jwt);
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        assertThat(authorities).containsExactlyInAnyOrder("ADMIN", "STAFF_MEMBER");
        assertThat(authorities).allMatch(authority -> !authority.startsWith("ROLE_"));
    }

    @Test
    void missingRolesClaimReturnsEmptyAuthorities() {
        JwtAuthenticationConverter converter = webSecurityConfig.jwtAuthenticationConverter();
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("username", "user1")
                .build();

        AbstractAuthenticationToken authentication = converter.convert(jwt);

        assertThat(authentication.getAuthorities()).isEmpty();
    }

    @Test
    void onlyRolesClaimIsUsedForAuthorities() {
        JwtAuthenticationConverter converter = webSecurityConfig.jwtAuthenticationConverter();
        Jwt jwt = Jwt.withTokenValue("token")
                .header("alg", "none")
                .claim("roles", "MANAGER")
                .claim("scope", "ADMIN STAFF_MEMBER")
                .build();

        AbstractAuthenticationToken authentication = converter.convert(jwt);
        List<String> authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        assertThat(authorities).containsExactly("MANAGER");
        assertThat(WebSecurityConfig.AUTHORITIES_CLAIM_NAME).isEqualTo("roles");
    }
}
