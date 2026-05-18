package springboot.interview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

@SpringBootApplication
@EnableFeignClients(basePackages = "springboot.interview")
@ComponentScan(
    basePackages = "springboot.interview",
    excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*QA.*")
)
public class SpringBootInterviewApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootInterviewApplication.class, args);
    }
}
