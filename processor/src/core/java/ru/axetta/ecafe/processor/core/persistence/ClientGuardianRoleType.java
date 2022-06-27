/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

public enum ClientGuardianRoleType {

    PARENT(1, "Родитель"),
    GUARDIAN(2, "Опекун"),
    TRUSTEE(3, "Попечитель"),
    GUARDIAN_REPRESENTATIVE(4, "Представитель органа опеки и попечительства"),
    TRUSTED_REPRESENTATIVE(5, "Доверенный представитель");

    private final String description;
    private final int code;
    static Map<Integer, ClientGuardianRoleType> map = new HashMap<>();

    static {
        for (ClientGuardianRoleType role : ClientGuardianRoleType.values()) {
            map.put(role.code, role);
        }
    }

    ClientGuardianRoleType(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public static ClientGuardianRoleType fromInteger(Integer value){
        if (value == null)
            return null;
        return map.get(value);
    }

    @Override
    public String toString() {
        return description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

}
