package net.jotorren.microservices.tx;

import java.util.List;

import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository("CompositeTransactionParticipantDao")
@Scope(BeanDefinition.SCOPE_PROTOTYPE)
public class CompositeTransactionParticipantDao {

    // @PersistenceContext(type = PersistenceContextType.EXTENDED, synchronization = SynchronizationType.UNSYNCHRONIZED)
    // private EntityManager em;

    @Autowired(required = true)
    @Qualifier("sessionFactory")
    private LocalSessionFactoryBean localSessionFactory;

    public Session getEntityManager() {
        Session em = localSessionFactory.getObject().getCurrentSession();
        return em;
    }

    public void save(Object entity) {

        getEntityManager().persist(entity);
    }

    public <T> T saveOrUpdate(T entity) {
        return (T) getEntityManager().merge(entity);
    }

    public void remove(Object entity) {
        getEntityManager().delete(entity);
    }

    @Transactional(readOnly = false)
    public void commit() {
        getEntityManager().flush();
        getEntityManager().clear();
    }

    public void apply(List<EntityCommand<?>> transactionOperations) {
        if (null == transactionOperations) {
            return;
        }

        for (EntityCommand<?> command : transactionOperations) {
            switch (command.getAction().ordinal()) {
            case 0:
                save(command.getEntity());
                break;
            case 1:
                saveOrUpdate(command.getEntity());
                break;
            case 2:
                remove(command.getEntity());
                break;
            }
        }
    }
}
