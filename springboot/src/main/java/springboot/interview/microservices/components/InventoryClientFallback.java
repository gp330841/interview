package springboot.interview.microservices.components;

import org.springframework.stereotype.Component;

@Component
public class InventoryClientFallback implements InventoryClient {
    @Override
    public String checkInventory(Long productId) {
        return "Fallback: Inventory service is currently unavailable for product " + productId;
    }
}
