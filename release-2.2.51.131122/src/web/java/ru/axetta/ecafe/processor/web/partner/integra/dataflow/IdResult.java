/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 27.07.12
 * Time: 13:20
 * To change this template use File | Settings | File Templates.
 */
public class IdResult {
    public Long id;
    public Long resultCode;
    public String description;
    public IdResult(){}
    public IdResult(Long id, Long resultCode, String description){
        this.id=id;
        this.resultCode=resultCode;
        this.description=description;
    }
}
