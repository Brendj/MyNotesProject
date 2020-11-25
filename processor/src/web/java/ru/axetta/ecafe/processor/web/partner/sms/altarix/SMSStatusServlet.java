/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.sms.altarix;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientSms;
import ru.axetta.ecafe.processor.core.persistence.EnterEvent;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sms.DeliveryResponse;
import ru.axetta.ecafe.processor.core.sms.UpdateClientSmsDeliveryStatusJob;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.ClientList;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 24.01.13
 * Time: 18:02
 * To change this template use File | Settings | File Templates.
 */
@WebServlet(
        name = "SMSStatusServlet",
        description = "SMSStatusServlet",
        urlPatterns = {"/sms-status"}
)
public class SMSStatusServlet extends HttpServlet {

    private static final Logger logger = LoggerFactory.getLogger(SMSStatusServlet.class);
    private static final String UPDATE_STATUS_REQUEST = "update";
    private static final String REQUEST_SUBSCRIBER_INFO = "info";


    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String request_type = (String) request.getParameter("request_type");
        String subscriber = (String) request.getParameter("subscriber");

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html; charset=UTF-8");
        response.setHeader("Content-Type", "text/xml; charset=UTF-8");

        if (subscriber == null || subscriber.length() < 1) {
            response.getWriter().write("error=Необходимо указать параметр 'subscriber'");
            return;
        }
        subscriber = Client.checkAndConvertMobile(subscriber);
        if (subscriber == null) {
            response.getWriter().write("error=Указаный телефон подписчика указан не корректно");
            return;
        }


