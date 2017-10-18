/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import java.util.Collections;
import java.util.Date;
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
        private Long qty; // Кол-во
        private String sumPrice; //Сумма без скидки
        private String sumPriceDiscount; // Сумма скидки
        private String total; // Итоговая сумма
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

        public void setSumPrice(String sumPrice) {
            this.sumPrice = sumPrice;
        }

        public String getSumPriceDiscount() {
            return sumPriceDiscount;
        }

        public void setSumPriceDiscount(String sumPriceDiscount) {
            this.sumPriceDiscount = sumPriceDiscount;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
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

        public void setQty(Long qty) {
            this.qty = qty;
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
