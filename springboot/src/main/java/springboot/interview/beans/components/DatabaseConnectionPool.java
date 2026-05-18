package springboot.interview.beans.components;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class DatabaseConnectionPool implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseConnectionPool.class);

    public DatabaseConnectionPool() {
        logger.info("1. DatabaseConnectionPool: Constructor called.");
    }

    @PostConstruct
    public void init() {
        logger.info("2. DatabaseConnectionPool: @PostConstruct called.");
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("3. DatabaseConnectionPool: InitializingBean.afterPropertiesSet called.");
    }

    @PreDestroy
    public void cleanup() {
        logger.info("4. DatabaseConnectionPool: @PreDestroy called.");
    }

    @Override
    public void destroy() throws Exception {
        logger.info("5. DatabaseConnectionPool: DisposableBean.destroy called.");
    }
}
