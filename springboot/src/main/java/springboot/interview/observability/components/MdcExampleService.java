package springboot.interview.observability.components;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MdcExampleService {

    private static final Logger logger = LoggerFactory.getLogger(MdcExampleService.class);

    public void processWithMdc() {
        String transactionId = UUID.randomUUID().toString();
        try {
            MDC.put("transactionId", transactionId);
            logger.info("Processing started with MDC context.");
            // Simulate work
            Thread.sleep(100);
            logger.info("Processing completed with MDC context.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            MDC.clear();
        }
    }
}
