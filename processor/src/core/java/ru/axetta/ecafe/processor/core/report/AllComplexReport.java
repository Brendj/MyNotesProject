/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.OrderDetail;

import org.hibernate.Query;
import org.hibernate.Session;

import java.math.BigInteger;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 25.01.12
 * Time: 22:55
 * Онлайн отчеты -> Отчет по комплексам -> Все комплексы
 */
public class AllComplexReport extends BasicReport {
    protected final List<ComplexItem> complexItems;

    public static class Builder {

        public AllComplexReport build(Session session, Date startDate, Date endDate, List<Long> idOfOrgList)
                throws Exception {
            Date generateTime = new Date();
            List<ComplexItem> complexItems = new LinkedList<ComplexItem>();
            if (!idOfOrgList.isEmpty()) {
                // Обработать лист с организациями
                String orgCondition = "";
                orgCondition = "and (";
                for (Long idOfOrg : idOfOrgList) {
                    orgCondition = orgCondition.concat("o.idOfOrg = " + idOfOrg + " or ");
                }
                orgCondition = orgCondition.substring(0, orgCondition.length() - 4) + ") ";

                /*String preparedQuery = "select org.officialName, od.menuDetailName, od.rPrice, od.discount, "
                        + "sum(od.qty) as quantity, min(o.createdDate), max(o.createdDate) "
                        //+ "  from CF_Orders o, CF_OrderDetails od, CF_Orgs org "
                        + "  from CF_Orders o, CF_OrderDetails od, CF_Orgs org, cf_clients c "
                        + " where o.idOfOrder = od.idOfOrder and o.state=0 and od.state=0 "
                        + "   and o.idOfOrg = od.idOfOrg and org.idOfOrg = od.idOfOrg "
                        //|+ "   and o.idofclient = c.idofclient and c.idoforg in (select friendlyOrg from cf_friendly_organization where currentorg in (:forgs)) "
                        + "   and o.idofclient = c.idofclient and c.idoforg in (select friendlyOrg from cf_friendly_organization where currentorg = c.idoforg) "
                        + "   and o.createdDate >= :fromCreatedDate and o.createdDate <= :toCreatedDate"
                        + "   and (od.menuType >= :fromMenuType and od.menuType <= :toMenuType) "
                        + orgCondition
                        + " group by org.officialName, od.menuDetailName, od.rPrice, od.discount "
                        + " order by org.officialName, od.menuDetailName";
                List resultList = null;
                Query query = session.createSQLQuery(preparedQuery);

                long startDateLong = startDate.getTime();
                long endDateLong = endDate.getTime();
                query.setLong("fromCreatedDate", startDateLong);
                query.setLong("toCreatedDate", endDateLong);
                query.setInteger("fromMenuType", OrderDetail.TYPE_COMPLEX_MIN);
                query.setInteger("toMenuType", OrderDetail.TYPE_COMPLEX_MAX);
                //query.setParameterList("forgs", idOfOrgList);

                resultList = query.list();*/
                List resultList = getResultList(session, orgCondition, startDate, endDate, false);
                complexItems = getComplexItems(resultList);

                /*for (Object result : resultList) {
                    Object[] complex = (Object[]) result;
                    String officialName = (String) complex[0];
                    String menuDetailName = (String) complex[1];
                    Long rPrice = ((BigInteger) complex[2]).longValue();
                    Long discount = ((BigInteger) complex[3]).longValue();
                    Long qty = ((BigInteger) complex[4]).longValue();
                    Date firstTimeSale = new Date(((BigInteger) complex[5]).longValue());
                    Date lastTimeSale = new Date(((BigInteger) complex[6]).longValue());
                    ComplexItem complexItem = new ComplexItem(officialName, menuDetailName, rPrice, discount, qty,
                            firstTimeSale, lastTimeSale);
                    complexItems.add(complexItem);
                }*/

                resultList = getResultList(session, orgCondition, startDate, endDate, true);
                if (resultList != null && resultList.size() > 0) {
                    List<ComplexItem> complexItems2 = getComplexItems(resultList);
                    for (ComplexItem item2 : complexItems2) {
                        for (ComplexItem item : complexItems) {
                            if (item.equals(item2)) {
                                item.setQtyTemp(item2.getQty());
                                item.setSumPriceDiscountTemp(item2.getSumPriceDiscount());
                                item.setSumPriceTemp(item2.getSumPrice());
                                item.setTotalTemp(item2.getTotal());
                                break;
                            }
                        }
                    }
                }

            } else {
                throw new Exception("Укажите список организаций");
            }
            return new AllComplexReport(generateTime, new Date().getTime() - generateTime.getTime(), complexItems);
        }

    }

    private static List<ComplexItem> getComplexItems(List resultList) {
        List<ComplexItem> complexItems = new LinkedList<ComplexItem>();
        for (Object result : resultList) {
            Object[] complex = (Object[]) result;
            String officialName = (String) complex[0];
            String menuDetailName = (String) complex[1];
            Long rPrice = ((BigInteger) complex[2]).longValue();
            Long discount = ((BigInteger) complex[3]).longValue();
            Long qty = ((BigInteger) complex[4]).longValue();
            Date firstTimeSale = new Date(((BigInteger) complex[5]).longValue());
            Date lastTimeSale = new Date(((BigInteger) complex[6]).longValue());
            ComplexItem complexItem = new ComplexItem(officialName, menuDetailName, rPrice, discount, qty,
                    firstTimeSale, lastTimeSale);
            complexItems.add(complexItem);
        }
        return complexItems;
    }

