/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.OrderDetail;

import org.hibernate.Query;
import org.hibernate.Session;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class SalesReport extends BasicReport {

    private final List<SalesItem> salesItems;

    public static class Builder {

        public SalesReport build(Session session, Date startDate, Date endDate, List<Long> idOfOrgList)
                throws Exception {
            Date generateTime = new Date();
            List<SalesItem> salesItems = new ArrayList<SalesItem>();
            if (!idOfOrgList.isEmpty()) {
                String preparedQuery =
                          "select org.officialName, od.MenuDetailName, od.MenuOutput, od.MenuOrigin, od.rPrice, "
                        + " od.discount, sum(od.qty) as quantity, min(o.createdDate), max(o.createdDate) \n"
                        + "from CF_Orders o join CF_OrderDetails od on (o.idOfOrder = od.idOfOrder and o.idOfOrg = od.idOfOrg) \n"
                        + "                 join CF_Orgs org on (org.idOfOrg = od.idOfOrg) \n"
                        + "where o.createdDate >= :fromCreatedDate and o.createdDate <= :toCreatedDate and od.menuType = :menuType \n"
                        + " and (org.idOfOrg in (:orgs) or org.idOfOrg in "
                        + "  (select me.IdOfDestOrg from CF_MenuExchangeRules me where me.IdOfSourceOrg in (:destOrgs))) \n"
                        + "group by org.officialName, od.menuDetailName, od.MenuOrigin, od.rPrice, od.MenuOutput, od.discount \n"
                        + "order by org.officialName, od.MenuOrigin, od.menuDetailName";
                Query query = session.createSQLQuery(preparedQuery);
                long startDateLong = startDate.getTime();
                long endDateLong = endDate.getTime();
                query.setLong("fromCreatedDate", startDateLong);
                query.setLong("toCreatedDate", endDateLong);
                query.setInteger("menuType", OrderDetail.TYPE_DISH_ITEM);
                query.setParameterList("orgs", idOfOrgList);
                query.setParameterList("destOrgs", idOfOrgList);

                List resultList = query.list();

                for (Object result : resultList) {
                    Object[] sale = (Object[]) result;
                    String officialName = (String) sale[0];
                    String menuDetailName = (String) sale[1];
                    String menuOutput = (String) sale[2];
                    Integer menuOrigin = (Integer) sale[3];
                    Long rPrice = ((BigInteger) sale[4]).longValue();
                    Long discount = ((BigInteger) sale[5]).longValue();
                    Long qty = ((BigInteger) sale[6]).longValue();
                    Date firstTimeSale = new Date(((BigInteger) sale[7]).longValue());
                    Date lastTimeSale = new Date(((BigInteger) sale[8]).longValue());
                    SalesItem salesItem = new SalesItem(officialName, menuDetailName, menuOutput, menuOrigin, rPrice,
                            discount, qty, firstTimeSale, lastTimeSale);
                    salesItems.add(salesItem);
                }
            }
            return new SalesReport(generateTime, new Date().getTime() - generateTime.getTime(), salesItems);
        }

    }

    public SalesReport() {
        super();
        this.salesItems = Collections.emptyList();
    }

    public SalesReport(Date generateTime, long generateDuration, List<SalesItem> salesItems) {
        super(generateTime, generateDuration);
        this.salesItems = salesItems;
    }

    public static class SalesItem {

        private final String officialName; // Название организации
        private final String menuDetailName; // Название
        private final String menuOutput; //Выход
        private final Integer menuOrigin; // Вид производства,
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

        public String getMenuOutput() {
            return menuOutput;
        }

        public Integer getMenuOrigin() {
            return menuOrigin;
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

        public SalesItem(String officialName, String menuDetailName, String menuOutput, Integer menuOrigin, Long rPrice,
                Long discount, Long qty, Date firstTimeSale, Date lastTimeSale) {
            this.officialName = officialName;
            this.menuDetailName = menuDetailName;
            this.menuOutput = menuOutput;
            this.menuOrigin = menuOrigin;
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

    public List<SalesItem> getSalesItems() {
        return salesItems;
    }

}