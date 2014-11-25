/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;


import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientGroup;

import org.hibernate.Session;

import java.math.BigDecimal;
import java.math.BigInteger;
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
            String orgCondition = "";
            if (!idOfOrgList.isEmpty()) {
                for (Long idOfOrg : idOfOrgList) {
                    orgCondition = orgCondition.concat("cf_orgs.idOfOrg = " + idOfOrg + " or ");
                }
                orgCondition = " (" + orgCondition.substring(0, orgCondition.length() - 4) + ") ";
            }
            TotalServiceQueryLauncher queryLauncher = RuntimeContext.getAppContext().getBean(TotalServiceQueryLauncher.class);

            queryLauncher.loadOrgs(orgCondition, entries);
            queryLauncher.loadValue(entries, "planBenefitClientsCount",
                                        "select cf_orgs.idoforg, count (idofclientcomplexdiscount) "
                                      + "from cf_clientscomplexdiscounts "
                                      + "left join cf_clients on cf_clients.idofclient=cf_clientscomplexdiscounts.idofclient "
                                      + "left join cf_orgs on cf_clients.idoforg=cf_orgs.idoforg "
                                      + "where cf_clients.idOfClientGroup<" + ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue() + " "
                                      + " AND " + orgCondition
                                      + "group by cf_orgs.idoforg "
                                      + "order by cf_orgs.idoforg ");
            queryLauncher.loadValue(entries, "currentClientsCount",
                    "select cf_enterevents.idoforg, count(distinct cf_enterevents.idofclient) " +
                            "from cf_orgs " +
                            "left join cf_clients on cf_clients.idoforg=cf_orgs.idoforg " +
                            "left join cf_enterevents on cf_enterevents.idofclient=cf_clients.idofclient " +
                            "where cf_orgs.state=1 "
                            //+ " and cf_clients.idOfClientGroup<" + ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()
                            + " AND " + orgCondition +
                            " AND cf_enterevents.evtdatetime>=" + startDate.getTime() +
                            " AND cf_enterevents.evtdatetime<" + endDate.getTime() +
                            " group by cf_enterevents.idoforg");
            queryLauncher.loadValue(entries, "realBenefitClientsCount",
                    "select cf_orgs.idoforg, count(distinct cf_orders.idofclient) " +
                            "from cf_orgs " +
                            "left join cf_orders on cf_orders.idoforg = cf_orgs.idoforg " +
                            "left join cf_clients on cf_clients.idofclient = cf_orders.idofclient " +
                            "where cf_orgs.state=1 and cf_orders.socdiscount<>0 and cf_orders.state=0 "
                            //+ "and cf_clients.idOfClientGroup<" + ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()
                            + " AND " + orgCondition + " and "
                            + "cf_orders.createddate>=" + startDate.getTime() + " AND cf_orders.createddate<" + endDate.getTime() +
                            "group by cf_orgs.idoforg");
            queryLauncher.loadValue(entries, "realPayedClientsCount",
                    "select cf_orgs.idoforg, count(distinct cf_orders.idofclient) " +
                            "from cf_orgs " +
                            "left join cf_orders on cf_orders.idoforg = cf_orgs.idoforg " +
                            "left join cf_clients on cf_clients.idofclient = cf_orders.idofclient " +
                            "where cf_orgs.state=1 and cf_orders.socdiscount<>0  and cf_orders.state=0 "
                            //+ "and cf_clients.idOfClientGroup<" + ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()
                            + " AND " + orgCondition + " and "
                            + "cf_orders.createddate>=" + startDate.getTime() + " AND cf_orders.createddate<" + endDate.getTime() +
                            "group by cf_orgs.idoforg");
            queryLauncher.loadValue(entries, "uniqueClientsCount",
                    "select cf_orgs.idoforg, count(distinct cf_orders.idofclient) " +
                            "from cf_orgs " +
                            "left join cf_orders on cf_orders.idoforg = cf_orgs.idoforg " +
                            "left join cf_clients on cf_clients.idofclient = cf_orders.idofclient " +
                            "where cf_orgs.state=1  and cf_orders.state=0 "
                            //+ " and cf_clients.idOfClientGroup<" + ClientGroup.Predefined.CLIENT_EMPLOYEES.getValue()
                            + " AND " + orgCondition + " and "
                            + "cf_orders.createddate>=" + startDate.getTime() + " AND cf_orders.createddate<" + endDate.getTime() +
                            "group by cf_orgs.idoforg");

            List<TotalEntry> result = new LinkedList(entries.values());
            calculatePercents(result, "planBenefitClientsCount");
            calculatePercents(result, "currentClientsCount");
            calculatePercents(result, "realBenefitClientsCount", "planBenefitClientsCount");
            calculatePercents(result, "realPayedClientsCount");
            calculatePercents(result, "uniqueClientsCount");


            return new TotalServicesReport(generateTime, new Date().getTime() - generateTime.getTime(), result);
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
                        double v = ((BigInteger) vO).doubleValue();
                        double b = ((BigInteger) bO).doubleValue();
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

        private String officialName;          // Название организации
        private int totalClientsCount;        // Общее количество клиентов
        private int planBenefitClientsCount;  // Число получающих льготное питание
        private int currentClientsCount;      // Находящиеся в ОУ в текущий момент
        private int realBenefitClientsCount;  // Число реально получившие льготное питание
        private int realPayedClientsCount;    // Получившие платное питание
        private int uniqueClientsCount;       // Уникальные записи об обучающихся
        private Map<String, Object> data;


        public Map<String, Object> getData() {
            return data;
        }

        public void setTotalClientsCount(int totalClientsCount) {
            this.totalClientsCount = totalClientsCount;
        }

        public void setPlanBenefitClientsCount(int planBenefitClientsCount) {
            this.planBenefitClientsCount = planBenefitClientsCount;
        }

        public void setCurrentClientsCount(int currentClientsCount) {
            this.currentClientsCount = currentClientsCount;
        }

        public void setRealBenefitClientsCount(int realBenefitClientsCount) {
            this.realBenefitClientsCount = realBenefitClientsCount;
        }

        public void setRealPayedClientsCount(int realPayedClientsCount) {
            this.realPayedClientsCount = realPayedClientsCount;
        }

        public void setUniqueClientsCount(int uniqueClientsCount) {
            this.uniqueClientsCount = uniqueClientsCount;
        }

        public String getOfficialName() {
            return officialName;
        }

        public int getTotalClientsCount() {
            return totalClientsCount;
        }

        public int getPlanBenefitClientsCount() {
            return planBenefitClientsCount;
        }

        public int getCurrentClientsCount() {
            return currentClientsCount;
        }

        public int getRealBenefitClientsCount() {
            return realBenefitClientsCount;
        }

        public int getRealPayedClientsCount() {
            return realPayedClientsCount;
        }

        public int getUniqueClientsCount() {
            return uniqueClientsCount;
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