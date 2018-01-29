/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync;

import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 20.03.13
 * Time: 14:18
 * To change this template use File | Settings | File Templates.
 */
public enum SyncType {

    TYPE_FULL(0,"Full"),
    TYPE_GET_ACC_INC(1,"GetAccInc"),
    TYPE_GET_CLIENTS_PARAMS(2,"GetClientParams"),
    TYPE_GET_GET_ACC_REGISGTRY_UPDATE(3,"GetAccRegisgtryUpdate"),
    TYPE_COMMODITY_ACCOUNTING(4,"CommodityAccounting"),
    TYPE_REESTR_TALOONS_APPROVAL(6,"ReestrTaloonsApproval"),
    TYPE_ZERO_TRANSACTIONS(7, "ZeroTransactions"),
    TYPE_MIGRANTS(8, "Migrants"),
    TYPE_HELP_REQUESTS(13, "HelpRequests"),
    TYPE_CONSTRUCTED(20,"ConstructedSections");

    private static Map<String,SyncType> map = new HashMap<String,SyncType>();
    static {
        for (SyncType syncTypes : SyncType.values()) {
            map.put(syncTypes.description, syncTypes);
        }
    }

    private int value;
    private String description;

    private SyncType(int value, String description) {
        this.value = value;
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public int getValue() {
        return value;
    }

    public static SyncType parse(String description){
        return map.get(description);
    }
}
