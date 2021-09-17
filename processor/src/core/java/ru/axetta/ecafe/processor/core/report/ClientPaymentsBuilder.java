/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrganizationType;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.ReportPropertiesUtils;

import java.io.File;
import java.math.BigInteger;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 02.04.15
 * Time: 17:06
 */

public class ClientPaymentsBuilder extends BasicReportForAllOrgJob.Builder {

    private static final String SALES_SQL = "select " + "cf_orgs.idoforg, "
            + "substring(cf_orgs.officialname from '[^[:alnum:]]* {0,1}№ {0,1}([0-9]*)'), "
            + "cf_contragents.contragentname, " + "int8(sum(cf_orders.rsum)) as sales, "
            + "int8(sum(cf_orders.socdiscount)) as discounts, " + "cf_orgs.shortname " + "from cf_orgs "
            + "left join cf_orders on cf_orgs.idoforg=cf_orders.idoforg and "
            + "                       cf_orders.createddate between :fromCreatedDate and :toCreatedDate "
            + "left join cf_contragents on cf_orders.idofcontragent=cf_contragents.idofcontragent and cf_contragents.classid = :contragentType "
            + "where cf_orgs.idOfOrg in (:ids) and cf_orders.state = 0 "
            + "group by cf_orgs.idoforg, cf_orgs.shortname, cf_contragents.contragentname "
            + "order by cf_orgs.shortname, cf_contragents.contragentname";

    private static final String TRANSACTIONS_SQL = "select " + "cf_orgs.idoforg, "
            + "substring(cf_orgs.officialname from '[^[:alnum:]]* {0,1}№ {0,1}([0-9]*)'), "
            + "cf_contragents.contragentname, " + "int8(sum(cf_clientpayments.paysum)) as payments, "
            + "cf_orgs.shortname " + "from cf_orgs "
            + "left join cf_transactions on cf_orgs.idoforg=cf_transactions.idoforg and "
            + "cf_transactions.transactiondate between :fromCreatedDate and :toCreatedDate "
            + "join cf_clientpayments on cf_clientpayments.idoftransaction=cf_transactions.idoftransaction "
            + "left join cf_contragents on cf_orgs.defaultSupplier=cf_contragents.idofcontragent and cf_contragents.classid = :contragentType "
            + "where cf_orgs.idOfOrg in (:ids) "
            + "group by cf_orgs.idoforg, cf_orgs.shortname, cf_contragents.contragentname "
            + "order by cf_orgs.shortname";

    private static final String SALES_SQL_WITHOUT_START_DATE = "select " + "cf_orgs.idoforg, "
            + "substring(cf_orgs.officialname from '[^[:alnum:]]* {0,1}№ {0,1}([0-9]*)'), "
            + "cf_contragents.contragentname, " + "int8(sum(cf_orders.rsum)) as sales, "
            + "int8(sum(cf_orders.socdiscount)) as discounts, " + "cf_orgs.shortname " + "from cf_orgs "
            + "left join cf_orders on cf_orgs.idoforg=cf_orders.idoforg and "
            + "                       cf_orders.createddate <= :toCreatedDate "
            + "left join cf_contragents on cf_orders.idofcontragent=cf_contragents.idofcontragent and cf_contragents.classid = :contragentType "
            + "where cf_orgs.idOfOrg in (:ids) and cf_orders.state = 0 "
            + "group by cf_orgs.idoforg, cf_orgs.shortname, cf_contragents.contragentname "
            + "order by cf_orgs.shortname, cf_contragents.contragentname";

    private static final String TRANSACTIONS_SQL_WITHOUT_START_DATE = "select " + "cf_orgs.idoforg, "
            + "substring(cf_orgs.officialname from '[^[:alnum:]]* {0,1}№ {0,1}([0-9]*)'), "
            + "cf_contragents.contragentname, " + "int8(sum(cf_clientpayments.paysum)) as payments, "
            + "cf_orgs.shortname " + "from cf_orgs "
            + "left join cf_transactions on cf_orgs.idoforg=cf_transactions.idoforg and "
            + "cf_transactions.transactiondate  <= :toCreatedDate "
            + "join cf_clientpayments on cf_clientpayments.idoftransaction=cf_transactions.idoftransaction "
            + "left join cf_contragents on cf_orgs.defaultSupplier=cf_contragents.idofcontragent and cf_contragents.classid = :contragentType "
            + "where cf_orgs.idOfOrg in (:ids) "
            + "group by cf_orgs.idoforg, cf_orgs.shortname, cf_contragents.contragentname "
            + "order by cf_orgs.shortname";

