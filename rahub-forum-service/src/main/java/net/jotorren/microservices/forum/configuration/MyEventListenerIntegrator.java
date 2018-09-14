package net.jotorren.microservices.forum.configuration;

import javax.persistence.PersistenceUnit;

import org.hibernate.boot.Metadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.integrator.spi.Integrator;
import org.hibernate.service.spi.SessionFactoryServiceRegistry;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;

import net.jotorren.microservices.forum.domain.Forum;

public class MyEventListenerIntegrator implements Integrator {

    @PersistenceUnit
    private LocalSessionFactoryBean localSessionFactory;

    @Override
    public void integrate(Metadata metadata, SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {
        EventListenerRegistry eventListenerRegistry = serviceRegistry.getService(EventListenerRegistry.class);
        eventListenerRegistry.appendListeners(EventType.PRE_INSERT, new Forum());
        eventListenerRegistry.appendListeners(EventType.PRE_UPDATE, new Forum());
        eventListenerRegistry.appendListeners(EventType.PRE_DELETE, new Forum());

    }

    @Override
    public void disintegrate(SessionFactoryImplementor sessionFactory, SessionFactoryServiceRegistry serviceRegistry) {

    }

}
