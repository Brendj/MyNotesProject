/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

public class SfcUserOrgs {

    private Long idOfSfcUserOrg;
    private User user;
    private Org org;

    public SfcUserOrgs() {
    }

    public SfcUserOrgs(User user, Org org) {
        this.user = user;
        this.org = org;
    }

    public Long getIdOfSfcUserOrg() {
        return idOfSfcUserOrg;
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

    public void setIdOfSfcUserOrg(Long idOfSfcUserOrg) {
        this.idOfSfcUserOrg = idOfSfcUserOrg;
    }

}
