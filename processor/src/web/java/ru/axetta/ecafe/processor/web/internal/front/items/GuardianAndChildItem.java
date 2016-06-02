/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.front.items;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Liya
 * Date: 31.05.16
 * Time: 11:37
 */
public class GuardianAndChildItem {
    private Long idOfClient;
    private Long idOfOrg;
    private String fullname;
    // Заполняется для опекуна, который подал запрос по карте
    private List<Long> idOfChildren = new ArrayList<Long>();
    // Заполняется для опекаемых, чей опекун подал запрос по карте
    private List<Long> idOfGuardian = new ArrayList<Long>();

    public GuardianAndChildItem() {
    }

    public GuardianAndChildItem(Long idOfClient, Long idOfOrg, String fullname) {
        this.idOfClient = idOfClient;
        this.idOfOrg = idOfOrg;
        this.fullname = fullname;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public List<Long> getIdOfChildren() {
        return idOfChildren;
    }

    public void setIdOfChildren(List<Long> idOfChildren) {
        this.idOfChildren = idOfChildren;
    }

    public List<Long> getIdOfGuardian() {
        return idOfGuardian;
    }

    public void setIdOfGuardian(List<Long> idOfGuardian) {
        this.idOfGuardian = idOfGuardian;
    }
}
