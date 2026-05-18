# Spring Boot - Security (Spring Security, JWT, OAuth2)

## Table of Contents
1. [Real-World Use Cases](#real-world-use-cases)
2. [Beginner Level Questions](#beginner-level-questions)
3. [Intermediate Level Questions](#intermediate-level-questions)
4. [Advanced Level Questions](#advanced-level-questions)

---

## Real-World Use Cases
1. **Stateless Microservice Authentication**: A mobile app communicates with multiple Spring Boot microservices. Instead of passing credentials every time or maintaining heavy server-side sessions, the app logs in once to an Auth Service. It receives a signed **JWT (JSON Web Token)**. For subsequent requests, the app attaches the JWT in the `Authorization: Bearer` header. Spring Security on each microservice validates the token signature locally (without hitting a database) and grants access based on the roles encoded within the token.
2. **Social Login Integration (SSO)**: A web portal allows users to "Login with Google" or "Login with GitHub". Using Spring Security's **OAuth2 Client** integration, the application delegates the authentication process to the third-party provider. Once Google verifies the user, Spring Boot automatically creates a local security context for the user, allowing seamless Single Sign-On.

---

## Beginner Level Questions

### Q1. What is Spring Security? [Easy]
**Answer:** Spring Security is a powerful and highly customizable authentication and access-control framework. It is the de-facto standard for securing Spring-based applications.

### Q2. What is the difference between Authentication and Authorization? [Easy]
**Answer:**
* **Authentication**: The process of verifying *who* you are (e.g., logging in with a username and password).
* **Authorization**: The process of verifying *what* you are allowed to do (e.g., checking if you have the "ADMIN" role to delete a user).

### Q3. How does Spring Security intercept requests? [Easy]
**Answer:** It uses a chain of Servlet **Filters** (`DelegatingFilterProxy` and `FilterChainProxy`). Every incoming HTTP request must pass through this security filter chain before it reaches the `DispatcherServlet` or your Controllers.

### Q4. What is `SecurityContextHolder`? [Easy]
**Answer:** It is the most fundamental class in Spring Security. It stores the details of the currently authenticated principal (user). By default, it uses a `ThreadLocal` to store these details, meaning the security context is always available to methods executing on the same thread.

### Q5. How do you retrieve the currently logged-in user in a Controller? [Easy]
**Answer:** You can use `SecurityContextHolder.getContext().getAuthentication().getPrincipal()`, or more simply, inject it directly into the controller method using the `@AuthenticationPrincipal` annotation.
**Code Example:**
```java
@GetMapping("/me")
public String getCurrentUser(@AuthenticationPrincipal UserDetails userDetails) {
    return userDetails.getUsername();
}
```

### Q6. What is the `UserDetails` interface? [Easy]
**Answer:** It provides core user information to Spring Security. It contains methods to retrieve the user's password, username, and granted authorities (roles), as well as boolean flags indicating if the account is expired, locked, or disabled.

### Q7. What is the `UserDetailsService`? [Easy]
**Answer:** It is a core interface used to retrieve user-related data. It has one method: `loadUserByUsername(String username)`. Spring Security uses this service during the authentication process to fetch the user record from the database to compare passwords.

### Q8. Why do we need a `PasswordEncoder`? [Easy]
**Answer:** Passwords should never be stored in plain text in a database. A `PasswordEncoder` performs a one-way hashing algorithm (like BCrypt) on the password before saving it. During login, it hashes the provided password and compares the hashes.

### Q9. What is BCrypt? [Easy]
**Answer:** BCrypt is a widely used, strong password hashing function. It incorporates a "salt" (random data) to protect against rainbow table attacks and is intentionally slow (computationally expensive) to protect against brute-force attacks.

### Q10. What is a CSRF attack? [Easy]
**Answer:** Cross-Site Request Forgery. It occurs when a malicious website causes a user's web browser to perform an unwanted action on a trusted site where the user is currently authenticated (using session cookies).

---

## Intermediate Level Questions

### Q11. How does Spring Security prevent CSRF attacks? [Medium]
**Answer:** It implements the Synchronizer Token Pattern. The server generates a unique, cryptographically strong token and sends it to the client. The client must include this exact token in the body or header of every state-changing request (POST, PUT, DELETE). If the token is missing or invalid, Spring Security rejects the request with a 403 Forbidden.

### Q12. Should you enable CSRF protection for a stateless REST API? [Medium]
**Answer:** Generally, **no**. CSRF relies on the browser automatically attaching session cookies. If your REST API is entirely stateless and relies on JWTs passed in the `Authorization` header (which browsers do not attach automatically to cross-site requests), CSRF protection is unnecessary and should be disabled (`http.csrf().disable()`).

### Q13. What is JWT (JSON Web Token)? [Medium]
**Answer:** JWT is an open standard (RFC 7519) that defines a compact and self-contained way for securely transmitting information between parties as a JSON object. The information is verified and trusted because it is digitally signed using a secret (HMAC) or a public/private key pair (RSA).

### Q14. What are the three parts of a JWT? [Medium]
**Answer:**
1. **Header**: Contains the type of token (JWT) and the signing algorithm (e.g., HS256).
2. **Payload**: Contains the "claims" (statements about the user, like their ID, roles, and token expiration time).
3. **Signature**: A cryptographic hash of the encoded Header, encoded Payload, and a Secret Key. It ensures the token hasn't been tampered with.

### Q15. How do you implement JWT Authentication in Spring Security? [Medium]
**Answer:** 
1. Disable sessions (`SessionCreationPolicy.STATELESS`).
2. Create a custom `OncePerRequestFilter`.
3. In the filter, extract the token from the `Authorization: Bearer` header.
4. Validate the token signature and expiration.
5. Extract the username and roles from the token.
6. Create an `UsernamePasswordAuthenticationToken` and place it in the `SecurityContextHolder`.

### Q16. Explain Method Level Security in Spring. [Medium]
**Answer:** In addition to securing URLs, Spring allows you to secure individual methods at the service layer using annotations like `@PreAuthorize`, `@PostAuthorize`, `@Secured`, and `@RolesAllowed`. You must explicitly enable this by adding `@EnableMethodSecurity` to your configuration.

### Q17. How does `@PreAuthorize` work? Give an example. [Medium]
**Answer:** It evaluates a SpEL (Spring Expression Language) expression *before* the method is invoked. If the expression evaluates to false, an `AccessDeniedException` is thrown.
**Code Example:**
```java
@PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
public void deleteUser(Long userId) { ... }
```

### Q18. What is the difference between `@PreAuthorize` and `@PostAuthorize`? [Medium]
**Answer:** `@PreAuthorize` blocks execution *before* the method runs. `@PostAuthorize` allows the method to execute fully, but evaluates an expression *afterward*. It can inspect the `returnObject` and prevent the caller from receiving the result if authorization fails.

### Q19. What is CORS and how do you configure it in Spring Security? [Medium]
**Answer:** Cross-Origin Resource Sharing (CORS) is a browser mechanism that restricts cross-domain requests. While you can configure CORS in Spring MVC, if Spring Security is enabled, you must also configure it in the Security Filter Chain (`http.cors()`) so that pre-flight `OPTIONS` requests are not blocked by the security filters before they reach the MVC layer.

### Q20. What is OAuth2? [Medium]
**Answer:** OAuth2 is an industry-standard authorization framework. It enables a third-party application to obtain limited access to an HTTP service on behalf of a resource owner. It is not an authentication protocol (though OpenID Connect adds authentication on top of it).

---

## Advanced Level Questions

### Q21. Explain the OAuth2 Authorization Code Grant Flow. [Hard]
**Answer:** (Common for web apps with backends)
1. User clicks "Login with Google". The app redirects the user to Google's authorization server.
2. User logs in to Google and grants permission.
3. Google redirects back to the app with a short-lived `authorization_code`.
4. The app's backend securely exchanges this `code` + `client_secret` with Google for an `access_token`.
5. The backend uses the `access_token` to fetch user details and log them into the app.

### Q22. How has Spring Security configuration changed in recent versions (Spring Boot 3 / Spring Security 6)? [Hard]
**Answer:** The old way of extending `WebSecurityConfigurerAdapter` has been completely removed. The modern approach is component-based configuration: you register a `SecurityFilterChain` bean. Additionally, `antMatchers()` has been replaced by `requestMatchers()`, and the configuration syntax heavily relies on Lambda DSLs instead of method chaining.

### Q23. How do you implement a "Remember Me" feature? [Hard]
**Answer:** Spring Security provides built-in support. When configured, it sends a special "remember-me" cookie to the browser. If the user's session expires, Spring Security intercepts the request, reads the cookie, validates a hash (or checks a persistent database token), and automatically re-authenticates the user without requiring a password.

### Q24. What are the security risks of JWTs compared to traditional sessions? [Hard]
**Answer:** Because JWTs are stateless and self-contained, they cannot be easily invalidated (revoked) before they expire. If a JWT is stolen, the attacker has full access until the token's expiration time is reached. Sessions, stored on the server, can be instantly destroyed by the server administrator.

### Q25. How do you solve the JWT revocation problem? [Hard]
**Answer:** 
1. Keep token lifespans very short (e.g., 15 minutes) and use long-lived **Refresh Tokens** to silently obtain new JWTs. If a user's account is compromised, you revoke the Refresh Token in the database.
2. Implement a Token Blacklist (using Redis) storing the IDs of revoked JWTs. The security filter must check Redis on every request, which partially defeats the stateless benefit of JWTs.

### Q26. Explain the `AuthenticationManager` and `AuthenticationProvider`. [Hard]
**Answer:** 
* `AuthenticationManager`: The main interface for authentication. Its `authenticate()` method attempts to authenticate the user. Its default implementation is `ProviderManager`.
* `AuthenticationProvider`: `ProviderManager` delegates the actual work to a list of `AuthenticationProvider`s. One provider might authenticate against a Database (using `UserDetailsService`), another might authenticate an LDAP directory, and another might authenticate a JWT. If one provider succeeds, authentication is successful.

### Q27. How do you handle multiple security configurations in the same application? [Hard]
**Answer:** You can define multiple `SecurityFilterChain` beans. You differentiate them by using `securityMatcher("/api/**")` to restrict which URLs a specific chain applies to, and you use the `@Order` annotation to define the evaluation precedence (Spring checks them in order until it finds a matching chain).

### Q28. What is a Password Encoder Upgrade strategy? [Hard]
**Answer:** If you migrate from an old insecure hashing algorithm (like MD5) to BCrypt, you use Spring's `DelegatingPasswordEncoder`. It prefixes hashes with their ID (e.g., `{bcrypt}$2a$...`). When a user logs in, Spring detects the old `{md5}` prefix, successfully authenticates them, generates a new `{bcrypt}` hash from their plain-text password, and updates the database, seamlessly migrating users upon their next login.

### Q29. How do you secure a websocket connection in Spring? [Hard]
**Answer:** Standard HTTP security filters do not apply to ongoing WebSocket frames. You must configure Spring Security to intercept STOMP messages. You use `@EnableWebSocketMessageBroker` and configure a `ChannelInterceptor` that extracts the JWT token from the STOMP `CONNECT` frame headers, validates it, and sets the `Principal` for the websocket session.

### Q30. Explain Role Hierarchy in Spring Security. [Hard]
**Answer:** Normally, if a method requires `ROLE_USER`, an admin with `ROLE_ADMIN` would be denied unless they explicitly have both roles. Role Hierarchy allows you to configure rules like `ROLE_ADMIN > ROLE_USER`. You define a `RoleHierarchy` bean. Once configured, Spring automatically understands that anyone with the Admin role implicitly possesses all permissions of the User role, reducing database clutter.
