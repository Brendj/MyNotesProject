/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.front.items;

import java.util.List;

public abstract class ClientField {

    public static class ClientFieldItemParam {
        public String paramName;
        public String paramValue;
    }

    protected List<ClientFieldItemParam> param;

    public List<ClientFieldItemParam> getParam() {
        return param;
    }

    public void setParam(List<ClientFieldItemParam> param) {
        this.param = param;
    }
}
