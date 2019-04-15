/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.service.org.OrgService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.DataBaseSafeConverterUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by anvarov on 04.05.2017.
 */
@Component
public class MonitoringOfReportService {
    final private static Logger logger = LoggerFactory.getLogger(MonitoringOfReportService.class);
    private final static Integer ORGS_AMOUNT_FOR_REPORT = 20;
    private final static String DATE_FORMAT = "DD/MM/YYYY";
    private final SimpleDateFormat QUERY_DATE_FORMAT = new SimpleDateFormat(DATE_FORMAT);

    private List<MonitoringOfItem> getMonitoringOfItems(Date startDate, Long idOfOrg,
            Map<Long, List<NumberOfPasses>> numberOfPassesMap, Map<Long, List<NumberOfPreferential>> numberOfPreferentialMap,
            Map<Long, List<NumberOfStudentsAndGuardians>> numberOfBuffetMap,
            Map<Long, List<NumberOfStudentsAndGuardians>> numberOfSubfeedMap,
            Map<Long, List<NumberOfStudentsAndGuardians>> numberOfPaidMap, Map<Long, List<Long>> numberOfpreordersMap,
            Integer i) {

        MonitoringOfItem monitoringOfItem = new MonitoringOfItem();
        monitoringOfItem.setsDate(startDate);

        List<MonitoringOfItem> monitoringOfItemList = new LinkedList<MonitoringOfItem>();
        monitoringOfItemList.add(monitoringOfItem);

        // passes
        List<NumberOfPasses> numberOfPassesList = numberOfPassesMap.get(idOfOrg);
        if(numberOfPassesList != null) {
            NumberOfPasses numberOfPasses = numberOfPassesList.get(i);
            monitoringOfItem.setNumberOfPassesStudents(numberOfPasses.getStudents());
            monitoringOfItem.setNumberOfUniquePassesStudents(numberOfPasses.getUniqueStudents());
            monitoringOfItem.setNumberOfPassesEmployees(numberOfPasses.getEmployees());
            monitoringOfItem.setNumberOfUniquePassesEmployees(numberOfPasses.getUniqueEmployees());
            monitoringOfItem.setNumberOfPassesGuardians(numberOfPasses.getGuardians());
            monitoringOfItem.setNumberOfUniquePassesGuardians(numberOfPasses.getUniqueGuardians());
            monitoringOfItem.setSummaryOfPasses(
                    monitoringOfItem.getNumberOfPassesStudents() + monitoringOfItem.getNumberOfPassesEmployees() + monitoringOfItem.getNumberOfPassesGuardians());
        }

        // lgotnoe
        List<NumberOfPreferential> numberOfPreferentialList = numberOfPreferentialMap.get(idOfOrg);
        if(numberOfPreferentialList != null) {
            NumberOfPreferential numberOfPreferential = numberOfPreferentialList.get(i);
            monitoringOfItem.setNumberOfLgotnoeFriendlyOrg(numberOfPreferential.getFriendlyOrg());
            monitoringOfItem.setNumberOfLgotnoeOtherOrg(numberOfPreferential.getOthersOrg());
            monitoringOfItem.setNumberOfLgotnoe(monitoringOfItem.getNumberOfLgotnoeFriendlyOrg() + monitoringOfItem.getNumberOfLgotnoeOtherOrg());
            monitoringOfItem.setNumberOfReserve(numberOfPreferential.getReserve());
        }


        // buffet
        List<NumberOfStudentsAndGuardians> numberOfBuffetList = numberOfBuffetMap.get(idOfOrg);
        if(numberOfBuffetList != null) {
            NumberOfStudentsAndGuardians numberOfBuffet = numberOfBuffetList.get(i);
            monitoringOfItem.setNumberOfBuffetStudent(numberOfBuffet.getStudents());
            monitoringOfItem.setNumberOfBuffetGuardians(numberOfBuffet.getGuardians());
        }

        // subfeed
        List<NumberOfStudentsAndGuardians> numberOfSubfeedList = numberOfSubfeedMap.get(idOfOrg);
        if(numberOfSubfeedList != null) {
            NumberOfStudentsAndGuardians numberOfSubfeed = numberOfSubfeedList.get(i);
            monitoringOfItem.setNumberOfSubFeedStudents(numberOfSubfeed.getStudents());
            monitoringOfItem.setNumberOfSubFeedGuardians(numberOfSubfeed.getGuardians());
        }

        // paid
        List<NumberOfStudentsAndGuardians> numberOfPaidList = numberOfPaidMap.get(idOfOrg);
        if(numberOfPaidList != null) {
            NumberOfStudentsAndGuardians numberOfPaid = numberOfPaidList.get(i);
            monitoringOfItem.setNumberOfPaidStudents(numberOfPaid.getStudents());
            monitoringOfItem.setNumberOfPaidGuardians(numberOfPaid.getGuardians());
        }

        //preorders
        List<Long> numberOfUniquePreorderClientsList = numberOfpreordersMap.get(idOfOrg);
        if(numberOfUniquePreorderClientsList != null){
            Long numberOfUniquePreorderClients = numberOfUniquePreorderClientsList.get(i);
            monitoringOfItem.setNumberOfPreorders(numberOfUniquePreorderClients);
        }
        return monitoringOfItemList;
    }

