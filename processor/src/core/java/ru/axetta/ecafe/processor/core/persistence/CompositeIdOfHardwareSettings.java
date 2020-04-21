/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

public class CompositeIdOfHardwareSettings implements Serializable {

    private Long idOfOrg;
    private Long idOfHardwareSetting;

    protected CompositeIdOfHardwareSettings() {
    }

    public CompositeIdOfHardwareSettings(Long idOfOrg, Long idOfHardwareSetting) {
        this.idOfHardwareSetting = idOfHardwareSetting;
        this.idOfOrg = idOfOrg;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public Long getIdOfHardwareSetting() {
        return idOfHardwareSetting;
    }

    public void setIdOfHardwareSetting(Long idOfHardwareSetting) {
        this.idOfHardwareSetting = idOfHardwareSetting;
    }
}
