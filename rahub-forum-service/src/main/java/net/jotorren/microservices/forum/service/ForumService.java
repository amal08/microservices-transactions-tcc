package net.jotorren.microservices.forum.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.jotorren.microservices.context.ThreadLocalContext;
import net.jotorren.microservices.forum.dao.ForumDao;
import net.jotorren.microservices.forum.dao.ForumTransactionAwareDao;
import net.jotorren.microservices.forum.domain.Forum;
import net.jotorren.microservices.tx.CompositeTransactionParticipantDao;
import net.jotorren.microservices.tx.CompositeTransactionParticipantService;
import net.jotorren.microservices.tx.EntityCommand;

@Service
@Transactional
public class ForumService extends CompositeTransactionParticipantService {

    private static final Logger LOG = LoggerFactory.getLogger(ForumService.class);

    @Autowired
    private ApplicationContext context;

    @Autowired
    private ForumDao dao;

    public String addNewForum(Forum Forum) {
        String uuid = UUID.randomUUID().toString();
        Forum.setForumId(uuid);
        Integer.parseInt(Forum.getTopicCategory());
        Forum saved = dao.save(Forum);
        return saved.getForumId();
    }

    public Forum getForum(String pk) {
        return dao.findOne(pk);
    }

    // Composite Transaction methods

    @Override
    public CompositeTransactionParticipantDao getCompositeTransactionDao() {
        return context.getBean(ForumTransactionAwareDao.class);
    }

    public String addNewForum(String txId, Forum Forum) {
        ThreadLocalContext.put(CURRENT_TRANSACTION_KEY, txId);

        String uuid = UUID.randomUUID().toString();
        Forum.setForumId(uuid);

        LOG.info("Creating transaction [{}]", txId);
        Integer.parseInt(Forum.getTopicCategory());
        // throw exception if not integer
        Forum saved = getCompositeTransactionDao().saveOrUpdate(Forum);

        return saved.getForumId();
    }

    public Forum getForum(String txId, String pk) {
        ThreadLocalContext.remove(CURRENT_TRANSACTION_KEY);

        LOG.warn("Looking for transaction [{}]", txId);
        List<EntityCommand<?>> transactionOperations = getCompositeTransactionManager().fetch(txId);
        if (null == transactionOperations) {
            LOG.error("Transaction [{}] does not exist", txId);
            return null;
        }
        ForumTransactionAwareDao unsynchronizedDao = (ForumTransactionAwareDao) getCompositeTransactionDao();
        unsynchronizedDao.apply(transactionOperations);

        return unsynchronizedDao.findOne(pk);
    }
}
