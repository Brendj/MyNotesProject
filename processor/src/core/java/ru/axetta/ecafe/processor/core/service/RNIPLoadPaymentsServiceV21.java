/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import generated.ru.gov.smev.artefacts.x.services.message_exchange._1.SMEVMessageExchangePortType;
import generated.ru.gov.smev.artefacts.x.services.message_exchange._1.SMEVMessageExchangeService;
import generated.ru.gov.smev.artefacts.x.services.message_exchange.types._1.*;
import generated.ru.gov.smev.artefacts.x.services.message_exchange.types.basic._1.MessagePrimaryContent;
import generated.ru.gov.smev.artefacts.x.services.message_exchange.types.basic._1.ObjectFactory;
import generated.ru.gov.smev.artefacts.x.services.message_exchange.types.basic._1.XMLDSigSignatureType;
import generated.ru.mos.rnip.xsd.catalog._2_1.*;
import generated.ru.mos.rnip.xsd.common._2_1.*;
import generated.ru.mos.rnip.xsd.searchconditions._2_1.ExportPaymentsKindType;
import generated.ru.mos.rnip.xsd.searchconditions._2_1.PaymentsExportConditions;
import generated.ru.mos.rnip.xsd.searchconditions._2_1.TimeConditionsType;
import generated.ru.mos.rnip.xsd.services.export_payments._2_1.ExportPaymentsRequest;
import generated.ru.mos.rnip.xsd.services.import_catalog._2_1.ImportCatalogRequest;
import generated.ru.mos.rnip.xsd.services.import_catalog._2_1.ImportCatalogResponse;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.RnipEventType;
import ru.axetta.ecafe.processor.core.persistence.RnipMessage;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import generated.ru.gov.smev.artefacts.x.services.message_exchange.types.basic._1.ObjectFactory;

/**
 * Created by nuc on 17.10.2018.
 */
