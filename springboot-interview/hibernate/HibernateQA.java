package springboot.interview.hibernate;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StatelessSession;
import org.hibernate.Transaction;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.Interceptor;
import org.hibernate.type.Type;

import jakarta.persistence.*;
import java.io.Serializable;

/**
 * Spring Boot - Hibernate & ORM Interview Questions
 * Contains practical code examples for StatelessSession, DynamicUpdate, and Interceptors.
 */
public class HibernateQA {

    // Q23: @DynamicUpdate and @DynamicInsert [Hard]
    @Entity
    @Table(name = "products")
    @DynamicUpdate // Only modified fields are included in the SQL UPDATE statement
    public static class Product {
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
        private String name;
        private double price;
        private String heavyDescriptionText;

        public Long getId() { return id; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public double getPrice() { return price; }
        public void setPrice(double price) { this.price = price; }
    }

    // Q15: Composite Primary Key Mapping [Medium]
    @Embeddable
    public static class OrderItemId implements Serializable {
        private Long orderId;
        private Long productId;
        // MUST implement equals() and hashCode()
    }

    @Entity
    @Table(name = "order_items")
    public static class OrderItem {
        @EmbeddedId
        private OrderItemId id;
        private int quantity;
    }

    public static class HibernateExamples {

        private SessionFactory sessionFactory;

        // Q25: What is the Hibernate StatelessSession? [Hard]
        public void q25() {
            /*
             * Answer: A command-oriented API with no first-level cache, used for massive batch processing
             * to avoid OutOfMemory errors.
             */
            
            // Example: Batch inserting 100,000 records without crashing memory
            try (StatelessSession statelessSession = sessionFactory.openStatelessSession()) {
                Transaction tx = statelessSession.beginTransaction();
                
                for (int i = 0; i < 100000; i++) {
                    Product p = new Product();
                    p.setName("Product " + i);
                    statelessSession.insert(p); // Direct DB insert, no cache tracking
                }
                
                tx.commit();
            }
        }

        // Q11 & Q12: get() vs load(), save() vs persist() vs merge() [Medium]
        public void q11_q12_q13() {
            /*
             * Answer: 
             * get() hits DB immediately, load() returns a proxy.
             * persist() is JPA standard, save() is Hibernate specific (returns ID immediately).
             * merge() copies state to a managed instance, leaving the original detached.
             */
            try (Session session = sessionFactory.openSession()) {
                // Returns a Proxy. No DB hit yet.
                Product proxyProduct = session.load(Product.class, 1L); 
                
                // Now it hits the DB because we accessed a non-ID property
                System.out.println(proxyProduct.getName()); 
            }
        }
    }

    // Q21: Custom Hibernate Interceptor [Hard]
    public static class AuditInterceptor implements Interceptor {
        
        // This method is called exactly before an entity is saved to the DB
        @Override
        public boolean onSave(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) {
            if (entity instanceof Product) {
                System.out.println("AUDIT: New Product is being created with ID " + id);
                // We could also modify the 'state' array here to auto-set creation timestamps
            }
            return false; // Return false if we didn't modify the state array
        }
        
        // Other methods omitted: onFlushDirty (updates), onDelete, etc.
    }
}
