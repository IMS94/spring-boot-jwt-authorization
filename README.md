# Role Based Access Control (RBAC) with Spring Boot and JWT

This repo hosts the source code for the article [**Role Based Access Control (RBAC) with Spring Boot and JWT**](https://medium.com/geekculture/role-based-access-control-rbac-with-spring-boot-and-jwt-bc20a8c51c15?source=github_source).

This example project demonstrates how to use Spring Boot's inbuilt OAuth2 Resource Server to authenticate and
authorize REST APIs with JWT. First, we have enabled **JWT authentication** and secondly, have introduced
**Role Based Access Control (RBAC)** by mapping a `roles` claim in JWT to granted authorities in Spring Security.

Furthermore, provides a `/login` endpoint to generate and issue JWTs upon
successful login by the users.

This approach is ideal to be used as the
**backend for a single page application (SPA)** written using a frontend framework like
ReactJS, Angular, etc.

## Requirements

- **Java 17** or higher
- **Maven 3.6+**
- **Spring Boot 3.5.x**

## Solution Overview

![Solution Overview](https://github.com/IMS94/spring-boot-jwt-authorization/blob/master/authorization_process.png?raw=true "Solution Overview")

## Role Based Access Control

An example of role based access control.

![RBAC Example](https://github.com/IMS94/spring-boot-jwt-authorization/blob/master/rbac_sample.png?raw=true "RBAC Example")

## JWT Authentication Overview

![JWT Authentication Overview](https://github.com/IMS94/spring-boot-jwt-authorization/blob/master/solution_overview.png?raw=true "JWT Authentication Overview")

## Getting Started

1. Ensure you have **Java 17** or higher installed
2. Build the project: `mvn clean install`
3. Run the main class `com.example.springboot.jwt.JwtApplication` to start the application
4. The application starts on `http://localhost:8080`

## API Endpoints

### Authentication

| Endpoint | Method | Auth Required | Content-Type | Description |
|----------|--------|:---:|---|---|
| `/login` | POST | No | `application/x-www-form-urlencoded` | Accepts `username` and `password` form params, returns a signed JWT |

### Products (Protected)

| Endpoint | Method | Required Authority | Description |
|----------|--------|---|---|
| `/products` | GET | `STAFF_MEMBER` | List all products |
| `/products` | POST | `ASSISTANT_MANAGER`, `MANAGER`, or `ADMIN` | Add a new product (JSON body: `name`, `description`) |
| `/products/{id}` | DELETE | `ADMIN` or `MANAGER` | Delete a product by ID |

## Demo Users

Four in-memory users are pre-configured for testing:

| Username | Password | Authorities |
|----------|----------|---|
| `user1` | `1234` | `ADMIN`, `STAFF_MEMBER` |
| `user2` | `1234` | `STAFF_MEMBER` |
| `user3` | `1234` | `ASSISTANT_MANAGER`, `STAFF_MEMBER` |
| `user4` | `1234` | `MANAGER`, `STAFF_MEMBER` |

## RBAC Authorization Matrix

The table below shows which operations each individual role grants:

| Role | GET `/products` | POST `/products` | DELETE `/products/{id}` |
|------|:---:|:---:|:---:|
| `STAFF_MEMBER` | Yes | No | No |
| `ASSISTANT_MANAGER` | No | Yes | No |
| `MANAGER` | No | Yes | Yes |
| `ADMIN` | No | Yes | Yes |

> **Note:** Each demo user holds `STAFF_MEMBER` *plus* their primary role, so in practice all four users can list products. A user with **only** `ASSISTANT_MANAGER` (and no `STAFF_MEMBER`) would be denied `GET /products`.

### Per-User Access Summary

| User | List Products | Add Product | Delete Product |
|------|:---:|:---:|:---:|
| `user1` (ADMIN + STAFF_MEMBER) | Yes | Yes | Yes |
| `user2` (STAFF_MEMBER) | Yes | No | No |
| `user3` (ASST_MGR + STAFF_MEMBER) | Yes | Yes | No |
| `user4` (MANAGER + STAFF_MEMBER) | Yes | Yes | Yes |

## Testing

The project includes unit tests that verify the RBAC rules are correctly enforced:

- **`RbacWebMvcTests`** — Parameterized `@WebMvcTest` tests that exercise every endpoint with various role combinations, verifying that the correct HTTP status (200 or 403) is returned and that the service layer is only invoked for authorized requests.
- **`WebSecurityConfigAuthConverterTests`** — Tests that the JWT `roles` claim is correctly mapped to Spring Security granted authorities without any prefix (e.g. no `ROLE_` prefix), and that unrelated claims like `scope` are ignored.

Run the tests with:

```bash
mvn test
```

## Version History

### v0.0.2 — Spring Boot 3.5 / Spring Security 6

Upgraded from Spring Boot 2.5.3 to **3.5.0**, which required migrating to the Spring Security 6 API. Key changes:

| What Changed | Spring Boot 2.x (old) | Spring Boot 3.x (new) |
|---|---|---|
| Security config base class | `extends WebSecurityConfigurerAdapter` | `@Bean SecurityFilterChain` (component-based) |
| Method security annotation | `@EnableGlobalMethodSecurity` | `@EnableMethodSecurity` |
| URL authorization | `authorizeRequests()` | `authorizeHttpRequests()` |
| Path matchers | `antMatchers()` | `requestMatchers()` |
| CORS configuration | `.cors()` (default) | `.cors(cors -> cors.configurationSource(...))` (lambda DSL) |
| Java baseline | Java 8+ | **Java 17+** (required by Jakarta EE 10) |
| JWT library | `com.auth0:java-jwt:3.11.0` | `com.auth0:java-jwt:4.4.0` |
| Servlet namespace | `javax.servlet` | `jakarta.servlet` |
| Test mock annotation | `@MockBean` | `@MockitoBean` (Spring Boot 3.4+) |

Additional changes in this release:

- Added explicit CORS configuration bean (`CorsConfigurationSource`) since the default `.cors()` shorthand was removed.
- Added `server.error.include-message=always` to surface validation error messages in API responses.
- Added RBAC unit tests (`RbacWebMvcTests`, `WebSecurityConfigAuthConverterTests`) using `spring-security-test`.
- Added `spring-security-test` dependency and Lombok annotation processor configuration to `pom.xml`.

### v0.0.1 — Initial Release (Spring Boot 2.5.3)

Original implementation with Spring Boot 2.5.3, Spring Security 5, and `WebSecurityConfigurerAdapter`.
