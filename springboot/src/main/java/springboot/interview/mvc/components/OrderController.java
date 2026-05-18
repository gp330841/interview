package springboot.interview.mvc.components;

import org.springframework.web.bind.annotation.*;
import springboot.interview.domain.entity.Order;
import springboot.interview.transactions.components.TransactionalOrderService;

import java.util.Map;

@RestController
@RequestMapping("/api/mvc/orders")
public class OrderController {

    private final TransactionalOrderService orderService;

    public OrderController(TransactionalOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public Order createOrder(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String email = request.get("email");
        return orderService.createOrderForUser(name, email);
    }

    @PostMapping("/fail")
    public String createOrderFail(@RequestBody Map<String, String> request) throws Exception {
        String name = request.get("name");
        String email = request.get("email");
        orderService.createOrderWithException(name, email);
        return "This will never be reached due to exception and rollback";
    }
}
