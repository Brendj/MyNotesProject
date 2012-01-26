/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core;

import ru.axetta.ecafe.processor.core.persistence.TransactionJournal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: damir
 * Date: 26.01.12
 * Time: 16:07
 * To change this template use File | Settings | File Templates.
 */

@Component
@Scope("singleton")

public class TransactionJournalService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionJournalService.class);

    @PersistenceContext
    private EntityManager entityManager;
    @Resource
    private PlatformTransactionManager transactionManager;

    public static class TransactionJournalItem{

    }

    public void processTransactionJournalQueue() {
        //To change body of created methods use File | Settings | File Templates.
        TransactionStatus trx = null;
        try{
            trx = transactionManager.getTransaction(new DefaultTransactionDefinition());
            writeTJ(new TransactionJournalItem());
        } catch (Throwable e) {
            logger.error("Failed to save journal events to db", e);
            transactionManager.rollback(trx);
        }
    }

    @Transactional
    public void writeTJ(TransactionJournalItem transactionJournalItem) throws Exception {
        try {
             createTJ();
        } catch (Exception e) {
            logger.error("Failed to init application.", e);
            throw e;
        }
    }

    private void createTJ(){
        Date currentTime = new Date();
        TransactionJournal transactionJournal = new TransactionJournal();
        logger.info(transactionJournal.toString());
        entityManager.persist(transactionJournal);
        logger.info("transaction Journal created");
    }
}
