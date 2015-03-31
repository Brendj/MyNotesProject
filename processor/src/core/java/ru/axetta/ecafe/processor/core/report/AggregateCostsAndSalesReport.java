/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.MenuExchangeRule;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.Order;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodBasicBasketPrice;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.text.DecimalFormat;
import java.util.*;

/* Мониторинг -> Отчет по показателям цен и продаж */

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
                if (org == null) {
                    break;
                }

                // Проверка того, является ли указанная организация поставщиком
                // Если да - работать с организациями, для которых поставщиком указана данная организация
                boolean orgIsSourceFlag = false;
                HashSet<Long> workingOrgSet = new HashSet<Long>();
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
                                workingOrgSet.add(curOrg.getIdOfOrg());
                            }
                        }
                    }
                    orgIsSourceFlag = true;
                }
                else {
                    workingOrgSet.add(org.getIdOfOrg());
                }

                StringBuffer queryStringBuffer;
                String queryString;
                Query query;
                List resultList;

                // объем продаж и средний чек
                queryStringBuffer = new StringBuffer("select count(o.id.idOfOrg), sum(o.RSum) from Order o where"
                        + " o.createTime > " + startDate.getTime() + " and o.createTime < " + endDate.getTime()
                        + " and (o.RSum > 0) and o.state=0 and (");
                for (Long curOrg : workingOrgSet) {
                    queryStringBuffer.append("o.id.idOfOrg = ");
                    queryStringBuffer.append(curOrg);
                    queryStringBuffer.append(" or ");
                }
                queryString = queryStringBuffer.toString();
                queryString = queryString.substring(0, queryString.length() - 4);
                queryString += ") group by o.id.idOfOrg order by o.id.idOfOrg";
                query = session.createQuery(queryString);
                resultList = query.list();

                double salesVolume = 0.0;
                long totalCountOfPaidOrders = 0L;

                for (Object resultObject : resultList) {
                    Object[] result = (Object[]) resultObject;

                    Long currentOrgCountOfPaidOrders = (Long) result[0];
                    Long rSum = (Long) result[1];

                    Double curentOrgSalesValue = 0.0;
                    curentOrgSalesValue += rSum;

                    totalCountOfPaidOrders += currentOrgCountOfPaidOrders;
                    salesVolume += curentOrgSalesValue;
                }
                salesVolume /= 100.0;
                double averageReceipt;
                if (salesVolume > 0.0) {
                    averageReceipt = salesVolume / ((double) totalCountOfPaidOrders);
                } else {
                    averageReceipt = 0.0;
                }

                // средняя цена комплексов
                queryStringBuffer = new StringBuffer("select count(ci.org.idOfOrg), sum(ci.currentPrice) from ComplexInfo ci,"
                        + " ComplexInfoDetail cid, MenuDetail md, Menu m where"
                        + " ci.idOfComplexInfo = cid.complexInfo.idOfComplexInfo"
                        + " and cid.menuDetail.idOfMenuDetail = md.idOfMenuDetail"
                        + " and md.menu.idOfMenu = m.idOfMenu"
                        + " and m.createTime > " + startDate.getTime() + " and m.createTime < " + endDate.getTime()
                        + " and ci.currentPrice IS NOT NULL and (");
                for (Long curOrg : workingOrgSet) {
                    queryStringBuffer.append("ci.org.idOfOrg = ");
                    queryStringBuffer.append(curOrg);
                    queryStringBuffer.append(" or ");
                }
                queryString = queryStringBuffer.toString();
                queryString = queryString.substring(0, queryString.length() - 4);
                queryString += ") group by ci.org.idOfOrg order by ci.org.idOfOrg";
                query = session.createQuery(queryString);
                resultList = query.list();

                double averageComplexPrice = 0.0;
                int orgWithComplexesCount = 0;

                for (Object resultObject : resultList) {
                    Object[] result = (Object[]) resultObject;

                    orgWithComplexesCount++;
                    Double currentOrgCountOfComplexes = Double.valueOf((Long) result[0]);
                    if (currentOrgCountOfComplexes > 0) {
                        Double currentOrgAverageComplexPrice = Double.valueOf((Long) result[1]) / currentOrgCountOfComplexes;
                        averageComplexPrice += currentOrgAverageComplexPrice;
                    }

                }
                if (orgWithComplexesCount > 0) {
                    averageComplexPrice = averageComplexPrice / ((double) orgWithComplexesCount) / 100.0;
                }

                // средняя месячная трата
                queryStringBuffer = new StringBuffer("select sum(o.RSum) from Order o where"
                        + " o.createTime > " + startDate.getTime() + " and o.state=0 and o.createTime < " + endDate.getTime()
                        + " and o.RSum > 0 and (");
                for (Long curOrg : workingOrgSet) {
                    queryStringBuffer.append("o.id.idOfOrg = ");
                    queryStringBuffer.append(curOrg);
                    queryStringBuffer.append(" or ");
                }
                queryString = queryStringBuffer.toString();
                queryString = queryString.substring(0, queryString.length() - 4);
                queryString += ") group by o.client.idOfClient order by o.client.idOfClient";
                query = session.createQuery(queryString);
                resultList = query.list();
                long daysCount = countDaysBetween(startDate, endDate);
                long clientsCount = 0L;
                double averageMonthlyExpense = 0.0;
                for (Object resultObject : resultList) {
                    Long clientExpenses = (Long) resultObject;
                    clientsCount++;
                    averageMonthlyExpense += clientExpenses;
                }
                if (clientsCount > 0) {
                    averageMonthlyExpense = averageMonthlyExpense / ((double) clientsCount) / ((double) daysCount) * 30.0;
                }

                // Базовая корзина
                Long id;
                Criteria ruleCriteria = session.createCriteria(MenuExchangeRule.class);
                ruleCriteria.add(Restrictions.eq("idOfSourceOrg",idOfOrg));
                List list = ruleCriteria.list();
                if(list.isEmpty()){
                    Criteria destCriteria = session.createCriteria(MenuExchangeRule.class);
                    destCriteria.add(Restrictions.eq("idOfDestOrg",idOfOrg));
                    destCriteria.setProjection(Projections.property("idOfSourceOrg"));
                    id = (Long) destCriteria.uniqueResult();
                }  else {
                    id = idOfOrg;
                }
                Criteria criteria = session.createCriteria(GoodBasicBasketPrice.class);
                criteria.add(Restrictions.eq("orgOwner", id));
                criteria.setProjection(Projections.sum("price"));
                Object result = criteria.uniqueResult();
                Double sum = result==null?0.0: (Long) result * 1.0 /100 ;


                costsAndSalesItems.add(new CostsAndSales(idOfOrg, orgIsSourceFlag, org.getShortName(), salesVolume, averageReceipt, averageMonthlyExpense, averageComplexPrice, sum ));            }
            return new AggregateCostsAndSalesReport(generateTime, new Date().getTime() - generateTime.getTime(),
                    costsAndSalesItems);
        }

        private long countDaysBetween(Date startDate, Date endDate) {
            final double MILLISECONDS_IN_DAY = 1000.0 * 60.0 * 60.0 * 24.0;

            if (endDate.before(startDate)) {
                Date tempDate = endDate;
                endDate = startDate;
                startDate = tempDate;
            }

            // Если ошибка не повториться Infinity - то этот код можно удалить.
/*            Calendar startCal = GregorianCalendar.getInstance();
            startCal.setTime(startDate);
            startCal.set(Calendar.HOUR_OF_DAY, 0);
            startCal.set(Calendar.MINUTE, 0);
            startCal.set(Calendar.SECOND, 0);
            long startTime = startCal.getTimeInMillis();

            Calendar endCal = GregorianCalendar.getInstance();
            endCal.setTime(endDate);
            endCal.set(Calendar.HOUR_OF_DAY, 0);
            endCal.set(Calendar.MINUTE, 0);
            endCal.set(Calendar.SECOND, 0);
            long endTime = endCal.getTimeInMillis();*/

            double tmpDays = (endDate.getTime() - startDate.getTime()) / MILLISECONDS_IN_DAY;
            return Math.round(tmpDays);
        }

        private Org getOrgById(Session session, Long idOfOrg) {
            Criteria orgCriteria = session.createCriteria(Org.class);
            orgCriteria.add(Restrictions.eq("idOfOrg", idOfOrg));
            return (Org) orgCriteria.uniqueResult();
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
        private boolean orgIsSourceFlag;
        private String officialName;
        private double salesVolume;
        private double averageReceipt;
        private double averageMonthlyExpense;
        private double averageComplexPrice;
        private double basicBasketPrice;

        private String format = "%.2f";

        public CostsAndSales(long idOfOrg, boolean orgIsSourceFlag, String officialName, double salesVolume,
                double averageReceipt, double averageMonthlyExpense, double averageComplexPrice, double basicBasketPrice) {
            this.idOfOrg = idOfOrg;
            this.orgIsSourceFlag = orgIsSourceFlag;
            this.officialName = officialName;
            this.salesVolume = salesVolume;
            this.averageReceipt = averageReceipt;
            this.averageMonthlyExpense = averageMonthlyExpense;
            this.averageComplexPrice = averageComplexPrice;
            this.basicBasketPrice = basicBasketPrice;
        }

        private String formatOutput(double value) {
            if (value > 0) {
                return String.format(format, value);
            }
            return "-";
        }

        public long getIdOfOrg() {
            return idOfOrg;
        }

        public void setIdOfOrg(long idOfOrg) {
            this.idOfOrg = idOfOrg;
        }

        public String getOrgType() {
            if (orgIsSourceFlag) {
                return "Поставщик";
            } else {
                return "ОУ";
            }
        }

        public void setOrgIsSourceFlag(boolean orgIsSourceFlag) {
            this.orgIsSourceFlag = orgIsSourceFlag;
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
            return formatOutput(salesVolume);
        }

        public void setSalesVolume(double salesVolume) {
            this.salesVolume = salesVolume;
        }

        public double getAverageMonthlyExpense() {
            return averageMonthlyExpense;
        }

        public String getFormattedAverageMonthlyExpense() {
            return formatOutput(averageMonthlyExpense);
        }

        public void setAverageMonthlyExpense(double averageMonthlyExpense) {
            this.averageMonthlyExpense = averageMonthlyExpense;
        }

        public double getAverageReceipt() {
            return averageReceipt;
        }

        public String getFormattedAverageReceipt() {
            return formatOutput(averageReceipt);
        }

        public void setAverageReceipt(double averageReceipt) {
            this.averageReceipt = averageReceipt;
        }

        public double getAverageComplexPrice() {
            return averageComplexPrice;
        }

        public String getFormattedAverageComplexPrice() {
            return formatOutput(averageComplexPrice);
        }

        public void setAverageComplexPrice(double averageComplexPrice) {
            this.averageComplexPrice = averageComplexPrice;
        }

        public double getBasicBasketPrice() {
            return basicBasketPrice;
        }

        public String getFormattedBasicBacketPrice() {
            return formatOutput(basicBasketPrice);
        }

        public void setBasicBasketPrice(double basicBasketPrice) {
            this.basicBasketPrice = basicBasketPrice;
        }

    }
}