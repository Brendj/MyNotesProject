/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.questionaryservice;

import ru.axetta.ecafe.processor.core.persistence.questionary.Answer;
import ru.axetta.ecafe.processor.core.persistence.questionary.Questionary;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 21.12.12
 * Time: 17:27
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
public class TestQuesrtionary {

    private static Logger logger = LoggerFactory.getLogger(TestQuesrtionary.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @PostConstruct
    public void init(){
        /*logger.info("test create");
        try {
            //onDelete();
        } catch (Exception e){
            logger.error("error create Answer", e);
        }*/
    }

    protected void onDelete() {
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);
        TypedQuery<Questionary> query = entityManager.createQuery("from Questionary", Questionary.class);
        for (Questionary questionary: query.getResultList()){
            for (Answer answer : questionary.getAnswers()){
                entityManager.remove(answer);
            }
            entityManager.remove(questionary);
        }
        transactionManager.commit(status);
    }

    protected void onCreate() throws Exception{
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();
        TransactionStatus status = transactionManager.getTransaction(def);
        Questionary questionary = new Questionary("Questionary");
        entityManager.persist(questionary);
        for (int i=0; i<10; i++){
            Answer answer =new Answer("answer "+i, questionary);
            entityManager.persist(answer);
        }
        transactionManager.commit(status);
    }

}