    private final String templateFilename;

    private String organizationNames;

    public ClientPaymentsBuilder(String templateFilename) {
        this.templateFilename = templateFilename;
    }

    @Override
    public BasicReportJob build(Session session, Date startTime, Date endTime, Calendar calendar) throws Exception {
        if (!(new File(this.templateFilename)).exists()) {
            throw new Exception(String.format("Не найден файл шаблона '%s'", templateFilename));
        }
        String idOfOrg = StringUtils.trimToEmpty(getReportProperties().getProperty(ReportPropertiesUtils.P_ID_OF_ORG));
        String[] splitedIdOfOrg = idOfOrg.split("[,]");

        List<Long> idOfOrgList = new ArrayList<Long>();
        for (String idOfOrgs : splitedIdOfOrg) {
            idOfOrgList.add(Long.parseLong(idOfOrgs));
        }

        String organizationTypeModify = getReportProperties().getProperty("organizationTypeModify");

        OrganizationType organizationType = findOrganizationType(organizationTypeModify);

        List<Long> filteredOrgsByOrgType = idOfOrgList;

        if (organizationType != null) {
            List<Org> orgList = getOrgsList(session, filteredOrgsByOrgType);
            filteredOrgsByOrgType = filteredOrgsByOrgType(orgList, organizationType);
        } else {
            String filterOrg = getReportProperties().getProperty("organizationNames");
            organizationNames = filterOrg;
        }

        Date generateBeginTime = new Date();

        /* Параметры для передачи в jasper */
        Map<String, Object> parameterMap = new HashMap<String, Object>();
        if (startTime != null) {
            parameterMap.put("beginDate", CalendarUtils.dateToString(startTime));
        } else {
            parameterMap.put("beginDate", "Начало использования ИС ПП");
        }

        parameterMap.put("endDate", CalendarUtils.dateToString(endTime));
        if (organizationType != null) {
            parameterMap.put("orgType", organizationType.toString());
            parameterMap.put("organizationNames", organizationNames);
        } else {
            parameterMap.put("orgType", "");
            parameterMap.put("organizationNames", organizationNames);
        }

        JRDataSource dataSource = buildDataSource(session, startTime, endTime, filteredOrgsByOrgType);
        JasperPrint jasperPrint = JasperFillManager.fillReport(templateFilename, parameterMap, dataSource);

        Date generateEndTime = new Date();
        final long generateDuration = generateEndTime.getTime() - generateBeginTime.getTime();

        return new ClientPaymentsReport(generateBeginTime, generateDuration, jasperPrint, startTime, endTime);
    }

    private JRDataSource buildDataSource(Session session, Date startDate, Date endDate, List<Long> idOfOrgList) {
        List<ClientPaymentItem> items = new LinkedList<ClientPaymentItem>();

        idOfOrgList = receiveOrgList(idOfOrgList);
        if (!idOfOrgList.isEmpty()) {
            if (startDate != null) {
                parseSales(items, executeSQL(session, SALES_SQL, startDate.getTime(), endDate.getTime(), idOfOrgList));
                parseTransactions(items,
                        executeSQL(session, TRANSACTIONS_SQL, startDate.getTime(), endDate.getTime(), idOfOrgList));
            } else {
                parseSales(items, executeWithOutStartDaySQL(session, SALES_SQL_WITHOUT_START_DATE, endDate.getTime(),
                        idOfOrgList));
                parseTransactions(items,
                        executeWithOutStartDaySQL(session, TRANSACTIONS_SQL_WITHOUT_START_DATE, endDate.getTime(),
                                idOfOrgList));
            }
        }

        Long totalPayment = 0L;
        Long totalSales = 0L;
        Long totalDiff = 0L;
        Long totalDiscounts = 0L;

        for (ClientPaymentItem clientPaymentItem : items) {
            totalPayment += clientPaymentItem.getP();
            totalSales += clientPaymentItem.getS();
            totalDiff += clientPaymentItem.getDif();
            totalDiscounts += clientPaymentItem.getDis();
        }

        for (ClientPaymentItem clientPaymentItem : items) {
            clientPaymentItem.setTotalPayments(totalPayment);
            clientPaymentItem.setTotalSales(totalSales);
            clientPaymentItem.setTotalDiscounts(totalDiscounts);
            clientPaymentItem.setTotalDiff(totalDiff);
        }

        return new JRBeanCollectionDataSource(items);
    }

