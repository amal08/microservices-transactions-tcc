package net.jotorren.microservices.tx;

import java.util.Map;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.event.spi.DeleteEvent;
import org.hibernate.event.spi.DeleteEventListener;
import org.hibernate.event.spi.MergeEvent;
import org.hibernate.event.spi.MergeEventListener;
import org.hibernate.event.spi.PersistEvent;
import org.hibernate.event.spi.PersistEventListener;
import org.hibernate.event.spi.PostDeleteEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jotorren.microservices.context.SpringContext;
import net.jotorren.microservices.context.ThreadLocalContext;

public class ChangeStateJpaListener implements PersistEventListener, MergeEventListener, DeleteEventListener {

    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LoggerFactory.getLogger(ChangeStateJpaListener.class);

    private void enlist(Object entity, EntityCommand.Action action, String txId) {

        EntityCommand<Object> command = new EntityCommand<Object>();
        command.setEntity(entity);
        command.setAction(action);
        command.setTransactionId(txId);
        command.setTimestamp(System.currentTimeMillis());

        CompositeTransactionManager txManager = SpringContext.getBean(CompositeTransactionManager.class);
        txManager.enlist(txId, command);
    }

    public void onPostDelete(PostDeleteEvent event) {

    }

    @Override
    public void onDelete(DeleteEvent event) throws HibernateException {

    }

    @Override
    public void onDelete(DeleteEvent event, Set transientEntities) throws HibernateException {
        String txId = (String) ThreadLocalContext.get(CompositeTransactionParticipantService.CURRENT_TRANSACTION_KEY);
        if (null == txId) {
            LOG.info("onPreRemove outside any transaction");
        } else {
            LOG.info("onPreRemove inside transaction [{}]", txId);
            enlist(event.getObject(), EntityCommand.Action.DELETE, txId);
        }

    }

    @Override
    public void onPersist(PersistEvent event) throws HibernateException {
        String txId = (String) ThreadLocalContext.get(CompositeTransactionParticipantService.CURRENT_TRANSACTION_KEY);
        if (null == txId) {
            LOG.info("onPrePersist outside any transaction");
        } else {
            LOG.info("onPrePersist inside transaction [{}]", txId);
            enlist(event.getObject(), EntityCommand.Action.INSERT, txId);
        }

    }

    @Override
    public void onPersist(PersistEvent event, Map createdAlready) throws HibernateException {

    }

    @Override
    public void onMerge(MergeEvent event) throws HibernateException {
        String txId = (String) ThreadLocalContext.get(CompositeTransactionParticipantService.CURRENT_TRANSACTION_KEY);
        if (null == txId) {
            LOG.info("onPreUpdate outside any transaction");
        } else {
            LOG.info("onPreUpdate inside transaction [{}]", txId);
            enlist(event.getEntity(), EntityCommand.Action.UPDATE, txId);
        }

    }

    @Override
    public void onMerge(MergeEvent event, Map copiedAlready) throws HibernateException {
        // TODO Auto-generated method stub

    }

}
