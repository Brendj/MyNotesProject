/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core;


import generated.opc.ru.msk.schemas.uec.common.v1.AdditionalDataType;
import generated.opc.ru.msk.schemas.uec.common.v1.ErrorType;
import generated.opc.ru.msk.schemas.uec.identification.v1.HolderIdDescriptionType;
import generated.opc.ru.msk.schemas.uec.identification.v1.LegalIdDescriptionType;
import generated.opc.ru.msk.schemas.uec.identification.v1.OrganizationType;
import generated.opc.ru.msk.schemas.uec.transaction.v1.*;
import generated.opc.ru.msk.schemas.uec.transactionservice.v1.*;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;

import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

//import org.apache.cxf.annotations.Logging;
//import org.apache.cxf.interceptor.InInterceptors;
//import org.apache.cxf.interceptor.LoggingInInterceptor;
//import org.apache.cxf.interceptor.LoggingOutInterceptor;
////import org.apache.cxf.interceptor.OutInterceptors;
//import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;
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
@DependsOn("runtimeContext")
public class TransactionJournalService {
    private static final int MAX_RECORDS_IN_BATCH = 100;

    private static final Logger logger = LoggerFactory.getLogger(TransactionJournalService.class);

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private RuntimeContext runtimeContext;

    private boolean isActivated;

    public boolean isActivated() {
        return isActivated;
    }

    public void setActivated(boolean activated) {
        isActivated = activated;
    }

    public void processTransactionJournalQueue() {
        if (!runtimeContext.getOptionValueBool(Option.OPTION_SEND_JOURNAL_TRANSACTIONS_TO_NFP)) return;
        if (!runtimeContext.isMainNode()) return;
        for (;;) {
            try {
                if (!RuntimeContext.getAppContext().getBean(TransactionJournalService.class).processTransactionBatch()) break;
            } catch (Throwable throwable) {
                logger.error("Ошибка при обработке журнала транзакций", throwable);
                break;
            }
        }
    }

    @Transactional
    public boolean processTransactionBatch() throws Throwable {
        List<TransactionJournal> tjList = DAOUtils.fetchTransactionJournalRecs(entityManager, MAX_RECORDS_IN_BATCH);
        if (tjList.size()==0) return false;
        //вызов веб службы
        TransactionService service = new TransactionService();
        TransactionServicePortType port = service.getTransactionServicePort();
        TransactionListType transactionListType = new TransactionListType();
        Client client = ClientProxy.getClient(port);

        String nfpUrl = runtimeContext.getOptionValueString(Option.OPTION_NFP_SERVICE_ADDRESS);
        BindingProvider provider = (BindingProvider)port;
        provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, nfpUrl);