        RuntimeContext runtimeContext = null;
        Session session = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            session = runtimeContext.createPersistenceSession();
        } catch (Exception e) {
            logger.error("Failed to receive session using RuntimeContext");
            response.getWriter().write("error=Внутренняя ошибка, попробуйте повторить попытку позже");
            return;
        }


        try {
            if (request_type.equalsIgnoreCase(UPDATE_STATUS_REQUEST)) {
                String status = (String) request.getParameter("status");
                String idofsms = (String) request.getParameter("idofsms");
                if (status == null || status.length() < 1) {
                    response.getWriter().write("error=Необходимо указать параметр 'status'");
                    return;
                }
                if (idofsms == null || idofsms.length() < 1) {
                    response.getWriter().write("error=Необходимо указать идентификатор SMS сообщения");
                    return;
                }


                Transaction persistenceTransaction = null;
                try {
                    persistenceTransaction = session.beginTransaction();

                    updateSMSStatusRequest(subscriber, idofsms, status, response, session);

                    session.flush();
                    persistenceTransaction.commit();
                    persistenceTransaction = null;
                } catch (Exception e) {
                    logger.error("Failed to update SMS delivery status.", e);
                } finally {
                    HibernateUtils.rollback(persistenceTransaction, logger);
                }
            } else if (request_type.equalsIgnoreCase(REQUEST_SUBSCRIBER_INFO)) {
                requestSubscriberInfo(subscriber, response, session);
            } else {
                response.getWriter().write("error=Ошибка, неизвестный тип запроса");
                return;
            }
        } catch (Exception e) {
            logger.error("Failed to execute SMS service request", e);
            response.getWriter().write("error=Внутренняя ошибка, попробуйте повторить попытку позже");
        } finally {
            HibernateUtils.close(session, logger);
        }
    }

    public void requestSubscriberInfo(String subscriber, HttpServletResponse response, Session session)
            throws Exception {
        StringBuilder responseText = new StringBuilder("");
        List<Long> idOfClients = DAOUtils.extractIDFromGuardByGuardMobile(session,Client.checkAndConvertMobile(subscriber));
        if (idOfClients.isEmpty()) {
            response.getWriter().write("error=Не удалось найти подписчика с таким телефонным номером (" + subscriber + ")");
            return;
        }
        for (Long idOfClient : idOfClients) {
                String nextStr = getSubscriberInfo(idOfClient, session);
                if (nextStr.length() < 1) {
                    continue;
                }
                if (responseText.length() > 1) {
                    responseText.append('\n');
                }
                responseText.append(nextStr);
        }
        response.getWriter().write(responseText.toString());
    }

    public String getSubscriberInfo (Long idOfClient, Session session)
        {
        StringBuilder responseText = new StringBuilder("");
        org.hibernate.Query q = session.createSQLQuery(
                "select cf_persons.firstname, cf_persons.secondname, cf_persons.surname, to_timestamp(cf_enterevents.evtdatetime / 1000), cf_enterevents.passdirection, cf_clients.balance "
                        +
                        "from cf_clients " +
                        "left join cf_persons on cf_persons.idofperson=cf_clients.idofperson " +
                        "left join cf_enterevents on cf_enterevents.idofclient=cf_clients.idofclient and cf_enterevents.evtdatetime<>0 " +
                        "where cf_clients.idofclient=:idofclient " +
                        "order by evtdatetime desc " +
                        "limit 1");
        q.setParameter("idofclient", idOfClient);
        List resultList = q.list();
        if (resultList.size() > 0) {
            Object e[]           = (Object[]) resultList.get(0);
            String firstName     = (String) e[0];
            String secondName    = (String) e[1];
            String surname       = (String) e[2];
            Date evtDate         = e[3] == null ? null : new Date(((Timestamp) e[3]).getTime());
            Integer passTypeCode = e[4] == null ? null : ((Integer) e[4]).intValue();
            Long balance         = e[5] == null ? null : ((BigInteger) e[5]).longValue();
            String passType      = null;
            if (passTypeCode != null) {
                switch (passTypeCode) {
                    case EnterEvent.ENTRY:
                        passType = "Вход";
                        break;
                    case EnterEvent.EXIT:
                        passType = "Выход";
                        break;
                    case EnterEvent.PASSAGE_IS_FORBIDDEN:
                        passType = "Проход запрещен";
                        break;
                    case EnterEvent.TURNSTILE_IS_BROKEN:
                        passType = "Взлом турникета";
                        break;
                    case EnterEvent.EVENT_WITHOUT_PASSAGE:
                        passType = "Событие без прохода";
                        break;
                    case EnterEvent.PASSAGE_RUFUSAL:
                        passType = "Отказ от прохода";
                        break;
                    case EnterEvent.RE_ENTRY:
                        passType = "Повторный вход";
                        break;
                    case EnterEvent.RE_EXIT:
                        passType = "Повторный выход";
                        break;
                    case EnterEvent.QUERY_FOR_ENTER:
                        passType = "Запрос на вход";
                        break;
                    case EnterEvent.QUERY_FOR_EXIT:
                        passType = "Запрос на выход";
                        break;
                    case EnterEvent.DETECTED_INSIDE:
                        passType = "Обнаружен на подносе карты внутри здания";
                        break;
                    case EnterEvent.CHECKED_BY_TEACHER_EXT:
                        passType = "отмечен в классном журнале через внешнюю систему";
                        break;
                    case EnterEvent.CHECKED_BY_TEACHER_INT:
                        passType = "отмечен учителем внутри здания";
                        break;
                }
            }

            responseText.append(firstName).append(" ").
                    append(secondName).append(" ").
                    append(surname).append(" ").
                    append(passType == null ? "" : passType + " ").
                    append(evtDate == null ? "" : parseDate (evtDate) + " ").
                    append(balance == null ? "" : beautifyBalance(balance));
        }
    return responseText.toString();
    }

    public String parseDate (Date evt)
        {
        Calendar event = new GregorianCalendar();
        event.setTimeInMillis(evt.getTime());

        Calendar today = new GregorianCalendar();
        today.setTimeInMillis(System.currentTimeMillis());

        if (today.get(Calendar.YEAR) == event.get(Calendar.YEAR) &&
            today.get(Calendar.MONTH) == event.get(Calendar.MONTH) &&
            today.get(Calendar.DAY_OF_MONTH) == event.get(Calendar.DAY_OF_MONTH))
            {
            return "сегодня " + new SimpleDateFormat("HH:mm").format(evt);
            }

        today.setTimeInMillis(System.currentTimeMillis() - 86400000);

        if (today.get(Calendar.YEAR) == event.get(Calendar.YEAR) &&
            today.get(Calendar.MONTH) == event.get(Calendar.MONTH) &&
            today.get(Calendar.DAY_OF_MONTH) == event.get(Calendar.DAY_OF_MONTH))
            {
            return "вчера " + new SimpleDateFormat("HH:mm").format(evt);
            }

        return new SimpleDateFormat("yyyy.MM.dd HH:mm").format(evt);
        }


    public void updateSMSStatusRequest(String subscriber, String idofsms, String status, HttpServletResponse response,
            Session session) throws Exception {
        ClientSms clientSms = null;
        Criteria clientSmsCriteria = session.createCriteria(ClientSms.class);
        clientSmsCriteria.add(Restrictions.eq("idOfSms", idofsms));
        List clientSmsList = clientSmsCriteria.list();
        if (clientSmsList.size() > 0) {
            clientSms = (ClientSms) clientSmsList.get(0);
        }
        if (clientSms == null) {
            response.getWriter()
                    .write("error=Не удалось найти SMS сообщение с указанным идентификатором (" + idofsms + ")");
            return;
        }


        int statusCode = -1;
        if (status.equals("2")) {
            statusCode = DeliveryResponse.DELIVERED;
        }
        if (status.equals("1")) {
            statusCode = DeliveryResponse.NOT_DELIVERED;
        }
        if (status.equals("0")) {
            statusCode = DeliveryResponse.SENT;
        }
        DeliveryResponse smsResponse = new DeliveryResponse(statusCode, clientSms.getSendTime(),
                new Date(System.currentTimeMillis()));

        UpdateClientSmsDeliveryStatusJob
                .updateSmsDeliveryStatus(new Date(System.currentTimeMillis()), session, clientSms, smsResponse);
        response.getWriter().write("success=Статус SMS сообщения успешно изменен");
    }


    public String beautifyBalance(long balance) {
        String balanceStr = NumberFormat.getCurrencyInstance().format((double) balance / 100);
        return balanceStr;
    }
}