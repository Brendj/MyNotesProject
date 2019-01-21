/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.front.items;

import ru.axetta.ecafe.processor.web.internal.ResponseItem;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.LinkedList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
public class ClientResponse extends ResponseItem {
    public static class ClientResultItemParam {
        @XmlElement(name="paramName")
        public String paramName;

        @XmlElement(name="paramValue")
        public String paramValue;

        public ClientResultItemParam() {}

        public ClientResultItemParam(String paramName, String paramValue) {
            this.paramName = paramName;
            this.paramValue = paramValue;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ClientResult {

        @XmlElement(name="param")
        protected List<ClientResultItemParam> param;

        public ClientResult() {
            param = new LinkedList<ClientResultItemParam>();
        }

        public List<ClientResultItemParam> getParam() {
            return param;
        }

        public void setParam(List<ClientResultItemParam> param) {
            this.param = param;
        }
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class ClientResultList {

        @XmlElement(name="clientResult")
        protected List<ClientResult> clientResultList;

        public ClientResultList() {
            clientResultList = new LinkedList<ClientResult>();
        }

        public List<ClientResult> getClientResultList() {
            return clientResultList;
        }

        public void setClientResultList(List<ClientResult> clientResultList) {
            this.clientResultList = clientResultList;
        }
    }

    @XmlElement(name="clientResultList")
    protected List<ClientResultList> clientResultList;


    public ClientResponse() {
        clientResultList = new LinkedList<ClientResultList>();
    }

    public List<ClientResultList> getClientResultList() {
        return clientResultList;
    }

    public void setClientResultList(List<ClientResultList> clientResultList) {
        this.clientResultList = clientResultList;
    }
}
