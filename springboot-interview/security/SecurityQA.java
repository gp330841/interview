package springboot.interview.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.stereotype.Service;

/**
 * Spring Boot - Security Interview Questions
 * Contains practical code examples for modern Spring Security 6 configurations, 
 * Method Security, and Password Encoding.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Enables @PreAuthorize (Q16)
public class SecurityQA {

    // Q22: Modern Spring Security 6 Configuration (No WebSecurityConfigurerAdapter) [Hard]
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
        http
            // Q12: Disabling CSRF for Stateless APIs
            .csrf(csrf -> csrf.disable())
            
            // Setting session management to stateless (Crucial for JWTs)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            
            // Configuring authorization rules
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**", "/login").permitAll() // Public endpoints
                .requestMatchers("/api/admin/**").hasRole("ADMIN")       // Admin only
                .anyRequest().authenticated()                            // Everything else requires auth
            );
            
            // Note: In a real JWT setup, you would add your custom filter here:
            // .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // Q8 & Q9: Password Encoder and BCrypt [Easy]
    @Bean
    public PasswordEncoder passwordEncoder() {
        // Automatically handles salting and secure hashing
        return new BCryptPasswordEncoder();
    }

    // Q7: UserDetailsService [Easy]
    // Example using an In-Memory manager for simplicity (Normally fetches from a DB)
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        UserDetails admin = User.builder()
                .username("admin")
                .password(encoder.encode("adminPass")) // Must be encoded!
                .roles("ADMIN", "USER")
                .build();

        UserDetails user = User.builder()
                .username("user")
                .password(encoder.encode("userPass"))
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    @Service
    public static class SecureDocumentService {

        // Q17: How does @PreAuthorize work? [Medium]
        public void q17() {
            /*
             * Answer: Evaluates a SpEL expression BEFORE the method executes.
             * It can access method arguments using the # symbol.
             */
        }
        
        // This method can only be called if the user is an ADMIN, 
        // OR if the user's username exactly matches the 'ownerName' parameter.
        @PreAuthorize("hasRole('ADMIN') or authentication.name == #ownerName")
        public void deleteDocument(String documentId, String ownerName) {
            System.out.println("Document " + documentId + " securely deleted.");
        }
    }
}
