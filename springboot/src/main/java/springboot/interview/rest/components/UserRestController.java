package springboot.interview.rest.components;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springboot.interview.domain.entity.User;
import springboot.interview.domain.repository.UserRepository;
import springboot.interview.exceptions.components.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import springboot.interview.observability.components.MdcExampleService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {

    private static final Logger logger = LoggerFactory.getLogger(UserRestController.class);
    
    private final UserRepository userRepository;
    private final MdcExampleService mdcExampleService;

    public UserRestController(UserRepository userRepository, MdcExampleService mdcExampleService) {
        this.userRepository = userRepository;
        this.mdcExampleService = mdcExampleService;
    }

    @GetMapping
    public List<User> getAllUsers() {
        // Trigger MDC logging simulation
        mdcExampleService.processWithMdc();
        logger.info("Fetching all users from the database...");
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable("id") Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(201).body(savedUser);
    }
}
