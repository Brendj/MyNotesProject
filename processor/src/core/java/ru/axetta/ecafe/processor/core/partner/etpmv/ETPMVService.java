/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.etpmv;

import generated.contingent.ispp.*;
import generated.etp.ObjectFactory;
import generated.etp.*;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.DiscountManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.ApplicationForFoodExistsException;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang.StringUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.zlp.kafka.BenefitKafkaService;
import ru.axetta.ecafe.processor.core.zlp.kafka.RequestValidationData;
import ru.axetta.ecafe.processor.core.zlp.kafka.request.DocValidationRequest;
import ru.axetta.ecafe.processor.core.zlp.kafka.request.GuardianshipValidationRequest;

import org.hibernate.Session;
import org.hibernate.Transaction;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.handler.Handler;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by nuc on 01.11.2018.
 */
@Component
@Scope("singleton")
public class ETPMVService {
    private static final Logger logger = LoggerFactory.getLogger(ETPMVService.class);
    private final int COORDINATE_MESSAGE = 0;
    private final int COORDINATE_STATUS_MESSAGE = 1;
    public static final String ISPP_ID = "-063101-";
    public static final String NEW_ISPP_ID = "-100101-";
    private final int PAUSE_IN_MILLIS = 1000;
    public final String BENEFIT_INOE = "0";
    public final String BENEFIT_REGULAR = "1";
    private final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    //public final Set<String> BENEFITS_ETP = new HashSet<String>(Arrays.asList("0", "1", "2", "3", "4", "5"));
    //private final int DAYS_TO_EXPIRE = 5;
    private IsppWebServiceService service;
    private IsppWebService port;
    private BindingProvider bindingProvider;
    JAXBContext jaxbContext;
    JAXBContext jaxbConsumerContext;
    private final int AIS_CONTINGENT_CONNECT_TIMEOUT = 10000;
    private final int AIS_CONTINGENT_REQUEST_TIMEOUT = 10*60*1000;
    private final int AIS_CONTINGENT_MAX_PACKET = 10;
    private final String REQUEST_TIMEOUT = "com.sun.xml.internal.ws.request.timeout";
    private final String CONNECT_TIMEOUT = "com.sun.xml.internal.ws.connect.timeout";

    @Async
    public void processIncoming(String message) {
        try {
            int type = getMessageType(message);
            if (type == COORDINATE_MESSAGE) {
                CoordinateMessage coordinateMessage = (CoordinateMessage) getCoordinateMessage(message);
                processCoordinateMessage(coordinateMessage, message);
            }
            if (type == COORDINATE_STATUS_MESSAGE) {
                //Обработка сообщения из ЕТП об отмене заявления
                CoordinateStatusMessage coordinateStatusMessage = (CoordinateStatusMessage)getCoordinateMessage(message);
                processCoordinateStatusMessage(coordinateStatusMessage, message);
            }
        } catch (Exception e) {
            logger.error("Error in process incoming ETP message: ", e);
            sendToBK(message);
        }
    }

    private boolean getCoordinateMessageFormat(String message) {
        return message.contains(NEW_ISPP_ID);
    }

    public Object getCoordinateMessage(String message) throws Exception {
        JAXBContext jaxbContext = getJAXBContext();
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        InputStream stream = new ByteArrayInputStream(message.getBytes(Charset.forName("UTF-8")));
        return unmarshaller.unmarshal(stream);
    }

