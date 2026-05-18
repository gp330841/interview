package springboot.interview.di.components;

import org.springframework.stereotype.Service;

@Service
public class OrderService {
    private final PaymentProcessor paymentProcessor;

    // Constructor injection: Spring automatically injects the @Primary PaymentProcessor
    public OrderService(PaymentProcessor paymentProcessor) {
        this.paymentProcessor = paymentProcessor;
    }

    public String placeOrder(double amount) {
        return "OrderService: " + paymentProcessor.process(amount);
    }
}
