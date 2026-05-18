package springboot.interview.kafka.components;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    @KafkaListener(topics = "orders-topic", groupId = "order-group")
    public void consumeOrderEvent(String message) {
        System.out.println("--> KafkaConsumer: Received Message: " + message);
    }
}
