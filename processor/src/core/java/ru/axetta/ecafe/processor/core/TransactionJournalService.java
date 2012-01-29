/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core;

import ru.axetta.ecafe.processor.core.persistence.*;

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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    //private Org org;
    //private EnterEvent enterEvent;
    private List<Client> clients;
        /*
    public static class TransactionJournalItem{

    }
          */
    public void processTransactionJournalQueue() {
        //To change body of created methods use File | Settings | File Templates.
        TransactionStatus trx = null;
        try{
            trx = transactionManager.getTransaction(new DefaultTransactionDefinition());
            transactClient();
            writeTransactionJournal();



            transactionManager.commit(trx);
        } catch (Throwable e) {
            logger.error("Failed to save journal events to db", e);
            transactionManager.rollback(trx);
        }
    }

    @Transactional
    public void writeTransactionJournal() throws Exception {
        try {
            createTJClient();
        } catch (Exception e) {
            logger.error("Failed to save journal events to db", e);
            throw e;
        }
    }

    @Transactional
    public void transactClient() throws Exception{
        try {
           this.clients = entityManager.createQuery("select c from Client c where c.org.idOfOrg=2").getResultList();
            logger.info(String.valueOf(this.clients.size()));
        } catch (Exception e) {
            logger.error("Failed to save journal events to db", e);
            throw e;
        }
    }

    private void createTJClient() throws Exception{
        if(null != this.clients){
            for(Client client: clients){
                if(null != client.getEnterEvents()){
                    List<EnterEvent> enterEventList =new ArrayList<EnterEvent>(client.getEnterEvents());
                    for (EnterEvent enterEvent: enterEventList){
                        TransactionJournal transactionJournal = new TransactionJournal();
                        //OGRN
                        transactionJournal.setOGRN(client.getOrg().getOGRN());
                        //transactionSystemCode
                        transactionJournal.setTransactionCode("ISPP");
                        //transactionIdDescription
                        Date currentTime = new Date();
                        transactionJournal.setSycroDate(currentTime);
                        //transactionTypeDescription
                        transactionJournal.setServiceCode("SCHL_ACC");
                        String passdirection;
                        switch (enterEvent.getPassDirection()){
                            case 0: passdirection="IN"; break;
                            case 1: passdirection="OUT";  break;
                            default: passdirection="ERROR";
                        }
                        transactionJournal.setTransactionCode(passdirection);
                        //holderDescription
                        List<Card> cardList =new ArrayList<Card>(client.getCards());
                        if(!cardList.isEmpty()){
                            Card card=cardList.get(0);
                            transactionJournal.setCartTypeName(Card.TYPE_NAMES[card.getCardType()]);
                            transactionJournal.setCartTypeName("Универсальная Электронная Карта");
                            transactionJournal.setCardIdentityCode(card.getCardNo());
                        }
                        //snils
                        transactionJournal.setClientSnilsSan(client.getSan());
                        //additionalInfo
                        //ISPP_ACCOUNT_NUMBER
                        transactionJournal.setContractId(client.getContractId());
                        //ISPP_CLIENT_TYPE
                        transactionJournal.setClientType(client.getClientGroup().getGroupName());
                        //ISPP_INPUT_GROUP
                        transactionJournal.setEnterName(enterEvent.getEnterName());
                        entityManager.persist(transactionJournal);
                    }
                }
                if(null != client.getOrders()){
                    List<Order> orderList =new ArrayList<Order>(client.getOrders());
                    for (Order order: orderList){
                        TransactionJournal transactionJournal = new TransactionJournal();
                        //OGRN
                        transactionJournal.setOGRN(client.getOrg().getOGRN());
                        //transactionSystemCode
                        transactionJournal.setTransactionCode("ISPP");
                        //transactionIdDescription
                        Date currentTime = new Date();
                        transactionJournal.setSycroDate(currentTime);
                        //transactionTypeDescription
                        transactionJournal.setServiceCode("SCHL_FD");

                        //holderDescription
                        List<Card> cardList =new ArrayList<Card>(client.getCards());
                        if(!cardList.isEmpty()){
                            Card card=cardList.get(0);
                            transactionJournal.setCartTypeName(Card.TYPE_NAMES[card.getCardType()]);
                            transactionJournal.setCartTypeName("Универсальная Электронная Карта");
                            transactionJournal.setCardIdentityCode(card.getCardNo());
                        }
                        //snils
                        transactionJournal.setClientSnilsSan(client.getSan());
                        //accountingDescription
                        transactionJournal.setOrderRSum(order.getRSum());
                        transactionJournal.setContractId(client.getContractId());

                        //transactionJournal.getClientType(client.getClientGroup());

                        entityManager.persist(transactionJournal);
                    }
                }
            }
        }
    }
             /*
    private void CreateTJ(){
        if(null != org.getClients()){
            List<Client> clientList=new ArrayList<Client>(org.getClients());
            for(Client client: clientList){
                if(null != client.getEnterEvents()){
                    List<EnterEvent> enterEventList = new ArrayList<EnterEvent>(client.getEnterEvents());
                    for (EnterEvent enterEvent1: enterEventList){
                        String passdirection;
                        switch (enterEvent1.getPassDirection()){
                            case 0: passdirection="IN"; break;
                            case 1: passdirection="OUT";  break;
                            default: passdirection="ERROR";
                        }
                        Date currentTime = new Date();
                        TransactionJournal transactionJournal = new TransactionJournal();
                        transactionJournal.setOGRN(org.getOGRN());
                        transactionJournal.setServiceCode("SCHL_ACC");
                        transactionJournal.setTransactionCode(passdirection);
                        List<Card> cardList =new ArrayList<Card>(client.getCards());
                        if(!cardList.isEmpty()){
                            Card card=cardList.get(0);
                            transactionJournal.setCartTypeName(Card.TYPE_NAMES[card.getCardType()]);
                            transactionJournal.setCartTypeName("Универсальная Электронная Карта");
                            transactionJournal.setCardIdentityCode(card.getCardNo());
                        }
                        transactionJournal.setClientSnilsSan(client.getSan());
                        transactionJournal.setContractId(client.getContractId());
                        transactionJournal.setSycroDate(currentTime);
                        logger.info(transactionJournal.toString());
                        entityManager.persist(transactionJournal);
                        logger.info(transactionJournal.toString());


                     client.getClientGroup();
                     client.getContractId();


                    }
                }  else{
                    Date currentTime = new Date();
                    TransactionJournal transactionJournal = new TransactionJournal();
                    transactionJournal.setOGRN(org.getOGRN());
                    transactionJournal.setServiceCode("SCHL_ACC");
                    transactionJournal.setSycroDate(currentTime);
                    logger.info(transactionJournal.toString());
                    entityManager.persist(transactionJournal);
                    logger.info("null != client.getEnterEvents()");
                }
            }
        } else {
            Date currentTime = new Date();
            TransactionJournal transactionJournal = new TransactionJournal();
            transactionJournal.setOGRN(org.getOGRN());
            transactionJournal.setServiceCode("SCHL_ACC");
            transactionJournal.setSycroDate(currentTime);
            logger.info(transactionJournal.toString());
            entityManager.persist(transactionJournal);
            logger.info("null != org.getClients()");
        }
        logger.info("End");
    }
    
    private void createTransactionJournal(){
        Date currentTime = new Date();
        TransactionJournal transactionJournal = new TransactionJournal();
        transactionJournal.setServiceCode("SCHL_ACC");
        String passdirection;
        switch (enterEvent.getPassDirection()){
            case 0: passdirection="IN"; break;
            case 1: passdirection="OUT";  break;
            default: passdirection="ERROR";
        }
        transactionJournal.setTransactionCode(passdirection);
        if(null != enterEvent.getOrg().getOGRN()){
            transactionJournal.setOGRN(enterEvent.getOrg().getOGRN());
        }
        transactionJournal.setEnterName(enterEvent.getEnterName());
        if(null != enterEvent.getClient().getCards()){
            List<Card> cardList =new ArrayList<Card>(enterEvent.getClient().getCards());
            if(!cardList.isEmpty()){
                Card card=cardList.get(0);
                transactionJournal.setCartTypeName(Card.TYPE_NAMES[card.getCardType()]);
                transactionJournal.setCartTypeName("Универсальная Электронная Карта");
                transactionJournal.setCardIdentityCode(card.getCardNo());
            }
        }
        transactionJournal.setContractId(enterEvent.getClient().getContractId());
        transactionJournal.setClientSnilsSan(enterEvent.getClient().getSan());
        if(null != enterEvent.getClient().getClientGroup().getGroupName()){
            transactionJournal.setClientType(enterEvent.getClient().getClientGroup().getGroupName());
        }
        if(null != enterEvent.getClient().getOrders()){
            List<Order> orderList =new ArrayList<Order>(enterEvent.getClient().getOrders());
            logger.info(String.valueOf(orderList.size()));
            if(!orderList.isEmpty()){
                Order order=orderList.get(0);
                transactionJournal.setOrderRSum(order.getRSum());
            }
        }
        transactionJournal.setSycroDate(currentTime);
        entityManager.persist(transactionJournal);
    }

    @Transactional
    public void transactEnterName() throws Exception{
        try {
            List<EnterEvent> enterEventList = entityManager.createQuery("select e from EnterEvent e where e.compositeIdOfEnterEvent.idOfOrg="+this.org.getIdOfOrg()).getResultList();
            for(EnterEvent event: enterEventList){
                if(!event.getEnterName().equals("")){
                    this.enterEvent=event;
                    break;
                }
            }
        } catch (Exception e) {
            logger.error("Failed to save journal events to db", e);
            throw e;
        }
    }

    @Transactional
    public void transactOrg() throws Exception{
        try {
            List orgList = entityManager.createQuery("select o from Org o").getResultList();
            for(Object obj:  orgList){
                this.org=(Org) obj;
                break;
            }
        } catch (Exception e) {
            logger.error("Failed to save journal events to db", e);
            throw e;
        }
    }    */

}
