/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.front.items;

/**
 * Created with IntelliJ IDEA.
 * User: Anvarov
 * Date: 13.04.16
 * Time: 14:17
 * To change this template use File | Settings | File Templates.
 */
public class RegistryChangeItemParam {

    public String fieldNameParam;
    public String fieldValueParam;

    public RegistryChangeItemParam() {
    }

    public RegistryChangeItemParam(String fieldNameParam, String fieldValueParam) {
        this.fieldNameParam = fieldNameParam;
        this.fieldValueParam = fieldValueParam;
    }
}
