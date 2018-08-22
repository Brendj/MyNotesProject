/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.geoplaner;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

//TODO изменить реализацию методов класса для передачи данных в Geoplaner онлайн (во время синхронизации)
@Component
@Scope("prototype")
public class GeoplanerJsonManager {
    private static final Logger logger = LoggerFactory.getLogger(GeoplanerJsonManager.class);
    private final static boolean isOn = managerIsOn();
    private GeoplanerJsonService service = RuntimeContext.getAppContext().getBean(GeoplanerJsonService.class);
    private Date today = CalendarUtils.startOfDay(new Date());

    public static boolean managerIsOn() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String reqInstance = runtimeContext
                .getConfigProperties().getProperty("ecafe.processor.geoplaner.sendevents", "false");
        return Boolean.parseBoolean(reqInstance);
    }

    public void sendEvents()throws Exception{
        if(isOn){
           sendEventsToGeoplaner();
        }
    }

    private void sendEventsToGeoplaner() throws Exception{
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            sendEnterEventsToGeoplaner(session);
            sendTransactionalInfoToGeoplaner(session);

            transaction.commit();
            transaction = null;
        }catch (Exception e){
            logger.error(e.getMessage());
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    private void sendEnterEventsToGeoplaner(Session session) throws Exception{
        List<JsonEnterEventInfo> infoList = null;
        List<EnterEventIdsContainer> idsForUpdate = new LinkedList<EnterEventIdsContainer>();
        try {
            infoList = buildJsonEnterEventInfo(session, idsForUpdate);
            if (infoList == null || infoList.isEmpty()) {
                logger.warn("No EnterEventSendInfo records for send to Geoplaner App");
                return;
            }
            Integer statusCode = service.sendPost(infoList, true);
            if(!statusCode.equals(200)){
                logger.warn("The Geoplaner returned code " + statusCode);
                return;
            }
            updateEnterEvents(session, idsForUpdate, 1);
        }catch (Exception e) {
            logger.error("Can't send EnterEventSendInfo to Geoplaner App: ", e);
            updateEnterEvents(session, idsForUpdate, 0);
        }
    }

    private void sendTransactionalInfoToGeoplaner(Session session) throws Exception{
        List<JsonTransactionInfo> infoList = null;
        List<Long> idForUpdate = new LinkedList<Long>();
        try {
            infoList = buildJsonTransactionInfo(session, idForUpdate);
            if (infoList == null || infoList.isEmpty()) {
                logger.warn("No Transaction's records for send to Geoplaner App");
                return;
            }
            Integer statusCode = service.sendPost(infoList, false);
            if(!statusCode.equals(200)){
                logger.warn("The Geoplaner returned code " + statusCode);
                return;
            }
            updateTransactionInfo(session, idForUpdate, 1);
        }catch (Exception e) {
            logger.error("Can't send EnterEventSendInfo to Geoplaner App: " + e.getMessage());
            updateTransactionInfo(session, idForUpdate, 0);
        }
    }

    private List<JsonEnterEventInfo> buildJsonEnterEventInfo(Session session, List<EnterEventIdsContainer> ids) throws Exception{
        List<JsonEnterEventInfo> result = new LinkedList<JsonEnterEventInfo>();
        Integer smartWatchCardType = Arrays.asList(Card.TYPE_NAMES).indexOf("Часы (Mifare)");
        Query query = session.createSQLQuery(" select crd.cardPrintedNo, crd.cardNo, eesi.evtdatetime, ee.passdirection, "
                + " o.ShortAddress, o.ShortName, eesi.idofenterevent, eesi.idoforg "
                + " from cf_enterevents_send_info eesi "
                + " join cf_enterevents ee on eesi.idofenterevent = ee.idofenterevent and eesi.idoforg = ee.idoforg "
                + " join cf_cards crd on eesi.idofcard = crd.idofcard "
                + " join cf_clients c on c.idofclient = ee.idofclient and c.idofclient = crd.idofclient "
                + " join cf_orgs o on eesi.idoforg = o.idoforg "
                + " where crd.cardtype = :smartWatchCardType "
                + " and c.hasactivesmartwatch = 1 "
                + " and eesi.evtdatetime > :today "
                + " order by crd.cardPrintedNo, crd.cardNo, eesi.evtdatetime ");
        query.setParameter("smartWatchCardType", smartWatchCardType);
        query.setParameter("today", this.today.getTime());
        List<Object[]> dataFromDB = query.list();
        if(dataFromDB == null || dataFromDB.isEmpty()){
            return null;
        }
        JsonEnterEventInfo enterEventInfo = null;

        for(Object[] row : dataFromDB){
            JsonEnterEventInfoItem item = new JsonEnterEventInfoItem();
            if(isNewEnterEventEntity(enterEventInfo, ((BigInteger) row[0]).longValue(), ((BigInteger) row[1]).longValue())) {
                enterEventInfo = new JsonEnterEventInfo();
                Long trackerId = ((BigInteger) row[0]).longValue();
                Long trackerUid = ((BigInteger) row[1]).longValue();
                enterEventInfo.setTrackerId(trackerId);
                enterEventInfo.setTrackerUid(trackerUid);
                result.add(enterEventInfo);
            }
            Date evtDate = new Date(((BigInteger) row[2]).longValue());
            item.setEvtDateTime(evtDate);
            item.setDirection((Integer) row[3]);
            item.setShortAddress((String) row[4]);
            item.setShortName((String) row[5]);
            enterEventInfo.getEvents().add(item);

            EnterEventIdsContainer id = new EnterEventIdsContainer();
            id.setIdOfEnterEvent(((BigInteger) row[6]).longValue());
            id.setIdOfOrg(((BigInteger) row[7]).longValue());
            ids.add(id);
        }
        return result;
    }

    private List<JsonTransactionInfo> buildJsonTransactionInfo(Session session, List<Long> idForUpdate) {
        List<JsonTransactionInfo> result = new LinkedList<JsonTransactionInfo>();
        Integer smartWatchCardType = Arrays.asList(Card.TYPE_NAMES).indexOf("Часы (Mifare)");
        Query query = session.createSQLQuery("select crd.cardprintedno, crd.cardno, t.transactiondate, t.sourcetype, "
                + " t.transactionsum, t.source, t.idOfTransaction "
                + " from cf_transactions t "
                + " join cf_clients c on c.idofclient = t.idofclient "
                + " join cf_cards crd on crd.idofclient = t.idofclient "
                + " where c.hasactivesmartwatch = 1 "
                + " and crd.cardtype = :smartWatchCardType "
                + " and t.transactiondate > :today "
                + " order by crd.cardPrintedNo, crd.cardNo, t.transactiondate ");
        query.setParameter("smartWatchCardType", smartWatchCardType);
        query.setParameter("today", this.today.getTime());
        List<Object[]> dataFromDB = query.list();
        if(dataFromDB == null || dataFromDB.isEmpty()){
            return null;
        }
        JsonTransactionInfo transactionInfo = null;

        for(Object[] row : dataFromDB){
            JsonTransactionInfoItem item = new JsonTransactionInfoItem();
            if(isNewTransactionEntity(transactionInfo, ((BigInteger) row[0]).longValue(),((BigInteger) row[1]).longValue())) {
                Long trackerId = ((BigInteger) row[0]).longValue();
                Long trackerUid = ((BigInteger) row[1]).longValue();
                transactionInfo = new JsonTransactionInfo();
                transactionInfo.setTrackerId(trackerId);
                transactionInfo.setTrackerUid(trackerUid);
                result.add(transactionInfo);
            }
            Date evtDate = new Date(((BigInteger) row[2]).longValue());
            item.setTransactionTime(evtDate);
            item.setSourceType((Integer) row[3]);
            item.setTransactionSum(((BigInteger) row[4]).longValue());
            item.setSourceName((String) row[5]);
            transactionInfo.getTransactions().add(item);

            Long idOfTransaction = ((BigInteger) row[6]).longValue();
            idForUpdate.add(idOfTransaction);
        }
        return result;
    }

    private boolean isNewTransactionEntity(JsonTransactionInfo transactionInfo, long trackerId, long trackerUid) {
        return transactionInfo == null || (transactionInfo.getTrackerId() == null && transactionInfo.getTrackerUid() == null)
                || (!transactionInfo.getTrackerUid().equals(trackerUid) || !transactionInfo.getTrackerId().equals(trackerId));
    }

    private boolean isNewEnterEventEntity(JsonEnterEventInfo enterEventInfo, long trackerId, long trackerUid) {
        return enterEventInfo == null || (enterEventInfo.getTrackerId() == null && enterEventInfo.getTrackerUid() == null)
                || (!enterEventInfo.getTrackerUid().equals(trackerUid) || !enterEventInfo.getTrackerId().equals(trackerId));
    }

    private void updateEnterEvents(Session session, List<EnterEventIdsContainer> idsList, int sendToGeoplaner) {
        if(idsList == null || idsList.isEmpty()){
            return;
        }
        for(EnterEventIdsContainer container : idsList) {
            Long idOfEnterEvent = container.getIdOfEnterEvent();
            Long idOfOrg = container.getIdOfOrg();
            Query query = session.createSQLQuery("update cf_enterevent_send_info "
                            + " set sendToGeoplaner = :sendToGeoplaner"
                            + " where idOfEnterEvent = :idOfEnterEvent and idOfOrg = :idOfOrg ");
            query.setParameter("sendToGeoplaner", sendToGeoplaner);
            query.setParameter("idOfEnterEvent", idOfEnterEvent);
            query.setParameter("idOfOrg", idOfOrg);
            query.executeUpdate();
        }
    }

    private void updateTransactionInfo(Session session, List<Long> ids, int sendToGeoplaner) {
        if(ids == null || ids.isEmpty()){
            return;
        }
        Query query = session.createSQLQuery("update cf_transactions "
                + " set sendToGeoplaner = :sendToGeoplaner "
                + " where idOfTransaction in (:listOfIds) ");
        query.setParameter("sendToGeoplaner", sendToGeoplaner);
        query.setParameterList("listOfIds", ids);
        query.executeUpdate();
    }

    private class EnterEventIdsContainer{
        private Long idOfEnterEvent;
        private Long IdOfOrg;

        public Long getIdOfEnterEvent() {
            return idOfEnterEvent;
        }

        public void setIdOfEnterEvent(Long idOfEnterEvent) {
            this.idOfEnterEvent = idOfEnterEvent;
        }

        public Long getIdOfOrg() {
            return IdOfOrg;
        }

        public void setIdOfOrg(Long idOfOrg) {
            IdOfOrg = idOfOrg;
        }
    }
}
