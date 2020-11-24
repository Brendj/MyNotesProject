/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.emias.LiberateClientsList;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.ExternalEventNotificationService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.cxf.message.Message;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by a.voinov on 28.10.2019.
 */
@WebService(targetNamespace = "http://ru.axetta.ecafe")
public class EMIASController extends HttpServlet {

    public static final String USED_KEY_FOR_EZD = "ecafe.processor.emias.usedkey";
    public static final String USED_IP_FOR_EZD = "ecafe.processor.emias.usedip";
    public static final String IP_FOR_EZD = "ecafe.processor.emias.ip";
    public static final String KEY_FOR_EZD = "ecafe.processor.emias.key";
    private final Logger logger = LoggerFactory.getLogger(EMIASController.class);

    @Resource
    private WebServiceContext context;

    @WebMethod(operationName = "getLiberateClientsList")
    public List<OrgSummaryResult> getLiberateClientsList(
            @WebParam(name = "LiberateClientsList") List<LiberateClientsList> liberateClientsLists) {

        List<OrgSummaryResult> orgSummaryResults = new ArrayList<>();

        //Контроль безопасности
        if (!validateAccess()) {
            orgSummaryResults.add(new OrgSummaryResult(ResponseItem.ERROR_WRONG_KEY_EMIAS,
                    ResponseItem.ERROR_WRONG_KEY_MESSAGE_EMIAS));
            return orgSummaryResults;
        }

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            for (LiberateClientsList liberateClientsList : liberateClientsLists) {

                if (liberateClientsList.getGuid() == null || liberateClientsList.getIdEventEMIAS() == null
                        || liberateClientsList.getTypeEventEMIAS() == null
                        || liberateClientsList.getDateLiberate() == null) {
                    if (liberateClientsList.getIdEventEMIAS() == null) {
                        orgSummaryResults.add(new OrgSummaryResult(ResponseItem.ERROR_ARGUMENT_NOT_FOUND,
                                ResponseItem.ERROR_ARGUMENT_NOT_FOUND_MESSAGE));
                    } else {
                        orgSummaryResults.add(new OrgSummaryResult(ResponseItem.ERROR_ARGUMENT_NOT_FOUND,
                                ResponseItem.ERROR_ARGUMENT_NOT_FOUND_MESSAGE,
                                liberateClientsList.getIdEventEMIAS() == null ? 0L
                                        : liberateClientsList.getIdEventEMIAS()));
                    }
                    continue;
                }

                Client cl = DAOUtils.findClientByGuid(persistenceSession, liberateClientsList.getGuid());
                if (cl == null) {
                    orgSummaryResults.add(new OrgSummaryResult(ResponseItem.ERROR_CLIENT_NOT_FOUND_EMIAS,
                            ResponseItem.ERROR_CLIENT_NOT_FOUND_MESSAGE_EMIAS, liberateClientsList.getIdEventEMIAS()));
                    continue;
                }

                List<EMIAS> emias = DAOUtils
                        .getEmiasbyidEventEMIAS(liberateClientsList.getIdEventEMIAS(), persistenceSession);

                if (liberateClientsList.getIdEventEMIAS() < 0L || !emias.isEmpty()) {
                    orgSummaryResults.add(new OrgSummaryResult(ResponseItem.ERROR_ARGUMENT_NOT_FOUND,
                            ResponseItem.ERROR_ID_EVENT_EMIAS, liberateClientsList.getIdEventEMIAS()));
                    continue;
                }
                Integer eventsStatus = -1;
                boolean goodIdEmias = true;
                switch (liberateClientsList.getTypeEventEMIAS().intValue()) {
                    case 1://создание освобождения (в том числе продление освобождения)
                        if (liberateClientsList.getStartDateLiberate() == null
                                || liberateClientsList.getEndDateLiberate() == null) {
                            orgSummaryResults.add(new OrgSummaryResult(ResponseItem.ERROR_ARGUMENT_NOT_FOUND,
                                    ResponseItem.ERROR_ARGUMENT_NOT_FOUND_MESSAGE,
                                    liberateClientsList.getIdEventEMIAS()));
                            continue;
                        }
                        DAOUtils.saveEMIAS(persistenceSession, liberateClientsList);
                        eventsStatus = 2;
                        break;
                    case 2:
                        if (liberateClientsList.getIdEventCancelEMIAS() == null)
                        {
                            orgSummaryResults.add(new OrgSummaryResult(ResponseItem.ERROR_EVENT_NOT_FOUND,
                                    ResponseItem.ERROR_CANCEL_EVENT_NOT_FOUND_MESSAGE, liberateClientsList.getIdEventEMIAS()));
                            goodIdEmias = false;
                        } else
                        {
                            DAOUtils.updateEMIAS(persistenceSession, liberateClientsList);
                            eventsStatus = 3;
                        }
                        break;
                    case 3:
                        DAOUtils.saveEMIAS(persistenceSession, liberateClientsList);
                        eventsStatus = 4;
                        break;
                    case 4:
                        if (liberateClientsList.getIdEventCancelEMIAS() == null)
                        {
                            orgSummaryResults.add(new OrgSummaryResult(ResponseItem.ERROR_EVENT_NOT_FOUND,
                                    ResponseItem.ERROR_CANCEL_EVENT_NOT_FOUND_MESSAGE, liberateClientsList.getIdEventEMIAS()));
                            goodIdEmias = false;
                        }
                        else {
                            DAOUtils.updateEMIAS(persistenceSession, liberateClientsList);
                            eventsStatus = 5;
                        }
                        break;
                    default: {
                        orgSummaryResults.add(new OrgSummaryResult(ResponseItem.ERROR_EVENT_NOT_FOUND,
                                ResponseItem.ERROR_EVENT_NOT_FOUND_MESSAGE, liberateClientsList.getIdEventEMIAS()));
                        goodIdEmias = false;
                        continue;
                    }
                }
                if (goodIdEmias) {
                    if (eventsStatus != -1) {
                        logger.info("Старт сервиса по отправке уведомлений в ЕМП для ЕМИАС");
                        ExternalEventVersionHandler handler = new ExternalEventVersionHandler(persistenceSession);
                        ExternalEvent event = new ExternalEvent(cl, cl.getOrg().getShortNameInfoService(), cl.getOrg().getOfficialName(), ExternalEventType.SPECIAL,
                                liberateClientsList.getDateLiberate(), ExternalEventStatus.fromInteger(eventsStatus),
                                handler);
                        persistenceSession.save(event);
                        event.setForTest(false);
                        ExternalEventNotificationService notificationService = RuntimeContext.getAppContext().getBean(ExternalEventNotificationService.class);
                        notificationService.setSTART_DATE(liberateClientsList.getStartDateLiberate());
                        notificationService.setEND_DATE(liberateClientsList.getEndDateLiberate());
                        notificationService.sendNotification(cl, event);
                    }

                    orgSummaryResults.add(new OrgSummaryResult(ResponseItem.OK, ResponseItem.OK_MESSAGE_2,
                            liberateClientsList.getIdEventEMIAS()));
                }
            }
            persistenceTransaction.commit();
            persistenceTransaction = null;

        } catch (Exception e) {
            logger.error("Ошибка при сохранении данных для ЕМИАС", e);
            orgSummaryResults.clear();
            orgSummaryResults.add(new OrgSummaryResult(ResponseItem.ERROR_INTERNAL_EMIAS,
                    ResponseItem.ERROR_INTERNAL_MESSAGE_EMIAS));
            return orgSummaryResults;
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return orgSummaryResults;
    }

