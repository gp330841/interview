package springboot.interview.beans.components;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class LifecycleDemoBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DatabaseConnectionPool) {
            System.out.println("--> BeanPostProcessor: Before Initialization for " + beanName);
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof DatabaseConnectionPool) {
            System.out.println("--> BeanPostProcessor: After Initialization for " + beanName);
        }
        return bean;
    }
}
