/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by i.semenov on 30.05.2019.
 */
public enum RnipRequestType {
    SEND_REQUEST(0, "SendRequestRequest"),
    GET_RESPONSE(1, "GetResponseRequest"),
    ACK(2, "AckRequest");

    private final Integer code;
    private final String description;
    private static Map<Integer, RnipRequestType> mapInt = new HashMap<Integer,RnipRequestType>();
    private static Map<String, RnipRequestType> mapStr = new HashMap<String,RnipRequestType>();
    static {
        for (RnipRequestType value : RnipRequestType.values()) {
            mapInt.put(value.getCode(), value);
            mapStr.put(value.toString(), value);
        }
    }

    RnipRequestType(Integer code, String description) {
        this.code = code;
        this.description = description;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public static RnipRequestType fromInteger(Integer id) {
        return mapInt.get(id);
    }

    public static RnipRequestType fromString(String description) {
        return mapStr.get(description);
    }
}
