/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created with IntelliJ IDEA.
 * User: a.anvarov
 */

public class UserOrgs {

    private Long idOfUserOrg;
    private User user;
    private Org org;
    private UserNotificationType userNotificationType;

    public UserNotificationType getUserNotificationType() {
        return userNotificationType;
    }

    public void setUserNotificationType(UserNotificationType userNotificationType) {
        this.userNotificationType = userNotificationType;
    }

    public UserOrgs() {
    }

    public UserOrgs(User user, Org org, UserNotificationType userNotificationType) {
        this.user = user;
        this.org = org;
        this.userNotificationType = userNotificationType;
    }

    public Long getIdOfUserOrg() {
        return idOfUserOrg;
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

    public void setIdOfUserOrg(Long idOfUserOrg) {
        this.idOfUserOrg = idOfUserOrg;
    }

    //public Long getUser() {
    //    return user;
    //}
    //
    //public void setUser(Long user) {
    //    this.user = user;
    //}
    //
    //public Long getOrg() {
    //    return org;
    //}
    //
    //public void setOrg(Long org) {
    //    this.org = org;
    //}
}
