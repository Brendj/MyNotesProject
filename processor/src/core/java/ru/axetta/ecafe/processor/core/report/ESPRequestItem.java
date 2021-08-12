/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Voinov
 * Date: 22.06.21
 * To change this template use File | Settings | File Templates.
 */
public class ESPRequestItem {
    private Date createDate;
    private Date updateDate;
    private String status;
    private String numberReqeust;
    private Long idOfOrg;
    private String shortAddress;
    private String shortName;
    private String topic;
    private String fio;
    private String phone;


    public ESPRequestItem(Date createDate, Date updateDate, String status, String numberReqeust, Long idOfOrg,
            String shortAddress, String shortName, String topic, String fio, String phone) {
        this.createDate = createDate;
        this.updateDate = updateDate;
        this.status = status;
        this.numberReqeust=numberReqeust;
        this.idOfOrg=idOfOrg;
        this.shortAddress=shortAddress;
        this.shortName=shortName;
        this.topic=topic;
        this.fio=fio;
        this.phone=phone;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNumberReqeust() {
        return numberReqeust;
    }

    public void setNumberReqeust(String numberReqeust) {
        this.numberReqeust = numberReqeust;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getShortAddress() {
        return shortAddress;
    }

    public void setShortAddress(String shortAddress) {
        this.shortAddress = shortAddress;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getFio() {
        return fio;
    }

    public void setFio(String fio) {
        this.fio = fio;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}