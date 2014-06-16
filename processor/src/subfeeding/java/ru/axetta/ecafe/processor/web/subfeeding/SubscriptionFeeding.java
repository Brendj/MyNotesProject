/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.subfeeding;

import ru.axetta.ecafe.processor.web.partner.integra.dataflow.SubscriptionFeedingExt;

import java.io.Serializable;
import java.util.Comparator;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 11.06.14
 * Time: 16:17
 * To change this template use File | Settings | File Templates.
 */
public class SubscriptionFeeding implements Serializable {

    static class SubscriptionFeedingCompareByUpdateDate implements Comparator<SubscriptionFeeding> {

        @Override
        public int compare(SubscriptionFeeding o1, SubscriptionFeeding o2) {
            return o1.updateDate.compareTo(o2.updateDate);
        }
    }

    public static SubscriptionFeedingCompareByUpdateDate buildUpdateDateComparator(){
        return new SubscriptionFeedingCompareByUpdateDate();
    }

    private Long idOfSubscriptionFeeding;
    private String guid;
    private Date dateCreateService;
    private Date dateActivate;
    private Date lastDatePause;
    private Date dateDeactivate;
    private Date updateDate;
    private Boolean suspended;

    public SubscriptionFeeding(SubscriptionFeedingExt subscriptionFeedingExt) {
        this.idOfSubscriptionFeeding = subscriptionFeedingExt.getIdOfSubscriptionFeeding();
        this.guid = subscriptionFeedingExt.getGuid();
        this.dateCreateService = subscriptionFeedingExt.getDateCreateService();
        this.dateActivate = subscriptionFeedingExt.getDateActivate();
        this.lastDatePause = subscriptionFeedingExt.getLastDatePause();
        this.dateDeactivate = subscriptionFeedingExt.getDateDeactivate();
        this.updateDate = subscriptionFeedingExt.getUpdateDate();
        this.suspended = subscriptionFeedingExt.getSuspended();
    }

    public Date getSubscriptionActionDate(){
        if(dateActivate==null){
            return dateCreateService;
        } else {
            if(lastDatePause==null){
                return dateActivate;
            } else {
                return lastDatePause;
            }
        }
    }

    public String getSubscriptionAction(){
        Date currentDate = new Date();
        if(dateActivate==null) return "Создание услуги АП";
        if(dateActivate.getTime()>currentDate.getTime() && lastDatePause==null) return "Возобновление подписки на услугу АП";
        if(dateActivate.getTime()<currentDate.getTime() && lastDatePause==null) return "Начало подписки на услугу АП";
        if(lastDatePause!=null && lastDatePause.getTime()>currentDate.getTime()) return "Прекращение услуги АП";
        if(lastDatePause!=null && lastDatePause.getTime()<currentDate.getTime()) return "Приостановка подписки на услугу АП";
        return "";
    }

    public String getSubscriptionState(){
        Date currentDate = new Date();
        if(dateActivate==null) return "Услуга подключена";
        if(dateActivate.getTime()>currentDate.getTime() && lastDatePause==null) return "Ожидает активации";
        if(dateActivate.getTime()<currentDate.getTime() && lastDatePause==null) return "Активна";
        if(lastDatePause!=null && lastDatePause.getTime()>currentDate.getTime()) return "Ожидает прекращения";
        if(lastDatePause!=null && lastDatePause.getTime()<currentDate.getTime()) return "Приостановлена";
        return "";
    }

    public Long getIdOfSubscriptionFeeding() {
        return idOfSubscriptionFeeding;
    }

    public String getGuid() {
        return guid;
    }

    public Date getDateCreateService() {
        return dateCreateService;
    }

    public Date getDateActivate() {
        return dateActivate;
    }

    public Date getLastDatePause() {
        return lastDatePause;
    }

    public Date getDateDeactivate() {
        return dateDeactivate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public Boolean getSuspended() {
        return suspended;
    }
}
