/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.oku.dataflow;

import org.codehaus.jackson.annotate.JsonProperty;

public class ClientData implements IResponseEntity {
    @JsonProperty(value="organization_id")
    private Long idOfOrg;

    public ClientData(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public ClientData() {

    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }
}
