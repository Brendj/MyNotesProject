/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.OrderDetail;

import org.hibernate.Query;
import org.hibernate.Session;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 11.04.13
 * Time: 12:08
 * To change this template use File | Settings | File Templates.
 */
public class ClientPaymentsReport extends BasicReport {



    private final List<ClientPaymentItem> items;

    public static class Builder {

        public ClientPaymentsReport build(Session session, Date startDate, Date endDate, List<Long> idOfOrgList)
                throws Exception {
            Date generateTime = new Date();
            List<ClientPaymentItem> items = new LinkedList<ClientPaymentItem>();
            if (!idOfOrgList.isEmpty()) {
                // Обработать лист с организациями
                String orgCondition = " cf_orgs.idOfOrg in (";
                for (Long idOfOrg : idOfOrgList) {
                    if (!orgCondition.endsWith("(")) {
                        orgCondition = orgCondition.concat(", ");
                    }
                    orgCondition = orgCondition.concat("" +idOfOrg);
                }
                orgCondition = orgCondition + ") ";

                String preparedQuery =
                          "select substring(cf_orgs.officialname from '[^[:alnum:]]* {0,1}№ {0,1}([0-9]*)'), cf_contragents.contragentname, "
                        + "int8(sum(cf_transactions.transactionsum)) as payments, int8(sum(cf_orders.rsum)) as sales, int8(sum(cf_orders.socdiscount)) as discounts "
                        + "from cf_orgs "
                        + "left join cf_clients on cf_orgs.idoforg=cf_clients.idoforg "
                        + "left join cf_orders on cf_orgs.idoforg=cf_orders.idoforg and "
                        + "                       cf_orders.createddate between :fromCreatedDate and :toCreatedDate "
                        + "left join cf_contragents on cf_orders.idofcontragent=cf_contragents.idofcontragent and cf_contragents.classid=:contragentsType "
                        + "left join cf_transactions on cf_clients.idofclient=cf_transactions.idofclient and "
                        + "                       cf_transactions.transactiondate between :fromCreatedDate and :toCreatedDate "
                        + "where " + orgCondition
                        + "group by cf_orgs.officialname, cf_contragents.contragentname "
                        + "order by cf_orgs.officialname, cf_contragents.contragentname ";
                List resultList = null;
                Query query = session.createSQLQuery(preparedQuery);
                long startDateLong = startDate.getTime();
                long endDateLong = endDate.getTime();
                query.setLong("fromCreatedDate", startDateLong);
                query.setLong("toCreatedDate", endDateLong);
                query.setInteger("contragentsType", Contragent.TSP);

                resultList = query.list();

                for (Object result : resultList) {
                    Object[] pay = (Object[]) result;
                    String orgName = (String) pay[0];
                    String agent = (String) pay[1];
                    Long payments = ((BigInteger) pay[2]).longValue();
                    Long sales = ((BigInteger) pay[3]).longValue();
                    Long discounts = ((BigInteger) pay[4]).longValue();
                    ClientPaymentItem item = new ClientPaymentItem(orgName, agent, payments, sales, discounts);
                    items.add(item);
                }
            }
            return new ClientPaymentsReport(generateTime, new Date().getTime() - generateTime.getTime(), items);
        }

    }

    public ClientPaymentsReport() {
        super();
        this.items = Collections.emptyList();
    }

    public ClientPaymentsReport(Date generateTime, long generateDuration, List<ClientPaymentItem> items) {
        super(generateTime, generateDuration);
        this.items = items;
    }

    public static class ClientPaymentItem {

        private final String orgName; // Наименование организации
        private final String agent; // Наименование контрагента
        private final String payments; // Сумма платежей
        private final String sales; // Сумма продаж
        private final String discounts; // Сумма льготных продаж
        private final String diff; // Разница (Сумма продаж - Сумма льготных продаж)


        public String getOrgName () {
            return orgName;
        }


        public String getAgent () {
            return agent;
        }


        public String getPayments () {
            return payments;
        }


        public String getSales() {
            return sales;
        }


        public String getDiscounts() {
            return discounts;
        }


        public String getDiff() {
            return diff;
        }



        public ClientPaymentItem(String orgName, String agent, Long payments, Long sales, Long discounts) {
            this.orgName = orgName;
            this.agent = agent;
            this.payments = longToMoney(payments);
            this.sales = longToMoney(sales);
            this.discounts = longToMoney(discounts);
            this.diff = longToMoney((payments == null ? 0L : payments) - (sales == null ? 0L : sales));
        }
    }

    public List<ClientPaymentItem> getClientPaymentItems() {
        return items;
    }

}
