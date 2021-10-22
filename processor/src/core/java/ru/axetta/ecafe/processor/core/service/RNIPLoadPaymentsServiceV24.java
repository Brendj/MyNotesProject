/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import generated.ru.gov.smev.artefacts.x.services.message_exchange._1.*;
import generated.ru.gov.smev.artefacts.x.services.message_exchange.types._1.*;
import generated.ru.gov.smev.artefacts.x.services.message_exchange.types.basic._1.AckTargetMessage;
import generated.ru.gov.smev.artefacts.x.services.message_exchange.types.basic._1.XMLDSigSignatureType;
import generated.ru.mos.rnip.xsd.catalog._2_1.*;
import generated.ru.mos.rnip.xsd.common._2_1.*;
import generated.ru.mos.rnip.xsd.searchconditions._2_1.ExportPaymentsKindType;
import generated.ru.mos.rnip.xsd.searchconditions._2_1.PaymentsExportConditions;
import generated.ru.mos.rnip.xsd.searchconditions._2_1.TimeConditionsType;
import generated.ru.mos.rnip.xsd.services.export_payments._2_1.ExportPaymentsRequest;
import generated.ru.mos.rnip.xsd.services.import_catalog._2_1.ImportCatalogRequest;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.RnipMessage;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Component("RNIPLoadPaymentsServiceV24")
@Scope("singleton")
public class RNIPLoadPaymentsServiceV24 extends RNIPLoadPaymentsServiceV22 {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RNIPLoadPaymentsServiceV21.class);
    private static final org.slf4j.Logger loggerSendAck = LoggerFactory.getLogger(RNIPLoadPaymentsServiceV21.class);
    private static final org.slf4j.Logger loggerGetResponse = LoggerFactory.getLogger(RNIPLoadPaymentsServiceV21.class);
    private static SMEVMessageExchangeService_24 service24;
    private static SMEVMessageExchangePortType_24 port24;
    private static BindingProvider bindingProvider21;
    private static final Object sync = new Object();

    @Resource
    RNIPGetPaymentsServiceV21 getPaymentsService;

    public static final int PAGING_VALUE = 100;
    public static final String SUCCESS_CODE = "0 -";
    public static final String NODATA_CODE = "NO_DATA -";
    public static final String EMPTY_PACKET = "Empty packet, rerequest sent";
    public static final String ALREADY_PROCESSED = "уже обрабатывался";

    private final static ThreadLocal<String> hasError = new ThreadLocal<String>(){
        @Override protected String initialValue() { return null; }
    };



    @Override
    protected RNIPSecuritySOAPHandlerV24 getSecurityHandler(String alias, String pass, Contragent contragent) {
        return new RNIPSecuritySOAPHandlerV24(alias, pass, getPacketLogger(contragent));
    }

    @Override
    protected void setProperCatalogRequestSection(int requestType, ImportCatalogRequest importCatalogRequest,
            ServiceCatalogType serviceCatalogType) {
        importCatalogRequest.setServiceCatalog(serviceCatalogType);
    }

    @Override
    public String getRNIPUrl() {
        return RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_URL_V22);
    }

    @Override
    public SendRequestResponse executeLoadPaymentsV21(int requestType, Contragent contragent, Date updateDate, Date startDate,
            Date endDate, int paging) throws Exception {
        InitRNIP24Service(contragent);

        SendRequestRequest sendRequestRequest = getMessageHeaderV21(contragent);

        generated.ru.mos.rnip.xsd.services.export_payments._2_1.ObjectFactory exportPaymentObjectFactory =
                new generated.ru.mos.rnip.xsd.services.export_payments._2_1.ObjectFactory();

        ExportPaymentsRequest exportPaymentsRequest = exportPaymentObjectFactory.createExportPaymentsRequest();
        exportPaymentsRequest.setId(String.format("N_%s", UUID.randomUUID().toString()));
        exportPaymentsRequest.setTimestamp(RNIPSecuritySOAPHandler.toXmlGregorianCalendar(new Date()));
        exportPaymentsRequest.setSenderIdentifier(getMacroPart(contragent, "CONTRAGENT_ID"));

        generated.ru.mos.rnip.xsd.common._2_1.ObjectFactory commonObjectFactory = new generated.ru.mos.rnip.xsd.common._2_1.ObjectFactory();
        PagingType pagingObject = commonObjectFactory.createPagingType();
        pagingObject.setPageLength(BigInteger.valueOf(PAGING_VALUE));
        pagingObject.setPageNumber(BigInteger.valueOf(paging));
        exportPaymentsRequest.setPaging(pagingObject);

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
            response = port24.sendRequest(sendRequestRequest);
            hasError.set(null);
        } catch (Exception e) {
            logger.error("Error in request to rnip 2.1", e);
            hasError.set(e.getMessage());
        }
        return response;
    }

    @Override
    public SendRequestResponse executeModifyCatalogV21(int requestType, Contragent contragent, Date updateDate, Date startDate, Date endDate) throws Exception {
        InitRNIP24Service(contragent);

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
        setProperCatalogRequestSection(requestType, importCatalogRequest, serviceCatalogType);
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
        serviceType.setName("Пополнение лицевого счета карты прохода и питания ребенка");
        serviceType.setRevisionDate(RNIPSecuritySOAPHandler.toXmlGregorianCalendar(
                RuntimeContext.getInstance().getDefaultLocalCalendar(null).getTime()));
        ServiceCategoryType serviceCategoryType = serviceTypeObjectFactory.createServiceCategoryType();
        serviceCategoryType.setCode("PIP0000019");
        serviceCategoryType.setName("Проход и питание");
        serviceType.setServiceCategory(serviceCategoryType);

        DescriptionParametersType descriptionParametersType = serviceTypeObjectFactory.createDescriptionParametersType();

        DescriptionSimpleParameter descriptionSimpleParameter1 = serviceTypeObjectFactory.createDescriptionSimpleParameter();
        descriptionSimpleParameter1.setForPayment(true);
        descriptionSimpleParameter1.setForSearch(false);
        descriptionSimpleParameter1.setLabel("Код услуги");
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
        descriptionSimpleParameter2.setRegexp("^\\d{1,15}$");

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
        descriptionSimpleParameter4.setReadonly(true);
        descriptionSimpleParameter4.setRequired(true);
        descriptionSimpleParameter4.setVisible(false);
        descriptionSimpleParameter4.setDefaultValue("00");

        DescriptionSimpleParameter descriptionSimpleParameter5 = serviceTypeObjectFactory.createDescriptionSimpleParameter();
        descriptionSimpleParameter5.setForPayment(true);
        descriptionSimpleParameter5.setForSearch(false);
        descriptionSimpleParameter5.setLabel("Тип платежа");
        descriptionSimpleParameter5.setName("PAYMENTTYPE");
        descriptionSimpleParameter5.setReadonly(true);
        descriptionSimpleParameter5.setRequired(true);
        descriptionSimpleParameter5.setVisible(false);
        descriptionSimpleParameter5.setDefaultValue("0");

        DescriptionSimpleParameter descriptionSimpleParameter6 = serviceTypeObjectFactory.createDescriptionSimpleParameter();
        descriptionSimpleParameter6.setForPayment(true);
        descriptionSimpleParameter6.setForSearch(false);
        descriptionSimpleParameter6.setLabel("Основание платежа");
        descriptionSimpleParameter6.setName("PAYTREASON");
        descriptionSimpleParameter6.setReadonly(true);
        descriptionSimpleParameter6.setRequired(true);
        descriptionSimpleParameter6.setVisible(false);
        descriptionSimpleParameter6.setDefaultValue("0");

        DescriptionSimpleParameter descriptionSimpleParameter7 = serviceTypeObjectFactory.createDescriptionSimpleParameter();
        descriptionSimpleParameter7.setForPayment(true);
        descriptionSimpleParameter7.setForSearch(false);
        descriptionSimpleParameter7.setLabel("Налоговый период");
        descriptionSimpleParameter7.setName("TAXPERIOD");
        descriptionSimpleParameter7.setReadonly(true);
        descriptionSimpleParameter7.setRequired(true);
        descriptionSimpleParameter7.setVisible(false);
        descriptionSimpleParameter7.setDefaultValue("0");

        DescriptionSimpleParameter descriptionSimpleParameter8 = serviceTypeObjectFactory.createDescriptionSimpleParameter();
        descriptionSimpleParameter8.setForPayment(true);
        descriptionSimpleParameter8.setForSearch(false);
        descriptionSimpleParameter8.setLabel("Показатель номера документа");
        descriptionSimpleParameter8.setName("TAXDOCNUMBER");
        descriptionSimpleParameter8.setReadonly(true);
        descriptionSimpleParameter8.setRequired(true);
        descriptionSimpleParameter8.setVisible(false);
        descriptionSimpleParameter8.setDefaultValue("0");

        DescriptionSimpleParameter descriptionSimpleParameter9 = serviceTypeObjectFactory.createDescriptionSimpleParameter();
        descriptionSimpleParameter9.setForPayment(true);
        descriptionSimpleParameter9.setForSearch(false);
        descriptionSimpleParameter9.setLabel("Показатель даты документа");
        descriptionSimpleParameter9.setName("TAXDOCDATE");
        descriptionSimpleParameter9.setReadonly(true);
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
        descriptionSimpleParameter12.setName("PURPOSE");
        descriptionSimpleParameter12.setReadonly(true);
        descriptionSimpleParameter12.setRequired(true);
        descriptionSimpleParameter12.setVisible(false);
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
            SendRequestResponse response = port24.sendRequest(sendRequestRequest);
            hasError.set(null);
            return response;
        } catch (Exception e) {
            logger.error("Error execute request for create/modify catalog in RNIP v2.0: ", e);
            hasError.set(e.getMessage());
        }
        return null;
    }

    @Override
    protected void sendAckRnipMessage(RnipMessage rnipMessage) throws Exception {
        InitRNIP24Service(rnipMessage.getContragent());

        generated.ru.gov.smev.artefacts.x.services.message_exchange.types._1.ObjectFactory requestObjectFactory =
                new generated.ru.gov.smev.artefacts.x.services.message_exchange.types._1.ObjectFactory();

        AckRequest ackRequest = requestObjectFactory.createAckRequest();

        generated.ru.gov.smev.artefacts.x.services.message_exchange.types.basic._1.ObjectFactory messageExchangeObjectFactory =
                new generated.ru.gov.smev.artefacts.x.services.message_exchange.types.basic._1.ObjectFactory();
        XMLDSigSignatureType callerInformationSystemSignature = messageExchangeObjectFactory.createXMLDSigSignatureType();
        ackRequest.setCallerInformationSystemSignature(callerInformationSystemSignature);

        AckTargetMessage ackTargetMessage = messageExchangeObjectFactory.createAckTargetMessage();
        ackTargetMessage.setId(RNIPSecuritySOAPHandlerV21.SIGN_ID);
        ackTargetMessage.setAccepted(true);
        ackTargetMessage.setValue(rnipMessage.getResponseMessageId());
        ackRequest.setAckTargetMessage(ackTargetMessage);
        AckRequest.Sender sender = requestObjectFactory.createAckRequestSender();
        sender.setMnemonic(getMacroPart(rnipMessage.getContragent(), "CONTRAGENT_ID"));
        ackRequest.setSender(sender);

        try {
            port24.ack(ackRequest);
            RnipDAOService.getInstance().saveAsAckSent(rnipMessage);
        } catch (InvalidContentException e) {
            try {
                if (e.getMessage().contains(ALREADY_PROCESSED)) {
                    RnipDAOService.getInstance().saveAsAckSent(rnipMessage);
                }
            } catch (Exception ee) {
                loggerSendAck.error("Error in process Ack response rnip 2.1: ", ee);
            }
        } catch (Exception e) {
            loggerSendAck.error("Error in request to rnip 2.1", e);
            return;
        }
    }

    @Override
    public void processRnipMessage(RnipMessage rnipMessage) throws Exception {
        InitRNIP24Service(rnipMessage.getContragent());

        generated.ru.gov.smev.artefacts.x.services.message_exchange.types._1.ObjectFactory requestObjectFactory =
                new generated.ru.gov.smev.artefacts.x.services.message_exchange.types._1.ObjectFactory();
        GetResponseRequest getResponseRequest = requestObjectFactory.createGetResponseRequest();

        generated.ru.gov.smev.artefacts.x.services.message_exchange.types.basic._1.ObjectFactory messageExchangeObjectFactory =
                new generated.ru.gov.smev.artefacts.x.services.message_exchange.types.basic._1.ObjectFactory();
        XMLDSigSignatureType callerInformationSystemSignature = messageExchangeObjectFactory.createXMLDSigSignatureType();
        getResponseRequest.setCallerInformationSystemSignature(callerInformationSystemSignature);

        GetResponseRequest.OriginalMessageID originalMessageID = requestObjectFactory.createGetResponseRequestOriginalMessageID();
        originalMessageID.setId(RNIPSecuritySOAPHandlerV21.SIGN_ID);
        originalMessageID.setValue(rnipMessage.getMessageId());
        getResponseRequest.setOriginalMessageID(originalMessageID);
        GetResponseRequest.Sender sender = requestObjectFactory.createGetResponseRequestSender();
        sender.setMnemonic(getMacroPart(rnipMessage.getContragent(), "CONTRAGENT_ID"));
        getResponseRequest.setSender(sender);

        GetResponseResponse response = null;
        String[] responseMessageToSave = {"", ""};
        boolean hasMore = false;
        try {
            response = port24.getResponse(getResponseRequest);
            GetResponseResponse.ResponseMessage responseMessage = response.getResponseMessage();
            if (responseMessage == null) {
                //Если получаем пустой ответ, то повторяем запрос с другим MessageID
                int paging = rnipMessage.getPaging() == null ? 1 : rnipMessage.getPaging();
                receiveContragentPayments(getRequestType(rnipMessage.getEventType()), rnipMessage.getContragent(),
                        rnipMessage.getStartDate(), rnipMessage.getEndDate(), paging);
                loggerGetResponse.info(String.format("Получен пустой ответ на запрос с ид=%s, контрагент %s. Отправлен повторный запрос", rnipMessage.getMessageId(),
                        rnipMessage.getContragent().getContragentName()));
                RnipDAOService.getInstance().saveAsProcessed(rnipMessage, EMPTY_PACKET, null, rnipMessage.getEventType());
                return;
            }
            Response internalResponse = responseMessage.getResponse();
            responseMessageToSave = checkResponseByEventType(internalResponse, rnipMessage);
            if (noErrors(responseMessageToSave[0]) && isPaymentRequest(rnipMessage)) {
                hasMore = getHasMore(internalResponse);
                loggerGetResponse.info(String.format("Разбор новых платежей для контрагента %s..", rnipMessage.getContragent().getContragentName()));

                RNIPPaymentsResponse res = parsePayments(internalResponse);
                info("Получено %s новых платежей для контрагента %s, применение..", res.getPayments().size(), rnipMessage.getContragent().getContragentName());
                boolean isAutoRun = true; //todo тут было условие (startDate == null);
                addPaymentsToDb(res.getPayments(), isAutoRun);

                loggerGetResponse.info(String.format("Все новые платежи для контрагента %s обработаны", rnipMessage.getContragent().getContragentName()));
            }
        } catch (Exception e) {
            responseMessageToSave[0] = "100 - Internal Error";
            loggerGetResponse.error("Error in GetResponseRequest to rnip 2.1", e);
            throw e;
        }
        RnipDAOService.getInstance().saveAsProcessed(rnipMessage, responseMessageToSave[0], responseMessageToSave[1], rnipMessage.getEventType());
        if (hasMore) {
            int paging = rnipMessage.getPaging() == null ? 1 : rnipMessage.getPaging();
            receiveContragentPayments(getRequestType(rnipMessage.getEventType()), rnipMessage.getContragent(),
                    rnipMessage.getStartDate(), rnipMessage.getEndDate(), paging + 1);
        }
    }

    protected SMEVMessageExchangeService_24 getServiceImpl_24() {
        return new SMEVMessageExchangeService_24();
    }

    private void InitRNIP24Service(Contragent contragent) throws MalformedURLException {
        synchronized (sync) {
            if (port24 == null) {
                service24 = getServiceImpl_24();
                port24 = service24.getSMEVMessageExchangeEndpoint();
                bindingProvider21 = (BindingProvider) port24;
                URL endpoint = new URL(getRNIPUrl());
                setEndpointAddress(bindingProvider21, endpoint.toString());
            }
        }
        String alias = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_ALIAS);
        String pass = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_PASSWORD);
        final RNIPSecuritySOAPHandlerV21 pfrSecuritySOAPHandler = getSecurityHandler(alias, pass, contragent);
        final List<Handler> handlerChain = new ArrayList<Handler>();
        handlerChain.add(pfrSecuritySOAPHandler);
        bindingProvider21.getBinding().setHandlerChain(handlerChain);
    }
}
