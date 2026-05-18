package springboot.interview.kafka.components;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendOrderEvent(String orderId) {
        System.out.println("--> KafkaProducer: Sending Order Created Event for Order ID: " + orderId);
        kafkaTemplate.send("orders-topic", orderId, "Order Created");
    }
}
