/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.enterevents;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.dao.AbstractJpaDao;
import ru.axetta.ecafe.processor.core.persistence.dao.model.enterevent.DAOEnterEventSummaryModel;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: shamil
 * Date: 14.08.14
 * Time: 13:07
 * To change this template use File | Settings | File Templates.
 */
@Repository
public class EnterEventsRepository extends AbstractJpaDao<Org> {

    private final static Logger logger = LoggerFactory.getLogger(EnterEventsRepository.class);

    public static EnterEventsRepository getInstance() {
        return RuntimeContext.getAppContext().getBean(EnterEventsRepository.class);
    }


    public List<DAOEnterEventSummaryModel> getEnterEventsSummaryNotEmptyClient(Long endTime){
        long begin = CalendarUtils.truncateToDayOfMonth(new Date(endTime)).getTime();
        return getEnterEventsSummaryNotEmptyClient(begin,endTime);
    }

    @Transactional(readOnly = true)
    public List<DAOEnterEventSummaryModel> getEnterEventsSummaryNotEmptyClient(Long startTime,Long endTime) {

        List<Object[]> tempList = (ArrayList) entityManager.createNativeQuery(
                "SELECT DISTINCT ON(e) e.idofclient, e.idoforg, e.passdirection, e.eventcode, e.idoftempcard, e.evtdatetime, e.idofvisitor, e.visitorfullname, c.idofclientgroup, ( e.idoforg || ' ' || e.idofclient )as e  "
                        + "FROM cf_enterevents e "
                        + "INNER JOIN cf_clients c ON e.idofclient = c.idofclient   "
                        + "WHERE e.evtdatetime BETWEEN :startDateTime AND :endDateTime "
                        + "and e.idofclient is not null "
                        + "ORDER BY e, e.evtdatetime DESC")
                .setParameter("startDateTime", startTime)
                .setParameter("endDateTime", endTime)
                        //.setParameter("startDateTime", 1355097600000L)
                        //.setParameter("endDateTime", 1355183999000L)
                .getResultList();

        return parse(tempList);
    }

    public List<DAOEnterEventSummaryModel> getEnterEventsSummaryVisitors(Long endTime){
        long begin = CalendarUtils.truncateToDayOfMonth(new Date(endTime)).getTime();
        return getEnterEventsSummaryVisitors(begin,endTime);
    }

    @Transactional(readOnly = true)
    public List<DAOEnterEventSummaryModel> getEnterEventsSummaryVisitors(Long startTime,Long endTime) {

        List<Object[]> tempList = (ArrayList) entityManager.createNativeQuery(
                "SELECT DISTINCT ON(e.idofvisitor) e.idofclient, e.idoforg, e.passdirection, e.eventcode, e.idoftempcard, e.evtdatetime, e.idofvisitor, e.visitorfullname "
                        + "FROM cf_enterevents e "
                        + "WHERE e.evtdatetime BETWEEN :startDateTime AND :endDateTime "
                        + "and e.idofclient is  null and e.idofvisitor is not null "
                        + "ORDER BY e.idofvisitor, e.evtdatetime DESC")
                .setParameter("startDateTime", startTime)
                .setParameter("endDateTime", endTime)
                        //.setParameter("startDateTime", 1355097600000L)
                        //.setParameter("endDateTime", 1355183999000L)
                .getResultList();

        return parse(tempList);
    }

    @Transactional(readOnly = true)
    public List<DAOEnterEventSummaryModel> getEnterEventsSummary(Long idOfOrg, Long startTime, Long endTime) {
        List<Object[]> tempList = (ArrayList) entityManager.createNativeQuery(
                "SELECT  e.idofclient, e.idoforg, e.passdirection, e.eventcode, e.idoftempcard, e.evtdatetime, e.idofvisitor, e.visitorfullname, c.idofclientgroup, (p.surname || ' ' ||p.firstname || ' ' || p.secondname) as fullname, g.groupname "
                        + " FROM cf_enterevents e "
                        + " INNER JOIN cf_clients c ON e.idofclient = c.idofclient  and e.idoforg = c.idoforg "
                        + " INNER JOIN cf_persons p ON p.idofperson = c.idofperson "
                        + " INNER JOIN cf_clientgroups g on c.idofclientgroup = g.idofclientgroup and c.idoforg = g.idoforg "
                        + " WHERE e.evtdatetime BETWEEN :startTime AND :endTime "
                        + " and e.idofclient is not null "
                        + " and e.idOfOrg = :idOfOrg "
                        + " ORDER BY g.groupname, e.idofclient, e.evtdatetime DESC ")
                .setParameter("idOfOrg", idOfOrg)
                .setParameter("startTime", startTime)
                .setParameter("endTime", endTime)
                        //.setParameter("startDateTime", 1355097600000L)
                        //.setParameter("endDateTime", 1355183999000L)
                .getResultList();

        return parseFull(tempList);
    }

