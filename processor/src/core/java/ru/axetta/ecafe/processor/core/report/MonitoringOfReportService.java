/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.service.org.OrgService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.util.*;

/**
 * Created by anvarov on 04.05.2017.
 */
public class MonitoringOfReportService {
    final private static Logger logger = LoggerFactory.getLogger(MonitoringOfReportService.class);
    private final static Integer ORGS_AMOUNT_FOR_REPORT = 20;
    public List<ReportItem> buildReportItems(Date startTime, List<Long> idOfOrgList) {
        List<DatePeriods> datePeriodsList = new ArrayList<DatePeriods>();

        int dayOfWeek = CalendarUtils.getDayOfWeek(startTime);

        if (dayOfWeek == 2) {
            DatePeriods datePeriods = new DatePeriods(startTime, CalendarUtils.addOneDay(startTime));
            datePeriodsList.add(datePeriods);
        } else if (dayOfWeek == 3) {
            DatePeriods datePeriods = new DatePeriods(CalendarUtils.addDays(startTime, -1), startTime);
            datePeriodsList.add(datePeriods);
            DatePeriods datePeriod = new DatePeriods(startTime, CalendarUtils.addOneDay(startTime));
            datePeriodsList.add(datePeriod);
        } else if (dayOfWeek == 4) {
            DatePeriods datePeriodss = new DatePeriods(CalendarUtils.addDays(startTime, -2),
                    CalendarUtils.addDays(startTime, -1));
            datePeriodsList.add(datePeriodss);
            DatePeriods datePeriods = new DatePeriods(CalendarUtils.addDays(startTime, -1), startTime);
            datePeriodsList.add(datePeriods);
            DatePeriods datePeriod = new DatePeriods(startTime, CalendarUtils.addOneDay(startTime));
            datePeriodsList.add(datePeriod);
        } else if (dayOfWeek == 5) {
            DatePeriods datePeriodsss = new DatePeriods(CalendarUtils.addDays(startTime, -3),
                    CalendarUtils.addDays(startTime, -2));
            datePeriodsList.add(datePeriodsss);
            DatePeriods datePeriodss = new DatePeriods(CalendarUtils.addDays(startTime, -2),
                    CalendarUtils.addDays(startTime, -1));
            datePeriodsList.add(datePeriodss);
            DatePeriods datePeriod = new DatePeriods(CalendarUtils.addDays(startTime, -1), startTime);
            datePeriodsList.add(datePeriod);
            DatePeriods datePeriods = new DatePeriods(startTime, CalendarUtils.addOneDay(startTime));
            datePeriodsList.add(datePeriods);
        } else if (dayOfWeek == 6) {
            DatePeriods datePeriod = new DatePeriods(CalendarUtils.addDays(startTime, -4),
                    CalendarUtils.addDays(startTime, -3));
            datePeriodsList.add(datePeriod);
            DatePeriods datePeriods = new DatePeriods(CalendarUtils.addDays(startTime, -3),
                    CalendarUtils.addDays(startTime, -2));
            datePeriodsList.add(datePeriods);
            DatePeriods datePeriodss = new DatePeriods(CalendarUtils.addDays(startTime, -2),
                    CalendarUtils.addDays(startTime, -1));
            datePeriodsList.add(datePeriodss);
            DatePeriods datePeriodsss = new DatePeriods(CalendarUtils.addDays(startTime, -1), startTime);
            datePeriodsList.add(datePeriodsss);
            DatePeriods datePeriodssss = new DatePeriods(startTime, CalendarUtils.addOneDay(startTime));
            datePeriodsList.add(datePeriodssss);
        } else if (dayOfWeek == 7) {
            DatePeriods datePeriodd = new DatePeriods(CalendarUtils.addDays(startTime, -5),
                    CalendarUtils.addDays(startTime, -4));
            datePeriodsList.add(datePeriodd);
            DatePeriods datePeriod = new DatePeriods(CalendarUtils.addDays(startTime, -4),
                    CalendarUtils.addDays(startTime, -3));
            datePeriodsList.add(datePeriod);
            DatePeriods datePeriods = new DatePeriods(CalendarUtils.addDays(startTime, -3),
                    CalendarUtils.addDays(startTime, -2));
            datePeriodsList.add(datePeriods);
            DatePeriods datePeriodss = new DatePeriods(CalendarUtils.addDays(startTime, -2),
                    CalendarUtils.addDays(startTime, -1));
            datePeriodsList.add(datePeriodss);
            DatePeriods datePeriodsss = new DatePeriods(CalendarUtils.addDays(startTime, -1), startTime);
            datePeriodsList.add(datePeriodsss);
            DatePeriods datePeriodssss = new DatePeriods(startTime, CalendarUtils.addOneDay(startTime));
            datePeriodsList.add(datePeriodssss);
        }

        List<ReportItem> reportItemList = getOrgData(idOfOrgList, datePeriodsList);

        return reportItemList;
    }

    private List<List<MonitoringOfItem>> getMonitoringOfItems(List<DatePeriods> datePeriodList,
            Long idOfOrg, Map<Long, List<NumberOfPasses>> numberOfPassesMap,
            Map<Long, List<NumberOfPreferential>> numberOfPreferentialMap,
            Map<Long, List<NumberOfStudentsAndGuardians>> numberOfBuffetMap,
            Map<Long, List<NumberOfStudentsAndGuardians>> numberOfSubfeedMap,
            Map<Long, List<NumberOfStudentsAndGuardians>> numberOfPaidMap) {
        List<List<MonitoringOfItem>> monitoringOfItemList = new ArrayList<List<MonitoringOfItem>>();

        for (DatePeriods datePeriod : datePeriodList) {
            List<MonitoringOfItem> monitoringOfItems = new ArrayList<MonitoringOfItem>();
            MonitoringOfItem monitoringOfItem = new MonitoringOfItem();
            monitoringOfItem.setsDate(datePeriod.getStartDate());
            monitoringOfItems.add(monitoringOfItem);
            monitoringOfItemList.add(monitoringOfItems);
        }

        // passes
        List<NumberOfPasses> numberOfPassesList = numberOfPassesMap.get(idOfOrg);
        for (int i = 0; i < numberOfPassesList.size(); i++) {
            NumberOfPasses numberOfPasses = numberOfPassesList.get(i);
            MonitoringOfItem monitoringOfItem = monitoringOfItemList.get(i).get(0);

            monitoringOfItem.setNumberOfPassesStudents(numberOfPasses.getStudents());
            monitoringOfItem.setNumberOfUniquePassesStudents(numberOfPasses.getUniqueStudents());
            monitoringOfItem.setNumberOfPassesEmployees(numberOfPasses.getEmployees());
            monitoringOfItem.setNumberOfUniquePassesEmployees(numberOfPasses.getUniqueEmployees());
            monitoringOfItem.setNumberOfPassesGuardians(numberOfPasses.getGuardians());
            monitoringOfItem.setNumberOfUniquePassesGuardians(numberOfPasses.getUniqueGuardians());
            monitoringOfItem.setSummaryOfPasses(
                    monitoringOfItem.getNumberOfPassesStudents() + monitoringOfItem.getNumberOfPassesEmployees()
                            + monitoringOfItem.getNumberOfPassesGuardians());
        }

        // lgotnoe
        List<NumberOfPreferential> numberOfLgotnoeList = numberOfPreferentialMap.get(idOfOrg);
        for (int i = 0; i < numberOfLgotnoeList.size(); i++) {
            NumberOfPreferential numberOfPreferential = numberOfLgotnoeList.get(i);
            MonitoringOfItem monitoringOfItem = monitoringOfItemList.get(i).get(0);

            monitoringOfItem.setNumberOfLgotnoeFriendlyOrg(numberOfPreferential.getFriendlyOrg());
            monitoringOfItem.setNumberOfLgotnoeOtherOrg(numberOfPreferential.getOthersOrg());
            monitoringOfItem.setNumberOfLgotnoe(monitoringOfItem.getNumberOfLgotnoeFriendlyOrg() + monitoringOfItem.getNumberOfLgotnoeOtherOrg());
            monitoringOfItem.setNumberOfReserve(numberOfPreferential.getReserve());
        }

        // buffet
        List<NumberOfStudentsAndGuardians> numberOfBuffetList = numberOfBuffetMap.get(idOfOrg);
        for (int i = 0; i < numberOfBuffetList.size(); i++) {
            NumberOfStudentsAndGuardians numberOfBuffet = numberOfBuffetList.get(i);
            MonitoringOfItem monitoringOfItem = monitoringOfItemList.get(i).get(0);

            monitoringOfItem.setNumberOfBuffetStudent(numberOfBuffet.getStudents());
            monitoringOfItem.setNumberOfBuffetGuardians(numberOfBuffet.getGuardians());
        }

        // subfeed
        List<NumberOfStudentsAndGuardians> numberOfSubfeedList = numberOfSubfeedMap.get(idOfOrg);
        for (int i = 0; i < numberOfSubfeedList.size(); i++) {
            NumberOfStudentsAndGuardians numberOfSubfeed = numberOfSubfeedList.get(i);
            MonitoringOfItem monitoringOfItem = monitoringOfItemList.get(i).get(0);

            monitoringOfItem.setNumberOfSubFeedStudents(numberOfSubfeed.getStudents());
            monitoringOfItem.setNumberOfSubFeedGuardians(numberOfSubfeed.getGuardians());
        }

        // paid
        List<NumberOfStudentsAndGuardians> numberOfPaidList = numberOfPaidMap.get(idOfOrg);
        for (int i = 0; i < numberOfPaidList.size(); i++) {
            NumberOfStudentsAndGuardians numberOfPaid = numberOfPaidList.get(i);
            MonitoringOfItem monitoringOfItem = monitoringOfItemList.get(i).get(0);

            monitoringOfItem.setNumberOfPaidStudents(numberOfPaid.getStudents());
            monitoringOfItem.setNumberOfPaidGuardians(numberOfPaid.getGuardians());
        }

        return monitoringOfItemList;
    }

