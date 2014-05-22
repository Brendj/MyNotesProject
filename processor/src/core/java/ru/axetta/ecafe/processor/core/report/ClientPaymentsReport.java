/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.Contragent;

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


    private static final String SALES_SQL =
                        "select "
                      + "cf_orgs.idoforg, "
                      + "substring(cf_orgs.officialname from '[^[:alnum:]]* {0,1}№ {0,1}([0-9]*)'), "
                      + "cf_contragents.contragentname, "
                      + "int8(sum(cf_orders.rsum)) as sales, "
                      + "int8(sum(cf_orders.socdiscount)) as discounts, "
                      + "cf_orgs.shortname "
                      + "from cf_orgs "
                      + "left join cf_orders on cf_orgs.idoforg=cf_orders.idoforg and "
                      + "                       cf_orders.createddate between :fromCreatedDate and :toCreatedDate "
                      + "left join cf_contragents on cf_orders.idofcontragent=cf_contragents.idofcontragent and cf_contragents.classid = :contragentType "
                      + "where cf_orgs.idOfOrg in (:ids) "
                      + "group by cf_orgs.idoforg, cf_orgs.shortname, cf_contragents.contragentname "
                      + "order by cf_orgs.shortname, cf_contragents.contragentname";
    private static final String TRANSACTIONS_SQL =
                        "select "
                      + "cf_orgs.idoforg, "
                      + "substring(cf_orgs.officialname from '[^[:alnum:]]* {0,1}№ {0,1}([0-9]*)'), "
                      + "cf_contragents.contragentname, "
                      + "int8(sum(cf_clientpayments.paysum)) as payments, "
                      + "cf_orgs.shortname "
                      + "from cf_orgs "
                      + "left join cf_clients on cf_orgs.idoforg=cf_clients.idoforg "
                      + "left join cf_transactions on cf_clients.idofclient=cf_transactions.idofclient and "
                      + "                             cf_transactions.transactiondate between :fromCreatedDate and :toCreatedDate "
                      + "join cf_clientpayments on cf_clientpayments.idoftransaction=cf_transactions.idoftransaction "
                      + "left join cf_contragents on cf_orgs.defaultSupplier=cf_contragents.idofcontragent and cf_contragents.classid = :contragentType "
                      + "where cf_orgs.idOfOrg in (:ids) "
                      + "group by cf_orgs.idoforg, cf_orgs.shortname, cf_contragents.contragentname "
                      + "order by cf_orgs.shortname";

    private final List<ClientPaymentItem> items;

    public static class Builder {

        public ClientPaymentsReport build(Session session, Date startDate, Date endDate, List<Long> idOfOrgList)
                throws Exception {
            Date generateTime = new Date();
            List<ClientPaymentItem> items = new LinkedList<ClientPaymentItem>();
            if (!idOfOrgList.isEmpty()) {
                parseSales(items, executeSQL(session, SALES_SQL, startDate.getTime(), endDate.getTime(), idOfOrgList));
                parseTransactions(items,
                        executeSQL(session, TRANSACTIONS_SQL, startDate.getTime(), endDate.getTime(), idOfOrgList));
            }
            return new ClientPaymentsReport(generateTime, new Date().getTime() - generateTime.getTime(), items);
        }

        private List executeSQL(Session session, String sql, long startDate, long endDate, List<Long> idOfOrgList) {
            Query query = session.createSQLQuery(sql);
            query.setLong("fromCreatedDate", startDate);
            query.setLong("toCreatedDate", endDate);
            query.setParameter("contragentType", Contragent.TSP);
            query.setParameterList("ids", idOfOrgList);
            return query.list();
        }

        private void parseSales (List<ClientPaymentItem> items, List res) {
            for (Object result : res) {
                Object[] o = (Object[]) result;
                long idOfOrg = ((BigInteger) o[0]).longValue();
                String orgNumber = (String) o[1];
                String agent = (String) o[2];
                String orgFullName = (String) o[5];
                Long sales = null;
                Long discounts = null;
                if (o[2] != null) {
                    sales = ((BigInteger) o[3]).longValue();
                }
                if (o[3] != null) {
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
                String orgName = orgNumber == null ? orgFullName : orgNumber;
                ClientPaymentItem item = new ClientPaymentItem(idOfOrg, orgName, agent, 0L, sales, discounts);
                items.add(item);
            }
        }

        private void parseTransactions (List<ClientPaymentItem> items, List res) {
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
                String orgName = orgNumber == null ? orgFullName : orgNumber;
                ClientPaymentItem item = lookupOrgById(items, idOfOrg);
                if (item == null) {
                    item = new ClientPaymentItem(idOfOrg, orgName, agent, payments, 0L, 0L);
                    items.add(item);
                }
                item.setPayments(payments);
            }
        }

        private ClientPaymentItem lookupOrgByName (List<ClientPaymentItem> items, String orgName) {
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

        private ClientPaymentItem lookupOrgById (List<ClientPaymentItem> items, long idOfOrg) {
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

        private final long idOfOrg; // ID организации
        private final String orgName; // Наименование организации
        private final String agent; // Наименование контрагента
        private Long payments; // Сумма платежей
        private Long sales; // Сумма продаж
        private Long discounts; // Сумма льготных продаж

        public long getIdOfOrg() {
            return idOfOrg;
        }

        public String getOrgName () {
            return orgName;
        }


        public String getAgent () {
            return agent;
        }


        public String getPayments () {
            return longToMoney(payments);
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


        public void setPayments (Long payments) {
            this.payments = payments;
        }


        public ClientPaymentItem(long idOfOrg, String orgName, String agent, Long payments, Long sales, Long discounts) {
            this.idOfOrg = idOfOrg;
            this.orgName = orgName;
            this.agent = agent;
            this.payments = payments;
            this.sales = sales;
            this.discounts = discounts;
        }
    }

    public List<ClientPaymentItem> getClientPaymentItems() {
        return items;
    }

}
