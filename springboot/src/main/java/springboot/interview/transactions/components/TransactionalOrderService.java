package springboot.interview.transactions.components;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import springboot.interview.domain.entity.Order;
import springboot.interview.domain.entity.User;
import springboot.interview.domain.repository.OrderRepository;
import springboot.interview.domain.repository.UserRepository;

@Service
public class TransactionalOrderService {

    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final ApplicationEventPublisher eventPublisher;

    public TransactionalOrderService(UserRepository userRepository, OrderRepository orderRepository, ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.orderRepository = orderRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Order createOrderForUser(String name, String email) {
        // Find or create user
        User user = userRepository.findByEmail(email);
        if (user == null) {
            user = new User(name, email);
            user = userRepository.save(user);
        }

        // Create order
        Order order = new Order(user);
        order = orderRepository.save(order);

        // Publish event to be handled after commit
        eventPublisher.publishEvent(new OrderCreatedEvent(order.getId()));

        return order;
    }

    @Transactional(rollbackFor = Exception.class)
    public void createOrderWithException(String name, String email) throws Exception {
        createOrderForUser(name, email);
        throw new Exception("Simulated error to trigger rollback");
    }
}
