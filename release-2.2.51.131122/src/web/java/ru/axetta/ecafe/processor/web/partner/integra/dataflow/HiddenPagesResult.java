/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 10.09.12
 * Time: 16:03
 * To change this template use File | Settings | File Templates.
 */
public class HiddenPagesResult {
    public String hiddenPages;
    public Long resultCode;
    public String description;
    public HiddenPagesResult(){}
    public HiddenPagesResult(String hiddenPages, Long resultCode, String description){
        this.hiddenPages=hiddenPages;
        this.resultCode=resultCode;
        this.description=description;
    }
}