    private void processCoordinateMessage(CoordinateMessage coordinateMessage, String message) throws Exception {
        long begin_time = System.currentTimeMillis();
        ETPMVDaoService daoService = RuntimeContext.getAppContext().getBean(ETPMVDaoService.class);

        CoordinateData coordinateData = coordinateMessage.getCoordinateDataMessage();
        RequestService requestService = coordinateData.getService();
        String serviceNumber = requestService.getServiceNumber();
        logger.info("Incoming ETP message with ServiceNumber = " + serviceNumber);
        if (!serviceNumber.contains(ISPP_ID) && !serviceNumber.contains(NEW_ISPP_ID)) throw new Exception("Wrong ISPP_ID in Service Number");
        try {
            if (serviceNumber.contains(NEW_ISPP_ID)) {
                //валидация на то, что пакет парсится и хватает данных для отправки запросов по межведу
                RequestValidationData data = new RequestValidationData(coordinateMessage, null);
                data.testForData();
            }
        } catch (Exception e) {
            logger.error("Error in valid application data: ", e);
            sendStatus(begin_time, serviceNumber, ApplicationForFoodState.DELIVERY_ERROR, "not enough data");
            sendToBK(message);
            return;
        }

        boolean newFormat = getCoordinateMessageFormat(message);

        daoService.saveEtpPacket(serviceNumber, message);

        RequestServiceForSign requestServiceForSign = coordinateData.getSignService();
        RequestServiceForSign.CustomAttributes customAttributes = requestServiceForSign.getCustomAttributes();

        Element serviceProperties = (Element) customAttributes.getAny();
        String guid;
        List<String> benefits;
        String yavl_lgot = "";
        Boolean validDoc = null;
        Boolean validGuardianship = null;
        if (newFormat) {
            guid = getServicePropertiesValue(serviceProperties, "IDLink");
            benefits = getBenefitsFromServiceProperties(serviceProperties);
            Boolean isLegal = Boolean.parseBoolean(getServicePropertiesValue(serviceProperties, "IsLegalRepresentative"));
            if (!isLegal) {
                logger.error("Error in processCoordinateMessage: not legal represent");
                sendStatus(begin_time, serviceNumber, ApplicationForFoodState.DELIVERY_ERROR, "not legal represent");
                sendToBK(message);
                return;
            }
            if (benefits.size() == 0 || wrongBenefits(benefits)) {
                logger.error("Error in processCoordinateMessage: wrong benefits in packet");
                sendStatus(begin_time, serviceNumber, ApplicationForFoodState.DELIVERY_ERROR, "wrong benefits in packet");
                sendToBK(message);
                return;
            }
            String strValidDoc = getServicePropertiesValue(serviceProperties, "Validity");
            String strValidGuardianship = getServicePropertiesValue(serviceProperties, "ValidationGuardianship");
            if (!StringUtils.isEmpty(strValidDoc)) validDoc = Boolean.parseBoolean(strValidDoc);
            if (!StringUtils.isEmpty(strValidGuardianship)) validGuardianship = Boolean.parseBoolean(strValidGuardianship);
        } else {
            guid = getServicePropertiesValue(serviceProperties, "guid");
            yavl_lgot = getServicePropertiesValue(serviceProperties, "yavl_lgot");

            benefits = Arrays.asList(getServicePropertiesValue(serviceProperties, "benefit"));

            if (wrongBenefits(yavl_lgot, benefits.get(0))) {
                logger.error("Error in processCoordinateMessage: wrong benefits in packet");
                sendStatus(begin_time, serviceNumber, ApplicationForFoodState.DELIVERY_ERROR, "wrong benefits in packet");
                sendToBK(message);
                return;
            }
        }

        ArrayOfBaseDeclarant contacts = requestServiceForSign.getContacts();
        BaseDeclarant baseDeclarant = getBaseDeclarant(contacts);
        if (baseDeclarant == null) {
            logger.error("Error in processCoordinateMessage: wrong contacts data");
            sendStatus(begin_time, serviceNumber, ApplicationForFoodState.DELIVERY_ERROR, "wrong contacts data");
            sendToBK(message);
            return;
        }
        String firstName = ((RequestContact) baseDeclarant).getFirstName();
        String lastName = ((RequestContact) baseDeclarant).getLastName();
        String middleName = ((RequestContact) baseDeclarant).getMiddleName();
        String mobile = Client.checkAndConvertMobile(((RequestContact) baseDeclarant).getMobilePhone());
        if (StringUtils.isEmpty(guid) || StringUtils.isEmpty(firstName) || StringUtils.isEmpty(lastName) || StringUtils.isEmpty(mobile)) {
            logger.error("Error in processCoordinateMessage: not enough data");
            sendStatus(begin_time, serviceNumber, ApplicationForFoodState.DELIVERY_ERROR,  "not enough data");
            sendToBK(message);
            return;
        }
        Client client = DAOReadonlyService.getInstance().getClientByMeshGuid(guid);

        if (client == null) {
            logger.error("Error in processCoordinateMessage: client not found");
            sendStatus(begin_time, serviceNumber, ApplicationForFoodState.DELIVERY_ERROR, "client not found");
            sendToBK(message);
            return;
        }
        ApplicationForFood applicationForFood = daoService.findApplicationForFood(guid);

        if (applicationForFood != null) {
            if (!testForApplicationForFoodStatus(applicationForFood)) {
                logger.error("Error in processCoordinateMessage: ApplicationForFood found but status is incorrect");
                sendStatus(begin_time, serviceNumber, ApplicationForFoodState.DELIVERY_ERROR, "ApplicationForFood found but status is incorrect");
                sendToBK(message);
                return;
            }
        }
        List<Integer> _benefits;
        if (newFormat) {
            _benefits = RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).getDSZNBenefits(benefits);
        } else {
            _benefits = yavl_lgot.equals(BENEFIT_INOE) ? null
                    : Arrays.asList(RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).getDSZNBenefit(benefits.get(0)));
        }

        try {
            daoService.createApplicationForGood(client, _benefits, mobile, firstName, middleName, lastName, serviceNumber,
                    ApplicationForFoodCreatorType.PORTAL, validDoc, validGuardianship);
        } catch (ApplicationForFoodExistsException e) {
            logger.error("Error in processCoordinateMessage: ApplicationForFood found but status is incorrect");
            sendStatus(begin_time, serviceNumber, ApplicationForFoodState.DELIVERY_ERROR, "ApplicationForFood found but status is incorrect");
            sendToBK(message);
            return;
        }

        sendStatus(begin_time, serviceNumber, ApplicationForFoodState.TRY_TO_REGISTER, null);
        begin_time = System.currentTimeMillis();
        sendStatus(begin_time, serviceNumber, ApplicationForFoodState.REGISTERED, null);
        if (yavl_lgot.equals(BENEFIT_INOE)) {
            begin_time = System.currentTimeMillis();
            sendStatus(begin_time, serviceNumber, ApplicationForFoodState.PAUSED, null);
        }

        if (newFormat) {
            RuntimeContext.getAppContext().getBean(BenefitKafkaService.class).sendBenefitValidationRequest(serviceNumber);
        }

        daoService.updateEtpPacketWithSuccess(serviceNumber);
    }

    private void processCoordinateStatusMessage(CoordinateStatusMessage coordinateStatusMessage, String message) throws Exception {
        long begin_time = System.currentTimeMillis();
        ETPMVDaoService daoService = RuntimeContext.getAppContext().getBean(ETPMVDaoService.class);

        CoordinateStatusData coordinateStatusData = coordinateStatusMessage.getCoordinateStatusDataMessage();
        String serviceNumber = coordinateStatusData.getServiceNumber();
        StatusType statusType = coordinateStatusData.getStatus();
        Integer code = statusType.getStatusCode();
        //Обрабатываем только заявления на отмену
        if (ApplicationForFoodState.CANCELED.getCode().equals(code.toString())) {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            Session persistenceSession = null;
            Transaction persistenceTransaction = null;
            try {
                persistenceSession = runtimeContext.createPersistenceSession();
                persistenceTransaction = persistenceSession.beginTransaction();
                logger.info("Incoming ETP message with ServiceNumber = " + serviceNumber);
                if (!serviceNumber.contains(ISPP_ID) && !serviceNumber.contains(NEW_ISPP_ID))
                    throw new Exception("Wrong ISPP_ID in Service Number");
                daoService.saveEtpPacket(serviceNumber, message);

                //Находим заявления по номеру
                ApplicationForFood applicationForFood = daoService.getApplicationForFoodWithDtisznCodes(serviceNumber);
                if (applicationForFood == null)
                {
                    logger.error("Error in processCoordinateStatusMessage: application not found");
                    //sendStatus(begin_time, serviceNumber, ApplicationForFoodState.DELIVERY_ERROR, "wrong contacts data");
                    sendToStatusBK(message);
                    return;
                }
                Client client = applicationForFood.getClient();
                ApplicationForFoodState applicationForFoodState = applicationForFood.getStatus().getApplicationForFoodState();
                //заявление найдено и оно в статусе "заявление зарегистрировано" в одном из следующих статусов 1050, 1052, 1075, 7704.х (7704.1...10), 7705.х (7705.1...10) и не в архиве
                if (!applicationForFood.getArchived() && (applicationForFoodState == ApplicationForFoodState.REGISTERED ||
                        applicationForFoodState == ApplicationForFoodState.RESULT_PROCESSING ||
                        applicationForFoodState == ApplicationForFoodState.OK ||
                        applicationForFoodState.getCode().startsWith(ApplicationForFoodState.INFORMATION_REQUEST_SENDED.getCode()) ||
                        applicationForFoodState.getCode().startsWith(ApplicationForFoodState.INFORMATION_REQUEST_RECEIVED.getCode()))) {
                    for (ApplicationForFoodDiscount applicationForFoodDiscount: applicationForFood.getDtisznCodes()) {
                        applicationForFoodDiscount.removeConfirmed();
                        persistenceSession.update(applicationForFoodDiscount);
                        ClientDtisznDiscountInfo discountInfo = DAOUtils.getDTISZNDiscountInfoByClientAndCode(persistenceSession, client,
                                applicationForFoodDiscount.getDtisznCode().longValue());
                        if (discountInfo != null) {
                            discountInfo.setArchived(true);
                            discountInfo.setArchiveDate(new Date());
                            discountInfo.setLastUpdate(new Date());
                            persistenceSession.update(discountInfo);
                        }
                    }
                    DAOUtils.updateApplicationForFood(persistenceSession, applicationForFood.getClient(), new ApplicationForFoodStatus(
                            ApplicationForFoodState.WITHDRAWN));
                    sendStatus(begin_time, serviceNumber, ApplicationForFoodState.WITHDRAWN, null);
                    //sendToBK(message);
                    DiscountManager.rebuildAppointedMSPByClient(persistenceSession, client);
                } else {
                    logger.error("Error in processCoordinateStatusMessage: not found application");
                    //sendStatus(begin_time, serviceNumber, ApplicationForFoodState.DELIVERY_ERROR, "not found application");
                    sendToStatusBK(message);
                    return;
                }
                daoService.updateEtpPacketWithSuccess(serviceNumber);
                persistenceTransaction.commit();
                persistenceTransaction = null;
            } catch (Exception e) {
                logger.error("Error in processCoordinateStatusMessage", e);
            } finally {
                HibernateUtils.rollback(persistenceTransaction, logger);
                HibernateUtils.close(persistenceSession, logger);
            }
        }
    }


    private boolean wrongBenefits(List<String> benefits) {
        return RuntimeContext.getAppContext().getBean(ETPMVDaoService.class)
                .getDSZNBenefits(benefits).size() != benefits.size();
    }

    private BaseDeclarant getBaseDeclarant(ArrayOfBaseDeclarant contacts) {
        BaseDeclarant bd = null;
        for (BaseDeclarant baseDeclarant : contacts.getBaseDeclarant()) {
            if (!baseDeclarant.getType().equals(ContactType.CHILD)) {
                if (bd != null) {
                    return null;
                }
                bd = baseDeclarant;
            }
        }
        return bd;
    }

    private boolean wrongBenefits(String yavl_lgot, String benefit) {
        if (StringUtils.isEmpty(yavl_lgot)) return true;
        if (!(yavl_lgot.equals(BENEFIT_INOE) || yavl_lgot.equals(BENEFIT_REGULAR))) return true;
        if (yavl_lgot.equals(BENEFIT_REGULAR) && (StringUtils.isEmpty(benefit) || !RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).benefitExists(benefit))) return true;
        return false;
    }

    public static boolean testForApplicationForFoodStatus(ApplicationForFood applicationForFood) {
        if (applicationForFood.getStatus().getApplicationForFoodState().equals(ApplicationForFoodState.DELIVERY_ERROR)) return true; //103099
        if (applicationForFood.getArchived() != null && applicationForFood.getArchived()) return true; //признак архивности
        if (applicationForFood.getStatus().getApplicationForFoodState().equals(ApplicationForFoodState.DENIED_BENEFIT)
                || applicationForFood.getStatus().getApplicationForFoodState().equals(ApplicationForFoodState.DENIED_GUARDIANSHIP)
                || applicationForFood.getStatus().getApplicationForFoodState().equals(ApplicationForFoodState.DENIED_OLD)) return true; //1080.1, 1080.2, 1080.3
        return false;
    }

    @Async
    public void sendStatusAsync(long begin_time, String serviceNumber, ApplicationForFoodState status) throws Exception {
        sendStatus(begin_time, serviceNumber, status);
    }

    public void sendStatus(long begin_time, String serviceNumber, ApplicationForFoodState status) throws Exception {
        sendStatus(begin_time, serviceNumber, status, null);
    }

    public void sendStatus(long begin_time, String serviceNumber, ApplicationForFoodState status, String errorMessage) throws Exception {
        logger.info("Sending status to ETP with ServiceNumber = " + serviceNumber + ". Status = " + status.getCode());
        String message = createStatusMessage(serviceNumber, status);
        boolean success = false;
        try {
            if (System.currentTimeMillis() - begin_time < PAUSE_IN_MILLIS) {
                Thread.sleep(PAUSE_IN_MILLIS - (System.currentTimeMillis() - begin_time)); //пауза между получением заявления и ответом, или между двумя статусами не менее секунды
            }
            RuntimeContext.getAppContext().getBean(ETPMVClient.class).sendStatus(message);
            logger.info("Status with ServiceNumber = " + serviceNumber + " sent to ETP. Status = " + status.getCode());
            success = true;
        } catch (Exception e) {
            logger.error("Error in sendStatus: ", e);
        }
        RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).saveOutgoingStatus(serviceNumber, message, success, errorMessage, new ApplicationForFoodStatus(status));
    }

    private void sendToBK(String message) {
        boolean success = false;
        try {
            RuntimeContext.getAppContext().getBean(ETPMVClient.class).addToBKQueue(message);
            success = true;
        } catch (Exception e) {
            logger.error("Error in sendBKStatus: ", e);
        }
        RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).saveBKStatus(message, success);
    }

    private void sendToStatusBK(String message) {
        boolean success = false;
        try {
            RuntimeContext.getAppContext().getBean(ETPMVClient.class).addToStatusBKQueue(message);
            success = true;
        } catch (Exception e) {
            logger.error("Error in sendStatusBK: ", e);
        }
        RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).saveBKStatus(message, success);
    }

    private String createStatusMessage(String serviceNumber, ApplicationForFoodState status) throws Exception {
        ObjectFactory objectFactory = new ObjectFactory();
        CoordinateStatusMessage coordinateStatusMessage = objectFactory.createCoordinateStatusMessage();
        CoordinateStatusData coordinateStatusData = objectFactory.createCoordinateStatusData();
        StatusType statusType = objectFactory.createStatusType();
        statusType.setStatusCode(status.getPureCode());
        statusType.setStatusTitle(status.getDescription());
        DateFormat format = new SimpleDateFormat(DATE_FORMAT);
        XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(format.format(new Date()));
        calendar.setTimezone(getTimeZoneOffsetInMinutes());
        statusType.setStatusDate(calendar);
        coordinateStatusData.setStatus(statusType);
        coordinateStatusData.setServiceNumber(serviceNumber);
        coordinateStatusMessage.setCoordinateStatusDataMessage(coordinateStatusData);
        if (status.getReason() != null) {
            DictionaryItem dictionaryItem = objectFactory.createDictionaryItem();
            dictionaryItem.setCode(status.getReason());
            dictionaryItem.setName(status.getDescription());
            coordinateStatusData.setReason(dictionaryItem);
        }
        coordinateStatusData.setNote(status.getNote());

        JAXBContext jaxbContext = getJAXBContextToSendStatus();
        Marshaller marshaller = jaxbContext.createMarshaller();
        StringWriter sw = new StringWriter();
        marshaller.marshal(coordinateStatusMessage, sw);
        return sw.toString();
    }

    private JAXBContext getJAXBContextToSendStatus() throws Exception {
        if (jaxbContext == null) jaxbContext = JAXBContext.newInstance(CoordinateStatusMessage.class);
        return jaxbContext;
    }

    private int getTimeZoneOffsetInMinutes() {
        return TimeZone.getTimeZone("Europe/Moscow").getRawOffset()/60000; //значение часового сдвига в мс выражаем в минутах
    }

    private String getServicePropertiesValue(Element serviceProperties, String elementName) {
        for (int i = 0; i < serviceProperties.getChildNodes().getLength(); i++) {
            Node node = serviceProperties.getChildNodes().item(i);
            if (node.getLocalName().equals(elementName)) return serviceProperties.getChildNodes().item(i).getFirstChild().getNodeValue();
        }
        return null;
    }

    private List<String> getBenefitsFromServiceProperties(Element serviceProperties) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < serviceProperties.getChildNodes().getLength(); i++) {
            Node node = serviceProperties.getChildNodes().item(i);
            if (node.getLocalName() != null && node.getLocalName().equals("PreferentialCategory")) {
                for (int j = 0; j < node.getChildNodes().getLength(); j++) {
                    Node node2 = node.getChildNodes().item(j);
                    if (node2.getFirstChild() != null && Boolean.parseBoolean(node2.getFirstChild().getTextContent())) {
                        result.add(node2.getLocalName());
                    }
                }
            }
        }
        return result;
    }

    public void resendStatuses() {
        if (!RuntimeContext.getInstance().actionIsOnByNode("ecafe.processor.etp.consumer.node") ||
                !RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.etp.isOn", "false").equals("true")) {
            return;
        }
        logger.info("Start resend statuses from EtpOutgoingMessage");
        List<EtpOutgoingMessage> messages = RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).getNotSendedMessages();
        int counter = 1;
        for (EtpOutgoingMessage message : messages) {
            logger.info(String.format("Resend status %s from %s", counter, messages.size()));
            boolean success = false;
            try {
                RuntimeContext.getAppContext().getBean(ETPMVClient.class).sendStatus(message.getEtpMessagePayload());
                success = true;
            } catch (Exception e) {
                logger.error("Error in sendErrorStatus: ", e);
            }
            RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).updateOutgoingStatus(message, success);
            counter++;
        }
        logger.info("End resend statuses from EtpOutgoingMessage");
        resendApplicationForFoodStatuses();
    }

    public void resendApplicationForFoodStatuses() {
        logger.info("Start resend resendApplicationForFood statuses");
        Date startDate = CalendarUtils.addDays(new Date(), -7);
        Date endDate = CalendarUtils.addHours(new Date(), -3);
        List<ApplicationForFoodHistory> list = RuntimeContext.getAppContext().getBean(ETPMVDaoService.class)
                .getNotSendedApplicationForFoodHistory(startDate, endDate);
        int counter = 1;
        for (ApplicationForFoodHistory history : list) {
            try {
                logger.info(String.format("Resend status %s from %s", counter, list.size()));
                long begin_time = System.currentTimeMillis();
                sendStatus(begin_time, history.getApplicationForFood().getServiceNumber(), history.getStatus().getApplicationForFoodState(), null);
                counter++;
            } catch (Exception e) {
                logger.error(String.format("Error in resendApplicationForFood status, serviceNumber = %s:", history.getApplicationForFood().getServiceNumber()), e);
            }
        }
        logger.info("End resend resendApplicationForFood statuses");
    }

    public void sendToAISContingent() throws Exception {
        if (!RuntimeContext.getInstance().actionIsOnByNode("ecafe.processor.etp.aiscontingent.node")) {
            return;
        }
        sendToAISContingentTask();
    }

    public void sendToAISContingentTask() throws Exception {
        logger.info("Start sending data to AIS Contingent");
        List<ApplicationForFood> list = RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).getDataForAISContingent();
        if (list.size() == 0) {
            logger.info("Finish sending data to AIS Contingent. No records sent");
            return;
        }
        initAISContingentService();
        generated.contingent.ispp.ObjectFactory objectFactory = new generated.contingent.ispp.ObjectFactory();
        SetBenefits setBenefits = objectFactory.createSetBenefits();
        ServiceHeader isppHeaders = objectFactory.createServiceHeader();
        isppHeaders.setUsername(RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.etp.aiscontingent.username", "ispp"));
        isppHeaders.setPassword(encryptPassword(RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.etp.aiscontingent.password", "D43!asT7")));
        SetBenefitsRequest setBenefitsRequest = objectFactory.createSetBenefitsRequest();
        setBenefits.setRequest(setBenefitsRequest);
        SetBenefitsRequest.Children children = objectFactory.createSetBenefitsRequestChildren();
        int counter = 0;
        int sent_counter = 0;
        for (ApplicationForFood applicationForFood : list) {
            for (ApplicationForFoodDiscount appDiscount: applicationForFood.getDtisznCodes()) {
                Child child = objectFactory.createChild();
                child.setBenefitCode(appDiscount.getDtisznCode() == null ? "0" : appDiscount.getDtisznCode().toString());
                child.setMeshGUID(applicationForFood.getClient().getMeshGUID());
                children.getChild().add(child);
                sent_counter++;
                if (children.getChild().size() > AIS_CONTINGENT_MAX_PACKET) {
                    counter++;
                    setBenefitsRequest.setChildren(children);
                    logger.info(String.format("Sending request %s to AIS Contingent", counter));
                    SetBenefitsResponseSmall response = port.setBenefits(setBenefits, isppHeaders);
                    logger.info(String.format("Got response %s from AIS Contingent. Processing...", counter));
                    processResponseFromAISContingent(response);
                    children.getChild().clear();
                }
            }
        }
        if (children.getChild().size() > 0) {
            setBenefitsRequest.setChildren(children);
            logger.info(String.format("Sending request %s to AIS Contingent", counter));
            SetBenefitsResponseSmall response = port.setBenefits(setBenefits, isppHeaders);
            logger.info(String.format("Got response %s from AIS Contingent. Processing...", counter));
            processResponseFromAISContingent(response);
        }
        logger.info(String.format("Finish sending data to AIS Contingent. Sent %s records", sent_counter));
    }

    private void processResponseFromAISContingent(SetBenefitsResponseSmall response) throws Exception {
        if (response != null && response.getResult() != null && response.getResult().getSuccess() != null) {
            Long nextVersion = RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).getApplicationForFoodNextVersion();
            Long historyVersion = RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).getApplicationForFoodHistoryNextVersion();
            for (Child child : response.getResult().getSuccess().getChild()) {
                List<ApplicationForFood> apps = RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).confirmFromAISContingent(child.getMeshGUID(),
                        nextVersion, historyVersion);
                for (ApplicationForFood applicationForFood : apps) {
                    sendStatus(System.currentTimeMillis() - PAUSE_IN_MILLIS, applicationForFood.getServiceNumber(), applicationForFood.getStatus().getApplicationForFoodState(), null);
                }
            }
        }
        if (response != null && response.getResult() != null && response.getResult().getNotFound() != null && response.getResult().getNotFound().getChild().size() > 0) {
            StringBuilder notFoundGuids = new StringBuilder();
            for (Child child : response.getResult().getNotFound().getChild()) {
                notFoundGuids.append("MeshGuid: " + emptyIfNull(child.getMeshGUID()));
            }
            logger.error("Not found guids from AIS Contingent: " + notFoundGuids.toString());
        }
    }

    private String emptyIfNull(String str) {
        return str == null ? "" : str;
    }

    private String encryptPassword(String source) throws Exception {
        final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        messageDigest.reset();
        messageDigest.update(source.getBytes(Charset.forName("UTF8")));
        final byte[] resultByte = messageDigest.digest();
        return new String(Hex.encodeHex(resultByte));
    }

    public void sendRequestForGuardianshipValidation(ApplicationForFood applicationForFood) {
        if (applicationForFood.getValidGuardianShip()) {
            //todo исполнение заявления?
            return;
        }
        //RuntimeContext.getAppContext().getBean(BenefitKafkaService.class).sendGuardianshipValidationRequest(applicationForFood);
        RuntimeContext.getAppContext().getBean(BenefitKafkaService.class).sendRequest(applicationForFood, GuardianshipValidationRequest.class);
    }

    public void sendRequestForDocValidation(ApplicationForFood applicationForFood) {
        if (applicationForFood.getValidDoc()) {
            sendRequestForGuardianshipValidation(applicationForFood);
            return;
        }
        RuntimeContext.getAppContext().getBean(BenefitKafkaService.class).sendRequest(applicationForFood, DocValidationRequest.class);
    }

    private void initAISContingentService() throws Exception {
        if (port == null) {
            service = new IsppWebServiceService();
            port = service.getIsppWebServicePort();
            bindingProvider = (BindingProvider) port;
            URL endpoint = new URL(getAISContingentUrl());
            bindingProvider.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, endpoint.toString());
            Map<String, Object> requestContext = bindingProvider.getRequestContext();
            requestContext.put(REQUEST_TIMEOUT, AIS_CONTINGENT_REQUEST_TIMEOUT);
            requestContext.put(CONNECT_TIMEOUT, AIS_CONTINGENT_CONNECT_TIMEOUT);

            final AISContingentMessageLogger loggingHandler = new RuntimeContext().getAppContext().getBean(AISContingentMessageLogger.class);
            final List<Handler> handlerChain = new ArrayList<Handler>();
            handlerChain.add(loggingHandler);
            bindingProvider.getBinding().setHandlerChain(handlerChain);
        }
    }

    private String getAISContingentUrl() throws Exception {
        String url = RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.etp.aiscontingent.url", "");
        if (StringUtils.isEmpty(url)) throw new Exception("AIS Contingent endpoint address is not specified");
        return url;
    }
    /*public void processExpired() {
        Date dateTo = CalendarUtils.addDays(new Date(), DAYS_TO_EXPIRE); //todo как рассчитать 5 РАБОЧИХ дней без привязки к ОО?
        List<ApplicationForFood> list = RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).getExpiredApplicationsForFood(dateTo);
        long begin_time = System.currentTimeMillis();
        for (ApplicationForFood applicationForFood : list) {
            try {
                RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).expireApplicationForFood(applicationForFood);
                sendStatus(begin_time, applicationForFood.getServiceNumber(), ApplicationForFoodState.RESUME, null);
                begin_time = System.currentTimeMillis();
                sendStatus(begin_time, applicationForFood.getServiceNumber(), ApplicationForFoodState.DENIED,
                        ApplicationForFoodDeclineReason.NO_DOCS);
            } catch (Exception e) {
                logger.error(String.format("Error in expire application for food, serviceNumber = %s. Error stack: ", applicationForFood.getServiceNumber()), e);
            }
        }
    }*/

    private JAXBContext getJAXBContext() throws Exception {
        if (jaxbConsumerContext == null) {
            jaxbConsumerContext = JAXBContext.newInstance(CoordinateMessage.class, CoordinateStatusMessage.class);
        }
        return jaxbConsumerContext;
    }

    private int getMessageType(String message) throws Exception {
        if (message.contains(":CoordinateMessage")) {
            return COORDINATE_MESSAGE;
        }
        if (message.contains(":CoordinateStatusMessage")) {
            return COORDINATE_STATUS_MESSAGE;
        }
        throw new Exception("Unknown message type");
    }

    public int getPauseValue() {
        return PAUSE_IN_MILLIS;
    }

    @Async
    public void sendStatusesAsync(List<ETPMVScheduledStatus> statusList) throws Exception {
        for (ETPMVScheduledStatus status : statusList) {
            sendStatus(status);
        }
    }

    public void sendStatus(ETPMVScheduledStatus status) throws Exception {
        logger.info("Sending status to ETP with ServiceNumber = " + status.getServiceNumber() + ". Status = " + status.getState().getCode());
        String message = createStatusMessage(status.getServiceNumber(), status.getState());
        boolean success = false;
        try {
            Thread.sleep(PAUSE_IN_MILLIS);
            RuntimeContext.getAppContext().getBean(ETPMVClient.class).sendStatus(message);
            logger.info("Status with ServiceNumber = " + status.getServiceNumber() + " sent to ETP. Status = " + status.getState().getCode());
            success = true;
        } catch (Exception e) {
            logger.error("Error in sendStatus: ", e);
        }
        RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).saveOutgoingStatus(status.getServiceNumber(), message, success, null, new ApplicationForFoodStatus(status.getState()));
    }

    public void scheduleSync() throws Exception {
        String syncSchedule = RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.etp.aiscontingent.cron", "");
        if (syncSchedule.equals("")) {
            return;
        }
        try {
            JobDetail job = new JobDetail("SendToAISContingent", Scheduler.DEFAULT_GROUP, AISContingentServiceJob.class);
            SchedulerFactory sfb = new StdSchedulerFactory();
            Scheduler scheduler = sfb.getScheduler();
            if (!syncSchedule.equals("")) {
                CronTrigger trigger = new CronTrigger("SendToAISContingent", Scheduler.DEFAULT_GROUP);
                trigger.setCronExpression(syncSchedule);
                if (scheduler.getTrigger("SendToAISContingent", Scheduler.DEFAULT_GROUP) != null) {
                    scheduler.deleteJob("SendToAISContingent", Scheduler.DEFAULT_GROUP);
                }
                scheduler.scheduleJob(job, trigger);
            }
            scheduler.start();
        } catch(Exception e) {
            logger.error("Failed to schedule revise 2.0 service job:", e);
        }
    }
    public static class AISContingentServiceJob implements Job {
        @Override
        public void execute(JobExecutionContext arg0) throws JobExecutionException {
            try {
                RuntimeContext.getAppContext().getBean(ETPMVService.class).sendToAISContingent();
            } catch (JobExecutionException e) {
                throw e;
            } catch (Exception e) {
                logger.error("Failed to send data to AIS Contingent:", e);
            }
        }
    }
}

