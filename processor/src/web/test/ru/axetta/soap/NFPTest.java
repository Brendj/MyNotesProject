/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.soap;

import generated.opc.ru.msk.schemas.uec.common.v1.AdditionalDataType;
import generated.opc.ru.msk.schemas.uec.common.v1.ErrorType;
import generated.opc.ru.msk.schemas.uec.identification.v1.HolderIdDescriptionType;
import generated.opc.ru.msk.schemas.uec.identification.v1.LegalIdDescriptionType;
import generated.opc.ru.msk.schemas.uec.identification.v1.OrganizationType;
import generated.opc.ru.msk.schemas.uec.transaction.v1.*;
import generated.opc.ru.msk.schemas.uec.transactionservice.v1.TransactionService;
import generated.opc.ru.msk.schemas.uec.transactionservice.v1.TransactionServicePortType;
import junit.framework.TestCase;

import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.TransactionJournal;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class NFPTest extends TestCase {

    public void testStore() throws Exception {
        String nfpUrl= "http://10.126.216.2:3000/gateway/services/SID0003103";
        TransactionService service = new TransactionService(new URL(nfpUrl+"?wsdl"), new QName("http://schemas.msk.ru/uec/TransactionService/v1", "TransactionService"));
        TransactionServicePortType port = service.getTransactionServicePort();
        TransactionListType transactionListType = new TransactionListType();
        //Client client = ClientProxy.getClient(port);

        BindingProvider provider = (BindingProvider)port;
        provider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, nfpUrl);

        List<Handler> handlerChain = new ArrayList<Handler>();
              handlerChain.add(new SignHandlerTest.LogMessageHandler());

        //((BindingProvider)port).getBinding().setHandlerChain(handlerChain);

        //client.getInInterceptors().add(new LoggingInInterceptor());
        //client.getOutInterceptors().add(new LoggingOutInterceptor());
        long maxIdOfTransactionJournal=-1;
        long idOfOrg=0, idOfEnterEvent=0; String ogrn="12345678";
        String cardNo="12345", san="12345678905"; long contractId=200485;
        TransactionJournal tj = new TransactionJournal(
                idOfOrg,
                idOfEnterEvent, new Date(),
                ogrn, TransactionJournal.SERVICE_CODE_SCHL_ACC, "IN",
                TransactionJournal.CARD_TYPE_CODE_UEC,
                TransactionJournal.CARD_TYPE_ID_CODE_MUID, "Mifare",
                cardNo, san, contractId,
                "Сотрудники", "Вход-1");

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
        holderIdDescriptionType.setCardTypeName("UEC");
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

        ErrorListType quote = port.storeTransactions(transactionListType);

        if(null != quote){
            for(ErrorType errorType: quote.getError()) {
                System.out.print(errorType.getErrorCode()+" : "+errorType.getErrorDescription());
                Date transactionDate = new Date();
                gregorianCalendar = new GregorianCalendar();
                StringBuilder sb= new StringBuilder();
                gregorianCalendar.setTime(transactionDate);
                xmlGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(gregorianCalendar);
                sb.append("[");
                sb.append(xmlGregorianCalendar);
                sb.append("]");
                if(errorType.getErrorCode().equals("0")){
                    sb.append("Отправка прошла успешно: ");
                    System.out.println(sb.toString());
                } else {
                    sb.append("Ошибка отправки транзакции: ");
                    sb.append(errorType.getErrorDescription());

                    System.out.println(sb.toString());
                }
            }
        }
    }
}
