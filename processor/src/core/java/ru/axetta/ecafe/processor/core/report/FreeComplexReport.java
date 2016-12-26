/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
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
 * Онлайн отчеты -> Отчет по комплексам -> Бесплатные комплексы
 */
public class FreeComplexReport extends BasicReport {
    private final List<ComplexItem> complexItems;

    public static class Builder {

        public FreeComplexReport build(Session session, Date startDate, Date endDate, List<Long> idOfOrgList)
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

                String preparedQuery = "select org.officialName, od.menuDetailName, od.rPrice, od.discount, "
                        + "sum(od.qty) as quantity, " + " min(o.createdDate), max(o.createdDate) "
                        + "  from CF_Orders o, CF_OrderDetails od, CF_Orgs org "
                        + " where o.idOfOrder = od.idOfOrder and o.state=0 and od.state=0 "
                        + "   and o.idOfOrg = od.idOfOrg" + "   and org.idOfOrg = od.idOfOrg "
                        + "   and o.createdDate >= :fromCreatedDate " + "   and o.createdDate <= :toCreatedDate"
                        + "   and (od.menuType >= :fromMenuType and od.menuType <= :toMenuType) " + orgCondition
                        //+ "   and (((od.rPrice + od.discount) = od.discount and o.trdDiscount is null) or"
                        + "   and (od.socDiscount > 0 and od.rprice = 0) "
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

                resultList = query.list();

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
            } else {
                throw new Exception("Укажите список организаций");
            }
            return new FreeComplexReport(generateTime, new Date().getTime() - generateTime.getTime(), complexItems);
        }

    }

    public FreeComplexReport() {
        super();
        this.complexItems = Collections.emptyList();
    }

    public FreeComplexReport(Date generateTime, long generateDuration, List<ComplexItem> complexItems) {
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
    }

    public List<ComplexItem> getComplexItems() {
        return complexItems;
    }

}
