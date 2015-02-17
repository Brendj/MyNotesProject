/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;


import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;

import org.hibernate.Session;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 29.10.12
 * Time: 14:44
 * Онлайн отчеты -> Свод по услугам
 */
public class TotalServicesReport extends BasicReport {

    private final List<TotalEntry> items;


    public static class Builder {

        public TotalServicesReport build(Session session, Date startDate, Date endDate, List<Long> idOfOrgList)
                throws Exception {
            if(idOfOrgList == null || idOfOrgList.size() < 1) {
                throw new Exception("Необходимо выбрать организацию");
            }
            java.text.Format df = new SimpleDateFormat("yyyy-MM-dd");
            Date generateTime = new Date();
            Map<Long, TotalEntry> entries = new HashMap<Long, TotalEntry>();
            // Обработать лист с организациями
            String orgConditionWithCFORGS = getOrgCondition(idOfOrgList, "cf_orgs.idOfOrg = ");
            String orgConditionWithCFORDERS = getOrgCondition(idOfOrgList, "cf_orders.idOfOrg = ");
            String orgConditionIn = getOrgTuple(idOfOrgList);
            TotalServiceQueryLauncher queryLauncher = RuntimeContext.getAppContext().getBean(TotalServiceQueryLauncher.class);

            // Инициализация структуры данных, подсчет общего количества учащихся
            queryLauncher.loadOrgs(orgConditionWithCFORGS, entries);
            // Получение количества получающих льготное питание
            queryLauncher.loadValue(entries, "planBenefitClientsCount",
                                        "select cf_orgs.idoforg, count (idofclientcomplexdiscount) "
                                      + "from cf_clientscomplexdiscounts "
                                      + "left join cf_clients on cf_clients.idofclient=cf_clientscomplexdiscounts.idofclient "
                                      + "left join cf_orgs on cf_clients.idoforg=cf_orgs.idoforg "
                                      + "where cf_clients.idOfClientGroup<" + ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue() + " "
                                      + " AND " + orgConditionWithCFORGS
                                      + "group by cf_orgs.idoforg "
                                      + "order by cf_orgs.idoforg ");
            // Получение количества событий прохода через турникет за период
            queryLauncher.loadValue(entries, "currentClientsCount",
                    "select cf_enterevents.idoforg, count(distinct cf_enterevents.idofclient) " +
                            "from cf_orgs " +
                            "left join cf_clients on cf_clients.idoforg=cf_orgs.idoforg " +
                            "left join cf_enterevents on cf_enterevents.idofclient=cf_clients.idofclient and cf_enterevents.idoforg in " + orgConditionIn +
                            "where cf_orgs.state=1 "
                            + " and cf_clients.idOfClientGroup<" + ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()
                            + " AND " + orgConditionWithCFORGS +
                            " AND cf_enterevents.evtdatetime>=" + startDate.getTime() +
                            " AND cf_enterevents.evtdatetime<" + endDate.getTime() +
                            " group by cf_enterevents.idoforg");
            // Получение количества учеников получивших льготное питание
            queryLauncher.loadValue(entries, "realBenefitClientsCount",
                    "SELECT cf_orders.idoforg, COUNT(DISTINCT cf_orders.idofclient) "
                            + " FROM cf_orders "
                            + " LEFT JOIN cf_orderdetails ON cf_orders.idoforder = cf_orderdetails.idoforder and cf_orders.idoforg = cf_orderdetails.idoforg "
                            + " LEFT JOIN cf_clients ON cf_clients.idofclient = cf_orders.idofclient "
                            + " WHERE "
                            + "     cf_orderdetails.menutype between " + OrderDetail.TYPE_COMPLEX_MIN + " and " + OrderDetail.TYPE_COMPLEX_MAX
                            + "     AND cf_orders.state = 0 "
                            + "     AND cf_orders.ordertype in (4, 6) "
                            + "     AND cf_clients.idOfClientGroup < " + ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()
                            + "     AND cf_orders.createddate between " + startDate.getTime() + " and " + endDate.getTime()
                            + "     AND " + orgConditionWithCFORDERS
                            + "group by cf_orders.idoforg");
            // Получение количества получивших платное комплексное питание
            queryLauncher.loadValue(entries, "realPayedClientsCount",
                    "SELECT cf_orders.idoforg, COUNT(DISTINCT cf_orders.idofclient) "
                            + " FROM cf_orders  "
                            + " LEFT JOIN cf_orderdetails ON cf_orders.idoforder = cf_orderdetails.idoforder and cf_orders.idoforg = cf_orderdetails.idoforg "
                            + " LEFT JOIN cf_clients ON cf_clients.idofclient = cf_orders.idofclient "
                            + " WHERE "
                            + "     cf_orderdetails.menutype between " + OrderDetail.TYPE_COMPLEX_MIN + " and " + OrderDetail.TYPE_COMPLEX_MAX
                            + "     AND cf_orders.state = 0 "
                            + "     AND cf_orders.ordertype in (1, 3, 7) "
                            + "     AND cf_clients.idOfClientGroup < " + ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()
                            + "     AND cf_orders.createddate between " + startDate.getTime() + " and " + endDate.getTime()
                            + "     AND " + orgConditionWithCFORDERS
                            + "group by cf_orders.idoforg");
            // Получение количества клиентов получивших платное питание в буфете
            queryLauncher.loadValue(entries, "realSnackPayedClientsCount",
                    "SELECT cf_orders.idoforg, COUNT(DISTINCT cf_orders.idofclient) "
                            + " FROM cf_orders "
                            + " LEFT JOIN cf_orderdetails ON cf_orders.idoforder = cf_orderdetails.idoforder and cf_orders.idoforg = cf_orderdetails.idoforg "
                            + " LEFT JOIN cf_clients ON cf_clients.idofclient = cf_orders.idofclient "
                            + " WHERE "
                            + "     cf_orderdetails.menutype = " + OrderDetail.TYPE_DISH_ITEM
                            + "     AND cf_orders.state = 0 "
                            + "     AND cf_orders.ordertype in (1) "
                            + "     AND cf_clients.idOfClientGroup < " + ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()
                            + "     AND cf_orders.createddate between " + startDate.getTime() + " and " + endDate.getTime()
                            + "     AND " + orgConditionWithCFORDERS
                            + "group by cf_orders.idoforg");
            // Получение количества клиентов получивших питание
            queryLauncher.loadValue(entries, "uniqueClientsCount",
                    "SELECT cf_orders.idoforg, COUNT(DISTINCT cf_orders.idofclient) "
                            + " FROM cf_orders "
                            + " LEFT JOIN cf_orderdetails ON cf_orders.idoforder = cf_orderdetails.idoforder and cf_orders.idoforg = cf_orderdetails.idoforg "
                            + " LEFT JOIN cf_clients ON cf_clients.idofclient = cf_orders.idofclient "
                            + " WHERE "
                            + "     (cf_orderdetails.menutype = " + OrderDetail.TYPE_DISH_ITEM
                            + "     OR cf_orderdetails.menutype between " + OrderDetail.TYPE_COMPLEX_MIN + " and " + OrderDetail.TYPE_COMPLEX_MAX + ")"
                            + "     AND cf_orders.state = 0 "
                            + "     AND cf_orders.ordertype in (1, 3, 4, 6, 7) "
                            + "     AND cf_clients.idOfClientGroup < " + ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()
                            + "     AND cf_orders.createddate between " + startDate.getTime() + " and " + endDate.getTime()
                            + "     AND " + orgConditionWithCFORDERS
                            + "group by cf_orders.idoforg");

            List<TotalEntry> result = new LinkedList(entries.values());
            Long totalClientsCount = 0L;
            Long planBenefitClientsCount = 0L;
            Long currentClientsCount = 0L;
            Long realBenefitClientsCount = 0L;
            Long realPayedClientsCount = 0L;
            Long realSnackPayedClientsCount = 0L;
            Long uniqueClientsCount = 0L;
            for (TotalEntry entry : result) {
                totalClientsCount += Long.parseLong(entry.getData().get("totalClientsCount").toString());
                planBenefitClientsCount += Long.parseLong(entry.getData().get("planBenefitClientsCount").toString());
                currentClientsCount += Long.parseLong(entry.getData().get("currentClientsCount").toString());
                realBenefitClientsCount += Long.parseLong(entry.getData().get("realBenefitClientsCount").toString());
                realPayedClientsCount += Long.parseLong(entry.getData().get("realPayedClientsCount").toString());
                realSnackPayedClientsCount += Long.parseLong(entry.getData().get("realSnackPayedClientsCount").toString());
                uniqueClientsCount += Long.parseLong(entry.getData().get("uniqueClientsCount").toString());
            }
            TotalEntry totalEntry = new TotalEntry("Итого");
            totalEntry.getData().put("totalClientsCount", totalClientsCount);
            totalEntry.getData().put("planBenefitClientsCount", planBenefitClientsCount);
            totalEntry.getData().put("currentClientsCount", currentClientsCount);
            totalEntry.getData().put("realBenefitClientsCount", realBenefitClientsCount);
            totalEntry.getData().put("realPayedClientsCount", realPayedClientsCount);
            totalEntry.getData().put("realSnackPayedClientsCount", realSnackPayedClientsCount);
            totalEntry.getData().put("uniqueClientsCount", uniqueClientsCount);
            result.add(totalEntry);

            calculatePercents(result, "planBenefitClientsCount");
            calculatePercents(result, "currentClientsCount");
            calculatePercents(result, "realBenefitClientsCount");
            calculatePercents(result, "realPayedClientsCount");
            calculatePercents(result, "realSnackPayedClientsCount");
            calculatePercents(result, "uniqueClientsCount");


            return new TotalServicesReport(generateTime, new Date().getTime() - generateTime.getTime(), result);
        }

