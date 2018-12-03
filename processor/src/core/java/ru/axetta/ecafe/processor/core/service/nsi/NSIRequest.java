/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.nsi;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NSIRequest {
    @JsonProperty("params")
    private List<NSIRequestParam> paramList;
    private Long page;
    @JsonProperty("object-type")
    private String objectType;
    @JsonProperty("page-size")
    private Long pageSize;

    public NSIRequest() {

    }

    public NSIRequest(List<NSIRequestParam> paramList, Long page, String objectType, Long pageSize) {
        this.paramList = paramList;
        this.page = page;
        this.objectType = objectType;
        this.pageSize = pageSize;
    }

    public List<NSIRequestParam> getParamList() {
        return paramList;
    }

    public void setParamList(List<NSIRequestParam> paramList) {
        this.paramList = paramList;
    }

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }
}
