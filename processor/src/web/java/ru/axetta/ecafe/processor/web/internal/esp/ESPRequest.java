/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

/**
 * Created by a.voinov on 27.10.2020.
 */

package ru.axetta.ecafe.processor.web.internal.esp;

import ru.axetta.ecafe.processor.web.partner.library.JsonLibraryDeSerializer;

import org.codehaus.jackson.map.annotate.JsonDeserialize;

import java.util.Date;

@JsonDeserialize(using = JsoneEspDeSerializer.class)
public class ESPRequest {
    private Date dateRequest; //Дата подачи обращения
    private String meshGuid; //Клиент
    private String email; //Почта
    private Long idOfOrg; //Организация
    private String topic; //Тема обращения
    private String message; //Обращение

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

    public String getMeshGuid() {
        return meshGuid;
    }

    public void setMeshGuid(String meshGuid) {
        this.meshGuid = meshGuid;
    }
}
