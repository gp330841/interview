package springboot.interview.di.components;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
@Qualifier("stripeProcessor")
public class StripePaymentProcessor implements PaymentProcessor {
    @Override
    public String process(double amount) {
        return "Processed $" + amount + " via Stripe.";
    }
}
