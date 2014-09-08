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

    public void internalError(){
        resultCode = ResultConst.CODE_INTERNAL_ERROR;
        description = ResultConst.DESCR_INTERNAL_ERROR;
    }

    public void notFound(){
        resultCode = ResultConst.CODE_NOT_FOUND;
        description = ResultConst.DESCR_NOT_FOUND;
    }
}
