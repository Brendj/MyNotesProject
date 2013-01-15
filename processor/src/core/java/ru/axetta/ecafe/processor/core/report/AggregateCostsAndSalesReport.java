/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.MenuExchangeRule;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.Order;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;


import java.text.DecimalFormat;
import java.util.*;

public class AggregateCostsAndSalesReport extends BasicReport {

    private final List<CostsAndSales> costsAndSalesItems;

    public static class Builder {

        public AggregateCostsAndSalesReport build(Session session, Date startDate, Date endDate, List<Long> idOfOrgList)
                throws Exception {
            Date generateTime = new Date();
            List<CostsAndSales> costsAndSalesItems = new ArrayList<CostsAndSales>();
            for (Long idOfOrg : idOfOrgList) {
                Criteria orgCriteria = session.createCriteria(Org.class);
                orgCriteria.add(Restrictions.eq("idOfOrg", idOfOrg));
                Org org = (Org) orgCriteria.uniqueResult();

                // Проверка того, является ли указанная организация поставщиком
                // Если да - работать с организациями, для которых поставщиком указана данная организация
                HashSet<Org> workingOrgSet = new HashSet<Org>();
                Criteria destMenuExchangeCriteria = session.createCriteria(MenuExchangeRule.class);
                destMenuExchangeCriteria.add(Restrictions.eq("idOfSourceOrg", idOfOrg));
                List menuExchangeRuleList = destMenuExchangeCriteria.list();
                if (!menuExchangeRuleList.isEmpty()) {
                    for (Object object: menuExchangeRuleList) {
                        MenuExchangeRule menuExchangeRule = (MenuExchangeRule) object;
                        Long idOfDestOrg = menuExchangeRule.getIdOfDestOrg();
                        if (idOfDestOrg != null) {
                            Org curOrg = getOrgById(session, idOfDestOrg);
                            if (curOrg != null) {
                                workingOrgSet.add(curOrg);
                            }
                        }
                    }
                } else {
                    Org curOrg = getOrgById(session, idOfOrg);
                    if (curOrg != null) {
                        workingOrgSet.add(curOrg);
                    }
                }

                StringBuffer queryStringBuffer = new StringBuffer("select count(o.id.idOfOrg), sum(o.sumByCard), sum(o.sumByCash) from Order o where"
                        + " ((o.sumByCard > 0) or (o.sumByCash > 0))  and (");
                for (Org curOrg : workingOrgSet) {
                    queryStringBuffer.append("o.id.idOfOrg = ");
                    queryStringBuffer.append(curOrg.getIdOfOrg());
                    queryStringBuffer.append(" or ");
                }
                String queryString = queryStringBuffer.toString();
                queryString = queryString.substring(0, queryString.length() - 4);
                queryString += ") group by o.id.idOfOrg order by o.id.idOfOrg";
                Query query = session.createQuery(queryString);
                List resultList = query.list();

                double salesVolume = 0.0;
                double totalCountOfPaidOrders = 0.0;

                for (Object resultObject : resultList) {
                    Object[] result = (Object[]) resultObject;

                    Double currentOrgCountOfPaidOrders = Double.valueOf((Long) result[0]);
                    Long sumByCard = (Long) result[1];
                    Long sumByCash = (Long) result[2];

                    Double curentOrgSalesValue = 0.0;
                    curentOrgSalesValue += sumByCard;
                    curentOrgSalesValue += sumByCash;

                    totalCountOfPaidOrders += currentOrgCountOfPaidOrders;
                    salesVolume += curentOrgSalesValue;
                }
                salesVolume /= 100.0;
                double averageReceipt;
                if (salesVolume > 0.0) {
                    averageReceipt = salesVolume / totalCountOfPaidOrders;
                } else {
                    averageReceipt = 0.0;
                }

                costsAndSalesItems.add(new CostsAndSales(idOfOrg, org.getOfficialName(), salesVolume, averageReceipt, 0.0, 0.0, 0.0));
            }
            return new AggregateCostsAndSalesReport(generateTime, new Date().getTime() - generateTime.getTime(),
                    costsAndSalesItems);
        }

        private Org getOrgById(Session session, Long idOfOrg) {
            Criteria orgCriteria = session.createCriteria(Org.class);
            orgCriteria.add(Restrictions.eq("idOfOrg", idOfOrg));
            Org org = (Org) orgCriteria.uniqueResult();
            return org;
        }

    }

    public AggregateCostsAndSalesReport() {
        super();
        this.costsAndSalesItems = Collections.emptyList();
    }

    public AggregateCostsAndSalesReport(Date generateTime, long generateDuration, List<CostsAndSales> costsAndSalesItems) {
        super(generateTime, generateDuration);
        this.costsAndSalesItems = costsAndSalesItems;
    }

    public List<CostsAndSales> getCostsAndSalesItems() {
        return costsAndSalesItems;
    }

    public static class CostsAndSales {

        private long idOfOrg;
        private String officialName;
        private double salesVolume;
        private double averageReceipt;
        private double averageMonthlyExpense;
        private double averageComplexPrice;
        private double basicBasketPrice;

        private String format = "%.2f";

        public CostsAndSales(long idOfOrg, String officialName, double salesVolume, double averageReceipt,
                double averageMonthlyExpense, double averageComplexPrice, double basicBasketPrice) {
            this.idOfOrg = idOfOrg;
            this.officialName = officialName;
            this.salesVolume = salesVolume;
            this.averageReceipt = averageReceipt;
            this.averageMonthlyExpense = averageMonthlyExpense;
            this.averageComplexPrice = averageComplexPrice;
            this.basicBasketPrice = basicBasketPrice;
        }

        public long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public String getOfficialName() {
            return officialName;
        }

        public void setOfficialName(String officialName) {
            this.officialName = officialName;
        }

        public double getSalesVolume() {
            return salesVolume;
        }

        public String getFormattedSalesVolume() {
            return String.format(format, salesVolume);
        }

        public void setSalesVolume(double salesVolume) {
            this.salesVolume = salesVolume;
        }

        public double getAverageMonthlyExpense() {
            return averageMonthlyExpense;
        }

        public String getFormattedAverageMonthlyExpense() {
            return String.format(format, averageMonthlyExpense);
        }

        public void setAverageMonthlyExpense(double averageMonthlyExpense) {
            this.averageMonthlyExpense = averageMonthlyExpense;
        }

        public double getAverageReceipt() {
            return averageReceipt;
        }

        public String getFormattedAverageReceipt() {
            return String.format(format, averageReceipt);
        }

        public void setAverageReceipt(double averageReceipt) {
            this.averageReceipt = averageReceipt;
        }

        public double getAverageComplexPrice() {
            return averageComplexPrice;
        }

        public String getFormattedAverageComplexPrice() {
            return String.format(format, averageComplexPrice);
        }

        public void setAverageComplexPrice(double averageComplexPrice) {
            this.averageComplexPrice = averageComplexPrice;
        }

        public double getBasicBasketPrice() {
            return basicBasketPrice;
        }

        public String getFormattedBasicBacketPrice() {
            return String.format(format, basicBasketPrice);
        }

        public void setBasicBasketPrice(double basicBasketPrice) {
            this.basicBasketPrice = basicBasketPrice;
        }

    }
}