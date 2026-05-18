package springboot.interview.jpa;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Spring Boot - Spring Data JPA Interview Questions
 * Contains practical code examples for Entities, Query Methods, and N+1 solutions.
 */
public class JPAQA {

    // Example Entity demonstrating basic JPA annotations and Optimistic Locking
    @Entity
    @Table(name = "users")
    public static class User {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;
        private String email;

        // Q22: Optimistic Locking [Hard]
        @Version // Hibernate increments this automatically on updates
        private Integer version;

        // Q16: FetchType.LAZY vs EAGER [Medium]
        // Best practice is always LAZY for collections to prevent massive data loading
        @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
        private List<Order> orders;

        // Q15: @Transient [Medium]
        // This field exists in the Java object but won't be saved to the database table
        @Transient
        private int calculatedAge;

        // Getters and Setters omitted for brevity
    }

    @Entity
    @Table(name = "orders")
    public static class Order {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
        @ManyToOne(fetch = FetchType.LAZY) @JoinColumn(name = "user_id") private User user;
    }

    // Custom Repository demonstrating query techniques
    public interface UserRepository extends JpaRepository<User, Long> {

        // Q4: Query Method Derivation [Easy]
        // Spring writes the SQL: SELECT * FROM users WHERE email = ?
        User findByEmail(String email);

        // Q10: Custom JPQL Query [Easy]
        @Query("SELECT u FROM User u WHERE u.name LIKE %:name%")
        List<User> searchByName(@Param("name") String name);

        // Q12: Native SQL Query [Medium]
        // Uses raw SQL (database specific) rather than JPQL
        @Query(value = "SELECT * FROM users u WHERE u.email = :email", nativeQuery = true)
        User findByEmailNative(@Param("email") String email);

        // Q14 & Q29: @Modifying for Bulk Updates/Deletes [Medium/Hard]
        // Bypasses loading entities into memory, making it highly efficient
        @Modifying
        @Query("UPDATE User u SET u.email = :newEmail WHERE u.id = :id")
        int updateEmailDirectly(@Param("id") Long id, @Param("newEmail") String newEmail);

        // Q18: Solving N+1 Query Problem using JOIN FETCH [Medium]
        // Forces a single query to fetch the User and all their Orders simultaneously
        @Query("SELECT u FROM User u JOIN FETCH u.orders WHERE u.id = :id")
        User findByIdWithOrders(@Param("id") Long id);

        // Q18 Alternative: Solving N+1 using @EntityGraph [Medium]
        // A cleaner way to dynamically override FetchType.LAZY to EAGER for a specific query
        @EntityGraph(attributePaths = {"orders"})
        User findByName(String name);
    }
}
