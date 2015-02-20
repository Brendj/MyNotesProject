/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * Created with IntelliJ IDEA.
 * User: Sasha
 * Date: 16.02.15
 * Time: 15:15
 * To change this template use File | Settings | File Templates.
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getPublicationListSimple", propOrder = {
        "contractId",
        "searchCondition",
        "limit",
        "offset"
})

public class GetPublicationListSimple {
    protected Long contractId;
    protected String searchCondition;
    protected int limit;
    protected int offset;

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long value) {
        this.contractId = value;
    }

    public String getSearchCondition() {
        return searchCondition;
    }

    public void setSearchCondition(String value) {
        this.searchCondition = value;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int value) {
        this.limit = value;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int value) {
        this.offset = value;
    }
}