    private Map<Long, List<NumberOfPasses>> generateNumberOfPasses(Date startTime, Date endTime, List<Long> idOfOrgList,
            Boolean divideIntoPeriods) {
        Map<Long, List<NumberOfPasses>> result = new HashMap<Long, List<NumberOfPasses>>();
        Session session = null;
        Transaction transaction = null;
        try {
            String divideByDay = divideIntoPeriods ? ", TO_CHAR(TO_TIMESTAMP(cfo.createddate / 1000), '"+ DATE_FORMAT +"') as evttime " : "";
            String groupByDay = divideIntoPeriods ? ", evttime " : "";
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();
            String selectFields =
                        "count(DISTINCT(CASE WHEN ((idofclientgroup < :employees) ) "
                                + "                   THEN idofenterevent END)) AS number_of_passes_students, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup < :employees) ) "
                                + "                   THEN idofclient END)) AS number_of_unique_passes_students, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup IN (:employees, :administration, :employee, :techEmployees, :visitors, :others))) "
                                + "                   THEN idofenterevent END)) AS number_of_passes_employees, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup IN (:employees, :administration, :employee, :techEmployees, :visitors, :others))) "
                                + "                   THEN idofclient END)) AS number_of_unique_passes_employees, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup = :parents) )"
                                + "                   THEN idofenterevent END)) AS number_of_passes_guardians, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup = :parents) )"
                                + "                   THEN idofclient END)) AS number_of_unique_passes_guardians "
                                + divideByDay;

            String orgCondition = "";
            if (idOfOrgList.size() <= ORGS_AMOUNT_FOR_REPORT) {
                orgCondition = " idoforg in (:idOfOrgs) and ";
            }

            String sqlString =
                    "SELECT idoforg, " + selectFields
                            + "FROM cf_enterevents "
                            + "WHERE " + orgCondition
                            + "    ((idofclientgroup < :employees) OR idofclientgroup IN "
                            + "        (:employees, :administration, :employee, :techEmployees, :parents, :visitors, :others)) "
                            + " AND passdirection IN (:passEntry, :passExit, :passReEntry, :passReExit) AND evtdatetime BETWEEN :startTime AND :endTime "
                            + "group by idoforg " + groupByDay;

            Query query = session.createSQLQuery(sqlString);

            query.setParameter("employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("administration", ClientGroup.Predefined.CLIENT_ADMINISTRATION.getValue());
            query.setParameter("employee", ClientGroup.Predefined.CLIENT_EMPLOYEE.getValue());
            query.setParameter("techEmployees", ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES.getValue());
            query.setParameter("visitors", ClientGroup.Predefined.CLIENT_VISITORS.getValue());
            query.setParameter("parents", ClientGroup.Predefined.CLIENT_PARENTS.getValue());
            query.setParameter("others", ClientGroup.Predefined.CLIENT_OTHERS.getValue());
            if (idOfOrgList.size() <= ORGS_AMOUNT_FOR_REPORT) {
                query.setParameterList("idOfOrgs", idOfOrgList);
            }
            query.setParameter("passEntry", EnterEvent.ENTRY);
            query.setParameter("passExit", EnterEvent.EXIT);
            query.setParameter("passReEntry", EnterEvent.RE_ENTRY);
            query.setParameter("passReExit", EnterEvent.RE_EXIT);
            query.setParameter("startTime", startTime.getTime());
            query.setParameter("endTime", endTime.getTime());

            List<Object[]> list = query.list();

            if(divideIntoPeriods) {
                for (Object[] row : list) {
                    Long idOfCurrentOrg = -1L;
                    Date currentDay = startTime;
                    Date limitDate = CalendarUtils.addOneDay(endTime);
                    List<NumberOfPasses> preferentialList = new LinkedList<NumberOfPasses>();
                    Date dateFromRow = QUERY_DATE_FORMAT.parse((String)row[7]);
                    if (idOfCurrentOrg.equals(-1L) || !idOfCurrentOrg.equals(row[0])) {
                        result.put(idOfCurrentOrg, preferentialList);
                        idOfCurrentOrg = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(row[0]);
                        preferentialList = new LinkedList<NumberOfPasses>();
                        currentDay = startTime;
                    }
                    while (!CalendarUtils.isCurrentDay(currentDay, limitDate)) {
                        if (!CalendarUtils.isCurrentDay(currentDay, dateFromRow)) {
                            NumberOfPasses numberOfPreferential = new NumberOfPasses();
                            preferentialList.add(numberOfPreferential);
                            currentDay = CalendarUtils.addOneDay(currentDay);
                        } else if (CalendarUtils.isWorkDateWithoutParser(true, dateFromRow)) {
                            NumberOfPasses numberOfPreferential = new NumberOfPasses((
                                    (BigInteger) row[1]).longValue(), ((BigInteger) row[2]).longValue(), ((BigInteger) row[3]).longValue(),
                                    ((BigInteger) row[4]).longValue(), ((BigInteger) row[5]).longValue(), ((BigInteger) row[6]).longValue());
                            preferentialList.add(numberOfPreferential);
                            currentDay = CalendarUtils.addOneDay(currentDay);
                            break;
                        }
                    }
                }
            } else {
                for (Object[] object : list) {
                    NumberOfPasses numberOfPasses = new NumberOfPasses(((BigInteger) object[1]).longValue(), ((BigInteger) object[2]).longValue(), ((BigInteger) object[3]).longValue(),
                            ((BigInteger) object[4]).longValue(), ((BigInteger) object[5]).longValue(), ((BigInteger) object[6]).longValue());
                    List<NumberOfPasses> numberOfPassesList = new ArrayList<NumberOfPasses>(1);
                    numberOfPassesList.add(numberOfPasses);
                    result.put(((BigInteger) object[0]).longValue(), numberOfPassesList);
                }
            }
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in generateNumberOfPasses: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    private Map<Long, List<NumberOfPreferential>> numberOfPreferential(Date startTime, Date endTime, List<Long> idOfOrgList,
            Boolean divideIntoPeriods) {
        Map<Long, List<NumberOfPreferential>> result = new HashMap<Long, List<NumberOfPreferential>>();
        Session session = null;
        Transaction transaction = null;
        try {
            String divideByDay = divideIntoPeriods ? ", TO_CHAR(TO_TIMESTAMP(cfo.createddate / 1000), '"+ DATE_FORMAT +"') as evttime " : "";
            String groupByDay = divideIntoPeriods ? ", evttime " : "";
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            String selectFields =
                    "count(DISTINCT(CASE WHEN ((fo.friendlyorg IS NOT NULL) ) "
                  + "                 THEN cfo.idofclient END)) AS number_of_lgotnoe_friendly, "
                  + "count(DISTINCT(CASE WHEN ((fo.friendlyorg IS NULL) ) "
                  + "                 THEN cfo.idofclient END)) AS number_of_lgotnoe_other, "
                  + "count(DISTINCT(CASE WHEN (cfo.ordertype IN (:reservePlan, :changePlan) ) "
                  + "                 THEN cfo.idofclient END)) AS number_of_reserve "
                  + divideByDay;


            String orgCondition = "";
            if (idOfOrgList.size() <= ORGS_AMOUNT_FOR_REPORT) {
                orgCondition = " cfo.idoforg in (:idOfOrgs) and ";
            }
            String sqlQuery =
                    "SELECT cfo.idoforg, " + selectFields
                  + "FROM cf_orders cfo "
                  + "LEFT JOIN cf_orderdetails cfod ON cfod.idoforg = cfo.idoforg AND cfod.idoforder = cfo.idoforder "
                  + "LEFT JOIN cf_clients c ON cfo.idofclient = c.idofclient "
                  + "LEFT JOIN cf_clientgroups g ON g.idofclientgroup = c.idofclientgroup AND c.idoforg = g.idoforg "
                  + "LEFT JOIN cf_friendly_organization fo on fo.currentorg = c.idoforg "
                  + "WHERE cfo.ordertype IN (:reducedPricePlan, :correctionType) AND " + orgCondition
                  + "    cfo.state = 0 AND g.idofclientgroup < :employees AND cfo.createddate BETWEEN :startTime AND :endTime AND "
                  + "    cfod.menutype >= :minType AND cfod.menutype <= :maxType  AND cfod.idofrule >= 0"
                  + " group by cfo.idoforg " + groupByDay;

            Query query = session.createSQLQuery(sqlQuery);
            if (idOfOrgList.size() <= ORGS_AMOUNT_FOR_REPORT) {
                query.setParameterList("idOfOrgs", idOfOrgList);
            }

            query.setParameter("startTime", startTime.getTime());
            query.setParameter("endTime", endTime.getTime());
            query.setParameter("minType", OrderDetail.TYPE_COMPLEX_MIN);
            query.setParameter("maxType", OrderDetail.TYPE_COMPLEX_MAX);
            query.setParameter("reducedPricePlan", OrderTypeEnumType.REDUCED_PRICE_PLAN.ordinal());
            query.setParameter("correctionType", OrderTypeEnumType.CORRECTION_TYPE.ordinal());
            query.setParameter("employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("reservePlan", OrderTypeEnumType.REDUCED_PRICE_PLAN_RESERVE.ordinal());
            query.setParameter("changePlan", OrderTypeEnumType.DISCOUNT_PLAN_CHANGE.ordinal());


            List<Object[]> list = query.list();

            if(divideIntoPeriods) {
                Long idOfCurrentOrg = -1L;
                Date currentDay = startTime;
                Date limitDate = CalendarUtils.addOneDay(endTime);
                List<NumberOfPreferential> preferentialList = new LinkedList<NumberOfPreferential>();
                for (Object[] row : list) {
                    Date dateFromRow = QUERY_DATE_FORMAT.parse((String)row[4]);
                    if (idOfCurrentOrg.equals(-1L) || !idOfCurrentOrg.equals(row[0])) {
                        result.put(idOfCurrentOrg, preferentialList);
                        idOfCurrentOrg = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(row[0]);
                        preferentialList = new LinkedList<NumberOfPreferential>();
                        currentDay = startTime;
                    }
                    while (!CalendarUtils.isCurrentDay(currentDay, limitDate)) {
                        if (!CalendarUtils.isCurrentDay(currentDay, dateFromRow)) {
                            NumberOfPreferential numberOfPreferential = new NumberOfPreferential();
                            preferentialList.add(numberOfPreferential);
                            currentDay = CalendarUtils.addOneDay(currentDay);
                        } else if (CalendarUtils.isWorkDateWithoutParser(true, dateFromRow)) {
                            NumberOfPreferential numberOfPreferential = new NumberOfPreferential(((BigInteger) row[1]).longValue(), ((BigInteger) row[2]).longValue(),
                                    ((BigInteger) row[3]).longValue());
                            preferentialList.add(numberOfPreferential);
                            currentDay = CalendarUtils.addOneDay(currentDay);
                            break;
                        }
                    }
                }
            } else {
                for (Object[] object : list) {
                    NumberOfPreferential numberOfPreferential = new NumberOfPreferential(((BigInteger) object[1]).longValue(),
                            ((BigInteger) object[2]).longValue(), ((BigInteger) object[3]).longValue());
                    List<NumberOfPreferential> numberOfPreferentials = new ArrayList<NumberOfPreferential>(1);
                    numberOfPreferentials.add(numberOfPreferential);
                    result.put(((BigInteger) object[0]).longValue(), numberOfPreferentials);
                }
            }
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in generateNumberOfPreferential: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    private Map<Long, List<NumberOfStudentsAndGuardians>> numberOfBuffet(Date startTime, Date endTime, List<Long> idOfOrgList,
            Boolean divideIntoPeriods) {
        Map<Long, List<NumberOfStudentsAndGuardians>> result = new HashMap<Long, List<NumberOfStudentsAndGuardians>>();
        Session session = null;
        Transaction transaction = null;
        try {
            String divideByDay = divideIntoPeriods ? ", TO_CHAR(TO_TIMESTAMP(cfo.createddate / 1000), '"+ DATE_FORMAT +"') as evttime " : "";;
            String groupByDay = divideIntoPeriods ? ", evttime " : "";
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            String selectFields =
                                " count(DISTINCT(CASE WHEN ((g.idofclientgroup < :employees) ) "
                                + "   THEN cfo.idofclient END)) AS number_of_buffet_students, "
                                + "count(DISTINCT(CASE WHEN ((g.idofclientgroup IN "
                                + "   (:employees, :administration, :displaced, :tech_employees, :visitors, :other, :parents)) "
                                + "   ) THEN cfo.idofclient END)) "
                                + "   AS number_of_buffet_guardians "
                                + divideByDay;

            String orgCondition = "";
            if (idOfOrgList.size() <= ORGS_AMOUNT_FOR_REPORT) {
                orgCondition = " cfo.idoforg in (:idOfOrgs) and ";
            }

            String sqlQuery = "SELECT  cfo.idoforg, " + selectFields
                    + "FROM cf_orders cfo "
                    + "LEFT JOIN cf_orderdetails cfod ON cfod.idoforg = cfo.idoforg AND cfod.idoforder = cfo.idoforder "
                    + "LEFT JOIN cf_clients c ON cfo.idofclient = c.idofclient "
                    + "LEFT JOIN cf_clientgroups g ON g.idofclientgroup = c.idofclientgroup AND c.idoforg = g.idoforg "
                    + "WHERE cfo.ordertype IN (:unknownType, :defaultType, :vendingType) AND "
                    + orgCondition
                    + " cfo.state = :stateCommited "
                    + "    AND ((g.idofclientgroup < :employees) OR g.idofclientgroup IN "
                    + "        (:employees, :administration, :displaced, :tech_employees, :visitors, :other, :parents)) "
                    + "    AND cfo.createddate BETWEEN :startTime AND :endTime"
                    + " group by cfo.idoforg " + groupByDay;
            Query query = session.createSQLQuery(sqlQuery);

            if (idOfOrgList.size() <= ORGS_AMOUNT_FOR_REPORT) {
                query.setParameterList("idOfOrgs", idOfOrgList);
            }
            query.setParameter("startTime", startTime.getTime());
            query.setParameter("endTime", endTime.getTime());
            query.setParameter("employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("administration", ClientGroup.Predefined.CLIENT_ADMINISTRATION.getValue());
            query.setParameter("displaced", ClientGroup.Predefined.CLIENT_DISPLACED.getValue());
            query.setParameter("tech_employees", ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES.getValue());
            query.setParameter("visitors", ClientGroup.Predefined.CLIENT_VISITORS.getValue());
            query.setParameter("other", ClientGroup.Predefined.CLIENT_OTHERS.getValue());
            query.setParameter("parents", ClientGroup.Predefined.CLIENT_PARENTS.getValue());
            query.setParameter("unknownType", OrderTypeEnumType.UNKNOWN.ordinal());
            query.setParameter("defaultType", OrderTypeEnumType.DEFAULT.ordinal());
            query.setParameter("vendingType", OrderTypeEnumType.VENDING.ordinal());
            query.setParameter("stateCommited", Order.STATE_COMMITED);

            List<Object[]> list = query.list();

            transaction.commit();
            transaction = null;
            result = getFromQuery(list, divideIntoPeriods, startTime, endTime);
        } catch (Exception e) {
            logger.error("Error in numberOfBuffet: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    private Map<Long, List<NumberOfStudentsAndGuardians>> getFromQuery(List<Object[]> list, Boolean divideIntoPeriods,
            Date startTime, Date endTime) throws Exception{
        Map<Long,List<NumberOfStudentsAndGuardians>> result = new HashMap<Long, List<NumberOfStudentsAndGuardians>>();

        if(divideIntoPeriods){
            Long idOfCurrentOrg = -1L;
            Date currentDay = startTime;
            Date limitDate = CalendarUtils.addOneDay(endTime);
            List<NumberOfStudentsAndGuardians> numberOfStudentsAndGuardiansList = new LinkedList<NumberOfStudentsAndGuardians>();
            for (Object[] row : list) {
                Date dateFromRow = QUERY_DATE_FORMAT.parse((String)row[3]);
                if (idOfCurrentOrg.equals(-1L) || !idOfCurrentOrg.equals(row[0])) {
                    result.put(idOfCurrentOrg, numberOfStudentsAndGuardiansList);
                    idOfCurrentOrg = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(row[0]);
                    numberOfStudentsAndGuardiansList = new LinkedList<NumberOfStudentsAndGuardians>();
                    currentDay = startTime;
                }
                while (!CalendarUtils.isCurrentDay(currentDay, limitDate)) {
                    if (!CalendarUtils.isCurrentDay(currentDay, dateFromRow)) {
                        NumberOfStudentsAndGuardians numberOfPreferential = new NumberOfStudentsAndGuardians();
                        numberOfStudentsAndGuardiansList.add(numberOfPreferential);
                        currentDay = CalendarUtils.addOneDay(currentDay);
                    } else if (CalendarUtils.isWorkDateWithoutParser(true, dateFromRow)) {
                        NumberOfStudentsAndGuardians numberOfPreferential = new NumberOfStudentsAndGuardians(((BigInteger) row[1]).longValue(),
                                ((BigInteger) row[2]).longValue());
                        numberOfStudentsAndGuardiansList.add(numberOfPreferential);
                        currentDay = CalendarUtils.addOneDay(currentDay);
                        break;
                    }
                }
            }
        } else {
            for (Object[] object : list) {
                NumberOfStudentsAndGuardians numberOfBuffet = new NumberOfStudentsAndGuardians(((BigInteger) object[1]).longValue(),
                        ((BigInteger) object[2]).longValue());
                List<NumberOfStudentsAndGuardians> numbers = new ArrayList<NumberOfStudentsAndGuardians>(1);
                numbers.add(numberOfBuffet);
                result.put(((BigInteger) object[0]).longValue(), numbers);
            }
        }
        return result;
    }

    private Map<Long, List<NumberOfStudentsAndGuardians>> numberOfSubFeed(Date startTime, Date endTime,
            List<Long> idOfOrgList, Boolean divideIntoPeriods) {
        Map<Long, List<NumberOfStudentsAndGuardians>> result = new HashMap<Long, List<NumberOfStudentsAndGuardians>>();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            String selectFields =
                        "count(DISTINCT(CASE WHEN ((g.idofclientgroup < :employees) ) "
                                + "                   THEN cfo.idofclient END)) AS number_of_subfeed_students, "
                                + "count(DISTINCT(CASE WHEN ((g.idofclientgroup IN "
                                + "        (:employees, :administration, :employee, :tech_employees, :visitors, :other, :parents))) "
                                + "        THEN cfo.idofclient END)) AS number_of_subfeed_guardians ";
            String orgCondition = "";
            if (idOfOrgList.size() <= ORGS_AMOUNT_FOR_REPORT) {
                orgCondition = " cfo.idoforg in (:idOfOrgs) and ";
            }

            String sqlQuery = "SELECT cfo.idoforg, " + selectFields
                    + "FROM CF_OrderDetails cfod "
                    + "LEFT OUTER JOIN CF_Orders cfo ON cfod.IdOfOrg = cfo.IdOfOrg AND cfod.IdOfOrder = cfo.IdOfOrder "
                    + "    AND cfo.State = :stateCommited AND cfo.OrderType = :subscriptionPlan "
                    + "LEFT OUTER JOIN CF_Orgs org ON cfo.IdOfOrg = org.IdOfOrg "
                    + "LEFT JOIN cf_clients c ON cfo.idofclient = c.idofclient "
                    + "LEFT JOIN cf_clientgroups g ON g.idofclientgroup = c.idofclientgroup AND c.idoforg = g.idoforg "
                    + "WHERE cfod.State = :detailedStateCommited AND "
                    + orgCondition
                    + " (cfo.CreatedDate BETWEEN :startTime AND :endTime) "
                    + "    AND cfod.MenuType >= :minType AND cfod.MenuType <= :maxType "
                    + "    AND ((g.idofclientgroup < :employees) OR "
                    + "        g.idofclientgroup IN (:employees, :administration, :employee, :tech_employees, :visitors, :other, :parents)) group by cfo.idoforg";

            Query query = session.createSQLQuery(sqlQuery);

            if (idOfOrgList.size() <= ORGS_AMOUNT_FOR_REPORT) {
                query.setParameterList("idOfOrgs", idOfOrgList);
            }

            query.setParameter("startTime", startTime.getTime());
            query.setParameter("endTime", endTime.getTime());
            query.setParameter("minType", OrderDetail.TYPE_COMPLEX_MIN);
            query.setParameter("maxType", OrderDetail.TYPE_COMPLEX_MAX);
            query.setParameter("employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("administration", ClientGroup.Predefined.CLIENT_ADMINISTRATION.getValue());
            query.setParameter("employee", ClientGroup.Predefined.CLIENT_EMPLOYEE.getValue());
            query.setParameter("tech_employees", ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES.getValue());
            query.setParameter("visitors", ClientGroup.Predefined.CLIENT_VISITORS.getValue());
            query.setParameter("other", ClientGroup.Predefined.CLIENT_OTHERS.getValue());
            query.setParameter("parents", ClientGroup.Predefined.CLIENT_PARENTS.getValue());
            query.setParameter("stateCommited", Order.STATE_COMMITED);
            query.setParameter("detailedStateCommited", OrderDetail.STATE_COMMITED);
            query.setParameter("subscriptionPlan", OrderTypeEnumType.SUBSCRIPTION_FEEDING.ordinal());

            List<Object[]> list = query.list();
            transaction.commit();
            transaction = null;
            result = getFromQuery(list, divideIntoPeriods, startTime, endTime);
        } catch (Exception e) {
            logger.error("Error in numberOfSubFeed: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    private Map<Long, List<NumberOfStudentsAndGuardians>> numberOfPaid(Date startTime, Date endTime, List<Long> idOfOrgList,
            Boolean divideIntoPeriods) {
        Map<Long, List<NumberOfStudentsAndGuardians>> result = new HashMap<Long, List<NumberOfStudentsAndGuardians>>();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            String selectFields =
                        "count(DISTINCT(CASE WHEN ((g.idofclientgroup < :employees)) "
                                + "                   THEN cfo.idofclient END)) AS number_of_paid_students, "
                                + "count(DISTINCT(CASE WHEN ((g.idofclientgroup IN "
                                + "        (:employees, :administration, :employee, :tech_employees, :visitors, :other, :parents))) "
                                + "        THEN cfo.idofclient END)) AS number_of_paid_guardians ";

            String orgCondition = "";
            if (idOfOrgList.size() <= ORGS_AMOUNT_FOR_REPORT) {
                orgCondition = " cfo.idoforg in (:idOfOrgs) and ";
            }

            String sqlQuery = "SELECT cfo.idoforg, " + selectFields
                    + "FROM CF_OrderDetails cfod "
                    + "LEFT OUTER JOIN CF_Orders cfo ON cfod.IdOfOrg = cfo.IdOfOrg AND cfod.IdOfOrder = cfo.IdOfOrder "
                    + "    AND cfo.State = :stateCommited AND cfo.OrderType = :payPlan "
                    + "LEFT OUTER JOIN CF_Orgs org ON cfo.IdOfOrg = org.IdOfOrg "
                    + "LEFT JOIN cf_clients c ON cfo.idofclient = c.idofclient "
                    + "LEFT JOIN cf_clientgroups g ON g.idofclientgroup = c.idofclientgroup AND c.idoforg = g.idoforg "
                    + "LEFT JOIN cf_preorder_linkod pl on cfo.idoforder = pl.idoforder and cfo.idoforg = pl.idoforg "
                    + "WHERE cfod.State = :detailedStateCommited AND pl.idoforder IS NULL AND"
                    + orgCondition
                    + " (cfo.CreatedDate BETWEEN :startTime AND :endTime) "
                    + "AND cfod.MenuType >= :minType AND cfod.MenuType <= :maxType AND ((g.idofclientgroup < :employees) "
                    + "    OR g.idofclientgroup IN (:employees, :administration, :employee, :tech_employees, :visitors, :other, :parents)) group by cfo.idoforg";

            Query query = session.createSQLQuery(sqlQuery);
            if (idOfOrgList.size() <= ORGS_AMOUNT_FOR_REPORT) {
                query.setParameterList("idOfOrgs", idOfOrgList);
            }
            query.setParameter("startTime", startTime.getTime());
            query.setParameter("endTime", endTime.getTime());
            query.setParameter("minType", OrderDetail.TYPE_COMPLEX_MIN);
            query.setParameter("maxType", OrderDetail.TYPE_COMPLEX_MAX);
            query.setParameter("employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("administration", ClientGroup.Predefined.CLIENT_ADMINISTRATION.getValue());
            query.setParameter("employee", ClientGroup.Predefined.CLIENT_EMPLOYEE.getValue());
            query.setParameter("tech_employees", ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES.getValue());
            query.setParameter("visitors", ClientGroup.Predefined.CLIENT_VISITORS.getValue());
            query.setParameter("other", ClientGroup.Predefined.CLIENT_OTHERS.getValue());
            query.setParameter("parents", ClientGroup.Predefined.CLIENT_PARENTS.getValue());
            query.setParameter("stateCommited", Order.STATE_COMMITED);
            query.setParameter("detailedStateCommited", OrderDetail.STATE_COMMITED);
            query.setParameter("payPlan", OrderTypeEnumType.PAY_PLAN.ordinal());

            List<Object[]> list = query.list();
            transaction.commit();
            transaction = null;
            result = getFromQuery(list, divideIntoPeriods, startTime, endTime);
        } catch (Exception e) {
            logger.error("Error in numberOfPaid: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    private Map<Long, PeopleData> loadPeopleDataForOrgs(List<Long> idOfOrgList) {
        Session session = null;
        Transaction transaction = null;
        Map<Long, PeopleData> result = new HashMap<Long, PeopleData>();
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();
            String orgsCondition = " ";
            if (idOfOrgList.size() <= ORGS_AMOUNT_FOR_REPORT) {
                orgsCondition = " cfc.idoforg in (:idOfOrg) and ";
            }
            Query query = session.createSQLQuery(
                    "SELECT "
                            + "    count(DISTINCT(CASE WHEN (cfc.IdOfClientGroup = :parents) THEN cfc.idofclient END)) AS parents, "
                            + "    count(DISTINCT(CASE WHEN (cfc.IdOfClientGroup IN (:employees, :administration)) THEN cfc.idofclient END)) "
                            + "        AS pedagogical_composition, "
                            + "    count(DISTINCT(CASE WHEN (cfc.idofclientgroup < :employees) THEN cfc.idofclient END)) AS all_peoples, "
                            + "    count(DISTINCT(CASE WHEN (cfc.idofclientgroup IN (:others, :tech_employees, :visitors)) "
                            + "                    THEN cfc.idofclient END)) AS other_emloyees, "
                            + "    count(DISTINCT(CASE WHEN ((cfc.idofclientgroup < :employees) AND (cfca.idofclient IS NOT NULL)) "
                            + "                    THEN cfca.idofclient END)) AS students_with_cards, cfc.idoforg "
                            + "FROM cf_clients cfc "
                            + "LEFT JOIN cf_cards cfca ON cfc.idofclient = cfca.idofclient "
                            + "WHERE " + orgsCondition + " (cfc.IdOfClientGroup = :parents "
                            + "        OR cfc.IdOfClientGroup IN (:employees, :administration) "
                            + "        OR (cfc.idofclientgroup < :employees) "
                            + "        OR (cfc.idofclientgroup IN (:others, :tech_employees, :visitors))) group by cfc.idoforg");

            if (idOfOrgList.size() <= 10) {
                query.setParameterList("idOfOrg", idOfOrgList);
            }
            query.setParameter("parents", ClientGroup.Predefined.CLIENT_PARENTS.getValue());
            query.setParameter("employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("administration", ClientGroup.Predefined.CLIENT_ADMINISTRATION.getValue());
            query.setParameter("others", ClientGroup.Predefined.CLIENT_OTHERS.getValue());
            query.setParameter("tech_employees", ClientGroup.Predefined.CLIENT_TECH_EMPLOYEES.getValue());
            query.setParameter("visitors", ClientGroup.Predefined.CLIENT_VISITORS.getValue());

            List<Object[]> list = query.list();

            for (Object[] obj : list) {
                PeopleData peopleData = new PeopleData(((BigInteger) obj[0]).longValue(), ((BigInteger) obj[1]).longValue(),
                        ((BigInteger) obj[2]).longValue(), ((BigInteger) obj[3]).longValue(), ((BigInteger) obj[4]).longValue());
                result.put(((BigInteger) obj[5]).longValue(), peopleData);
            }
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in loadPeopleDataForOrgs: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    public List<ReportItem> buildReportItems(Date startTime, Date endTime, List<Long> idOfOrgList,
            Boolean divideIntoPeriods) throws Exception {
        List<ReportItem> reportItemList = new LinkedList<ReportItem>();
        Map<Long, PeopleData> peopleDataMap = loadPeopleDataForOrgs(idOfOrgList);
        Map<Long, List<NumberOfPasses>> numberOfPassesMap = generateNumberOfPasses(startTime, endTime, idOfOrgList, divideIntoPeriods);
        Map<Long, List<NumberOfPreferential>> numberOfPreferentialMap = numberOfPreferential(startTime, endTime, idOfOrgList, divideIntoPeriods);
        Map<Long, List<NumberOfStudentsAndGuardians>> numberOfBuffetMap = numberOfBuffet(startTime, endTime, idOfOrgList, divideIntoPeriods);
        Map<Long, List<NumberOfStudentsAndGuardians>> numberOfSubfeedMap = numberOfSubFeed(startTime, endTime, idOfOrgList, divideIntoPeriods);
        Map<Long, List<NumberOfStudentsAndGuardians>> numberOfPaidMap = numberOfPaid(startTime, endTime, idOfOrgList, divideIntoPeriods);
        Map<Long, List<Long>> numberOfpreordersMap = buildNumberOfPreordersMap(startTime, endTime, idOfOrgList, divideIntoPeriods);
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();
            for (Long idOfOrg : idOfOrgList) {
                Org org = (Org) session.load(Org.class, idOfOrg);
                if (!org.getState().equals(Org.ACTIVE_STATE) || org.getType().getCode().equals(OrganizationType.SUPPLIER.getCode())){
                    continue;
                }
                ReportItem reportItem = new ReportItem();
                Org mainOrg = OrgService.getInstance().getMainBulding(org);
                reportItem.setOrgNum(mainOrg.getOrgNumberFromNameInfoService());
                reportItem.setShortName(org.getShortNameInfoService());
                reportItem.setAddress(org.getAddress());
                reportItem.setIdOfOrg(String.valueOf(org.getIdOfOrg()));
                reportItem.setCode(String.valueOf(org.getUniqueAddressId()));
                reportItem.setDistrict(org.getDistrict());
                reportItem.setTypeOfBuilding(org.getType().getShortType());
                reportItem.setTypeOfBuildingInternal(org.getTypeInitial().getShortType());
                reportItem.setIntroductionQueue(org.getIntroductionQueue());
                reportItem.setOrgStatus(org.stateString());

                PeopleData peopleData = peopleDataMap.get(idOfOrg);

                reportItem.setStudentsInDatabase(peopleData == null ? "" : String.valueOf(peopleData.getAllPeoples()));
                reportItem.setStudentsWithMaps(peopleData == null ? "" : String.valueOf(peopleData.getStudentsWithCards()));
                reportItem.setParents(peopleData == null ? "" : String.valueOf(peopleData.getParents()));
                reportItem.setPedagogicalComposition(peopleData == null ? "" : String.valueOf(peopleData.getPedagogicalComposition()));
                reportItem.setOtherEmployees(peopleData == null ? "" : String.valueOf(peopleData.getOther()));

                List<MonitoringOfItem> monitoringOfItemListFirst = getMonitoringOfItems(startTime,
                        idOfOrg, numberOfPassesMap, numberOfPreferentialMap, numberOfBuffetMap,
                        numberOfSubfeedMap, numberOfPaidMap, numberOfpreordersMap, 1);

                List<MonitoringOfItem> monitoringOfItemListSecond = getMonitoringOfItems(startTime,
                        idOfOrg, numberOfPassesMap, numberOfPreferentialMap, numberOfBuffetMap,
                        numberOfSubfeedMap, numberOfPaidMap, numberOfpreordersMap, 2);

                List<MonitoringOfItem> monitoringOfItemListThrid = getMonitoringOfItems(startTime,
                        idOfOrg, numberOfPassesMap, numberOfPreferentialMap, numberOfBuffetMap,
                        numberOfSubfeedMap, numberOfPaidMap, numberOfpreordersMap, 3);

                reportItem.setMonitoringOfItemsFirst(monitoringOfItemListFirst);
                reportItem.setMonitoringOfItemsSecond(monitoringOfItemListSecond);
                reportItem.setMonitoringOfItemsThrid(monitoringOfItemListThrid);

                reportItemList.add(reportItem);
            }
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in MonitoringOfReportService: ", e);
            throw e;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        Collections.sort(reportItemList, new Comparator<ReportItem>() {
            @Override
            public int compare(ReportItem o1, ReportItem o2) {
                int cmp = o1.getShortName().compareToIgnoreCase(o2.getShortName());
                if(cmp == 0){
                    Long idOfOrgO1 = Long.parseLong(o1.getIdOfOrg());
                    Long idOfOrgO2 = Long.parseLong(o2.getIdOfOrg());
                    return idOfOrgO1.compareTo(idOfOrgO2);
                }
                return cmp;
            }
        });

        return reportItemList;
    }

    private Map<Long, List<Long>> buildNumberOfPreordersMap(Date startTime, Date endTime,
            List<Long> idOfOrgList, Boolean divideIntoPeriods) {
        Session session = null;
        Transaction transaction = null;
        Map<Long, List<Long>> result = new HashMap<Long, List<Long>>();
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();
            String orgsCondition = " ";
            if (idOfOrgList.size() <= 10) {
                orgsCondition = " and c.idoforg in (:idOfOrg)";
            }
            Query query = session.createSQLQuery(
                    "SELECT pl.idoforg, count(DISTINCT pc.idofclient) AS unique_clients_count "
                            + " FROM cf_preorder_linkod pl "
                            + " JOIN cf_orders o ON o.idoforder = pl.idoforder AND o.idoforg = pl.idoforg "
                            + " JOIN cf_preorder_complex pc ON pc.guid = pl.preorderguid "
                            + " JOIN cf_clients c on c.idofclient = pc.idofclient "
                            + " WHERE o.state = 0 AND pc.preorderdate BETWEEN :startDate AND :endDate " + orgsCondition
                            + " AND c.idofclientgroup < :employees "
                            + " GROUP BY pl.idoforg ");

            if (idOfOrgList.size() <= 10) {
                query.setParameterList("idOfOrg", idOfOrgList);
            }

            query.setParameter("employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("startDate", startTime.getTime());
            query.setParameter("endDate", endTime.getTime());
            List<Object[]> list = query.list();

            if(divideIntoPeriods){
                Long idOfCurrentOrg = -1L;
                Date currentDay = startTime;
                Date limitDate = CalendarUtils.addOneDay(endTime);
                List<Long> uniqueClients = new LinkedList<Long>();
                for (Object[] row : list) {
                    Date dateFromRow = QUERY_DATE_FORMAT.parse((String)row[4]);
                    if (idOfCurrentOrg.equals(-1L) || !idOfCurrentOrg.equals(row[0])) {
                        result.put(idOfCurrentOrg, uniqueClients);
                        idOfCurrentOrg = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(row[0]);
                        uniqueClients = new LinkedList<Long>();
                        currentDay = startTime;
                    }
                    while (!CalendarUtils.isCurrentDay(currentDay, limitDate)) {
                        if (!CalendarUtils.isCurrentDay(currentDay, dateFromRow)) {
                            Long numberOfPreferential = 0L;
                            uniqueClients.add(numberOfPreferential);
                            currentDay = CalendarUtils.addOneDay(currentDay);
                        } else if (CalendarUtils.isWorkDateWithoutParser(true, dateFromRow)) {
                            Long numberOfPreferential = ((BigInteger) row[1]).longValue();
                            uniqueClients.add(numberOfPreferential);
                            currentDay = CalendarUtils.addOneDay(currentDay);
                            break;
                        }
                    }
                }
            } else {
                for (Object[] obj : list) {
                    Long idOfUniqueClient = DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(obj[1]);
                    List<Long> uniqueClients = new ArrayList<Long>(1);
                    uniqueClients.add(idOfUniqueClient);
                    result.put(DataBaseSafeConverterUtils.getLongFromBigIntegerOrNull(obj[0]), uniqueClients);
                }
            }
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in buildNumberOfPreorders: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    public static class ReportItem {

        private String orgNum;
        private String shortName;
        private String address;
        private String idOfOrg;
        private String code;
        private String district;
        private String typeOfBuilding;
        private String typeOfBuildingInternal;
        private String introductionQueue;
        private String orgStatus;
        private String studentsInDatabase;
        private String studentsWithMaps;
        private String parents;
        private String pedagogicalComposition;
        private String otherEmployees;

        private List<MonitoringOfItem> monitoringOfItemsFirst;
        private List<MonitoringOfItem> monitoringOfItemsSecond;
        private List<MonitoringOfItem> monitoringOfItemsThrid;

        public ReportItem() {
        }

        public ReportItem(String orgNum, String shortName, String address, String idOfOrg, String code, String district,
                String typeOfBuilding, String introductionQueue, String orgStatus, String studentsInDatabase,
                String studentsWithMaps, String parents, String pedagogicalComposition, String otherEmployees,
                List<MonitoringOfItem> monitoringOfItemsFirst) {
            this.orgNum = orgNum;
            this.shortName = shortName;
            this.address = address;
            this.idOfOrg = idOfOrg;
            this.code = code;
            this.district = district;
            this.typeOfBuilding = typeOfBuilding;
            this.introductionQueue = introductionQueue;
            this.orgStatus = orgStatus;
            this.studentsInDatabase = studentsInDatabase;
            this.studentsWithMaps = studentsWithMaps;
            this.parents = parents;
            this.pedagogicalComposition = pedagogicalComposition;
            this.otherEmployees = otherEmployees;
            this.monitoringOfItemsFirst = monitoringOfItemsFirst;
        }

        public String getOrgNum() {
            return orgNum;
        }

        public void setOrgNum(String orgNum) {
            this.orgNum = orgNum;
        }

        public String getShortName() {
            return shortName;
        }

        public void setShortName(String shortName) {
            this.shortName = shortName;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(String idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getDistrict() {
            return district;
        }

        public void setDistrict(String district) {
            this.district = district;
        }

        public String getTypeOfBuilding() {
            return typeOfBuilding;
        }

        public void setTypeOfBuilding(String typeOfBuilding) {
            this.typeOfBuilding = typeOfBuilding;
        }

        public String getIntroductionQueue() {
            return introductionQueue;
        }

        public void setIntroductionQueue(String introductionQueue) {
            this.introductionQueue = introductionQueue;
        }

        public String getOrgStatus() {
            return orgStatus;
        }

        public void setOrgStatus(String orgStatus) {
            this.orgStatus = orgStatus;
        }

        public String getStudentsInDatabase() {
            return studentsInDatabase;
        }

        public void setStudentsInDatabase(String studentsInDatabase) {
            this.studentsInDatabase = studentsInDatabase;
        }

        public String getStudentsWithMaps() {
            return studentsWithMaps;
        }

        public void setStudentsWithMaps(String studentsWithMaps) {
            this.studentsWithMaps = studentsWithMaps;
        }

        public String getParents() {
            return parents;
        }

        public void setParents(String parents) {
            this.parents = parents;
        }

        public String getPedagogicalComposition() {
            return pedagogicalComposition;
        }

        public void setPedagogicalComposition(String pedagogicalComposition) {
            this.pedagogicalComposition = pedagogicalComposition;
        }

        public String getOtherEmployees() {
            return otherEmployees;
        }

        public void setOtherEmployees(String otherEmployees) {
            this.otherEmployees = otherEmployees;
        }

        public List<MonitoringOfItem> getMonitoringOfItemsFirst() {
            return monitoringOfItemsFirst;
        }

        public void setMonitoringOfItemsFirst(List<MonitoringOfItem> monitoringOfItemsFirst) {
            this.monitoringOfItemsFirst = monitoringOfItemsFirst;
        }

        public String getTypeOfBuildingInternal() {
            return typeOfBuildingInternal;
        }

        public void setTypeOfBuildingInternal(String typeOfBuildingInternal) {
            this.typeOfBuildingInternal = typeOfBuildingInternal;
        }

        public List<MonitoringOfItem> getMonitoringOfItemsSecond() {
            return monitoringOfItemsSecond;
        }

        public void setMonitoringOfItemsSecond(List<MonitoringOfItem> monitoringOfItemsSecond) {
            this.monitoringOfItemsSecond = monitoringOfItemsSecond;
        }

        public List<MonitoringOfItem> getMonitoringOfItemsThrid() {
            return monitoringOfItemsThrid;
        }

        public void setMonitoringOfItemsThrid(List<MonitoringOfItem> monitoringOfItemsThrid) {
            this.monitoringOfItemsThrid = monitoringOfItemsThrid;
        }
    }

    public static class MonitoringOfItem {

        private Date sDate;
        private Long numberOfPassesStudents;
        private Long numberOfUniquePassesStudents;
        private Long numberOfPassesEmployees;
        private Long numberOfUniquePassesEmployees;
        private Long numberOfPassesGuardians;
        private Long numberOfUniquePassesGuardians;
        private Long summaryOfPasses;

        private Long numberOfLgotnoe;
        private Long numberOfLgotnoeFriendlyOrg;
        private Long numberOfLgotnoeOtherOrg;
        private Long numberOfReserve;
        private Long numberOfBuffetStudent;
        private Long numberOfBuffetGuardians;
        private Long numberOfSubFeedStudents;
        private Long numberOfSubFeedGuardians;
        private Long numberOfPaidStudents;
        private Long numberOfPaidGuardians;
        private Long numberOfPreorders;

        public MonitoringOfItem() {
            this.numberOfPassesStudents = 0L;
            this.numberOfUniquePassesStudents = 0L;
            this.numberOfPassesEmployees = 0L;
            this.numberOfUniquePassesEmployees = 0L;
            this.numberOfPassesGuardians = 0L;
            this.numberOfUniquePassesGuardians = 0L;
            this.summaryOfPasses = 0L;
            this.numberOfLgotnoe = 0L;
            this.numberOfLgotnoeFriendlyOrg = 0L;
            this.numberOfLgotnoeOtherOrg = 0L;
            this.numberOfReserve = 0L;
            this.numberOfBuffetStudent = 0L;
            this.numberOfBuffetGuardians = 0L;
            this.numberOfSubFeedStudents = 0L;
            this.numberOfSubFeedGuardians = 0L;
            this.numberOfPaidStudents = 0L;
            this.numberOfPaidGuardians = 0L;
            this.numberOfPreorders = 0L;
        }

        public MonitoringOfItem(Date sDate, Long numberOfPassesStudents, Long numberOfUniquePassesStudents,
                Long numberOfPassesEmployees, Long numberOfUniquePassesEmployees, Long numberOfPassesGuardians, Long numberOfUniquePassesGuardians,
                Long summaryOfPasses, Long numberOfLgotnoe, Long numberOfReserve, Long numberOfBuffetStudent,
                Long numberOfBuffetGuardians, Long numberOfSubFeedStudents, Long numberOfSubFeedGuardians,
                Long numberOfPaidStudents, Long numberOfPaidGuardians) {
            this.sDate = sDate;
            this.numberOfPassesStudents = numberOfPassesStudents;
            this.numberOfUniquePassesStudents = numberOfUniquePassesStudents;
            this.numberOfPassesEmployees = numberOfPassesEmployees;
            this.numberOfUniquePassesEmployees = numberOfUniquePassesEmployees;
            this.numberOfPassesGuardians = numberOfPassesGuardians;
            this.numberOfUniquePassesGuardians = numberOfUniquePassesGuardians;
            this.summaryOfPasses = summaryOfPasses;
            this.numberOfLgotnoe = numberOfLgotnoe;
            this.numberOfReserve = numberOfReserve;
            this.numberOfBuffetStudent = numberOfBuffetStudent;
            this.numberOfBuffetGuardians = numberOfBuffetGuardians;
            this.numberOfSubFeedStudents = numberOfSubFeedStudents;
            this.numberOfSubFeedGuardians = numberOfSubFeedGuardians;
            this.numberOfPaidStudents = numberOfPaidStudents;
            this.numberOfPaidGuardians = numberOfPaidGuardians;
        }

        public Date getsDate() {
            return sDate;
        }

        public void setsDate(Date sDate) {
            this.sDate = sDate;
        }

        public Long getNumberOfPassesStudents() {
            return numberOfPassesStudents;
        }

        public void setNumberOfPassesStudents(Long numberOfPassesStudents) {
            this.numberOfPassesStudents = numberOfPassesStudents;
        }

        public Long getNumberOfPassesEmployees() {
            return numberOfPassesEmployees;
        }

        public void setNumberOfPassesEmployees(Long numberOfPassesEmployees) {
            this.numberOfPassesEmployees = numberOfPassesEmployees;
        }

        public Long getNumberOfPassesGuardians() {
            return numberOfPassesGuardians;
        }

        public void setNumberOfPassesGuardians(Long numberOfPassesGuardians) {
            this.numberOfPassesGuardians = numberOfPassesGuardians;
        }

        public Long getSummaryOfPasses() {
            return summaryOfPasses;
        }

        public void setSummaryOfPasses(Long summaryOfPasses) {
            this.summaryOfPasses = summaryOfPasses;
        }

        public Long getNumberOfLgotnoe() {
            return numberOfLgotnoe;
        }

        public void setNumberOfLgotnoe(Long numberOfLgotnoe) {
            this.numberOfLgotnoe = numberOfLgotnoe;
        }

        public Long getNumberOfReserve() {
            return numberOfReserve;
        }

        public void setNumberOfReserve(Long numberOfReserve) {
            this.numberOfReserve = numberOfReserve;
        }

        public Long getNumberOfBuffetStudent() {
            return numberOfBuffetStudent;
        }

        public void setNumberOfBuffetStudent(Long numberOfBuffetStudent) {
            this.numberOfBuffetStudent = numberOfBuffetStudent;
        }

        public Long getNumberOfBuffetGuardians() {
            return numberOfBuffetGuardians;
        }

        public void setNumberOfBuffetGuardians(Long numberOfBuffetGuardians) {
            this.numberOfBuffetGuardians = numberOfBuffetGuardians;
        }

        public Long getNumberOfSubFeedStudents() {
            return numberOfSubFeedStudents;
        }

        public void setNumberOfSubFeedStudents(Long numberOfSubFeedStudents) {
            this.numberOfSubFeedStudents = numberOfSubFeedStudents;
        }

        public Long getNumberOfSubFeedGuardians() {
            return numberOfSubFeedGuardians;
        }

        public void setNumberOfSubFeedGuardians(Long numberOfSubFeedGuardians) {
            this.numberOfSubFeedGuardians = numberOfSubFeedGuardians;
        }

        public Long getNumberOfPaidStudents() {
            return numberOfPaidStudents;
        }

        public void setNumberOfPaidStudents(Long numberOfPaidStudents) {
            this.numberOfPaidStudents = numberOfPaidStudents;
        }

        public Long getNumberOfPaidGuardians() {
            return numberOfPaidGuardians;
        }

        public void setNumberOfPaidGuardians(Long numberOfPaidGuardians) {
            this.numberOfPaidGuardians = numberOfPaidGuardians;
        }

        public Long getNumberOfLgotnoeFriendlyOrg() {
            return numberOfLgotnoeFriendlyOrg;
        }

        public void setNumberOfLgotnoeFriendlyOrg(Long numberOfLgotnoeFriendlyOrg) {
            this.numberOfLgotnoeFriendlyOrg = numberOfLgotnoeFriendlyOrg;
        }

        public Long getNumberOfLgotnoeOtherOrg() {
            return numberOfLgotnoeOtherOrg;
        }

        public void setNumberOfLgotnoeOtherOrg(Long numberOfLgotnoeOtherOrg) {
            this.numberOfLgotnoeOtherOrg = numberOfLgotnoeOtherOrg;
        }

        public Long getNumberOfUniquePassesStudents() {
            return numberOfUniquePassesStudents;
        }

        public void setNumberOfUniquePassesStudents(Long numberOfUniquePassesStudents) {
            this.numberOfUniquePassesStudents = numberOfUniquePassesStudents;
        }

        public Long getNumberOfUniquePassesEmployees() {
            return numberOfUniquePassesEmployees;
        }

        public void setNumberOfUniquePassesEmployees(Long numberOfUniquePassesEmployees) {
            this.numberOfUniquePassesEmployees = numberOfUniquePassesEmployees;
        }

        public Long getNumberOfUniquePassesGuardians() {
            return numberOfUniquePassesGuardians;
        }

        public void setNumberOfUniquePassesGuardians(Long numberOfUniquePassesGuardians) {
            this.numberOfUniquePassesGuardians = numberOfUniquePassesGuardians;
        }

        public Long getNumberOfPreorders() {
            return numberOfPreorders;
        }

        public void setNumberOfPreorders(Long numberOfPreorders) {
            this.numberOfPreorders = numberOfPreorders;
        }
    }

    public static class NumberOfPasses {
        private Long students;
        private Long uniqueStudents;
        private Long employees;
        private Long uniqueEmployees;
        private Long guardians;
        private Long uniqueGuardians;

        public NumberOfPasses(Long students, Long uniqueStudents, Long employees, Long uniqueEmployees, Long guardians, Long uniqueGuardians) {
            this.students = students;
            this.uniqueStudents = uniqueStudents;
            this.employees = employees;
            this.uniqueEmployees = uniqueEmployees;
            this.guardians = guardians;
            this.uniqueGuardians = uniqueGuardians;
        }

        public NumberOfPasses(){
            students = 0L;
            uniqueStudents = 0L;
            employees = 0L;
            uniqueEmployees = 0L;
            guardians = 0L;
            uniqueGuardians = 0L;
        }

        public Long getStudents() {
            return students;
        }

        public void setStudents(Long students) {
            this.students = students;
        }

        public Long getUniqueStudents() {
            return uniqueStudents;
        }

        public void setUniqueStudents(Long uniqueStudents) {
            this.uniqueStudents = uniqueStudents;
        }

        public Long getEmployees() {
            return employees;
        }

        public void setEmployees(Long employees) {
            this.employees = employees;
        }

        public Long getUniqueEmployees() {
            return uniqueEmployees;
        }

        public void setUniqueEmployees(Long uniqueEmployees) {
            this.uniqueEmployees = uniqueEmployees;
        }

        public Long getGuardians() {
            return guardians;
        }

        public void setGuardians(Long guardians) {
            this.guardians = guardians;
        }

        public Long getUniqueGuardians() {
            return uniqueGuardians;
        }

        public void setUniqueGuardians(Long uniqueGuardians) {
            this.uniqueGuardians = uniqueGuardians;
        }
    }

    public static class NumberOfPreferential {
        private Long friendlyOrg;
        private Long othersOrg;
        private Long reserve;

        public NumberOfPreferential(Long friendlyOrg, Long othersOrg, Long reserve) {
            this.friendlyOrg = friendlyOrg;
            this.othersOrg = othersOrg;
            this.reserve = reserve;
        }

        public NumberOfPreferential(){
            friendlyOrg = 0L;
            othersOrg = 0L;
            reserve = 0L;
        }

        public Long getFriendlyOrg() {
            return friendlyOrg;
        }

        public void setFriendlyOrg(Long friendlyOrg) {
            this.friendlyOrg = friendlyOrg;
        }

        public Long getOthersOrg() {
            return othersOrg;
        }

        public void setOthersOrg(Long othersOrg) {
            this.othersOrg = othersOrg;
        }

        public Long getReserve() {
            return reserve;
        }

        public void setReserve(Long reserve) {
            this.reserve = reserve;
        }
    }

    public static class NumberOfStudentsAndGuardians {
        private Long students;
        private Long guardians;

        public NumberOfStudentsAndGuardians(Long students, Long guardians) {
            this.students = students;
            this.guardians = guardians;
        }

        public NumberOfStudentsAndGuardians(){
            students = 0L;
            guardians = 0L;
        }

        public Long getStudents() {
            return students;
        }

        public void setStudents(Long students) {
            this.students = students;
        }

        public Long getGuardians() {
            return guardians;
        }

        public void setGuardians(Long guardians) {
            this.guardians = guardians;
        }
    }

    public static class PeopleData {
        private Long parents;
        private Long pedagogicalComposition;
        private Long allPeoples;
        private Long other;
        private Long studentsWithCards;

        private PeopleData(Long parents, Long pedagogicalComposition, Long allPeoples,
                Long other, Long studentsWithCards) {
            this.parents = parents;
            this.pedagogicalComposition = pedagogicalComposition;
            this.allPeoples = allPeoples;
            this.other = other;
            this.studentsWithCards = studentsWithCards;
        }

        public Long getParents() {
            return parents;
        }

        public void setParents(Long parents) {
            this.parents = parents;
        }

        public Long getPedagogicalComposition() {
            return pedagogicalComposition;
        }

        public void setPedagogicalComposition(Long pedagogicalComposition) {
            this.pedagogicalComposition = pedagogicalComposition;
        }

        public Long getAllPeoples() {
            return allPeoples;
        }

        public void setAllPeoples(Long allPeoples) {
            this.allPeoples = allPeoples;
        }

        public Long getOther() {
            return other;
        }

        public void setOther(Long other) {
            this.other = other;
        }

        public Long getStudentsWithCards() {
            return studentsWithCards;
        }

        public void setStudentsWithCards(Long studentsWithCards) {
            this.studentsWithCards = studentsWithCards;
        }
    }
}