    @Transactional(readOnly = true)
    public List<DAOEnterEventSummaryModel> getEnterEventsSummaryEmptyClient(Long dateTime) {
        long begin = CalendarUtils.truncateToDayOfMonth(new Date(dateTime)).getTime();
        List<Object[]> tempList = (ArrayList) entityManager.createNativeQuery(
                "SELECT e.idofclient, e.idoforg, e.passdirection, e.eventcode, e.idoftempcard, e.evtdatetime, e.idofvisitor, e.visitorfullname "
                        + "FROM cf_enterevents e "
                        + "WHERE e.evtdatetime BETWEEN :startDateTime AND :endDateTime "
                        + "and e.idofclient is null and e.idofvisitor is  null "
                        + "ORDER BY  e.evtdatetime DESC")
                .setParameter("startDateTime", begin)
                .setParameter("endDateTime", dateTime)
                        //.setParameter("startDateTime", 1355097600000L)
                        //.setParameter("endDateTime", 1355183999000L)
                .getResultList();

        return parse(tempList);
    }

    private static List<DAOEnterEventSummaryModel> parse(List<Object[]> tempList){
        List<DAOEnterEventSummaryModel> result = new ArrayList<DAOEnterEventSummaryModel>();
        for (Object[] temp : tempList) {

            DAOEnterEventSummaryModel entry = new DAOEnterEventSummaryModel();
            entry.setIdOfClient(temp[0] != null ? ((BigInteger) temp[0]).longValue() : null);
            entry.setIdOfOrg(temp[1] != null ? ((BigInteger) temp[1]).longValue() : null);
            entry.setPassDirection((Integer) temp[2]);
            entry.setEventCode((Integer) temp[3]);
            entry.setIdofTempCard(temp[4] != null ? ((BigInteger) temp[4]).longValue() : null);
            entry.setEvtDateTime(temp[5] != null ? ((BigInteger) temp[5]).longValue() : null);
            if(temp.length >= 6)
                entry.setIdOfVisitor(temp[6] != null ? ((BigInteger) temp[6]).longValue() : null);
            if(temp.length >= 7)
                entry.setVisitorFullName(temp[7] != null ? (String) temp[7] : null);
            if(temp.length > 8)
                entry.setIdOfClientGroup(temp[8] != null ? ((BigInteger) temp[8]).longValue() : null);
            result.add(entry);
        }
        return result;
    }

    private static List<DAOEnterEventSummaryModel> parseFull(List<Object[]> tempList){
        List<DAOEnterEventSummaryModel> result = new ArrayList<DAOEnterEventSummaryModel>();
        for (Object[] temp : tempList) {

            DAOEnterEventSummaryModel entry = new DAOEnterEventSummaryModel();
            entry.setIdOfClient(temp[0] != null ? ((BigInteger) temp[0]).longValue() : null);
            entry.setIdOfOrg(temp[1] != null ? ((BigInteger) temp[1]).longValue() : null);
            entry.setPassDirection((Integer) temp[2]);
            entry.setEventCode((Integer) temp[3]);
            entry.setIdofTempCard(temp[4] != null ? ((BigInteger) temp[4]).longValue() : null);
            entry.setEvtDateTime(temp[5] != null ? ((BigInteger) temp[5]).longValue() : null);
            entry.setIdOfVisitor(temp[6] != null ? ((BigInteger) temp[6]).longValue() : null);
            entry.setVisitorFullName((String) temp[9]);
            entry.setIdOfClientGroup(temp[8] != null ? ((BigInteger) temp[8]).longValue() : null);
            entry.setGroupName((String) temp[10]);
            result.add(entry);
        }
        return result;
    }


    @Transactional(readOnly = true)
    public Map<Long, Map<Long, List<DAOEnterEventSummaryModel> > > getEnterEventsSummaryNotEmptyClientFull(Long dateTime) {
        long begin = CalendarUtils.truncateToDayOfMonth(new Date(dateTime)).getTime();
        List<Object[]> tempList = (ArrayList) entityManager.createNativeQuery(
                "SELECT e.idofclient, e.idoforg, e.passdirection, e.eventcode, e.idoftempcard, e.evtdatetime, e.idofvisitor, e.visitorfullname, c.idofclientgroup, ( e.idoforg || ' ' || e.idofclient )as e  "
                        + "FROM cf_enterevents e "
                        + "LEFT JOIN cf_clients c ON e.idofclient = c.idofclient  and e.idoforg = c.idoforg "
                        + "WHERE e.evtdatetime BETWEEN :startDateTime AND :endDateTime "
                        + "and e.idofclient is not null "
                        + "ORDER BY e, e.evtdatetime DESC")
                .setParameter("startDateTime", begin)
                .setParameter("endDateTime", dateTime)
                        //.setParameter("startDateTime", 1398027420000L)
                        //.setParameter("endDateTime", 1398106620000L)
                .getResultList();

        Map<Long, Map<Long, List<DAOEnterEventSummaryModel> > > resultMap = new HashMap<Long, Map<Long, List<DAOEnterEventSummaryModel> > >();

        for(DAOEnterEventSummaryModel model : parse(tempList) ){
            if( !resultMap.containsKey(model.getIdOfOrg()) ){
                resultMap.put(model.getIdOfOrg(), new HashMap<Long, List<DAOEnterEventSummaryModel>>());
            }
            if( !resultMap.get(model.getIdOfOrg()).containsKey(model.getIdOfClient()) ){
                resultMap.get(model.getIdOfOrg()).put(model.getIdOfClient(), new LinkedList<DAOEnterEventSummaryModel>());
            }



            resultMap.get(model.getIdOfOrg()).get(model.getIdOfClient()).add(model);
        }
        return resultMap;
    }
}
