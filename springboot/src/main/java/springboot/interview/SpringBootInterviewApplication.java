package springboot.interview;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableFeignClients(basePackages = "springboot.interview")
@ComponentScan(
    basePackages = "springboot.interview",
    excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = ".*QA.*")
)
@EntityScan(basePackages = "springboot.interview.domain.entity")
@EnableJpaRepositories(basePackages = "springboot.interview.domain.repository")
public class SpringBootInterviewApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringBootInterviewApplication.class, args);
    }
}
