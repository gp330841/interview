package springboot.interview.beans;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * Spring Boot - Spring Beans & Lifecycle Interview Questions
 * Contains practical code examples for Bean Scopes and Lifecycle concepts.
 */
public class BeansQA {

    // Q5: How do you change the scope of a Bean? [Easy]
    public void q5() {
        /*
         * Answer:
         * Using the @Scope annotation. By default it is "singleton".
         */
        
        // Example:
        @Component
        @Scope("prototype") // A new instance is created every time it's requested
        class PrototypeWorker {
            public void doWork() { }
        }
    }

    // Q7 & Q8: PostConstruct and PreDestroy [Easy]
    public void q7_q8() {
        /*
         * Answer:
         * @PostConstruct runs immediately after dependency injection.
         * @PreDestroy runs just before the bean is destroyed by the container.
         */
        
        // Example:
        @Component
        class DatabaseConnectionPool {
            
            @PostConstruct
            public void init() {
                System.out.println("1. Dependencies injected. Opening DB connections now...");
            }
            
            @PreDestroy
            public void cleanup() {
                System.out.println("2. Application shutting down. Closing DB connections safely.");
            }
        }
    }

    // Q14: Three ways to define Initialization and Destruction callbacks [Medium]
    public void q14() {
        /*
         * Answer:
         * 1. JSR-250 Annotations (@PostConstruct, @PreDestroy)
         * 2. Spring Interfaces (InitializingBean, DisposableBean)
         * 3. @Bean attributes (initMethod, destroyMethod)
         */

        // Example using Interfaces (Way #2)
        @Component
        class LegacyBean implements InitializingBean, DisposableBean {
            @Override
            public void afterPropertiesSet() throws Exception {
                System.out.println("InitializingBean: afterPropertiesSet executed.");
            }

            @Override
            public void destroy() throws Exception {
                System.out.println("DisposableBean: destroy executed.");
            }
        }

        // Example using @Bean attributes (Way #3)
        class ThirdPartyBean {
            public void customInit() { System.out.println("Custom Init"); }
            public void customCleanup() { System.out.println("Custom Cleanup"); }
        }

        @Configuration
        class AppConfig {
            @Bean(initMethod = "customInit", destroyMethod = "customCleanup")
            public ThirdPartyBean thirdPartyBean() {
                return new ThirdPartyBean();
            }
        }
    }

    // Q29 & Q30: @Configuration proxyBeanMethods (Lite Mode) [Hard]
    public void q29_q30() {
        /*
         * Answer:
         * Setting proxyBeanMethods = false disables CGLIB proxying. Calling a @Bean method 
         * directly will create a NEW instance rather than returning the singleton.
         */

        class DependencyA {}
        class DependencyB {
            DependencyB(DependencyA a) {}
        }

        @Configuration(proxyBeanMethods = false) // Lite mode (faster startup)
        class LiteConfig {
            
            @Bean
            public DependencyA a() {
                return new DependencyA(); 
            }

            @Bean
            public DependencyB b() {
                // Because proxyBeanMethods=false, calling a() here creates a NEW instance
                // of DependencyA, completely separate from the DependencyA bean in the IoC container.
                return new DependencyB(a()); 
            }
        }
    }
}
