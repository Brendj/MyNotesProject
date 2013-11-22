/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 26.07.12
 * Time: 15:52
 * To change this template use File | Settings | File Templates.
 */
public class SendResult {
       public int recoverStatus;
       public Long resultCode;
       public String description;
    public SendResult(int recoverStatus,Long resultCode, String desc) {
        this.recoverStatus=recoverStatus;
        this.resultCode = resultCode;
        this.description = desc;
    }
    public SendResult() {}
}
