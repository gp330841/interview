package springboot.interview.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Spring Boot - Exception Handling Interview Questions
 * Contains practical code examples for Global Exception Handling, Validation Errors, and RFC 7807 Problem Details.
 */
public class ExceptionsQA {

    // Q9: Mapping a custom exception without an Advice class [Easy]
    @ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "The requested resource was not found")
    public static class ResourceNotFoundException extends RuntimeException {
        public ResourceNotFoundException(String message) {
            super(message);
        }
    }

    // Example showing Q10: Using ResponseStatusException directly in business logic
    public void findUser(Long id) {
        if (id == null) {
            // Throwing this automatically results in a 400 Bad Request
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "ID cannot be null");
        }
    }

    // Standard Custom Error DTO for older/traditional APIs
    public static class ApiError {
        private String timestamp = LocalDateTime.now().toString();
        private int status;
        private String error;
        private String message;

        public ApiError(int status, String error, String message) {
            this.status = status;
            this.error = error;
            this.message = message;
        }
        // Getters omitted for brevity
    }

    // Q5, Q6, & Q13: Global Exception Handler using @RestControllerAdvice [Medium]
    @RestControllerAdvice
    public static class GlobalExceptionHandler {

        // Catching a custom exception and returning a custom JSON DTO
        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex) {
            ApiError error = new ApiError(404, "Not Found", ex.getMessage());
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        // Q13: Handling Validation Errors globally [Medium]
        // Thrown when @Valid or @Validated fails on a @RequestBody
        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
            Map<String, String> errors = new HashMap<>();
            
            // Extracting specific field errors and their messages
            ex.getBindingResult().getAllErrors().forEach((error) -> {
                String fieldName = ((FieldError) error).getField();
                String errorMessage = error.getDefaultMessage();
                errors.put(fieldName, errorMessage);
            });
            
            // Returns {"email": "Must not be empty", "password": "Must be 8 chars"}
            return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        }

        // Q25, Q26, Q27: Spring Boot 3 RFC 7807 Problem Details API [Hard]
        // This is the modern standard way to return errors in Spring Boot 3+
        @ExceptionHandler(IllegalStateException.class)
        public ProblemDetail handleIllegalStateAsProblemDetail(IllegalStateException ex) {
            // Create a standard ProblemDetail object
            ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, ex.getMessage());
            
            // Setting standard RFC fields
            problemDetail.setType(URI.create("https://api.mycompany.com/errors/conflict"));
            problemDetail.setTitle("State Conflict Exception");
            
            // Q27: Adding custom extension properties
            problemDetail.setProperty("timestamp", LocalDateTime.now());
            problemDetail.setProperty("trackingId", "TRK-987654321");
            
            return problemDetail; // Spring automatically serializes this into the RFC 7807 JSON format
        }

        // Catch-all fallback for any unhandled exceptions
        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiError> handleAllExceptions(Exception ex) {
            // In a real app, you MUST log this exception trace here!
            ApiError error = new ApiError(500, "Internal Server Error", "An unexpected error occurred");
            return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
