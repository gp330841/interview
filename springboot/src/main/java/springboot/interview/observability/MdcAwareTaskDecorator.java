package springboot.interview.observability;

import io.opentelemetry.context.Context;
import io.opentelemetry.context.Scope;
import org.slf4j.MDC;
import org.springframework.core.task.TaskDecorator;

import java.util.Map;

public class MdcAwareTaskDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        // Capture the current thread's MDC and OpenTelemetry context
        Map<String, String> contextMap = MDC.getCopyOfContextMap();
        Context otelContext = Context.current();
        return () -> {
            Map<String, String> previous = MDC.getCopyOfContextMap();
            try (Scope scope = otelContext.makeCurrent()) {
                if (contextMap != null) {
                    MDC.setContextMap(contextMap);
                }
                runnable.run();
            } finally {
                if (previous != null) {
                    MDC.setContextMap(previous);
                } else {
                    MDC.clear();
                }
            }
        };
    }
}
