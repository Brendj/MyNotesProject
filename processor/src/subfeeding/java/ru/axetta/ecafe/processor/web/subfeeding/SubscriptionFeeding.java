/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.subfeeding;

import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.SubscriptionFeedingExt;
import ru.axetta.ecafe.processor.web.partner.integra.dataflow.SubscriptionFeedingJournalExt;

import java.io.Serializable;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 11.06.14
 * Time: 16:17
 * To change this template use File | Settings | File Templates.
 */
public class SubscriptionFeeding implements Serializable {

    private Long idOfSubscriptionFeeding;
    private String guid;
    private Date dateCreateService;
    private Date dateActivateSubscription;
    private Date lastDatePause;
    private Date dateDeactivate;
    private Date updateDate;
    private Boolean suspended;
    private String changesPlace;

    public SubscriptionFeedingExt toSubscriptionFeedingExt(){
        SubscriptionFeedingExt subscriptionFeedingExt = new SubscriptionFeedingExt();
        subscriptionFeedingExt.setIdOfSubscriptionFeeding(idOfSubscriptionFeeding);
        subscriptionFeedingExt.setGuid(guid);
        subscriptionFeedingExt.setDateCreateService(dateCreateService);
        subscriptionFeedingExt.setDateActivateSubscription(dateActivateSubscription);
        subscriptionFeedingExt.setLastDatePauseSubscription(lastDatePause);
        subscriptionFeedingExt.setDateDeactivateService(dateDeactivate);
        subscriptionFeedingExt.setUpdateDate(updateDate);
        subscriptionFeedingExt.setWasSuspended(suspended);
        return subscriptionFeedingExt;
    }

    public SubscriptionFeeding(SubscriptionFeedingExt subscriptionFeedingExt) {
        this.idOfSubscriptionFeeding = subscriptionFeedingExt.getIdOfSubscriptionFeeding();
        this.guid = subscriptionFeedingExt.getGuid();
        this.dateCreateService = subscriptionFeedingExt.getDateCreateService();
        this.dateActivateSubscription = subscriptionFeedingExt.getDateActivateSubscription();
        this.lastDatePause = subscriptionFeedingExt.getLastDatePauseSubscription();
        this.dateDeactivate = subscriptionFeedingExt.getDateDeactivateService();
        this.updateDate = subscriptionFeedingExt.getUpdateDate();
        this.suspended = subscriptionFeedingExt.getWasSuspended();
        this.changesPlace = subscriptionFeedingExt.getChangesPlace() ? "АРМ Администратора" : "Личный кабинет";
    }

    public SubscriptionFeeding(SubscriptionFeedingJournalExt subscriptionFeedingExt) {
        this.idOfSubscriptionFeeding = subscriptionFeedingExt.getIdOfSubscriptionFeeding();
        this.guid = subscriptionFeedingExt.getGuid();
        this.dateCreateService = subscriptionFeedingExt.getDateCreateService();
        this.dateActivateSubscription = subscriptionFeedingExt.getDateActivateSubscription();
        this.lastDatePause = subscriptionFeedingExt.getLastDatePauseSubscription();
        this.dateDeactivate = subscriptionFeedingExt.getDateDeactivateService();
        this.updateDate = subscriptionFeedingExt.getUpdateDate();
        this.suspended = subscriptionFeedingExt.getWasSuspended();
        this.changesPlace = subscriptionFeedingExt.getChangesPlace() ? "АРМ Администратора" : "Личный кабинет";
    }

    public Date getSubscriptionActionDate(){
        if(dateActivateSubscription ==null){
            return dateCreateService;
        } else {
            if(lastDatePause==null){
                return dateActivateSubscription;
            } else {
                return lastDatePause;
            }
        }
    }

    public String getSubscriptionAction(){
        Date currentDate = new Date();
        if(dateActivateSubscription ==null) return "Создание услуги АП";
        if(dateActivateSubscription.getTime()>currentDate.getTime() && lastDatePause==null) return "Возобновление подписки на услугу АП";
        if(dateActivateSubscription.getTime()<currentDate.getTime() && lastDatePause==null) return "Начало подписки на услугу АП";
        if(lastDatePause!=null && lastDatePause.getTime()>currentDate.getTime()) return "Прекращение услуги АП";
        if(lastDatePause!=null && lastDatePause.getTime()<currentDate.getTime()) return "Приостановка подписки на услугу АП";
        return "";
    }

    public String getSubscriptionState(){
        Date currentDate = new Date();
        if(dateActivateSubscription ==null) return "Услуга подключена";
        if(dateActivateSubscription.getTime()>currentDate.getTime() && lastDatePause==null) return "Ожидает активации";
        if(dateActivateSubscription.getTime()<currentDate.getTime() && lastDatePause==null) return "Активна";
        if(lastDatePause!=null && lastDatePause.getTime()>currentDate.getTime()) return "Ожидает прекращения";
        if(lastDatePause!=null && lastDatePause.getTime()<currentDate.getTime()) return "Приостановлена";
        return "";
    }

    public String getSubscriptionStateWithDate(){
        Date currentDate = new Date();
        if(dateActivateSubscription ==null) return "Услуга подключена";
        if(dateActivateSubscription.getTime()>currentDate.getTime() && lastDatePause==null)
            return String.format("Ожидает активации c %s", CalendarUtils.dateShortToString(dateActivateSubscription));
        if(dateActivateSubscription.getTime()<currentDate.getTime() && lastDatePause==null)
            return "Активна";
        if(lastDatePause!=null && lastDatePause.getTime()>currentDate.getTime())
            return String.format("Ожидает прекращения c %s", CalendarUtils.dateShortToString(lastDatePause));
        if(lastDatePause!=null && lastDatePause.getTime()<currentDate.getTime())
            return "Приостановлена";
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

    public Date getDateActivateSubscription() {
        return dateActivateSubscription;
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

    public String getChangesPlace() {
        return changesPlace;
    }

    public void setChangesPlace(String changesPlace) {
        this.changesPlace = changesPlace;
    }
}
