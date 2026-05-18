package springboot.interview.beans.components;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConnectionPool implements InitializingBean, DisposableBean {

    public DatabaseConnectionPool() {
        System.out.println("1. DatabaseConnectionPool: Constructor called.");
    }

    @PostConstruct
    public void customInit() {
        System.out.println("2. DatabaseConnectionPool: @PostConstruct called.");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        System.out.println("3. DatabaseConnectionPool: InitializingBean.afterPropertiesSet called.");
    }

    @PreDestroy
    public void customDestroy() {
        System.out.println("4. DatabaseConnectionPool: @PreDestroy called.");
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("5. DatabaseConnectionPool: DisposableBean.destroy called.");
    }
}
