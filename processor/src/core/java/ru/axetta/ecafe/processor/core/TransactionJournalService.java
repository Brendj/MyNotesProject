/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core;

import ru.axetta.ecafe.processor.core.persistence.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Collections;
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
    @Autowired
    private PlatformTransactionManager transactionManager;

    private List<TransactionJournalItem> transactionJournalItems= Collections.emptyList();
    
    public static class TransactionJournalItem{
        private long idOfTransactionJournal;
        private String serviceCode;
        private String transactionCode;
        private long cardIdentityCode;
        private long contractId;
        private String clientSnilsSan;
        private String enterName;
        private long orderRSum;
        private String clientType;
        private String cartTypeName;
        private String OGRN;
        private Date sycroDate;

        public TransactionJournalItem(TransactionJournal transactionJournal){
            this.idOfTransactionJournal = transactionJournal.getIdOfTransactionJournal();
            this.serviceCode = transactionJournal.getServiceCode();
            this.transactionCode = transactionJournal.getTransactionCode();
            this.cardIdentityCode = transactionJournal.getCardIdentityCode();
            this.contractId = transactionJournal.getContractId();
            this.clientSnilsSan = transactionJournal.getClientSnilsSan();
            this.enterName = transactionJournal.getEnterName();
            this.orderRSum = transactionJournal.getOrderRSum();
            this.clientType = transactionJournal.getClientType();
            this.cartTypeName = transactionJournal.getCartTypeName();
            this.OGRN = transactionJournal.getOGRN();
            this.sycroDate = transactionJournal.getSycroDate();
        }

        public long getIdOfTransactionJournal() {
            return idOfTransactionJournal;
        }

        public void setIdOfTransactionJournal(long idOfTransactionJournal) {
            this.idOfTransactionJournal = idOfTransactionJournal;
        }

        public String getServiceCode() {
            return serviceCode;
        }

        public void setServiceCode(String serviceCode) {
            this.serviceCode = serviceCode;
        }

        public String getTransactionCode() {
            return transactionCode;
        }

        public void setTransactionCode(String transactionCode) {
            this.transactionCode = transactionCode;
        }

        public long getCardIdentityCode() {
            return cardIdentityCode;
        }

        public void setCardIdentityCode(long cardIdentityCode) {
            this.cardIdentityCode = cardIdentityCode;
        }

        public long getContractId() {
            return contractId;
        }

        public void setContractId(long contractId) {
            this.contractId = contractId;
        }

        public String getClientSnilsSan() {
            return clientSnilsSan;
        }

        public void setClientSnilsSan(String clientSnilsSan) {
            this.clientSnilsSan = clientSnilsSan;
        }

        public String getEnterName() {
            return enterName;
        }

        public void setEnterName(String enterName) {
            this.enterName = enterName;
        }

        public long getOrderRSum() {
            return orderRSum;
        }

        public void setOrderRSum(long orderRSum) {
            this.orderRSum = orderRSum;
        }

        public String getClientType() {
            return clientType;
        }

        public void setClientType(String clientType) {
            this.clientType = clientType;
        }

        public String getCartTypeName() {
            return cartTypeName;
        }

        public void setCartTypeName(String cartTypeName) {
            this.cartTypeName = cartTypeName;
        }

        public String getOGRN() {
            return OGRN;
        }

        public void setOGRN(String OGRN) {
            this.OGRN = OGRN;
        }

        public Date getSycroDate() {
            return sycroDate;
        }

        public void setSycroDate(Date sycroDate) {
            this.sycroDate = sycroDate;
        }
    }
    
    public void processTransactionJournalQueue() {
        //To change body of created methods use File | Settings | File Templates.
        TransactionStatus trx = null;
        try{
            trx = transactionManager.getTransaction(new DefaultTransactionDefinition());
            buildTransactionJournal();
            //вызов веб службы

            cleanTransactionJournal();
            transactionManager.commit(trx);
        } catch (Throwable e) {
            logger.error("Failed to save journal events to db", e);
            transactionManager.rollback(trx);
        }
    }

    @Transactional
    public void buildTransactionJournal() throws Exception{
        try {
           List transactionJournalList = entityManager.createQuery("select tj from TransactionJournal tj").getResultList();
            if (transactionJournalList.isEmpty()){
                for(Object object: transactionJournalList){
                    TransactionJournal transactionJournal = (TransactionJournal) object;
                    TransactionJournalItem transactionJournalItem = new TransactionJournalItem(transactionJournal);
                    this.transactionJournalItems.add(transactionJournalItem);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to save journal events to db", e);
            throw e;
        }
    }

    @Transactional
    public void cleanTransactionJournal() throws Exception{
        try {
            List<TransactionJournal> transactionJournals = entityManager.createQuery("select tj from TransactionJournal tj").getResultList();
            if (!transactionJournals.isEmpty())
            {
                entityManager.createQuery("Delete from TransactionJournal");
            }
        } catch (Exception e) {
            logger.error("Failed to save journal events to db", e);
            throw e;
        }
    }

}
