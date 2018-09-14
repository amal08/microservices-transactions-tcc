package net.jotorren.microservices.forum.dao;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import net.jotorren.microservices.forum.domain.Forum;
import net.jotorren.microservices.tx.CompositeTransactionParticipantDao;

@Repository("ForumTransactionAwareDao")
@Scope("prototype")
public class ForumTransactionAwareDao extends CompositeTransactionParticipantDao {

    public Forum findOne(String pk) {
        return getEntityManager().get(Forum.class, pk);
    }
}
