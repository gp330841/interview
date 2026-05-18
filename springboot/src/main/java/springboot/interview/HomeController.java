package springboot.interview;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Welcome to the Spring Boot Interview Prep Application! You have successfully logged in.";
    }

    @GetMapping("/api/status")
    public String status() {
        return "Application is running perfectly.";
    }
}