    private List<Org> getOrgsList(Session session, List<Long> idOfOrgList) {
        List<Org> orgListBy = new ArrayList<Org>();
        for (Long id : idOfOrgList) {
            Org org = (Org) session.load(Org.class, id);
            orgListBy.add(org);
        }
        return orgListBy;
    }

    //Фильтровка организаций по Округу выбранному
    private List<Long> filteredOrgsByOrgType(List<Org> orgListBy, OrganizationType organizationType) {

        String filteredOrgsNames = "";

        List<Long> orgList = new ArrayList<Long>();

        for (Org org : orgListBy) {
            if (org.getType().equals(organizationType)) {
                orgList.add(org.getIdOfOrg());
                filteredOrgsNames = filteredOrgsNames + org.getShortName() + "; ";
            }
        }

        this.organizationNames = filteredOrgsNames;

        return orgList;
    }

    //нахождение - типа организации
    private OrganizationType findOrganizationType(String organizationTypeModify) {
        OrganizationType[] organizationTypes = OrganizationType.values();

        for (OrganizationType orgType : organizationTypes) {
            if (orgType.name().equals(organizationTypeModify)) {
                return orgType;
            }
        }
        return null;
    }

    public static class ClientPaymentItem {

        private final long idOfOrg; // ID организации
        private final String orgName; // Наименование организации
        private final String agent; // Наименование контрагента
        private Long payments; // Сумма платежей
        private Long sales; // Сумма продаж
        private Long discounts; // Сумма льготных продаж

        private Long totalPayments;
        private Long totalSales;
        private Long totalDiff;
        private Long totalDiscounts;

        public long getIdOfOrg() {
            return idOfOrg;
        }

        public String getOrgName() {
            return orgName;
        }


        public String getAgent() {
            return agent;
        }


        public String getPayments() {
            return longToMoney(payments);
        }

        public Long getP() {
            return payments;
        }

        public Long getS() {
            return sales;
        }

        public Long getDis() {
            return discounts;
        }

        public Long getDif() {
            return (payments == null ? 0L : payments) - (sales == null ? 0L : sales);
        }

        public String getSales() {
            return longToMoney(sales);
        }


        public String getDiscounts() {
            return longToMoney(discounts);
        }


        public String getDiff() {
            return longToMoney((payments == null ? 0L : payments) - (sales == null ? 0L : sales));
        }

        public void setPayments(Long payments) {
            this.payments = payments;
        }

        public String getTotalDiscounts() {
            return longToMoney(totalDiscounts);
        }

        public void setTotalDiscounts(Long totalDiscounts) {
            this.totalDiscounts = totalDiscounts;
        }

        public String getTotalDiff() {
            return longToMoney(totalDiff);
        }

        public void setTotalDiff(Long totalDiff) {
            this.totalDiff = totalDiff;
        }

        public String getTotalSales() {
            return longToMoney(totalSales);
        }

        public void setTotalSales(Long totalSales) {
            this.totalSales = totalSales;
        }

        public String getTotalPayments() {
            return longToMoney(totalPayments);
        }

        public void setTotalPayments(Long totalPayments) {
            this.totalPayments = totalPayments;
        }

        public ClientPaymentItem(long idOfOrg, String orgName, String agent, Long payments, Long sales,
                Long discounts) {
            this.idOfOrg = idOfOrg;
            this.orgName = orgName;
            this.agent = agent;
            this.payments = payments;
            this.sales = sales;
            this.discounts = discounts;
        }
    }


