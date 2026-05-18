package springboot.interview.microservices.components;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "inventory-service", url = "http://localhost:8081", fallback = InventoryClientFallback.class)
public interface InventoryClient {

    @GetMapping("/api/inventory/{productId}")
    String checkInventory(@PathVariable("productId") Long productId);
}
