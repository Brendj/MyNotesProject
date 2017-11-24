/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.dao.enterevents;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.EnterEvent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.dao.AbstractJpaDao;
import ru.axetta.ecafe.processor.core.persistence.dao.model.enterevent.DAOEnterEventSummaryModel;
import ru.axetta.ecafe.processor.core.persistence.dao.model.enterevent.EnterEventCount;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.persistence.TypedQuery;
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
                "SELECT DISTINCT ON(e) e.idofclient, e.idoforg, og.shortname, e.passdirection, e.eventcode, e.idoftempcard, e.evtdatetime, e.idofvisitor, e.visitorfullname, c.idofclientgroup, ( e.idoforg || ' ' || e.idofclient )as e  "
                        + "FROM cf_enterevents e "
                        + "INNER JOIN cf_clients c ON e.idofclient = c.idofclient   "
                        + " INNER JOIN cf_orgs og ON e.idoforg = og.idoforg "
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
                "SELECT DISTINCT ON(e.idofvisitor) e.idofclient, e.idoforg, og.shortname, e.passdirection, e.eventcode, e.idoftempcard, e.evtdatetime, e.idofvisitor, e.visitorfullname "
                        + "FROM cf_enterevents e "
                        + " INNER JOIN cf_orgs og ON e.idoforg = og.idoforg "
                        + "WHERE e.evtdatetime BETWEEN :startDateTime AND :endDateTime "
                        + "and e.idofclient is  null and e.idofvisitor is not null "
                        + "ORDER BY e.idofvisitor, e.evtdatetime DESC")
                .setParameter("startDateTime", startTime)
                .setParameter("endDateTime", endTime)
                .getResultList();

        return parse(tempList);
    }

    @Transactional(readOnly = true)
    public List<DAOEnterEventSummaryModel> getEnterEventsSummary(String orgList, Long startTime, Long endTime) {
        String sql =
                "SELECT  e.idofclient, e.idoforg, og.shortname, e.passdirection, e.eventcode, e.idoftempcard, e.evtdatetime, e.idofvisitor, e.visitorfullname, c.idofclientgroup, (p.surname || ' ' ||p.firstname || ' ' || p.secondname) as fullname, g.groupname , c.idoforg as cidoforg, cg.shortname as cshortname "
                        + " FROM cf_enterevents e "
                        + " INNER JOIN cf_clients c ON e.idofclient = c.idofclient   and c.idOfOrg IN (" + orgList + ") "
                        + " INNER JOIN cf_orgs cg ON cg.idoforg=c.idoforg "
                        + " INNER JOIN cf_persons p ON p.idofperson = c.idofperson "
                        + " INNER JOIN cf_clientgroups g ON c.idofclientgroup = g.idofclientgroup and c.idoforg = g.idoforg "
                        + " INNER JOIN cf_orgs og ON e.idoforg = og.idoforg "
                        + " WHERE e.evtdatetime BETWEEN :startTime AND :endTime "
                        + " and e.idofclient IS not null "
                        + " and e.idOfOrg IN (" + orgList + ") "
                        + " ORDER BY g.groupname, e.idofclient, e.evtdatetime DESC ";
        List<Object[]> tempList = (ArrayList) entityManager.createNativeQuery(sql).setParameter("startTime", startTime)
                .setParameter("endTime", endTime)
                .getResultList();

        return parseFull(tempList);
    }

    @Transactional(readOnly = true)
    public List<DAOEnterEventSummaryModel> getEnterEventsSummaryEmptyClient(Long dateTime) {
        long begin = CalendarUtils.truncateToDayOfMonth(new Date(dateTime)).getTime();
        List<Object[]> tempList = (ArrayList) entityManager.createNativeQuery(
                "SELECT e.idofclient, e.idoforg, og.shortname, e.passdirection, e.eventcode, e.idoftempcard, e.evtdatetime, e.idofvisitor, e.visitorfullname "
                        + "FROM cf_enterevents e "
                        + " INNER JOIN cf_orgs og ON e.idoforg = og.idoforg "
                        + " WHERE e.evtdatetime BETWEEN :startDateTime AND :endDateTime "
                        + " AND e.idofclient IS null AND e.idofvisitor IS  null "
                        + " ORDER BY  e.evtdatetime DESC")
                .setParameter("startDateTime", begin)
                .setParameter("endDateTime", dateTime)
                .getResultList();

        return parse(tempList);
    }

    private static List<DAOEnterEventSummaryModel> parse(List<Object[]> tempList){
        List<DAOEnterEventSummaryModel> result = new ArrayList<DAOEnterEventSummaryModel>();
        for (Object[] temp : tempList) {

            DAOEnterEventSummaryModel entry = new DAOEnterEventSummaryModel();
            entry.setIdOfClient(temp[0] != null ? ((BigInteger) temp[0]).longValue() : null);
            entry.setIdOfOrg(temp[1] != null ? ((BigInteger) temp[1]).longValue() : null);
            entry.setOrgName((String) temp[2]);
            entry.setPassDirection((Integer) temp[3]);
            entry.setEventCode((Integer) temp[4]);
            entry.setIdofTempCard(temp[5] != null ? ((BigInteger) temp[5]).longValue() : null);
            entry.setEvtDateTime(temp[6] != null ? ((BigInteger) temp[6]).longValue() : null);
            if(temp.length >= 7)
                entry.setIdOfVisitor(temp[7] != null ? ((BigInteger) temp[7]).longValue() : null);
            if(temp.length >= 8)
                entry.setVisitorFullName(temp[8] != null ? (String) temp[8] : null);
            if(temp.length > 9)
                entry.setIdOfClientGroup(temp[9] != null ? ((BigInteger) temp[9]).longValue() : null);
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
            entry.setOrgName((String) temp[2]);
            entry.setPassDirection((Integer) temp[3]);
            entry.setEventCode((Integer) temp[4]);
            entry.setIdofTempCard(temp[5] != null ? ((BigInteger) temp[5]).longValue() : null);
            entry.setEvtDateTime(temp[6] != null ? ((BigInteger) temp[6]).longValue() : null);
            entry.setIdOfVisitor(temp[7] != null ? ((BigInteger) temp[7]).longValue() : null);
            entry.setVisitorFullName((String) temp[10]);
            entry.setIdOfClientGroup(temp[9] != null ? ((BigInteger) temp[9]).longValue() : null);
            entry.setGroupName((String) temp[11]);
            entry.setClientOrgId( ((BigInteger) temp[12]).longValue() );
            entry.setClientOrgName((String) temp[13]);
            result.add(entry);
        }
        return result;
    }


    @Transactional(readOnly = true)
    public Map<Long, Map<Long, List<DAOEnterEventSummaryModel> > > getEnterEventsSummaryNotEmptyClientFull(Long dateTime) {
        long begin = CalendarUtils.truncateToDayOfMonth(new Date(dateTime)).getTime();
        List<Object[]> tempList = (ArrayList) entityManager.createNativeQuery(
                "SELECT e.idofclient, e.idoforg, og.shortname, e.passdirection, e.eventcode, e.idoftempcard, e.evtdatetime, e.idofvisitor, e.visitorfullname, c.idofclientgroup, ( e.idoforg || ' ' || e.idofclient )as e  "
                        + "FROM cf_enterevents e "
                        + "LEFT JOIN cf_clients c ON e.idofclient = c.idofclient  and e.idoforg = c.idoforg "
                        + " INNER JOIN cf_orgs og ON e.idoforg = og.idoforg "
                        + "WHERE e.evtdatetime BETWEEN :startDateTime AND :endDateTime "
                        + "and e.idofclient is not null "
                        + "ORDER BY e, e.evtdatetime DESC")
                .setParameter("startDateTime", begin)
                .setParameter("endDateTime", dateTime)
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


    @Transactional
    public List<EnterEventCount> findAllStudentsEnterEventsCount() {
        Query nativeQuery = entityManager.createNativeQuery(
                "select c.idoforg, count(*)from cf_clients c,"
                        + "(select distinct idofclient from cf_enterevents where evtdatetime between 1409877287000 and 1409963687000 "
                        + " ) e where c.idofclientgroup <  1100000000 "
                        + " and  c.idofclient =e.idofclient "
                        + "group by c.idoforg "
                        + " order by idoforg ");
        List<EnterEventCount> result = new ArrayList<EnterEventCount>();
        for (Object o : nativeQuery.getResultList()) {
            Object[] o1 = (Object[]) o;
            result.add(new EnterEventCount(((BigInteger)o1[0]).longValue(),((BigInteger)o1[1]).intValue()));
        }
        return result;
    }


    @Transactional
    public List<EnterEventCount> findAllBeneficiaryStudentsEnterEventsCount() {
        Query nativeQuery = entityManager.createNativeQuery(
                "select c.idoforg, count(*)from cf_clients c,"
                        + "(select distinct idofclient from cf_enterevents where evtdatetime between 1409877287000 and 1409963687000 "
                        + " ) e where c.idofclientgroup <  1100000000 "
                        + " AND c.DiscountMode > 0  and  c.idofclient =e.idofclient "
                        + "group by c.idoforg "
                        + " order by idoforg ");
        List<EnterEventCount> result = new ArrayList<EnterEventCount>();
        for (Object o : nativeQuery.getResultList()) {
            Object[] o1 = (Object[]) o;
            result.add(new EnterEventCount(((BigInteger)o1[0]).longValue(),((BigInteger)o1[1]).intValue()));
        }
        return result;
    }
    @Transactional
    public List<EnterEventCount> findAllBeneficiaryStudentsEnterEvents(Date startTime, Date endTime) {
        Query nativeQuery = entityManager.createNativeQuery(
                "select c.idoforg, e.idofclient from cf_clients c, "
                        + "(select distinct idofclient from cf_enterevents where evtdatetime between :startTime and :endTime  "
                        + ") e where c.idofclientgroup <  1100000000  "
                        + "AND c.DiscountMode > 0  and  c.idofclient =e.idofclient  "
                        + "order by idoforg ")
                .setParameter("startTime",startTime.getTime())
                .setParameter("endTime",endTime.getTime());
        List<EnterEventCount> result = new ArrayList<EnterEventCount>();
        for (Object o : nativeQuery.getResultList()) {
            Object[] o1 = (Object[]) o;
            result.add(new EnterEventCount(((BigInteger)o1[0]).longValue(),((BigInteger)o1[1]).longValue()));
        }
        return result;
    }


    @Transactional(readOnly = true)
    public List<EnterEvent> findLastNEnterEvent(long orgId,Date minDate, Date maxDate, int n){
        TypedQuery<EnterEvent> query = entityManager.createQuery(
                "from EnterEvent where evtDateTime between :minDate and :maxDate and org.idOfOrg = :idOfOrg order by evtDateTime desc ",
                EnterEvent.class);
        query.setMaxResults(n);
        query.setParameter("idOfOrg",orgId);
        query.setParameter("minDate",minDate);
        query.setParameter("maxDate",maxDate);
        return query.getResultList();
    }
}
