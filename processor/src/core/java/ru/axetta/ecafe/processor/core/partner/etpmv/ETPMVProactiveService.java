package ru.axetta.ecafe.processor.core.partner.etpmv;

import generated.etp.*;
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
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;


@Component
@Scope("singleton")
public class ETPMVProactiveService {
    private static final Logger logger = LoggerFactory.getLogger(ETPMVProactiveService.class);
    private final int COORDINATE_MESSAGE = 0;

    JAXBContext jaxbConsumerContext;

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

    private String generateServiceNumber() {
        //порядковый номер обращения (изменяемый) - генерим как select nextval('proaktiv_service_number_seq')

        //<ns2:ServiceNumber>6508-9000022-880182-0000050/22</ns2:ServiceNumber> <!--6508-код ведомства ДОНМ;
        // 9000022-код системы ИС ГУСОЭВ (ИС ПП является подсистемой);
        // 880182-код услуги "Проактивное предоставление услуги питания за счет средств бюджета города Москвы";
        // 0000050-порядковый номер обращения (изменяемый);
        // 22-последние 2 цифры года.-->
    }
}