    protected static List getResultList(Session session, String orgCondition, Date startDate, Date endDate, boolean tempClients) {
        String preparedQuery = "select org.officialName, od.menuDetailName, od.rPrice, od.discount, "
                + "sum(od.qty) as quantity, min(o.createdDate), max(o.createdDate) "
                + "  from CF_Orders o, CF_OrderDetails od, CF_Orgs org, cf_clients c "
                + " where o.idOfOrder = od.idOfOrder and o.state=0 and od.state=0 "
                + "   and o.idOfOrg = od.idOfOrg and org.idOfOrg = od.idOfOrg "
                + "   and o.idofclient = c.idofclient and c.idoforg" + (tempClients ? " not " : "")
                + " in (select friendlyOrg from cf_friendly_organization where currentorg = o.idoforg) "
                + "   and o.createdDate >= :fromCreatedDate and o.createdDate <= :toCreatedDate"
                + "   and (od.menuType >= :fromMenuType and od.menuType <= :toMenuType) "
                + orgCondition
                + " group by org.officialName, od.menuDetailName, od.rPrice, od.discount "
                + " order by org.officialName, od.menuDetailName";
        Query query = session.createSQLQuery(preparedQuery);

        long startDateLong = startDate.getTime();
        long endDateLong = endDate.getTime();
        query.setLong("fromCreatedDate", startDateLong);
        query.setLong("toCreatedDate", endDateLong);
        query.setInteger("fromMenuType", OrderDetail.TYPE_COMPLEX_MIN);
        query.setInteger("toMenuType", OrderDetail.TYPE_COMPLEX_MAX);

        return query.list();
    }

    public AllComplexReport() {
        super();
        this.complexItems = Collections.emptyList();
    }

    public AllComplexReport(Date generateTime, long generateDuration, List<ComplexItem> complexItems) {
        super(generateTime, generateDuration);
        this.complexItems = complexItems;
    }

    public static class ComplexItem {

        private final String officialName; // Название организации
        private final String menuDetailName; // Название
        private final String rPrice; // Цена за ед
        private final String discount; // Скидка на ед
        private final Long qty; // Кол-во
        private final String sumPrice; //Сумма без скидки
        private final String sumPriceDiscount; // Сумма скидки
        private final String total; // Итоговая сумма
        private final Date firstTimeSale; // Время первой продажи
        private final Date lastTimeSale; // Время последней продажи
        private Long qtyTemp; // Кол-во
        private String sumPriceTemp; //Сумма без скидки
        private String sumPriceDiscountTemp; // Сумма скидки
        private String totalTemp; // Итоговая сумма

        public String getOfficialName() {
            return officialName;
        }

        public String getMenuDetailName() {
            return menuDetailName;
        }

        public String getrPrice() {
            return rPrice;
        }

        public String getDiscount() {
            return discount;
        }

        public Long getQty() {
            return qty;
        }

        public String getSumPrice() {
            return sumPrice;
        }

        public String getSumPriceDiscount() {
            return sumPriceDiscount;
        }

        public String getTotal() {
            return total;
        }

        public Date getFirstTimeSale() {
            return firstTimeSale;
        }

        public Date getLastTimeSale() {
            return lastTimeSale;
        }

        public ComplexItem(String officialName, String menuDetailName, Long rPrice, Long discount, Long qty,
                Date firstTimeSale, Date lastTimeSale) {
            this.officialName = officialName;
            this.menuDetailName = menuDetailName;
            this.rPrice = longToMoney(rPrice + discount);
            this.discount = longToMoney(discount);
            this.qty = qty;
            this.sumPrice = longToMoney((rPrice + discount) * qty);
            this.sumPriceDiscount = longToMoney(discount * qty);
            this.total = longToMoney(rPrice * qty);
            this.firstTimeSale = firstTimeSale;
            this.lastTimeSale = lastTimeSale;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof ComplexItem)) {
                return false;
            }

            ComplexItem that = (ComplexItem) o;

            if (officialName.equals(that.getOfficialName()) && menuDetailName.equals(that.getMenuDetailName())
                    && rPrice.equals(that.getrPrice()) && discount.equals(that.getDiscount())) {
                return true;
            } else {
                return false;
            }
        }

        public Long getQtyTemp() {
            return qtyTemp;
        }

        public void setQtyTemp(Long qtyTemp) {
            this.qtyTemp = qtyTemp;
        }

        public String getSumPriceTemp() {
            return sumPriceTemp;
        }

        public void setSumPriceTemp(String sumPriceTemp) {
            this.sumPriceTemp = sumPriceTemp;
        }

        public String getSumPriceDiscountTemp() {
            return sumPriceDiscountTemp;
        }

        public void setSumPriceDiscountTemp(String sumPriceDiscountTemp) {
            this.sumPriceDiscountTemp = sumPriceDiscountTemp;
        }

        public String getTotalTemp() {
            return totalTemp;
        }

        public void setTotalTemp(String totalTemp) {
            this.totalTemp = totalTemp;
        }
    }

    public List<ComplexItem> getComplexItems() {
        return complexItems;
    }
}
