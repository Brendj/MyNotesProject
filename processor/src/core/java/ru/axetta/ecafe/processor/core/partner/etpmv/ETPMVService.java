/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.etpmv;

import generated.etp.*;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.service.RNIPSecuritySOAPHandler;

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
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Date;

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
        if (!serviceNumber.contains(ISPP_ID)) throw new Exception("Wrong ISPP_ID in Service Number");

        daoService.saveEtpPacket(serviceNumber, message);

        RequestServiceForSign requestServiceForSign = coordinateData.getSignService();
        RequestServiceForSign.CustomAttributes customAttributes = requestServiceForSign.getCustomAttributes();

        ElementNSImpl serviceProperties = (ElementNSImpl) customAttributes.getAny();
        String guid = getServicePropertiesValue(serviceProperties, "guid");
        String yavl_lgot = getServicePropertiesValue(serviceProperties, "yavl_lgot");
        String benefit = getServicePropertiesValue(serviceProperties, "benefit");

        ArrayOfBaseDeclarant contacts = requestServiceForSign.getContacts();
        String firstName = null, lastName = null, middleName = null, mobile = null;
        for (BaseDeclarant baseDeclarant : contacts.getBaseDeclarant()) {
            if (!baseDeclarant.getType().equals(ContactType.CHILD)) {
                firstName = ((RequestContact)baseDeclarant).getFirstName();
                lastName = ((RequestContact)baseDeclarant).getLastName();
                middleName = ((RequestContact)baseDeclarant).getMiddleName();
                mobile = Client.checkAndConvertMobile(((RequestContact)baseDeclarant).getMobilePhone());
                break;
            }
        }
        if (StringUtils.isEmpty(guid) || StringUtils.isEmpty(yavl_lgot) || StringUtils.isEmpty(benefit)
            || StringUtils.isEmpty(firstName) || StringUtils.isEmpty(lastName) || StringUtils.isEmpty(middleName) || StringUtils.isEmpty(mobile)) {
                logger.error("Error in processCoordinateMessage: not enough data");
                sendStatus(begin_time, serviceNumber, ApplicationForFoodState.DELIVERY_ERROR, null);
                return;
        }
        Client client = DAOService.getInstance().getClientByGuid(guid);
        if (client == null) {
            logger.error("Error in processCoordinateMessage: client not found");
            sendStatus(begin_time, serviceNumber, ApplicationForFoodState.DELIVERY_ERROR, null);
            return;
        }
        ApplicationForFood applicationForFood = daoService.findApplicationForFood(guid);

        if (applicationForFood != null) {
            if (!testForApplicationForFoodStatus(applicationForFood)) {
                logger.error("Error in processCoordinateMessage: ApplicationForFood found but status is incorrect");
                sendStatus(begin_time, serviceNumber, ApplicationForFoodState.DELIVERY_ERROR, null);
                return;
            }
        }
        daoService.createApplicationForGood(client, Long.parseLong(benefit), mobile, firstName, middleName, lastName, serviceNumber, ApplicationForFoodCreatorType.PORTAL);
        sendStatus(begin_time, serviceNumber, ApplicationForFoodState.TRY_TO_REGISTER, null);
        begin_time = System.currentTimeMillis();
        sendStatus(begin_time, serviceNumber, ApplicationForFoodState.REGISTERED, null);
        daoService.updateEtpPacketWithSuccess(serviceNumber);
    }

    private boolean testForApplicationForFoodStatus(ApplicationForFood applicationForFood) {
        if (applicationForFood.getStatus().getApplicationForFoodState().equals(ApplicationForFoodState.DELIVERY_ERROR)) return true; //103099
        if (applicationForFood.getStatus().getApplicationForFoodState().equals(ApplicationForFoodState.DENIED)
                && (applicationForFood.getStatus().getDeclineReason().equals(ApplicationForFoodDeclineReason.NO_DOCS)
                || applicationForFood.getStatus().getDeclineReason().equals(ApplicationForFoodDeclineReason.NO_APPROVAL)
                || applicationForFood.getStatus().getDeclineReason().equals(ApplicationForFoodDeclineReason.INFORMATION_CONFLICT))) return true; //1080.1, 1080.2, 1080.3
        return false;
    }

    private void sendStatus(long begin_time, String serviceNumber, ApplicationForFoodState status, ApplicationForFoodDeclineReason reason) throws Exception {
        String message = createStatusMessage(serviceNumber, status, reason);
        boolean success = false;
        try {
            if (System.currentTimeMillis() - begin_time < PAUSE_IN_MILLIS) {
                Thread.sleep(PAUSE_IN_MILLIS - (System.currentTimeMillis() - begin_time)); //пауза между получением заявления и ответом, или между двумя статусами не менее секунды
            }
            RuntimeContext.getAppContext().getBean(ETPMVClient.class).sendStatus(message);
            success = true;
        } catch (Exception e) {
            logger.error("Error in sendErrorStatus: ", e);
        }
        RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).saveOutgoingStatus(serviceNumber, message, success);
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
        statusType.setStatusDate(RNIPSecuritySOAPHandler.toXmlGregorianCalendar(new Date()));
        coordinateStatusData.setStatus(statusType);
        coordinateStatusData.setServiceNumber(serviceNumber);
        coordinateStatusMessage.setCoordinateStatusDataMessage(coordinateStatusData);
        if (reason != null) {
            DictionaryItem dictionaryItem = objectFactory.createDictionaryItem();
            dictionaryItem.setCode(reason.getCode().toString());
            dictionaryItem.setName(reason.getDescription());
            coordinateStatusData.setReason(dictionaryItem);
        }

        JAXBContext jaxbContext = JAXBContext.newInstance(CoordinateStatusMessage.class);
        Marshaller marshaller = jaxbContext.createMarshaller();
        StringWriter sw = new StringWriter();
        marshaller.marshal(coordinateStatusMessage, sw);
        return sw.toString();
    }

    private String getServicePropertiesValue(ElementNSImpl serviceProperties, String elementName) {
        for (int i = 0; i < serviceProperties.getLength(); i++) {
            String nodeName = serviceProperties.getChildNodes().item(i).getNodeName();
            if (nodeName != null && nodeName.equals(elementName)) return serviceProperties.getChildNodes().item(i).getFirstChild().getNodeValue();
        }
        return null;
    }

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

}
