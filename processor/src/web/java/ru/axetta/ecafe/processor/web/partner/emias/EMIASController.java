/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.emias;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.emias.LiberateClientsList;
import ru.axetta.ecafe.processor.core.logic.ClientManager;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.ExternalEventNotificationService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
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
import java.text.SimpleDateFormat;
import java.util.*;

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

    @WebMethod(operationName = "getExemptionVisitingList")
    public ExemptionVisitingResult getExemptionVisitingList(
            @WebParam(name = "contractId") Long contractId, @WebParam(name = "guardMobile") String guardMobile) {
        ExemptionVisitingResult exemptionVisitingResult = new ExemptionVisitingResult();
        guardMobile = Client.checkAndConvertMobile(guardMobile);
        if (guardMobile == null) {
            return new ExemptionVisitingResult(ResponseItem.ERROR_INCORRECT_FORMAT_OF_MOBILE, ResponseItem.ERROR_INCORRECT_FORMAT);
        }
        Session session = null;
        Transaction persistenceTransaction = null;
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        try {
            session = runtimeContext.createPersistenceSession();
            persistenceTransaction = session.beginTransaction();

            Client client = DAOUtils.findClientByContractId(session, contractId);
            if (client == null) {
                return new ExemptionVisitingResult (ResponseItem.ERROR_CLIENT_NOT_FOUND, ResponseItem.ERROR_CLIENT_NOT_FOUND_MESSAGE_EMIAS);
            }

            List<Client> guardians = ClientManager.findGuardiansByClient(session, client.getIdOfClient());
            boolean guardianWithMobileFound = false;
            for (Client item : guardians) {
                if (item.getMobile().equals(guardMobile))
                {
                    guardianWithMobileFound = true;
                    break;
                }
            }
            if (!guardianWithMobileFound)
                return new ExemptionVisitingResult (ResponseItem.ERROR_GUARDIAN, ResponseItem.ERROR_GUARDIAN_MESSAGE);
            List<EMIAS> emiasList = DAOReadonlyService.getInstance().getEmiasbyClient(session, client);
            Map<Long, Boolean> dates = new HashMap<>();
            //Находим все даты из промежутка
            for (EMIAS emias: emiasList)
            {
                Date startdate = CalendarUtils.startOfDay(emias.getStartDateLiberate());
                Date enddate = CalendarUtils.startOfDay(emias.getEndDateLiberate());
                List<Long> dates2 = CalendarUtils.daysBetweenInMillis(startdate, enddate);
                for (Long dateStr: dates2)
                {
                    dates.put(dateStr, false);
                }
            }
            //Переопределяем записанные даты
            List<EMIASbyDay> emiaSbyDays = DAOReadonlyService.getInstance().getEmiasbyDayForClient(session, client, null);
            for (EMIASbyDay emiaSbyDay: emiaSbyDays)
            {
                //Добавление только если есть такой дествительный промежуток
                if (dates.containsKey(emiaSbyDay.getDate().getTime()))
                    dates.put(emiaSbyDay.getDate().getTime(), emiaSbyDay.getEat());
            }
            //Сортируем список дат
            SortedSet<Long> keys = new TreeSet<>(dates.keySet());
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            for (Long key : keys) {
                ExemptionVisitingDay exemptionVisitingResultDays = new ExemptionVisitingDay();
                exemptionVisitingResultDays.setDate(sdf.format(new Date(key)));
                exemptionVisitingResultDays.setAgreed(dates.get(key));
                exemptionVisitingResult.getExemptionVisitingResultDays().add(exemptionVisitingResultDays);
            }
            persistenceTransaction = null;
            exemptionVisitingResult.resultCode = ResponseItem.OK;
            exemptionVisitingResult.description = ResponseItem.OK_MESSAGE;
            return exemptionVisitingResult;
        } catch (Exception e) {
            new ExemptionVisitingResult (ResponseItem.ERROR_INTERNAL_EMIAS, ResponseItem.ERROR_INTERNAL_MESSAGE_EMIAS);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(session, logger);
        }
        return exemptionVisitingResult;
    }

    @WebMethod(operationName = "setExemptionVisitingClientList")
    public ExemptionVisitingResult setExemptionVisitingClientList(
            @WebParam(name = "contractId") Long contractId, @WebParam(name = "guardMobile") String guardMobile,
            @WebParam(name = "exemptionVisitingDays") List<ExemptionVisitingDay> exemptionVisitingDays) {
        ExemptionVisitingResult exemptionVisitingResult = new ExemptionVisitingResult();
        guardMobile = Client.checkAndConvertMobile(guardMobile);
        if (guardMobile == null) {
            return new ExemptionVisitingResult (ResponseItem.ERROR_INCORRECT_FORMAT_OF_MOBILE, ResponseItem.ERROR_INCORRECT_FORMAT);
        }
        Session session = null;
        Transaction persistenceTransaction = null;
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        try {
            session = runtimeContext.createPersistenceSession();
            persistenceTransaction = session.beginTransaction();

            Client client = DAOUtils.findClientByContractId(session, contractId);
            if (client == null) {
                return new ExemptionVisitingResult (ResponseItem.ERROR_CLIENT_NOT_FOUND, ResponseItem.ERROR_CLIENT_NOT_FOUND_MESSAGE_EMIAS);
            }

            List<Client> guardians = ClientManager.findGuardiansByClient(session, client.getIdOfClient());
            boolean guardianWithMobileFound = false;
            for (Client item : guardians) {
                if (item.getMobile().equals(guardMobile))
                {
                    guardianWithMobileFound = true;
                    break;
                }
            }
            if (!guardianWithMobileFound)
                return new ExemptionVisitingResult (ResponseItem.ERROR_GUARDIAN, ResponseItem.ERROR_GUARDIAN_MESSAGE);
            SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
            for (ExemptionVisitingDay exemptionVisitingDay: exemptionVisitingDays)
            {
                if ((sdf.parse(exemptionVisitingDay.getDate()).getTime()+1L)<(CalendarUtils.startOfDay(new Date())).getTime())
                    return new ExemptionVisitingResult (ResponseItem.ERROR_INCORRECT_DATE, ResponseItem.ERROR_INCORRECT_DATE_MESSAGE);
            }
            List<EMIAS> emiasList = DAOReadonlyService.getInstance().getEmiasbyClient(session, client);
            List<EMIASbyDay> emiaSbyDays = DAOReadonlyService.getInstance().getEmiasbyDayForClient(session, client, null);
            //Находим все даты из промежутка
            Map<String, Boolean> dates = new HashMap<>();
            //Находим все даты из промежутка
            for (EMIAS emias: emiasList)
            {
                Date startdate = CalendarUtils.startOfDay(emias.getStartDateLiberate());
                Date enddate = CalendarUtils.endOfDay(emias.getEndDateLiberate());
                List<String> dates2 = CalendarUtils.datesBetween(startdate, enddate, 2);

                for (String dateStr: dates2)
                {
                    dates.put(dateStr, false);
                }
            }
            for (ExemptionVisitingDay exemptionVisitingDay: exemptionVisitingDays)
            {
                if (dates.containsKey(exemptionVisitingDay.getDate()))
                {
                    boolean datefind = false;
                    for (EMIASbyDay emiaSbyDay: emiaSbyDays) {
                        //Есть ли эта дата уже в списке в отдельной таблице
                        if (sdf.format(emiaSbyDay.getDate()).equals(exemptionVisitingDay.getDate()))
                        {
                            emiaSbyDay.setEat(exemptionVisitingDay.getAgreed());
                            emiaSbyDay.setVersion(DAOUtils.getMaxVersionOfEmiasbyDay(session) + 1);
                            emiaSbyDay.setIdOfOrg(client.getOrg().getIdOfOrg());
                            emiaSbyDay.setUpdateDate(new Date());
                            session.save(emiaSbyDay);
                            datefind = true;
                            break;
                        }
                    }
                    //Дату не нашли
                    if (!datefind)
                    {
                        EMIASbyDay emiaSbyDay = new EMIASbyDay();
                        emiaSbyDay.setIdOfClient(client.getIdOfClient());
                        emiaSbyDay.setDate(sdf.parse(exemptionVisitingDay.getDate()));
                        emiaSbyDay.setEat(exemptionVisitingDay.getAgreed());
                        emiaSbyDay.setCreateDate(new Date());
                        emiaSbyDay.setUpdateDate(new Date());
                        emiaSbyDay.setVersion(DAOUtils.getMaxVersionOfEmiasbyDay(session) + 1);
                        emiaSbyDay.setIdOfOrg(client.getOrg().getIdOfOrg());
                        session.save(emiaSbyDay);
                    }
                }
            }
            session.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
            exemptionVisitingResult.resultCode = ResponseItem.OK;
            exemptionVisitingResult.description = ResponseItem.OK_MESSAGE;
            return exemptionVisitingResult;
        } catch (Exception e) {
            new ExemptionVisitingResult (ResponseItem.ERROR_INTERNAL_EMIAS, ResponseItem.ERROR_INTERNAL_MESSAGE_EMIAS);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(session, logger);
        }
        return exemptionVisitingResult;
    }

    @WebMethod(operationName = "getLiberateClientsList")
    public List<OrgSummaryResult> getLiberateClientsList(
            @WebParam(name = "LiberateClientsList") List<LiberateClientsList> liberateClientsLists) {
        //Какой алгоритм обработки использовать: старый или новый ?
        Boolean usedOld = Boolean.parseBoolean(RuntimeContext.getInstance().getConfigProperties().getProperty("ecafe.processor.emias.old", "false"));
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
                List<EMIAS> emias;
                if (usedOld) {
                    emias = DAOUtils.getEmiasbyidEventEMIAS(liberateClientsList.getIdEventEMIAS(), persistenceSession);
                    if (liberateClientsList.getIdEventEMIAS() < 0L || !emias.isEmpty()) {
                        orgSummaryResults.add(new OrgSummaryResult(ResponseItem.ERROR_ARGUMENT_NOT_FOUND,
                                ResponseItem.ERROR_ID_EVENT_EMIAS, liberateClientsList.getIdEventEMIAS()));
                        continue;
                    }
                }
                else {
                    emias = DAOUtils.getEmiasbyMeshGuid(cl.getMeshGUID(), persistenceSession);
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
                        if (usedOld)
                            DAOUtils.saveEMIAS(persistenceSession, liberateClientsList);
                        else
                            DAOUtils.saveEMIASkafka(persistenceSession, liberateClientsList);
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
                            if (usedOld)
                                DAOUtils.updateEMIAS(persistenceSession, liberateClientsList);
                            else
                            {
                                for (EMIAS emias1: emias)
                                {
                                    if (emias1.getIdemias().equals(liberateClientsList.getIdEventCancelEMIAS().toString()))
                                    {
                                        DAOUtils.archivedEMIAS(persistenceSession, emias1);
                                    }
                                }
                            }
                            eventsStatus = 3;
                        }
                        break;
                    case 3:
                        if (usedOld)
                            DAOUtils.saveEMIAS(persistenceSession, liberateClientsList);
                        else
                            DAOUtils.saveEMIASkafka(persistenceSession, liberateClientsList);
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
                            if (usedOld)
                                DAOUtils.updateEMIAS(persistenceSession, liberateClientsList);
                            else
                            {
                                for (EMIAS emias1: emias)
                                {
                                    if (emias1.getIdemias().equals(liberateClientsList.getIdEventCancelEMIAS().toString()))
                                    {
                                        DAOUtils.archivedEMIAS(persistenceSession, emias1);
                                    }
                                }
                            }
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
