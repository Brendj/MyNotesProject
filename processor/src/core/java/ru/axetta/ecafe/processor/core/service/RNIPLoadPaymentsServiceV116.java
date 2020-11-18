/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import generated.smev.gisgmp.client.ru.gosuslugi.smev.rev120315.*;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp._02000000.smevgisgmpservice.SmevGISGMPService;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp._02000000.smevgisgmpservice.SmevGISGMPService_Service;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.catalog.DescriptionParametersType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.catalog.DescriptionSimpleParameter;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.catalog.ServiceCatalogType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.catalog.ServiceCategoryType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.common.*;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.errinfo.ResultInfo;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.exportpaymentsresponse.ExportPaymentsResponseType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.message.RequestMessageType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.message.ResponseMessageType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.messagedata.ImportCatalogRequest;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.organization.AccountCatalogType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.organization.AccountsType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.organization.BankType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.organization.PayeeType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.paymentinfo.PaymentType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.DataRequest;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.ticket.TicketType;
import ru.CryptoPro.JCP.tools.Array;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Holder;
import javax.xml.ws.handler.Handler;
import java.io.File;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 07.10.15
 * Time: 14:26
 * To change this template use File | Settings | File Templates.
 */
@Component("RNIPLoadPaymentsServiceV116")
@Scope("singleton")
public class RNIPLoadPaymentsServiceV116 extends RNIPLoadPaymentsService {

