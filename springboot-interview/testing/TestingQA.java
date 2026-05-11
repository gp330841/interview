package springboot.interview.testing;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Spring Boot - Testing (JUnit & Mockito) Interview Questions
 * Contains practical code examples for Unit Testing with Mockito and WebMvcTest for Controllers.
 */
public class TestingQA {

    // Dummy classes representing the application code
    public static class User {
        private String name;
        public User(String name) { this.name = name; }
        public String getName() { return name; }
    }

    public interface UserRepository {
        User save(User user);
        User findById(Long id);
        void deleteById(Long id);
    }

    public static class UserService {
        private final UserRepository repository;
        public UserService(UserRepository repository) { this.repository = repository; }

        public User createUser(String name) {
            if (name == null) throw new IllegalArgumentException("Name cannot be null");
            return repository.save(new User(name));
        }
        
        public void removeUser(Long id) {
            repository.deleteById(id);
        }
    }

    // --- 1. PURE UNIT TESTING (Fast, No Spring Context) ---
    @ExtendWith(MockitoExtension.class)
    public static class UserServiceUnitTest {

        // Q8: @Mock creates a fake dependency
        @Mock
        private UserRepository userRepository;

        // Q8: @InjectMocks injects the @Mock above into this real instance
        @InjectMocks
        private UserService userService;

        // Q16: ArgumentCaptor [Medium]
        @Captor
        private ArgumentCaptor<User> userCaptor;

        @Test
        public void testCreateUser_Success() {
            // Q9: Mocking behavior
            // We don't want to actually save to a DB, so we fake the save() method
            User mockSavedUser = new User("John");
            when(userRepository.save(any(User.class))).thenReturn(mockSavedUser);

            // Execute the method being tested
            User result = userService.createUser("John");

            // Q10: Verifying the mock was called
            verify(userRepository, times(1)).save(any(User.class));
            assertEquals("John", result.getName());
            
            // Q16: Capturing the internal argument passed to the mock
            verify(userRepository).save(userCaptor.capture());
            assertEquals("John", userCaptor.getValue().getName()); // Asserting the internal state
        }

        @Test
        public void testCreateUser_ThrowsException() {
            // Testing exception handling
            assertThrows(IllegalArgumentException.class, () -> userService.createUser(null));
            
            // Verify DB was NEVER called because validation failed first
            verify(userRepository, never()).save(any());
        }

        @Test
        public void testRemoveUser_VoidMethod() {
            // Q17: Testing void methods
            doNothing().when(userRepository).deleteById(1L);
            
            userService.removeUser(1L);
            
            verify(userRepository).deleteById(1L);
        }
    }

    // --- 2. SPRING WEB LAYER INTEGRATION TESTING (Moderate speed, Tests HTTP layer) ---
    // Q11: @WebMvcTest [Medium]
    // Starts ONLY the web layer (DispatcherServlet, Controllers), ignoring Services/Databases
    @WebMvcTest(controllers = UserController.class) 
    public static class UserControllerIntegrationTest {

        // Q12: Utility to simulate HTTP requests without a real network server
        @Autowired
        private MockMvc mockMvc;

        // Q14: @MockBean adds a mock into the Spring ApplicationContext
        @MockBean
        private UserService userService;

        @Test
        public void testGetUserEndpoint() throws Exception {
            // Setup the mock (The controller will autowire this mock)
            when(userService.createUser("Alice")).thenReturn(new User("Alice"));

            // Q18: Using MockMvc andExpect()
            mockMvc.perform(get("/api/users/Alice")
                    .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk()) // Asserts HTTP 200
                    // Uses JSONPath to assert the JSON body contains "Alice"
                    .andExpect(jsonPath("$.name").value("Alice")); 
        }
    }

    // Dummy Controller for the test above
    @org.springframework.web.bind.annotation.RestController
    public static class UserController {
        @Autowired private UserService userService;
        
        @org.springframework.web.bind.annotation.GetMapping("/api/users/{name}")
        public User getUser(@org.springframework.web.bind.annotation.PathVariable String name) {
            return userService.createUser(name); // Dummy implementation
        }
    }
}
