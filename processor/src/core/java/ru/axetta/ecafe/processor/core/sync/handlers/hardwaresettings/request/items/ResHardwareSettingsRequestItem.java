/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items;

import ru.axetta.ecafe.processor.core.sync.AbstractToElement;

import java.util.Date;

public abstract class ResHardwareSettingsRequestItem implements AbstractToElement {

    private Date lastUpdate;
    private String type;

    public ResHardwareSettingsRequestItem() {
    }

    public ResHardwareSettingsRequestItem(Date lastUpdate, String type) {
        this.lastUpdate = lastUpdate;
        this.type = type;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
