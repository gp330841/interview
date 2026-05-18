package springboot.interview.di.components;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class PaymentGatewayManager {
    private final List<PaymentProcessor> allProcessors;

    @Autowired
    public PaymentGatewayManager(List<PaymentProcessor> allProcessors) {
        this.allProcessors = allProcessors;
    }

    public String executeAll(double amount) {
        StringBuilder result = new StringBuilder("PaymentGatewayManager executing all: ");
        allProcessors.forEach(p -> result.append("[").append(p.process(amount)).append("] "));
        return result.toString();
    }
}
