/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

/**
 * Created by nuc on 04.04.2019.
 */
public class LogService {
    private Long idOfLogPacket;
    private LogServiceType idOfService;
    private Date createdDate;
    private String packetBody;
    private String responseBody;

    public LogService() {

    }

    public LogService(LogServiceType idOfService, String packetBody, String responseBody) {
        this.idOfService= idOfService;
        this.packetBody = packetBody;
        this.responseBody = responseBody;
        this.createdDate = new Date();
    }

    public Long getIdOfLogPacket() {
        return idOfLogPacket;
    }

    public void setIdOfLogPacket(Long idOfLogPacket) {
        this.idOfLogPacket = idOfLogPacket;
    }

    public LogServiceType getIdOfService() {
        return idOfService;
    }

    public void setIdOfService(LogServiceType idOfService) {
        this.idOfService = idOfService;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getPacketBody() {
        return packetBody;
    }

    public void setPacketBody(String packetBody) {
        this.packetBody = packetBody;
    }

    public String getResponseBody() {
        return responseBody;
    }

    public void setResponseBody(String responseBody) {
        this.responseBody = responseBody;
    }
}