    protected List<Long> receiveOrgList(List<Long> idOfOrgList) {
        Set<Long> result = new TreeSet<Long>();
        for (Long idOfOrg : idOfOrgList) {
            Org o = DAOReadonlyService.getInstance().findOrg(idOfOrg);
            if (o == null) {
                continue;
            }
            if (o.getType().ordinal() == OrganizationType.SUPPLIER.ordinal()) {
                addContragentOrgs(result, o.getDefaultSupplier());
            } else {
                result.add(idOfOrg);
            }
        }

        idOfOrgList = new ArrayList<Long>();
        idOfOrgList.addAll(result);
        return idOfOrgList;
    }

    protected void addContragentOrgs(Set<Long> idOfOrgList, Contragent contragent) {
        List<Org> orgs = DAOService.getInstance().getOrgsByDefaultSupplier(contragent);
        for (Org o : orgs) {
            idOfOrgList.add(o.getIdOfOrg());
        }
    }

    private List executeSQL(Session session, String sql, long startDate, long endDate, List<Long> idOfOrgList) {
        Query query = session.createSQLQuery(sql);
        query.setLong("fromCreatedDate", startDate);
        query.setLong("toCreatedDate", endDate);
        query.setParameter("contragentType", Contragent.TSP);
        query.setParameterList("ids", idOfOrgList);
        return query.list();
    }

    private List executeWithOutStartDaySQL(Session session, String sql, long endDate, List<Long> idOfOrgList) {
        Query query = session.createSQLQuery(sql);
        query.setLong("toCreatedDate", endDate);
        query.setParameter("contragentType", Contragent.TSP);
        query.setParameterList("ids", idOfOrgList);
        return query.list();
    }

    private void parseSales(List<ClientPaymentItem> items, List res) {
        for (Object result : res) {
            Object[] o = (Object[]) result;
            long idOfOrg = ((BigInteger) o[0]).longValue();
            //String orgNumber = (String) o[1];
            String agent = (String) o[2];
            String orgFullName = (String) o[5];
            Long sales = null;
            Long discounts = null;
            if (o[3] != null) {
                sales = ((BigInteger) o[3]).longValue();
                discounts = ((BigInteger) o[4]).longValue();
            }
            if (sales == null && discounts == null) {
                continue;
            }
            if (sales == null) {
                sales = 0L;
            }
            if (discounts == null) {
                discounts = 0L;
            }
            String orgName = orgFullName;//orgNumber == null ? orgFullName : orgNumber;
            ClientPaymentItem item = new ClientPaymentItem(idOfOrg, orgName, agent, 0L, sales, discounts);
            items.add(item);
        }
    }

    private void parseTransactions(List<ClientPaymentItem> items, List res) {
        for (Object result : res) {
            Object[] o = (Object[]) result;
            long idOfOrg = ((BigInteger) o[0]).longValue();
            String orgNumber = (String) o[1];
            String agent = (String) o[2];
            String orgFullName = (String) o[4];
            Long payments = null;
            if (o[2] != null) {
                payments = ((BigInteger) o[3]).longValue();
            }
            if (payments == null) {
                continue;
            }
            ClientPaymentItem item = lookupOrgById(items, idOfOrg);
            if (item == null) {
                item = new ClientPaymentItem(idOfOrg, orgFullName, agent, payments, 0L, 0L);
                items.add(item);
            }
            item.setPayments(payments);
        }
    }

    private ClientPaymentItem lookupOrgByName(List<ClientPaymentItem> items, String orgName) {
        if (items == null || items.size() < 1) {
            return null;
        }
        for (ClientPaymentItem i : items) {
            if (i.getOrgName().equals(orgName)) {
                return i;
            }
        }
        return null;
    }

    private ClientPaymentItem lookupOrgById(List<ClientPaymentItem> items, long idOfOrg) {
        if (items == null || items.size() < 1) {
            return null;
        }
        for (ClientPaymentItem i : items) {
            if (i.getIdOfOrg() == idOfOrg) {
                return i;
            }
        }
        return null;
    }

    public static String longToMoney(Long money) {
        return String.format("%d.%02d", money / 100, Math.abs(money % 100));
    }

    public String getOrganizationNames() {
        return organizationNames;
    }
}
