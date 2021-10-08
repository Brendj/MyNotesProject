/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

/**
 * Created by a.voinov on 16.06.2021.
 */

package ru.axetta.ecafe.processor.web.internal.esp;

import java.util.Date;
import java.util.List;

public class ESPRequest {
    private Date dateRequest; //Дата подачи обращения
    private Long idOfClient; //Клиент
    private String email; //Почта
    private Long idOfOrg; //Организация
    private String topic; //Тема обращения
    private String message; //Обращение
    private List<ESPRequestAttachedFile> attached; //Доп файлы

    public Date getDateRequest() {
        return dateRequest;
    }

    public void setDateRequest(Date dateRequest) {
        this.dateRequest = dateRequest;
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

    public List<ESPRequestAttachedFile> getAttached() {
        return attached;
    }

    public void setAttached(List<ESPRequestAttachedFile> attached) {
        this.attached = attached;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }
}
