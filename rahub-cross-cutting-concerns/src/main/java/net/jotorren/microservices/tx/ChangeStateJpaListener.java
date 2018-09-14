package net.jotorren.microservices.tx;

import org.hibernate.event.spi.PreDeleteEvent;
import org.hibernate.event.spi.PreDeleteEventListener;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.jotorren.microservices.context.SpringContext;
import net.jotorren.microservices.context.ThreadLocalContext;

public class ChangeStateJpaListener implements PreInsertEventListener, PreUpdateEventListener, PreDeleteEventListener {

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

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        String txId = (String) ThreadLocalContext.get(CompositeTransactionParticipantService.CURRENT_TRANSACTION_KEY);
        if (null == txId) {
            LOG.info("onPrePersist outside any transaction");
        } else {
            LOG.info("onPrePersist inside transaction [{}]", txId);
            enlist(event.getEntity(), EntityCommand.Action.INSERT, txId);
        }
        return true;
    }

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        String txId = (String) ThreadLocalContext.get(CompositeTransactionParticipantService.CURRENT_TRANSACTION_KEY);
        if (null == txId) {
            LOG.info("onPreUpdate outside any transaction");
        } else {
            LOG.info("onPreUpdate inside transaction [{}]", txId);
            enlist(event.getEntity(), EntityCommand.Action.UPDATE, txId);
        }
        return true;
    }

    @Override
    public boolean onPreDelete(PreDeleteEvent event) {
        String txId = (String) ThreadLocalContext.get(CompositeTransactionParticipantService.CURRENT_TRANSACTION_KEY);
        if (null == txId) {
            LOG.info("onPreRemove outside any transaction");
        } else {
            LOG.info("onPreRemove inside transaction [{}]", txId);
            enlist(event.getEntity(), EntityCommand.Action.DELETE, txId);
        }
        return false;
    }
}
