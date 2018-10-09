/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.LinkedList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CardInfo")
public class CardInfo  extends Result{
    @XmlElement(name = "orgEnabledMultiCardMod")
    private Boolean orgEnabledMultiCardMod;

    @XmlElement(name = "clientHasActiveMultiCardMode")
    private Boolean clientHasActiveMultiCardMode;

    @XmlElement(name = "items")
    private List<CardInfoItem> items;

    public List<CardInfoItem> getItems() {
        if(this.items == null){
            this.items = new LinkedList<CardInfoItem>();
        }
        return items;
    }

    public Boolean getOrgEnabledMultiCardMod() {
        return orgEnabledMultiCardMod;
    }

    public void setOrgEnabledMultiCardMod(Boolean orgEnabledMultiCardMod) {
        this.orgEnabledMultiCardMod = orgEnabledMultiCardMod;
    }

    public Boolean getClientHasActiveMultiCardMode() {
        return clientHasActiveMultiCardMode;
    }

    public void setClientHasActiveMultiCardMode(Boolean clientHasActiveMultiCardMode) {
        this.clientHasActiveMultiCardMode = clientHasActiveMultiCardMode;
    }
}
