/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import ru.axetta.ecafe.processor.core.RuleProcessor;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.dao.clients.ClientDao;
import ru.axetta.ecafe.processor.core.report.model.autoenterevent.Data;
import ru.axetta.ecafe.processor.core.report.model.autoenterevent.MapKeyModel;
import ru.axetta.ecafe.processor.core.report.model.autoenterevent.ShortBuilding;
import ru.axetta.ecafe.processor.core.report.model.autoenterevent.StClass;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class AutoEnterEventV2Report extends BasicReportForOrgJob {

    public static SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yy");

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {

    }

    @Override
    public AutoReportRunner getAutoReportRunner() {
        return new AutoReportRunner() {
            public void run(AutoReportBuildTask autoReportBuildTask) {
                if (getLogger().isDebugEnabled()) {
                    getLogger().debug(String.format("Building auto reports \"%s\"", getMyClass().getCanonicalName()));
                }
                String classPropertyValue = getMyClass().getCanonicalName();
                List<AutoReport> autoReports = new ArrayList<AutoReport>();
                Session session = null;
                org.hibernate.Transaction transaction = null;
                try {
                    session = autoReportBuildTask.sessionFactory.openSession();
                    transaction = BasicReport.createTransaction(session);
                    transaction.begin();
                    List<RuleProcessor.Rule> thisReportRulesList = getThisReportRulesList(session);

                    for (RuleProcessor.Rule rule : thisReportRulesList) {
                        String pre_orgs = rule.getExpressionValue(ReportPropertiesUtils.P_ID_OF_ORG);
                        if (pre_orgs == null) {
                            pre_orgs = getAllOrgs(session);
                        }
                        String[] idOfOrgs = pre_orgs.split(",");
                        for (String id : idOfOrgs) {
                            Org org = (Org)session.load(Org.class, Long.parseLong(id));
                            if (!doReportByOrgCondition(session, org)) {
                                continue;
                            }
                            Properties properties = new Properties();
                            ReportPropertiesUtils.addProperties(properties, getMyClass(), autoReportBuildTask);
                            properties.setProperty(ReportPropertiesUtils.P_ID_OF_ORG, id == null ? "" : id);
                            properties.setProperty(ReportPropertiesUtils.P_ORG_NUMBER_IN_NAME, org.getOrgNumberInName());

                            BasicReportForOrgJob report = createInstance();
                            report.setReportProperties(properties);
                            //report.setIdOfOrg(Long.parseLong(id));
                            report.initialize(autoReportBuildTask.startTime, autoReportBuildTask.endTime, Long.parseLong(id),
                                    autoReportBuildTask.templateFileName, autoReportBuildTask.sessionFactory,
                                    autoReportBuildTask.startCalendar);

                            autoReports.add(new AutoReport(report, properties));
                        }
                    }
                    transaction.commit();
                    transaction = null;
                    autoReportBuildTask.executorService.execute(
                            new AutoReportProcessor.ProcessTask(autoReportBuildTask.autoReportProcessor, autoReports,
                                    autoReportBuildTask.documentBuilders));
                } catch (Exception e) {
                    getLogger().error(String.format("Failed at building auto reports \"%s\"", classPropertyValue), e);
                } finally {
                    HibernateUtils.rollback(transaction, getLogger());
                    HibernateUtils.close(session, getLogger());
                }
            }
        };
    }

    private String getAllOrgs(Session session) {
        Query query = session.createSQLQuery("SELECT string_agg(CAST(idOfOrg as varchar), ',') FROM cf_orgs");
        return (String)query.uniqueResult();
    }

    private boolean doReportByOrgCondition(Session session, Org org) {
        Set<Org> fOrgs = org.getFriendlyOrg();
        if (fOrgs.size() <= 1) {
            return true;
        } else {
            boolean mainBuildingExists = false;
            for (Org o : fOrgs) {
                if (o.isMainBuilding()) {
                    mainBuildingExists = true;
                    break;
                }
            }
            if (mainBuildingExists && org.isMainBuilding()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static class Builder extends BasicReportJob.Builder {


        private final String templateFilename;

        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }



        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            startTime = CalendarUtils.roundToBeginOfDay(startTime);
            parameterMap.put("orgName", org.getOfficialName());
            parameterMap.put("beginDate", CalendarUtils.dateShortToString(startTime));
            parameterMap.put("endDate", CalendarUtils.dateShortToString(endTime));


            endTime = CalendarUtils.endOfDay(endTime);
            calendar.setTime(startTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, org, startTime, endTime, (Calendar) calendar.clone(), parameterMap));
            Date generateEndTime = new Date();
            return new AutoEnterEventV2Report(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, org.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, OrgShortItem org, Date startTime, Date endTime,
                Calendar calendar, Map<String, Object> parameterMap) throws Exception {
            startTime = CalendarUtils.truncateToDayOfMonth(startTime);


            //Список организаций
            List<ShortBuilding> friendlyOrgs = getFriendlyOrgs(session, org.getIdOfOrg());
            String friendlyOrgsIds = "" + org.getIdOfOrg();
            Set<Long> ids = new HashSet<Long>();
            ids.add(org.getIdOfOrg());
            for (ShortBuilding building : friendlyOrgs) {
                friendlyOrgsIds += "," + building.getId();
                ids.add(building.getId());
            }

            ClientDao clientDao = RuntimeContext.getAppContext().getBean(ClientDao.class);

            List<Client> allByOrg = clientDao.findAllByOrg(ids);
            List<Data> currentClassList;
            Map<String, StClass> stClassMap = new HashMap<String, StClass>();

            List<Long> clientIdList = new LinkedList<Long>();
            for (Client client : allByOrg) {
                if (!stClassMap.containsKey(client.getClientGroup().getGroupName())) {
                    stClassMap.put(client.getClientGroup().getGroupName(),
                            new StClass(client.getClientGroup().getGroupName(), friendlyOrgs, new LinkedList<Data>()));
                }
                currentClassList = stClassMap.get(client.getClientGroup().getGroupName()).getDataList();
                if (!clientIdList.contains(client.getIdOfClient())) {
                    currentClassList.addAll(prepareDataList(client, friendlyOrgs, startTime, endTime));
                    clientIdList.add(client.getIdOfClient());
                }


            }


            //данные для отчета

            Query query = session.createSQLQuery(
                    "SELECT  ee.idofenterevent, ee.idoforg, ee.passdirection, ee.eventcode, ee.idofclient,ee.evtdatetime, "
                            + "    pn.firstname, pn.surname, pn.secondname, cg.groupname, os.officialname "
                            + "    FROM cf_enterevents ee "
                            + "    LEFT JOIN cf_clients cs  ON ee.idofclient = cs.idofclient "
                            + "    LEFT JOIN cf_persons pn ON pn.idofperson = cs.idofperson "
                            + "    LEFT JOIN cf_clientgroups cg ON cg.idofclientgroup = cs.idofclientgroup AND cs.idoforg = cg.idoforg "
                            + "    LEFT JOIN  cf_orgs os ON ee.idoforg = os.idoforg " + " WHERE ee.idoforg IN ("
                            + friendlyOrgsIds + ") AND cs.idoforg IN (" + friendlyOrgsIds + ") "
                            + " AND ee.evtdatetime BETWEEN " + startTime.getTime() + " AND " + endTime.getTime()
                            + "     AND ee.idofclient IS NOT null " + " AND ee.PassDirection in (0, 1, 6, 7) "

                            + "     AND cs.idofclientgroup != 1100000060 "
                            + "     ORDER BY os.officialname, cg.groupname, ee.idofclient,ee.evtdatetime     --limit 100");

            query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
            List rList = query.list();

            //парсим данные
            for (Object o : rList) {
                Map<String, Object> row = (Map<String, Object>) o;
                if (!stClassMap.containsKey(row.get("groupname"))) {
                    stClassMap.put((String) row.get("groupname"),
                            new StClass((String) row.get("groupname"), friendlyOrgs, new LinkedList<Data>()));
                }
                currentClassList = stClassMap.get((String) row.get("groupname")).getDataList();

                if (!clientIdList.contains(((BigInteger) row.get("idofclient")).longValue())) {
                    currentClassList.addAll(prepareDataList(row, friendlyOrgs, startTime, endTime));
                    clientIdList.add(((BigInteger) row.get("idofclient")).longValue());
                }
                for (Data event : currentClassList) {
                    if ((event.getF01().equals(((BigInteger) row.get("idofclient")).toString())) && (event.getF03()
                            .equals((String) row.get("groupname"))) && (event.getF04().equals(CalendarUtils
                            .dateShortToString(new Date(((BigInteger) row.get("evtdatetime")).longValue())))) && (event
                            .getF05().equals((String) row.get("officialname")))) {
                        updateEventData(event, row);
                    }
                }
            }
            Map<MapKeyModel, Long> usersEntrySummaryMap = new HashMap<MapKeyModel, Long>();
            //заполняем время внутри

            List<MapKeyModel> mapKeyModelList = new ArrayList<MapKeyModel>();

            List<StClass> stClassList = new LinkedList<StClass>(stClassMap.values());
            for (StClass stClass : stClassList) {
                if (stClass.getDataList() == null || stClass.getDataList().size() == 0) {
                    logger.error("stClass.getDataList().size() = ", stClass.getDataList().size());
                } else {
                    mapKeyModelList.add(new MapKeyModel(stClass.getDataList().get(0).getF04(),
                            stClass.getDataList().get(0).getF01()));
                    for (int i = 1; i < stClass.getDataList().size(); i++) {
                        MapKeyModel mapKeyModel = new MapKeyModel(stClass.getDataList().get(i).getF04(),
                                stClass.getDataList().get(i).getF01());
                        if (uniqueMapKeyModel(mapKeyModelList, mapKeyModel)) {
                            mapKeyModelList.add(mapKeyModel);
                        }
                    }

                    for (MapKeyModel model : mapKeyModelList) {
                        usersEntrySummaryMap.put(model, 0L);
                    }
                }
            }

            for (StClass stClass : stClassList) {
                for (Data data : stClass.getDataList()) {
                    updateInsideSummaryTime(data, usersEntrySummaryMap);
                }
            }
            for (StClass stClass : stClassList) {
                for (Data data : stClass.getDataList()) {
                    updateEntrySummaryTime(data, usersEntrySummaryMap);
                }
            }
            Collections.sort(stClassList);
            return new JRBeanCollectionDataSource(stClassList);
        }

        public boolean uniqueMapKeyModel(List<MapKeyModel> mapKeyModels, MapKeyModel mapKeyModel) {
            for (MapKeyModel model : mapKeyModels) {
                if (model.getDate().equals(mapKeyModel.getDate()) && model.getClientID().equals(mapKeyModel.getClientID())) {
                    return false;
                }
            }
            return true;
        }


        private static void updateInsideSummaryTime(Data data, Map<MapKeyModel, Long> entrySummaryMap)
                throws ParseException {
            if (data.getF09() != null) {
                Long value = 0L;
                Long enter = 0L;
                Long exit = 0L;
                List<String> f09Splited = Arrays.asList(data.getF09().split(","));
                if (f09Splited.size() <= 1) {
                    return;
                }
                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                for (String entry : f09Splited) {
                    String temp = entry.replaceAll("[()]+", "");
                    if (temp.charAt(temp.length() - 1) == '+') {
                        enter = format.parse(temp.substring(0, temp.length() - 1)).getTime();
                        exit = 0L;
                    } else if (temp.charAt(temp.length() - 1) == '-') {
                        exit = format.parse(temp.substring(0, temp.length() - 1)).getTime();
                    }
                    if ((enter != 0) && (exit != 0)) {
                        value = value + exit - enter;
                        enter = 0L;
                        exit = 0L;
                    }
                }
                if (value > 0) {
                    Set<MapKeyModel> entrySummaryMapKeys = entrySummaryMap.keySet();

                    for (MapKeyModel mapKey : entrySummaryMapKeys) {
                        if (mapKey.getDate().equals(data.getF04()) && mapKey.getClientID().equals(data.getF01())) {

                            if (entrySummaryMap.containsKey(mapKey)) {
                                Long sumEntry = entrySummaryMap.get(mapKey) + value;
                                entrySummaryMap.put(mapKey, sumEntry);
                                System.out.println();
                            }
                            break;
                        }
                    }
                    long hours = value / (60 * 60 * 1000);
                    long minutes = value / (60 * 1000) % 60;
                    data.setF08(
                            "" + (hours < 10 ? "0" + hours : hours) + ":" + (minutes < 10 ? "0" + minutes : minutes));
                }
            }
        }

        private static void updateEntrySummaryTime(Data data, Map<MapKeyModel, Long> entrySummaryMap)
                throws ParseException {
            Set<MapKeyModel> entrySummaryMapKeys = entrySummaryMap.keySet();

            for (MapKeyModel mapKey : entrySummaryMapKeys) {
                if (mapKey.getDate().equals(data.getF04()) && mapKey.getClientID().equals(data.getF01())) {

                    if (entrySummaryMap.containsKey(mapKey)) {
                        Long sumEntry = entrySummaryMap.get(mapKey);
                        if (sumEntry > 0) {
                            long hours = sumEntry / (60 * 60 * 1000);
                            long minutes = sumEntry / (60 * 1000) % 60;
                            data.setF10("" + (hours < 10 ? "0" + hours : hours) + ":" + (minutes < 10 ? "0" + minutes
                                    : minutes));
                        }
                    }
                    break;
                }
            }
        }

        //возвращает список Data с заполненными дата-корпусами
        private static List<Data> prepareDataList(Map<String, Object> rs, List<ShortBuilding> friendlyOrgs, Date begin,
                Date end) throws SQLException {
            List<Data> resultList = new LinkedList<Data>();
            List<String> dateList = new LinkedList<String>();
            dateList.add(CalendarUtils.dateShortToString(begin));
            Calendar beginC = Calendar.getInstance();
            beginC.setTime(begin);
            Calendar endC = Calendar.getInstance();
            endC.setTime(end);
            while (beginC.compareTo(endC) == -1) {
                beginC.add(Calendar.DAY_OF_MONTH, 1);
                dateList.add(CalendarUtils.dateShortToString(beginC.getTime()));
            }
            if (dateList.size() > 1) {
                dateList.remove(dateList.size() - 1);
            }

            for (String date : dateList) {
                for (ShortBuilding building : friendlyOrgs) {
                    Data eventData = new Data();
                    eventData.setEventId(((BigInteger) rs.get("idofenterevent")).longValue());
                    eventData.setF01(((BigInteger) rs.get("idofclient")).toString());
                    eventData.setF02(rs.get("surname") + " " + rs.get("firstname") + " " + rs.get("secondname"));
                    eventData.setF03((String) rs.get("groupname"));
                    eventData.setF04(date);
                    eventData.setF05(building.getF05());
                    resultList.add(eventData);
                }
            }

            return resultList;
        }

        //возвращает список Data с заполненными дата-корпусами
        private static List<Data> prepareDataList(Client client, List<ShortBuilding> friendlyOrgs, Date begin, Date end)
                throws SQLException {
            List<Data> resultList = new LinkedList<Data>();
            List<String> dateList = new LinkedList<String>();
            dateList.add(CalendarUtils.dateShortToString(begin));
            Calendar beginC = Calendar.getInstance();
            beginC.setTime(begin);
            Calendar endC = Calendar.getInstance();
            endC.setTime(end);
            while (beginC.compareTo(endC) == -1) {
                beginC.add(Calendar.DAY_OF_MONTH, 1);
                dateList.add(CalendarUtils.dateShortToString(beginC.getTime()));
            }
            if (dateList.size() > 1) {
                dateList.remove(dateList.size() - 1);
            }

            for (String date : dateList) {
                for (ShortBuilding building : friendlyOrgs) {
                    Data eventData = new Data();
                    eventData.setEventId(0L);
                    eventData.setF01(client.getIdOfClient().toString());
                    eventData.setF02(client.getPerson().getFullName());
                    eventData.setF03(client.getClientGroup().getGroupName());
                    eventData.setF04(date);
                    eventData.setF05(building.getF05());
                    resultList.add(eventData);
                }
            }

            return resultList;
        }

        //Добавляет событие прохода к записи
        private static void updateEventData(Data data, Map<String, Object> rs) throws SQLException {
            Data newData = new Data(rs);

            if (newData.getF06() != null) {
                if (data.getF06() != null) {
                    if (CalendarUtils.timeEquals(data.getF06(), newData.getF06())) {
                        data.setF06(newData.getF06());
                        data.setF09(newData.getF09() + "," + data.getF09());
                    } else {
                        data.setF09(data.getF09() + "," + newData.getF09());
                    }
                } else {
                    data.setF06(newData.getF06());
                    if (data.getF09() != null) {
                        data.setF09(data.getF09() + "," + newData.getF09());
                    } else {
                        data.setF09(newData.getF09());
                    }
                }
            }

            if (newData.getF07() != null) {
                if (data.getF07() != null) {

                    if (!CalendarUtils.timeEquals(data.getF07(), newData.getF07())) {
                        data.setF07(newData.getF07());
                    }
                    data.setF09(data.getF09() + "," + newData.getF09());
                } else {
                    data.setF07(newData.getF07());
                    if (data.getF09() != null) {
                        data.setF09(data.getF09() + "," + newData.getF09());
                    } else {
                        data.setF09(newData.getF09());
                    }
                }

            }
        }


        //находим список корпусов
        private static List<ShortBuilding> getFriendlyOrgs(Session session, Long idOfOrg) {
            List<ShortBuilding> resultList = new LinkedList<ShortBuilding>();

            Org org = (Org) session.load(Org.class, idOfOrg);
            Set<Org> friendlyOrgs = org.getFriendlyOrg();

            for (Org organization : friendlyOrgs) {
                if (organization.isMainBuilding()) {
                    resultList.add(new ShortBuilding(organization.getIdOfOrg(), organization.getOfficialName(), "2"));
                }
            }

            for (Org organization : friendlyOrgs) {
                if (!organization.isMainBuilding()) {
                    resultList.add(new ShortBuilding(organization.getIdOfOrg(), organization.getOfficialName(), "2"));
                }
            }
            return resultList;
        }
    }


    public AutoEnterEventV2Report(Date generateTime, long generateDuration, JasperPrint print, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, print, startTime, endTime, idOfOrg);
    }

    private static final Logger logger = LoggerFactory.getLogger(AutoEnterEventV2Report.class);

    public AutoEnterEventV2Report() {
    }

    @Override
    public BasicReportForOrgJob createInstance() {
        return new AutoEnterEventV2Report();
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