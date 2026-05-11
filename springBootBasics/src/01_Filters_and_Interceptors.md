# Spring Filters and Interceptors: Advanced Interview Questions

## 1. What is the difference between a Filter and an Interceptor in Spring?

Both Filters and Interceptors allow you to intercept and process requests, but they operate at different levels of the application stack.

**Filter (Servlet Filter)**
*   **Level**: Belongs to the Servlet API, not the Spring framework itself.
*   **Execution**: Executes *before* the request reaches the `DispatcherServlet` and *after* the response leaves it.
*   **Scope**: Intercepts all incoming web requests (even static resources if configured).
*   **Context**: Does not have access to Spring Context (ApplicationContext) by default unless you use Spring's `DelegatingFilterProxy` or `OncePerRequestFilter`.
*   **Use Cases**: Authentication, authorization, logging request details (IP, URI), CORS handling, request/response compression, or modifying request parameters.

**Interceptor (HandlerInterceptor)**
*   **Level**: Belongs strictly to the Spring MVC framework.
*   **Execution**: Executes *after* the `DispatcherServlet` routes the request to an appropriate Controller, but *before* the Controller method is actually invoked.
*   **Scope**: Intercepts only the requests mapped to Spring MVC Controllers.
*   **Context**: Fully integrated with Spring IoC. Has access to the `ApplicationContext` and the specific `Handler` (Controller method) that will execute the request.
*   **Use Cases**: Checking user roles before hitting a specific endpoint, adding common model attributes for views, granular logging based on the specific handler.

---

## 2. Explain `OncePerRequestFilter`. Why is it preferred over a standard Servlet `Filter`?

In standard Servlet environments, a request might be dispatched multiple times within a single HTTP request lifecycle (for example, due to internal `FORWARD` or `ERROR` dispatches). A standard `javax.servlet.Filter` might be invoked multiple times for the same logical request.

Spring provides `OncePerRequestFilter` to guarantee that the filter's `doFilterInternal` method is executed **exactly once** per request, regardless of internal dispatching.

**Why use it?**
It is crucial for filters that modify the response or handle authentication (like Spring Security's JWT filters). If an authentication filter runs twice on a forward, it might throw an error or re-process expensive token validations unnecessarily.

**Code Example:**
```java
import org.springframework.web.filter.OncePerRequestFilter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        // Verify token logic...
        
        // Continue down the chain
        filterChain.doFilter(request, response);
    }
}
```

---

## 3. How do you configure the execution order of multiple Filters in Spring Boot?

When you have multiple filters (e.g., LoggingFilter, AuthFilter), order matters. You can configure order in two ways:

**Approach 1: Using `@Order` Annotation**
If your filter is a Spring bean (annotated with `@Component`), you can simply use the `@Order` annotation. Lower numbers have higher precedence (execute first).

```java
@Component
@Order(1) // Executes first
public class SecurityFilter extends OncePerRequestFilter { ... }

@Component
@Order(2) // Executes second
public class LoggingFilter extends OncePerRequestFilter { ... }
```

**Approach 2: Using `FilterRegistrationBean`**
This provides more control, allowing you to specify URL patterns along with the order.

```java
@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<LoggingFilter> loggingFilter() {
        FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoggingFilter());
        registrationBean.addUrlPatterns("/api/*");
        registrationBean.setOrder(2); 
        return registrationBean;
    }
}
```

---

## 4. What are the three main methods of a `HandlerInterceptor`?

A Spring `HandlerInterceptor` provides three distinct points of intervention:

1.  **`preHandle(HttpServletRequest, HttpServletResponse, Object handler)`**:
    *   Called *before* the actual handler (Controller method) is executed.
    *   **Return value**: `boolean`. If it returns `true`, the execution chain continues. If `false`, Spring assumes the interceptor handled the response itself (e.g., sent a 401 Unauthorized redirect), and no further interceptors or the controller will be called.
2.  **`postHandle(HttpServletRequest, HttpServletResponse, Object handler, ModelAndView modelAndView)`**:
    *   Called *after* the handler executes, but *before* the View is rendered.
    *   Allows you to add attributes to the `ModelAndView` or modify the model before the UI is generated. (Less common in REST APIs since there is no View).
3.  **`afterCompletion(HttpServletRequest, HttpServletResponse, Object handler, Exception ex)`**:
    *   Called *after* the complete request has finished (View is rendered, or JSON is written to response).
    *   Mainly used for resource cleanup or detailed performance logging (you can check if an `Exception` occurred during the request).
