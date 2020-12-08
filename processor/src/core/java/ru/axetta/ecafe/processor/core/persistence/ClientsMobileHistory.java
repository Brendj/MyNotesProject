/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Good;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodGroup;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 27.11.15
 * Time: 12:06
 * To change this template use File | Settings | File Templates.
 */
public class ClientsMobileHistory {
    private Long ifofclientsmobilehistory;
    private Client client;
    private String oldmobile;
    private String newmobile;
    private String action;
    private Date createdate;
    private String source;
    private String showing;
    private User user;
    private Org org;

    private static final Logger logger = LoggerFactory.getLogger(ClientsMobileHistory.class);

    public ClientsMobileHistory(String source)
    {
        this.setSource(source);
    }
    public ClientsMobileHistory()
    { }
    public void saveClientMobileHistoryInDB ()
    {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            ClientsMobileHistory clientsMobileHistory1 = new ClientsMobileHistory();
            clientsMobileHistory1.setNewmobile(this.getNewmobile());
            clientsMobileHistory1.setOldmobile(this.getOldmobile());
            clientsMobileHistory1.setCreatedate(this.getCreatedate());
            clientsMobileHistory1.setClient(this.getClient());
            clientsMobileHistory1.setSource(this.getSource());
            clientsMobileHistory1.setAction(this.getAction());
            clientsMobileHistory1.setOrg(this.getOrg());
            clientsMobileHistory1.setShowing(this.getShowing());
            clientsMobileHistory1.setUser(this.getUser());
            persistenceSession.persist(clientsMobileHistory1);
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public String getOldmobile() {
        return oldmobile;
    }

    public void setOldmobile(String oldmobile) {
        this.oldmobile = oldmobile;
    }

    public String getNewmobile() {
        return newmobile;
    }

    public void setNewmobile(String newmobile) {
        this.newmobile = newmobile;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Long getIfofclientsmobilehistory() {
        return ifofclientsmobilehistory;
    }

    public void setIfofclientsmobilehistory(Long ifofclientsmobilehistory) {
        this.ifofclientsmobilehistory = ifofclientsmobilehistory;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getShowing() {
        return showing;
    }

    public void setShowing(String showing) {
        this.showing = showing;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Org getOrg() {
        return org;
    }

    public void setOrg(Org org) {
        this.org = org;
    }
}
