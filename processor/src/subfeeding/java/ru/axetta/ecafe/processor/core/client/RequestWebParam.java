/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.client;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 26.07.12
 * Time: 15:11
 * To change this template use File | Settings | File Templates.
 */
public class RequestWebParam {
   public String url;
   public String pParam;
   public String dateParam;
   public String contractIdParam;
    public RequestWebParam(String url, String pParam, String dateParam, String contractIdParam){
        this.url=url;
        this.pParam=pParam;
        this.dateParam=dateParam;
        this.contractIdParam=contractIdParam;

    }
    public RequestWebParam(){};
}
