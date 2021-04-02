/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.sms.emp.type.EMPEventType;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceContext;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 12.12.14
 * Time: 17:59
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("singleton")
public class SMSResendingService {
    private static Logger logger = LoggerFactory.getLogger(SMSService.class);

    @PersistenceContext(unitName = "processorPU")
    private javax.persistence.EntityManager em;

    @PersistenceContext(unitName = "reportsPU")
    private javax.persistence.EntityManager rem;    //readonly

    public SMSResendingService() {
    }

    public boolean isOn() {
        return RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_SMS_RESENDING_ON);
    }

    public void executeResending() {
        if (!RuntimeContext.getInstance().isMainNode() || !isOn()) {
            return;
        }

        List<ClientSmsResending> items = getInstance().loadResendings();
        processResendings(items);
    }

    @Transactional
    public List<ClientSmsResending> loadResendings() {
        Session session = rem.unwrap(Session.class);
        List<ClientSmsResending> resendings = getInstance().loadResendingMessages(session);
        return resendings;
    }

    protected void processResendings(List<ClientSmsResending> resendings) {
        if (resendings != null && resendings.size() > 0) {
            SecurityJournalProcess process = SecurityJournalProcess.createJournalRecordStart(
                SecurityJournalProcess.EventType.SMS_RESENDING, new Date());
            process.saveWithSuccess(true);
        }
        boolean isSuccessEnd = true;
        boolean resultProcess;
        for(ClientSmsResending resending : resendings) {
            resultProcess = getInstance().processResendingMessage(resending);
            isSuccessEnd = isSuccessEnd && resultProcess;
        }
        if (resendings != null && resendings.size() > 0) {
            SecurityJournalProcess processEnd = SecurityJournalProcess.createJournalRecordEnd(
                    SecurityJournalProcess.EventType.SMS_RESENDING, new Date());
            processEnd.saveWithSuccess(isSuccessEnd);
        }
    }

    public List<ClientSmsResending> loadResendingMessages(Session session) {
        Query q = session.createQuery("from ClientSmsResending order by lastResendingDate desc");
        List<ClientSmsResending> res = q.list();
        if(res == null) {
            return Collections.EMPTY_LIST;
        }
        return res;
    }

    @Transactional
    public boolean processResendingMessage(ClientSmsResending clientSmsResending) {
        Session session = em.unwrap(Session.class);
        clientSmsResending = (ClientSmsResending) session.merge(clientSmsResending);

        Client cl = clientSmsResending.getClient();
        String[] params = unwrapParams(clientSmsResending.getParamsContents());
        String evtType = getEventType(clientSmsResending, params);
        Integer direction = getEventDirection(clientSmsResending, params);
        Client guardian = getEventGuardian(clientSmsResending, params);

        if(guardian != null) {
            params = EventNotificationService.attachGuardianIdToValues(guardian.getIdOfClient(), params);
        }
        params = EventNotificationService.attachEventDirectionToValues(direction, params);
        params = EventNotificationService.attachTargetIdToValues(clientSmsResending.getContentsId(), params);
        params = EventNotificationService.attachToValues("resending", "true", params);

        boolean success = false;
        try {
            RuntimeContext.getAppContext().getBean(EventNotificationService.class).sendNotification(cl, null, evtType, params, direction, guardian, false, clientSmsResending.getEventTime());
            success = true;
        } catch (RuntimeException re) {
            success = false;
        } catch (Exception e) {
            logger.error("Failed to resend SMS event", e);
            success = false;
        }


        if(success) {
            getInstance().removeResending(clientSmsResending);
        } else {
            clientSmsResending.setLastResendingDate(new Date(System.currentTimeMillis()));
            session.save(clientSmsResending);
        }
        return success;
    }

    public static SMSResendingService getInstance() {
        return RuntimeContext.getAppContext().getBean(SMSResendingService.class);
    }

    public ClientSmsResending addResending(String idOfSms, Client client, String phone,
                                           String serviceName, Long contentsId, Integer contentsType,
                                           Object contents, String[] values, Date eventTime) {
        if (!RuntimeContext.getInstance().isMainNode() || !isOn()) {
            return null;
        }
        if(idOfSms == null || StringUtils.isBlank(idOfSms)) {
            throw new IllegalArgumentException("SMS identity can't be null or empty");
        }
        if(client == null) {
            throw new IllegalArgumentException("Client can't be null");
        }
        if(phone == null || StringUtils.isBlank(phone)) {
            throw new IllegalArgumentException("Phone can't be null or empty");
        }
        if(contentsId == null) {
            throw new IllegalArgumentException("Event target ID can't be null");
        }
        if(contentsType == null) {
            throw new IllegalArgumentException("Event type can't be null");
        }

        String textContents = null;
        String paramsContents = null;
        if(contents instanceof EMPEventType) {
            EMPEventType empEvent = (EMPEventType) contents;
            textContents = empEvent.buildText();
            Map<String, String> params = mergeParameters(empEvent.getParameters(), values);
            paramsContents = wrapParams(params);
        } else if(contents instanceof String) {
            textContents = contents.toString();
            paramsContents = "";
        }

        boolean isResending = EventNotificationService.findBooleanValueInParams(new String [] {"resending"}, values);
        if(isResending) {
            return null;
        }
        Date currentDate = new Date(System.currentTimeMillis());
        ClientSmsResending clientSmsResending = new ClientSmsResending
                                                (idOfSms, 1L, client, phone, serviceName, contentsId, contentsType,
                                                 textContents, paramsContents, currentDate, currentDate, eventTime, client.getOrg().getIdOfOrg());
        clientSmsResending.setNodeName(RuntimeContext.getInstance().getNodeName());
        try {
            clientSmsResending = getInstance().saveResending(clientSmsResending);
        } catch (Exception e) {
            logger.error("Failed to save sms resending entity", e);
        }
        return clientSmsResending;
    }

    @Transactional
    public ClientSmsResending saveResending(ClientSmsResending resending) {
        ClientSmsResending obj = em.merge(resending);
        return obj;
    }

    @Transactional
    public void removeResending(ClientSmsResending clientSmsResending) {
        if(clientSmsResending == null) {
            return;
        }
        em.remove(clientSmsResending);
    }

    protected static String wrapParams(Map<String, String> params) {
        StringBuilder str = new StringBuilder();
        for(String k : params.keySet()) {
            String v = params.get(k);
            if(str.length() > 0) {
                str.append(";");
            }
            str.append(String.format("[%s=%s]", k, v));
        }
        return str.toString();
    }

    protected static String[] unwrapParams(String params) {
        Map<String, String> mp = null;
        String pairsList[] = params.split(";");
        for(String pair : pairsList) {
            if(!pair.startsWith("[") || !pair.endsWith("]")) {
                continue;
            }
            String[] cont = pair.split("=");
            String k = cont[0].substring(1);
            String v = cont[1].substring(0, cont[1].length() - 1);
            if(k == null || v == null || StringUtils.isBlank(k) || StringUtils.isBlank(v)) {
                continue;
            }
            if(mp == null) {
                mp = new HashMap<String, String>();
            }
            mp.put(k, v);
        }

        if(mp == null || mp.size() < 1) {
            return new String [0];
        }

        String[] res = new String[mp.size() * 2];
        int i = 0;
        for(String k : mp.keySet()) {
            String v = mp.get(k);
            res[i] = k;
            res[i + 1] = v;
            i += 2;
        }
        return res;
    }

    protected String getEventType(ClientSmsResending clientSmsResending, String[] params) {
        Integer type = clientSmsResending.getContentsType();
        if(type == null) {
            return null;
        }

        String res = null;
        Long guardianId = EventNotificationService.getGuardianIdFromValues(params);
        if(type == ClientSms.TYPE_ENTER_EVENT_NOTIFY) {
            if(guardianId == null) {
                res = EventNotificationService.NOTIFICATION_ENTER_EVENT;
            } else {
                res = EventNotificationService.NOTIFICATION_PASS_WITH_GUARDIAN;
            }
        } else if(type == ClientSms.TYPE_PAYMENT_REGISTERED) {
            res = EventNotificationService.NOTIFICATION_BALANCE_TOPUP;
        } else if(type == ClientSms.TYPE_LINKING_TOKEN) {
            res = EventNotificationService.MESSAGE_LINKING_TOKEN_GENERATED;
        } else if(type == ClientSms.TYPE_PAYMENT_NOTIFY) {
            res = EventNotificationService.MESSAGE_PAYMENT;
        } else if(type == ClientSms.TYPE_SMS_SUBSCRIPTION_FEE) {
            res = EventNotificationService.NOTIFICATION_SMS_SUBSCRIPTION_FEE;
        } else if(type == ClientSms.TYPE_SMS_SUB_FEE_WITHDRAW) {
            res = EventNotificationService.NOTIFICATION_SMS_SUB_FEE_WITHDRAW_SUCCESS;
            res = EventNotificationService.NOTIFICATION_SMS_SUB_FEE_WITHDRAW_NOT_SUCCESS;
        } else if(type == ClientSms.TYPE_SUBSCRIPTION_FEEDING) {
            res = EventNotificationService.NOTIFICATION_SUBSCRIPTION_FEEDING;
        } else if(type == ClientSms.TYPE_SUBSCRIPTION_FEEDING_WITHDRAW_NOT_SUCCESS) {
            res = EventNotificationService.NOTIFICATION_SUBSCRIPTION_FEEDING_WITHDRAW_NOT_SUCCESS;
        }
        return res;
    }

    protected Map<String, String> mergeParameters(Map<String, String> params, String[] values) {
        if((params == null || params.size() < 1) && (values == null || values.length < 1)) {
            return Collections.EMPTY_MAP;
        }

        Map<String, String> res = new HashMap<String, String>();
        res.putAll(params);

        for (int n = 0; n < values.length; n += 2) {
            String k = values[n];
            String v = values[n + 1];
            res.put(k, v);
        }

        return res;
    }

    protected Integer getEventDirection(ClientSmsResending clientSmsResending, String[] params) {
        Integer direction = EventNotificationService.getEventDirectionFromValues(params);
        return direction;
    }

    protected Client getEventGuardian(ClientSmsResending clientSmsResending, String[] params) {
        Long idOfGuardian = EventNotificationService.getGuardianIdFromValues(params);
        if(idOfGuardian == null) {
            return null;
        }

        Client cl = DAOService.getInstance().findClientById(idOfGuardian);
        return cl;
    }
}