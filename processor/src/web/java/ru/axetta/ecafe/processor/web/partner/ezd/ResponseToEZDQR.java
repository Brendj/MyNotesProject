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
    @JsonProperty("dateFrom")
    private String dateFrom;
    @JsonProperty("dateTo")
    private String dateTo;

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

    public String getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.dateFrom = dateFrom;
    }

    public String getDateTo() {
        return dateTo;
    }

    public void setDateTo(String dateTo) {
        this.dateTo = dateTo;
    }
}
