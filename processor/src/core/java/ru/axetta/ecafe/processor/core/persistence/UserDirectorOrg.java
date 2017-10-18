/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserDirectorOrg {
    protected static Logger logger;
    static {
        try {  logger = LoggerFactory.getLogger(User.class); } catch (Throwable ignored) {}
    }

    private Long idOfUserDirectorOrg;
    private User user;
    private Org org;

    public UserDirectorOrg() {
    }

    public UserDirectorOrg(User user, Org org) {
        this.user = user;
        this.org = org;
    }

    public Long getIdOfUserDirectorOrg() {
        return idOfUserDirectorOrg;
    }

    public void setIdOfUserDirectorOrg(Long idOfUserDirectorOrg) {
        this.idOfUserDirectorOrg = idOfUserDirectorOrg;
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
