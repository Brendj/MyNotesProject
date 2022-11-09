package ru.axetta.ecafe.processor.core.partner.etpmv;

import generated.etp.*;
import generated.etp.Person;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.DiscountManager;
import ru.axetta.ecafe.processor.core.partner.etpmv.enums.StatusETPMessageType;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.proactive.ProactiveMessage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;


@Component
@Scope("singleton")
public class ETPMVProactiveService {
    private static final Logger logger = LoggerFactory.getLogger(ETPMVProactiveService.class);
    private final int COORDINATE_MESSAGE = 0;
    private final int COORDINATE_STATUS_MESSAGE = 1;
    private final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    private static final String RESPONSIBLE_STATUS_VALUE_PROPERTY = "ecafe.processor.proaktiv.responsible.status.value";
    private static final String RESPONSIBLE_JOB_TITLE_PROPERTY = "ecafe.processor.proaktiv.responsible.job.title";
    private static final String DEPARTMENT_NAME_PROPERTY = "ecafe.processor.proaktiv.department.name";
    private static final String DEPARTMENT_CODE_PROPERTY = "ecafe.processor.proaktiv.department.code";
    private static final String DEPARTMENT_INN_PROPERTY = "ecafe.processor.proaktiv.department.inn";
    private static final String DEPARTMENT_OGRN_PROPERTY = "ecafe.processor.proaktiv.department.ogrn";
    private static final String DEPARTMENT_REGDATE_PROPERTY = "ecafe.processor.proaktiv.department.regdate";
    private static final String DEPARTMENT_SYSTEM_CODE_PROPERTY = "ecafe.processor.proaktiv.department.system.code";

    private final static String RESPONSIBLE_STATUS_VALUE = "ИС ПП";
    private final static String RESPONSIBLE_JOB_TITLE_VALUE = "Исполнитель";
    private static final String DEPARTMENT_NAME = "Департамент образования и науки города Москвы";
    private static final String DEPARTMENT_CODE = "6508";
    private static final String DEPARTMENT_INN = "7719028495";
    private static final String DEPARTMENT_ORGN = "1027700386625";
    private static final String DEPARTMENT_REGDATE = "981417600000";//2001-02-06T00:00:00.000+03:00
    private static final String DEPARTMENT_SYSTEM_CODE = "9000022";
    private static final String PROACTIVE_CODE = "880182";
    private static final String PROACTIVE_NAME = "Проактивное предоставление услуги питания за счет средств бюджета города Москвы";
    private final int PAUSE_IN_MILLIS = 1000;

    JAXBContext jaxbConsumerContext;
    JAXBContext jaxbContext;

    @Async
    public void processIncoming(String message) {
        try {
            int type = getMessageType(message);
            if (type == COORDINATE_STATUS_MESSAGE) {
                CoordinateStatusMessage coordinateStatusMessage = (CoordinateStatusMessage)getCoordinateMessage(message);
                processCoordinateStatusMessage(coordinateStatusMessage, message);
            }
        } catch (Exception e) {
            logger.error("Error in process incoming ETP Proactive message: ", e);
            sendToBK(message);
        }
    }

    private int getMessageType(String message) throws Exception {
        if (message.contains("CoordinateMessage")) {
            return COORDINATE_MESSAGE;
        }
        if (message.contains("CoordinateStatusMessage")) {
            return COORDINATE_STATUS_MESSAGE;
        }
        throw new Exception("Unknown message type");
    }

    private void sendToBK(String message) {
        boolean success = false;
        try {
            RuntimeContext.getAppContext().getBean(ETPProaktivClient.class).addToBKQueue(message);
            success = true;
        } catch (Exception e) {
            logger.error("Error in sendBKStatus: ", e);
        }
        RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).saveBKStatus(message, success);
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

