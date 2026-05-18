package springboot.interview.di.components;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service
public class HeavyService {
    private final ExpensiveComponent expensiveComponent;

    // The expensiveComponent is not instantiated until its methods are actually called
    public HeavyService(@Lazy ExpensiveComponent expensiveComponent) {
        System.out.println("--> HeavyService initialized (ExpensiveComponent is just a proxy right now)");
        this.expensiveComponent = expensiveComponent;
    }

    public String performHeavyTask() {
        return "HeavyService: " + expensiveComponent.doWork();
    }
}
