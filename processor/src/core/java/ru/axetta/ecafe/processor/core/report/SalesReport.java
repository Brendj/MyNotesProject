/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.OrderDetail;

import org.hibernate.Query;
import org.hibernate.Session;

import java.math.BigInteger;
import java.util.*;

/**
 *  Онлайн отчеты -> Отчет по продажам
 */

public class SalesReport extends BasicReport {
    /*
    * Параметры отчета для добавления в правила и шаблоны
    *
    * При создании любого отчета необходимо добавить параметры:
    * REPORT_NAME - название отчета на русском
    * TEMPLATE_FILE_NAMES - названия всех jasper-файлов, созданных для отчета
    * IS_TEMPLATE_REPORT - добавлять ли отчет в шаблоны отчетов
    * PARAM_HINTS - параметры отчета (смотри ReportRuleConstants.PARAM_HINTS)
    * заполняется, если отчет добавлен в шаблоны (класс AutoReportGenerator)
    *
    * Затем КАЖДЫЙ класс отчета добавляется в массив ReportRuleConstants.ALL_REPORT_CLASSES
    */
    public static final String REPORT_NAME = "Отчет по реализации";
    public static final String[] TEMPLATE_FILE_NAMES = {"kzn\\SalesReport.jasper"};
    public static final boolean IS_TEMPLATE_REPORT = false;
    public static final int[] PARAM_HINTS = new int[]{};


    private final List<SalesItem> salesItems;

    public static class Builder {

        public SalesReport build(Session session, Date startDate, Date endDate, List<Long> idOfOrgList)
                throws Exception {
            Date generateTime = new Date();
            List<SalesItem> salesItems = new ArrayList<SalesItem>();
            if (!idOfOrgList.isEmpty()) {
                String preparedQuery =
                          "select org.shortnameinfoservice, od.MenuDetailName, od.rPrice, "
                        + " od.discount, sum(od.qty) as quantity, min(o.createdDate), max(o.createdDate), case when od.discount > 0 then sum(od.qty) else 0 end "
                        + "from CF_Orders o join CF_OrderDetails od on (o.idOfOrder = od.idOfOrder and o.idOfOrg = od.idOfOrg) "
                        + "                 join CF_Orgs org on (org.idOfOrg = od.idOfOrg) \n"
                        + "where o.createdDate >= :fromCreatedDate and o.createdDate <= :toCreatedDate and od.menuType = :menuType "
                        + "  and o.state=0 and od.state=0 and (org.idOfOrg in (:orgs) or org.idOfOrg in "
                        + "  (select me.IdOfDestOrg from CF_MenuExchangeRules me where me.IdOfSourceOrg in (:destOrgs))) "
                        + "group by org.shortnameinfoservice, od.menuDetailName, od.rPrice, od.discount "
                        + "order by org.shortnameinfoservice, od.menuDetailName";
                Query query = session.createSQLQuery(preparedQuery);
                long startDateLong = startDate.getTime();
                long endDateLong = endDate.getTime();
                query.setLong("fromCreatedDate", startDateLong);
                query.setLong("toCreatedDate", endDateLong);
                query.setInteger("menuType", OrderDetail.TYPE_DISH_ITEM);
                query.setParameterList("orgs", idOfOrgList);
                query.setParameterList("destOrgs", idOfOrgList);

                List resultList = query.list();
                Map<String, SalesItem> map = new TreeMap<>();

                for (Object result : resultList) {
                    Object[] sale = (Object[]) result;
                    String officialName = (String) sale[0];
                    String menuDetailName = (String) sale[1];
                    Long rPrice = ((BigInteger) sale[2]).longValue();
                    Long discount = ((BigInteger) sale[3]).longValue();
                    Long qty = ((BigInteger) sale[4]).longValue();
                    Date firstTimeSale = new Date(((BigInteger) sale[5]).longValue());
                    Date lastTimeSale = new Date(((BigInteger) sale[6]).longValue());
                    Long qtyDiscount = ((BigInteger) sale[7]).longValue();
                    String key = menuDetailName + (rPrice + discount);
                    SalesItem salesItem = map.get(key);
                    if (salesItem == null) {
                        salesItem = new SalesItem(officialName, menuDetailName, rPrice, discount, qty, firstTimeSale,
                                lastTimeSale, qtyDiscount);
                    }
                    salesItem.getDiscountSet().add(discount);
                    map.put(key, salesItem);
                }
                salesItems.addAll(map.values());
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
        private final String rPrice; // Цена за ед
        private final String discount; // Скидка на ед
        private final Long qty; // Кол-во
        private final String sumPrice; //Сумма без скидки
        private final String sumPriceDiscount; // Сумма скидки
        private final String total; // Итоговая сумма
        private final Date firstTimeSale; // Время первой продажи
        private final Date lastTimeSale; // Время последней продажи
        private final Set<Long> discountSet = new HashSet<>();
        private final Long qtyDiscount;

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

        public String getDiscountForReport() {
            String result = "";
            for (Long d : discountSet) {
                result += longToMoney(d) + " + ";
            }
            if (result.length() > 0) result = result.substring(0, result.length() - 3);
            return result;
        }

        public SalesItem(String officialName, String menuDetailName, Long rPrice,
                Long discount, Long qty, Date firstTimeSale, Date lastTimeSale, Long qtyDiscount) {
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
            this.qtyDiscount = qtyDiscount;
        }

        public Long getQtyDiscount() {
            return qtyDiscount;
        }

        public Set<Long> getDiscountSet() {
            return discountSet;
        }
    }

    public List<SalesItem> getSalesItems() {
        return salesItems;
    }

}