    private void processCoordinateStatusMessage(CoordinateStatusMessage coordinateStatusMessage, String message) throws Exception {
        CoordinateStatusData coordinateStatusData = coordinateStatusMessage.getCoordinateStatusDataMessage();
        String serviceNumber = coordinateStatusData.getServiceNumber();
        logger.info(String.format("Incoming ETP proactive message with ServiceNumber = %s", serviceNumber));
        StatusType statusType = coordinateStatusData.getStatus();
        Integer code = statusType.getStatusCode();
        ETPMVDaoService daoService = RuntimeContext.getAppContext().getBean(ETPMVDaoService.class);
        daoService.saveEtpPacket(serviceNumber, message, ETPMessageType.PROACTIVE);
        ProactiveMessage proactiveMessage = daoService.getProactiveMessages(serviceNumber);

        //Пользователь портала отказывается от ЛП для обучающегося.
        if (StatusETPMessageType.REFUSAL.getCode().equals(code.toString())) {
            daoService.saveProactiveMessageStatus(proactiveMessage, StatusETPMessageType.REFUSAL);
            if (proactiveMessage.getClient() != null) {
                DiscountManager.disableAllDiscounts(proactiveMessage.getClient().getIdOfClient());
                //ИСПП отправляет сообщение порталу об отказе от услуги ЛП пользователем со статусом 1080.1 через очередь ЕТП МВ pp.notification_status_out
                StatusETPMessageType status = StatusETPMessageType.REFUSE_USER;
                status.setFullName(proactiveMessage.getClient().getPerson().getFullName());
                sendStatus(new Date().getTime(), proactiveMessage, status, true);
            } else {
                sendToBK(message);
            }
            return;
        }
        //Если получено сообщения о вручении адресату возможности отказа
        if (StatusETPMessageType.HANDED_TO_THE_ADDRESSEE.getCode().equals(code.toString())) {
            //Делаем проверку только для статуса 8021
            if (StatusETPMessageType.POSSIBLE_REJECTION.equals(proactiveMessage.getStatus()))
            {
                daoService.saveProactiveMessageStatus(proactiveMessage, StatusETPMessageType.HANDED_TO_THE_ADDRESSEE);
            }
            else
            {
                sendToBK(message);
            }
            return;
        }
        //Если адресату сообщение не доставлено, то ИСПП получает и сохраняет статус 1030. Окончание процесса Проактив МоС.
        if (StatusETPMessageType.NOT_DELIVERED_TO_ADDRESSEE.getCode().equals(code.toString())) {
            daoService.saveProactiveMessageStatus(proactiveMessage, StatusETPMessageType.NOT_DELIVERED_TO_ADDRESSEE);
        }
        else {
            //Если адресату сообщение доставлено, то ИСПП получает и сохраняет статус 1040
            if (StatusETPMessageType.DELIVERED_TO_ADDRESSEE.getCode().equals(code.toString())) {
                daoService.saveProactiveMessageStatus(proactiveMessage, StatusETPMessageType.DELIVERED_TO_ADDRESSEE);
                //ИСПП формирует и отправляет порталу сообщение о возможности отказа от услуги со статусом 8021 через очередь ЕТП МВ pp.notification_status_out
                sendStatus(new Date().getTime(), proactiveMessage,  StatusETPMessageType.POSSIBLE_REJECTION, true);
            }
            else
            {
                //если другой статус - вернуть ошибку на портал
                sendToBK(message);
            }
        }
    }

