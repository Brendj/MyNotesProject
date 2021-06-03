/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.emias;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.ExternalEventNotificationService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.HttpURLConnection;

@Path(value = "")
@Controller
public class EmiasInternal {
    private Logger logger = LoggerFactory.getLogger(EmiasInternal.class);
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Path(value = "kafkaEmias")
    public Response kafkaEmias(@RequestBody InfoForEMPFromKafkaEmias infoForEMPFromKafkaEmias) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            logger.info("Старт сервиса по отправке уведомлений в ЕМП для ЕМИАС от Кафки");
            ExternalEventVersionHandler handler = new ExternalEventVersionHandler(persistenceSession);
            Client cl = DAOUtils.findClientByMeshGuid(persistenceSession, infoForEMPFromKafkaEmias.getMeshGuid());
            ExternalEvent event = new ExternalEvent(cl, cl.getOrg().getShortNameInfoService(), cl.getOrg().getOfficialName(), ExternalEventType.SPECIAL,
                    infoForEMPFromKafkaEmias.getCreate_at(), ExternalEventStatus.fromInteger(infoForEMPFromKafkaEmias.getEventStatus()),
                    handler);
            persistenceSession.save(event);
            event.setForTest(false);
            ExternalEventNotificationService notificationService = RuntimeContext.getAppContext().getBean(ExternalEventNotificationService.class);
            notificationService.setSTART_DATE(infoForEMPFromKafkaEmias.getStart_liberation());
            notificationService.setEND_DATE(infoForEMPFromKafkaEmias.getEnd_liberation());
            notificationService.sendNotification(cl, event);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            return Response.status(HttpURLConnection.HTTP_OK).entity(infoForEMPFromKafkaEmias).build();
        } catch (Exception e) {
            logger.error("Ошибка при отправке уведомлени в ЕМП от ЕМИАС", e);
            return Response.status(HttpURLConnection.HTTP_OK).entity(infoForEMPFromKafkaEmias).build();
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }
}
