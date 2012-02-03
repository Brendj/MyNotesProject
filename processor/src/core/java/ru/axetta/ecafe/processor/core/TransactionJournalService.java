/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core;


import ru.msk.schemas.uec.common.v1.AdditionalDataType;
import ru.msk.schemas.uec.common.v1.ErrorType;
import ru.msk.schemas.uec.identification.v1.HolderIdDescriptionType;
import ru.msk.schemas.uec.identification.v1.LegalIdDescriptionType;
import ru.msk.schemas.uec.identification.v1.OrganizationType;
import ru.msk.schemas.uec.transaction.v1.*;
import ru.msk.schemas.uec.transactionservice.v1.TransactionService;
import ru.msk.schemas.uec.transactionservice.v1.TransactionServicePortType;

import ru.axetta.ecafe.processor.core.persistence.*;

import org.apache.cxf.annotations.Logging;
import org.apache.cxf.interceptor.InInterceptors;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.interceptor.OutInterceptors;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.Service;
import java.math.BigDecimal;
import java.util.*;

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

    private static List<String> infoOfTransactionJournal = new LinkedList<String>();

    public static List<String> getInfoOfTransactionJournal() {
        return infoOfTransactionJournal;
    }

    private List<TransactionJournalItem> transactionJournalItems= new LinkedList<TransactionJournalItem>();

    public static class TransactionJournalItem{
        private long idOfTransactionJournal;
        private String serviceCode;
        private String transactionCode;
        private long contractId;
        private String clientSnilsSan;
        private String enterName;
        private String clientType;
        private String OGRN;
        private Date sycroDate;

        private String cardIdentityName;
        private String cardIdentityCode;
        private String cardTypeName;
        private String cardTypeCode;

        private long orderRSum;
        private Date accountingDate;

        public Date getAccountingDate() {
            return accountingDate;
        }

        public void setAccountingDate(Date accountingDate) {
            this.accountingDate = accountingDate;
        }

        public TransactionJournalItem(TransactionJournal transactionJournal) {
            this.idOfTransactionJournal = transactionJournal.getIdOfTransactionJournal();;
            this.serviceCode = transactionJournal.getServiceCode();
            this.transactionCode = transactionJournal.getTransactionCode();
            this.contractId = transactionJournal.getContractId();
            this.clientSnilsSan = transactionJournal.getClientSnilsSan();
            this.enterName = transactionJournal.getEnterName();
            this.clientType = transactionJournal.getClientType();
            this.OGRN = transactionJournal.getOGRN();
            this.sycroDate = transactionJournal.getSycroDate();
            this.cardIdentityName =transactionJournal.getCardIdentityName();
            this.cardIdentityCode =transactionJournal.getCardIdentityCode();
            this.cardTypeName = transactionJournal.getCardTypeName();
            this.cardTypeCode = transactionJournal.getCardTypeCode();
            this.orderRSum = transactionJournal.getOrderRSum();
            this.accountingDate = transactionJournal.getAccountingDate();
        }

        public String getCardIdentityName() {
            return cardIdentityName;
        }

        public void setCardIdentityName(String cardIdentityName) {
            this.cardIdentityName = cardIdentityName;
        }

        public String getCardIdentityCode() {
            return cardIdentityCode;
        }

        public void setCardIdentityCode(String cardIdentityCode) {
            this.cardIdentityCode = cardIdentityCode;
        }

        public String getCardTypeName() {
            return cardTypeName;
        }

        public void setCardTypeName(String cardTypeName) {
            this.cardTypeName = cardTypeName;
        }

        public String getCardTypeCode() {
            return cardTypeCode;
        }

        public void setCardTypeCode(String cardTypeCode) {
            this.cardTypeCode = cardTypeCode;
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

        @Override
        public String toString() {
            return "TransactionJournalItem{" +
                    "idOfTransactionJournal=" + idOfTransactionJournal +
                    ", serviceCode='" + serviceCode + '\'' +
                    ", transactionCode='" + transactionCode + '\'' +
                    ", contractId=" + contractId +
                    ", clientSnilsSan='" + clientSnilsSan + '\'' +
                    ", enterName='" + enterName + '\'' +
                    ", clientType='" + clientType + '\'' +
                    ", OGRN='" + OGRN + '\'' +
                    ", sycroDate=" + sycroDate +
                    ", cardIdentityName='" + cardIdentityName + '\'' +
                    ", cardIdentityCode='" + cardIdentityCode + '\'' +
                    ", cardTypeName='" + cardTypeName + '\'' +
                    ", cardTypeCode='" + cardTypeCode + '\'' +
                    ", orderRSum=" + orderRSum +
                    ", accountingDate=" + accountingDate +
                    '}';
        }
    }


    public void processTransactionJournalQueue() {
        List<TransactionJournalItem> curTransactionJournalItems=Collections.emptyList();
            /*
        synchronized (TransactionJournalService.class) {
            if (transactionJournalItems.size() == 0) {
                return;
            }
            curTransactionJournalItems = transactionJournalItems;
            transactionJournalItems = new LinkedList<TransactionJournalItem>();
        }     */
        curTransactionJournalItems = transactionJournalItems;
        TransactionStatus trx = null;
        try{
            trx = transactionManager.getTransaction(new DefaultTransactionDefinition());
            buildTransactionJournal();
            //вызов веб службы
            TransactionService service = new TransactionService();
            TransactionServicePortType port = service.getTransactionServicePort();
            TransactionListType transactionListType = new TransactionListType();

            if(!curTransactionJournalItems.isEmpty()){

                for (TransactionJournalItem transactionJournalItem: curTransactionJournalItems){




                    TransactionDescriptionType transactionDescriptionType = new TransactionDescriptionType();

                    //info of organization
                    TransactionSourceDescriptionType transactionSourceDescriptionType = new TransactionSourceDescriptionType();

                    LegalIdDescriptionType legalIdDescriptionType = new LegalIdDescriptionType();
                    legalIdDescriptionType.setIdCodeType("OGRN");
                    legalIdDescriptionType.setIdCode(transactionJournalItem.getOGRN());
                    transactionSourceDescriptionType.setTransactionSourceId(legalIdDescriptionType);
                    transactionSourceDescriptionType.setOrganizationType(OrganizationType.SCHOOL);
                    transactionSourceDescriptionType.setTransactionSystemCode("ISPP");

                    transactionDescriptionType.setTransactionSourceDescription(transactionSourceDescriptionType);

                    //info of Transaction
                    TransactionIdDescriptionType transactionIdDescriptionType = new TransactionIdDescriptionType();
                    transactionIdDescriptionType.setTransactionId(String.valueOf(transactionJournalItem.getIdOfTransactionJournal()));
                    GregorianCalendar gregorianCalendar = new GregorianCalendar();
                    gregorianCalendar.setTime(transactionJournalItem.getSycroDate());
                    XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
                    transactionIdDescriptionType.setTransactionDate(xmlGregorianCalendar);

                    transactionDescriptionType.setTransactionIdDescription(transactionIdDescriptionType);

                    // info of transaction Type Description
                    TransactionTypeDescriptionType transactionTypeDescriptionType = new TransactionTypeDescriptionType();
                    transactionTypeDescriptionType.setServiceCode(transactionJournalItem.getServiceCode());
                    transactionTypeDescriptionType.setTransactionCode(transactionJournalItem.getTransactionCode());

                    transactionDescriptionType.setTransactionTypeDescription(transactionTypeDescriptionType);

                    //info of Carts
                    HolderIdDescriptionType holderIdDescriptionType = new HolderIdDescriptionType();
                    holderIdDescriptionType.setCardIdentityCode(transactionJournalItem.getCardIdentityCode());
                    holderIdDescriptionType.setCardIdentityName(transactionJournalItem.getCardIdentityName());
                    holderIdDescriptionType.setCardTypeCode(transactionJournalItem.getCardTypeCode());
                    holderIdDescriptionType.setUecId(transactionJournalItem.getCardTypeName());
                    holderIdDescriptionType.setSnils(transactionJournalItem.getClientSnilsSan());
                    transactionDescriptionType.setHolderDescription(holderIdDescriptionType);

                    //info of accountingDescription

                    if(null != transactionJournalItem.getServiceCode() && transactionJournalItem.getServiceCode().equals("SCHL_FD")){
                        TransactionDescriptionType.AccountingDescription accountingDescription = new TransactionDescriptionType.AccountingDescription();
                        AccountingDescriptionItemType accountingDescriptionItemType = new AccountingDescriptionItemType();
                        AccountingDescriptionItemType.FinancialDescription financialDescription = new AccountingDescriptionItemType.FinancialDescription();
                        FinancialDescriptionItemType financialDescriptionItemType = new FinancialDescriptionItemType();
                        financialDescriptionItemType.setFinancialCode("DBT");
                        financialDescriptionItemType.setFinancialAmount(BigDecimal.valueOf(transactionJournalItem.getOrderRSum()));
                        financialDescriptionItemType.setFinancialCurrency("RUR");
                        gregorianCalendar.setTime(transactionJournalItem.getAccountingDate());
                        xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
                        financialDescriptionItemType.setAccountingDate(xmlGregorianCalendar);
                        financialDescription.getFinancialDescriptionItem().add(financialDescriptionItemType);
                        accountingDescriptionItemType.setFinancialDescription(financialDescription);
                        accountingDescription.getAccountingDescriptionItem().add(accountingDescriptionItemType);
                        transactionDescriptionType.setAccountingDescription(accountingDescription);
                    }


                    // additional info

                    AdditionalDataType additionalDataType = new AdditionalDataType();
                    AdditionalDataType.AdditionalData additionalDataIAN = new AdditionalDataType.AdditionalData();
                    additionalDataIAN.setAdditionalDataCode("ISPP_ACCOUNT_NUMBER");
                    additionalDataIAN.setAdditionalDataDescription("Идентификатор лицевого счета");
                    additionalDataIAN.setAdditionalDataValue(String.valueOf(transactionJournalItem.getContractId()));
                    additionalDataType.getAdditionalData().add(additionalDataIAN);

                    AdditionalDataType.AdditionalData additionalDataICT = new AdditionalDataType.AdditionalData();
                    additionalDataICT.setAdditionalDataCode("ISPP_CLIENT_TYPE");
                    additionalDataICT.setAdditionalDataDescription("Тип клиента");
                    additionalDataICT.setAdditionalDataValue(transactionJournalItem.getClientType());
                    additionalDataType.getAdditionalData().add(additionalDataICT);

                    //Enter event info

                    if(null != transactionJournalItem.getServiceCode() && transactionJournalItem.getServiceCode().equals("SCHL_ACC")){
                        AdditionalDataType.AdditionalData additionalDataIIG = new AdditionalDataType.AdditionalData();
                        additionalDataIIG.setAdditionalDataCode("ISPP_INPUT_GROUP");
                        additionalDataIIG.setAdditionalDataDescription("Наименование входной группы");
                        additionalDataIIG.setAdditionalDataValue(transactionJournalItem.getEnterName());
                        additionalDataType.getAdditionalData().add(additionalDataIIG);
                    }

                    transactionDescriptionType.setAdditionalInfo(additionalDataType);

                    transactionListType.getTransaction().add(transactionDescriptionType);


                }
                ErrorListType quote = port.storeTransactions(transactionListType);

                if(null != quote){
                    for(ErrorType errorType: quote.getError()) {
                        logger.info(errorType.getErrorCode()+" : "+errorType.getErrorDescription());
                        Date transactionDate = new Date();
                        GregorianCalendar gregorianCalendar = new GregorianCalendar();
                        StringBuilder sb= new StringBuilder();
                        gregorianCalendar.setTime(transactionDate);
                        XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
                        sb.append("[");
                        sb.append(xmlGregorianCalendar);
                        sb.append("]");
                        if(errorType.getErrorCode().equals("0")){
                            sb.append(" Транзакция прошла успешно: ");
                            infoOfTransactionJournal.add(sb.toString());
                            cleanTransactionJournal();
                        } else {
                            sb.append(" Ошибка транзакции: ");
                            sb.append(errorType.getErrorDescription());

                            infoOfTransactionJournal.add(sb.toString());
                        }
                    }
                }

            }
            transactionManager.commit(trx);
        } catch (Throwable e) {
            logger.error("Failed to save journal events to db", e);
            transactionManager.rollback(trx);
        }
    }



    @Transactional
    public void buildTransactionJournal() throws Exception{
        //synchronized (TransactionJournalService.class) {
            List transactionJournalList = entityManager.createQuery("select tj from TransactionJournal tj").getResultList();

            if (!transactionJournalList.isEmpty()){
                for(Object object: transactionJournalList){
                     TransactionJournal transactionJournal = (TransactionJournal) object;
                     TransactionJournalItem transactionJournalItem = new TransactionJournalItem(transactionJournal);
                     this.transactionJournalItems.add(transactionJournalItem);
                }
            }
        //}
    }

    @Transactional
    public void cleanTransactionJournal() throws Exception{
       // synchronized (TransactionJournalService.class) {
            List<TransactionJournal> transactionJournals = entityManager.createQuery("select tj from TransactionJournal tj").getResultList();
            if (!transactionJournals.isEmpty())
            {
                for(Object object: transactionJournals){
                    TransactionJournal transactionJournal = (TransactionJournal) object;
                    entityManager.remove(transactionJournal);
                }
            }
       // }
    }

}
