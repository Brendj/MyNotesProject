/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 26.07.12
 * Time: 16:10
 * To change this template use File | Settings | File Templates.
 */
public class CheckPasswordResult {
    public boolean succeeded;
    public Long resultCode;
    public String description;
    public CheckPasswordResult(boolean succeeded,Long resultCode, String desc) {
        this.succeeded=succeeded;
        this.resultCode = resultCode;
        this.description = desc;
    }
    public CheckPasswordResult() {}
}
