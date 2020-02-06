/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

public class ClientGroupResult extends Result {
    public static final Integer PARENT = 1;
    public static final Integer PARENT_EMPLOYEE = 2;
    public static final Integer EMPLOYEE = 3;
    public static final Integer STUDENT = 4;

    private Integer value;

    public ClientGroupResult() { }

    public ClientGroupResult(Long resultCode, String desc) {
        super(resultCode, desc);
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }
}
