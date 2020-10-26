# Stateless JWT Authentication with Spring Boot

This example project demonstrates the usage of Spring Boot OAuth2 Resource Server
with JWT configuration to protect a REST API with JWT based authentication.

Furthermore, provides a "/login" endpoint to generate and issue JWTs upon
successful login by the users.

This approach is ideal to be used as the 
**backend for a single page application (SPA)** written using a frontend framework like
ReactJS, Angular, etc...

## Getting Started

- Use `mvn clean install` in the project root directory to build the project. 
- Run the main class, `com.example.springboot.jwt.JwtApplication` to start the application.

## Endpoints

- `/login` -> Public endpoint which returns a signed JWT for valid user credentials (username/password)
- `/user` -> A protected endpoint which returns the user details of the 
requesting user.