        private String getOrgTuple(List<Long> idOfOrgList) {
            String orgConditionIn = "";
            if (!idOfOrgList.isEmpty()) {
                for (Long idOfOrg : idOfOrgList) {
                    orgConditionIn = orgConditionIn.concat(idOfOrg + ", ");
                }
                orgConditionIn = " (" + orgConditionIn.substring(0, orgConditionIn.length() - 2) + ") ";
            }
            return orgConditionIn;
        }

        private String getOrgCondition(List<Long> idOfOrgList, String prefix) {
            String orgConditionWithCFORGS = "";
            if (!idOfOrgList.isEmpty()) {
                for (Long idOfOrg : idOfOrgList) {
                    orgConditionWithCFORGS = orgConditionWithCFORGS.concat(prefix + idOfOrg + " or ");
                }
                orgConditionWithCFORGS = " (" + orgConditionWithCFORGS.substring(0, orgConditionWithCFORGS.length() - 4) + ") ";
            }
            return orgConditionWithCFORGS;
        }


        private void calculatePercents(List<TotalEntry> entries, String key) {
            calculatePercents(entries, key, "totalClientsCount");
        }


        private void calculatePercents(List<TotalEntry> entries, String key, String base) {
            for (TotalEntry e : entries) {
                Object vO = e.getData().get(key);
                Object bO = e.getData().get(base);
                if (vO != null && bO != null) {
                    try {
                        Double v = Double.parseDouble(vO.toString());
                        Double b = Double.parseDouble(bO.toString());
                        e.put("per_" + key, new BigDecimal(b == 0 ? 0 : v / b * 100).
                                setScale(2, BigDecimal.ROUND_HALF_DOWN).toString() + "%");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }
    }


    public TotalServicesReport() {
        super();
        this.items = Collections.emptyList();
    }


    public TotalServicesReport(Date generateTime, long generateDuration, List<TotalEntry> items) {
        super(generateTime, generateDuration);
        this.items = items;
    }


    public static class TotalEntry {

        private String officialName;                    // Название организации
        //private String totalClientsCount;             // Общее количество учащихся
        //private String planBenefitClientsCount;       // Число получающих льготное питание
        //private String perPlanBenefitClientsCount;
        //private String currentClientsCount;           // Зафиксирован проход в течении периода
        //private String perCurrentClientsCount;
        //private String realBenefitClientsCount;       // Число получивших льготное питание
        //private String perRealBenefitClientsCount;
        //private String realPayedClientsCount;         // Число получивших комплексное питание
        //private String perRealPayedClientsCount;
        //private String realPayedSnackClientsCount;    // Число получивших платное питание в буфете
        //private String perRealPayedSnackClientsCount;
        //private String uniqueClientsCount;            // Число получивших питание (льготное + платное)
        //private String perUniqueClientsCount;
        private Map<String, Object> data;


        public Map<String, Object> getData() {
            return data;
        }

        public String getOfficialName() {
            return officialName;
        }

        public void put(String k, Object v) {
            data.put(k, v);
        }

        public TotalEntry(String officialName) {
            this.officialName = officialName;
            data = new HashMap<String, Object>();
        }
    }

    public List<TotalEntry> getItems() {
        return items;
    }
}