package springboot.interview.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Spring Boot - Aspect-Oriented Programming (AOP) Interview Questions
 * Contains practical code examples for Pointcuts, Advices, and Custom Annotations.
 */
public class AOPQA {

    // Custom Annotation for Q25
    @Target(ElementType.METHOD)
    @Retention(RetentionPolicy.RUNTIME)
    public @interface LogExecutionTime {
    }

    // Example Service Class to be intercepted
    @Component
    public static class UserService {
        
        @LogExecutionTime // Will be intercepted by Q25
        public void createUser(String name) {
            System.out.println("Creating user: " + name);
            // Simulate work
            try { Thread.sleep(100); } catch (InterruptedException e) {}
        }
        
        public void deleteUser(Long id) {
            System.out.println("Deleting user: " + id);
        }
    }

    // The Aspect Class
    @Aspect
    @Component
    public static class LoggingAspect {

        // Q17: How do you extract and reuse a Pointcut expression? [Medium]
        public void q17() {
            /*
             * Answer: Create an empty method annotated with @Pointcut to hold the expression.
             */
        }
        
        // Extracted Pointcut: Matches all methods inside UserService
        @Pointcut("execution(* springboot.interview.aop.AOPQA.UserService.*(..))")
        public void userServiceMethods() {}

        // Reusing the extracted Pointcut with a @Before advice
        @Before("userServiceMethods()")
        public void logBefore() {
            System.out.println("[AOP] @Before: Method is about to execute...");
        }

        // Q12: How do you pass the target method's execution context into an @Around advice? [Medium]
        // Q25: How do you intercept a method based on a custom annotation? [Hard]
        public void q12_q25() {
            /*
             * Answer: 
             * Q12: Pass ProceedingJoinPoint to the method signature and call pjp.proceed().
             * Q25: Use the @annotation pointcut designator targeting the fully qualified annotation name.
             */
        }

        // @Around advice matching ONLY methods annotated with @LogExecutionTime
        @Around("@annotation(springboot.interview.aop.AOPQA.LogExecutionTime)")
        public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
            
            System.out.println("[AOP] @Around: Intercepting method annotated with @LogExecutionTime");
            
            long start = System.currentTimeMillis();
            
            // Execute the actual target method (e.g., createUser)
            Object proceedResult = joinPoint.proceed(); 
            
            long executionTime = System.currentTimeMillis() - start;
            System.out.println("[AOP] @Around: " + joinPoint.getSignature().getName() + " executed in " + executionTime + "ms");
            
            return proceedResult;
        }

        // Q27: Is it possible to modify the arguments passed to the target method? [Hard]
        public void q27() {
            /*
             * Answer: Yes, using @Around advice. Retrieve args, modify them, and pass to proceed(args).
             */
        }
        
        // Example of modifying arguments
        @Around("execution(* springboot.interview.aop.AOPQA.UserService.createUser(..))")
        public Object modifyArguments(ProceedingJoinPoint pjp) throws Throwable {
            Object[] args = pjp.getArgs();
            
            if (args.length > 0 && args[0] instanceof String) {
                String originalName = (String) args[0];
                System.out.println("[AOP] Original Argument: " + originalName);
                
                // Modifying the argument
                args[0] = originalName.toUpperCase() + " (Modified by AOP)";
            }
            
            // Proceed with the MODIFIED arguments
            return pjp.proceed(args);
        }
    }
}
