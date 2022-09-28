package ru.axetta.ecafe.processor.core.partner.etpmv;

import generated.etp.*;
import generated.etp.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.partner.etpmv.enums.StatusETPMessageType;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.proactive.ProactiveMessage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
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
import java.util.Properties;
import java.util.TimeZone;


@Component
@Scope("singleton")
public class ETPMVProactiveService {
    private static final Logger logger = LoggerFactory.getLogger(ETPMVProactiveService.class);
    private final int COORDINATE_MESSAGE = 0;
    private final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    private static final String RESPONSIBLE_STATUS_VALUE_PROPERTY = "ecafe.processor.proaktiv.responsible.status.value";
    private static final String RESPONSIBLE_JOB_TITLE_PROPERTY = "ecafe.processor.proaktiv.responsible.job.title";
    private static final String DEPARTMENT_NAME_PROPERTY = "ecafe.processor.proaktiv.department.name";
    private static final String DEPARTMENT_CODE_PROPERTY = "ecafe.processor.proaktiv.department.code";
    private static final String DEPARTMENT_INN_PROPERTY = "ecafe.processor.proaktiv.department.inn";
    private static final String DEPARTMENT_OGRN_PROPERTY = "ecafe.processor.proaktiv.department.ogrn";
    private static final String DEPARTMENT_SYSTEM_CODE_PROPERTY = "ecafe.processor.proaktiv.department.system.code";

    private final static String RESPONSIBLE_STATUS_VALUE = "ИС ПП";
    private final static String RESPONSIBLE_JOB_TITLE_VALUE = "Исполнитель";
    private static final String DEPARTMENT_NAME = "Департамент образования и науки города Москвы";
    private static final String DEPARTMENT_CODE = "6508";
    private static final String DEPARTMENT_INN = "7719028495";
    private static final String DEPARTMENT_ORGN = "1027700386625";
    private static final String DEPARTMENT_SYSTEM_CODE = "9000022";
    private final int PAUSE_IN_MILLIS = 1000;

    JAXBContext jaxbConsumerContext;
    JAXBContext jaxbContext;

    @Async
    public void processIncoming(String message) {
        try {
            int type = getMessageType(message);
            if (type == COORDINATE_MESSAGE) {
                CoordinateMessage coordinateMessage = (CoordinateMessage) getCoordinateMessage(message);
                processCoordinateMessage(coordinateMessage, message);
            }
        } catch (Exception e) {
            logger.error("Error in process incoming ETP Proactive message: ", e);
        }
    }

    private int getMessageType(String message) throws Exception {
        if (message.contains(":CoordinateMessage")) {
            return COORDINATE_MESSAGE;
        }
        throw new Exception("Unknown message type");
    }


    public Object getCoordinateMessage(String message) throws Exception {
        JAXBContext jaxbContext = getJAXBContext();
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        InputStream stream = new ByteArrayInputStream(message.getBytes(Charset.forName("UTF-8")));
        return unmarshaller.unmarshal(stream);
    }

    private JAXBContext getJAXBContext() throws Exception {
        if (jaxbConsumerContext == null) {
            jaxbConsumerContext = JAXBContext.newInstance(CoordinateMessage.class, CoordinateStatusMessage.class);
        }
        return jaxbConsumerContext;
    }

    private void processCoordinateMessage(CoordinateMessage coordinateMessage, String message) throws Exception {
        ETPMVDaoService daoService = RuntimeContext.getAppContext().getBean(ETPMVDaoService.class);

        CoordinateData coordinateData = coordinateMessage.getCoordinateDataMessage();
        RequestService requestService = coordinateData.getService();
        String serviceNumber = requestService.getServiceNumber();
        logger.info("Incoming ETP Proactive message with ServiceNumber = " + serviceNumber);
        RequestServiceForSign requestServiceForSign = coordinateData.getSignService();
        ArrayOfBaseDeclarant arrayOfBaseDeclarant = requestServiceForSign.getContacts();
        String ssoid = arrayOfBaseDeclarant.getBaseDeclarant().get(0).getSsoId();
        ProactiveMessage proactiveMessage = daoService.getProactiveMessage(serviceNumber, ssoid);
        if (proactiveMessage == null)
        {
            logger.info("Not found proactiveMessage for ServiceNumber = " + serviceNumber + " and ssoid = " + ssoid);
            return;
        }

        RequestStatus requestStatus = coordinateData.getStatus();
        Integer statusCode = requestStatus.getStatus().getStatusCode();
        StatusETPMessageType newStatus = StatusETPMessageType.findStatusETPMessageType(statusCode.toString());
        //Сохранили новый статус
        daoService.saveProactiveMessageStatus(proactiveMessage, newStatus);
    }

