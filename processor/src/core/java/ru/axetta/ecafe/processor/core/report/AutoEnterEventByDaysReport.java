/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import com.google.common.collect.Lists;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuleProcessor;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.collections.map.MultiValueMap;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
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
    public static final String TEMPLATE_FILE_NAMES_FOR_CLIENT = "AutoEnterEventByDaysReportClient.jasper";
    public static final String P_ID_CLIENT = "idOfClients";
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{28, 29, -3, 22, 23, 24};

    public static class Builder extends BasicReportJob.Builder {

        private final String templateFilename;

        public static class ReportItem {

            private static String[] data = new String[31];
            private static String[] dataweek = new String[4];
            private static SimpleDateFormat ft = new SimpleDateFormat("hh:mm");

            static {
                for (int day = 0; day < 31; day++) {
                    data[day] = "";
                }
                for (int week = 0; week < 4; week++) {
                    dataweek[week] = "";
                }
            }

            private String fio = null;
            private List<String> timeList = new ArrayList<String>(Arrays.asList(data));
            private List <String> timeinWeekList = new ArrayList<String>(Arrays.asList(dataweek));
            //private Map<Integer, Date> dayMaxMin = new HashMap<Integer, Date>();
            private MultiValueMap dayMaxMin = new MultiValueMap();
            //Массив входов для одного дня
            private Map<Integer, List<Date>> dayEntry = new HashMap<Integer, List<Date>>();
            //Массив выходов для одного дня
            private Map<Integer, List<Date>> dayExit = new HashMap<Integer, List<Date>>();
            private String groupName = "";
            private String presenceOfDay = "";

            public String getFio() {
                return fio;
            }

            public void setFio(String fio) {
                this.fio = fio;
            }

            public void addEventTime(int type, Long day, Long dateTime) {
                if (type == 0) {
                    addEntryTime(day, dateTime);
                } else {
                    addExitTime(day, dateTime);
                }
            }

            private void addEntryTime(Long day, Long dateTime) {
                int dayOfMonth = CalendarUtils.getDayOfMonth(new Date(day)) - 1;
                List<Date> date = dayEntry.get(dayOfMonth);
                if (date == null)
                    date = new ArrayList<>();
                date.add(new Date(dateTime));
                dayEntry.put(dayOfMonth, date);

            }

            private void addExitTime(Long day, Long dateTime) {
                int dayOfMonth = CalendarUtils.getDayOfMonth(new Date(day)) - 1;
                List<Date> date = dayExit.get(dayOfMonth);
                if (date == null)
                    date = new ArrayList<>();
                date.add(new Date(dateTime));
                dayExit.put(dayOfMonth, date);
            }

            public void buildTimeList() {
                long timeInOrg = 0;
                long countDays = 0;
                ArrayList<Long> timeinWeekLong = new ArrayList<>();
                for (int day = 0; day < 31; day++) {
                    List<Date> dateEntry = dayEntry.get(day);
                    List<Date> dateExit = dayExit.get(day);
                    if (dateEntry != null || dateExit != null) {
                        // Если есть и вход и выход
                        if (dateEntry != null && dateExit != null) {
                            //Суммарное время пребывания в организации
                            final long duration = getDuration(dateEntry, dateExit);
                            //final long duration = dateEntry.getTime() - dateExit.getTime();
                            String s = String.format("%s-%s", CalendarUtils.timeToString(getMaxMinDate(dateEntry, false)),
                                    CalendarUtils.timeToString(getMaxMinDate(dateExit, true)));
                            timeList.add(day, s);
                            //timeInOrg += Math.abs(duration / 60000);
                            timeInOrg += duration;
                            countDays++;
                        } else {
                            // Если только вход
                            if (dateEntry != null) {
                                timeList.add(day, "ВХ " + CalendarUtils.timeToString(getMaxMinDate(dateEntry, false)));
                            }
                            // Если только выход
                            if (dateExit != null) {
                                timeList.add(day, "ВЫХ " + CalendarUtils.timeToString(getMaxMinDate(dateExit, true)));
                            }
                        }
                    }
                    if (day == 6)
                        timeinWeekLong.add(timeInOrg);
                    if (day == 13)
                        timeinWeekLong.add(timeInOrg - timeinWeekLong.get(0));
                    if (day == 20)
                        timeinWeekLong.add(timeInOrg - (timeinWeekLong.get(1) + timeinWeekLong.get(0)));
                    if (day == 27)
                        timeinWeekLong.add(timeInOrg - (timeinWeekLong.get(2) + timeinWeekLong.get(1) + timeinWeekLong.get(0)));
                }
                if (countDays > 0) {
                    presenceOfDay = getStringTime (timeInOrg);
                }
                for (int i = 0; i < 4; i++)
                    timeinWeekList.add(i, getStringTime(timeinWeekLong.get(i)));
            }

            private String getStringTime (Long time)
            {
                long result = time/1000;
                long min = result / 60;
                long sec = result % 60;
                if (min < 60)
                    return String.format("%02d:%02d", min, sec);
                else
                {
                    long hour = min/60;
                    min = min%60;
                    return String.format("%02d:%02d:%02d", hour, min, sec);
                }
            }

            //Функция для получения максимально/минимальной даты из списка
            private Date getMaxMinDate(List<Date> dates, boolean maximum) {
                Date date = null;
                for (Date date1 : dates) {
                    if (date == null) {
                        date = new Date(date1.getTime());
                    } else {
                        if (maximum) {
                            if (date.getTime() < date1.getTime()) {
                                date.setTime(date1.getTime());
                            }
                        } else {
                            if (date.getTime() > date1.getTime()) {
                                date.setTime(date1.getTime());
                            }
                        }
                    }
                }
                return date;
            }

            private Long getDuration(List<Date> dateEntry, List<Date> dateExit)
            {
                Long durations = 0L;
                try {
                    List<Date> dateTotal = new ArrayList<>();
                    dateTotal.addAll(dateEntry);
                    dateTotal.addAll(dateExit);
                    Collections.sort(dateTotal);

                    Long start = null;
                    Long end;
                    boolean wasEnd = false;
                    for (Date date : dateTotal) {
                        if (dateEntry.indexOf(date) != -1) {
                            start = date.getTime();
                            wasEnd = false;
                        } else {
                            if (start != null) {
                                end = date.getTime();
                                if (!wasEnd) {
                                    durations += (end - start);
                                }
                                wasEnd = true;
                            }
                        }
                    }
                } catch (Exception e)
                {
                    durations = 0L;
                }
                return durations;
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

            public List<String> getTimeinWeekList() {
                return timeinWeekList;
            }

            public void setTimeinWeekList(List<String> timeinWeekList) {
                this.timeinWeekList = timeinWeekList;
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

            Boolean isAllFriendlyOrgs;
            if (reportProperties.getProperty("isAllFriendlyOrgs") != null) {
                isAllFriendlyOrgs = Boolean.valueOf(reportProperties.getProperty("isAllFriendlyOrgs"));
            } else {
                isAllFriendlyOrgs = true;
            }
            //Если строим отчет по всем корпусам, то добавляем их названия
            if (isAllFriendlyOrgs)
            {
                StringBuilder sb = new StringBuilder();
                //sb.append(", ");
                for (Org org : orgLoad.getFriendlyOrg()) {
                    sb.append(org.getShortAddress()).append(", ");
                }
                parameterMap.put("allAdress", "В отчете представлена фиксация событий посещения любого здания ОО");
                parameterMap.put("shortAddress", sb.substring(0, sb.length() - 2));
            }
            else {
                parameterMap.put("allAdress", "В отчете представлена фиксация событий посещения главного здания ОО");
                parameterMap.put("shortAddress", orgLoad.getShortAddress());
            }

            parameterMap.put("shortNameInfoService", orgLoad.getShortNameInfoService());
            calendar.setTime(startTime);
            Date firtstDayOfMonth = CalendarUtils.getFirstDayOfMonth(startTime);
            for (int day = 1; day <= 31; day++) {
                daysOfMonth.add((day - 1), String.format("%d %s", day,
                        CalendarUtils.dayInWeekToString(CalendarUtils.addDays(firtstDayOfMonth, day - 1))));
            }
            parameterMap.put("days", daysOfMonth);
            parameterMap.put("monthName", calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("ru")));
            JasperPrint jasperPrint = JasperFillManager
                    .fillReport(templateFilename, parameterMap, createDataSource(session, org, startTime, endTime));
            Date generateEndTime = new Date();
            return new AutoEnterEventByDaysReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime)
                throws Exception {
            Map<Long, ReportItem> map = new HashMap<Long, ReportItem>();
            String typeConditionsValue = (String) getReportProperties().get("enterEventType");

            //DetachedCriteria orgCriteria = DetachedCriteria.forClass(Org.class);
            //orgCriteria.add(Restrictions.eq("idOfOrg", org.getIdOfOrg()));
            //orgCriteria.setProjection(Property.forName("friendlyOrg"));

            //по все корпусам фильтр
            Boolean isAllFriendlyOrgs;

            if (reportProperties.getProperty("isAllFriendlyOrgs") != null) {
                isAllFriendlyOrgs = Boolean.valueOf(reportProperties.getProperty("isAllFriendlyOrgs"));
            } else {
                isAllFriendlyOrgs = true;
            }

            Set<Long> ids = new HashSet<Long>();

            if (isAllFriendlyOrgs) {
                final String sql = String.format("SELECT friendlyorg FROM cf_friendly_organization WHERE currentorg=%d",
                        org.getIdOfOrg());
                Query query = session.createSQLQuery(sql);
                List orgList = query.list();

                ids.add(org.getIdOfOrg());
                for (Object obj : orgList) {
                    ids.add(Long.parseLong(obj.toString()));
                }
            } else {
                ids.add(org.getIdOfOrg());
            }

            //Фильтр по клиенту
            List<String> stringClientsList;
            List<Long> clientsList = new ArrayList<>();
            try
            {
                String idOfClients = StringUtils.trimToEmpty(reportProperties.getProperty(P_ID_CLIENT));
                stringClientsList = Arrays.asList(StringUtils.split(idOfClients, ','));
                for (String clientId: stringClientsList)
                    clientsList.add(Long.valueOf(clientId));
            } catch (Exception e)
            {
                stringClientsList = new ArrayList<>();
                clientsList = new ArrayList<>();
            }


            //группа фильтр
            String groupName;
            if (reportProperties.getProperty("groupName") == null) {
                groupName = null;
            } else {
                groupName = reportProperties.getProperty("groupName");
            }

            ArrayList<String> groupList = new ArrayList<String>();

            if (groupName != null) {
                String[] groups = StringUtils.split(groupName, ",");
                for (String str : groups) {
                    groupList.add(str);
                }
            }
            List<Client> clientList;
            if (clientsList.isEmpty()) {
                Criteria clientCriteria = session.createCriteria(Client.class);
                clientCriteria.createAlias("clientGroup", "cg", JoinType.LEFT_OUTER_JOIN);
                //clientCriteria.add(Property.forName("org.idOfOrg").in(orgCriteria));
                clientCriteria.add(Restrictions.in("org.idOfOrg", ids));

                if (!clientsList.isEmpty()) {
                    clientCriteria.add(Restrictions.in("idOfClient", clientsList));
                }
                //clientCriteria.add(Restrictions.ne("idOfClientGroup", 1100000060L)); // Исключаем из списка Выбывших
                if (!groupList.isEmpty()) {
                    clientCriteria.add(Restrictions.in("cg.groupName", groupList));
                }
                if (typeConditionsValue != null) {
                    // значения могут перечисляться через запятую, однако данный параметр может принимать только 1 из "все", "учащиеся", "все_без_учащихся"
                    String typeConditionsValues[] = typeConditionsValue.split(RuleProcessor.DELIMETER);
                    if (typeConditionsValues.length > 1) {
                        throw new Exception(String.format("%s: Параметр enterEventType не может принимать несколько значений.",
                                AutoEnterEventByDaysReport.class.getSimpleName()));
                    }
                    if ((typeConditionsValues[0].trim().equals(RuleCondition.ENTEREVENT_TYPE_TEXT[RuleCondition.ENTEREVENT_TYPE_STUDS]))) {
                        clientCriteria.add(Restrictions.lt("idOfClientGroup", ClientGroup.PREDEFINED_ID_OF_GROUP_EMPLOYEES));
                    } else if ((typeConditionsValues[0].trim().equals(RuleCondition.ENTEREVENT_TYPE_TEXT[RuleCondition.ENTEREVENT_TYPE_WITHOUTSTUDS]))) {
                        clientCriteria.add(Restrictions.ge("idOfClientGroup", ClientGroup.PREDEFINED_ID_OF_GROUP_EMPLOYEES));
                    }
                }
                clientList = clientCriteria.list();
            }
            else
            {
                Criteria clientCriteria = session.createCriteria(Client.class);
                clientCriteria.add(Restrictions.in("idOfClient", clientsList));
                clientList = clientCriteria.list();
            }
            for (Client client : clientList) {
                ReportItem reportItem = new ReportItem();
                if (client.getClientGroup() != null && client.getClientGroup().getGroupName() != null)
                    reportItem.setGroupName(client.getClientGroup().getGroupName());
                else
                    reportItem.setGroupName("Не найдена");
                if (client.getPerson() != null && client.getPerson().getFullName() != null)
                    reportItem.setFio(client.getPerson().getFullName());
                else
                    reportItem.setFio("Не найдена");
                map.put(client.getIdOfClient(), reportItem);
            }

            if (map.keySet().isEmpty()) {
                // составим пустой отчет
                ReportItem emptyItem = new ReportItem();
                emptyItem.setGroupName("");
                emptyItem.setFio("");
                return new JRBeanCollectionDataSource(Arrays.asList(emptyItem));
            }

            List<Long> clientIds = new ArrayList<>(map.keySet());
            List<List<Long>> partitionedClientIds = Lists.partition(clientIds, 30000);
            List list = new ArrayList<>();
            for (List<Long> partitionClientIds: partitionedClientIds) {
                Criteria reportCrit = session.createCriteria(EnterEvent.class);
                reportCrit.createAlias("client", "c", JoinType.INNER_JOIN);
                reportCrit.createAlias("c.clientGroup", "cg", JoinType.LEFT_OUTER_JOIN);
                reportCrit.add(Restrictions.in("org.idOfOrg", ids));
                reportCrit.add(Restrictions.in("passDirection", Arrays.asList(0, 1, 6, 7)));
                reportCrit.add(Restrictions.in("client.idOfClient", partitionClientIds));
                reportCrit.add(Restrictions.between("evtDateTime", startTime, endTime));
                if (!groupList.isEmpty()) {
                    reportCrit.add(Restrictions.in("cg.groupName", groupList));
                }
                reportCrit.setProjection(Projections.projectionList().add(Projections.groupProperty("client.idOfClient"))
                        .add(Projections.sqlGroupProjection("({alias}.evtDateTime/24/3600/1000)*24*3600*1000 as eventDay",
                                "{alias}.evtDateTime", new String[]{"eventDay"}, new Type[]{new LongType()}))
                        .add(Projections
                                .sqlGroupProjection("CASE WHEN (passdirection in (0,6)) then 0 else 1 end as passtext",
                                        "{alias}.passdirection", new String[]{"passtext"}, new Type[]{new IntegerType()}))
                        .add(Projections.sqlGroupProjection(
                                "(case when {alias}.passDirection in (0,6) then min({alias}.evtDateTime) else max({alias}.evtDateTime) end) as eventDateTime",
                                "{alias}.passDirection", new String[]{"eventDateTime"}, new Type[]{new LongType()})));
                list.addAll(reportCrit.list());
            }
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
            for (ReportItem item : values) {
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
                    //return o1.getGroupName().replaceAll("[\\s|-]+", "").length()-o2.getGroupName().replaceAll("[\\s|-]+", "").length();
                    int stringCompareResult = ((Integer) o1.getGroupName().replaceAll("[\\s|-]+", "").length())
                            .compareTo(o2.getGroupName().replaceAll("[\\s|-]+", "").length());
                    if (stringCompareResult != 0) {
                        return stringCompareResult;
                    }

                    return o1.getGroupName().toLowerCase().compareTo(o2.getGroupName().toLowerCase());
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



