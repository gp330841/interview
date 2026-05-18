package springboot.interview.di.components;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/di/demo")
public class DIDemoController {

    private final OrderService orderService;
    private final CheckoutService checkoutService;
    private final PaymentGatewayManager gatewayManager;
    private final ReportGeneratorService reportGeneratorService;
    private final HeavyService heavyService;

    public DIDemoController(
            OrderService orderService,
            CheckoutService checkoutService,
            PaymentGatewayManager gatewayManager,
            ReportGeneratorService reportGeneratorService,
            HeavyService heavyService) {
        this.orderService = orderService;
        this.checkoutService = checkoutService;
        this.gatewayManager = gatewayManager;
        this.reportGeneratorService = reportGeneratorService;
        this.heavyService = heavyService;
    }

    @GetMapping
    public Map<String, String> executeDemo() {
        Map<String, String> results = new HashMap<>();

        // 1. Constructor Injection (Primary Bean)
        results.put("1_ConstructorInjection", orderService.placeOrder(100.0));

        // 2. Qualifier Injection (Stripe Bean)
        results.put("2_QualifierInjection", checkoutService.checkout(200.0));

        // 3. List Injection (All Beans)
        results.put("3_ListInjection", gatewayManager.executeAll(300.0));

        // 4. Lookup Proxy Scope (Prototype in Singleton)
        results.put("4_LookupScope_A", reportGeneratorService.generate());
        results.put("4_LookupScope_B", reportGeneratorService.generate());

        // 5. Lazy Injection
        results.put("5_LazyInjection", heavyService.performHeavyTask());

        return results;
    }
}
