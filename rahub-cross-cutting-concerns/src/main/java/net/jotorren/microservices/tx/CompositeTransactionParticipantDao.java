package net.jotorren.microservices.tx;

import java.util.List;

import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Scope;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;

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
        em.setFlushMode(FlushMode.MANUAL);
        return em;
    }

    public Object save(Object entity) {
        Session em = getEntityManager();
        em.persist(entity);
        return entity;
    }

    public <T> T saveOrUpdate(T entity) {
        Session em = getEntityManager();
        T t = (T) em.merge(entity);
        return t;
    }

    public void remove(Object entity) {
        Session em = getEntityManager();
        em.delete(entity);

    }

    public void saveAndCommit(Object entity) {
        Session em = getEntityManager();
        em.persist(entity);
        em.flush();
    }

    public <T> T updateAndCommit(T entity) {
        Session em = getEntityManager();
        T t = (T) em.merge(entity);
        em.flush();
        return t;
    }

    public void removeAndCommit(Object entity) {
        Session em = getEntityManager();
        em.delete(entity);
        em.flush();
        em.clear();
    }

    public void apply(List<EntityCommand<?>> transactionOperations) {
        if (null == transactionOperations) {
            return;
        }

        for (EntityCommand<?> command : transactionOperations) {
            switch (command.getAction().ordinal()) {
            case 0:
                saveAndCommit(command.getEntity());
                break;
            case 1:
                updateAndCommit(command.getEntity());
                break;
            case 2:
                removeAndCommit(command.getEntity());
                break;
            }
        }
    }

}
