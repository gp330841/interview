package springboot.interview.di;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Spring Boot - Dependency Injection Interview Questions
 * Contains practical code examples for DI concepts.
 */
public class DIQA {

    // Q4: Why is Constructor Injection recommended over Field Injection? [Easy]
    public void q4() {
        /*
         * Answer:
         * Constructor injection ensures the object is fully initialized, dependencies are not null,
         * allows making dependencies 'final', and is easily mockable in unit tests without Spring context.
         */
        
        // Example:
        @Service
        class OrderService {
            // Field can be final, ensuring immutability
            private final PaymentProcessor paymentProcessor;

            // Spring automatically injects this. No @Autowired needed if it's the only constructor.
            public OrderService(PaymentProcessor paymentProcessor) {
                this.paymentProcessor = paymentProcessor;
            }
        }
    }

    // Q7: What is the role of the @Qualifier annotation? [Easy]
    public void q7() {
        /*
         * Answer:
         * Used to specify exactly which bean to inject when multiple beans of the same type exist.
         */
        
        // Example:
        @Service
        class CheckoutService {
            private final PaymentProcessor processor;

            // Resolves ambiguity if both StripePaymentProcessor and PayPalPaymentProcessor exist
            @Autowired
            public CheckoutService(@Qualifier("stripeProcessor") PaymentProcessor processor) {
                this.processor = processor;
            }
        }
    }

    // Q16: How do you solve the Scoped Proxy Problem (Singleton depending on Prototype)? [Medium]
    public void q16() {
        /*
         * Answer:
         * Use @Lookup method injection or inject an ObjectFactory/Provider.
         */
        
        // Example using @Lookup
        @Service
        abstract class ReportGeneratorService { // Singleton by default
            
            // Spring dynamically overrides this method to return a new instance from the context
            @Lookup
            public abstract ReportTask getNewReportTask();

            public void generate() {
                // We get a fresh prototype instance every time this is called
                ReportTask task = getNewReportTask();
                task.execute();
            }
        }
    }

    // Q18: How can you inject a list or map of beans? [Medium]
    public void q18() {
        /*
         * Answer:
         * Spring can autowire all implementations of an interface into a Collection.
         */
        
        // Example:
        @Service
        class PaymentGatewayManager {
            private final List<PaymentProcessor> allProcessors;

            @Autowired
            public PaymentGatewayManager(List<PaymentProcessor> allProcessors) {
                // allProcessors will contain Stripe, PayPal, etc.
                this.allProcessors = allProcessors;
            }
            
            public void executeAll() {
                allProcessors.forEach(PaymentProcessor::process);
            }
        }
    }

    // Q17: What does the @Lazy annotation do in DI? [Medium]
    public void q17() {
        /*
         * Answer:
         * Delays bean creation until it is actually needed, which can solve circular dependencies
         * and speed up application startup.
         */
        
        // Example:
        @Service
        class HeavyService {
            private final ExpensiveComponent expensiveComponent;

            // expensiveComponent won't be instantiated until a method on it is actually invoked.
            // Spring injects a proxy instead.
            public HeavyService(@Lazy ExpensiveComponent expensiveComponent) {
                this.expensiveComponent = expensiveComponent;
            }
        }
    }

    // Dummy interfaces and classes to make the examples compile conceptually
    interface PaymentProcessor { void process(); }
    class ReportTask { void execute() {} }
    class ExpensiveComponent {}
}
