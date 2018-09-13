package net.jotorren.microservices.content.dao;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import net.jotorren.microservices.content.domain.SourceCodeItem;
import net.jotorren.microservices.tx.CompositeTransactionParticipantDao;

@Repository("ContentTransactionAwareDao")
@Scope("prototype")
public class ContentTransactionAwareDao extends CompositeTransactionParticipantDao {

    public SourceCodeItem findOne(String pk) {
        return getEntityManager().get(SourceCodeItem.class, pk);
    }
}
