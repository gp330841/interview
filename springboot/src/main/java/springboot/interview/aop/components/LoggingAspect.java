package springboot.interview.aop.components;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    // Pointcut matches any method execution inside springboot.interview.transactions.components package
    @Pointcut("execution(* springboot.interview.transactions.components.*.*(..))")
    public void transactionServiceMethods() {}

    @Around("transactionServiceMethods()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        
        System.out.println("--> AOP [@Around]: Intercepting " + joinPoint.getSignature().getName());

        Object proceed;
        try {
            proceed = joinPoint.proceed(); // execute the actual method
        } catch (Exception e) {
            System.out.println("--> AOP [@Around]: Exception caught in " + joinPoint.getSignature().getName() + ": " + e.getMessage());
            throw e;
        }

        long executionTime = System.currentTimeMillis() - start;
        System.out.println("--> AOP [@Around]: " + joinPoint.getSignature().getName() + " executed in " + executionTime + "ms");

        return proceed;
    }
}
