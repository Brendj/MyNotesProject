/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuleProcessor;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.EnterEvent;
import ru.axetta.ecafe.processor.core.persistence.RuleCondition;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 11.03.12
 * Time: 13:46
 * To change this template use File | Settings | File Templates.
 */
public class AutoEnterEventReport extends BasicReportForOrgJob {
    /*
    * Параметры отчета для добавления в правила и шаблоны
    *
    * При создании любого отчета необходимо добавить параметры:
    * REPORT_NAME - название отчета на русском
    * TEMPLATE_FILE_NAMES - названия всех jasper-файлов, созданных для отчета
    * IS_TEMPLATE_REPORT - добавлять ли отчет в шаблоны отчетов
    * PARAM_HINTS - параметры отчета (смотри ReportRuleConstants.PARAM_HINTS)
    * заполняется, если отчет добавлен в шаблоны (класс AutoReportGenerator)
    *
    * Затем КАЖДЫЙ класс отчета добавляется в массив ReportRuleConstants.ALL_REPORT_CLASSES
    */
    public static final String REPORT_NAME = "Детализированный отчет по посещению";
    public static final String[] TEMPLATE_FILE_NAMES = {"AutoEnterEventByDaysReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{28, 29, -3, 22, 23, 24, 32};

    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {
    }

    public static class Builder extends BasicReportJob.Builder {

        public class Event implements Comparable<Event>{
            private long time; // время события
            private int passdirection; // направление (вошел, вышел)
            private String guardianFIO; // ФИО представителя для садиков

            public Event(long time, int passdirection, String guardianFIO) {
                this.time = time;
                this.passdirection = passdirection;
                this.guardianFIO = guardianFIO;
            }

            public long getTime() {
                return time;
            }

            public void setTime(long time) {
                this.time = time;
            }

            public int getPassdirection() {
                return passdirection;
            }

            public void setPassdirection(int passdirection) {
                this.passdirection = passdirection;
            }

            public String getGuardianFIO() {
                return guardianFIO;
            }

            public void setGuardianFIO(String guardianFIO) {
                this.guardianFIO = guardianFIO;
            }

            @Override
            public int compareTo(Event event) {
                return (time<event.getTime() ? -1 : (time==event.getTime() ? 0 : 1));
            }
        }

        public static class ReportItem {

            private Integer id = null;
            private String fio = null;
            // события прохода через турникет
            private NavigableSet<Event> events = new TreeSet<Event>();
            private String date;
            private String groupName; // группа клиента (класс, сотрудники и т.д.)

            public ReportItem(Integer id, String fio, String date, String groupName) {
                this.id = id;
                this.fio = fio;
                this.date = date;
                this.groupName = groupName;
            }

            public String getGroupName() {
                return groupName;
            }

            public ReportItem() {
            }

            public Integer getId() {
                return id;
            }

            public String getFio() {
                return fio;
            }

            public void addEvent(Event e){
                events.add(e);
            }

            //public NavigableSet<Event> getEvents() {
            //    return events;
            //}

            public String getDate() {
                return date;
            }

            // время прихода
            public String getTimeEnter() {
                if (events.isEmpty())
                    return "";
                for (Event e : events) {
                    if (EnterEvent.isEntryOrReEntryEvent(e.passdirection))
                        return timeFormat.format(new Date(e.getTime()));
                }
                return "-";
            }

            // время ухода
            public String getTimeExit() {
                if (events.isEmpty())
                    return "";
                for (Event e : events.descendingSet()) {
                    if (EnterEvent.isExitOrReExitEvent(e.passdirection))
                        return timeFormat.format(new Date(e.getTime()));
                }
                return "-";
            }

            // время отсутствия
            public Integer getAbsenceOfDay() {
                if (events.isEmpty())
                    return null;
                Integer result = 0;
                Long exitTime = null;
                for (Event e : events) {
                    if (EnterEvent.isEntryOrReEntryEvent(e.getPassdirection()) && exitTime!=null) {
                        result += (int)((e.getTime() - exitTime) / (1000 * 60));
                    }
                    if (EnterEvent.isExitOrReExitEvent(e.getPassdirection()))
                        exitTime = e.getTime();
                }
                return result;
            }

            // время присутствия в течении дня
            public String getPresenceOfDay() {
                if (events.isEmpty()) return ""; // событий прохода небыло
                boolean lastExit = false;
                long result = 0;
                long entryTime = 0L;
                for (Event e: events){
                    if(EnterEvent.isEntryOrReEntryEvent(e.getPassdirection()) && entryTime<=0){
                        entryTime = e.getTime();
                        lastExit = false;
                    }
                    if(EnterEvent.isExitOrReExitEvent(e.getPassdirection()) && entryTime>0){
                        double value = ((e.getTime() * 1.0 - entryTime * 1.0) / (1000.0 * 60.0));
                        result += Math.round(value);
                        //result += (int)((e.getTime() - entryTime) / (1000 * 60));
                        lastExit = true;
                        entryTime = 0L;
                    }
                }

                //if (lastEntry == null)  return ""; // клиент только выходил
                if (!lastExit)  return ""; // клиент последний раз вошел но не вышел
                //Calendar calendar = Calendar.getInstance();
                //calendar.setTimeZone(RuntimeContext.getInstance().getLocalTimeZone(null));
                //calendar.setTimeInMillis(presenceOfDay);
                //SimpleDateFormat ft = new SimpleDateFormat("hh:mm:ss");
                //ft.setTimeZone(RuntimeContext.getInstance().getLocalTimeZone(null));
                //return ft.format(calendar.getTime());
                //long h = (presenceOfDay / 60000L) / 60;
                //long m = (presenceOfDay / 60000L) % 60;
                if(result == 0L) return "00:00";
                long h = result / 60;
                long m = result % 60;
                return String.format("%02d:%02d", h,m);
            }

            // первый вход - последний выход
            public String getTimeEnterExit() {
                if (events.isEmpty())
                    return "";
                StringBuilder sb = new StringBuilder();
                for (Event e : events) {
                    if (EnterEvent.isEntryOrExitEvent(e.getPassdirection())) {
                        if(StringUtils.isNotEmpty(e.getGuardianFIO())){
                            sb.append(e.getGuardianFIO()).append(" ");
                        }
                        sb.append(timeFormat.format(new Date(e.getTime())));
                        sb.append((EnterEvent.isEntryOrReEntryEvent(e.getPassdirection())?" (+)":" (-)"));
                        if(!e.equals(events.last())) sb.append(", ");
                    }
                }
                if (sb.length()>0)
                    return sb.toString();
                else
                    return "-";

            }
        }

        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar)
                throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            parameterMap.put("orgName", org.getOfficialName());
            calendar.setTime(startTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, endTime, (Calendar) calendar.clone(), parameterMap));
            Date generateEndTime = new Date();
            return new AutoEnterEventReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime,
                Calendar calendar, Map<String, Object> parameterMap) throws Exception {
            HashMap<Integer, HashMap<Integer, ReportItem>> mapItems = new HashMap<Integer, HashMap<Integer, ReportItem>>();
            List<ReportItem> resultRows = new LinkedList<ReportItem>();
            Calendar c = Calendar.getInstance();

            String typeCondition = "";
            String typeConditionsValue = (String)getReportProperties().get("enterEventType");
            if (typeConditionsValue != null) {
                String typeConditionsValues[] = typeConditionsValue.split(RuleProcessor.DELIMETER);
                if (typeConditionsValues.length > 1)
                    throw new Exception(String.format("%s: Параметр enterEventType не может принимать несколько значений.", AutoEnterEventByDaysReport.class.getSimpleName()));
                if ((typeConditionsValues[0].trim().equals(RuleCondition.ENTEREVENT_TYPE_TEXT[RuleCondition.ENTEREVENT_TYPE_STUDS])))
                    typeCondition = String.format("AND c.idOfClientGroup<%d ", ClientGroup.PREDEFINED_ID_OF_GROUP_EMPLOYEES);
                else if ((typeConditionsValues[0].trim().equals(RuleCondition.ENTEREVENT_TYPE_TEXT[RuleCondition.ENTEREVENT_TYPE_WITHOUTSTUDS])))
                    typeCondition = String.format("AND c.idOfClientGroup>=%d ", ClientGroup.PREDEFINED_ID_OF_GROUP_EMPLOYEES);
            }

            //Query query = session.createSQLQuery(
            //        "SELECT p.firstname, p.surname, p.secondname, e.evtdatetime, e.passdirection, c.idOfClient, g.groupname, e.guardianid "
            //        + " FROM cf_enterevents e, cf_clients c, cf_persons p, cf_clientgroups g "
            //        + " WHERE e.idofclient=c.idofclient and p.idofperson=c.idofperson AND e.idoforg=:idOfOrg AND g.idofclientgroup=c.idofclientgroup AND g.idoforg=c.idoforg "
            //        + " where c.idoforg=:idOfOrg"
            //        + " AND e.evtdatetime>=:startTime AND e.evtdatetime<=:endTime "
            //        + typeCondition+ " AND c.idOfClientGroup!=1100000060 "
            //        + " order by g.groupname, e.evtdatetime/(1000*60*60*24), p.surname, p.firstname, p.secondname;");

            Query query = session.createSQLQuery(
                    "SELECT p.firstname, p.surname, p.secondname, e.evtdatetime, e.passdirection, c.idOfClient, g.groupname, e.guardianid"
                    + " from cf_enterevents e"
                    + " left join cf_clients c on c.idofclient=e.idofclient "
                    + " left join cf_persons p on c.idofperson=p.idofperson "
                    + " left join cf_clientgroups g on "
                    + " g.idofclientgroup=c.idofclientgroup and g.idoforg=c.idoforg "+ typeCondition+ "  AND c.idOfClientGroup!=1100000060"
                    //+ " where c.idoforg=:idOfOrg"
                    + " where e.idoforg=:idOfOrg "
                    + " AND e.evtdatetime>=:startTime AND e.evtdatetime<=:endTime "
                    + typeCondition+ " AND c.idOfClientGroup!=1100000060 "
                    + " order by g.groupname, e.evtdatetime/(1000*60*60*24), p.surname, p.firstname, p.secondname;");

            //query.setParameter("startTime", CalendarUtils.getTimeFirstDayOfMonth(startTime.getTime()));
            //query.setParameter("endTime", CalendarUtils.getTimeLastDayOfMonth(startTime.getTime()));
            query.setParameter("startTime", startTime.getTime());
            query.setParameter("endTime", endTime.getTime());
            query.setParameter("idOfOrg", org.getIdOfOrg());

            List resultList = query.list();
            HashMap<Integer, ReportItem> tmpMap;
            int i = 1;
            for (Object o : resultList) {
                Object vals[]=(Object[])o;
                String firstname = (String)vals[0];
                String surname = (String)vals[1];
                String secondname = (String)vals[2];
                Long time = Long.parseLong(vals[3].toString());
                Integer passdirection = Integer.parseInt(vals[4].toString());
                Long idOfClient = Long.parseLong(vals[5].toString());
                String groupName = (String)vals[6];
                String guardianFIO=null;
                if(vals[7]!=null && StringUtils.isNotEmpty(vals[7].toString())){
                    Long guardianId = Long.parseLong(vals[7].toString());
                    Criteria guardianCriteria = session.createCriteria(Client.class);
                    guardianCriteria.createAlias("person","p");
                    guardianCriteria.add(Restrictions.eq("idOfClient",guardianId));
                    guardianCriteria.add(Restrictions.isNotNull("person"));
                    guardianCriteria.setProjection(Projections.projectionList().add(Projections.property("p.surname"))
                            .add(Projections.property("p.firstName")).add(Projections.property("p.secondName")));
                    Object o1 = guardianCriteria.uniqueResult();
                    if(o1!=null) {
                        Object[] fields = (Object[]) o1;
                        StringBuilder stringBuilder = new StringBuilder();
                        String guardianSurname = fields[0]==null?"":fields[0].toString();
                        String guardianFirstname = fields[1]==null?"":fields[1].toString();
                        String guardianSecondname = fields[2]==null?"":fields[2].toString();
                        if (StringUtils.isNotEmpty(guardianSurname))
                            stringBuilder.append(guardianSurname).append(' ');
                        if (StringUtils.isNotEmpty(guardianFirstname))
                            stringBuilder.append(guardianFirstname.trim().charAt(0)).append(". ");
                        if (StringUtils.isNotEmpty(guardianSecondname))
                            stringBuilder.append(guardianSecondname.trim().charAt(0)).append('.');
                        guardianFIO = stringBuilder.toString();
                    }
                }
                c.setTimeInMillis(time);
                int day = c.get(Calendar.DAY_OF_MONTH);

                tmpMap = mapItems.get(idOfClient.hashCode());
                if (tmpMap == null) {
                    tmpMap = new HashMap<Integer, ReportItem>(31);
                    mapItems.put(idOfClient.hashCode(), tmpMap);
                }
                ReportItem reportItem = tmpMap.get(day);
                if (reportItem == null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    if (surname != null && !surname.trim().isEmpty())
                    stringBuilder.append(surname).append(' ');
                    if (firstname != null && !firstname.trim().isEmpty())
                        stringBuilder.append(firstname.trim().charAt(0)).append(". ");
                    if (secondname != null && !secondname.trim().isEmpty())
                        stringBuilder.append(secondname.trim().charAt(0)).append('.');
                    reportItem = new ReportItem(i++, stringBuilder.toString().trim(), dateFormat.format(new Date(time)), groupName);
                    tmpMap.put(day, reportItem);
                    resultRows.add(reportItem);
                }
                //reportItem.getEvents().add(new Event(time, passdirection, guardianFIO));
                reportItem.addEvent(new Event(time, passdirection, guardianFIO));
            }
            return new JRBeanCollectionDataSource(resultRows);
        }

    }


    public AutoEnterEventReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfOrg);    //To change body of overridden methods use File | Settings | File Templates.
    }
    private static final Logger logger = LoggerFactory.getLogger(AutoEnterEventReport.class);

    public AutoEnterEventReport() {}

    @Override
    public BasicReportForOrgJob createInstance() {
        return new AutoEnterEventReport();
    }

    @Override
    public BasicReportJob.Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    @Override
    public int getDefaultReportPeriod() {
        return REPORT_PERIOD_PREV_DAY;
    }
}


