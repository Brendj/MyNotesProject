/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.kzn.model;

import ru.axetta.ecafe.processor.core.persistence.dao.model.order.OrderItem;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

/**
 * User: regal
 * Date: 09.03.15
 * Time: 2:23
 */
public class BeneficiaryByAllOrgItem implements Comparable {
    public final static int MIN_BEN_COMPLEX = 200;

    private int monthNumb;
    private String monthName;
    private int contingent;
    private long pricePerDay;
    private int days;
    private int[] daysArray;
    private int childDay;
    private long expenses;

    public BeneficiaryByAllOrgItem() {
    }

    public BeneficiaryByAllOrgItem(OrderItem orderItem){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(orderItem.getOrderDate());
        monthNumb = calendar.get(Calendar.MONTH);
        monthName = new DateFormatSymbols().getMonths()[monthNumb];
        expenses =orderItem.getSum();
        pricePerDay = orderItem.getSum();
        daysArray = new  int[32];
        daysArray[calendar.get(Calendar.DAY_OF_MONTH)]++;


    }
    public void update(OrderItem orderItem){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(orderItem.getOrderDate());
        daysArray[calendar.get(Calendar.DAY_OF_MONTH)]++;

        expenses += orderItem.getSum();

    }

    public void calculate(Map<Integer, List<Integer>> dayMap){
        //C
        Object[] integers = dayMap.get(monthNumb).toArray();
        for (int i = 0; i < integers.length; i++){
            if((Integer)integers[i] >= MIN_BEN_COMPLEX){
                days++;
            }
        }

        // D
        if(pricePerDay != 0){
            childDay  = (int) (expenses / pricePerDay);
        }

        // A
        if(days != 0){
            contingent = childDay / days;
        }
    }

    public boolean equals(OrderItem orderItem){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(orderItem.getOrderDate());
        return (calendar.get(Calendar.MONTH) == monthNumb) && (orderItem.getSum() == pricePerDay);
    }

    public int getMonthNumb() {
        return monthNumb;
    }

    public void setMonthNumb(int monthNumb) {
        this.monthNumb = monthNumb;
    }

    public String getMonthName() {
        return monthName;
    }

    public void setMonthName(String monthName) {
        this.monthName = monthName;
    }

    public int getContingent() {
        return contingent;
    }

    public void setContingent(int contingent) {
        this.contingent = contingent;
    }

    public long getPricePerDay() {
        return pricePerDay;
    }

    public void setPricePerDay(long pricePerDay) {
        this.pricePerDay = pricePerDay;
    }

    public int getDays() {
        return days;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public int getChildDay() {
        return childDay;
    }

    public void setChildDay(int childDay) {
        this.childDay = childDay;
    }

    public long getExpenses() {
        return expenses;
    }

    public void setExpenses(long expenses) {
        this.expenses = expenses;
    }

    @Override
    public int compareTo(Object o) {
        return Integer.valueOf(monthNumb).compareTo(((BeneficiaryByAllOrgItem)o).getMonthNumb());
    }
}