    public void sendMessage(Client client, Client guardian, String serviceNumber, String ssoid) {
        logger.info("Sending status to ETP Proactive with ServiceNumber = " + serviceNumber);
        try {
            RuntimeContext.getAppContext().getBean(ETPProaktivClient.class).sendMessage(serviceNumber);
            RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).saveProactiveMessage(client, guardian, serviceNumber, ssoid);
        } catch (Exception e) {
            logger.error("Error in sendMessage: ", e);
        }
    }

    public void sendMSPAssignedMessage(Client client, Client guardian, String ssoid) {

    }

    public void sendStatus(long begin_time, String serviceNumber, StatusETPMessageType status) throws Exception {
        serviceNumber = serviceNumber == null ? generateServiceNumber() : serviceNumber;
        logger.info("Sending status to proaktiv ETP with ServiceNumber = " + serviceNumber + ". Status = " + status.getCode());
        String message = createStatusMessage(serviceNumber, status);
        boolean success = false;
        try {
            if (System.currentTimeMillis() - begin_time < PAUSE_IN_MILLIS) {
                Thread.sleep(PAUSE_IN_MILLIS - (System.currentTimeMillis() - begin_time)); //пауза между получением заявления и ответом, или между двумя статусами не менее секунды
            }
            RuntimeContext.getAppContext().getBean(ETPProaktivClient.class).sendStatus(message);
            logger.info("Status with ServiceNumber = " + serviceNumber + " sent to proaktiv ETP. Status = " + status.getCode());
            success = true;
        } catch (Exception e) {
            logger.error("Error in sendStatus: ", e);
        }
        //RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).saveOutgoingStatus(serviceNumber, message, success, errorMessage, new ApplicationForFoodStatus(status));
    }

    private String createStatusMessage(String serviceNumber, StatusETPMessageType status) throws Exception {
        Properties configProperties = RuntimeContext.getInstance().getConfigProperties();
        ObjectFactory objectFactory = new ObjectFactory();
        CoordinateStatusMessage coordinateStatusMessage = objectFactory.createCoordinateStatusMessage();
        CoordinateStatusData coordinateStatusData = objectFactory.createCoordinateStatusData();
        DateFormat format = new SimpleDateFormat(DATE_FORMAT);
        XMLGregorianCalendar calendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(format.format(new Date()));
        calendar.setTimezone(getTimeZoneOffsetInMinutes());
        coordinateStatusData.setResponseDate(objectFactory.createCoordinateStatusDataResponseDate(calendar));

        StatusType statusType = objectFactory.createStatusType();
        statusType.setStatusCode(status.getPureCode());
        statusType.setStatusTitle(status.getDescription());
        statusType.setStatusDate(calendar);
        coordinateStatusData.setStatus(statusType);

        if (serviceNumber == null) {
            serviceNumber = generateServiceNumber();
        }
        coordinateStatusData.setServiceNumber(serviceNumber);

        Person responsible = objectFactory.createPerson();
        responsible.setLastName(configProperties.getProperty(RESPONSIBLE_STATUS_VALUE_PROPERTY, RESPONSIBLE_STATUS_VALUE));
        responsible.setFirstName(configProperties.getProperty(RESPONSIBLE_STATUS_VALUE_PROPERTY, RESPONSIBLE_STATUS_VALUE));
        responsible.setMiddleName(configProperties.getProperty(RESPONSIBLE_STATUS_VALUE_PROPERTY, RESPONSIBLE_STATUS_VALUE));
        responsible.setJobTitle(configProperties.getProperty(RESPONSIBLE_JOB_TITLE_PROPERTY, RESPONSIBLE_JOB_TITLE_VALUE));
        coordinateStatusData.setResponsible(responsible);

        coordinateStatusData.setNote(status.getNote());

        coordinateStatusMessage.setCoordinateStatusDataMessage(coordinateStatusData);
        if (status.getReason() != null) {
            DictionaryItem dictionaryItem = objectFactory.createDictionaryItem();
            dictionaryItem.setCode(status.getReason());
            dictionaryItem.setName(status.getDescription());
            coordinateStatusData.setReason(dictionaryItem);
        }
        Department department = objectFactory.createDepartment();
        department.setName(configProperties.getProperty(DEPARTMENT_NAME_PROPERTY, DEPARTMENT_NAME));
        department.setCode(configProperties.getProperty(DEPARTMENT_CODE_PROPERTY, DEPARTMENT_CODE));
        department.setInn(configProperties.getProperty(DEPARTMENT_INN_PROPERTY, DEPARTMENT_INN));
        department.setOgrn(configProperties.getProperty(DEPARTMENT_OGRN_PROPERTY, DEPARTMENT_ORGN));
        department.setSystemCode(objectFactory.createDepartmentSystemCode(configProperties.getProperty(DEPARTMENT_SYSTEM_CODE_PROPERTY, DEPARTMENT_SYSTEM_CODE)));
        coordinateStatusData.setDepartment(department);

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

    public String generateServiceNumber() {
        return "";
        //порядковый номер обращения (изменяемый) - генерим как select nextval('proaktiv_service_number_seq')

        //<ns2:ServiceNumber>6508-9000022-880182-0000050/22</ns2:ServiceNumber> <!--6508-код ведомства ДОНМ;
        // 9000022-код системы ИС ГУСОЭВ (ИС ПП является подсистемой);
        // 880182-код услуги "Проактивное предоставление услуги питания за счет средств бюджета города Москвы";
        // 0000050-порядковый номер обращения (изменяемый);
        // 22-последние 2 цифры года.-->
    }
}

