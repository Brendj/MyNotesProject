/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import org.hibernate.Session;

import java.util.Date;

/**
 * Created by Liya on 01.04.2016.
 */
public class JournalApplicationForFood {
    private Long idofoperation;
    private Long idofapplicationforfood;
    private OperationtypeForJournalApplication operationtype;
    private User user;
    private Date createddate;


    public JournalApplicationForFood() {
    }

    public void saveJournalApplicationForFood(Session session, User user, Long idofapplicationforfood, OperationtypeForJournalApplication typeoperation) {
        setCreateddate(new Date());
        setOperationtype(typeoperation);
        setIdofapplicationforfood(idofapplicationforfood);
        setUser(user);
        session.persist(this);
    }

    public Long getIdofoperation() {
        return idofoperation;
    }

    public void setIdofoperation(Long idofoperation) {
        this.idofoperation = idofoperation;
    }

    public Long getIdofapplicationforfood() {
        return idofapplicationforfood;
    }

    public void setIdofapplicationforfood(Long idofapplicationforfood) {
        this.idofapplicationforfood = idofapplicationforfood;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getCreateddate() {
        return createddate;
    }

    public void setCreateddate(Date createddate) {
        this.createddate = createddate;
    }

    public OperationtypeForJournalApplication getOperationtype() {
        return operationtype;
    }

    public void setOperationtype(OperationtypeForJournalApplication operationtype) {
        this.operationtype = operationtype;
    }
}
