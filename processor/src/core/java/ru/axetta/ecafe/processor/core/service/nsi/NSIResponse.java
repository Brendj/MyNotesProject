/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.nsi;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class NSIResponse<E extends NSIResponseItem> {
    @JsonProperty("payload")
    private List<E> payLoad;
    private Long page;
    @JsonProperty("total-count")
    private Long totalCount;
    @JsonProperty("pages-count")
    private Long pagesCount;
    @JsonProperty("page-size")
    private Long pageSize;

    public NSIResponse(List<E> payLoad, Long page, Long totalCount, Long pagesCount, Long pageSize) {
        this.payLoad = payLoad;
        this.page = page;
        this.totalCount = totalCount;
        this.pagesCount = pagesCount;
        this.pageSize = pageSize;
    }

    public NSIResponse() {}

    public List<E> getPayLoad() {
        return payLoad;
    }

    public void setPayLoad(List<E> payLoad) {
        this.payLoad = payLoad;
    }

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    public Long getPagesCount() {
        return pagesCount;
    }

    public void setPagesCount(Long pagesCount) {
        this.pagesCount = pagesCount;
    }

    public Long getPageSize() {
        return pageSize;
    }

    public void setPageSize(Long pageSize) {
        this.pageSize = pageSize;
    }
}

