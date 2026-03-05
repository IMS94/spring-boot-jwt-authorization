# Role Based Access Control (RBAC) with Spring Boot and JWT

This repo hosts the source code for the article [**Role Based Access Control (RBAC) with Spring Boot and JWT**](https://medium.com/geekculture/role-based-access-control-rbac-with-spring-boot-and-jwt-bc20a8c51c15?source=github_source).

This example project demonstrates how to use Spring Boot's inbuilt OAuth2 Resource Server to authenticate and
authorize REST APIs with JWT. First, we have enabled **JWT authentication** and secondly, have introduced
**Role Based Access Control (RBAC)** by mapping a roles claim in JWT to granted authorities in Spring Security.

Furthermore, provides a "/login" endpoint to generate and issue JWTs upon
successful login by the users.

This approach is ideal to be used as the
**backend for a single page application (SPA)** written using a frontend framework like
ReactJS, Angular, etc...

## Requirements

- **Java 17** or higher
- **Maven 3.6+**
- **Spring Boot 3.5.x**

## Solution Overview

![Solution Overview](https://github.com/IMS94/spring-boot-jwt-authorization/blob/master/authorization_process.png?raw=true "Solution Overview")

## Role Based Access Control
An example of role based access control.

![RBAC Example](https://github.com/IMS94/spring-boot-jwt-authorization/blob/master/rbac_sample.png?raw=true "Solution Overview")

## JWT Authentication Overview

![Solution Overview](https://github.com/IMS94/spring-boot-jwt-authorization/blob/master/solution_overview.png?raw=true "Solution Overview")

## Getting Started

- Ensure you have **Java 17** or higher installed
- Use `mvn clean install` in the project root directory to build the project
- Run the main class, `com.example.springboot.jwt.JwtApplication` to start the application

## Spring Boot 3.x / Spring Security 6 Changes

This project has been updated to use Spring Boot 3.5 and Spring Security 6, which introduces several important changes:

### Security Configuration (Modern Approach)

The `WebSecurityConfigurerAdapter` class has been **removed** in Spring Security 6. Instead, we now use a component-based approach with `SecurityFilterChain` beans:

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity  // Replaces @EnableGlobalMethodSecurity
public class WebSecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authz -> authz  // Replaces authorizeRequests()
                .requestMatchers("/error", "/login").permitAll()  // Replaces antMatchers()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );

        return http.build();
    }
}
```

### Key Migration Changes

| Old (Spring Boot 2.x) | New (Spring Boot 3.x) |
|----------------------|----------------------|
| `extends WebSecurityConfigurerAdapter` | `@Bean SecurityFilterChain` |
| `@EnableGlobalMethodSecurity` | `@EnableMethodSecurity` |
| `authorizeRequests()` | `authorizeHttpRequests()` |
| `antMatchers()` | `requestMatchers()` |
| Java 8+ | **Java 17+ (required)** |

## Endpoints

- `/login` -> Public endpoint which returns a signed JWT for valid user credentials (username/password)
- `/products` -> Contains several endpoints to add and remove product entities. Protected by JWT authentication and
authorized based on role.
