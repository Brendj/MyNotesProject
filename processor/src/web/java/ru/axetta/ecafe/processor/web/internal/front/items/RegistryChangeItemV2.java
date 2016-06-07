/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.front.items;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Anvarov
 * Date: 11.04.16
 * Time: 16:30
 * To change this template use File | Settings | File Templates.
 */
public class RegistryChangeItemV2 {

    private List<RegistryChangeItemParam> list;

    public List<RegistryChangeItemParam> getList() {
        return list;
    }

    public void setList(List<RegistryChangeItemParam> list) {
        this.list = list;
    }
}