        client.getInInterceptors().add(new LoggingInInterceptor());
        client.getOutInterceptors().add(new LoggingOutInterceptor());
        long maxIdOfTransactionJournal=-1;
        for (TransactionJournal tj : tjList){
            if (tj.getIdOfTransactionJournal()>maxIdOfTransactionJournal) maxIdOfTransactionJournal = tj.getIdOfTransactionJournal();
            TransactionDescriptionType transactionDescriptionType = new TransactionDescriptionType();

            //info of organization
            TransactionSourceDescriptionType transactionSourceDescriptionType = new TransactionSourceDescriptionType();

            LegalIdDescriptionType legalIdDescriptionType = new LegalIdDescriptionType();
            legalIdDescriptionType.setIdCodeType("OGRN");
            legalIdDescriptionType.setIdCode(tj.getOGRN()==null?"":tj.getOGRN());
            transactionSourceDescriptionType.setTransactionSourceId(legalIdDescriptionType);
            transactionSourceDescriptionType.setOrganizationType(OrganizationType.SCHOOL);
            transactionSourceDescriptionType.setTransactionSystemCode("ISPP");

            transactionDescriptionType.setTransactionSourceDescription(transactionSourceDescriptionType);

            //info of Transaction
            TransactionIdDescriptionType transactionIdDescriptionType = new TransactionIdDescriptionType();
            transactionIdDescriptionType.setTransactionId(String.valueOf(tj.getIdOfTransactionJournal()));
            GregorianCalendar gregorianCalendar = new GregorianCalendar();
            gregorianCalendar.setTime(tj.getTransDate());
            XMLGregorianCalendar xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
            transactionIdDescriptionType.setTransactionDate(xmlGregorianCalendar);

            transactionDescriptionType.setTransactionIdDescription(transactionIdDescriptionType);

            // info of transaction Type Description
            TransactionTypeDescriptionType transactionTypeDescriptionType = new TransactionTypeDescriptionType();
            transactionTypeDescriptionType.setServiceCode(tj.getServiceCode());
            transactionTypeDescriptionType.setTransactionCode(tj.getTransactionCode());

            transactionDescriptionType.setTransactionTypeDescription(transactionTypeDescriptionType);

            //info of Cards
            /*
            *   <ns3:holderDescription>
                 <ns2:cardTypeCode>UEC</ns2:cardTypeCode>
                 <ns2:cardTypeName>Универсальная Электронная Карта</ns2:cardTypeName>
                 <ns2:cardIdentityCode>MUID</ns2:cardIdentityCode>
                 <ns2:cardIdentityName>Mifare UID</ns2:cardIdentityName>
                 <ns2:uecId>42cf30a</ns2:uecId>
              </ns3:holderDescription>
            * */
            HolderIdDescriptionType holderIdDescriptionType = new HolderIdDescriptionType();
            holderIdDescriptionType.setCardTypeCode(tj.getCardTypeCode());
            holderIdDescriptionType.setCardTypeName(tj.getCardTypeName());
            holderIdDescriptionType.setCardIdentityCode(tj.getCardIdentityCode());
            holderIdDescriptionType.setCardIdentityName(tj.getCardIdentityName());
            /* добавлено поле  UecId */
            holderIdDescriptionType.setUecId(tj.getUecUd());
            //holderIdDescriptionType.setSnils(tj.getClientSan());

            transactionDescriptionType.setHolderDescription(holderIdDescriptionType);

            // additional info
            AdditionalDataType additionalDataType = new AdditionalDataType();
            AdditionalDataType.AdditionalData additionalDataIAN = new AdditionalDataType.AdditionalData();
            additionalDataIAN.setAdditionalDataCode("ISPP_ACCOUNT_NUMBER");
            additionalDataIAN.setAdditionalDataDescription("Идентификатор лицевого счета");
            additionalDataIAN.setAdditionalDataValue(String.valueOf(tj.getContractId()));
            additionalDataType.getAdditionalData().add(additionalDataIAN);

            AdditionalDataType.AdditionalData additionalDataICT = new AdditionalDataType.AdditionalData();
            additionalDataICT.setAdditionalDataCode("ISPP_CLIENT_TYPE");
            additionalDataICT.setAdditionalDataDescription("Тип клиента");
            additionalDataICT.setAdditionalDataValue(tj.getClientType());
            additionalDataType.getAdditionalData().add(additionalDataICT);

            //info of accountingDescription
            if(null != tj.getServiceCode() && tj.getServiceCode().equals(TransactionJournal.SERVICE_CODE_SCHL_FD)){
                TransactionDescriptionType.AccountingDescription accountingDescription = new TransactionDescriptionType.AccountingDescription();
                AccountingDescriptionItemType accountingDescriptionItemType = new AccountingDescriptionItemType();
                AccountingDescriptionItemType.FinancialDescription financialDescription = new AccountingDescriptionItemType.FinancialDescription();
                FinancialDescriptionItemType financialDescriptionItemType = new FinancialDescriptionItemType();
                financialDescriptionItemType.setFinancialCode("DBT");
                financialDescriptionItemType.setFinancialAmount(BigDecimal.valueOf(tj.getFinancialAmount()));
                financialDescriptionItemType.setFinancialCurrency("RUR");
                gregorianCalendar.setTime(tj.getAccountingDate());
                xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
                financialDescriptionItemType.setAccountingDate(xmlGregorianCalendar);
                financialDescription.getFinancialDescriptionItem().add(financialDescriptionItemType);
                accountingDescriptionItemType.setFinancialDescription(financialDescription);
                accountingDescription.getAccountingDescriptionItem().add(accountingDescriptionItemType);
                transactionDescriptionType.setAccountingDescription(accountingDescription);
            }

            //Enter event info
            if(null != tj.getServiceCode() && tj.getServiceCode().equals(TransactionJournal.TRANS_CODE_DEBIT)){
                AdditionalDataType.AdditionalData additionalDataIIG = new AdditionalDataType.AdditionalData();
                additionalDataIIG.setAdditionalDataCode("ISPP_INPUT_GROUP");
                additionalDataIIG.setAdditionalDataDescription("Наименование входной группы");
                additionalDataIIG.setAdditionalDataValue(tj.getEnterName());
                additionalDataType.getAdditionalData().add(additionalDataIIG);
            }

            transactionDescriptionType.setAdditionalInfo(additionalDataType);

            transactionListType.getTransaction().add(transactionDescriptionType);
        }
        logger.info("Отправка транзакций: " + transactionListType.getTransaction().size());

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
                    sb.append("Отправка прошла успешно: ");
                    logger.info(sb.toString());
                } else {
                    sb.append("Ошибка отправки транзакции: ");
                    sb.append(errorType.getErrorDescription());

                    logger.error(sb.toString());
                }
            }
        }
        int nRecs = DAOUtils.deleteFromTransactionJournal(entityManager, maxIdOfTransactionJournal);
        logger.info("Удалено из журнала транзакций: " + nRecs);

        return true;
    }

}
