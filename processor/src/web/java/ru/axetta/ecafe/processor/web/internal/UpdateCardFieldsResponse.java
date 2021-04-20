/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal;

import java.util.LinkedList;
import java.util.List;

public class UpdateCardFieldsResponse {
    private Integer code = ResponseItem.OK;
    private List<Long> problemProcessingCardIds = new LinkedList<>();

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public List<Long> getProblemProcessingCardIds() {
        return problemProcessingCardIds;
    }

    public void setProblemProcessingCardIds(List<Long> problemProcessingCardIds) {
        this.problemProcessingCardIds = problemProcessingCardIds;
    }
}
