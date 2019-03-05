/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import ru.axetta.ecafe.processor.core.persistence.ClientCreatedFromType;

/**
 * Created by i.semenov on 06.08.2018.
 */
public class ClientWithAddInfo {
    private ClientCreatedFromType clientCreatedFrom;
    private Integer informedSpecialMenu;
    private boolean disabled;

    public ClientCreatedFromType getClientCreatedFrom() {
        return clientCreatedFrom;
    }

    public void setClientCreatedFrom(ClientCreatedFromType clientCreatedFrom) {
        this.clientCreatedFrom = clientCreatedFrom;
    }

    public Integer getInformedSpecialMenu() {
        return informedSpecialMenu;
    }

    public void setInformedSpecialMenu(Integer informedSpecialMenu) {
        this.informedSpecialMenu = informedSpecialMenu;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }
}