    public void sendMessage(Client client, Client guardian, Integer dtisznCode, String serviceNumber, String ssoid, String fio) {
        logger.info("Sending status to ETP Proactive with ServiceNumber = " + serviceNumber);
        try {
            String msg = createCoordinateMessage(serviceNumber, ssoid, fio);
            RuntimeContext.getAppContext().getBean(ETPProaktivClient.class).sendMessage(msg);
            RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).saveProactiveMessage(client, guardian, dtisznCode, serviceNumber, ssoid);
            RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).saveProactiveOutgoingMessage(serviceNumber, msg, true, "");
        } catch (Exception e) {
            logger.error("Error in sendMessage: ", e);
        }
    }

    private String createCoordinateMessage(String serviceNumber, String ssoid, String fio) throws Exception
    {
        Properties configProperties = RuntimeContext.getInstance().getConfigProperties();
        DateFormat format = new SimpleDateFormat(DATE_FORMAT);
        ObjectFactory objectFactory = new ObjectFactory();
        CoordinateMessage coordinateMessage = objectFactory.createCoordinateMessage();
        CoordinateData coordinateData = objectFactory.createCoordinateData();

        RequestService requestService = objectFactory.createRequestService();
        XMLGregorianCalendar calendarRequest = DatatypeFactory.newInstance().newXMLGregorianCalendar(format.format(new Date()));
        calendarRequest.setTimezone(getTimeZoneOffsetInMinutes());
        requestService.setRegDate(calendarRequest);
        requestService.setServiceNumber(serviceNumber);

        Department department = objectFactory.createDepartment();
        department.setName(configProperties.getProperty(DEPARTMENT_NAME_PROPERTY, DEPARTMENT_NAME));
        department.setCode(configProperties.getProperty(DEPARTMENT_CODE_PROPERTY, DEPARTMENT_CODE));
        department.setInn(configProperties.getProperty(DEPARTMENT_INN_PROPERTY, DEPARTMENT_INN));
        department.setOgrn(configProperties.getProperty(DEPARTMENT_OGRN_PROPERTY, DEPARTMENT_ORGN));
        Long regDateDep;
        try {
            regDateDep = Long.valueOf(configProperties.getProperty(DEPARTMENT_REGDATE_PROPERTY, DEPARTMENT_REGDATE));
        } catch (Exception e)
        {
            regDateDep = Long.valueOf(DEPARTMENT_REGDATE);
        }
        XMLGregorianCalendar calendarDepartment = DatatypeFactory.newInstance().newXMLGregorianCalendar(format.format(new Date(regDateDep)));
        calendarDepartment.setTimezone(getTimeZoneOffsetInMinutes());
        department.setRegDate(calendarDepartment);
        department.setSystemCode(objectFactory.createDepartmentSystemCode(configProperties.getProperty(DEPARTMENT_SYSTEM_CODE_PROPERTY, DEPARTMENT_SYSTEM_CODE)));
        requestService.setDepartment(department);
        requestService.setCreatedByDepartment(department);

        requestService.setOutputKind(OutputKindType.PORTAL);

        coordinateData.setService(requestService);

        RequestServiceForSign requestServiceForSign = objectFactory.createRequestServiceForSign();
        requestServiceForSign.setId("0731");
        DictionaryItem dictionaryItem = objectFactory.createDictionaryItem();
        dictionaryItem.setCode(PROACTIVE_CODE);
        dictionaryItem.setName(PROACTIVE_NAME);
        requestServiceForSign.setServiceType(dictionaryItem);

        RequestContact requestContact = objectFactory.createRequestContact();
        requestContact.setType(ContactType.DECLARANT);
        requestContact.setSsoId(ssoid);
        ArrayOfBaseDeclarant arrayOfBaseDeclarant = objectFactory.createArrayOfBaseDeclarant();
        arrayOfBaseDeclarant.getBaseDeclarant().add(requestContact);
        requestServiceForSign.setContacts(arrayOfBaseDeclarant);

        RequestServiceForSign.CustomAttributes customAttributes = requestServiceForSign.getCustomAttributes();

        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        Document document = documentBuilder.newDocument();
        Node serviceProperties = document.createElement("ServiceProperties");

        Node method_informing = document.createElement("method_informing");

        Element val1 = document.createElement("value");
        val1.appendChild(document.createTextNode("1"));
        method_informing.appendChild(val1);

        Element val2 = document.createElement("value");
        val2.appendChild(document.createTextNode("4"));
        method_informing.appendChild(val2);

        serviceProperties.appendChild(method_informing);
        customAttributes.setAny(serviceProperties);

        coordinateData.setSignService(requestServiceForSign);

        RequestStatus requestStatus = objectFactory.createRequestStatus();

        StatusType statusType = objectFactory.createStatusType();
        statusType.setStatusCode(0);
        statusType.setStatusDate(calendarRequest);
        requestStatus.setStatus(statusType);
        requestStatus.setReasonText("Вашему ребенку (" + fio + ") назначено питание за счет средств бюджета города Москвы, которое будет предоставляться с ближайшего дня поступления в школу питания на вашего ребенка.\n" +
                "При необходимости вы можете отказаться от получения услуги до конца срока действия льготной категории в личном кабинете на Mos.ru.");
        coordinateData.setStatus(requestStatus);

        coordinateMessage.setCoordinateDataMessage(coordinateData);

        JAXBContext jaxbContext = getJAXBContextToSendStatus();
        Marshaller marshaller = jaxbContext.createMarshaller();
        StringWriter sw = new StringWriter();
        marshaller.marshal(coordinateMessage, sw);
        return sw.toString();
    }

    public void sendMSPAssignedMessage(Client client, Client guardian, Integer dtisznCode, String clientFIO, String ssoid) {
        sendMessage(client, guardian, dtisznCode, generateServiceNumber(), ssoid, clientFIO);
    }

    public void sendStatus(long begin_time, ProactiveMessage proactiveMessage, StatusETPMessageType status, Boolean isNotificationStatus) throws Exception {
        String serviceNumber;
        Boolean saveProactiveMessageStatus = true;
        if (proactiveMessage == null) {
            serviceNumber = generateServiceNumber();
            saveProactiveMessageStatus = false;
        }
        else
            serviceNumber = proactiveMessage.getServicenumber();
        logger.info("Sending status to proaktiv ETP with ServiceNumber = " + serviceNumber + ". Status = " + status.getCode());
        String message = createStatusMessage(serviceNumber, status);
        boolean success = false;
        try {
            if (System.currentTimeMillis() - begin_time < PAUSE_IN_MILLIS) {
                Thread.sleep(PAUSE_IN_MILLIS - (System.currentTimeMillis() - begin_time)); //пауза между получением заявления и ответом, или между двумя статусами не менее секунды
            }
            if (isNotificationStatus) {
                RuntimeContext.getAppContext().getBean(ETPProaktivClient.class).sendNotificationStatus(message);
            } else
            {
                RuntimeContext.getAppContext().getBean(ETPProaktivClient.class).sendStatus(message);
            }
            logger.info("Status with ServiceNumber = " + serviceNumber + " sent to proaktiv ETP. Status = " + status.getCode());
            success = true;
        } catch (Exception e) {
            logger.error("Error in sendStatus: ", e);
        }
        RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).saveProactiveOutgoingMessage(serviceNumber, message, success, "");
        if (success && saveProactiveMessageStatus) {
            RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).saveProactiveMessageStatus(proactiveMessage, status);
        }
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

    //порядковый номер обращения (изменяемый) - генерим как select nextval('proaktiv_service_number_seq')
    //<ns2:ServiceNumber>6508-9000022-880182-0000050/22</ns2:ServiceNumber> <!--6508-код ведомства ДОНМ;
    // 9000022-код системы ИС ГУСОЭВ (ИС ПП является подсистемой);
    // 880182-код услуги "Проактивное предоставление услуги питания за счет средств бюджета города Москвы";
    // 0000050-порядковый номер обращения (изменяемый);
    // 22-последние 2 цифры года.-->
    public String generateServiceNumber() {
        Long newNumber = RuntimeContext.getAppContext().getBean(ETPMVDaoService.class).getNextServiceNumber();
        String number = newNumber.toString();
        int size = number.length();
        for (int i = size; i < 7; i++) {
            number = "0" + number;
        }
        Integer year = Calendar.getInstance().get(Calendar.YEAR);
        return DEPARTMENT_CODE + "-" + DEPARTMENT_SYSTEM_CODE + "-" + PROACTIVE_CODE + "-" + number + "/" + (year.toString()).substring(2);
    }
}

