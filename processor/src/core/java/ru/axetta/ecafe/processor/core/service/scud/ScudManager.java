/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.scud;

import generated.spb.SCUD.PushResponse;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;


import java.math.BigInteger;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Component
@Scope("prototype")
public class ScudManager {
    private final Integer LIMIT_RECORDS = getLimitRecords();
    private final String DEFAULT_VALUE = "1";
    private static final Logger logger = LoggerFactory.getLogger(ScudManager.class);
    private final static boolean isOn = isOn();
    private ScudService service = RuntimeContext.getAppContext().getBean(ScudService.class);

    public static boolean isOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String reqInstance = runtimeContext.getConfigProperties().getProperty("ecafe.processor.scudmanager.sendtoexternal", "false");
        return Boolean.getBoolean(reqInstance);
    }

    private static Integer getLimitRecords(){
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String reqInstance = runtimeContext.getConfigProperties().getProperty("ecafe.processor.scudmanager.node", "1");
        return Integer.parseInt(reqInstance);
    }

    public void sendData() {
        if(isOn){
            sendToExternal(LIMIT_RECORDS);
        }
    }

    public void sendToExternal(Integer limitRecords) {
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = RuntimeContext.getInstance().createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            sendEnterEventsToExternal(persistenceSession, limitRecords);

            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Error sending data to SCUD service: ", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    private void sendEnterEventsToExternal(Session session, Integer limitRecords) {
        Integer sendToExternal = null;
        List<EventDataItem> list = null;
        try {
            list = findEnterEvents(session, limitRecords);
            if(list == null || list.isEmpty()){
                throw new Exception("No EnterEventSendInfo records");
            }
            PushResponse response = service.sendEvent(list);
            sendToExternal = 1;
            Integer responseCode = response.isResult()? 1 : 0;
            logger.info("Sending completed, sent list with " + list.size() + " elements, ResultCode is: " + responseCode);
            updateEnterEventsSendInfo(session, list, responseCode, sendToExternal);
        } catch (Exception e){
            logger.error("Can't send record to external: " + e.getMessage());
            sendToExternal = 0;
            updateEnterEventsSendInfo(session, list, 0, sendToExternal);
        }

    }

    private void updateEnterEventsSendInfo(Session session, List<EventDataItem> list, Integer responseCode,
            Integer sendToExternal) {
        try{
            for(EventDataItem element: list){
                Long idofEnterEvent = element.getIdOfEnterEvent().longValue();
                Long idofOrg = element.getIdOfOrg().longValue();
                DAOUtils.updateEnterEventsSendInfo(session, idofEnterEvent, idofOrg, responseCode, sendToExternal);
            }
        } catch (Exception e){
            logger.error("Can't update EnterEventSendInfo records in DB: " + e.getMessage());
        }
    }

    private List<EventDataItem> findEnterEvents(Session session, Integer limitRecords) {
        List<EventDataItem> dataItems = new LinkedList<EventDataItem>();
        try {
            Query query = session.createSQLQuery(
                            "SELECT o.ogrn, oa.idofaccessory, c.clientguid, crd.cardno, ee.passdirection, ee.evtdatetime, ee.idofenterevent, ee.idoforg "
                            + " FROM cf_enterevents_send_info eesi "
                            + " INNER JOIN cf_enterevents ee ON eesi.idofenterevents = ee.idofenterevent "
                            + " INNER JOIN cf_orgs o ON o.idoforg = eesi.idoforg "
                            + " LEFT JOIN cf_clients c ON ee.idofclient = c.idofclient "
                            + " LEFT JOIN cf_cards crd ON ee.idofcard = crd.idofcard "
                            + " LEFT JOIN cf_org_accessories oa ON ee.turnstileaddr = oa.accessorynumber "
                            + " WHERE eesi.sendtoexternal = 0 or eesi.responscode = 0 "
                            + " ORDER BY eesi.evtdatetime DESC "
                            + " LIMIT :limit ");
            query.setParameter("limit", limitRecords);
            List<Object[]> result = query.list();
            for(Object[] row : result ){
                String studentUid = null;
                Long cardUid = null;
                String ogrn = row[0] == null? DEFAULT_VALUE :(String) row[0];
                String turnstile = row[1] == null? DEFAULT_VALUE : String.valueOf(row[1]);
                if(row[2] == null && row[3] == null){
                    continue;
                } else if(row[2] == null) {
                    studentUid = DAOUtils.findClientGUIDByCardNo(session, (Long)row[3]);
                    if(studentUid == null) studentUid = DEFAULT_VALUE;
                } else if(row[3] == null) {
                    cardUid = DAOUtils.findCardNoByClientGUID(session, (String) row[2]);
                    if(cardUid == null) cardUid = Long.parseLong(DEFAULT_VALUE);
                } else {
                    studentUid = (String) row[2];
                    BigInteger buf = (BigInteger) row[3];
                    cardUid = buf.longValue();
                }
                Integer passDirection = (Integer) row[4];
                Date eventDate = new Date((Long)row[5]);
                BigInteger idofEnterEvent = (BigInteger) row[6];
                BigInteger idofOrg = (BigInteger) row[7];
                EventDataItem item = new EventDataItem(ogrn, null, turnstile,
                        studentUid, cardUid, passDirection, eventDate, idofEnterEvent, idofOrg);
                dataItems.add(item);
            }
            return dataItems;
        } catch (Exception e){
            logger.error("Can't get records for sent to SCUD: " + e.getMessage());
            return null;
        }
    }
}
