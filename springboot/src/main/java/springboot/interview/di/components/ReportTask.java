package springboot.interview.di.components;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
@Scope("prototype")
public class ReportTask {
    private final String taskId;

    public ReportTask() {
        this.taskId = UUID.randomUUID().toString();
        System.out.println("--> ReportTask created: " + taskId);
    }

    public String execute() {
        return "Executed ReportTask with ID: " + taskId;
    }
}
