# Dependency Audit Report

## Project Overview

This project demonstrates **Role Based Access Control (RBAC) with Spring Boot and JWT** using Spring Boot's inbuilt OAuth2 Resource Server for authentication and authorization.

---

## Current Dependency Versions

| Dependency | Current Version | Latest Stable Version | Status |
|------------|-----------------|----------------------|--------|
| Spring Boot | 2.5.3 | 3.5.8 (3.x) / 4.0.0 | ⚠️ Major version behind |
| Java | 1.8 | 17+ (for Spring Boot 3.x) | ⚠️ Major version behind |
| Auth0 java-jwt | 3.11.0 | 4.5.0 | ⚠️ Major version behind |
| Lombok | Managed by parent | 1.18.42 | ✅ OK (managed by Spring Boot) |
| spring-boot-starter-oauth2-resource-server | 2.5.3 | 3.5.8 | ⚠️ Tied to Spring Boot |
| spring-boot-starter-web | 2.5.3 | 3.5.8 | ⚠️ Tied to Spring Boot |

---

## Deprecations in Current Code

### 1. `WebSecurityConfigurerAdapter` (CRITICAL)

**Location:** `WebSecurityConfig.java:21`

```java
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
```

**Status:**
- ⚠️ **Deprecated** in Spring Security 5.7.0-M2
- ❌ **Removed** in Spring Security 6.x (Spring Boot 3.x)

**Impact:** Cannot upgrade to Spring Boot 3.x without rewriting this class.

---

### 2. `@EnableGlobalMethodSecurity` (DEPRECATED)

**Location:** `WebSecurityConfig.java:18-20`

```java
@EnableGlobalMethodSecurity(
        prePostEnabled = true
)
```

**Status:**
- ⚠️ **Deprecated** in Spring Security 6.x
- Replaced by `@EnableMethodSecurity`

**Note:** The new `@EnableMethodSecurity` enables `prePostEnabled` by default.

---

### 3. `authorizeRequests()` (DEPRECATED)

**Location:** `WebSecurityConfig.java:39`

```java
.authorizeRequests(configurer ->
```

**Status:**
- ⚠️ **Deprecated** in Spring Security 6.x
- Replaced by `authorizeHttpRequests()`

---

### 4. `antMatchers()` (DEPRECATED)

**Location:** `WebSecurityConfig.java:41`

```java
.antMatchers(
        "/error",
        "/login"
)
```

**Status:**
- ⚠️ **Deprecated** in Spring Security 6.x
- Replaced by `requestMatchers()`

---

### 5. Method Chaining with `.and()` (DEPRECATED)

**Location:** `WebSecurityConfig.java:35-37`

```java
.cors()
.and()
.csrf().disable()
```

**Status:**
- ⚠️ **Deprecated** style in Spring Security 6.x
- New approach uses lambda-based DSL

---

### 6. `http.oauth2ResourceServer().jwt()` (DEPRECATED)

**Location:** `WebSecurityConfig.java:51-53`

```java
http.oauth2ResourceServer()
        .jwt()
        .jwtAuthenticationConverter(authenticationConverter());
```

**Status:**
- ⚠️ **Deprecated** style in Spring Security 6.x
- New approach uses lambda-based DSL

---

## Spring Boot 3.x / Spring Security 6 - Modern Approach

### New SecurityFilterChain Configuration

The modern approach for Spring Boot 3.x replaces `WebSecurityConfigurerAdapter` with a `SecurityFilterChain` bean:

```java
@Configuration
@EnableMethodSecurity  // Replaces @EnableGlobalMethodSecurity
public class WebSecurityConfig {

    public static final String AUTHORITIES_CLAIM_NAME = "roles";

    private final PasswordEncoder passwordEncoder;

    public WebSecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/error", "/login").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(authenticationConverter()))
            );

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        // Same implementation, but as a @Bean method
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        // ... user creation logic
        return manager;
    }

    @Bean
    public JwtAuthenticationConverter authenticationConverter() {
        JwtGrantedAuthoritiesConverter authoritiesConverter = new JwtGrantedAuthoritiesConverter();
        authoritiesConverter.setAuthorityPrefix("");
        authoritiesConverter.setAuthoritiesClaimName(AUTHORITIES_CLAIM_NAME);

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(authoritiesConverter);
        return converter;
    }
}
```