@Component("RNIPLoadPaymentsServiceV21")
@Scope("singleton")
public class RNIPLoadPaymentsServiceV21 extends RNIPLoadPaymentsServiceV116 {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RNIPLoadPaymentsServiceV21.class);
    private static SMEVMessageExchangeService service21;
    private static SMEVMessageExchangePortType port21;
    private static BindingProvider bindingProvider21;

    public static final int PAGING_VALUE = 100;

    private final static ThreadLocal<String> hasError = new ThreadLocal<String>(){
        @Override protected String initialValue() { return null; }
    };

    @Override
    public Object executeRequest(int requestType, Contragent contragent, Date updateDate, Date startDate, Date endDate) throws Exception {
        SendRequestResponse requestResponse = (SendRequestResponse)executeRequest21(requestType, contragent, updateDate, startDate, endDate);
        if (isRequestQueued(requestResponse)) {
            RnipEventType eventType = null;
            if (requestType == REQUEST_MODIFY_CATALOG) eventType = RnipEventType.CONTRAGENT_EDIT;
            else if (requestType == REQUEST_CREATE_CATALOG) eventType = RnipEventType.CONTRAGENT_CREATE;
            RnipDAOService.getInstance().saveRnipMessage(requestResponse, contragent, eventType);
        }
        return requestResponse;
    }

    private boolean isRequestQueued(SendRequestResponse requestResponse) {
        try {
            return requestResponse.getMessageMetadata().getStatus().equals(generated.ru.gov.smev.artefacts.x.services.message_exchange.types.basic._1.InteractionStatusType.REQUEST_IS_QUEUED);
        } catch (Exception e) {
            logger.error("Response from RNIP - request was not queued", e);
        }
        return false;
    }

    public Object executeRequest21(int requestType, Contragent contragent, Date updateDate, Date startDate, Date endDate) throws Exception {
        switch(requestType) {
            case REQUEST_MODIFY_CATALOG:
            case REQUEST_CREATE_CATALOG:
                return executeModifyCatalog20(requestType, contragent, updateDate, startDate, endDate);
            case REQUEST_LOAD_PAYMENTS:
            case REQUEST_LOAD_PAYMENTS_MODIFIED:
                requests = new HashMap<Contragent, Integer>();
                int attempt = 1;
                String uuid = UUID.randomUUID().toString();

                List<SendRequestResponse> result = new ArrayList<SendRequestResponse>();
                SendRequestResponse messageDataType = executeLoadPaymentsV21(requestType, contragent, updateDate, startDate, endDate, attempt);
                result.add(messageDataType);
                String error = checkError(messageDataType);
                if (error != null) throw new Exception(String.format("RNIP v 2.1 check error: %s", error));
                while (hasMore(attempt)) {
                    attempt++;
                    messageDataType = executeLoadPaymentsV21(requestType, contragent, updateDate, startDate, endDate, attempt);
                    error = checkError(messageDataType);
                    if (error != null) throw new Exception(String.format("RNIP v 2.1 check error: %s", error));
                    result.add(messageDataType);
                }
                return result;
        }
        return null;
    }

    @Override
    protected String getRNIPUrl() {
        return RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_URL_V20);
    }

    private boolean hasMore(int attempt) {
        return false;
    }

    private SendRequestRequest getMessageHeaderV21(Contragent contragent) {
        generated.ru.gov.smev.artefacts.x.services.message_exchange.types._1.ObjectFactory requestObjectFactory =
                new generated.ru.gov.smev.artefacts.x.services.message_exchange.types._1.ObjectFactory();
        SendRequestRequest sendRequestRequest = requestObjectFactory.createSendRequestRequest();
        SenderProvidedRequestData senderProvidedRequestData = requestObjectFactory.createSenderProvidedRequestData();
        senderProvidedRequestData.setId(RNIPSecuritySOAPHandlerV21.SIGN_ID);
        sendRequestRequest.setSenderProvidedRequestData(senderProvidedRequestData);

        senderProvidedRequestData.setMessageID(UUID.randomUUID().toString());
        //senderProvidedRequestData.setMessageID("0a73eac6-7440-4a2c-b1f8-e70425776916");

        SenderProvidedRequestData.Sender sender = requestObjectFactory.createSenderProvidedRequestDataSender();
        sender.setMnemonic(getMacroPart(contragent, "CONTRAGENT_ID"));
        senderProvidedRequestData.setSender(sender);

        generated.ru.gov.smev.artefacts.x.services.message_exchange.types.basic._1.ObjectFactory messagePrimaryObjectFactory =
                new generated.ru.gov.smev.artefacts.x.services.message_exchange.types.basic._1.ObjectFactory();
        MessagePrimaryContent messagePrimaryContent = messagePrimaryObjectFactory.createMessagePrimaryContent();
        senderProvidedRequestData.setMessagePrimaryContent(messagePrimaryContent);

        XMLDSigSignatureType callerInformationSystemSignature = messagePrimaryObjectFactory.createXMLDSigSignatureType();
        sendRequestRequest.setCallerInformationSystemSignature(callerInformationSystemSignature);

        return sendRequestRequest;
    }

    public SendRequestResponse executeLoadPaymentsV21(int requestType, Contragent contragent, Date updateDate, Date startDate, Date endDate, int attempt) throws Exception {
        InitRNIP21Service();

        String alias = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_ALIAS);
        String pass = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_PASSWORD);
        final RNIPSecuritySOAPHandlerV21 pfrSecuritySOAPHandler = new RNIPSecuritySOAPHandlerV21(alias, pass, getPacketLogger(contragent));
        final List<Handler> handlerChain = new ArrayList<Handler>();
        handlerChain.add(pfrSecuritySOAPHandler);
        bindingProvider21.getBinding().setHandlerChain(handlerChain);

        SendRequestRequest sendRequestRequest = getMessageHeaderV21(contragent);

        generated.ru.mos.rnip.xsd.services.export_payments._2_1.ObjectFactory exportPaymentObjectFactory =
                new generated.ru.mos.rnip.xsd.services.export_payments._2_1.ObjectFactory();

        ExportPaymentsRequest exportPaymentsRequest = exportPaymentObjectFactory.createExportPaymentsRequest();
        exportPaymentsRequest.setId(String.format("N_%s", UUID.randomUUID().toString()));
        exportPaymentsRequest.setTimestamp(RNIPSecuritySOAPHandler.toXmlGregorianCalendar(new Date()));
        exportPaymentsRequest.setSenderIdentifier(getMacroPart(contragent, "CONTRAGENT_ID"));

        generated.ru.mos.rnip.xsd.common._2_1.ObjectFactory commonObjectFactory = new generated.ru.mos.rnip.xsd.common._2_1.ObjectFactory();
        PagingType paging = commonObjectFactory.createPagingType();
        paging.setPageLength(BigInteger.valueOf(PAGING_VALUE));
        paging.setPageNumber(BigInteger.valueOf(attempt));
        exportPaymentsRequest.setPaging(paging);

        generated.ru.mos.rnip.xsd.searchconditions._2_1.ObjectFactory searchConditionsObjectFactory = new generated.ru.mos.rnip.xsd.searchconditions._2_1.ObjectFactory();
        PaymentsExportConditions paymentsExportConditions = searchConditionsObjectFactory.createPaymentsExportConditions();
        switch (requestType) {
            case REQUEST_LOAD_PAYMENTS_MODIFIED:
                paymentsExportConditions.setKind(ExportPaymentsKindType.PAYMENTMODIFIED.value());
                break;
            case REQUEST_LOAD_PAYMENTS:
            default:
                paymentsExportConditions.setKind(ExportPaymentsKindType.PAYMENT.value());
                break;
        }
        TimeConditionsType timeConditions = searchConditionsObjectFactory.createTimeConditionsType();
        TimeIntervalType timeInterval = commonObjectFactory.createTimeIntervalType();
        Date sDate;
        Date eDate;
        if(startDate == null) {
            logger.warn("Auto start time");
            sDate = getStartDateByLastUpdateDate(updateDate);
        } else {
            logger.warn("Manual start: "+startDate);
            sDate = startDate;
        }
        if (endDate == null) {
            logger.warn("Auto end time");
            eDate = getEndDateByStartDate(sDate);
        } else {
            logger.warn("Manual end time");
            eDate = endDate;
        }
        timeInterval.setStartDate(RNIPSecuritySOAPHandler.toXmlGregorianCalendar(sDate));
        timeInterval.setEndDate(RNIPSecuritySOAPHandler.toXmlGregorianCalendar(eDate));
        timeConditions.setTimeInterval(timeInterval);
        paymentsExportConditions.setTimeConditions(timeConditions);
        exportPaymentsRequest.setPaymentsExportConditions(paymentsExportConditions);

        sendRequestRequest.getSenderProvidedRequestData().getMessagePrimaryContent().setExportPaymentsRequest(exportPaymentsRequest);

        SendRequestResponse response = null;
        try {
            response = port21.sendRequest(sendRequestRequest);
        } catch (Exception e) {
            logger.error("Error in request to rnip 2.1", e);
        }
        return response;
    }

    public SendRequestResponse executeModifyCatalog20(int requestType, Contragent contragent, Date updateDate, Date startDate, Date endDate) throws Exception {
        InitRNIP21Service();

        String alias = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_ALIAS);
        String pass = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_PASSWORD);
        final RNIPSecuritySOAPHandlerV21 pfrSecuritySOAPHandler = new RNIPSecuritySOAPHandlerV21(alias, pass, getPacketLogger(contragent));
        final List<Handler> handlerChain = new ArrayList<Handler>();
        handlerChain.add(pfrSecuritySOAPHandler);
        bindingProvider21.getBinding().setHandlerChain(handlerChain);

        SendRequestRequest sendRequestRequest = getMessageHeaderV21(contragent);

        generated.ru.mos.rnip.xsd.services.import_catalog._2_1.ObjectFactory importCatalogObjectFactory =
                new generated.ru.mos.rnip.xsd.services.import_catalog._2_1.ObjectFactory();
        ImportCatalogRequest importCatalogRequest = importCatalogObjectFactory.createImportCatalogRequest();
        importCatalogRequest.setId(String.format("I_%s", UUID.randomUUID()));
        importCatalogRequest.setTimestamp(RNIPSecuritySOAPHandler.toXmlGregorianCalendar(
                RuntimeContext.getInstance().getDefaultLocalCalendar(null).getTime()));
        importCatalogRequest.setSenderIdentifier(getMacroPart(contragent, "CONTRAGENT_ID"));
        sendRequestRequest.getSenderProvidedRequestData().getMessagePrimaryContent().setImportCatalogRequest(importCatalogRequest);

        generated.ru.mos.rnip.xsd.catalog._2_1.ObjectFactory serviceCatalogObjectFactory = new generated.ru.mos.rnip.xsd.catalog._2_1.ObjectFactory();
        ServiceCatalogType serviceCatalogType = serviceCatalogObjectFactory.createServiceCatalogType();
        if (requestType == RNIPLoadPaymentsService.REQUEST_MODIFY_CATALOG) {
            importCatalogRequest.setChanges(serviceCatalogType);
        } else if (requestType == RNIPLoadPaymentsService.REQUEST_CREATE_CATALOG) {
            importCatalogRequest.setServiceCatalog(serviceCatalogType);
        }
        //importCatalogRequest.setServiceCatalog(serviceCatalogType);
        serviceCatalogType.setId(String.format("I_%s", UUID.randomUUID().toString()));
        serviceCatalogType.setName("Изменение");
        serviceCatalogType.setRevisionDate(RNIPSecuritySOAPHandler.toXmlGregorianCalendar(
                RuntimeContext.getInstance().getDefaultLocalCalendar(null).getTime()));

        generated.ru.mos.rnip.xsd.catalog._2_1.ObjectFactory serviceTypeObjectFactory = new generated.ru.mos.rnip.xsd.catalog._2_1.ObjectFactory();
        ServiceType serviceType = serviceTypeObjectFactory.createServiceType();
        serviceType.setCode("AAAA" + getMacroPart(contragent, "CONTRAGENT_ID") + "0000000001");
        serviceType.setDesc("Услуги по оплате питания учеников в образовательных учреждениях");
        serviceType.setIsActive(true);
        serviceType.setName("Услуга питания в ОУ");
        serviceType.setRevisionDate(RNIPSecuritySOAPHandler.toXmlGregorianCalendar(
                RuntimeContext.getInstance().getDefaultLocalCalendar(null).getTime()));
        ServiceCategoryType serviceCategoryType = serviceTypeObjectFactory.createServiceCategoryType();
        serviceCategoryType.setCode("PIP0000019");
        serviceCategoryType.setName("Недоступно для оплаты");
        serviceType.setServiceCategory(serviceCategoryType);

        DescriptionParametersType descriptionParametersType = serviceTypeObjectFactory.createDescriptionParametersType();

        DescriptionSimpleParameter descriptionSimpleParameter1 = serviceTypeObjectFactory.createDescriptionSimpleParameter();
        descriptionSimpleParameter1.setForPayment(true);
        descriptionSimpleParameter1.setForSearch(false);
        descriptionSimpleParameter1.setLabel("Код гос. услуги");
        descriptionSimpleParameter1.setName("SRV_CODE");
        descriptionSimpleParameter1.setReadonly(true);
        descriptionSimpleParameter1.setRequired(true);
        descriptionSimpleParameter1.setVisible(false);
        descriptionSimpleParameter1.setDefaultValue("AAAA" + getMacroPart(contragent, "CONTRAGENT_ID") + "0000000001");

        DescriptionSimpleParameter descriptionSimpleParameter2 = serviceTypeObjectFactory.createDescriptionSimpleParameter();
        descriptionSimpleParameter2.setForPayment(true);
        descriptionSimpleParameter2.setForSearch(true);
        descriptionSimpleParameter2.setLabel("Номер договора");
        descriptionSimpleParameter2.setName("PAYMENT_TO");
        descriptionSimpleParameter2.setIsId(BigInteger.valueOf(1));
        descriptionSimpleParameter2.setReadonly(false);
        descriptionSimpleParameter2.setRequired(true);
        descriptionSimpleParameter2.setVisible(true);
        descriptionSimpleParameter2.setRegexp("^\\d{6,15}$");

        DescriptionSimpleParameter descriptionSimpleParameter3 = serviceTypeObjectFactory.createDescriptionSimpleParameter();
        descriptionSimpleParameter3.setForPayment(true);
        descriptionSimpleParameter3.setForSearch(false);
        descriptionSimpleParameter3.setLabel("Идентификатор поставщика");
        descriptionSimpleParameter3.setName("PAYMENT");
        descriptionSimpleParameter3.setReadonly(true);
        descriptionSimpleParameter3.setRequired(true);
        descriptionSimpleParameter3.setVisible(false);
        descriptionSimpleParameter3.setDefaultValue(getMacroPart(contragent, "CONTRAGENT_BMID"));

        DescriptionSimpleParameter descriptionSimpleParameter4 = serviceTypeObjectFactory.createDescriptionSimpleParameter();
        descriptionSimpleParameter4.setForPayment(true);
        descriptionSimpleParameter4.setForSearch(false);
        descriptionSimpleParameter4.setLabel("Статус плательщика");
        descriptionSimpleParameter4.setName("STATUS");
        descriptionSimpleParameter4.setReadonly(false);
        descriptionSimpleParameter4.setRequired(true);
        descriptionSimpleParameter4.setVisible(false);
        descriptionSimpleParameter4.setDefaultValue("00");

        DescriptionSimpleParameter descriptionSimpleParameter5 = serviceTypeObjectFactory.createDescriptionSimpleParameter();
        descriptionSimpleParameter5.setForPayment(true);
        descriptionSimpleParameter5.setForSearch(false);
        descriptionSimpleParameter5.setLabel("Тип платежа");
        descriptionSimpleParameter5.setName("PAYMENTTYPE");
        descriptionSimpleParameter5.setReadonly(false);
        descriptionSimpleParameter5.setRequired(true);
        descriptionSimpleParameter5.setVisible(false);
        descriptionSimpleParameter5.setDefaultValue("0");

        DescriptionSimpleParameter descriptionSimpleParameter6 = serviceTypeObjectFactory.createDescriptionSimpleParameter();
        descriptionSimpleParameter6.setForPayment(true);
        descriptionSimpleParameter6.setForSearch(false);
        descriptionSimpleParameter6.setLabel("Основание платежа");
        descriptionSimpleParameter6.setName("PURPOSE");
        descriptionSimpleParameter6.setReadonly(false);
        descriptionSimpleParameter6.setRequired(true);
        descriptionSimpleParameter6.setVisible(false);
        descriptionSimpleParameter6.setDefaultValue("0");

        DescriptionSimpleParameter descriptionSimpleParameter7 = serviceTypeObjectFactory.createDescriptionSimpleParameter();
        descriptionSimpleParameter7.setForPayment(true);
        descriptionSimpleParameter7.setForSearch(false);
        descriptionSimpleParameter7.setLabel("Налоговый период");
        descriptionSimpleParameter7.setName("TAXPERIOD");
        descriptionSimpleParameter7.setReadonly(false);
        descriptionSimpleParameter7.setRequired(true);
        descriptionSimpleParameter7.setVisible(false);
        descriptionSimpleParameter7.setDefaultValue("0");

        DescriptionSimpleParameter descriptionSimpleParameter8 = serviceTypeObjectFactory.createDescriptionSimpleParameter();
        descriptionSimpleParameter8.setForPayment(true);
        descriptionSimpleParameter8.setForSearch(false);
        descriptionSimpleParameter8.setLabel("Показатель номера документа");
        descriptionSimpleParameter8.setName("TAXDOCNUMBER");
        descriptionSimpleParameter8.setReadonly(false);
        descriptionSimpleParameter8.setRequired(true);
        descriptionSimpleParameter8.setVisible(false);
        descriptionSimpleParameter8.setDefaultValue("0");

        DescriptionSimpleParameter descriptionSimpleParameter9 = serviceTypeObjectFactory.createDescriptionSimpleParameter();
        descriptionSimpleParameter9.setForPayment(true);
        descriptionSimpleParameter9.setForSearch(false);
        descriptionSimpleParameter9.setLabel("Показатель даты документа");
        descriptionSimpleParameter9.setName("TAXDOCDATE");
        descriptionSimpleParameter9.setReadonly(false);
        descriptionSimpleParameter9.setRequired(true);
        descriptionSimpleParameter9.setVisible(false);
        descriptionSimpleParameter9.setDefaultValue("0");

        DescriptionSimpleParameter descriptionSimpleParameter10 = serviceTypeObjectFactory.createDescriptionSimpleParameter();
        descriptionSimpleParameter10.setForPayment(true);
        descriptionSimpleParameter10.setForSearch(false);
        descriptionSimpleParameter10.setLabel("Код бюджетной классификации");
        descriptionSimpleParameter10.setName("KBK");
        descriptionSimpleParameter10.setReadonly(true);
        descriptionSimpleParameter10.setRequired(true);
        descriptionSimpleParameter10.setVisible(false);
        descriptionSimpleParameter10.setDefaultValue(getMacroPart(contragent, "KBK"));

        DescriptionSimpleParameter descriptionSimpleParameter11 = serviceTypeObjectFactory.createDescriptionSimpleParameter();
        descriptionSimpleParameter11.setForPayment(true);
        descriptionSimpleParameter11.setForSearch(false);
        descriptionSimpleParameter11.setLabel("Наименование получателя");
        descriptionSimpleParameter11.setName("Recipient");
        descriptionSimpleParameter11.setReadonly(true);
        descriptionSimpleParameter11.setRequired(true);
        descriptionSimpleParameter11.setVisible(true);
        descriptionSimpleParameter11.setDefaultValue(getMacroPart(contragent, "CONTRAGENT_NAME"));

        DescriptionSimpleParameter descriptionSimpleParameter12 = serviceTypeObjectFactory.createDescriptionSimpleParameter();
        descriptionSimpleParameter12.setForPayment(true);
        descriptionSimpleParameter12.setForSearch(false);
        descriptionSimpleParameter12.setLabel("Назначение платежа");
        descriptionSimpleParameter12.setName("Narrative");
        descriptionSimpleParameter12.setReadonly(false);
        descriptionSimpleParameter12.setRequired(true);
        descriptionSimpleParameter12.setVisible(true);
        descriptionSimpleParameter12.setDefaultValue("Оплата услуг питания в образовательном учреждении");

        DescriptionSimpleParameter descriptionSimpleParameter13 = serviceTypeObjectFactory.createDescriptionSimpleParameter();
        descriptionSimpleParameter13.setForPayment(false);
        descriptionSimpleParameter13.setForSearch(false);
        descriptionSimpleParameter13.setLabel("Признак дублирования в ГИС ГМП");
        descriptionSimpleParameter13.setName("duplication");
        descriptionSimpleParameter13.setReadonly(true);
        descriptionSimpleParameter13.setRequired(false);
        descriptionSimpleParameter13.setVisible(false);
        descriptionSimpleParameter13.setDefaultValue("off");

        descriptionParametersType.getDescriptionSimpleParameterOrDescriptionComplexParameter().add(descriptionSimpleParameter1);
        descriptionParametersType.getDescriptionSimpleParameterOrDescriptionComplexParameter().add(descriptionSimpleParameter2);
        descriptionParametersType.getDescriptionSimpleParameterOrDescriptionComplexParameter().add(descriptionSimpleParameter3);
        descriptionParametersType.getDescriptionSimpleParameterOrDescriptionComplexParameter().add(descriptionSimpleParameter4);
        descriptionParametersType.getDescriptionSimpleParameterOrDescriptionComplexParameter().add(descriptionSimpleParameter5);
        descriptionParametersType.getDescriptionSimpleParameterOrDescriptionComplexParameter().add(descriptionSimpleParameter6);
        descriptionParametersType.getDescriptionSimpleParameterOrDescriptionComplexParameter().add(descriptionSimpleParameter7);
        descriptionParametersType.getDescriptionSimpleParameterOrDescriptionComplexParameter().add(descriptionSimpleParameter8);
        descriptionParametersType.getDescriptionSimpleParameterOrDescriptionComplexParameter().add(descriptionSimpleParameter9);
        descriptionParametersType.getDescriptionSimpleParameterOrDescriptionComplexParameter().add(descriptionSimpleParameter10);
        descriptionParametersType.getDescriptionSimpleParameterOrDescriptionComplexParameter().add(descriptionSimpleParameter11);
        descriptionParametersType.getDescriptionSimpleParameterOrDescriptionComplexParameter().add(descriptionSimpleParameter12);
        descriptionParametersType.getDescriptionSimpleParameterOrDescriptionComplexParameter().add(descriptionSimpleParameter13);

        serviceType.setPaymentParameters(descriptionParametersType);

        ServiceType.CurrenciesServices currenciesServices = serviceTypeObjectFactory.createServiceTypeCurrenciesServices();
        currenciesServices.getCurrencyCode().add(CurrencyCodeType.RUR);
        serviceType.setCurrenciesServices(currenciesServices);

        //generated.ru.mos.rnip.xsd.catalog._2_1.ObjectFactory payeeObjectFactory = new ObjectFactory();
        generated.ru.mos.rnip.xsd.catalog._2_1.ServiceType.Payee payee = new ServiceType.Payee();
        payee.setInn(getMacroPart(contragent, "INN"));
        payee.setKpp(getMacroPart(contragent, "KPP"));
        payee.setOgrn(getMacroPart(contragent, "OGRN"));
        payee.setOktmo(getMacroPart(contragent, "OKTMO"));
        payee.setName(getMacroPart(contragent, "CONTRAGENT_NAME"));

        generated.ru.mos.rnip.xsd.common._2_1.ObjectFactory orgAccountObjectFactory = new generated.ru.mos.rnip.xsd.common._2_1.ObjectFactory();
        OrgAccount orgAccount = orgAccountObjectFactory.createOrgAccount();
        orgAccount.setAccountNumber(getMacroPart(contragent, "FINANCE_ACCOUNT"));
        BankType bankType = orgAccountObjectFactory.createBankType();
        bankType.setBik(getMacroPart(contragent, "BIK"));
        bankType.setCorrespondentBankAccount(getMacroPart(contragent, "KORR_FINANCE_ACCOUNT"));
        bankType.setName(getMacroPart(contragent, "FINANCE_PROVIDER"));
        orgAccount.setBank(bankType);
        payee.setOrgAccount(orgAccount);
        serviceType.setPayee(payee);
        serviceType.setPaymentKind(BigInteger.valueOf(1L));

        generated.ru.mos.rnip.xsd.common._2_1.ObjectFactory moneyObjectFactory = new generated.ru.mos.rnip.xsd.common._2_1.ObjectFactory();
        Money minMoney = moneyObjectFactory.createMoney();
        minMoney.setCurrency(CurrencyCodeType.RUR);
        minMoney.setExponent(BigInteger.valueOf(2L));
        minMoney.setValue(1L);
        serviceType.setMinAmount(minMoney);
        Money maxMoney = moneyObjectFactory.createMoney();
        maxMoney.setCurrency(CurrencyCodeType.RUR);
        maxMoney.setExponent(BigInteger.valueOf(2L));
        maxMoney.setValue(2147483647L);
        serviceType.setMaxAmount(maxMoney);

        CommissionsType commissionsType = moneyObjectFactory.createCommissionsType();
        final CommissionType commissionType = moneyObjectFactory.createCommissionType();
        commissionType.setKind("4");
        commissionType.setPercent(BigDecimal.valueOf(Double.parseDouble(getMacroPart(contragent, "COMISSION_PERCENTS"))));
        commissionType.setMinValue(minMoney);
        commissionType.setMaxValue(maxMoney);
        commissionsType.getCommission().add(commissionType);
        serviceType.setCommissions(commissionsType);

        serviceCatalogType.getService().add(serviceType);

        try {
            SendRequestResponse response = port21.sendRequest(sendRequestRequest);
            hasError.set(null);
            return response;
        } catch (Exception e) {
            logger.error("Error execute request for create/modify catalog in RNIP v2.0: ", e);
            hasError.set(e.getMessage());
        }
        return null;
    }

    private void InitRNIP21Service() throws MalformedURLException {
        if (port21 == null) {
            service21 = new SMEVMessageExchangeService();
            port21 = service21.getSMEVMessageExchangeEndpoint();
            bindingProvider21 = (BindingProvider) port21;
            URL endpoint = new URL(getRNIPUrl());
            setEndpointAddress(bindingProvider21, endpoint.toString());
        }
    }

    @Override
    public String checkError(Object response) {
        if (response != null && response instanceof List) {
            return null; //возвращаем ОК для вызовов где параметром передается List, т.к. для элементов проверка уже пройдена
        }
        return hasError.get();
    }

    protected void forceRunGetResponse() {
        List<RnipMessage> messages = RnipDAOService.getInstance().getRnipMessages();
        for (RnipMessage rnipMessage : messages) {
            try {
                processRnipMessage(rnipMessage);
            } catch (Exception e) {
                logger.error("Error in processing rnip message async", e);
            }
        }
    }

    private void processRnipMessage(RnipMessage rnipMessage) throws Exception {
        InitRNIP21Service();
        String alias = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_ALIAS);
        String pass = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_PASSWORD);
        final RNIPSecuritySOAPHandlerV21 pfrSecuritySOAPHandler = new RNIPSecuritySOAPHandlerV21(alias, pass, getPacketLogger(rnipMessage.getContragent()));
        final List<Handler> handlerChain = new ArrayList<Handler>();
        handlerChain.add(pfrSecuritySOAPHandler);
        bindingProvider21.getBinding().setHandlerChain(handlerChain);

        generated.ru.gov.smev.artefacts.x.services.message_exchange.types._1.ObjectFactory requestObjectFactory =
                new generated.ru.gov.smev.artefacts.x.services.message_exchange.types._1.ObjectFactory();
        GetResponseRequest getResponseRequest = requestObjectFactory.createGetResponseRequest();

        generated.ru.gov.smev.artefacts.x.services.message_exchange.types.basic._1.ObjectFactory messageExchangeObjectFactory = new ObjectFactory();
        XMLDSigSignatureType callerInformationSystemSignature = messageExchangeObjectFactory.createXMLDSigSignatureType();
        getResponseRequest.setCallerInformationSystemSignature(callerInformationSystemSignature);

        GetResponseRequest.OriginalMessageID originalMessageID = requestObjectFactory.createGetResponseRequestOriginalMessageID();
        originalMessageID.setId(RNIPSecuritySOAPHandlerV21.SIGN_ID);
        originalMessageID.setValue(rnipMessage.getRequestId());
        getResponseRequest.setOriginalMessageID(originalMessageID);
        GetResponseRequest.Sender sender = requestObjectFactory.createGetResponseRequestSender();
        sender.setMnemonic(getMacroPart(rnipMessage.getContragent(), "CONTRAGENT_ID"));
        getResponseRequest.setSender(sender);

        GetResponseResponse response = null;
        try {
            response = port21.getResponse(getResponseRequest);
            GetResponseResponse.ResponseMessage responseMessage = response.getResponseMessage();
            Response internalResponse = responseMessage.getResponse();
            SenderProvidedResponseData senderProvidedResponseData = internalResponse.getSenderProvidedResponseData();
            MessagePrimaryContent messagePrimaryContent = senderProvidedResponseData.getMessagePrimaryContent();
            ImportCatalogResponse importCatalogResponse = messagePrimaryContent.getImportCatalogResponse();
            SingleImportProtocolType importProtocolType = importCatalogResponse.getImportProtocol();
            String responseMessageToSave = importProtocolType.getCode() + " " + importProtocolType.getDescription();
            RnipDAOService.getInstance().saveAsProcessed(rnipMessage, responseMessageToSave);
        } catch (Exception e) {
            logger.error("Error in request to rnip 2.1", e);
            return;
        }
    }

}
