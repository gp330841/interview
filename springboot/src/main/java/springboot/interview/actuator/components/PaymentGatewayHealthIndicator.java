package springboot.interview.actuator.components;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("paymentGateway")
public class PaymentGatewayHealthIndicator implements HealthIndicator {

    @Override
    public Health health() {
        boolean isGatewayUp = checkPaymentGateway();
        if (isGatewayUp) {
            return Health.up().withDetail("PaymentGateway", "Available").build();
        }
        return Health.down().withDetail("PaymentGateway", "Unavailable").build();
    }

    private boolean checkPaymentGateway() {
        // Mocking a successful connection
        return true;
    }
}
