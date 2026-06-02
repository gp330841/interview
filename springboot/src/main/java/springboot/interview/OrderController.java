//package springboot.interview;
//
//import jakarta.validation.Valid;
//import jakarta.validation.constraints.NotNull;
//import jakarta.validation.constraints.Positive;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.*;
//import springboot.interview.di.components.OrderService;
//
//import java.time.LocalDateTime;
//import java.util.List;
//
//@RestController
//@RequestMapping("/api/v1/orders")
//public class OrderController {
//
//    private final OrderService orderService;
//
//    public OrderController(OrderService orderService) {
//        this.orderService = orderService;
//    }
//
//    @PostMapping
//    public ResponseEntity<OrderDto> createOrder(@Valid @RequestBody OrderDto order) {
//        OrderDto createdOrder = orderService.createOrder(order);
//        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<OrderDto> getOrder(@PathVariable Long id) {
//        OrderDto order = orderService.getOrder(id);
//        return ResponseEntity.ok(order);
//    }
//
//    @GetMapping
//    public ResponseEntity<List<OrderDto>> getOrders() {
//        List<OrderDto> orders = orderService.getOrders();
//        return ResponseEntity.ok(orders);
//    }
//
//
//    public record OrderDto(
//
//            Long id,
//
//            LocalDateTime orderDate,
//
//            @NotNull
//            @Positive
//            Double amount
//    ) {}
//}
//
//
