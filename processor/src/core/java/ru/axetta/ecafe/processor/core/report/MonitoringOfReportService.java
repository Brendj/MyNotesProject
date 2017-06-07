/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.OrderDetail;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Query;
import org.hibernate.Session;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by anvarov on 04.05.2017.
 */
public class MonitoringOfReportService {

    public List<ReportItem> buildReportItems(Session session, Date startTime, List<Long> idOfOrgList) {

        List<DatePeriods> datePeriodsList = new ArrayList<DatePeriods>();

        int dayOfWeek = CalendarUtils.getDayOfWeek(startTime);

        if (dayOfWeek == 2) {
            DatePeriods datePeriods = new DatePeriods(startTime, CalendarUtils.addOneDay(startTime));
            datePeriodsList.add(datePeriods);
        } else if (dayOfWeek == 3) {
            DatePeriods datePeriods = new DatePeriods(CalendarUtils.addDays(startTime, -1), startTime);
            datePeriodsList.add(datePeriods);
            DatePeriods datePeriod = new DatePeriods(CalendarUtils.addDays(startTime, -1),
                    CalendarUtils.addOneDay(startTime));
            datePeriodsList.add(datePeriod);
        } else if (dayOfWeek == 4) {
            DatePeriods datePeriodss = new DatePeriods(CalendarUtils.addDays(startTime, -2),
                    CalendarUtils.addDays(startTime, -1));
            datePeriodsList.add(datePeriodss);
            DatePeriods datePeriods = new DatePeriods(CalendarUtils.addDays(startTime, -2), startTime);
            datePeriodsList.add(datePeriods);
            DatePeriods datePeriod = new DatePeriods(CalendarUtils.addDays(startTime, -2),
                    CalendarUtils.addOneDay(startTime));
            datePeriodsList.add(datePeriod);
        } else if (dayOfWeek == 5) {
            DatePeriods datePeriodsss = new DatePeriods(CalendarUtils.addDays(startTime, -3),
                    CalendarUtils.addDays(startTime, -2));
            datePeriodsList.add(datePeriodsss);
            DatePeriods datePeriodss = new DatePeriods(CalendarUtils.addDays(startTime, -3),
                    CalendarUtils.addDays(startTime, -1));
            datePeriodsList.add(datePeriodss);
            DatePeriods datePeriod = new DatePeriods(CalendarUtils.addDays(startTime, -3), startTime);
            datePeriodsList.add(datePeriod);
            DatePeriods datePeriods = new DatePeriods(CalendarUtils.addDays(startTime, -3),
                    CalendarUtils.addOneDay(startTime));
            datePeriodsList.add(datePeriods);
        } else if (dayOfWeek == 6) {
            DatePeriods datePeriod = new DatePeriods(CalendarUtils.addDays(startTime, -4),
                    CalendarUtils.addDays(startTime, -3));
            datePeriodsList.add(datePeriod);
            DatePeriods datePeriods = new DatePeriods(CalendarUtils.addDays(startTime, -4),
                    CalendarUtils.addDays(startTime, -2));
            datePeriodsList.add(datePeriods);
            DatePeriods datePeriodss = new DatePeriods(CalendarUtils.addDays(startTime, -4),
                    CalendarUtils.addDays(startTime, -1));
            datePeriodsList.add(datePeriodss);
            DatePeriods datePeriodsss = new DatePeriods(CalendarUtils.addDays(startTime, -4), startTime);
            datePeriodsList.add(datePeriodsss);
            DatePeriods datePeriodssss = new DatePeriods(CalendarUtils.addDays(startTime, -4),
                    CalendarUtils.addOneDay(startTime));
            datePeriodsList.add(datePeriodssss);
        } else if (dayOfWeek == 7) {
            DatePeriods datePeriodd = new DatePeriods(CalendarUtils.addDays(startTime, -5),
                    CalendarUtils.addDays(startTime, -4));
            datePeriodsList.add(datePeriodd);
            DatePeriods datePeriod = new DatePeriods(CalendarUtils.addDays(startTime, -5),
                    CalendarUtils.addDays(startTime, -3));
            datePeriodsList.add(datePeriod);
            DatePeriods datePeriods = new DatePeriods(CalendarUtils.addDays(startTime, -5),
                    CalendarUtils.addDays(startTime, -2));
            datePeriodsList.add(datePeriods);
            DatePeriods datePeriodss = new DatePeriods(CalendarUtils.addDays(startTime, -5),
                    CalendarUtils.addDays(startTime, -1));
            datePeriodsList.add(datePeriodss);
            DatePeriods datePeriodsss = new DatePeriods(CalendarUtils.addDays(startTime, -5), startTime);
            datePeriodsList.add(datePeriodsss);
            DatePeriods datePeriodssss = new DatePeriods(CalendarUtils.addDays(startTime, -5),
                    CalendarUtils.addOneDay(startTime));
            datePeriodsList.add(datePeriodssss);
        }

        List<ReportItem> reportItemList = getOrgData(session, idOfOrgList, datePeriodsList);

        return reportItemList;
    }

