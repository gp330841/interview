package springboot.interview.transactions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.io.IOException;

/**
 * Spring Boot - Transactions Interview Questions
 * Contains practical code examples for Propagation, Rollback scenarios, and Transactional Events.
 */
public class TransactionsQA {

    // Dummy repository interfaces for context
    interface OrderRepository { void save(Object order); }
    interface AuditRepository { void save(String log); }

    @Service
    public static class OrderService {

        @Autowired private OrderRepository orderRepository;
        @Autowired private AuditService auditService;
        @Autowired private ApplicationEventPublisher eventPublisher;

        // Q5: Forcing rollback for Checked Exceptions [Easy]
        public void q5() {
            /*
             * Answer: By default, @Transactional only rolls back on RuntimeExceptions.
             * Use rollbackFor = Exception.class to rollback on checked exceptions like IOException.
             */
        }
        @Transactional(rollbackFor = IOException.class)
        public void processFileWithTransaction() throws IOException {
            orderRepository.save("Some Data");
            throw new IOException("Disk Full!"); // Now this WILL trigger a rollback
        }

        // Q13 & Q22: Propagation.REQUIRES_NEW [Medium/Hard]
        public void q13_q22() {
            /*
             * Answer: REQUIRES_NEW suspends the current transaction and starts a completely independent one.
             * Below, even if the Order transaction fails, the Audit transaction commits successfully.
             */
        }
        @Transactional
        public void placeOrder() {
            try {
                orderRepository.save("New Order");
                
                // This call opens a NEW transaction, saves the audit, and commits it immediately
                auditService.logAction("User placed order"); 
                
                // Simulating a failure in the main business logic AFTER the audit is logged
                throw new RuntimeException("Payment Gateway Failed!");
                
            } catch (Exception e) {
                // The placeOrder transaction rolls back, but the logAction transaction remains committed in the DB!
                throw e;
            }
        }

        // Q29 & Q30: Transactional Events / Callbacks [Hard]
        public void q29_q30() {
            /*
             * Answer: Use @TransactionalEventListener to execute logic (like sending an email) 
             * ONLY AFTER the database transaction has successfully committed.
             */
        }
        @Transactional
        public void registerUser() {
            orderRepository.save("New User Data");
            
            // Publish the event. The listener won't execute immediately!
            // It waits for this registerUser() method to finish committing the DB transaction.
            eventPublisher.publishEvent(new UserRegisteredEvent("user@example.com"));
        }
    }

    @Service
    public static class AuditService {
        @Autowired private AuditRepository auditRepository;

        // Because of REQUIRES_NEW, this method always gets its own dedicated database connection
        // and transaction, independent of the caller.
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void logAction(String action) {
            auditRepository.save(action);
        }
    }

    // Event Record
    public static class UserRegisteredEvent {
        private final String email;
        public UserRegisteredEvent(String email) { this.email = email; }
        public String getEmail() { return email; }
    }

    // Event Listener Component
    @Service
    public static class EmailNotificationListener {

        // This method ONLY executes if the transaction that published the event successfully commits.
        // If the database transaction rolls back, this method is simply ignored.
        @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
        public void handleUserRegisteredEvent(UserRegisteredEvent event) {
            System.out.println("Transaction fully committed. Sending Welcome Email to: " + event.getEmail());
        }
    }
}
