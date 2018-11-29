/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.etpmv;

import generated.etp.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.apache.commons.lang.StringUtils;
import org.apache.xerces.dom.ElementNSImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by nuc on 01.11.2018.
 */
@Component
@Scope("singleton")
public class ETPMVService {
    private static final Logger logger = LoggerFactory.getLogger(ETPMVService.class);
    private final int COORDINATE_MESSAGE = 0;
    private final String ISPP_ID = "-063101-";
    private final int PAUSE_IN_MILLIS = 1000;
    public final String BENEFIT_INOE = "0";
    public final String BENEFIT_REGULAR = "1";
    private final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    //public final Set<String> BENEFITS_ETP = new HashSet<String>(Arrays.asList("0", "1", "2", "3", "4", "5"));
    //private final int DAYS_TO_EXPIRE = 5;

    @Async
    public void processIncoming(String message) {
        try {
            int type = getMessageType(message);

            JAXBContext jaxbContext = getJAXBContext(type);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();

            switch (type) {
                case COORDINATE_MESSAGE:
                    processCoordinateMessage(unmarshaller, message);
                    break;
            }

        } catch (Exception e) {
            logger.error("Error in process incoming ETP message: ", e);
            sendToBK(message);
        }
    }

    private void processCoordinateMessage(Unmarshaller unmarshaller, String message) throws Exception {
        long begin_time = System.currentTimeMillis();
        ETPMVDaoService daoService = RuntimeContext.getAppContext().getBean(ETPMVDaoService.class);

        InputStream stream = new ByteArrayInputStream(message.getBytes(Charset.forName("UTF-8")));
        CoordinateMessage coordinateMessage = (CoordinateMessage)unmarshaller.unmarshal(stream);
        CoordinateData coordinateData = coordinateMessage.getCoordinateDataMessage();
        RequestService requestService = coordinateData.getService();
        String serviceNumber = requestService.getServiceNumber();
        logger.info("Incoming ETP message with ServiceNumber = " + serviceNumber);
        if (!serviceNumber.contains(ISPP_ID)) throw new Exception("Wrong ISPP_ID in Service Number");

        daoService.saveEtpPacket(serviceNumber, message);

        RequestServiceForSign requestServiceForSign = coordinateData.getSignService();
        RequestServiceForSign.CustomAttributes customAttributes = requestServiceForSign.getCustomAttributes();

        ElementNSImpl serviceProperties = (ElementNSImpl) customAttributes.getAny();
        String guid = getServicePropertiesValue(serviceProperties, "guid");
        String yavl_lgot = getServicePropertiesValue(serviceProperties, "yavl_lgot");
        String benefit = getServicePropertiesValue(serviceProperties, "benefit");

        if (wrongBenefits(yavl_lgot, benefit)) {
            logger.error("Error in processCoordinateMessage: wrong benefits in packet");
            sendStatus(begin_time, serviceNumber, ApplicationForFoodState.DELIVERY_ERROR, null, "wrong benefits in packet");
            return;
        }

        ArrayOfBaseDeclarant contacts = requestServiceForSign.getContacts();
        BaseDeclarant baseDeclarant = getBaseDeclarant(contacts);
        if (baseDeclarant == null) {
            logger.error("Error in processCoordinateMessage: wrong contacts data");
            sendStatus(begin_time, serviceNumber, ApplicationForFoodState.DELIVERY_ERROR, null, "wrong contacts data");
            return;
        }
        String firstName = ((RequestContact)baseDeclarant).getFirstName();
        String lastName = ((RequestContact)baseDeclarant).getLastName();
        String middleName = ((RequestContact)baseDeclarant).getMiddleName();
        String mobile = Client.checkAndConvertMobile(((RequestContact)baseDeclarant).getMobilePhone());
        if (StringUtils.isEmpty(guid) || StringUtils.isEmpty(firstName) || StringUtils.isEmpty(lastName) || StringUtils.isEmpty(mobile)) {
                logger.error("Error in processCoordinateMessage: not enough data");
                sendStatus(begin_time, serviceNumber, ApplicationForFoodState.DELIVERY_ERROR, null, "not enough data");
                return;
        }
        Client client = DAOService.getInstance().getClientByGuid(guid);
        if (client == null) {
            logger.error("Error in processCoordinateMessage: client not found");
            sendStatus(begin_time, serviceNumber, ApplicationForFoodState.DELIVERY_ERROR, null, "client not found");
            return;
        }
        ApplicationForFood applicationForFood = daoService.findApplicationForFood(guid);

        if (applicationForFood != null) {
            if (!testForApplicationForFoodStatus(applicationForFood)) {
                logger.error("Error in processCoordinateMessage: ApplicationForFood found but status is incorrect");
                sendStatus(begin_time, serviceNumber, ApplicationForFoodState.DELIVERY_ERROR, null, "ApplicationForFood found but status is incorrect");
                return;
            }
        }
        Long _benefit = yavl_lgot.equals(BENEFIT_INOE) ? null : RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).getDSZNBenefit(benefit);
        daoService.createApplicationForGood(client, _benefit, mobile, firstName, middleName, lastName, serviceNumber, ApplicationForFoodCreatorType.PORTAL);
        sendStatus(begin_time, serviceNumber, ApplicationForFoodState.TRY_TO_REGISTER, null);
        begin_time = System.currentTimeMillis();
        sendStatus(begin_time, serviceNumber, ApplicationForFoodState.REGISTERED, null);
        if (yavl_lgot.equals(BENEFIT_INOE)) {
            begin_time = System.currentTimeMillis();
            sendStatus(begin_time, serviceNumber, ApplicationForFoodState.PAUSED, null);
        }
        daoService.updateEtpPacketWithSuccess(serviceNumber);
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

