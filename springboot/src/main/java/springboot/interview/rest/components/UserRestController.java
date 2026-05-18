package springboot.interview.rest.components;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springboot.interview.domain.entity.User;
import springboot.interview.domain.repository.UserRepository;
import springboot.interview.exceptions.components.ResourceNotFoundException;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserRestController {

    private final UserRepository userRepository;

    public UserRestController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        User savedUser = userRepository.save(user);
        return ResponseEntity.status(201).body(savedUser);
    }
}
