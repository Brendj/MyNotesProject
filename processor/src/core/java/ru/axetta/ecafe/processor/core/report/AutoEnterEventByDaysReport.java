/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuleProcessor;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.collections.map.MultiValueMap;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;
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
public class AutoEnterEventByDaysReport extends BasicReportForMainBuildingOrgJob {
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
    public static final String REPORT_NAME = "Сводный отчет по посещению";
    public static final String[] TEMPLATE_FILE_NAMES = {"AutoEnterEventByDaysReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{28, 29, -3, 22, 23, 24};

    public static class Builder extends BasicReportJob.Builder {

        private final String templateFilename;

        public static class ReportItem {

            private static String[] data = new String[31];
            private static SimpleDateFormat ft = new SimpleDateFormat("hh:mm");
            static {for (int day = 0; day < 31; day++) data[day] = "";}

            private String fio = null;
            private List<String> timeList = new ArrayList<String>(Arrays.asList(data));
            //private Map<Integer, Date> dayMaxMin = new HashMap<Integer, Date>();
            private MultiValueMap dayMaxMin = new MultiValueMap();
            private Map<Integer, Date> dayEntry = new HashMap<Integer, Date>();
            private Map<Integer, Date> dayExit = new HashMap<Integer, Date>();
            private String groupName = "";
            private String presenceOfDay="";

            public String getFio() {
                return fio;
            }

            public void setFio(String fio) {
                this.fio = fio;
            }

            public void addEventTime(int type, Long day, Long dateTime){
                if(type==0){
                    addEntryTime(day, dateTime);
                } else {
                    addExitTime(day, dateTime);
                }
            }

            private void addEntryTime(Long day, Long dateTime) {
                int dayOfMonth = CalendarUtils.getDayOfMonth(new Date(day))-1;
                Date date = dayEntry.get(dayOfMonth);
                if(date==null || date.getTime()>dateTime){
                    dayEntry.put(dayOfMonth, new Date(dateTime));
                }
            }

            private void addExitTime(Long day, Long dateTime) {
                int dayOfMonth = CalendarUtils.getDayOfMonth(new Date(day))-1;
                Date date = dayExit.get(dayOfMonth);
                if(date==null || date.getTime()<dateTime){
                    dayExit.put(dayOfMonth, new Date(dateTime));
                }
            }

            public void buildTimeList() {
                long timeInOrg = 0;
                long countDays = 0;
                for (int day = 0; day < 31; day++) {
                    Date dateEntry = dayEntry.get(day);
                    Date dateExit = dayExit.get(day);
                    if(dateEntry!=null || dateExit!=null){
                        // Если есть и вход и выход
                        if(dateEntry!=null && dateExit!=null){
                            final long duration = dateEntry.getTime() - dateExit.getTime();
                            String s = String.format("%s-%s", CalendarUtils.timeToString(dateEntry),
                                    CalendarUtils.timeToString(dateExit));
                            timeList.add(day, s);
                            timeInOrg +=Math.abs(duration/60000);
                            countDays++;
                        } else {
                            // Если только вход
                            if(dateEntry!=null){
                                timeList.add(day, CalendarUtils.timeToString(dateEntry));
                            }
                            // Если только выход
                            if(dateExit!=null){
                                timeList.add(day, CalendarUtils.timeToString(dateExit));
                            }
                        }
                    }
                }
                if(countDays>0){
                    long result = timeInOrg / countDays;
                    long h = result / 60;
                    long m = result % 60;
                    presenceOfDay = String.format("%02d:%02d", h,m);
                }
            }

            public List<String> getTimeList() {
                return timeList;
            }

            public void setTimeList(List<String> timeList) {
                this.timeList = timeList;
            }

            public String getGroupName() {
                return groupName;
            }

            public void setGroupName(String groupName) {
                this.groupName = groupName;
            }

            public String getPresenceOfDay() {
                return presenceOfDay;
            }

            public void setPresenceOfDay(String presenceOfDay) {
                this.presenceOfDay = presenceOfDay;
            }
        }

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            List<String> daysOfMonth = new ArrayList<String>(31); // 1 Вс	2 Пн	3 Вт	4 Ср ...
            Org orgLoad = (Org) session.load(Org.class, org.getIdOfOrg());
            StringBuilder sb = new StringBuilder();
            for(Org org : orgLoad.getFriendlyOrg()) {
                sb.append(org.getShortAddress()).append(", ");
            }
            parameterMap.put("shortNameInfoService", orgLoad.getShortNameInfoService());
            parameterMap.put("shortAddress", sb.substring(0, sb.length() - 2));
            calendar.setTime(startTime);
//            Calendar c = Calendar.getInstance();
//            Long startDate = CalendarUtils.getTimeFirstDayOfMonth(startTime.getTime());
            for (int day = 1; day <= 31; day++) {
                daysOfMonth.add((day - 1), String.format("%d %s", day, CalendarUtils.dayInWeekToString(CalendarUtils.addDays(startTime, day - 1))));
                //daysOfMonth.add((day - 1), String.format("%d %s", day, CalendarUtils.dayInWeekToString(startDate + (day - 1) * 1000 * 60 * 60 * 24)));
            }
            parameterMap.put("days", daysOfMonth);
            parameterMap.put("monthName", calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("ru")));
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, endTime));
            Date generateEndTime = new Date();
            return new AutoEnterEventByDaysReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime) throws Exception {
            Map<Long, ReportItem> map = new HashMap<Long, ReportItem>();
            String typeConditionsValue = (String)getReportProperties().get("enterEventType");

            //DetachedCriteria orgCriteria = DetachedCriteria.forClass(Org.class);
            //orgCriteria.add(Restrictions.eq("idOfOrg", org.getIdOfOrg()));
            //orgCriteria.setProjection(Property.forName("friendlyOrg"));

            final String sql = String.format("SELECT friendlyorg FROM cf_friendly_organization WHERE currentorg=%d", org.getIdOfOrg());
            Query query = session.createSQLQuery(sql);
            List orgList = query.list();
            Set<Long> ids = new HashSet<Long>();
            ids.add(org.getIdOfOrg());
            for (Object obj: orgList){
                ids.add(Long.parseLong(obj.toString()));
            }




            Criteria clientCriteria = session.createCriteria(Client.class);
            //clientCriteria.add(Property.forName("org.idOfOrg").in(orgCriteria));
            clientCriteria.add(Restrictions.in("org.idOfOrg", ids));
            clientCriteria.add(Restrictions.ne("idOfClientGroup", 1100000060L)); // Исключаем из списка Выбывших
            if (typeConditionsValue != null) {
                // значения могут перечисляться через запятую, однако данный параметр может принимать только 1 из "все", "учащиеся", "все_без_учащихся"
                String typeConditionsValues[] = typeConditionsValue.split(RuleProcessor.DELIMETER);
                if (typeConditionsValues.length > 1)
                    throw new Exception(String.format("%s: Параметр enterEventType не может принимать несколько значений.", AutoEnterEventByDaysReport.class.getSimpleName()));
                if ((typeConditionsValues[0].trim().equals(RuleCondition.ENTEREVENT_TYPE_TEXT[RuleCondition.ENTEREVENT_TYPE_STUDS]))){
                    clientCriteria.add(Restrictions.lt("idOfClientGroup", ClientGroup.PREDEFINED_ID_OF_GROUP_EMPLOYEES));
                }
                else if ((typeConditionsValues[0].trim().equals(RuleCondition.ENTEREVENT_TYPE_TEXT[RuleCondition.ENTEREVENT_TYPE_WITHOUTSTUDS]))){
                    clientCriteria.add(Restrictions.ge("idOfClientGroup", ClientGroup.PREDEFINED_ID_OF_GROUP_EMPLOYEES));
                }
            }
            List<Client> clientList = clientCriteria.list();

            for (Client client: clientList){
                ReportItem reportItem = new ReportItem();
                reportItem.setGroupName(client.getClientGroup().getGroupName());
                reportItem.setFio(client.getPerson().getFullName());
                map.put(client.getIdOfClient(), reportItem);
            }

            if(map.keySet().isEmpty()){
                // составим пустой отчет
                ReportItem emptyItem = new ReportItem();
                emptyItem.setGroupName("");
                emptyItem.setFio("");
                return new JRBeanCollectionDataSource(Arrays.asList(emptyItem));
            }

            Criteria reportCrit = session.createCriteria(EnterEvent.class);
            reportCrit.add(Restrictions.eq("org.idOfOrg", org.getIdOfOrg()));
            reportCrit.add(Restrictions.in("passDirection", Arrays.asList(0, 1, 6, 7)));
            reportCrit.add(Restrictions.in("client.idOfClient", map.keySet()));
            reportCrit.add(Restrictions.between("evtDateTime", startTime, endTime));
            reportCrit.setProjection(Projections.projectionList().add(Projections.groupProperty("client.idOfClient"))
                    .add(Projections.sqlGroupProjection("({alias}.evtDateTime/24/3600/1000)*24*3600*1000 as eventDay",
                            "{alias}.evtDateTime", new String[]{"eventDay"}, new Type[]{new LongType()}))
                    .add(Projections.sqlGroupProjection(
                            "CASE WHEN (passdirection in (0,6)) then 0 else 1 end as passtext",
                            "{alias}.passdirection", new String[]{"passtext"}, new Type[]{new IntegerType()}))
                    .add(Projections.sqlGroupProjection(
                            "(case when {alias}.passDirection in (0,6) then min({alias}.evtDateTime) else max({alias}.evtDateTime) end) as eventDateTime",
                            "{alias}.passDirection", new String[]{"eventDateTime"}, new Type[]{new LongType()}))
            );
            List list = reportCrit.list();
            for (Object obj : list) {
                Object[] row = (Object[]) obj;
                Long day = Long.valueOf(row[1].toString());
                Long eventDateTime = Long.valueOf(row[3].toString());
                int type = Integer.valueOf(row[2].toString());
                Long idOfClient = Long.valueOf(row[0].toString());
                ReportItem item = map.get(idOfClient);
                item.addEventTime(type, day, eventDateTime);
            }
            final List<ReportItem> values = new ArrayList<ReportItem>(map.values());
            for (ReportItem item: values){
                item.buildTimeList();
            }
            // сортируем по имени групп
            Comparator<ReportItem> fioComparator = new Comparator<ReportItem>() {
                @Override
                public int compare(ReportItem o1, ReportItem o2) {
                    return o1.getFio().compareTo(o2.getFio());
                }
            };
            Collections.sort(values, fioComparator);
            // сортируем по имени групп
            Comparator<ReportItem> nameComparator = new Comparator<ReportItem>() {
                 @Override
                 public int compare(ReportItem o1, ReportItem o2) {
                     return o1.getGroupName().compareTo(o2.getGroupName());
                 }
            };
            // сортируем по длите имени группы: например чтобы сначало были начальные классы потом старшие
            Collections.sort(values, nameComparator);
            Comparator<ReportItem> nameLengthComparator = new Comparator<ReportItem>() {
                @Override
                public int compare(ReportItem o1, ReportItem o2) {
                    return o1.getGroupName().length()-o2.getGroupName().length();
                }
            };
            Collections.sort(values, nameLengthComparator);

            return new JRBeanCollectionDataSource(values);
        }

    }


    public AutoEnterEventByDaysReport(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime,
                idOfOrg);    //To change body of overridden methods use File | Settings | File Templates.
    }

    private static final Logger logger = LoggerFactory.getLogger(AutoEnterEventByDaysReport.class);

    public AutoEnterEventByDaysReport() {
    }

    @Override
    public BasicReportForOrgJob createInstance() {
        return new AutoEnterEventByDaysReport();
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
        return REPORT_PERIOD_CURRENT_MONTH;
    }

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }
}



