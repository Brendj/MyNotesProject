/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.feeding;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Person;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.feeding.CycleDiagram;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 18.10.13
 * Time: 15:55
 * To change this template use File | Settings | File Templates.
 */
public class SubscriptionFeedingReportItem {

    private String fio;
    private Double balance;
    private Double subBalance1;
    private Date dateActivate;
    private Double weekPrice;
    private Double monthPrice;
    private Date simpleDateDeactivate;

    public SubscriptionFeedingReportItem() {}

    public SubscriptionFeedingReportItem(String fio, Double balance, Double subBalance1, Date dateActivate,
            Double weekPrice, Double monthPrice, Date simpleDateDeactivate) {
        this.fio = fio;
        this.balance = balance;
        this.subBalance1 = subBalance1;
        this.dateActivate = dateActivate;
        this.weekPrice = weekPrice;
        this.monthPrice = monthPrice;
        this.simpleDateDeactivate = simpleDateDeactivate;
    }

    public SubscriptionFeedingReportItem(Person person, Client client, Date dateActivate,  CycleDiagram diagram, Date simpleDateDeactivate) {
        this.fio = person.getFullName();
        Long subBalance11 = client.getSubBalance1()==null?0L:client.getSubBalance1();
        this.balance = (client.getBalance() - subBalance11) * 1.0 / 100;
        this.subBalance1 = subBalance11 * 1.0 / 100;
        this.dateActivate = dateActivate;
        this.weekPrice = diagram.getWeekPrice() * 1.0 / 100;
        this.monthPrice = diagram.getMonthPrice() * 1.0 / 100;
        this.simpleDateDeactivate = simpleDateDeactivate;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public Double getSubBalance1() {
        return subBalance1;
    }

    public void setSubBalance1(Double subBalance1) {
        this.subBalance1 = subBalance1;
    }

    public Date getDateActivate() {
        return dateActivate;
    }

    public void setDateActivate(Date dateActivate) {
        this.dateActivate = dateActivate;
    }

    public Double getWeekPrice() {
        return weekPrice;
    }

    public void setWeekPrice(Double weekPrice) {
        this.weekPrice = weekPrice;
    }

    public Double getMonthPrice() {
        return monthPrice;
    }

    public void setMonthPrice(Double monthPrice) {
        this.monthPrice = monthPrice;
    }

    public Date getSimpleDateDeactivate() {
        return simpleDateDeactivate;
    }

    public void setSimpleDateDeactivate(Date simpleDateDeactivate) {
        this.simpleDateDeactivate = simpleDateDeactivate;
    }
}
