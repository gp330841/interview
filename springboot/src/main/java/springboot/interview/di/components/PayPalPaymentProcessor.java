package springboot.interview.di.components;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

@Service
@Primary
@Qualifier("paypalProcessor")
public class PayPalPaymentProcessor implements PaymentProcessor {
    @Override
    public String process(double amount) {
        return "Processed $" + amount + " via PayPal.";
    }
}
