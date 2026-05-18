package springboot.interview.di.components;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class CheckoutService {
    private final PaymentProcessor processor;

    // Demonstrates @Qualifier to resolve ambiguity
    public CheckoutService(@Qualifier("stripeProcessor") PaymentProcessor processor) {
        this.processor = processor;
    }

    public String checkout(double amount) {
        return "CheckoutService: " + processor.process(amount);
    }
}