    private Map<Long, List<NumberOfPasses>> generateNumberOfPasses(List<DatePeriods> datePeriodList, List<Long> idOfOrgList) {
        Map<Long, List<NumberOfPasses>> result = new HashMap<Long, List<NumberOfPasses>>();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();
            Date wholeStartTime = datePeriodList.get(0).getStartDate();
            Date wholeEndTime = datePeriodList.get(datePeriodList.size()-1).getEndDate();

            String selectFields = "";

            if (datePeriodList.size() >= 1) {
                selectFields +=
                        "count(DISTINCT(CASE WHEN ((idofclientgroup < :employees) AND (evtdatetime BETWEEN :startTimeMonday AND :endTimeMonday)) "
                                + "                   THEN idofenterevent END)) AS number_of_passes_students_monday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup < :employees) AND (evtdatetime BETWEEN :startTimeMonday AND :endTimeMonday)) "
                                + "                   THEN idofclient END)) AS number_of_unique_passes_students_monday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup IN (:employees, :administration, :employee, :techEmployees, :visitors, :others))"
                                + "                   AND (evtdatetime BETWEEN :startTimeMonday AND :endTimeMonday)) "
                                + "                   THEN idofenterevent END)) AS number_of_passes_employees_monday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup IN (:employees, :administration, :employee, :techEmployees, :visitors, :others))"
                                + "                   AND (evtdatetime BETWEEN :startTimeMonday AND :endTimeMonday)) "
                                + "                   THEN idofclient END)) AS number_of_unique_passes_employees_monday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup = :parents) AND (evtdatetime BETWEEN :startTimeMonday AND :endTimeMonday))"
                                + "                   THEN idofenterevent END)) AS number_of_passes_guardians_monday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup = :parents) AND (evtdatetime BETWEEN :startTimeMonday AND :endTimeMonday))"
                                + "                   THEN idofclient END)) AS number_of_unique_passes_guardians_monday ";
            }

            if (datePeriodList.size() >= 2) {
                selectFields +=
                        ",count(DISTINCT(CASE WHEN ((idofclientgroup < :employees) AND (evtdatetime BETWEEN :startTimeTuesday AND :endTimeTuesday)) "
                                + "                   THEN idofenterevent END)) AS number_of_passes_students_tuesday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup < :employees) AND (evtdatetime BETWEEN :startTimeTuesday AND :endTimeTuesday)) "
                                + "                   THEN idofclient END)) AS number_of_unique_passes_students_tuesday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup IN (:employees, :administration, :employee, :techEmployees, :visitors, :others))"
                                + "                   AND (evtdatetime BETWEEN :startTimeTuesday AND :endTimeTuesday)) "
                                + "                   THEN idofenterevent END)) AS number_of_passes_employees_tuesday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup IN (:employees, :administration, :employee, :techEmployees, :visitors, :others))"
                                + "                   AND (evtdatetime BETWEEN :startTimeTuesday AND :endTimeTuesday)) "
                                + "                   THEN idofclient END)) AS number_of_unique_passes_employees_tuesday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup = :parents) AND (evtdatetime BETWEEN :startTimeTuesday AND :endTimeTuesday))"
                                + "                   THEN idofenterevent END)) AS number_of_passes_guardians_tuesday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup = :parents) AND (evtdatetime BETWEEN :startTimeTuesday AND :endTimeTuesday))"
                                + "                   THEN idofclient END)) AS number_of_unique_passes_guardians_tuesday ";
            }

            if (datePeriodList.size() >= 3) {
                selectFields +=
                        ",count(DISTINCT(CASE WHEN ((idofclientgroup < :employees) AND (evtdatetime BETWEEN :startTimeWednesday AND :endTimeWednesday)) "
                                + "                   THEN idofenterevent END)) AS number_of_passes_students_wednesday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup < :employees) AND (evtdatetime BETWEEN :startTimeWednesday AND :endTimeWednesday)) "
                                + "                   THEN idofclient END)) AS number_of_unique_passes_students_wednesday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup IN (:employees, :administration, :employee, :techEmployees, :visitors, :others))"
                                + "                   AND (evtdatetime BETWEEN :startTimeWednesday AND :endTimeWednesday)) "
                                + "                   THEN idofenterevent END)) AS number_of_passes_employees_wednesday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup IN (:employees, :administration, :employee, :techEmployees, :visitors, :others))"
                                + "                   AND (evtdatetime BETWEEN :startTimeWednesday AND :endTimeWednesday)) "
                                + "                   THEN idofclient END)) AS number_of_unique_passes_employees_wednesday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup = :parents) AND (evtdatetime BETWEEN :startTimeWednesday AND :endTimeWednesday))"
                                + "                   THEN idofenterevent END)) AS number_of_passes_guardians_wednesday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup = :parents) AND (evtdatetime BETWEEN :startTimeWednesday AND :endTimeWednesday))"
                                + "                   THEN idofclient END)) AS number_of_unique_passes_guardians_wednesday ";
            }

            if (datePeriodList.size() >= 4) {
                selectFields +=
                        ",count(DISTINCT(CASE WHEN ((idofclientgroup < :employees) AND (evtdatetime BETWEEN :startTimeThursday AND :endTimeThursday)) "
                                + "                   THEN idofenterevent END)) AS number_of_passes_students_thursday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup < :employees) AND (evtdatetime BETWEEN :startTimeThursday AND :endTimeThursday)) "
                                + "                   THEN idofclient END)) AS number_of_unique_passes_students_thursday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup IN (:employees, :administration, :employee, :techEmployees, :visitors, :others))"
                                + "                   AND (evtdatetime BETWEEN :startTimeThursday AND :endTimeThursday)) "
                                + "                   THEN idofenterevent END)) AS number_of_passes_employees_thursday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup IN (:employees, :administration, :employee, :techEmployees, :visitors, :others))"
                                + "                   AND (evtdatetime BETWEEN :startTimeThursday AND :endTimeThursday)) "
                                + "                   THEN idofclient END)) AS number_of_unique_passes_employees_thursday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup = :parents) AND (evtdatetime BETWEEN :startTimeThursday AND :endTimeThursday))"
                                + "                   THEN idofenterevent END)) AS number_of_passes_guardians_thursday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup = :parents) AND (evtdatetime BETWEEN :startTimeThursday AND :endTimeThursday))"
                                + "                   THEN idofclient END)) AS number_of_unique_passes_guardians_thursday ";
            }

            if (datePeriodList.size() >= 5) {
                selectFields +=
                        ",count(DISTINCT(CASE WHEN ((idofclientgroup < :employees) AND (evtdatetime BETWEEN :startTimeFriday AND :endTimeFriday)) "
                                + "                   THEN idofenterevent END)) AS number_of_passes_students_friday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup < :employees) AND (evtdatetime BETWEEN :startTimeFriday AND :endTimeFriday)) "
                                + "                   THEN idofclient END)) AS number_of_unique_passes_students_friday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup IN (:employees, :administration, :employee, :techEmployees, :visitors, :others))"
                                + "                   AND (evtdatetime BETWEEN :startTimeFriday AND :endTimeFriday)) "
                                + "                   THEN idofenterevent END)) AS number_of_passes_employees_friday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup IN (:employees, :administration, :employee, :techEmployees, :visitors, :others))"
                                + "                   AND (evtdatetime BETWEEN :startTimeFriday AND :endTimeFriday)) "
                                + "                   THEN idofclient END)) AS number_of_unique_passes_employees_friday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup = :parents) AND (evtdatetime BETWEEN :startTimeFriday AND :endTimeFriday))"
                                + "                   THEN idofenterevent END)) AS number_of_passes_guardians_friday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup = :parents) AND (evtdatetime BETWEEN :startTimeFriday AND :endTimeFriday))"
                                + "                   THEN idofclient END)) AS number_of_unique_passes_guardians_friday ";
            }

            if (datePeriodList.size() == 6) {
                selectFields +=
                        ",count(DISTINCT(CASE WHEN ((idofclientgroup < :employees) AND (evtdatetime BETWEEN :startTimeSaturday AND :endTimeSaturday)) "
                                + "                   THEN idofenterevent END)) AS number_of_passes_students_saturday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup < :employees) AND (evtdatetime BETWEEN :startTimeSaturday AND :endTimeSaturday)) "
                                + "                   THEN idofclient END)) AS number_of_unique_passes_students_saturday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup IN (:employees, :administration, :employee, :techEmployees, :visitors, :others))"
                                + "                   AND (evtdatetime BETWEEN :startTimeSaturday AND :endTimeSaturday)) "
                                + "                   THEN idofenterevent END)) AS number_of_passes_employees_saturday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup IN (:employees, :administration, :employee, :techEmployees, :visitors, :others))"
                                + "                   AND (evtdatetime BETWEEN :startTimeSaturday AND :endTimeSaturday)) "
                                + "                   THEN idofclient END)) AS number_of_unique_passes_employees_saturday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup = :parents) AND (evtdatetime BETWEEN :startTimeSaturday AND :endTimeSaturday))"
                                + "                   THEN idofenterevent END)) AS number_of_passes_guardians_saturday, "
                                + "count(DISTINCT(CASE WHEN ((idofclientgroup = :parents) AND (evtdatetime BETWEEN :startTimeSaturday AND :endTimeSaturday))"
                                + "                   THEN idofclient END)) AS number_of_unique_passes_guardians_saturday ";
            }
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
                            + "group by idoforg";

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
            query.setParameter("startTime", wholeStartTime.getTime());
            query.setParameter("endTime", wholeEndTime.getTime());

            updateQueryDates(query, datePeriodList);

            List<Object[]> list = query.list();

            for (Object[] object : list) {
                List<NumberOfPasses> numberOfPassesList = new ArrayList<NumberOfPasses>();
                if (datePeriodList.size() >= 1) {
                    NumberOfPasses numberOfPassesMonday = new NumberOfPasses(((BigInteger) object[1]).longValue(), ((BigInteger) object[2]).longValue(), ((BigInteger) object[3]).longValue(),
                            ((BigInteger) object[4]).longValue(), ((BigInteger) object[5]).longValue(), ((BigInteger) object[6]).longValue());
                    numberOfPassesList.add(numberOfPassesMonday);
                }
                if (datePeriodList.size() >= 2) {
                    NumberOfPasses numberOfPassesTuesday = new NumberOfPasses(((BigInteger) object[7]).longValue(), ((BigInteger) object[8]).longValue(), ((BigInteger) object[9]).longValue(),
                            ((BigInteger) object[10]).longValue(), ((BigInteger) object[11]).longValue(), ((BigInteger) object[12]).longValue());
                    numberOfPassesList.add(numberOfPassesTuesday);
                }
                if (datePeriodList.size() >= 3) {
                    NumberOfPasses numberOfPassesWednesday = new NumberOfPasses(((BigInteger) object[13]).longValue(), ((BigInteger) object[14]).longValue(), ((BigInteger) object[15]).longValue(),
                            ((BigInteger) object[16]).longValue(), ((BigInteger) object[17]).longValue(), ((BigInteger) object[18]).longValue());
                    numberOfPassesList.add(numberOfPassesWednesday);
                }
                if (datePeriodList.size() >= 4) {
                    NumberOfPasses numberOfPassesThursday = new NumberOfPasses(((BigInteger) object[19]).longValue(), ((BigInteger) object[20]).longValue(), ((BigInteger) object[21]).longValue(),
                            ((BigInteger) object[22]).longValue(), ((BigInteger) object[23]).longValue(), ((BigInteger) object[24]).longValue());
                    numberOfPassesList.add(numberOfPassesThursday);
                }
                if (datePeriodList.size() >= 5) {
                    NumberOfPasses numberOfPassesFriday = new NumberOfPasses(((BigInteger) object[25]).longValue(), ((BigInteger) object[26]).longValue(), ((BigInteger) object[27]).longValue(),
                            ((BigInteger) object[28]).longValue(), ((BigInteger) object[29]).longValue(), ((BigInteger) object[30]).longValue());
                    numberOfPassesList.add(numberOfPassesFriday);
                }
                if (datePeriodList.size() == 6) {
                    NumberOfPasses numberOfPassesSaturday = new NumberOfPasses(((BigInteger) object[31]).longValue(), ((BigInteger) object[32]).longValue(), ((BigInteger) object[33]).longValue(),
                            ((BigInteger) object[34]).longValue(), ((BigInteger) object[35]).longValue(), ((BigInteger) object[36]).longValue());
                    numberOfPassesList.add(numberOfPassesSaturday);
                }
                result.put(((BigInteger) object[0]).longValue(), numberOfPassesList);
            }
            transaction.commit();
            transaction = null;
            for (Long idOfOrg : idOfOrgList) {
                if (result.get(idOfOrg) == null) {
                    List<NumberOfPasses> numberOfPassesList = new ArrayList<NumberOfPasses>();
                    for (int ii = 1; ii <= datePeriodList.size(); ii++) {
                        NumberOfPasses numberOfPasses = new NumberOfPasses(0L, 0L, 0L, 0L, 0L, 0L);
                        numberOfPassesList.add(numberOfPasses);
                    }
                    result.put(idOfOrg, numberOfPassesList);
                }
            }
        } catch (Exception e) {
            logger.error("Error in generateNumberOfPasses: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    private Map<Long, List<NumberOfPreferential>> numberOfPreferential(List<DatePeriods> datePeriodList, List<Long> idOfOrgList) {
        Map<Long, List<NumberOfPreferential>> result = new HashMap<Long, List<NumberOfPreferential>>();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();
            Date wholeStartTime = datePeriodList.get(0).getStartDate();
            Date wholeEndTime = datePeriodList.get(datePeriodList.size()-1).getEndDate();

            String selectFields = "";

            if (datePeriodList.size() >= 1) {
                selectFields +=
                    "count(DISTINCT(CASE WHEN ((fo.friendlyorg IS NOT NULL) AND (cfo.createddate BETWEEN :startTimeMonday AND :endTimeMonday)) "
                  + "                 THEN cfo.idofclient END)) AS number_of_lgotnoe_friendly_monday, "
                  + "count(DISTINCT(CASE WHEN ((fo.friendlyorg IS NULL) AND (cfo.createddate BETWEEN :startTimeMonday AND :endTimeMonday)) "
                  + "                 THEN cfo.idofclient END)) AS number_of_lgotnoe_other_monday, "
                  + "count(DISTINCT(CASE WHEN (cfo.ordertype IN (:reservePlan, :changePlan) AND (cfo.createddate BETWEEN :startTimeMonday AND :endTimeMonday)) "
                  + "                 THEN cfo.idofclient END)) AS number_of_reserve_monday ";
            }
            if (datePeriodList.size() >= 2) {
                selectFields +=
                    ",count(DISTINCT(CASE WHEN ((fo.friendlyorg IS NOT NULL) AND (cfo.createddate BETWEEN :startTimeTuesday AND :endTimeTuesday)) "
                  + "                 THEN cfo.idofclient END)) AS number_of_lgotnoe_friendly_tuesday, "
                  + "count(DISTINCT(CASE WHEN ((fo.friendlyorg IS NULL) AND (cfo.createddate BETWEEN :startTimeTuesday AND :endTimeTuesday)) "
                  + "                 THEN cfo.idofclient END)) AS number_of_lgotnoe_other_tuesday, "
                  + "count(DISTINCT(CASE WHEN (cfo.ordertype IN (:reservePlan, :changePlan) AND (cfo.createddate BETWEEN :startTimeTuesday AND :endTimeTuesday)) "
                  + "                 THEN cfo.idofclient END)) AS number_of_reserve_tuesday ";
            }
            if (datePeriodList.size() >= 3) {
                selectFields +=
                    ",count(DISTINCT(CASE WHEN ((fo.friendlyorg IS NOT NULL) AND (cfo.createddate BETWEEN :startTimeWednesday AND :endTimeWednesday)) "
                  + "                 THEN cfo.idofclient END)) AS number_of_lgotnoe_friendly_wednesday, "
                  + "count(DISTINCT(CASE WHEN ((fo.friendlyorg IS NULL) AND (cfo.createddate BETWEEN :startTimeWednesday AND :endTimeWednesday)) "
                  + "                 THEN cfo.idofclient END)) AS number_of_lgotnoe_other_wednesday, "
                  + "count(DISTINCT(CASE WHEN (cfo.ordertype IN (:reservePlan, :changePlan) AND (cfo.createddate BETWEEN :startTimeWednesday AND :endTimeWednesday)) "
                  + "                 THEN cfo.idofclient END)) AS number_of_reserve_wednesday ";
            }
            if (datePeriodList.size() >= 4) {
                selectFields +=
                    ",count(DISTINCT(CASE WHEN ((fo.friendlyorg IS NOT NULL) AND (cfo.createddate BETWEEN :startTimeThursday AND :endTimeThursday)) "
                  + "                 THEN cfo.idofclient END)) AS number_of_lgotnoe_friendly_thursday, "
                  + "count(DISTINCT(CASE WHEN ((fo.friendlyorg IS NULL) AND (cfo.createddate BETWEEN :startTimeThursday AND :endTimeThursday)) "
                  + "                 THEN cfo.idofclient END)) AS number_of_lgotnoe_other_thursday, "
                  + "count(DISTINCT(CASE WHEN (cfo.ordertype IN (:reservePlan, :changePlan) AND (cfo.createddate BETWEEN :startTimeThursday AND :endTimeThursday)) "
                  + "                 THEN cfo.idofclient END)) AS number_of_reserve_thursday ";
            }
            if (datePeriodList.size() >= 5) {
                selectFields +=
                    ",count(DISTINCT(CASE WHEN ((fo.friendlyorg IS NOT NULL) AND (cfo.createddate BETWEEN :startTimeFriday AND :endTimeFriday)) "
                  + "                 THEN cfo.idofclient END)) AS number_of_lgotnoe_friendly_friday, "
                  + "count(DISTINCT(CASE WHEN ((fo.friendlyorg IS NULL) AND (cfo.createddate BETWEEN :startTimeFriday AND :endTimeFriday)) "
                  + "                 THEN cfo.idofclient END)) AS number_of_lgotnoe_other_friday, "
                  + "count(DISTINCT(CASE WHEN (cfo.ordertype IN (:reservePlan, :changePlan) AND (cfo.createddate BETWEEN :startTimeFriday AND :endTimeFriday)) "
                  + "                 THEN cfo.idofclient END)) AS number_of_reserve_friday ";
            }
            if (datePeriodList.size() == 6) {
                selectFields +=
                    ",count(DISTINCT(CASE WHEN ((fo.friendlyorg IS NOT NULL) AND (cfo.createddate BETWEEN :startTimeSaturday AND :endTimeSaturday)) "
                  + "                 THEN cfo.idofclient END)) AS number_of_lgotnoe_friendly_saturday, "
                  + "count(DISTINCT(CASE WHEN ((fo.friendlyorg IS NULL) AND (cfo.createddate BETWEEN :startTimeSaturday AND :endTimeSaturday)) "
                  + "                 THEN cfo.idofclient END)) AS number_of_lgotnoe_other_saturday, "
                  + "count(DISTINCT(CASE WHEN (cfo.ordertype IN (:reservePlan, :changePlan) AND (cfo.createddate BETWEEN :startTimeSaturday AND :endTimeSaturday)) "
                  + "                 THEN cfo.idofclient END)) AS number_of_reserve_saturday ";
            }

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
                  + "    cfod.menutype >= :minType AND cfod.menutype <= :maxType  AND cfod.idofrule >= 0 group by cfo.idoforg";

            Query query = session.createSQLQuery(sqlQuery);
            if (idOfOrgList.size() <= ORGS_AMOUNT_FOR_REPORT) {
                query.setParameterList("idOfOrgs", idOfOrgList);
            }
            query.setParameter("startTime", wholeStartTime.getTime());
            query.setParameter("endTime", wholeEndTime.getTime());
            query.setParameter("minType", OrderDetail.TYPE_COMPLEX_MIN);
            query.setParameter("maxType", OrderDetail.TYPE_COMPLEX_MAX);
            query.setParameter("reducedPricePlan", OrderTypeEnumType.REDUCED_PRICE_PLAN.ordinal());
            query.setParameter("correctionType", OrderTypeEnumType.CORRECTION_TYPE.ordinal());
            query.setParameter("employees", ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue());
            query.setParameter("reservePlan", OrderTypeEnumType.REDUCED_PRICE_PLAN_RESERVE.ordinal());
            query.setParameter("changePlan", OrderTypeEnumType.DISCOUNT_PLAN_CHANGE.ordinal());

            updateQueryDates(query, datePeriodList);

            List<Object[]> list = query.list();
            for (Object[] object : list) {
                List<NumberOfPreferential> numberOfPreferentialList = new ArrayList<NumberOfPreferential>();

                if (datePeriodList.size() >= 1) {
                    NumberOfPreferential numberOfPreferentialMonday = new NumberOfPreferential(((BigInteger) object[1]).longValue(),
                            ((BigInteger) object[2]).longValue(), ((BigInteger) object[3]).longValue());
                    numberOfPreferentialList.add(numberOfPreferentialMonday);
                }
                if (datePeriodList.size() >= 2) {
                    NumberOfPreferential numberOfPreferentialTuesday = new NumberOfPreferential(((BigInteger) object[4]).longValue(),
                            ((BigInteger) object[5]).longValue(), ((BigInteger) object[6]).longValue());
                    numberOfPreferentialList.add(numberOfPreferentialTuesday);
                }
                if (datePeriodList.size() >= 3) {
                    NumberOfPreferential numberOfPreferentialWednesday = new NumberOfPreferential(((BigInteger) object[7]).longValue(),
                            ((BigInteger) object[8]).longValue(), ((BigInteger) object[9]).longValue());
                    numberOfPreferentialList.add(numberOfPreferentialWednesday);
                }
                if (datePeriodList.size() >= 4) {
                    NumberOfPreferential numberOfPreferentialThursday = new NumberOfPreferential(((BigInteger) object[10]).longValue(),
                            ((BigInteger) object[11]).longValue(), ((BigInteger) object[12]).longValue());
                    numberOfPreferentialList.add(numberOfPreferentialThursday);
                }
                if (datePeriodList.size() >= 5) {
                    NumberOfPreferential numberOfPreferentialFriday = new NumberOfPreferential(((BigInteger) object[13]).longValue(),
                            ((BigInteger) object[14]).longValue(), ((BigInteger) object[15]).longValue());
                    numberOfPreferentialList.add(numberOfPreferentialFriday);
                }
                if (datePeriodList.size() == 6) {
                    NumberOfPreferential numberOfPreferentialSaturday = new NumberOfPreferential(((BigInteger) object[16]).longValue(),
                            ((BigInteger) object[17]).longValue(), ((BigInteger) object[18]).longValue());
                    numberOfPreferentialList.add(numberOfPreferentialSaturday);
                }
                result.put(((BigInteger) object[0]).longValue(), numberOfPreferentialList);
            }

            transaction.commit();
            transaction = null;
            for (Long idOfOrg : idOfOrgList) {
                if (result.get(idOfOrg) == null) {
                    List<NumberOfPreferential> numberOfPreferentialList = new ArrayList<NumberOfPreferential>();
                    for (int ii = 1; ii <= datePeriodList.size(); ii++) {
                        NumberOfPreferential numberOfPreferential = new NumberOfPreferential(0L, 0L, 0L);
                        numberOfPreferentialList.add(numberOfPreferential);
                    }
                    result.put(idOfOrg, numberOfPreferentialList);
                }
            }
        } catch (Exception e) {
            logger.error("Error in generateNumberOfPreferential: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    private Map<Long, List<NumberOfStudentsAndGuardians>> numberOfBuffet(List<DatePeriods> datePeriodList, List<Long> idOfOrgList) {
        Map<Long, List<NumberOfStudentsAndGuardians>> result = new HashMap<Long, List<NumberOfStudentsAndGuardians>>();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();



            Date wholeStartTime = datePeriodList.get(0).getStartDate();
            Date wholeEndTime = datePeriodList.get(datePeriodList.size() - 1).getEndDate();

            String selectFields = "";

            if (datePeriodList.size() >= 1) {
                selectFields +=
                        "count(DISTINCT(CASE WHEN ((g.idofclientgroup < :employees) AND (cfo.createddate BETWEEN :startTimeMonday AND :endTimeMonday)) "
                                + "   THEN cfo.idofclient END)) AS number_of_buffet_students_monday, " + "count(DISTINCT(CASE WHEN ((g.idofclientgroup IN "
                                + "   (:employees, :administration, :displaced, :tech_employees, :visitors, :other, :parents)) "
                                + "   AND (cfo.createddate BETWEEN :startTimeMonday AND :endTimeMonday)) THEN cfo.idofclient END)) "
                                + "   AS number_of_buffet_guardians_monday ";
            }
            if (datePeriodList.size() >= 2) {
                selectFields +=
                        ",count(DISTINCT(CASE WHEN ((g.idofclientgroup < :employees) AND (cfo.createddate BETWEEN :startTimeTuesday AND :endTimeTuesday)) "
                                + "   THEN cfo.idofclient END)) AS number_of_buffet_students_tuesday, " + "count(DISTINCT(CASE WHEN ((g.idofclientgroup IN "
                                + "   (:employees, :administration, :displaced, :tech_employees, :visitors, :other, :parents)) "
                                + "   AND (cfo.createddate BETWEEN :startTimeTuesday AND :endTimeTuesday)) THEN cfo.idofclient END)) "
                                + "   AS number_of_buffet_guardians_tuesday ";
            }
            if (datePeriodList.size() >= 3) {
                selectFields +=
                        ",count(DISTINCT(CASE WHEN ((g.idofclientgroup < :employees) AND (cfo.createddate BETWEEN :startTimeWednesday AND :endTimeWednesday)) "
                                + "   THEN cfo.idofclient END)) AS number_of_buffet_students_wednesday, " + "count(DISTINCT(CASE WHEN ((g.idofclientgroup IN "
                                + "   (:employees, :administration, :displaced, :tech_employees, :visitors, :other, :parents)) "
                                + "   AND (cfo.createddate BETWEEN :startTimeWednesday AND :endTimeWednesday)) THEN cfo.idofclient END)) "
                                + "   AS number_of_buffet_guardians_wednesday ";
            }
            if (datePeriodList.size() >= 4) {
                selectFields +=
                        ",count(DISTINCT(CASE WHEN ((g.idofclientgroup < :employees) AND (cfo.createddate BETWEEN :startTimeThursday AND :endTimeThursday)) "
                                + "   THEN cfo.idofclient END)) AS number_of_buffet_students_thursday, " + "count(DISTINCT(CASE WHEN ((g.idofclientgroup IN "
                                + "   (:employees, :administration, :displaced, :tech_employees, :visitors, :other, :parents)) "
                                + "   AND (cfo.createddate BETWEEN :startTimeThursday AND :endTimeThursday)) THEN cfo.idofclient END)) "
                                + "   AS number_of_buffet_guardians_thursday ";
            }
            if (datePeriodList.size() >= 5) {
                selectFields +=
                        ",count(DISTINCT(CASE WHEN ((g.idofclientgroup < :employees) AND (cfo.createddate BETWEEN :startTimeFriday AND :endTimeFriday)) "
                                + "   THEN cfo.idofclient END)) AS number_of_buffet_students_friday, " + "count(DISTINCT(CASE WHEN ((g.idofclientgroup IN "
                                + "   (:employees, :administration, :displaced, :tech_employees, :visitors, :other, :parents)) "
                                + "   AND (cfo.createddate BETWEEN :startTimeFriday AND :endTimeFriday)) THEN cfo.idofclient END)) "
                                + "   AS number_of_buffet_guardians_friday ";
            }
            if (datePeriodList.size() == 6) {
                selectFields +=
                        ",count(DISTINCT(CASE WHEN ((g.idofclientgroup < :employees) AND (cfo.createddate BETWEEN :startTimeSaturday AND :endTimeSaturday)) "
                                + "   THEN cfo.idofclient END)) AS number_of_buffet_students_saturday, " + "count(DISTINCT(CASE WHEN ((g.idofclientgroup IN "
                                + "   (:employees, :administration, :displaced, :tech_employees, :visitors, :other, :parents)) "
                                + "   AND (cfo.createddate BETWEEN :startTimeSaturday AND :endTimeSaturday)) THEN cfo.idofclient END)) "
                                + "   AS number_of_buffet_guardians_saturday ";
            }

            String orgCondition = "";
            if (idOfOrgList.size() <= ORGS_AMOUNT_FOR_REPORT) {
                orgCondition = " cfo.idoforg in (:idOfOrgs) and ";
            }
            String sqlQuery = "SELECT cfo.idoforg, " + selectFields + "FROM cf_orders cfo " + "LEFT JOIN cf_orderdetails cfod ON cfod.idoforg = cfo.idoforg AND cfod.idoforder = cfo.idoforder "
                    + "LEFT JOIN cf_clients c ON cfo.idofclient = c.idofclient " + "LEFT JOIN cf_clientgroups g ON g.idofclientgroup = c.idofclientgroup AND c.idoforg = g.idoforg "
                    + "WHERE cfo.ordertype IN (:unknownType, :defaultType, :vendingType) AND " + orgCondition + " cfo.state = :stateCommited "
                    + "    AND ((g.idofclientgroup < :employees) OR g.idofclientgroup IN " + "        (:employees, :administration, :displaced, :tech_employees, :visitors, :other, :parents)) "
                    + "    AND cfo.createddate BETWEEN :startTime AND :endTime group by cfo.idoforg";
            Query query = session.createSQLQuery(sqlQuery);

            if (idOfOrgList.size() <= ORGS_AMOUNT_FOR_REPORT) {
                query.setParameterList("idOfOrgs", idOfOrgList);
            }
            query.setParameter("startTime", wholeStartTime.getTime());
            query.setParameter("endTime", wholeEndTime.getTime());
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

            updateQueryDates(query, datePeriodList);

            List<Object[]> list = query.list();

            transaction.commit();
            transaction = null;
            result = getFromQuery(list, datePeriodList, idOfOrgList);
        } catch (Exception e) {
            logger.error("Error in numberOfBuffet: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    private Map<Long, List<NumberOfStudentsAndGuardians>> getFromQuery(List<Object[]> list, List<DatePeriods> datePeriodList, List<Long> idOfOrgList) {
        Map<Long, List<NumberOfStudentsAndGuardians>> result = new HashMap<Long, List<NumberOfStudentsAndGuardians>>();
        for (Object[] object : list) {
            List<NumberOfStudentsAndGuardians> numberOfBuffetList = new ArrayList<NumberOfStudentsAndGuardians>();
            if (datePeriodList.size() >= 1) {
                NumberOfStudentsAndGuardians numberOfBuffetMonday = new NumberOfStudentsAndGuardians(((BigInteger) object[1]).longValue(),
                        ((BigInteger) object[2]).longValue());
                numberOfBuffetList.add(numberOfBuffetMonday);
            }
            if (datePeriodList.size() >= 2) {
                NumberOfStudentsAndGuardians numberOfBuffetTuesday = new NumberOfStudentsAndGuardians(((BigInteger) object[3]).longValue(),
                        ((BigInteger) object[4]).longValue());
                numberOfBuffetList.add(numberOfBuffetTuesday);
            }
            if (datePeriodList.size() >= 3) {
                NumberOfStudentsAndGuardians numberOfBuffetWednesday = new NumberOfStudentsAndGuardians(((BigInteger) object[5]).longValue(),
                        ((BigInteger) object[6]).longValue());
                numberOfBuffetList.add(numberOfBuffetWednesday);
            }
            if (datePeriodList.size() >= 4) {
                NumberOfStudentsAndGuardians numberOfBuffetThursday = new NumberOfStudentsAndGuardians(((BigInteger) object[7]).longValue(),
                        ((BigInteger) object[8]).longValue());
                numberOfBuffetList.add(numberOfBuffetThursday);
            }
            if (datePeriodList.size() >= 5) {
                NumberOfStudentsAndGuardians numberOfBuffetFriday = new NumberOfStudentsAndGuardians(((BigInteger) object[9]).longValue(),
                        ((BigInteger) object[10]).longValue());
                numberOfBuffetList.add(numberOfBuffetFriday);
            }
            if (datePeriodList.size() == 6) {
                NumberOfStudentsAndGuardians numberOfBuffetSaturday = new NumberOfStudentsAndGuardians(((BigInteger) object[11]).longValue(),
                        ((BigInteger) object[12]).longValue());
                numberOfBuffetList.add(numberOfBuffetSaturday);
            }
            result.put(((BigInteger) object[0]).longValue(), numberOfBuffetList);
        }
        for (Long idOfOrg : idOfOrgList) {
            if (result.get(idOfOrg) == null) {
                List<NumberOfStudentsAndGuardians> numberOfStudentsAndGuardiansList = new ArrayList<NumberOfStudentsAndGuardians>();
                for (int ii = 1; ii <= datePeriodList.size(); ii++) {
                    NumberOfStudentsAndGuardians numberOfStudentsAndGuardians = new NumberOfStudentsAndGuardians(0L, 0L);
                    numberOfStudentsAndGuardiansList.add(numberOfStudentsAndGuardians);
                }
                result.put(idOfOrg, numberOfStudentsAndGuardiansList);
            }
        }
        return result;
    }

    private Map<Long, List<NumberOfStudentsAndGuardians>> numberOfSubFeed(List<DatePeriods> datePeriodList, List<Long> idOfOrgList) {
        Map<Long, List<NumberOfStudentsAndGuardians>> result = new HashMap<Long, List<NumberOfStudentsAndGuardians>>();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            Date wholeStartTime = datePeriodList.get(0).getStartDate();
            Date wholeEndTime = datePeriodList.get(datePeriodList.size() - 1).getEndDate();

            String selectFields = "";

            if (datePeriodList.size() >= 1) {
                selectFields +=
                        "count(DISTINCT(CASE WHEN ((g.idofclientgroup < :employees) AND (cfo.CreatedDate BETWEEN :startTimeMonday AND :endTimeMonday)) "
                                + "                   THEN cfo.idofclient END)) AS number_of_subfeed_students_monday, "
                                + "count(DISTINCT(CASE WHEN ((g.idofclientgroup IN " + "        (:employees, :administration, :employee, :tech_employees, :visitors, :other, :parents)) "
                                + "           AND (cfo.CreatedDate BETWEEN :startTimeMonday AND :endTimeMonday)) " + "        THEN cfo.idofclient END)) AS number_of_subfeed_guardians_monday ";
            }
            if (datePeriodList.size() >= 2) {
                selectFields +=
                        ",count(DISTINCT(CASE WHEN ((g.idofclientgroup < :employees) AND (cfo.CreatedDate BETWEEN :startTimeTuesday AND :endTimeTuesday)) "
                                + "                   THEN cfo.idofclient END)) AS number_of_subfeed_students_tuesday, "
                                + "count(DISTINCT(CASE WHEN ((g.idofclientgroup IN " + "        (:employees, :administration, :employee, :tech_employees, :visitors, :other, :parents)) "
                                + "           AND (cfo.CreatedDate BETWEEN :startTimeTuesday AND :endTimeTuesday)) " + "        THEN cfo.idofclient END)) AS number_of_subfeed_guardians_tuesday ";
            }
            if (datePeriodList.size() >= 3) {
                selectFields +=
                        ",count(DISTINCT(CASE WHEN ((g.idofclientgroup < :employees) AND (cfo.CreatedDate BETWEEN :startTimeWednesday AND :endTimeWednesday)) "
                                + "                   THEN cfo.idofclient END)) AS number_of_subfeed_students_wednesday, "
                                + "count(DISTINCT(CASE WHEN ((g.idofclientgroup IN " + "        (:employees, :administration, :employee, :tech_employees, :visitors, :other, :parents)) "
                                + "           AND (cfo.CreatedDate BETWEEN :startTimeWednesday AND :endTimeWednesday)) "
                                + "        THEN cfo.idofclient END)) AS number_of_subfeed_guardians_wednesday ";
            }
            if (datePeriodList.size() >= 4) {
                selectFields +=
                        ",count(DISTINCT(CASE WHEN ((g.idofclientgroup < :employees) AND (cfo.CreatedDate BETWEEN :startTimeThursday AND :endTimeThursday)) "
                                + "                   THEN cfo.idofclient END)) AS number_of_subfeed_students_thursday, "
                                + "count(DISTINCT(CASE WHEN ((g.idofclientgroup IN " + "        (:employees, :administration, :employee, :tech_employees, :visitors, :other, :parents)) "
                                + "           AND (cfo.CreatedDate BETWEEN :startTimeThursday AND :endTimeThursday)) " + "        THEN cfo.idofclient END)) AS number_of_subfeed_guardians_thursday ";
            }
            if (datePeriodList.size() >= 5) {
                selectFields +=
                        ",count(DISTINCT(CASE WHEN ((g.idofclientgroup < :employees) AND (cfo.CreatedDate BETWEEN :startTimeFriday AND :endTimeFriday)) "
                                + "                   THEN cfo.idofclient END)) AS number_of_subfeed_students_friday, "
                                + "count(DISTINCT(CASE WHEN ((g.idofclientgroup IN " + "        (:employees, :administration, :employee, :tech_employees, :visitors, :other, :parents)) "
                                + "           AND (cfo.CreatedDate BETWEEN :startTimeFriday AND :endTimeFriday)) " + "        THEN cfo.idofclient END)) AS number_of_subfeed_guardians_friday ";
            }
            if (datePeriodList.size() == 6) {
                selectFields +=
                        ",count(DISTINCT(CASE WHEN ((g.idofclientgroup < :employees) AND (cfo.CreatedDate BETWEEN :startTimeSaturday AND :endTimeSaturday)) "
                                + "                   THEN cfo.idofclient END)) AS number_of_subfeed_students_saturday, "
                                + "count(DISTINCT(CASE WHEN ((g.idofclientgroup IN " + "        (:employees, :administration, :employee, :tech_employees, :visitors, :other, :parents)) "
                                + "           AND (cfo.CreatedDate BETWEEN :startTimeSaturday AND :endTimeSaturday)) " + "        THEN cfo.idofclient END)) AS number_of_subfeed_guardians_saturday ";
            }
            String orgCondition = "";
            if (idOfOrgList.size() <= ORGS_AMOUNT_FOR_REPORT) {
                orgCondition = " cfo.idoforg in (:idOfOrgs) and ";
            }
            String sqlQuery = "SELECT cfo.idoforg, " + selectFields + "FROM CF_OrderDetails cfod " + "LEFT OUTER JOIN CF_Orders cfo ON cfod.IdOfOrg = cfo.IdOfOrg AND cfod.IdOfOrder = cfo.IdOfOrder "
                    + "    AND cfo.State = :stateCommited AND cfo.OrderType = :subscriptionPlan " + "LEFT OUTER JOIN CF_Orgs org ON cfo.IdOfOrg = org.IdOfOrg "
                    + "LEFT JOIN cf_clients c ON cfo.idofclient = c.idofclient " + "LEFT JOIN cf_clientgroups g ON g.idofclientgroup = c.idofclientgroup AND c.idoforg = g.idoforg "
                    + "WHERE cfod.State = :detailedStateCommited AND " + orgCondition + " (cfo.CreatedDate BETWEEN :startTime AND :endTime) "
                    + "    AND cfod.MenuType >= :minType AND cfod.MenuType <= :maxType " + "    AND ((g.idofclientgroup < :employees) OR "
                    + "        g.idofclientgroup IN (:employees, :administration, :employee, :tech_employees, :visitors, :other, :parents)) group by cfo.idoforg";

            Query query = session.createSQLQuery(sqlQuery);

            if (idOfOrgList.size() <= ORGS_AMOUNT_FOR_REPORT) {
                query.setParameterList("idOfOrgs", idOfOrgList);
            }
            query.setParameter("startTime", wholeStartTime.getTime());
            query.setParameter("endTime", wholeEndTime.getTime());
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

            updateQueryDates(query, datePeriodList);

            List<Object[]> list = query.list();
            transaction.commit();
            transaction = null;
            result = getFromQuery(list, datePeriodList, idOfOrgList);
        } catch (Exception e) {
            logger.error("Error in numberOfSubFeed: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return result;
    }

    private Map<Long, List<NumberOfStudentsAndGuardians>> numberOfPaid(List<DatePeriods> datePeriodList, List<Long> idOfOrgList) {
        Map<Long, List<NumberOfStudentsAndGuardians>> result = new HashMap<Long, List<NumberOfStudentsAndGuardians>>();
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            Date wholeStartTime = datePeriodList.get(0).getStartDate();
            Date wholeEndTime = datePeriodList.get(datePeriodList.size() - 1).getEndDate();

            String selectFields = "";

            if (datePeriodList.size() >= 1) {
                selectFields +=
                        "count(DISTINCT(CASE WHEN ((g.idofclientgroup < :employees) AND (cfo.CreatedDate BETWEEN :startTimeMonday AND :endTimeMonday)) "
                                + "                   THEN cfo.idofclient END)) AS number_of_paid_students_monday, " + "count(DISTINCT(CASE WHEN ((g.idofclientgroup IN "
                                + "        (:employees, :administration, :employee, :tech_employees, :visitors, :other, :parents)) "
                                + "           AND (cfo.CreatedDate BETWEEN :startTimeMonday AND :endTimeMonday))" + "        THEN cfo.idofclient END)) AS number_of_paid_guardians_monday ";
            }
            if (datePeriodList.size() >= 2) {
                selectFields +=
                        ",count(DISTINCT(CASE WHEN ((g.idofclientgroup < :employees) AND (cfo.CreatedDate BETWEEN :startTimeTuesday AND :endTimeTuesday)) "
                                + "                   THEN cfo.idofclient END)) AS number_of_paid_students_tuesday, " + "count(DISTINCT(CASE WHEN ((g.idofclientgroup IN "
                                + "        (:employees, :administration, :employee, :tech_employees, :visitors, :other, :parents)) "
                                + "           AND (cfo.CreatedDate BETWEEN :startTimeTuesday AND :endTimeTuesday))" + "        THEN cfo.idofclient END)) AS number_of_paid_guardians_tuesday ";
            }
            if (datePeriodList.size() >= 3) {
                selectFields +=
                        ",count(DISTINCT(CASE WHEN ((g.idofclientgroup < :employees) AND (cfo.CreatedDate BETWEEN :startTimeWednesday AND :endTimeWednesday)) "
                                + "                   THEN cfo.idofclient END)) AS number_of_paid_students_wednesday, "
                                + "count(DISTINCT(CASE WHEN ((g.idofclientgroup IN " + "        (:employees, :administration, :employee, :tech_employees, :visitors, :other, :parents)) "
                                + "           AND (cfo.CreatedDate BETWEEN :startTimeWednesday AND :endTimeWednesday))"
                                + "        THEN cfo.idofclient END)) AS number_of_paid_guardians_wednesday ";
            }
            if (datePeriodList.size() >= 4) {
                selectFields +=
                        ",count(DISTINCT(CASE WHEN ((g.idofclientgroup < :employees) AND (cfo.CreatedDate BETWEEN :startTimeThursday AND :endTimeThursday)) "
                                + "                   THEN cfo.idofclient END)) AS number_of_paid_students_thursday, " + "count(DISTINCT(CASE WHEN ((g.idofclientgroup IN "
                                + "        (:employees, :administration, :employee, :tech_employees, :visitors, :other, :parents)) "
                                + "           AND (cfo.CreatedDate BETWEEN :startTimeThursday AND :endTimeThursday))" + "        THEN cfo.idofclient END)) AS number_of_paid_guardians_thursday ";
            }
            if (datePeriodList.size() >= 5) {
                selectFields +=
                        ",count(DISTINCT(CASE WHEN ((g.idofclientgroup < :employees) AND (cfo.CreatedDate BETWEEN :startTimeFriday AND :endTimeFriday)) "
                                + "                   THEN cfo.idofclient END)) AS number_of_paid_students_friday, " + "count(DISTINCT(CASE WHEN ((g.idofclientgroup IN "
                                + "        (:employees, :administration, :employee, :tech_employees, :visitors, :other, :parents)) "
                                + "           AND (cfo.CreatedDate BETWEEN :startTimeFriday AND :endTimeFriday))" + "        THEN cfo.idofclient END)) AS number_of_paid_guardians_friday ";
            }
            if (datePeriodList.size() == 6) {
                selectFields +=
                        ",count(DISTINCT(CASE WHEN ((g.idofclientgroup < :employees) AND (cfo.CreatedDate BETWEEN :startTimeSaturday AND :endTimeSaturday)) "
                                + "                   THEN cfo.idofclient END)) AS number_of_paid_students_saturday, " + "count(DISTINCT(CASE WHEN ((g.idofclientgroup IN "
                                + "        (:employees, :administration, :employee, :tech_employees, :visitors, :other, :parents)) "
                                + "           AND (cfo.CreatedDate BETWEEN :startTimeSaturday AND :endTimeSaturday))" + "        THEN cfo.idofclient END)) AS number_of_paid_guardians_saturday ";
            }
            String orgCondition = "";
            if (idOfOrgList.size() <= ORGS_AMOUNT_FOR_REPORT) {
                orgCondition = " cfo.idoforg in (:idOfOrgs) and ";
            }
            String sqlQuery = "SELECT cfo.idoforg, " + selectFields + "FROM CF_OrderDetails cfod " + "LEFT OUTER JOIN CF_Orders cfo ON cfod.IdOfOrg = cfo.IdOfOrg AND cfod.IdOfOrder = cfo.IdOfOrder "
                    + "    AND cfo.State = :stateCommited AND cfo.OrderType = :payPlan " + "LEFT OUTER JOIN CF_Orgs org ON cfo.IdOfOrg = org.IdOfOrg "
                    + "LEFT JOIN cf_clients c ON cfo.idofclient = c.idofclient " + "LEFT JOIN cf_clientgroups g ON g.idofclientgroup = c.idofclientgroup AND c.idoforg = g.idoforg "
                    + "WHERE cfod.State = :detailedStateCommited AND " + orgCondition + " (cfo.CreatedDate BETWEEN :startTime AND :endTime) "
                    + "AND cfod.MenuType >= :minType AND cfod.MenuType <= :maxType AND ((g.idofclientgroup < :employees) "
                    + "    OR g.idofclientgroup IN (:employees, :administration, :employee, :tech_employees, :visitors, :other, :parents)) group by cfo.idoforg";

            Query query = session.createSQLQuery(sqlQuery);
            if (idOfOrgList.size() <= ORGS_AMOUNT_FOR_REPORT) {
                query.setParameterList("idOfOrgs", idOfOrgList);
            }
            query.setParameter("startTime", wholeStartTime.getTime());
            query.setParameter("endTime", wholeEndTime.getTime());
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

            updateQueryDates(query, datePeriodList);

            List<Object[]> list = query.list();
            transaction.commit();
            transaction = null;
            result = getFromQuery(list, datePeriodList, idOfOrgList);
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
            if (idOfOrgList.size() <= 10) {
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

    public List<ReportItem> getOrgData(List<Long> idOfOrgList, List<DatePeriods> datePeriodsList) {
        List<ReportItem> reportItemList = new ArrayList<ReportItem>();
        Map<Long, PeopleData> peopleDataMap = loadPeopleDataForOrgs(idOfOrgList);
        Map<Long, List<NumberOfPasses>> numberOfPassesMap = generateNumberOfPasses(datePeriodsList, idOfOrgList);
        Map<Long, List<NumberOfPreferential>> numberOfPreferentialMap = numberOfPreferential(datePeriodsList, idOfOrgList);
        Map<Long, List<NumberOfStudentsAndGuardians>> numberOfBuffetMap = numberOfBuffet(datePeriodsList, idOfOrgList);
        Map<Long, List<NumberOfStudentsAndGuardians>> numberOfSubfeedMap = numberOfSubFeed(datePeriodsList, idOfOrgList);
        Map<Long, List<NumberOfStudentsAndGuardians>> numberOfPaidMap = numberOfPaid(datePeriodsList, idOfOrgList);
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();
            for (Long idOfOrg : idOfOrgList) {
                Org org = (Org) session.load(Org.class, idOfOrg);
                if (!org.getState().equals(Org.ACTIVE_STATE)) continue;;
                ReportItem reportItem = new ReportItem();
                Org mainOrg = OrgService.getInstance().getMainBulding(org);
                reportItem.setOrgNum(mainOrg.getOrgNumberFromNameInfoService());
                reportItem.setShortName(org.getShortNameInfoService());
                reportItem.setAddress(org.getAddress());
                reportItem.setIdOfOrg(String.valueOf(org.getIdOfOrg()));
                reportItem.setCode(String.valueOf(org.getUniqueAddressId()));
                reportItem.setDistrict(org.getDistrict());
                reportItem.setTypeOfBuilding(org.getType().toString());
                reportItem.setIntroductionQueue(org.getIntroductionQueue());
                reportItem.setOrgStatus(org.stateString());

                PeopleData peopleData = peopleDataMap.get(idOfOrg);

                reportItem.setStudentsInDatabase(peopleData == null ? "" : String.valueOf(peopleData.getAllPeoples()));
                reportItem.setStudentsWithMaps(peopleData == null ? "" : String.valueOf(peopleData.getStudentsWithCards()));
                reportItem.setParents(peopleData == null ? "" : String.valueOf(peopleData.getParents()));
                reportItem.setPedagogicalComposition(peopleData == null ? "" : String.valueOf(peopleData.getPedagogicalComposition()));
                reportItem.setOtherEmployees(peopleData == null ? "" : String.valueOf(peopleData.getOther()));

                List<List<MonitoringOfItem>> monitoringOfItemList = getMonitoringOfItems(datePeriodsList,
                        idOfOrg, numberOfPassesMap, numberOfPreferentialMap, numberOfBuffetMap, numberOfSubfeedMap, numberOfPaidMap);

                if (monitoringOfItemList.size() >= 1) {
                    reportItem.setMonitoringOfItemsMonday(monitoringOfItemList.get(0));
                }

                if (monitoringOfItemList.size() >= 2) {
                    reportItem.setMonitoringOfItemsTuesday(monitoringOfItemList.get(1));
                }

                if (monitoringOfItemList.size() >= 3) {
                    reportItem.setMonitoringOfItemsWednesday(monitoringOfItemList.get(2));
                }

                if (monitoringOfItemList.size() >= 4) {
                    reportItem.setMonitoringOfItemsThursday(monitoringOfItemList.get(3));
                }

                if (monitoringOfItemList.size() >= 5) {
                    reportItem.setMonitoringOfItemsFriday(monitoringOfItemList.get(4));
                }

                if (monitoringOfItemList.size() == 6) {
                    reportItem.setMonitoringOfItemsSaturday(monitoringOfItemList.get(5));
                }

                reportItemList.add(reportItem);
            }
            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            logger.error("Error in MonitoringOfReportService: ", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return reportItemList;
    }

    private void updateQueryDates(Query query, List<DatePeriods> datePeriodList) {
        if (datePeriodList.size() >= 1) {
            DatePeriods mondayPeriod = datePeriodList.get(0);
            query.setParameter("startTimeMonday", mondayPeriod.getStartDate().getTime());
            query.setParameter("endTimeMonday", mondayPeriod.getEndDate().getTime());
        }
        if (datePeriodList.size() >= 2) {
            DatePeriods tuesdayPeriod = datePeriodList.get(1);
            query.setParameter("startTimeTuesday", tuesdayPeriod.getStartDate().getTime());
            query.setParameter("endTimeTuesday", tuesdayPeriod.getEndDate().getTime());
        }
        if (datePeriodList.size() >= 3) {
            DatePeriods wednesdayPeriod = datePeriodList.get(2);
            query.setParameter("startTimeWednesday", wednesdayPeriod.getStartDate().getTime());
            query.setParameter("endTimeWednesday", wednesdayPeriod.getEndDate().getTime());
        }
        if (datePeriodList.size() >= 4) {
            DatePeriods thursdayPeriod = datePeriodList.get(3);
            query.setParameter("startTimeThursday", thursdayPeriod.getStartDate().getTime());
            query.setParameter("endTimeThursday", thursdayPeriod.getEndDate().getTime());
        }
        if (datePeriodList.size() >= 5) {
            DatePeriods fridayPeriod = datePeriodList.get(4);
            query.setParameter("startTimeFriday", fridayPeriod.getStartDate().getTime());
            query.setParameter("endTimeFriday", fridayPeriod.getEndDate().getTime());
        }
        if (datePeriodList.size() >= 6) {
            DatePeriods saturdayPeriod = datePeriodList.get(5);
            query.setParameter("startTimeSaturday", saturdayPeriod.getStartDate().getTime());
            query.setParameter("endTimeSaturday", saturdayPeriod.getEndDate().getTime());
        }
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
        public String orgStatus;
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
                String typeOfBuilding, String introductionQueue, String orgStatus, String studentsInDatabase,
                String studentsWithMaps, String parents, String pedagogicalComposition, String otherEmployees,
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
            this.orgStatus = orgStatus;
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

        public MonitoringOfItem() {
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
