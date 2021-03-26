/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.ezd;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.LinkedList;
import java.util.List;

public class ResponseToEZDQR extends Result{
    @JsonProperty("meshguid")
    private String meshguid;
    @JsonProperty("qr")
    private String qr;

    public String getMeshguid() {
        return meshguid;
    }

    public void setMeshguid(String meshguid) {
        this.meshguid = meshguid;
    }

    public String getQr() {
        return qr;
    }

    public void setQr(String qr) {
        this.qr = qr;
    }
}
