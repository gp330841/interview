package springboot.interview.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Spring Boot - REST APIs Interview Questions
 * Contains practical code examples for REST Controllers, ResponseEntity, and Streaming.
 */
public class RestQA {

    // Dummy DTO
    public static class User {
        private Long id;
        private String name;

        public User(Long id, String name) { this.id = id; this.name = name; }
        public Long getId() { return id; }
        public String getName() { return name; }
    }

    @RestController // Combines @Controller and @ResponseBody
    @RequestMapping("/api/v1/users")
    public static class UserRestController {

        // Q4 & Q8: @RequestBody and @ResponseStatus [Easy]
        public void q4_q8() {
            /*
             * Answer: @RequestBody maps JSON to a Java object. 
             * @ResponseStatus forces a specific HTTP status code upon success.
             */
        }
        @PostMapping
        @ResponseStatus(HttpStatus.CREATED) // Returns 201 Created instead of 200 OK
        public User createUser(@RequestBody User newUser) {
            System.out.println("Saving user to DB: " + newUser.getName());
            return newUser; // Automatically serialized to JSON
        }

        // Q7: How do you return a specific HTTP Status Code dynamically? [Easy]
        public void q7() {
            /*
             * Answer: Use ResponseEntity to control status code, headers, and body dynamically.
             */
        }
        @GetMapping("/{id}")
        public ResponseEntity<User> getUserById(@PathVariable Long id) {
            if (id == 999L) {
                // Return 404 Not Found without throwing an exception
                return ResponseEntity.notFound().build();
            }
            User user = new User(id, "John Doe");
            // Return 200 OK with the User JSON in the body
            return ResponseEntity.ok(user);
        }

        // Q30: How do you handle large file downloads without OOM errors? [Hard]
        public void q30() {
            /*
             * Answer: Use StreamingResponseBody to write directly to the HTTP output stream,
             * preventing the entire file from being loaded into memory.
             */
        }
        @GetMapping("/download-large-file")
        public ResponseEntity<StreamingResponseBody> downloadLargeFile() {
            File largeFile = new File("/path/to/massive/file.zip");
            
            StreamingResponseBody responseBody = outputStream -> {
                try (InputStream inputStream = new FileInputStream(largeFile)) {
                    byte[] buffer = new byte[8192]; // 8KB chunks
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            };
            
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=\"large_file.zip\"")
                    .body(responseBody);
        }
    }

    // Q13: Global Exception Handling in REST [Medium]
    @RestControllerAdvice
    public static class GlobalRestExceptionHandler {
        
        public static class ResourceNotFoundException extends RuntimeException {
            public ResourceNotFoundException(String msg) { super(msg); }
        }
        
        public static class ErrorDetails {
            public String error;
            public ErrorDetails(String error) { this.error = error; }
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ErrorDetails> handleNotFound(ResourceNotFoundException ex) {
            ErrorDetails details = new ErrorDetails(ex.getMessage());
            // Globally catch this exception and return a clean 404 JSON response
            return new ResponseEntity<>(details, HttpStatus.NOT_FOUND);
        }
    }
}