    private boolean testForApplicationForFoodStatus(ApplicationForFood applicationForFood) {
        if (applicationForFood.getStatus().getApplicationForFoodState().equals(ApplicationForFoodState.DELIVERY_ERROR)) return true; //103099
        if (applicationForFood.getStatus().getApplicationForFoodState().equals(ApplicationForFoodState.DENIED)
                && (applicationForFood.getStatus().getDeclineReason().equals(ApplicationForFoodDeclineReason.NO_DOCS)
                || applicationForFood.getStatus().getDeclineReason().equals(ApplicationForFoodDeclineReason.NO_APPROVAL)
                || applicationForFood.getStatus().getDeclineReason().equals(ApplicationForFoodDeclineReason.INFORMATION_CONFLICT))) return true; //1080.1, 1080.2, 1080.3
        return false;
    }

    @Async
    public void sendStatusAsync(long begin_time, String serviceNumber, ApplicationForFoodState status, ApplicationForFoodDeclineReason reason) throws Exception {
        sendStatus(begin_time, serviceNumber, status, reason);
    }

    public void sendStatus(long begin_time, String serviceNumber, ApplicationForFoodState status, ApplicationForFoodDeclineReason reason) throws Exception {
        sendStatus(begin_time, serviceNumber, status, reason, null);
    }

    public void sendStatus(long begin_time, String serviceNumber, ApplicationForFoodState status, ApplicationForFoodDeclineReason reason, String errorMessage) throws Exception {
        logger.info("Sending status to ETP with ServiceNumber = " + serviceNumber);
        String message = createStatusMessage(serviceNumber, status, reason);
        boolean success = false;
        try {
            if (System.currentTimeMillis() - begin_time < PAUSE_IN_MILLIS) {
                Thread.sleep(PAUSE_IN_MILLIS - (System.currentTimeMillis() - begin_time)); //пауза между получением заявления и ответом, или между двумя статусами не менее секунды
            }
            RuntimeContext.getAppContext().getBean(ETPMVClient.class).sendStatus(message);
            success = true;
        } catch (Exception e) {
            logger.error("Error in sendStatus: ", e);
        }
        RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).saveOutgoingStatus(serviceNumber, message, success, errorMessage);
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

    private String createStatusMessage(String serviceNumber, ApplicationForFoodState status, ApplicationForFoodDeclineReason reason) throws Exception {
        ObjectFactory objectFactory = new ObjectFactory();
        CoordinateStatusMessage coordinateStatusMessage = objectFactory.createCoordinateStatusMessage();
        CoordinateStatusData coordinateStatusData = objectFactory.createCoordinateStatusData();
        StatusType statusType = objectFactory.createStatusType();
        statusType.setStatusCode(status.getCode());
        statusType.setStatusTitle(status.getDescription());
        DateFormat format = new SimpleDateFormat(DATE_FORMAT);
        XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(format.format(new Date()));
        calendar.setTimezone(getTimeZoneOffsetInMinutes());
        statusType.setStatusDate(calendar);
        coordinateStatusData.setStatus(statusType);
        coordinateStatusData.setServiceNumber(serviceNumber);
        coordinateStatusMessage.setCoordinateStatusDataMessage(coordinateStatusData);
        if (reason != null) {
            DictionaryItem dictionaryItem = objectFactory.createDictionaryItem();
            dictionaryItem.setCode(reason.getCode().toString());
            dictionaryItem.setName(reason.getDescription());
            coordinateStatusData.setReason(dictionaryItem);
        }
        if (!status.getCode().equals(ApplicationForFoodState.DENIED.getCode())) {
            coordinateStatusData.setNote(status.getNote());
        } else {
            if (null != reason) {
                coordinateStatusData.setNote(reason.getDescription());
            }
        }

        JAXBContext jaxbContext = JAXBContext.newInstance(CoordinateStatusMessage.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        StringWriter sw = new StringWriter();
        marshaller.marshal(coordinateStatusMessage, sw);
        return sw.toString();
    }

    private int getTimeZoneOffsetInMinutes() {
        return TimeZone.getTimeZone("Europe/Moscow").getRawOffset()/60000; //значение часового сдвига в мс выражаем в минутах
    }

    private String getServicePropertiesValue(ElementNSImpl serviceProperties, String elementName) {
        for (int i = 0; i < serviceProperties.getLength(); i++) {
            String nodeName = serviceProperties.getChildNodes().item(i).getNodeName();
            if (nodeName != null && nodeName.equals(elementName)) return serviceProperties.getChildNodes().item(i).getFirstChild().getNodeValue();
        }
        return null;
    }

    public void resendStatuses() {
        if (!RuntimeContext.getInstance().actionIsOnByNode("ecafe.processor.etp.consumer.node") ||
                !RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.etp.isOn", "false").equals("true")) {
            return;
        }
        List<EtpOutgoingMessage> messages = RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).getNotSendedMessages();
        for (EtpOutgoingMessage message : messages) {
            boolean success = false;
            try {
                RuntimeContext.getAppContext().getBean(ETPMVClient.class).sendStatus(message.getEtpMessagePayload());
                success = true;
            } catch (Exception e) {
                logger.error("Error in sendErrorStatus: ", e);
            }
            RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).updateOutgoingStatus(message, success);
        }
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

    private JAXBContext getJAXBContext(int type) throws Exception {
        switch (type) {
            case COORDINATE_MESSAGE:
                return JAXBContext.newInstance(CoordinateMessage.class);
        }
        return null;
    }

    private int getMessageType(String message) throws Exception {
        if (message.startsWith("<ns1:CoordinateMessage")) {
            return COORDINATE_MESSAGE;
        }
        throw new Exception("Unknown message type");
    }

    public int getPauseValue() {
        return PAUSE_IN_MILLIS;
    }
}