    private boolean validateAccess() {
        //Узнаем, нужно ли использовать проверку по ip
        String useip = RuntimeContext.getInstance().getConfigProperties().getProperty(USED_IP_FOR_EZD, "");
        Boolean useips;
        try {
            useips = Boolean.parseBoolean(useip);
        } catch (Exception e) {
            useips = false;
        }
        if (useips) {
            boolean goodIp = false;
            //Проверяем само значение ключа
            String IpsInternal = RuntimeContext.getInstance().getConfigProperties().getProperty(IP_FOR_EZD, "");
            //Список разрешенных ip
            String[] addressList = IpsInternal.split(";");

            //Получаем ip, откуда запрос
            MessageContext jaxwsContext = context.getMessageContext();
            HttpServletRequest httpServletRequest = ((HttpServletRequest) jaxwsContext
                    .get(SOAPMessageContext.SERVLET_REQUEST));
            String ip = httpServletRequest.getRemoteAddr();
            for (String ip1 : addressList) {
                if (ip1.equals(ip)) {
                    goodIp = true;
                    break;
                }
            }
            if (!goodIp) {
                return false;
            }
        }

        //Узнаем, нужно ли использовать ключ для доступа
        String useKey = RuntimeContext.getInstance().getConfigProperties().getProperty(USED_KEY_FOR_EZD, "");
        Boolean usedKey;
        try {
            usedKey = Boolean.parseBoolean(useKey);
        } catch (Exception e) {
            usedKey = false;
        }
        if (usedKey) {
            //Проверяем само значение ключа
            String keyInternal = RuntimeContext.getInstance().getConfigProperties().getProperty(KEY_FOR_EZD, "");
            Map<String, List> headers = (Map<String, List>) context.getMessageContext().get(Message.PROTOCOL_HEADERS);
            List<String> keyInput = headers.get("token");
            try {
                if (keyInput.get(0).equals(keyInternal)) {
                    return true;
                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }
        }
        return true;
    }
}
