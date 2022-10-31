/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.util.ObjectUtils;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.dao.clients.ClientDao;
import ru.axetta.ecafe.processor.core.report.model.autoenterevent.Data;
import ru.axetta.ecafe.processor.core.report.model.autoenterevent.MapKeyModel;
import ru.axetta.ecafe.processor.core.report.model.autoenterevent.ShortBuilding;
import ru.axetta.ecafe.processor.core.report.model.autoenterevent.StClass;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by anvarov on 06.04.18.
 */
public class DetailedEnterEventReport extends BasicReportForMainBuildingOrgJob {

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
    public static final String[] TEMPLATE_FILE_NAMES = {"DetailedEnterEventReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = true;
    public static final int[] PARAM_HINTS = new int[]{-46, -47, -48};
    final public static String P_ID_OF_CLIENTS = "idOfClients";
    final public static String P_ALL_FRIENDLY_ORGS = "friendsOrg";

    private final static Logger logger = LoggerFactory.getLogger(DetailedEnterEventReport.class);


    public DetailedEnterEventReport(Date generateTime, long generateDuration, JasperPrint jasperPrint, Date startTime,
            Date endTime, Long idOfOrg) {
        super(generateTime, generateDuration, jasperPrint, startTime, endTime, idOfOrg);
    }

    public DetailedEnterEventReport() {

    }

    public class AutoReportBuildJob extends BasicReportJob.AutoReportBuildJob {
    }

    @Override
    public BasicReportForOrgJob createInstance() {
        return new DetailedEnterEventReport();
    }

    @Override
    public Builder createBuilder(String templateFilename) {
        return new Builder(templateFilename);
    }

    @Override
    public Logger getLogger() {
        return logger;
    }

    public static class Builder extends BasicReportForAllOrgJob.Builder {

        private final String templateFilename;
        private List<String> clientGroupNames = new ArrayList<>();

        public List<String> getClientGroupNames() {
            return clientGroupNames;
        }

        public void setClientGroupNames(List<String> clientGroupNames) {
            this.clientGroupNames = clientGroupNames;
        }
        public Builder(String templateFilename) {
            this.templateFilename = templateFilename;
        }

        @Override
        public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {

            Date generateTime = new Date();
            Map<String, Object> parameterMap = new HashMap<String, Object>();
            startTime = CalendarUtils.roundToBeginOfDay(startTime);

            String idOfOrgString = StringUtils
                    .trimToEmpty(reportProperties.getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
            Long idOfOrg = Long.parseLong(idOfOrgString);

            Org orgLoad = (Org) session.load(Org.class, idOfOrg);

            Set<Org> orgs = orgLoad.getFriendlyOrg();

            for (Org orgM : orgs) {
                if (orgM.isMainBuilding()) {
                    parameterMap.put("shortNameInfoService", orgM.getShortNameInfoService());
                    break;
                } else {
                    parameterMap.put("shortNameInfoService", orgLoad.getShortNameInfoService());
                }
            }

            parameterMap.put("beginDate", CalendarUtils.dateShortToString(startTime));
            parameterMap.put("endDate", CalendarUtils.dateShortToString(endTime));


            endTime = CalendarUtils.endOfDay(endTime);
            calendar.setTime(startTime);
            JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap,
                    createDataSource(session, orgLoad, startTime, endTime, (Calendar) calendar.clone(), parameterMap));
            Date generateEndTime = new Date();
            return new DetailedEnterEventReport(generateTime, generateEndTime.getTime() - generateTime.getTime(),
                    jasperPrint, startTime, endTime, orgLoad.getIdOfOrg());
        }

        private JRDataSource createDataSource(Session session, Org org, Date startTime, Date endTime, Calendar calendar,
                Map<String, Object> parameterMap) throws Exception {
            startTime = CalendarUtils.truncateToDayOfMonth(startTime);

            //Список организаций
            List<ShortBuilding> friendlyOrgs = getFriendlyOrgs(session, org.getIdOfOrg());

            Set<Long> ids = new HashSet<Long>();
            String friendlyOrgsIds = "" + org.getIdOfOrg();

            String allFriendOrgsString = StringUtils.trimToEmpty(reportProperties.getProperty(P_ALL_FRIENDLY_ORGS));
            Boolean allFriendlyOrgs = Boolean.parseBoolean(allFriendOrgsString);

            if (allFriendlyOrgs) {
                ids.add(org.getIdOfOrg());
                for (ShortBuilding building : friendlyOrgs) {
                    friendlyOrgsIds += "," + building.getId();
                    ids.add(building.getId());
                }
            } else {
                ids.add(org.getIdOfOrg());
                friendlyOrgs = getFriendlyOrg(session, org);
            }


            String groupNameWhere = "";

            if (!ObjectUtils.isEmpty(clientGroupNames)) {
                int i = 0;
                String groupNameQuery = "";
                for (String group : clientGroupNames) {
                    groupNameQuery = groupNameQuery + "'" + group + "'";
                    if (i < clientGroupNames.size() - 1) {
                        groupNameQuery = groupNameQuery + ", ";
                    }
                    i++;
                }
                groupNameWhere = " AND cg.groupname in (" + groupNameQuery + ")";
            }

            //ClientDao clientDao = RuntimeContext.getAppContext().getBean(ClientDao.class);
            //
            //List<Client> allByOrg = null;

            //Фильтр по клиентам
            String idOfClientsString = StringUtils.trimToEmpty(reportProperties.getProperty(P_ID_OF_CLIENTS));
            List<String> stringClientsIdList = Arrays.asList(StringUtils.split(idOfClientsString, ','));
            
            String clientIdWhere = "";
            if (!stringClientsIdList.isEmpty()) {
                int i = 0;
                String clientIdQuery = "";
                for (String client : stringClientsIdList) {
                    clientIdQuery = clientIdQuery + "'" + client + "'";
                    if (i < stringClientsIdList.size() - 1) {
                        clientIdQuery = clientIdQuery + ", ";
                    }
                    i++;
                }
                clientIdWhere = " AND cs.idofclient in (" + clientIdQuery + ") ";
            }

            List<Data> currentClassList;
            Map<String, StClass> stClassMap = new HashMap<String, StClass>();

            List<Long> clientIdList = new LinkedList<Long>();

            //данные для отчета
            Query query = session.createSQLQuery(
                    "SELECT  ee.idofenterevent, ee.idoforg, ee.passdirection, ee.eventcode, ee.idofclient,ee.evtdatetime, "
                            + "    pn.firstname, pn.surname, pn.secondname, cg.groupname, os.shortaddress "
                            + "    FROM cf_enterevents ee "
                            + "    LEFT JOIN cf_clients cs  ON ee.idofclient = cs.idofclient "
                            + "    LEFT JOIN cf_persons pn ON pn.idofperson = cs.idofperson "
                            + "    LEFT JOIN cf_clientgroups cg ON cg.idofclientgroup = ee.idofclientgroup "
                            + "    AND cs.idoforg = cg.idoforg "
                            + "    LEFT JOIN  cf_orgs os ON ee.idoforg = os.idoforg WHERE ee.idoforg IN ("
                            + friendlyOrgsIds + ") "
                            //+ " AND cs.idoforg IN (" + friendlyOrgsIds + ") "
                            + " AND ee.evtdatetime BETWEEN " + startTime.getTime() + " AND " + endTime.getTime()
                            + " AND ee.idofclient IS NOT null AND ee.PassDirection in (0, 1, 6, 7) "
                            + groupNameWhere + clientIdWhere
                            + "     ORDER BY os.officialname, cg.groupname, ee.idofclient,ee.evtdatetime");

            query.setResultTransformer(Criteria.ALIAS_TO_ENTITY_MAP);
            List rList = query.list();

            //парсим данные
                for (Object o : rList) {
                    Map<String, Object> row = (Map<String, Object>) o;
                    Object groupNamed = row.get("groupname");
                    if (groupNamed == null)
                        groupNamed = "Группа не существует";
                    if (!stClassMap.containsKey(groupNamed)) {
                        stClassMap.put((String) groupNamed,
                                new StClass((String) groupNamed, friendlyOrgs, new LinkedList<Data>()));
                    }
                    currentClassList = stClassMap.get((String) groupNamed).getDataList();

                    if (!clientIdList.contains(((BigInteger) row.get("idofclient")).longValue())) {
                        currentClassList.addAll(prepareDataList(row, friendlyOrgs, startTime, endTime));
                        clientIdList.add(((BigInteger) row.get("idofclient")).longValue());
                    }
                    for (Data event : currentClassList) {
                        //Добавлена проверка на null
                        if (event.getF01() != null && event.getF03() != null && event.getF04() != null
                              && event.getF05() != null ) {
                            if ((event.getF01().equals(((BigInteger) row.get("idofclient")).toString())) && (event.getF03().equals((String) groupNamed)) && (event.getF04().equals(CalendarUtils
                                    .dateShortToString(new Date(((BigInteger) row.get("evtdatetime")).longValue())))) && (event.getF05().equals((String) row.get("shortaddress")))) {
                                updateEventData(event, row);
                            }
                        }
                    }
                }

            //Удаление групп без клиентов и клиентов без времени входа и выхода
            Integer counter;
            for(Iterator<Map.Entry<String, StClass>> it = stClassMap.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, StClass> entry = it.next();
                StClass value = (StClass) entry.getValue();

                if(value.getDataList().isEmpty()) {
                    it.remove();
                }
                else {
                    for (ListIterator<Data> i = value.getDataList().listIterator(); i.hasNext(); ) {
                        Data el = i.next();
                        if ((el.getF06() == null || el.getF06().equals("")) && (el.getF07() == null || el.getF07().equals(""))) {
                            i.remove();
                        }
                    }
                    counter = 1;
                    String cur = "";
                    for (Data i : value.getDataList()) {
                        if (!cur.equals(i.getF01()))
                            counter = 1;
                        cur = i.getF01();
                        i.setF11(counter);
                        counter++;
                    }
                    if(value.getDataList().isEmpty()) {
                        it.remove();
                    }
                }
            }

            Map<MapKeyModel, Long> usersEntrySummaryMap = new HashMap<MapKeyModel, Long>();
            //заполняем время внутри

            List<MapKeyModel> mapKeyModelList = new ArrayList<MapKeyModel>();

            List<StClass> stClassList = new LinkedList<StClass>(stClassMap.values());


            for (StClass stClass : stClassList) {
                if (stClass.getDataList() == null || stClass.getDataList().size() == 0) {
                    logger.error("Data for DetailedEnterEventReport not found. IdOfOrg = " + org.getIdOfOrg());
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

        //находим список корпусов
        private static List<ShortBuilding> getFriendlyOrgs(Session session, Long idOfOrg) {
            List<ShortBuilding> resultList = new LinkedList<ShortBuilding>();

            Org org = (Org) session.load(Org.class, idOfOrg);
            Set<Org> friendlyOrgs = org.getFriendlyOrg();

            for (Org organization : friendlyOrgs) {
                if (organization.isMainBuilding()) {
                    resultList.add(new ShortBuilding(organization.getIdOfOrg(), organization.getShortAddress(), "2"));
                }
            }

            for (Org organization : friendlyOrgs) {
                if (!organization.isMainBuilding()) {
                    resultList.add(new ShortBuilding(organization.getIdOfOrg(), organization.getShortAddress(), "2"));
                }
            }
            return resultList;
        }

        //один корпус
        private static List<ShortBuilding> getFriendlyOrg(Session session, Org org) {
            List<ShortBuilding> resultList = new LinkedList<ShortBuilding>();

            if (org.isMainBuilding()) {
                resultList.add(new ShortBuilding(org.getIdOfOrg(), org.getShortAddress(), "2"));
            }

            if (!org.isMainBuilding()) {
                resultList.add(new ShortBuilding(org.getIdOfOrg(), org.getShortAddress(), "2"));
            }

            return resultList;
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

        public boolean uniqueMapKeyModel(List<MapKeyModel> mapKeyModels, MapKeyModel mapKeyModel) {
            for (MapKeyModel model : mapKeyModels) {
                if (model.getDate().equals(mapKeyModel.getDate()) && model.getClientID()
                        .equals(mapKeyModel.getClientID())) {
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
    }
}
