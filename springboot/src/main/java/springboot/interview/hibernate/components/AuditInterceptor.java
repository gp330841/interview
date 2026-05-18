package springboot.interview.hibernate.components;

import org.hibernate.CallbackException;
import org.hibernate.Interceptor;
import org.hibernate.type.Type;
import org.springframework.stereotype.Component;
import springboot.interview.domain.entity.Order;

@Component
public class AuditInterceptor implements Interceptor {

    @Override
    public boolean onSave(Object entity, Object id, Object[] state, String[] propertyNames, Type[] types) throws CallbackException {
        if (entity instanceof Order) {
            System.out.println("--> Hibernate Interceptor: Saving a new Order");
        }
        return false;
    }
}
