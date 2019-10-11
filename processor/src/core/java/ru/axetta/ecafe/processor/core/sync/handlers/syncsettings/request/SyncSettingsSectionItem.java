/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.syncsettings.request;

import ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.ConcreteTime;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.SyncSettings;
import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.*;

public class SyncSettingsSectionItem implements AbstractToElement {
    private Integer contentType;
    private List<String> concreteTime = new LinkedList<>();
    private Integer everySeconds;
    private Integer limitStartHour;
    private Integer limitEndHour;
    private Boolean monday;
    private Boolean tuesday;
    private Boolean wednesday;
    private Boolean thursday;
    private Boolean friday;
    private Boolean saturday;
    private Boolean sunday;
    private Long version;
    private Boolean deleteState;

    private static final Comparator<String> comparable = new Comparator<String>() {
        @Override
        public int compare(String o1, String o2) {
            String[] oa1 = o1.split(":");
            String[] oa2 = o2.split(":");
            int res = oa1[0].compareTo(oa2[0]);

            return res == 0 ? oa1[1].compareTo(oa2[1]) : res;
        }
    };

    public SyncSettingsSectionItem(){

    }

    public SyncSettingsSectionItem(SyncSettings setting) {
        this.contentType = setting.getContentType().getTypeCode();
        this.concreteTime = buildConcreteTimeString(setting.getConcreteTime());
        this.everySeconds = setting.getEverySecond();
        this.limitStartHour = setting.getLimitStartHour();
        this.limitEndHour = setting.getLimitEndHour();
        this.monday = setting.getMonday();
        this.tuesday = setting.getTuesday();
        this.wednesday = setting.getWednesday();
        this.thursday = setting.getThursday();
        this.friday = setting.getFriday();
        this.saturday = setting.getSaturday();
        this.sunday = setting.getSunday();
        this.version = setting.getVersion();
        this.deleteState = setting.getDeleteState();
    }

    private List<String> buildConcreteTimeString(Set<ConcreteTime> concreteTime) {
        List<String> result = new LinkedList<>();
        for(ConcreteTime time : concreteTime){
            result.add(time.getConcreteTime());
        }
        Collections.sort(result, comparable);
        return result;
    }

    @Override
    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("SS");

        element.setAttribute("ContentType", contentType.toString());
        element.setAttribute("ConcreteTime", StringUtils.join(concreteTime, ";"));
        if(everySeconds != null) {
            element.setAttribute("EverySeconds", everySeconds.toString());
        }
        if(limitStartHour != null && limitEndHour != null) {
            element.setAttribute("LimitStartHour", limitStartHour.toString());
            element.setAttribute("LimitEndHour", limitEndHour.toString());
        }
        element.setAttribute("Monday", setBooleanAsBit(monday));
        element.setAttribute("Tuesday", setBooleanAsBit(tuesday));
        element.setAttribute("Wednesday", setBooleanAsBit(wednesday));
        element.setAttribute("Thursday", setBooleanAsBit(thursday));
        element.setAttribute("Friday", setBooleanAsBit(friday));
        element.setAttribute("Saturday", setBooleanAsBit(saturday));
        element.setAttribute("Sunday", setBooleanAsBit(sunday));
        element.setAttribute("V", version.toString());
        element.setAttribute("D", setBooleanAsBit(deleteState));

        return element;
    }

    private String setBooleanAsBit(Boolean val){
        return val ? "1" : "0";
    }

    public Integer getContentType() {
        return contentType;
    }

    public void setContentType(Integer contentType) {
        this.contentType = contentType;
    }

    public List<String> getConcreteTime() {
        return concreteTime;
    }

    public void setConcreteTime(List<String> concreteTime) {
        this.concreteTime = concreteTime;
    }

    public Integer getEverySeconds() {
        return everySeconds;
    }

    public void setEverySeconds(Integer everySeconds) {
        this.everySeconds = everySeconds;
    }

    public Integer getLimitStartHour() {
        return limitStartHour;
    }

    public void setLimitStartHour(Integer limitStartHour) {
        this.limitStartHour = limitStartHour;
    }

    public Integer getLimitEndHour() {
        return limitEndHour;
    }

    public void setLimitEndHour(Integer limitEndHour) {
        this.limitEndHour = limitEndHour;
    }

    public Boolean getMonday() {
        return monday;
    }

    public void setMonday(Boolean monday) {
        this.monday = monday;
    }

    public Boolean getTuesday() {
        return tuesday;
    }

    public void setTuesday(Boolean tuesday) {
        this.tuesday = tuesday;
    }

    public Boolean getWednesday() {
        return wednesday;
    }

    public void setWednesday(Boolean wednesday) {
        this.wednesday = wednesday;
    }

    public Boolean getThursday() {
        return thursday;
    }

    public void setThursday(Boolean thursday) {
        this.thursday = thursday;
    }

    public Boolean getFriday() {
        return friday;
    }

    public void setFriday(Boolean friday) {
        this.friday = friday;
    }

    public Boolean getSaturday() {
        return saturday;
    }

    public void setSaturday(Boolean saturday) {
        this.saturday = saturday;
    }

    public Boolean getSunday() {
        return sunday;
    }

    public void setSunday(Boolean sunday) {
        this.sunday = sunday;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getDeleteState() {
        return deleteState;
    }

    public void setDeleteState(Boolean deleteState) {
        this.deleteState = deleteState;
    }
}
