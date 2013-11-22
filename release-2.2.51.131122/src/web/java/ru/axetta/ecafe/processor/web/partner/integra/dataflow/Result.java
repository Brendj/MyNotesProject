/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

public class Result {
    public Long resultCode;
    public String description;

    public Result(Long resultCode, String desc) {
        this.resultCode = resultCode;
        this.description = desc;
    }
    public Result() {}
}
