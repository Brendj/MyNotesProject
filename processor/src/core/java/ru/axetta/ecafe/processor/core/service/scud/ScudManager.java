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
import java.util.*;

@Component
@Scope("prototype")
public class ScudManager {
    private final Integer LIMIT_RECORDS = getLimitRecords();
    private final String DEFAULT_VALUE = "1";
    private static final Logger logger = LoggerFactory.getLogger(ScudManager.class);
    private final static boolean isOn = isOn();
    private ScudService service = RuntimeContext.getAppContext().getBean(ScudService.class);
    private final String SCUD_ENDPOINT_ADDRESS = getScudEndPointAdressFromConfig();

    private String getScudEndPointAdressFromConfig() {
        Properties properties = RuntimeContext.getInstance().getConfigProperties();
        return properties
                .getProperty("ecafe.processor.scudmanager.mainendpointaddress", "http://10.146.136.36/service/webservice/scud");
    }

    public static boolean isOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String reqInstance = runtimeContext.getConfigProperties().getProperty("ecafe.processor.scudmanager.sendtoexternal", "false");
        return Boolean.parseBoolean(reqInstance);
    }

    private static Integer getLimitRecords(){
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String reqInstance = runtimeContext.getConfigProperties().getProperty("ecafe.processor.scudmanager.samplesize", "1000");
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
        Boolean sendToExternal = false;
        List<EventDataItem> list = null;
        try {
            list = findEnterEvents(session, limitRecords);
            if(list == null || list.isEmpty()){
                logger.warn("No EnterEventSendInfo records for send to SCUD");
                return;
            }
            HashMap<String, PushResponse> responses = service.sendEvent(list);
            PushResponse response = responses.get(SCUD_ENDPOINT_ADDRESS);
            if(response == null){
                throw new Exception("Response from SCUD is null");
            }
            sendToExternal = true;
            Integer responseCode = response.isResult()? 1 : 0;
            logger.info("Sending EnterEvent to SCUD completed, sent list with " + list.size() + " elements, ResultCode is: " + responseCode);
            updateEnterEventsSendInfo(session, list, response.isResult(), sendToExternal);
        } catch (Exception e){
            logger.error("Can't send record to external: ", e);
            sendToExternal = false;
            updateEnterEventsSendInfo(session, list, false, sendToExternal);
        }

    }

    private void updateEnterEventsSendInfo(Session session, List<EventDataItem> list, Boolean result,
            Boolean sendToExternal){
        try {
            if(list == null || list.isEmpty()){
                throw  new Exception("List of EnterEventsSendInfo is null or empty");
            }
            for(EventDataItem element: list){
                Long idofEnterEvent = element.getIdOfEnterEvent().longValue();
                Long idofOrg = element.getIdOfOrg().longValue();
                DAOUtils.updateEnterEventsSendInfo(session, idofEnterEvent, idofOrg, result, sendToExternal);
            }
        } catch (Exception e){
            logger.error("Can't update EnterEventSendInfo records in DB: ", e);
        }
    }

    private List<EventDataItem> findEnterEvents(Session session, Integer limitRecords) {
        List<EventDataItem> dataItems = new LinkedList<EventDataItem>();
        try {
            Query query = session.createSQLQuery(
                            "SELECT o.ogrn, oa.idofaccessory, c.clientguid, crd.cardno, eesi.directiontype, ee.evtdatetime, ee.idofenterevent, ee.idoforg "
                            + " FROM cf_enterevents_send_info eesi "
                            + " INNER JOIN cf_enterevents ee ON eesi.idofenterevent = ee.idofenterevent and eesi.idoforg = ee.idoforg "
                            + " INNER JOIN cf_orgs o ON eesi.idoforg = o.idoforg "
                            + " LEFT JOIN cf_clients c ON eesi.idofclient = c.idofclient "
                            + " LEFT JOIN cf_cards crd ON eesi.idofcard = crd.idofcard "
                            + " LEFT JOIN cf_org_accessories oa ON ee.turnstileaddr = oa.accessorynumber "
                            + " AND idofsourceorg = eesi.idoforg "
                            + " WHERE ( eesi.sendtoexternal = 0 OR eesi.responsecode = 0 ) "
                            + " AND (eesi.idofclient IS NOT NULL OR eesi.idofcard IS NOT NULL) "
                            + " ORDER BY eesi.evtdatetime DESC "
                            + " LIMIT :limit ");
            query.setParameter("limit", limitRecords);
            List<Object[]> result = query.list();
            for(Object[] row : result ){
                String studentUid = null;
                Long cardUid = null;
                String ogrn = stringIsNullOrEmpty((String) row[0]) ? DEFAULT_VALUE :(String) row[0];
                String turnstile = row[1] == null? DEFAULT_VALUE : String.valueOf(row[1]);
                if(row[2] == null && row[3] == null){
                    continue;
                } else if(row[2] == null) {
                    studentUid = DAOUtils.findClientGUIDByCardNo(session, ((BigInteger)row[3]).longValue());
                    cardUid = ((BigInteger)row[3]).longValue();
                    if(studentUid == null) studentUid = DEFAULT_VALUE;
                } else if(row[3] == null) {
                    cardUid = DAOUtils.findCardNoByClientGUID(session, (String) row[2]);
                    studentUid = (String) row[2];
                    if(cardUid == null) cardUid = Long.parseLong(DEFAULT_VALUE);
                } else {
                    studentUid = (String) row[2];
                    cardUid = ((BigInteger)row[3]).longValue();
                }
                Boolean passDirection = (Integer) row[4] == 1;
                Date eventDate = new Date(((BigInteger) row[5]).longValue());
                BigInteger idofEnterEvent = (BigInteger) row[6];
                BigInteger idofOrg = (BigInteger) row[7];
                EventDataItem item = new EventDataItem(ogrn, null, turnstile,
                        studentUid, cardUid, passDirection, eventDate, idofEnterEvent, idofOrg);
                dataItems.add(item);
            }
            return dataItems;
        } catch (Exception e){
            logger.error("Can't get records for sent to SCUD: ", e);
            return null;
        }
    }

    private boolean stringIsNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
