package springboot.interview.di.components;

import org.springframework.stereotype.Component;

@Component
public class ExpensiveComponent {
    public ExpensiveComponent() {
        System.out.println("--> ExpensiveComponent: Costly initialization happening now!");
        try {
            Thread.sleep(500); // Simulate expensive work
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public String doWork() {
        return "Expensive work complete.";
    }
}
