/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.service.user;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 15.07.15
 * Time: 10:42
 */

public class UserContragentsOrgsModel {

    private Long userId;
    private String roleName;
    private Map<Contragent, Set<Org>> userContragentsOrgsMap = new HashMap<Contragent, Set<Org>>();

    public UserContragentsOrgsModel() {
    }

    public UserContragentsOrgsModel(Long userId, String roleName, Map<Contragent, Set<Org>> userContragentOrgsMap) {
        this.userId = userId;
        this.roleName = roleName;
        this.userContragentsOrgsMap = userContragentOrgsMap;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public Map<Contragent, Set<Org>> getUserContragentsOrgsMap() {
        return userContragentsOrgsMap;
    }

    public void setUserContragentsOrgsMap(Map<Contragent, Set<Org>> userContragentsOrgsMap) {
        this.userContragentsOrgsMap = userContragentsOrgsMap;
    }
}
