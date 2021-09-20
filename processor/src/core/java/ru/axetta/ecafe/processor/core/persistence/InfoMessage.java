/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;
import java.util.Set;

/**
 * Created by i.semenov on 31.03.2017.
 */
public class InfoMessage {
    private Long idOfInfoMessage;
    private InfoMessageType mtype;
    private String header;
    private String content;
    private Date createdDate;
    private Long version;
    private User user;
    private Set<InfoMessageDetail> infoMessageDetails;

    public InfoMessage() {

    }

    public Long getIdOfInfoMessage() {
        return idOfInfoMessage;
    }

    public void setIdOfInfoMessage(Long idOfInfoMessage) {
        this.idOfInfoMessage = idOfInfoMessage;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<InfoMessageDetail> getInfoMessageDetails() {
        return infoMessageDetails;
    }

    public void setInfoMessageDetails(Set<InfoMessageDetail> infoMessageDetails) {
        this.infoMessageDetails = infoMessageDetails;
    }

    public InfoMessageType getMtype() {
        return mtype;
    }

    public void setMtype(InfoMessageType mtype) {
        this.mtype = mtype;
    }
}