### Key Differences Summary

| Aspect | Spring Boot 2.x (Current) | Spring Boot 3.x (Modern) |
|--------|---------------------------|--------------------------|
| Java Version | 8+ | **17+ (required)** |
| Security Config | `extends WebSecurityConfigurerAdapter` | `@Bean SecurityFilterChain` |
| Method Security | `@EnableGlobalMethodSecurity` | `@EnableMethodSecurity` |
| Authorization | `authorizeRequests()` | `authorizeHttpRequests()` |
| URL Matchers | `antMatchers()` | `requestMatchers()` |
| Configuration DSL | `.and()` chaining | Lambda-based DSL |
| javax packages | `javax.*` | `jakarta.*` |

---

## Auth0 java-jwt Library Changes (3.x → 4.x)

### Version Comparison

| Feature | Version 3.11.0 (Current) | Version 4.5.0 (Latest) |
|---------|-------------------------|------------------------|
| Java Support | 8, 11 | 8, 11, **17** |
| Security Patches | Older | ✅ Latest vulnerability fixes |
| API Changes | Legacy | Updated, some breaking changes |

### Breaking Changes in 4.x

The 4.x version includes API changes. The core functionality (`JWT.create()`, `Algorithm.RSA256()`, etc.) remains similar, but you should review the [changelog](https://github.com/auth0/java-jwt/releases) for specific breaking changes.

---

## Recommendation

### Should You Upgrade to Spring Boot 3.x?

**YES**, if:
- You want to use the latest security features and patches
- You're starting new development or have resources for refactoring
- You need Java 17+ features (records, sealed classes, pattern matching)
- Your blog/tutorial targets current best practices

**HOLD**, if:
- You need to maintain compatibility with Java 8 environments
- You have limited time for a significant rewrite
- The current implementation is working in production and security is not a concern

### Migration Effort Estimate

| Component | Effort | Description |
|-----------|--------|-------------|
| `WebSecurityConfig.java` | **Medium** | Complete rewrite to SecurityFilterChain pattern |
| `JwtConfiguration.java` | **Low** | Minor updates, mostly compatible |
| `JwtHelper.java` | **Medium** | Update auth0 java-jwt library calls if needed |
| `pom.xml` | **Low** | Update versions, add Jakarta dependencies |
| Testing | **Medium** | Verify all endpoints still work |
| **Total** | **Medium-High** | ~4-8 hours of focused work |

---

## Minor Version Updates (Without Breaking Changes)

If you prefer to stay on Spring Boot 2.x, you can safely update to:

```xml
<!-- Latest Spring Boot 2.x (End of OSS support: Nov 2023) -->
<parent>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-parent</artifactId>
    <version>2.7.18</version>
</parent>
```

**Note:** Spring Boot 2.x is now in commercial support only. The 2.7.x line was the final 2.x release.

---

## Sources

- [Spring Boot End of Life](https://endoflife.date/spring-boot)
- [Spring Boot Releases](https://github.com/spring-projects/spring-boot/releases)
- [Spring Security WebSecurityConfigurerAdapter Deprecation - Baeldung](https://www.baeldung.com/spring-deprecated-websecurityconfigureradapter)
- [Spring Security WebSecurityConfigurerAdapter Fix - CodeJava](https://www.codejava.net/frameworks/spring-boot/fix-websecurityconfigureradapter-deprecated)
- [OAuth 2.0 Resource Server JWT - Spring Security Docs](https://docs.spring.io/spring-security/reference/servlet/oauth2/resource-server/jwt.html)
- [Auth0 java-jwt on Maven Central](https://mvnrepository.com/artifact/com.auth0/java-jwt)
- [Auth0 java-jwt GitHub](https://github.com/auth0/java-jwt)
- [Lombok Maven Setup](https://projectlombok.org/setup/maven)
- [Lombok Changelog](https://projectlombok.org/changelog)
- [Spring Boot 3.5.8 Release](https://spring.io/blog/2025/11/20/spring-boot-3-5-8-available-now/)
