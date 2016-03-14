/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.front.items;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 11.03.16
 * Time: 17:57
 * To change this template use File | Settings | File Templates.
 */
public class ClientDescV2 {
    protected ClientDescItemParamList clientDescParams;

    public ClientDescItemParamList getClientDescParams() {
        return clientDescParams;
    }

    public void setClientDescParams(ClientDescItemParamList clientDescParams) {
        this.clientDescParams = clientDescParams;
    }

    public static class ClientDescItemParam {
        public String paramName;
        public String paramValue;
    }

    public static class ClientDescItemParamList {
        protected List<ClientDescItemParam> param;

        public List<ClientDescItemParam> getParam() {
            return param;
        }

        public void setParam(List<ClientDescItemParam> param) {
            this.param = param;
        }
    }
}
