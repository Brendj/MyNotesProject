/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.rest.items;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 05.05.14
 * Time: 13:23
 * To change this template use File | Settings | File Templates.
 */
public abstract class Result {

    private Long resultCode = 0L;
    private String description = "OK";

    public Result(Long resultCode, String desc) {
        this.resultCode = resultCode;
        this.description = desc;
    }
    public Result() {}

}
