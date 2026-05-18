package springboot.interview.transactions.components;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class TransactionEventListenerExample {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleOrderCreatedEvent(OrderCreatedEvent event) {
        System.out.println("--> TransactionEventListener: Order " + event.getOrderId() + " was successfully committed to DB. Sending email...");
    }
}