    public List<MonitoringOfItem> getMonitoringOfItems(Session session, DatePeriods datePeriod, Long idOfOrg) {
        List<MonitoringOfItem> monitoringOfItemList = new ArrayList<MonitoringOfItem>();

        MonitoringOfItem monitoringOfItem = new MonitoringOfItem();
        monitoringOfItem.setsDate(datePeriod.getStartDate());
        monitoringOfItem.setNumberOfPassesStudents(
                generateNumberOfPassesStudents(session, datePeriod.getStartDate(), datePeriod.getEndDate(), idOfOrg));
        monitoringOfItem.setNumberOfPassesEmployees(
                generateNumberOfPassesEmployees(session, datePeriod.getStartDate(), datePeriod.getEndDate(), idOfOrg));
        monitoringOfItem.setNumberOfPassesGuardians(
                generateNumberOfPassesGuardians(session, datePeriod.getStartDate(), datePeriod.getEndDate(), idOfOrg));

        monitoringOfItem.setSummaryOfPasses(
                monitoringOfItem.getNumberOfPassesStudents() + monitoringOfItem.getNumberOfPassesEmployees()
                        + monitoringOfItem.getNumberOfPassesGuardians());

        monitoringOfItem.setNumberOfLgotnoe(
                numberOfLgotnoe(idOfOrg, datePeriod.getStartDate(), datePeriod.getEndDate(), session));

        monitoringOfItem.setNumberOfReserve(
                numberOfReserve(idOfOrg, datePeriod.getStartDate(), datePeriod.getEndDate(), session));

        monitoringOfItem.setNumberOfBuffetStudent(
                numberOfBuffetStudent(idOfOrg, datePeriod.getStartDate(), datePeriod.getEndDate(), session));

        monitoringOfItem.setNumberOfBuffetGuardians(
                numberOfBuffetGuardians(idOfOrg, datePeriod.getStartDate(), datePeriod.getEndDate(), session));

        monitoringOfItem.setNumberOfSubFeedStudents(
                numberOfSubFeedStudents(idOfOrg, datePeriod.getStartDate(), datePeriod.getEndDate(), session));

        monitoringOfItem.setNumberOfSubFeedGuardians(
                numberOfSubFeedGuardians(idOfOrg, datePeriod.getStartDate(), datePeriod.getEndDate(), session));

        //Платные
        monitoringOfItem.setNumberOfPaidStudents(
                numberOfPaidStudents(idOfOrg, datePeriod.getStartDate(), datePeriod.getEndDate(), session));

        monitoringOfItem.setNumberOfPaidGuardians(
                numberOfPaidGuardians(idOfOrg, datePeriod.getStartDate(), datePeriod.getEndDate(), session));

        monitoringOfItemList.add(monitoringOfItem);

        return monitoringOfItemList;
    }

    public Long generateNumberOfPassesStudents(Session session, Date startTime, Date endTime, Long idOfOrg) {
        Query query = session.createSQLQuery(
                "SELECT count(*) FROM cf_enterevents WHERE idoforg = :idoforg AND idofclientgroup < 1100000000 "
                        + "AND passdirection IN (0,1,6,7) AND evtdatetime BETWEEN :startTime AND :endTime");

        query.setParameter("idoforg", idOfOrg);
        query.setParameter("startTime", startTime.getTime());
        query.setParameter("endTime", endTime.getTime());

        Long result = ((BigInteger) query.uniqueResult()).longValue();

        return result;
    }

    public Long generateNumberOfPassesEmployees(Session session, Date startTime, Date endTime, Long idOfOrg) {
        Query query = session.createSQLQuery(
                "SELECT count(*) FROM cf_enterevents WHERE idoforg = :idoforg AND idofclientgroup IN (1100000000, 1100000010, 1100000001, 1100000020, 1100000040, 1100000050) "
                        + "AND passdirection IN (0,1,6,7) AND evtdatetime BETWEEN :startTime AND :endTime");

        query.setParameter("idoforg", idOfOrg);
        query.setParameter("startTime", startTime.getTime());
        query.setParameter("endTime", endTime.getTime());

        Long result = ((BigInteger) query.uniqueResult()).longValue();

        return result;
    }