    private static SmevGISGMPService_Service service116;
    private static SmevGISGMPService port116;
    private static BindingProvider bindingProvider116;
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RNIPLoadPaymentsServiceV116.class);
    //todo добавить ограничение на MAX_REREQUESTS
    private static int MAX_REREQUESTS = 3; //максимальное число повторных запросов, если получено ReRequest = true
    protected HashMap<Contragent, Integer> requests;
    private Integer reqs;
    private final static ThreadLocal<String> messageId = new ThreadLocal<String>() {
        @Override protected String initialValue() { return UUID.randomUUID().toString(); }
    };

    @Override
    public Object executeRequest(int requestType, Contragent contragent, Date updateDate, Date startDate, Date endDate, int paging) throws Exception {
        return executeRequest116(requestType, contragent, updateDate, startDate, endDate, paging);
    }

    public Object executeRequest116(int requestType, Contragent contragent, Date updateDate, Date startDate, Date endDate, int paging) throws Exception {
        switch(requestType) {
            case REQUEST_MODIFY_CATALOG:
            case REQUEST_CREATE_CATALOG:
                return executeModifyCatalog(requestType, contragent, updateDate, startDate, endDate);
            case REQUEST_LOAD_PAYMENTS:
            case REQUEST_LOAD_PAYMENTS_MODIFIED:
                requests = new HashMap<Contragent, Integer>();
                int attempt = 1;
                String uuid = UUID.randomUUID().toString();

                List<MessageDataType> result = new ArrayList<MessageDataType>();
                MessageDataType messageDataType = executeLoadPayments(requestType, contragent, updateDate, startDate, endDate, attempt, uuid);
                result.add(messageDataType);
                String error = checkError(messageDataType);
                if (error != null) throw new Exception(String.format("RNIP v 1.16 check error: %s", error));
                while (hasMore(messageDataType)) {
                    attempt++;
                    messageDataType = executeLoadPayments(requestType, contragent, updateDate, startDate, endDate, attempt, uuid);
                    error = checkError(messageDataType);
                    if (error != null) throw new Exception(String.format("RNIP v 1.16 check error: %s", error));
                    result.add(messageDataType);
                }
                return result;
        }
        return null;
    }

    protected IRNIPMessageToLog getPacketLogger(final Contragent contragent) {
        return new IRNIPMessageToLog() {
            @Override
            public void LogPacket(String message, int message_type) throws Exception {
                String filePath;
                if (message_type == IRNIPMessageToLog.MESSAGE_OUT) {
                    filePath = RNIP_OUTPUT_FILE;
                }
                else {
                    filePath = RNIP_INPUT_FILE;
                }
                logRequestToFile(contragent, filePath, message);
            }
        };
    }

    protected void logRequestToFile(Contragent contragent, String filePath, String message) throws Exception {
        long timestamp = System.currentTimeMillis();
        File dir = new File(RNIP_DIR);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        String paymentDirPath = RNIP_DIR + contragent.getContragentName() + "_"+ CalendarUtils.formatToDateShortUnderscoreFormat(new Date()) + "/";
        paymentDirPath = paymentDirPath.replace(" ","_");
        paymentDirPath = paymentDirPath.replace("\"","_");
        File paymentDir = new File(paymentDirPath);
        if(!paymentDir.exists()) {
            paymentDir.mkdirs();
        }
        Array.writeFile(paymentDirPath + filePath + "_" + CalendarUtils.formatTimeUnderscoreExtendedToString(timestamp)
                + ".xml", message.getBytes("UTF-8"));
    }

    public MessageDataType executeLoadPayments(int requestType, Contragent contragent, Date updateDate, Date startDate,
            Date endDate, int attempt, String uuid) throws Exception {
        InitRNIP116Service();

        logger.info(String.format("Запрос на получение платежей их РНИП. Запрос № %s в серии", attempt));
        //logger.info("Запрос на получение платежей из РНИП");

        String alias = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_ALIAS);
        String pass = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_PASSWORD);

        final RNIPSecuritySOAPHandler rnipSecuritySOAPHandler = new RNIPSecuritySOAPHandler(alias, pass, getPacketLogger(contragent));
        final List<Handler> handlerChain = new ArrayList<Handler>();
        handlerChain.add(rnipSecuritySOAPHandler);
        bindingProvider116.getBinding().setHandlerChain(handlerChain);

        final generated.smev.gisgmp.client.ru.gosuslugi.smev.rev120315.ObjectFactory messageObjectFactory = new generated.smev.gisgmp.client.ru.gosuslugi.smev.rev120315.ObjectFactory();

        final MessageType messageType = messageObjectFactory.createMessageType();
        final MessageDataType messageDataType = messageObjectFactory.createMessageDataType();
        final AppDataType appDataType = messageObjectFactory.createAppDataType();

        final OrgExternalType sender = messageObjectFactory.createOrgExternalType();
        sender.setCode(RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_SENDER_CODE));
        sender.setName(RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_SENDER_NAME));
        messageType.setSender(sender);

        final OrgExternalType recipient = messageObjectFactory.createOrgExternalType();
        recipient.setCode("105805771");
        recipient.setName("ИС_РНиП");
        messageType.setRecipient(recipient);

        messageType.setServiceName("105805771");
        messageType.setTypeCode(TypeCodeType.GFNC);
        messageType.setStatus(StatusType.REQUEST);
        messageType.setDate(getXMLGregorianDate(new Date(System.currentTimeMillis())));
        messageType.setExchangeType("6");

        final generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.message.ObjectFactory mesOf =
            new generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.message.ObjectFactory();

        final RequestMessageType requestMessageType = mesOf.createRequestMessageType();

        //requestMessageType.setId(String.format("N_%s", UUID.randomUUID()));
        requestMessageType.setId(String.format("N_%s", uuid));
        requestMessageType.setSenderIdentifier(getMacroPart(contragent, "CONTRAGENT_ID"));
        requestMessageType.setTimestamp(RNIPSecuritySOAPHandler.toXmlGregorianCalendar(new Date()));

        final generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.messagedata.ObjectFactory mesDataOf =
            new generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.messagedata.ObjectFactory();

        final generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.ObjectFactory dataOf =
            new generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.ObjectFactory();

        final DataRequest dataRequest = dataOf.createDataRequest();
        switch (requestType) {
            case REQUEST_LOAD_PAYMENTS_MODIFIED:
                dataRequest.setKind("PAYMENTMODIFIED");
                break;
            case REQUEST_LOAD_PAYMENTS:
            default:
                dataRequest.setKind("PAYMENT");
                break;
        }
        dataRequest.setId("I_52d85fa5-18ae-11e5-b50b-bcaec5d977ce");
        final DataRequest.Filter filter = dataOf.createDataRequestFilter();
        final DataRequest.Filter.Conditions conditions = dataOf.createDataRequestFilterConditions();

        DataRequest.Filter.Conditions.Timeslot timeslot = new DataRequest.Filter.Conditions.Timeslot();

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
        timeslot.setStartDate(RNIPSecuritySOAPHandler.toXmlGregorianCalendar(sDate));
        timeslot.setEndDate(RNIPSecuritySOAPHandler.toXmlGregorianCalendar(eDate));

        conditions.setTimeslot(timeslot);

        filter.setConditions(conditions);
        dataRequest.setFilter(filter);

        DataRequest.Paging paging = dataOf.createDataRequestPaging();
        paging.setPageLength(100);
        paging.setPageNumber(attempt);
        dataRequest.setPaging(paging);

        requestMessageType.setRequestMessageData(mesDataOf.createExportRequest(dataRequest));

        appDataType.getAny().add(requestMessageType);
        messageDataType.setAppData(appDataType);

        final Holder<MessageType> messageTypeHolder = new Holder<MessageType>(messageType);
        final Holder<MessageDataType> messageDataTypeHolder = new Holder<MessageDataType>(messageDataType);

        try {
            port116.gisgmpTransferMsg(messageTypeHolder, messageDataTypeHolder);
            return messageDataTypeHolder.value;
        } catch (Exception e) {
            logger.error(String.format("Error call RNIP service: %s", e.getMessage()));
            return null;
        }
    }

    @Override
    protected String getRNIPUrl() {
        return RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_URL_V116);
    }

    @Override
    public RNIPLoadPaymentsService.RNIPPaymentsResponse parsePayments(Object response) throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(false);

        //DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        InputSource is = new InputSource();
        JAXBContext jaxbContext = JAXBContext.newInstance(PaymentType.class);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();

        List<MessageDataType> responseList = (List) response;
        Date rnipDate = null;

        for (MessageDataType responseItem : responseList) {
            Object o = responseItem.getAppData().getAny().get(0);
            ResponseMessageType responseMessageType = (ResponseMessageType) ((JAXBElement) o).getValue();
            ExportPaymentsResponseType exportPaymentsResponseType = (ExportPaymentsResponseType) responseMessageType.getResponseMessageData().getValue();
            List<ExportPaymentsResponseType.Payments.PaymentInfo> piList = exportPaymentsResponseType.getPayments().getPaymentInfo();

            if (rnipDate == null) rnipDate = getRnipDate(responseMessageType.getTimestamp()); //берем дату рнип из первого запроса в серии, если их несколько по признаку hasMore
            for (ExportPaymentsResponseType.Payments.PaymentInfo pi : piList) {
                String paymentInfoStr = new String(pi.getPaymentData(), "utf8").replaceAll("(\\r|\\n)", "");

                Map map = null;
                try {
                    StringReader reader = new StringReader(paymentInfoStr);
                    PaymentType payment = (PaymentType) unmarshaller.unmarshal(reader);
                    map = parsePayment(payment); //парсинг по формату 1.16
                } catch (Exception e) {
                    logger.error(String.format("Cant parse payment %s", paymentInfoStr));
                }
                if (map != null && map.size() > 0) {
                    result.add(map);
                } else {
                    is.setCharacterStream(new StringReader(paymentInfoStr));
                    Document doc = builderFactory.newDocumentBuilder().parse(is);
                    result.add(parsePayment(doc)); //парсинг по формату 1.15
                }

            }
        }

        RNIPLoadPaymentsService.RNIPPaymentsResponse res = new RNIPLoadPaymentsService.RNIPPaymentsResponse(result, rnipDate);
        return res;
    }

    @Override
    public long getContragentByRNIPCode(String contragentKey, List<Contragent> contragents) {
        contragentKey = contragentKey.substring(4, 10).trim();
        boolean v15 = false;
        if (contragentKey.startsWith("A") || contragentKey.startsWith("А")) {
            v15 = true; //платеж по формату 1.15, будем сравнивать последние 4 символа
        }
        for (Contragent c : contragents) {
            String cc = getRNIPIdFromRemarks (c.getRemarks());
            if (!v15 && cc != null && cc.equals(contragentKey)) {
                return c.getIdOfContragent();
            } else if (v15 && cc != null && cc.substring(2).equals(contragentKey.substring(2))) {
                return c.getIdOfContragent();
            }
        }
        return 0L;
    }

    @Override
    public Map<String, String> parsePayment(Document doc) {
        Map<String, String> vals = new HashMap<String, String>();
        parseNode(doc.getChildNodes(), vals);
        return vals;
    }

    public Map<String, String> parsePayment(PaymentType payment) {
        Map<String, String> vals = new HashMap<String, String>();
        vals.put(SYSTEM_IDENTIFIER_KEY, payment.getPaymentIdentificationData().getSystemIdentifier());
        vals.put(AMOUNT_KEY, payment.getAmount().toString());
        Date rnipDate = getRnipDate(payment.getPaymentDate());
        vals.put(PAYMENT_DATE_KEY, new SimpleDateFormat(RNIP_DATE_TIME_FORMAT).format(rnipDate));
        vals.put(BIK_KEY, payment.getPaymentIdentificationData().getBank().getBIK());
        vals.put(CHANGE_STATUS_KEY, payment.getChangeStatus().getMeaning());
        for (AdditionalDataType dataType : payment.getAdditionalData()) {
           if (dataType.getName().equals(PAYMENT_TO_KEY)) {
               vals.put(PAYMENT_TO_KEY, dataType.getValue());
           }
           if (dataType.getName().equals(SRV_CODE_KEY)) {
               vals.put(SRV_CODE_KEY, dataType.getValue());
           }
        }
        return vals;
    }

    //todo дописать заполнение всех разделов пакета и переписать метод getMacroPart
    public MessageDataType executeModifyCatalog(int requestType, Contragent contragent, Date updateDate, Date startDate, Date endDate) throws Exception {
        InitRNIP116Service();

        String alias = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_ALIAS);
        String pass = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_PASSWORD);
        final RNIPSecuritySOAPHandler pfrSecuritySOAPHandler = new RNIPSecuritySOAPHandler(alias, pass, getPacketLogger(contragent));
        final List<Handler> handlerChain = new ArrayList<Handler>();
        handlerChain.add(pfrSecuritySOAPHandler);
        bindingProvider116.getBinding().setHandlerChain(handlerChain);

        final generated.smev.gisgmp.client.ru.gosuslugi.smev.rev120315.ObjectFactory messageObjectFactory = new generated.smev.gisgmp.client.ru.gosuslugi.smev.rev120315.ObjectFactory();

        final MessageType messageType = messageObjectFactory.createMessageType();
        final MessageDataType messageDataType = messageObjectFactory.createMessageDataType();
        final AppDataType appDataType = messageObjectFactory.createAppDataType();

        final OrgExternalType sender = messageObjectFactory.createOrgExternalType();
        sender.setCode(RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_SENDER_CODE));
        sender.setName(RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_SENDER_NAME));
        messageType.setSender(sender);

        final OrgExternalType recipient = messageObjectFactory.createOrgExternalType();
        recipient.setCode("000009500");
        recipient.setName("RNIP");
        messageType.setRecipient(recipient);

        messageType.setServiceName("0");
        messageType.setTypeCode(TypeCodeType.GSRV);
        messageType.setStatus(StatusType.REQUEST);
        messageType.setDate(getXMLGregorianDate(new Date(System.currentTimeMillis())));
        messageType.setExchangeType("6");

        final generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.message.ObjectFactory mesOf =
                new generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.message.ObjectFactory();
        final RequestMessageType requestMessageType = mesOf.createRequestMessageType();
        //requestMessageType.setId("N_4a0d84ca-1fc6-11e5-99c3-bcaec5d977ce");
        requestMessageType.setId(String.format("I_%s", UUID.randomUUID()));
        requestMessageType.setSenderIdentifier(getMacroPart(contragent, "CONTRAGENT_ID"));
        requestMessageType.setTimestamp(RNIPSecuritySOAPHandler.toXmlGregorianCalendar(new Date()));

        final generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.messagedata.ObjectFactory mesDataOf =
                new generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.messagedata.ObjectFactory();

        final generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.catalog.ObjectFactory catOf =
                new generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.catalog.ObjectFactory();
        final ServiceCatalogType serviceCatalogType = catOf.createServiceCatalogType();
        serviceCatalogType.setId("I_52d85fa5-18ae-11e5-b50b-bcaec5d977ce");
        serviceCatalogType.setName("Изменение");
        serviceCatalogType.setRevisionDate(RNIPSecuritySOAPHandler.toXmlGregorianCalendar(
                RuntimeContext.getInstance().getDefaultLocalCalendar(null).getTime()));

        final generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.catalog.ServiceType serviceType = catOf.createServiceType();
        serviceType.setCode("AAAA" + getMacroPart(contragent, "CONTRAGENT_ID") + "0000000001");
        serviceType.setDesc("Услуги по оплате питания учеников в образовательных учреждениях");
        serviceType.setIsActive(true);
        serviceType.setName("Услуга питания в ОУ");
        serviceType.setRevisionDate(RNIPSecuritySOAPHandler.toXmlGregorianCalendar(
                RuntimeContext.getInstance().getDefaultLocalCalendar(null).getTime()));

        final ServiceCategoryType serviceCategoryType = catOf.createServiceCategoryType();
        serviceCategoryType.setCode("PIP0000019");
        serviceCategoryType.setName("Недоступно для оплаты");
        serviceType.setServiceCategory(serviceCategoryType);

        final DescriptionParametersType descriptionParametersType = catOf.createDescriptionParametersType();

        DescriptionSimpleParameter descriptionSimpleParameter1 = catOf.createDescriptionSimpleParameter();
        descriptionSimpleParameter1.setForPayment(true);
        descriptionSimpleParameter1.setForSearch(false);
        descriptionSimpleParameter1.setLabel("Код услуги");
        descriptionSimpleParameter1.setName("SRV_CODE");
        descriptionSimpleParameter1.setReadonly(true);
        descriptionSimpleParameter1.setRequired(true);
        descriptionSimpleParameter1.setVisible(false);
        descriptionSimpleParameter1.setDefaultValue("AAAA" + getMacroPart(contragent, "CONTRAGENT_ID") + "0000000001");

        DescriptionSimpleParameter descriptionSimpleParameter2 = catOf.createDescriptionSimpleParameter();
        descriptionSimpleParameter2.setForPayment(true);
        descriptionSimpleParameter2.setForSearch(true);
        descriptionSimpleParameter2.setLabel("Номер договора");
        descriptionSimpleParameter2.setName("PAYMENT_TO");
        descriptionSimpleParameter2.setIsId(BigInteger.valueOf(1));
        descriptionSimpleParameter2.setReadonly(false);
        descriptionSimpleParameter2.setRequired(true);
        descriptionSimpleParameter2.setVisible(true);
        descriptionSimpleParameter2.setRegexp("^\\d{1,15}$");

        DescriptionSimpleParameter descriptionSimpleParameter3 = catOf.createDescriptionSimpleParameter();
        descriptionSimpleParameter3.setForPayment(true);
        descriptionSimpleParameter3.setForSearch(false);
        descriptionSimpleParameter3.setLabel("Идентификатор поставщика");
        descriptionSimpleParameter3.setName("PAYMENT");
        descriptionSimpleParameter3.setReadonly(true);
        descriptionSimpleParameter3.setRequired(true);
        descriptionSimpleParameter3.setVisible(false);
        descriptionSimpleParameter3.setDefaultValue(getMacroPart(contragent, "CONTRAGENT_BMID"));

        DescriptionSimpleParameter descriptionSimpleParameter4 = catOf.createDescriptionSimpleParameter();
        descriptionSimpleParameter4.setForPayment(true);
        descriptionSimpleParameter4.setForSearch(false);
        descriptionSimpleParameter4.setLabel("Статус плательщика");
        descriptionSimpleParameter4.setName("STATUS");
        descriptionSimpleParameter4.setReadonly(true);
        descriptionSimpleParameter4.setRequired(true);
        descriptionSimpleParameter4.setVisible(false);
        descriptionSimpleParameter4.setDefaultValue("00");

        DescriptionSimpleParameter descriptionSimpleParameter5 = catOf.createDescriptionSimpleParameter();
        descriptionSimpleParameter5.setForPayment(true);
        descriptionSimpleParameter5.setForSearch(false);
        descriptionSimpleParameter5.setLabel("Тип платежа");
        descriptionSimpleParameter5.setName("PAYMENTTYPE");
        descriptionSimpleParameter5.setReadonly(true);
        descriptionSimpleParameter5.setRequired(true);
        descriptionSimpleParameter5.setVisible(false);
        descriptionSimpleParameter5.setDefaultValue("0");

        DescriptionSimpleParameter descriptionSimpleParameter6 = catOf.createDescriptionSimpleParameter();
        descriptionSimpleParameter6.setForPayment(true);
        descriptionSimpleParameter6.setForSearch(false);
        descriptionSimpleParameter6.setLabel("Основание платежа");
        descriptionSimpleParameter6.setName("PURPOSE");
        descriptionSimpleParameter6.setReadonly(true);
        descriptionSimpleParameter6.setRequired(true);
        descriptionSimpleParameter6.setVisible(false);
        descriptionSimpleParameter6.setDefaultValue("0");

        DescriptionSimpleParameter descriptionSimpleParameter7 = catOf.createDescriptionSimpleParameter();
        descriptionSimpleParameter7.setForPayment(true);
        descriptionSimpleParameter7.setForSearch(false);
        descriptionSimpleParameter7.setLabel("Налоговый период");
        descriptionSimpleParameter7.setName("TAXPERIOD");
        descriptionSimpleParameter7.setReadonly(true);
        descriptionSimpleParameter7.setRequired(true);
        descriptionSimpleParameter7.setVisible(false);
        descriptionSimpleParameter7.setDefaultValue("0");

        DescriptionSimpleParameter descriptionSimpleParameter8 = catOf.createDescriptionSimpleParameter();
        descriptionSimpleParameter8.setForPayment(true);
        descriptionSimpleParameter8.setForSearch(false);
        descriptionSimpleParameter8.setLabel("Показатель номера документа");
        descriptionSimpleParameter8.setName("TAXDOCNUMBER");
        descriptionSimpleParameter8.setReadonly(true);
        descriptionSimpleParameter8.setRequired(true);
        descriptionSimpleParameter8.setVisible(false);
        descriptionSimpleParameter8.setDefaultValue("0");

        DescriptionSimpleParameter descriptionSimpleParameter9 = catOf.createDescriptionSimpleParameter();
        descriptionSimpleParameter9.setForPayment(true);
        descriptionSimpleParameter9.setForSearch(false);
        descriptionSimpleParameter9.setLabel("Показатель даты документа");
        descriptionSimpleParameter9.setName("TAXDOCDATE");
        descriptionSimpleParameter9.setReadonly(true);
        descriptionSimpleParameter9.setRequired(true);
        descriptionSimpleParameter9.setVisible(false);
        descriptionSimpleParameter9.setDefaultValue("0");

        DescriptionSimpleParameter descriptionSimpleParameter10 = catOf.createDescriptionSimpleParameter();
        descriptionSimpleParameter10.setForPayment(true);
        descriptionSimpleParameter10.setForSearch(false);
        descriptionSimpleParameter10.setLabel("Код бюджетной классификации");
        descriptionSimpleParameter10.setName("KBK");
        descriptionSimpleParameter10.setReadonly(true);
        descriptionSimpleParameter10.setRequired(true);
        descriptionSimpleParameter10.setVisible(false);
        descriptionSimpleParameter10.setDefaultValue(getMacroPart(contragent, "KBK"));

        DescriptionSimpleParameter descriptionSimpleParameter11 = catOf.createDescriptionSimpleParameter();
        descriptionSimpleParameter11.setForPayment(true);
        descriptionSimpleParameter11.setForSearch(false);
        descriptionSimpleParameter11.setLabel("Наименование получателя");
        descriptionSimpleParameter11.setName("Recipient");
        descriptionSimpleParameter11.setReadonly(true);
        descriptionSimpleParameter11.setRequired(true);
        descriptionSimpleParameter11.setVisible(true);
        descriptionSimpleParameter11.setDefaultValue(getMacroPart(contragent, "CONTRAGENT_NAME"));

        DescriptionSimpleParameter descriptionSimpleParameter12 = catOf.createDescriptionSimpleParameter();
        descriptionSimpleParameter12.setForPayment(true);
        descriptionSimpleParameter12.setForSearch(false);
        descriptionSimpleParameter12.setLabel("Назначение платежа");
        descriptionSimpleParameter12.setName("Narrative");
        descriptionSimpleParameter12.setReadonly(false);
        descriptionSimpleParameter12.setRequired(true);
        descriptionSimpleParameter12.setVisible(true);
        descriptionSimpleParameter12.setDefaultValue("Оплата услуг питания в образовательном учреждении");

        DescriptionSimpleParameter descriptionSimpleParameter13 = catOf.createDescriptionSimpleParameter();
        descriptionSimpleParameter13.setForPayment(false);
        descriptionSimpleParameter13.setForSearch(false);
        descriptionSimpleParameter13.setLabel("Признак дублирования в ГИС ГМП");
        descriptionSimpleParameter13.setName("duplication");
        descriptionSimpleParameter13.setReadonly(true);
        descriptionSimpleParameter13.setRequired(false);
        descriptionSimpleParameter13.setVisible(false);
        descriptionSimpleParameter13.setDefaultValue("off");

        /*DescriptionSimpleParameter descriptionSimpleParameter14 = catOf.createDescriptionSimpleParameter();
        descriptionSimpleParameter14.setForPayment(true);
        descriptionSimpleParameter14.setForSearch(true);
        descriptionSimpleParameter14.setLabel("СНИЛС");
        descriptionSimpleParameter14.setName("AltPayerIdentifier");
        descriptionSimpleParameter14.setReadonly(false);
        descriptionSimpleParameter14.setRequired(true);
        descriptionSimpleParameter14.setVisible(true);
        descriptionSimpleParameter14.setRegexp("^14(0){9}\\d{11}643$");*/

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
        //descriptionParametersType.getDescriptionSimpleParameterOrDescriptionComplexParameter().add(descriptionSimpleParameter14);

        serviceType.setPaymentParameters(descriptionParametersType);

        generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.catalog.ServiceType.CurrenciesServices currenciesServices = catOf.createServiceTypeCurrenciesServices();
        currenciesServices.getCurrencyCode().add(CurrencyCodeType.RUR);
        serviceType.setCurrenciesServices(currenciesServices);

        final generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.organization.ObjectFactory orgOf =
                new generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.organization.ObjectFactory();
        PayeeType payeeType = orgOf.createPayeeType();
        payeeType.setINN(getMacroPart(contragent, "INN"));
        payeeType.setKPP(getMacroPart(contragent, "KPP"));
        payeeType.setOGRN(getMacroPart(contragent, "OGRN"));
        payeeType.setOKTMO(getMacroPart(contragent, "OKTMO"));
        payeeType.setName(getMacroPart(contragent, "CONTRAGENT_NAME"));

        AccountsType accountsType = orgOf.createAccountsType();
        AccountCatalogType accountCatalogType = orgOf.createAccountCatalogType();
        accountCatalogType.setAccount(getMacroPart(contragent, "FINANCE_ACCOUNT"));
        accountCatalogType.setKind(BigInteger.valueOf(1L));
        BankType bankType = orgOf.createBankType();
        bankType.setName(getMacroPart(contragent, "FINANCE_PROVIDER"));
        bankType.setBIK(getMacroPart(contragent, "BIK"));
        bankType.setCorrespondentBankAccount(getMacroPart(contragent, "KORR_FINANCE_ACCOUNT"));
        accountCatalogType.setBank(bankType);
        accountsType.getAccount().add(accountCatalogType);

        payeeType.setAccounts(accountsType);
        serviceType.setPayee(payeeType);

        serviceType.setPaymentKind(BigInteger.valueOf(1L));

        final generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.common.ObjectFactory comOf =
                new generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.common.ObjectFactory();
        final Money minMoney = comOf.createMoney();
        minMoney.setCurrency(CurrencyCodeType.RUR);
        minMoney.setExponent(BigInteger.valueOf(2L));
        minMoney.setValue(1L);
        serviceType.setMinAmount(minMoney);
        final Money maxMoney = comOf.createMoney();
        maxMoney.setCurrency(CurrencyCodeType.RUR);
        maxMoney.setExponent(BigInteger.valueOf(2L));
        maxMoney.setValue(2147483647L);
        serviceType.setMaxAmount(maxMoney);

        final CommissionsType commissionsType = comOf.createCommissionsType();
        final CommissionType commissionType = comOf.createCommissionType();
        commissionType.setKind("4");
        commissionType.setPercent(BigDecimal.valueOf(Double.parseDouble(getMacroPart(contragent, "COMISSION_PERCENTS"))));
        commissionType.setMinValue(minMoney);
        commissionType.setMaxValue(maxMoney);
        commissionsType.getComission().add(commissionType);

        serviceCatalogType.getService().add(serviceType);

        final ImportCatalogRequest importCatalogRequest = mesDataOf.createImportCatalogRequest();

        if (requestType == RNIPLoadPaymentsService.REQUEST_MODIFY_CATALOG) {
            importCatalogRequest.setChanges(mesDataOf.createImportCatalogRequestChanges(serviceCatalogType));
        }
        else if (requestType == RNIPLoadPaymentsService.REQUEST_CREATE_CATALOG) {
            importCatalogRequest.setServiceCatalog(serviceCatalogType);
        }

        requestMessageType.setRequestMessageData(mesDataOf.createImportCatalogRequest(importCatalogRequest));

        appDataType.getAny().add(requestMessageType);
        messageDataType.setAppData(appDataType);

        final Holder<MessageType> messageTypeHolder = new Holder<MessageType>(messageType);
        final Holder<MessageDataType> messageDataTypeHolder = new Holder<MessageDataType>(messageDataType);

        port116.gisgmpTransferMsg(messageTypeHolder, messageDataTypeHolder);

        return messageDataTypeHolder.value;
        /*final MessageDataType response = messageDataTypeHolder.value;

        String soapError = checkError116(response);
        if (!soapError.equals("OK")) {
            logger.error(String.format("Ошибка при изменении каталога для контрагента %s: %s", contragent.getContragentName(), soapError));
            throw new IllegalStateException ("Ошибка во время обращения к РНИП: " + soapError);
        }
        info("Каталог для контрагента %s изменен", contragent.getContragentName());

        return null;*/
    }

    private boolean hasMore(MessageDataType response) {
        try {
            Object o = response.getAppData().getAny().get(0);
            ResponseMessageType responseMessageType = (ResponseMessageType)((JAXBElement) o).getValue();
            ExportPaymentsResponseType exportPaymentsResponseType = (ExportPaymentsResponseType)responseMessageType.getResponseMessageData().getValue();
            return exportPaymentsResponseType.getPayments().isHasMore();
        }
        catch(Exception e) {
            return false;
        }
    }

    @Override
    public String checkError(Object response) {
        if (response != null && response instanceof List) {
            return null; //возвращаем ОК для вызовов где параметром передается List, т.к. для элементов проверка уже пройдена
        }
        try {
            Object o = ((MessageDataType)response).getAppData().getAny().get(0);
            ResponseMessageType responseMessageType = (ResponseMessageType)((JAXBElement) o).getValue();
            if (!(responseMessageType.getResponseMessageData().getValue() instanceof TicketType)) {
                return null;
            }
            TicketType ticketType = (TicketType)responseMessageType.getResponseMessageData().getValue();
            ResultInfo resultInfo = ticketType.getRequestProcessResult();
            String resultCode = resultInfo.getResultCode();
            String resultDescription = resultInfo.getResultDescription();
            if (resultCode.equals("0")) {
                return null;
            }
            else {
                return String.format("Receiver error from RNIP v1.16. Error code: %s, Description: %s", resultCode, resultDescription);
            }
        }
        catch (Exception e) {
            logger.error("Error parsing rnip response: ", e);
            return "Error parsing response RNIP v1.16";
        }
    }

    private String checkError(MessageDataType messageDataType) {
        return checkError((Object)messageDataType);
    }

    public void setEndpointAddress(BindingProvider bindingProvider, String endpointAddress) {
        final Map<String, Object> requestContext = bindingProvider.getRequestContext();
        requestContext.put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpointAddress);
        //setEndpointAddress(bindingProvider, endpointAddress.toString());
    }

    private void InitRNIP116Service() throws MalformedURLException {
        if (port116 == null) {
            service116 = new SmevGISGMPService_Service();
            port116 = service116.getSmevGISGMPServiceSOAP();
            bindingProvider116 = (BindingProvider) port116;
            Client client = ClientProxy.getClient(port116);
            HTTPConduit conduit = (HTTPConduit) client.getConduit();
            HTTPClientPolicy policy = conduit.getClient();
            policy.setReceiveTimeout(15 * 60 * 1000);
            policy.setConnectionTimeout(15 * 60 * 1000);

            URL endpoint = new URL(getRNIPUrl());
            setEndpointAddress(bindingProvider116, endpoint.toString());
        }
    }

    private XMLGregorianCalendar getXMLGregorianDate(Date dateTime) throws DatatypeConfigurationException {
        if (dateTime == null) {
            return null;
        }
        final GregorianCalendar gregorianCalendar = new GregorianCalendar();
        gregorianCalendar.setTimeInMillis(dateTime.getTime());
        final DatatypeFactory dataTypeFactory = DatatypeFactory.newInstance();
        return dataTypeFactory.newXMLGregorianCalendar(gregorianCalendar);
    }

    private Date getDatePart(String part, Date updateDate, Date startDate, Date endDate, int requestType) throws Exception {
        String str;
        Date start_date = null;
        Date return_date;
        if (part.equals("START_DATE")) {
            if(startDate == null){
                logger.warn("Auto");
                return_date = CalendarUtils.addMinute(new Date(updateDate.getTime()), -1);
            }else {
                logger.warn("Manual start: "+startDate);
                return_date = startDate;
            }
            return return_date;
        }

        if (part.equals("END_DATE")) {
            Date lastUpdateDate;
            if(startDate == null){
                logger.warn("Auto");
                lastUpdateDate = CalendarUtils.addMinute(new Date(updateDate.getTime()), -1);
            }else {
                logger.warn("Manual start: "+startDate);
                lastUpdateDate = startDate;
            }
            start_date = lastUpdateDate;
            if(endDate == null){
                logger.warn("Auto");
                if (requestType == REQUEST_LOAD_PAYMENTS) {
                    Date curtime = new Date(System.currentTimeMillis());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(start_date);
                    cal.add(Calendar.HOUR, 6);
                    Date time2 = cal.getTime();
                    if (curtime.before(time2)) {
                        return_date = curtime;
                    }
                    else {
                        return_date = time2;
                    }
                }
                else {
                    return_date = new Date(System.currentTimeMillis());
                }
            }else {
                logger.warn("Manual end: "+endDate);
                return_date = endDate;
            }
            return return_date;
        }
        return null;
    }

    protected String getMacroPart(Contragent contragent, String part) {

        if (part.equals("CONTRAGENT_ID")) {
            String id = getRNIPIdFromRemarks(contragent.getRemarks());
            return formatString(id == null ? "" : id);
        }
        if (part.equals("CONTRAGENT_BMID")) {
            String id = getRNIPBmIdFromRemarks(contragent.getRemarks());
            return formatString(id == null ? "" : id);
        }
        if (part.equals("FINANCE_PROVIDER")) {
            return formatString(contragent.getBank());
        }
        if (part.equals("CONTRAGENT_NAME")) {
            return formatString(contragent.getContragentName());
        }
        if (part.equals("FINANCE_ACCOUNT")) {
            return formatString(contragent.getAccount());
        }
        if (part.equals("KORR_FINANCE_ACCOUNT")) {
            return  formatString(contragent.getCorrAccount());
        }
        if (part.equals("KBK")) {
            //return "00000000000000000000";
            return "0";
        }
        if (part.equals("INN")) {
            return formatString(contragent.getInn());
        }
        if (part.equals("KPP")) {
            return formatString(contragent.getKpp());
        }
        if (part.equals("OKATO")) {
            return formatString(contragent.getOkato());
        }
        if (part.equals("OKTMO")) {
            String oktmo = contragent.getOktmo();
            if ((oktmo == null) || (oktmo.equals(""))) {
                return "0";
            }
            else {
                return formatString(oktmo);
            }
        }
        if (part.equals("OGRN")) {
            return formatString(contragent.getOgrn());
        }
        if (part.equals("BIK")) {
            return formatString(contragent.getBic());
        }
        if (part.equals("COMISSION_PERCENTS")) {
            String comissionStr = getRNIPComissionFromRemarks(contragent.getRemarks());
            double comission = 0D;
            try {
                comission = Double.parseDouble(comissionStr);
            } catch (Exception e) {
                comission = 0D;
            }
            String cStr = new BigDecimal(comission).setScale(1, BigDecimal.ROUND_HALF_DOWN).toString();
            return formatString(cStr.trim());
        }
        if (part.equals("CURRENT_DATE")) {
            String str = new SimpleDateFormat(RNIP_DATE_FORMAT).format(new Date(System.currentTimeMillis()));
            return formatString(str);
        }
        if (part.equals("CURRENT_DATE_TIME")) {
            String str = new SimpleDateFormat(RNIP_DATE_TIME_FORMAT).format(new Date(System.currentTimeMillis()));
            return formatString(str);
        }
        return "";
    }

    @Override
    protected String formatString(String str) {
        return str;
    }

    @Override
    public String getRNIPIdFromRemarks (String remark) {
        return getValueByNameFromRemars(remark, "RNIP_1_16_2");
    }

    @Deprecated   //тестовый метод, нет прав на экспорт каталога
    public MessageDataType executeExportCatalog(Contragent contragent, Date startDate, Date endDate) throws Exception {
        InitRNIP116Service();

        logger.info(String.format("Запрос на получение платежей их РНИП,. Попытка № %s в серии", reqs));

        String alias = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_ALIAS);
        String pass = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_PASSWORD);

        final RNIPSecuritySOAPHandler rnipSecuritySOAPHandler = new RNIPSecuritySOAPHandler(alias, pass, getPacketLogger(contragent));
        final List<Handler> handlerChain = new ArrayList<Handler>();
        handlerChain.add(rnipSecuritySOAPHandler);
        bindingProvider116.getBinding().setHandlerChain(handlerChain);

        final generated.smev.gisgmp.client.ru.gosuslugi.smev.rev120315.ObjectFactory messageObjectFactory = new generated.smev.gisgmp.client.ru.gosuslugi.smev.rev120315.ObjectFactory();

        final MessageType messageType = messageObjectFactory.createMessageType();
        final MessageDataType messageDataType = messageObjectFactory.createMessageDataType();
        final AppDataType appDataType = messageObjectFactory.createAppDataType();

        final OrgExternalType sender = messageObjectFactory.createOrgExternalType();
        sender.setCode("000000001");
        sender.setName("External Organization");
        messageType.setSender(sender);

        final OrgExternalType recipient = messageObjectFactory.createOrgExternalType();
        recipient.setCode("105805771");
        recipient.setName("ИС_РНиП");
        messageType.setRecipient(recipient);

        messageType.setServiceName("105805771");
        messageType.setTypeCode(TypeCodeType.GFNC);
        messageType.setStatus(StatusType.REQUEST);
        messageType.setDate(getXMLGregorianDate(new Date(System.currentTimeMillis())));
        messageType.setExchangeType("6");

        final generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.message.ObjectFactory mesOf =
                new generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.message.ObjectFactory();

        final RequestMessageType requestMessageType = mesOf.createRequestMessageType();

        requestMessageType.setId(String.format("N_%s", UUID.randomUUID()));
        requestMessageType.setSenderIdentifier(getMacroPart(contragent, "CONTRAGENT_ID"));
        requestMessageType.setTimestamp(RNIPSecuritySOAPHandler.toXmlGregorianCalendar(new Date()));

        final generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.messagedata.ObjectFactory mesDataOf =
                new generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.messagedata.ObjectFactory();

        final generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.ObjectFactory dataOf =
                new generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.ObjectFactory();

        final DataRequest dataRequest = dataOf.createDataRequest();
        dataRequest.setKind("CATALOG");
        dataRequest.setId("I_52d85fa5-18ae-11e5-b50b-bcaec5d977ce");
        final DataRequest.Filter filter = dataOf.createDataRequestFilter();
        final DataRequest.Filter.Conditions conditions = dataOf.createDataRequestFilterConditions();

        DataRequest.Filter.Conditions.Timeslot timeslot = new DataRequest.Filter.Conditions.Timeslot();
        timeslot.setStartDate(RNIPSecuritySOAPHandler.toXmlGregorianCalendar(new Date(System.currentTimeMillis() - (long)1000*60*60*24)));
        timeslot.setEndDate(RNIPSecuritySOAPHandler.toXmlGregorianCalendar(new Date(System.currentTimeMillis())));

        conditions.setTimeslot(timeslot);

        filter.setConditions(conditions);
        dataRequest.setFilter(filter);
        requestMessageType.setRequestMessageData(mesDataOf.createExportRequest(dataRequest));

        appDataType.getAny().add(requestMessageType);
        messageDataType.setAppData(appDataType);

        final Holder<MessageType> messageTypeHolder = new Holder<MessageType>(messageType);
        final Holder<MessageDataType> messageDataTypeHolder = new Holder<MessageDataType>(messageDataType);

        port116.gisgmpTransferMsg(messageTypeHolder, messageDataTypeHolder);

        return messageDataTypeHolder.value;
    }
}
