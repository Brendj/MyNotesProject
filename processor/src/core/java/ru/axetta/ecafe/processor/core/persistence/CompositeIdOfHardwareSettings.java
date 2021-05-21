/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.io.Serializable;

public class CompositeIdOfHardwareSettings implements Serializable {

    private Long idOfOrg;
    private String ipHost;

    protected CompositeIdOfHardwareSettings() {
    }

    public CompositeIdOfHardwareSettings(Long idOfOrg, String ipHost) {
        this.ipHost = ipHost;
        this.idOfOrg = idOfOrg;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getIpHost() {
        return ipHost;
    }

    public void setIpHost(String ipHost) {
        this.ipHost = ipHost;
    }
}
