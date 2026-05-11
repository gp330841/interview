# Spring Boot - Spring MVC

## Table of Contents
1. [Real-World Use Cases](#real-world-use-cases)
2. [Beginner Level Questions](#beginner-level-questions)
3. [Intermediate Level Questions](#intermediate-level-questions)
4. [Advanced Level Questions](#advanced-level-questions)

---

## Real-World Use Cases
1. **Traditional Web Application with Server-Side Rendering**: Building an e-commerce storefront where SEO is critical. Spring MVC controllers handle incoming HTTP requests, fetch data from the service layer, and pass it to a templating engine (like Thymeleaf or FreeMarker) using a `Model` or `ModelAndView` object to render the final HTML page sent to the browser.
2. **Form Processing and Validation**: Creating a user registration portal. A user submits a form, and Spring MVC automatically binds the incoming form parameters to a Java DTO object (`@ModelAttribute`). It applies JSR-380 validation rules (`@Valid` or `@Validated`), and if validation fails, it redirects back to the form page with error messages automatically populated via the `BindingResult`.

---

## Beginner Level Questions

### Q1. What is Spring MVC? [Easy]
**Answer:** Spring MVC (Model-View-Controller) is a sub-framework of the Spring Framework used to build web applications. It implements the Model-View-Controller design pattern, separating the application logic into three interconnected components.

### Q2. What are the roles of Model, View, and Controller? [Easy]
**Answer:** 
* **Model**: Represents the application data and business logic.
* **View**: The UI that displays the Model data to the user (e.g., JSP, Thymeleaf).
* **Controller**: Handles user requests, interacts with the Model to process the request, and selects the appropriate View to render the response.

### Q3. What is the `DispatcherServlet`? [Easy]
**Answer:** The `DispatcherServlet` is the front controller in Spring MVC. It intercepts all incoming HTTP requests and routes them to the appropriate Controller for processing based on the configured handler mappings.

### Q4. What is the purpose of the `@Controller` annotation? [Easy]
**Answer:** It is a class-level annotation that tells the Spring IoC container that the class acts as a Spring MVC controller. It is a specialization of the `@Component` annotation.

### Q5. What does the `@RequestMapping` annotation do? [Easy]
**Answer:** It maps web requests (URL paths) onto specific handler classes or handler methods within a Controller. It can be used at the class level and method level to define the routing path, HTTP methods, headers, and media types.

### Q6. How do you map a specific HTTP GET request to a method? [Easy]
**Answer:** You can use `@RequestMapping(method = RequestMethod.GET)` or, more simply, use the shortcut annotation `@GetMapping`.

### Q7. What is `@RequestParam` used for? [Easy]
**Answer:** It is used to bind HTTP request parameters (query parameters or form data) to method arguments in your Controller.
**Code Example:**
```java
@GetMapping("/search")
public String search(@RequestParam("q") String query) { ... }
// Matches: /search?q=spring
```

### Q8. What is `@PathVariable` used for? [Easy]
**Answer:** It is used to bind URI template variables (parts of the URL path itself) to method arguments in your Controller.
**Code Example:**
```java
@GetMapping("/users/{id}")
public String getUser(@PathVariable("id") Long userId) { ... }
// Matches: /users/123
```

### Q9. What is the `Model` interface used for in a Controller method? [Easy]
**Answer:** The `Model` object acts as a container for the data that needs to be displayed by the View. You can add attributes to the `Model` in your Controller, and those attributes will be accessible in the rendering engine (like Thymeleaf).

### Q10. What is a ViewResolver? [Easy]
**Answer:** A `ViewResolver` is a Spring MVC component that translates the logical view name returned by a Controller (e.g., "home") into a physical view file (e.g., `/WEB-INF/views/home.jsp` or `templates/home.html`).

---

## Intermediate Level Questions

### Q11. Explain the complete request flow in Spring MVC. [Medium]
**Answer:** 
1. The client sends an HTTP request.
2. The `DispatcherServlet` receives the request.
3. The `DispatcherServlet` consults the `HandlerMapping` to find the appropriate Controller method.
4. The request is passed to the Controller.
5. The Controller processes the logic and returns a logical view name and a `Model`.
6. The `DispatcherServlet` asks the `ViewResolver` to find the actual view file based on the logical name.
7. The chosen View is rendered using the `Model` data.
8. The `DispatcherServlet` returns the final rendered HTTP response to the client.

### Q12. What is the difference between `@RequestParam` and `@PathVariable`? [Medium]
**Answer:** 
* `@RequestParam` extracts values from the query string (e.g., `?id=1`) or form data. It is typically used for filtering or optional parameters.
* `@PathVariable` extracts values from the URI path itself (e.g., `/users/1`). It is used to identify a specific resource in a RESTful way.

### Q13. How does Spring MVC handle form submissions? [Medium]
**Answer:** You use the `@ModelAttribute` annotation to bind incoming form data directly to a Java POJO (DTO). Spring automatically maps the HTML input `name` attributes to the POJO's fields via setter methods.
**Code Example:**
```java
@PostMapping("/register")
public String submitForm(@ModelAttribute UserDto user) { ... }
```

### Q14. What is the role of `BindingResult`? [Medium]
**Answer:** `BindingResult` holds the result of the validation and binding process. If you annotate a model attribute with `@Valid`, you must place a `BindingResult` parameter immediately after it in the method signature. Spring populates this object with any validation errors, allowing you to check `bindingResult.hasErrors()` and redirect back to the form.

### Q15. How do you implement global Exception Handling in Spring MVC? [Medium]
**Answer:** By using a class annotated with `@ControllerAdvice`. Inside this class, you define methods annotated with `@ExceptionHandler(ExceptionClass.class)`. Whenever a controller throws that specific exception, Spring routes the exception to your advice method to generate a custom error page or response.

### Q16. What is the difference between `Model`, `ModelMap`, and `ModelAndView`? [Medium]
**Answer:** 
* `Model`: An interface providing basic attributes holding capabilities.
* `ModelMap`: A concrete implementation of `java.util.LinkedHashMap` intended for use with UI tools.
* `ModelAndView`: A holder for both the Model (the data) AND the View (the logical view name). It allows you to return both pieces of information from a controller method simultaneously.

### Q17. How do you handle file uploads in Spring MVC? [Medium]
**Answer:** You use the `MultipartFile` interface as a method parameter in your Controller. Spring's `MultipartResolver` parses the multi-part request and binds the file data to this object.
**Code Example:**
```java
@PostMapping("/upload")
public String handleUpload(@RequestParam("file") MultipartFile file) {
    // file.getBytes(), file.getOriginalFilename()
}
```

### Q18. What is the purpose of Spring Interceptors (`HandlerInterceptor`)? [Medium]
**Answer:** Interceptors are used to apply pre-processing (`preHandle`), post-processing (`postHandle`), and after-completion (`afterCompletion`) logic to web requests. Unlike Servlet Filters, they are fully integrated with the Spring context and have access to the specific Controller handler executing the request.

### Q19. How do you prevent double form submission in Spring MVC? [Medium]
**Answer:** By using the Post/Redirect/Get (PRG) pattern. After successfully processing a POST request, the Controller should not return a view directly. Instead, it should return a redirect command (e.g., `return "redirect:/success";`). This forces the browser to make a new GET request, meaning refreshing the page won't resubmit the form.

### Q20. How do you pass data across a redirect? [Medium]
**Answer:** You use `RedirectAttributes`.
* `addAttribute`: Appends data to the query string of the redirect URL.
* `addFlashAttribute`: Stores data temporarily in the HTTP Session just long enough to survive the redirect, making it available to the target view and then removing it.

---

## Advanced Level Questions

### Q21. Explain the internal working of `DispatcherServlet` initialization. [Hard]
**Answer:** When the `DispatcherServlet` is initialized by the Servlet container (e.g., Tomcat), its `init()` method is called. It creates or looks up the Spring `WebApplicationContext`. Then it initializes its 9 core strategic components: `MultipartResolver`, `LocaleResolver`, `ThemeResolver`, `HandlerMappings`, `HandlerAdapters`, `HandlerExceptionResolvers`, `RequestToViewNameTranslator`, `ViewResolvers`, and `FlashMapManager` by searching the context for beans implementing these interfaces.

### Q22. What is a `HandlerAdapter` and why does `DispatcherServlet` need it? [Hard]
**Answer:** The `DispatcherServlet` does not invoke the Controller directly. Instead, it uses a `HandlerAdapter`. This is an application of the Adapter design pattern. Because Spring supports many types of handlers (e.g., `@Controller` annotated classes, traditional `HttpRequestHandler` interfaces, or third-party web frameworks), the `DispatcherServlet` uses the specific `HandlerAdapter` to figure out exactly how to invoke the mapped handler.

### Q23. How does Content Negotiation work in Spring MVC? [Hard]
**Answer:** Content Negotiation determines the requested media type (e.g., JSON, XML, HTML). Spring determines this by checking (in order of priority):
1. URL path extensions (e.g., `/api/users.json`) - *Deprecated in modern Spring Boot*.
2. URL parameters (e.g., `/api/users?format=json`).
3. The HTTP `Accept` header (e.g., `Accept: application/json`).
Based on this, the `ContentNegotiatingViewResolver` (or `HttpMessageConverters` in REST) selects the appropriate way to render the response.

### Q24. How do you test a Spring MVC Controller without starting a web server? [Hard]
**Answer:** Using Spring's `MockMvc` framework, often combined with `@WebMvcTest`. `MockMvc` allows you to send mock HTTP requests to the `DispatcherServlet` and assert the model attributes, view names, and response status without the overhead of starting an embedded Tomcat server.

### Q25. What is the difference between `@ControllerAdvice` and `HandlerInterceptor` for exception handling? [Hard]
**Answer:** 
* `HandlerInterceptor` has an `afterCompletion` method that can inspect exceptions, but it is too late to change the response or render a custom view.
* `@ControllerAdvice` combined with `@ExceptionHandler` is specifically designed to catch exceptions thrown by controllers and generate a meaningful HTTP response or redirect to an error view. It is the standard way to handle application-level exceptions.

### Q26. How do you handle asynchronous request processing in Spring MVC? [Hard]
**Answer:** By returning a `Callable<T>`, `DeferredResult<T>`, or `CompletableFuture<T>` from the Controller method. This frees up the Tomcat worker thread immediately while the actual processing happens on a separate thread pool. Once the background thread finishes, the `DispatcherServlet` resumes the request and returns the result to the client.

### Q27. What is `@SessionAttributes` and how does it differ from raw `HttpSession`? [Hard]
**Answer:** `@SessionAttributes` is used at the class level to declare that certain model attributes should be temporarily stored in the session between multiple requests (e.g., a multi-page wizard form). It differs from `HttpSession` in that it is managed by Spring, is specifically tied to the Controller's workflow, and should be explicitly cleaned up by calling `SessionStatus.setComplete()` when the workflow finishes.

### Q28. Explain how CORS is handled in Spring MVC. [Hard]
**Answer:** Cross-Origin Resource Sharing (CORS) can be configured locally using the `@CrossOrigin` annotation on specific controller methods or classes. For global configuration, you implement `WebMvcConfigurer` and override the `addCorsMappings(CorsRegistry registry)` method to define allowed origins, methods, and headers across the entire application.

### Q29. What is a `View` in Spring MVC conceptually, and how do custom views work? [Hard]
**Answer:** A `View` is an interface responsible for rendering the actual response. While JSP and Thymeleaf are common, you can implement the `View` interface to create custom rendering logic, such as generating PDF documents (using iText) or Excel spreadsheets (using Apache POI). You register these custom views with a `BeanNameViewResolver` or a custom `ViewResolver`.

### Q30. How does Spring MVC bind Date objects from string inputs? [Hard]
**Answer:** String-to-Object conversion is handled by `WebDataBinder` and Spring's `Converter` or `Formatter` APIs. To bind a specific date format, you can annotate the Date/LocalDate field in your DTO with `@DateTimeFormat(pattern = "yyyy-MM-dd")`. If you need a global format, you register a custom `Formatter<LocalDate>` in your `WebMvcConfigurer`.