    public Long generateNumberOfPassesGuardians(Session session, Date startTime, Date endTime, Long idOfOrg) {
        Query query = session.createSQLQuery(
                "SELECT count(*) FROM cf_enterevents WHERE idoforg = :idoforg AND idofclientgroup = 1100000030 "
                        + "AND passdirection IN (0,1,6,7) AND evtdatetime BETWEEN :startTime AND :endTime");

        query.setParameter("idoforg", idOfOrg);
        query.setParameter("startTime", startTime.getTime());
        query.setParameter("endTime", endTime.getTime());

        Long result = ((BigInteger) query.uniqueResult()).longValue();

        return result;
    }

    //Питание Количество

    public Long numberOfLgotnoe(Long idOfOrg, Date startTime, Date endTime, Session session) {
        Query query = session.createSQLQuery("SELECT count(DISTINCT (cfo.idofclient)) "
                + "FROM cf_orders cfo LEFT JOIN cf_orderdetails cfod ON cfod.idoforg = cfo.idoforg AND cfod.idoforder = cfo.idoforder "
                + "LEFT JOIN cf_clients c ON cfo.idofclient = c.idofclient AND cfod.idoforg = c.idoforg "
                + "LEFT JOIN cf_clientgroups g ON g.idofclientgroup = c.idofclientgroup AND cfod.idoforg = g.idoforg "
                + "WHERE cfo.ordertype IN (4, 8) AND cfo.idoforg IN (:idoforg) AND cfo.state = 0 AND g.idofclientgroup < 1100000000 AND "
                + "cfo.createddate BETWEEN :startTime AND :endTime AND cfod.menutype >= :minType AND cfod.menutype <= :maxType  AND cfod.idofrule >= 0");
        query.setParameter("idoforg", idOfOrg);
        query.setParameter("startTime", startTime.getTime());
        query.setParameter("endTime", endTime.getTime());
        query.setParameter("minType", OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxType", OrderDetail.TYPE_COMPLEX_MAX);

        Long result = ((BigInteger) query.uniqueResult()).longValue();

        return result;
    }

    public Long numberOfReserve(Long idOfOrg, Date startTime, Date endTime, Session session) {
        Query query = session.createSQLQuery("SELECT count(DISTINCT (cfo.idofclient)) "
                + "FROM cf_orders cfo LEFT JOIN cf_orderdetails cfod ON cfod.idoforg = cfo.idoforg AND cfod.idoforder = cfo.idoforder "
                + "LEFT JOIN cf_clients c ON cfo.idofclient = c.idofclient AND cfod.idoforg = c.idoforg "
                + "LEFT JOIN cf_clientgroups g ON g.idofclientgroup = c.idofclientgroup AND cfod.idoforg = g.idoforg "
                + "WHERE cfo.ordertype IN (6) AND cfo.idoforg IN (:idoforg) AND cfo.state = 0 AND g.idofclientgroup < 1100000000 AND "
                + "cfo.createddate BETWEEN :startTime AND :endTime AND cfod.menutype >= :minType AND cfod.menutype <= :maxType AND cfod.idofrule >= 0");
        query.setParameter("idoforg", idOfOrg);
        query.setParameter("startTime", startTime.getTime());
        query.setParameter("endTime", endTime.getTime());
        query.setParameter("minType", OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxType", OrderDetail.TYPE_COMPLEX_MAX);

        Long result = ((BigInteger) query.uniqueResult()).longValue();

        return result;
    }

    public Long numberOfBuffetStudent(Long idOfOrg, Date startTime, Date endTime, Session session) {
        Query query = session.createSQLQuery("SELECT count(DISTINCT (cfo.idofclient)) "
                + "FROM cf_orders cfo LEFT JOIN cf_orderdetails cfod ON cfod.idoforg = cfo.idoforg AND cfod.idoforder = cfo.idoforder "
                + "LEFT JOIN cf_clients c ON cfo.idofclient = c.idofclient AND cfod.idoforg = c.idoforg "
                + "LEFT JOIN cf_clientgroups g ON g.idofclientgroup = c.idofclientgroup AND cfod.idoforg = g.idoforg "
                + "WHERE cfo.ordertype IN (1, 2, 0) AND cfo.idoforg IN (:idoforg) AND cfo.state = 0 AND g.idofclientgroup < 1100000000 AND "
                + "cfo.createddate BETWEEN :startTime AND :endTime AND cfod.menutype >= :minType AND cfod.menutype <= :maxType  AND cfod.idofrule >= 0");
        query.setParameter("idoforg", idOfOrg);
        query.setParameter("startTime", startTime.getTime());
        query.setParameter("endTime", endTime.getTime());
        query.setParameter("minType", OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxType", OrderDetail.TYPE_COMPLEX_MAX);

        Long result = ((BigInteger) query.uniqueResult()).longValue();

        return result;
    }

    public Long numberOfBuffetGuardians(Long idOfOrg, Date startTime, Date endTime, Session session) {
        Query query = session.createSQLQuery("SELECT count(DISTINCT (cfo.idofclient)) "
                + "FROM cf_orders cfo LEFT JOIN cf_orderdetails cfod ON cfod.idoforg = cfo.idoforg AND cfod.idoforder = cfo.idoforder "
                + "LEFT JOIN cf_clients c ON cfo.idofclient = c.idofclient AND cfod.idoforg = c.idoforg "
                + "LEFT JOIN cf_clientgroups g ON g.idofclientgroup = c.idofclientgroup AND cfod.idoforg = g.idoforg "
                + "WHERE cfo.ordertype IN (0, 1, 2) AND cfo.idoforg IN (:idoforg) AND cfo.state = 0 AND g.idofclientgroup < 1100000000 AND "
                + "cfo.createddate BETWEEN :startTime AND :endTime AND cfod.menutype >= :minType AND cfod.menutype <= :maxType  AND cfod.idofrule >= 0");
        query.setParameter("idoforg", idOfOrg);
        query.setParameter("startTime", startTime.getTime());
        query.setParameter("endTime", endTime.getTime());
        query.setParameter("minType", OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxType", OrderDetail.TYPE_COMPLEX_MAX);

        Long result = ((BigInteger) query.uniqueResult()).longValue();

        return result;
    }

    public Long numberOfSubFeedStudents(Long idOfOrg, Date startTime, Date endTime, Session session) {
        Query query = session.createSQLQuery("SELECT count(DISTINCT (cfo.idofclient)) "
                + "FROM CF_OrderDetails cfod LEFT OUTER JOIN cf_goods cfg ON cfod.IdOfGood = cfg.IdOfGood "
                + "LEFT OUTER JOIN CF_Orders cfo ON cfod.IdOfOrg = cfo.IdOfOrg AND cfod.IdOfOrder = cfo.IdOfOrder "
                + "LEFT OUTER JOIN CF_Orgs org ON cfo.IdOfOrg = org.IdOfOrg "
                + "LEFT JOIN cf_clients c ON cfo.idofclient = c.idofclient AND cfo.idoforg = c.idoforg "
                + "LEFT JOIN cf_clientgroups g ON g.idofclientgroup = c.idofclientgroup AND cfod.idoforg = g.idoforg "
                + "WHERE cfo.State = 0 AND cfod.State = 0 AND (cfo.OrderType IN (7)) AND (cfod.IdOfGood IS NOT NULL) AND "
                + "org.IdOfOrg = :idoforg AND (cfo.CreatedDate BETWEEN :startTime AND :endTime) AND cfod.MenuType >= :minType AND"
                + " cfod.MenuType <= :maxType AND g.idofclientgroup < 1100000000");

        query.setParameter("idoforg", idOfOrg);
        query.setParameter("startTime", startTime.getTime());
        query.setParameter("endTime", endTime.getTime());
        query.setParameter("minType", OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxType", OrderDetail.TYPE_COMPLEX_MAX);

        Long result = ((BigInteger) query.uniqueResult()).longValue();

        return result;
    }

    public Long numberOfSubFeedGuardians(Long idOfOrg, Date startTime, Date endTime, Session session) {
        Query query = session.createSQLQuery("SELECT count(DISTINCT (cfo.idofclient)) "
                + "FROM CF_OrderDetails cfod LEFT OUTER JOIN cf_goods cfg ON cfod.IdOfGood = cfg.IdOfGood "
                + "LEFT OUTER JOIN CF_Orders cfo ON cfod.IdOfOrg = cfo.IdOfOrg AND cfod.IdOfOrder = cfo.IdOfOrder "
                + "LEFT OUTER JOIN CF_Orgs org ON cfo.IdOfOrg = org.IdOfOrg "
                + "LEFT JOIN cf_clients c ON cfo.idofclient = c.idofclient AND cfo.idoforg = c.idoforg "
                + "LEFT JOIN cf_clientgroups g ON g.idofclientgroup = c.idofclientgroup AND cfod.idoforg = g.idoforg "
                + "WHERE cfo.State = 0 AND cfod.State = 0 AND (cfo.OrderType IN (7)) AND (cfod.IdOfGood IS NOT NULL) AND "
                + "org.IdOfOrg = :idoforg AND (cfo.CreatedDate BETWEEN :startTime AND :endTime) AND cfod.MenuType >= :minType AND "
                + "cfod.MenuType <= :maxType AND g.idofclientgroup  IN (1100000000, 1100000010, 1100000001, 1100000020, 1100000040, 1100000050)");

        query.setParameter("idoforg", idOfOrg);
        query.setParameter("startTime", startTime.getTime());
        query.setParameter("endTime", endTime.getTime());
        query.setParameter("minType", OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxType", OrderDetail.TYPE_COMPLEX_MAX);

        Long result = ((BigInteger) query.uniqueResult()).longValue();

        return result;
    }

    public Long numberOfPaidStudents(Long idOfOrg, Date startTime, Date endTime, Session session) {
        Query query = session.createSQLQuery("SELECT count(DISTINCT (cfo.idofclient)) "
                + "FROM CF_OrderDetails cfod LEFT OUTER JOIN cf_goods cfg ON cfod.IdOfGood = cfg.IdOfGood "
                + "LEFT OUTER JOIN CF_Orders cfo ON cfod.IdOfOrg = cfo.IdOfOrg AND cfod.IdOfOrder = cfo.IdOfOrder "
                + "LEFT OUTER JOIN CF_Orgs org ON cfo.IdOfOrg = org.IdOfOrg "
                + "LEFT JOIN cf_clients c ON cfo.idofclient = c.idofclient AND cfo.idoforg = c.idoforg "
                + "LEFT JOIN cf_clientgroups g ON g.idofclientgroup = c.idofclientgroup AND cfod.idoforg = g.idoforg "
                + "WHERE cfo.State = 0 AND cfod.State = 0 AND (cfo.OrderType IN (3)) AND (cfod.IdOfGood IS NOT NULL) AND "
                + "org.IdOfOrg = :idoforg AND (cfo.CreatedDate BETWEEN :startTime AND :endTime) AND cfod.MenuType >= :minType AND"
                + " cfod.MenuType <= :maxType AND g.idofclientgroup < 1100000000");

        query.setParameter("idoforg", idOfOrg);
        query.setParameter("startTime", startTime.getTime());
        query.setParameter("endTime", endTime.getTime());
        query.setParameter("minType", OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxType", OrderDetail.TYPE_COMPLEX_MAX);

        Long result = ((BigInteger) query.uniqueResult()).longValue();

        return result;
    }

    public Long numberOfPaidGuardians(Long idOfOrg, Date startTime, Date endTime, Session session) {
        Query query = session.createSQLQuery("SELECT count(DISTINCT (cfo.idofclient)) "
                + "FROM CF_OrderDetails cfod LEFT OUTER JOIN cf_goods cfg ON cfod.IdOfGood = cfg.IdOfGood "
                + "LEFT OUTER JOIN CF_Orders cfo ON cfod.IdOfOrg = cfo.IdOfOrg AND cfod.IdOfOrder = cfo.IdOfOrder "
                + "LEFT OUTER JOIN CF_Orgs org ON cfo.IdOfOrg = org.IdOfOrg "
                + "LEFT JOIN cf_clients c ON cfo.idofclient = c.idofclient AND cfo.idoforg = c.idoforg "
                + "LEFT JOIN cf_clientgroups g ON g.idofclientgroup = c.idofclientgroup AND cfod.idoforg = g.idoforg "
                + "WHERE cfo.State = 0 AND cfod.State = 0 AND (cfo.OrderType IN (3)) AND (cfod.IdOfGood IS NOT NULL) AND "
                + "org.IdOfOrg = :idoforg AND (cfo.CreatedDate BETWEEN :startTime AND :endTime) AND cfod.MenuType >= :minType AND"
                + " cfod.MenuType <= :maxType AND g.idofclientgroup IN (1100000000, 1100000010, 1100000001, 1100000020, 1100000040, 1100000050)");

        query.setParameter("idoforg", idOfOrg);
        query.setParameter("startTime", startTime.getTime());
        query.setParameter("endTime", endTime.getTime());
        query.setParameter("minType", OrderDetail.TYPE_COMPLEX_MIN);
        query.setParameter("maxType", OrderDetail.TYPE_COMPLEX_MAX);

        Long result = ((BigInteger) query.uniqueResult()).longValue();

        return result;
    }

    public List<ReportItem> getOrgData(Session session, List<Long> idOfOrgList, List<DatePeriods> datePeriodsList) {
        List<ReportItem> reportItemList = new ArrayList<ReportItem>();

        for (Long idOfOrg : idOfOrgList) {
            Org org = (Org) session.load(Org.class, idOfOrg);
            ReportItem reportItem = new ReportItem();
            reportItem.setOrgNum(org.getOrgNumberInName());
            reportItem.setShortName(org.getOfficialName());
            reportItem.setAddress(org.getAddress());
            reportItem.setIdOfOrg(String.valueOf(org.getIdOfOrg()));
            reportItem.setCode(String.valueOf(org.getUniqueAddressId()));
            reportItem.setDistrict(org.getDistrict());
            reportItem.setTypeOfBuilding(org.getType().toString());
            reportItem.setIntroductionQueue(org.getIntroductionQueue());

            reportItem.setStudentsInDatabase(allPeoples(org, session));
            reportItem.setStudentsWithMaps(studentsWithMaps(org, session));
            reportItem.setParents(parents(org, session));
            reportItem.setPedagogicalComposition(pedagogicalComposition(org, session));
            reportItem.setOtherEmployees(otherEmloyees(org, session));

            if (datePeriodsList.size() == 1) {
                reportItem.setMonitoringOfItemsMonday(getMonitoringOfItems(session, datePeriodsList.get(0), idOfOrg));
            }

            if (datePeriodsList.size() == 2) {
                reportItem.setMonitoringOfItemsMonday(getMonitoringOfItems(session, datePeriodsList.get(0), idOfOrg));
                reportItem.setMonitoringOfItemsTuesday(getMonitoringOfItems(session, datePeriodsList.get(1), idOfOrg));
            }

            if (datePeriodsList.size() == 3) {
                reportItem.setMonitoringOfItemsMonday(getMonitoringOfItems(session, datePeriodsList.get(0), idOfOrg));
                reportItem.setMonitoringOfItemsTuesday(getMonitoringOfItems(session, datePeriodsList.get(1), idOfOrg));
                reportItem
                        .setMonitoringOfItemsWednesday(getMonitoringOfItems(session, datePeriodsList.get(2), idOfOrg));
            }

            if (datePeriodsList.size() == 4) {
                reportItem.setMonitoringOfItemsMonday(getMonitoringOfItems(session, datePeriodsList.get(0), idOfOrg));
                reportItem.setMonitoringOfItemsTuesday(getMonitoringOfItems(session, datePeriodsList.get(1), idOfOrg));
                reportItem
                        .setMonitoringOfItemsWednesday(getMonitoringOfItems(session, datePeriodsList.get(2), idOfOrg));
                reportItem.setMonitoringOfItemsThursday(getMonitoringOfItems(session, datePeriodsList.get(3), idOfOrg));
            }

            if (datePeriodsList.size() == 5) {
                reportItem.setMonitoringOfItemsMonday(getMonitoringOfItems(session, datePeriodsList.get(0), idOfOrg));
                reportItem.setMonitoringOfItemsTuesday(getMonitoringOfItems(session, datePeriodsList.get(1), idOfOrg));
                reportItem
                        .setMonitoringOfItemsWednesday(getMonitoringOfItems(session, datePeriodsList.get(2), idOfOrg));
                reportItem.setMonitoringOfItemsThursday(getMonitoringOfItems(session, datePeriodsList.get(3), idOfOrg));
                reportItem.setMonitoringOfItemsFriday(getMonitoringOfItems(session, datePeriodsList.get(4), idOfOrg));
            }

            if (datePeriodsList.size() == 6) {
                reportItem.setMonitoringOfItemsMonday(getMonitoringOfItems(session, datePeriodsList.get(0), idOfOrg));
                reportItem.setMonitoringOfItemsTuesday(getMonitoringOfItems(session, datePeriodsList.get(1), idOfOrg));
                reportItem
                        .setMonitoringOfItemsWednesday(getMonitoringOfItems(session, datePeriodsList.get(2), idOfOrg));
                reportItem.setMonitoringOfItemsThursday(getMonitoringOfItems(session, datePeriodsList.get(3), idOfOrg));
                reportItem.setMonitoringOfItemsFriday(getMonitoringOfItems(session, datePeriodsList.get(4), idOfOrg));
                reportItem.setMonitoringOfItemsSaturday(getMonitoringOfItems(session, datePeriodsList.get(5), idOfOrg));
            }

            reportItemList.add(reportItem);
        }
        return reportItemList;
    }

    public String parents(Org org, Session session) {

        Query query = session.createSQLQuery(
                "SELECT count(DISTINCT(cfc.idofclient)) FROM cf_clients cfc WHERE cfc.idoforg = :idoforg AND cfc.IdOfClientGroup = 1100000030");
        query.setParameter("idoforg", org.getIdOfOrg());

        String result = String.valueOf(query.uniqueResult());

        return result;
    }

    public String pedagogicalComposition(Org org, Session session) {

        Query query = session.createSQLQuery("SELECT count(DISTINCT(cfc.idofclient)) FROM cf_clients cfc "
                + "WHERE cfc.idoforg = :idoforg AND cfc.IdOfClientGroup IN (1100000000, 1100000010)");
        query.setParameter("idoforg", org.getIdOfOrg());

        String result = String.valueOf(query.uniqueResult());

        return result;
    }

    public String allPeoples(Org org, Session session) {
        Query query = session.createSQLQuery("SELECT count(DISTINCT(cfc.idofclient)) "
                + "FROM cf_clients cfc WHERE cfc.idoforg = :idoforg AND cfc.idofclientgroup < 1100000000");
        query.setParameter("idoforg", org.getIdOfOrg());

        String result = String.valueOf(query.uniqueResult());

        return result;
    }

    public String otherEmloyees(Org org, Session session) {
        Query query = session.createSQLQuery("SELECT count(DISTINCT(cfc.idofclient)) FROM cf_clients cfc "
                + "WHERE cfc.idoforg = :idoforg AND cfc.idofclientgroup IN (1100000050, 1100000020, 1100000040)");
        query.setParameter("idoforg", org.getIdOfOrg());

        String result = String.valueOf(query.uniqueResult());

        return result;
    }

    public String studentsWithMaps(Org org, Session session) {
        Query query = session.createSQLQuery(
                "SELECT count(DISTINCT(cfca.idofclient ))  FROM cf_clients cfc LEFT JOIN cf_cards cfca ON cfc.idofclient = cfca.idofclient "
                        + " WHERE cfc.idoforg = :idoforg AND cfc.idofclientgroup < 1100000000");
        query.setParameter("idoforg", org.getIdOfOrg());

        String result = String.valueOf(query.uniqueResult());

        return result;
    }

    public static class ReportItem {

        public String orgNum;
        public String shortName;
        public String address;
        public String idOfOrg;
        public String code;
        public String district;
        public String typeOfBuilding;
        public String introductionQueue;
        public String studentsInDatabase;
        public String studentsWithMaps;
        public String parents;
        public String pedagogicalComposition;
        public String otherEmployees;

        public List<MonitoringOfItem> monitoringOfItemsMonday;
        public List<MonitoringOfItem> monitoringOfItemsTuesday;
        public List<MonitoringOfItem> monitoringOfItemsWednesday;
        public List<MonitoringOfItem> monitoringOfItemsThursday;
        public List<MonitoringOfItem> monitoringOfItemsFriday;
        public List<MonitoringOfItem> monitoringOfItemsSaturday;

        public ReportItem() {
        }

        public ReportItem(String orgNum, String shortName, String address, String idOfOrg, String code, String district,
                String typeOfBuilding, String introductionQueue, String studentsInDatabase, String studentsWithMaps,
                String parents, String pedagogicalComposition, String otherEmployees,
                List<MonitoringOfItem> monitoringOfItemsMonday, List<MonitoringOfItem> monitoringOfItemsTuesday,
                List<MonitoringOfItem> monitoringOfItemsWednesday, List<MonitoringOfItem> monitoringOfItemsThursday,
                List<MonitoringOfItem> monitoringOfItemsFriday, List<MonitoringOfItem> monitoringOfItemsSaturday) {
            this.orgNum = orgNum;
            this.shortName = shortName;
            this.address = address;
            this.idOfOrg = idOfOrg;
            this.code = code;
            this.district = district;
            this.typeOfBuilding = typeOfBuilding;
            this.introductionQueue = introductionQueue;
            this.studentsInDatabase = studentsInDatabase;
            this.studentsWithMaps = studentsWithMaps;
            this.parents = parents;
            this.pedagogicalComposition = pedagogicalComposition;
            this.otherEmployees = otherEmployees;
            this.monitoringOfItemsMonday = monitoringOfItemsMonday;
            this.monitoringOfItemsTuesday = monitoringOfItemsTuesday;
            this.monitoringOfItemsWednesday = monitoringOfItemsWednesday;
            this.monitoringOfItemsThursday = monitoringOfItemsThursday;
            this.monitoringOfItemsFriday = monitoringOfItemsFriday;
            this.monitoringOfItemsSaturday = monitoringOfItemsSaturday;
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

        public List<MonitoringOfItem> getMonitoringOfItemsMonday() {
            return monitoringOfItemsMonday;
        }

        public void setMonitoringOfItemsMonday(List<MonitoringOfItem> monitoringOfItemsMonday) {
            this.monitoringOfItemsMonday = monitoringOfItemsMonday;
        }

        public List<MonitoringOfItem> getMonitoringOfItemsTuesday() {
            return monitoringOfItemsTuesday;
        }

        public void setMonitoringOfItemsTuesday(List<MonitoringOfItem> monitoringOfItemsTuesday) {
            this.monitoringOfItemsTuesday = monitoringOfItemsTuesday;
        }

        public List<MonitoringOfItem> getMonitoringOfItemsWednesday() {
            return monitoringOfItemsWednesday;
        }

        public void setMonitoringOfItemsWednesday(List<MonitoringOfItem> monitoringOfItemsWednesday) {
            this.monitoringOfItemsWednesday = monitoringOfItemsWednesday;
        }

        public List<MonitoringOfItem> getMonitoringOfItemsThursday() {
            return monitoringOfItemsThursday;
        }

        public void setMonitoringOfItemsThursday(List<MonitoringOfItem> monitoringOfItemsThursday) {
            this.monitoringOfItemsThursday = monitoringOfItemsThursday;
        }

        public List<MonitoringOfItem> getMonitoringOfItemsFriday() {
            return monitoringOfItemsFriday;
        }

        public void setMonitoringOfItemsFriday(List<MonitoringOfItem> monitoringOfItemsFriday) {
            this.monitoringOfItemsFriday = monitoringOfItemsFriday;
        }

        public List<MonitoringOfItem> getMonitoringOfItemsSaturday() {
            return monitoringOfItemsSaturday;
        }

        public void setMonitoringOfItemsSaturday(List<MonitoringOfItem> monitoringOfItemsSaturday) {
            this.monitoringOfItemsSaturday = monitoringOfItemsSaturday;
        }
    }

    public static class MonitoringOfItem {

        private Date sDate;
        private Long numberOfPassesStudents;
        private Long numberOfPassesEmployees;
        private Long numberOfPassesGuardians;
        private Long summaryOfPasses;

        private Long numberOfLgotnoe;
        private Long numberOfReserve;
        private Long numberOfBuffetStudent;
        private Long numberOfBuffetGuardians;
        private Long numberOfSubFeedStudents;
        private Long numberOfSubFeedGuardians;
        private Long numberOfPaidStudents;
        private Long numberOfPaidGuardians;

        public MonitoringOfItem() {
        }

        public MonitoringOfItem(Date sDate, Long numberOfPassesStudents, Long numberOfPassesEmployees,
                Long numberOfPassesGuardians, Long summaryOfPasses, Long numberOfLgotnoe, Long numberOfReserve,
                Long numberOfBuffetStudent, Long numberOfBuffetGuardians, Long numberOfSubFeedStudents,
                Long numberOfSubFeedGuardians, Long numberOfPaidStudents, Long numberOfPaidGuardians) {
            this.sDate = sDate;
            this.numberOfPassesStudents = numberOfPassesStudents;
            this.numberOfPassesEmployees = numberOfPassesEmployees;
            this.numberOfPassesGuardians = numberOfPassesGuardians;
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
    }

    public static class DatePeriods {

        private Date startDate;
        private Date endDate;

        public DatePeriods() {
        }

        public DatePeriods(Date startDate, Date endDate) {
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public Date getStartDate() {
            return startDate;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public Date getEndDate() {
            return endDate;
        }

        public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }
    }
}
