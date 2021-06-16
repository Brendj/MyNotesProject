/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

/**
 * Created by a.voinov on 16.06.2021.
 */

package ru.axetta.ecafe.processor.web.internal.esp;

import java.util.Date;

public class ResponseESPRequestsPOJO {
    private Date dateRequest; //Дата подачи обращения
    private String meshGuid; //Клиент
    private String email; //Почта
    private Long idOfOrg; //Организация
    private String topic; //Тема обращения
    private String message; //Обращение
    /////////////////////
    private String numberrequest; //Номер обращения
    private Date updateDate; //Дата обновления
    private String status; //Статус
    private String solution; //Принятое решение

    public Date getDateRequest() {
        return dateRequest;
    }

    public void setDateRequest(Date dateRequest) {
        this.dateRequest = dateRequest;
    }

    public String getMeshGuid() {
        return meshGuid;
    }

    public void setMeshGuid(String meshGuid) {
        this.meshGuid = meshGuid;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNumberrequest() {
        return numberrequest;
    }

    public void setNumberrequest(String numberrequest) {
        this.numberrequest = numberrequest;
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

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }
}
