/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.Response;

import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by nuc on 23.06.2020.
 */
public class ResponseDiscountClientsItem {
    public static final Integer CODE_OK = 0;
    public static final Integer CODE_CLIENT_NOT_FOUND = 1;
    public static final Integer CODE_DISCOUNT_NOT_FOUND = 2;
    public static final Integer CODE_INTERNAL_ERROR = 100;

    public static final String MESSAGE_OK = "OK";
    public static final String MESSAGE_CLIENT_NOT_FOUND = "Клиент не найден";
    public static final String MESSAGE_DISCOUNT_NOT_FOUND = "Льгота не найдена";
    public static final String MESSAGE_INTERNAL_ERROR = "Внутренняя ошибка";

    @JsonProperty("ContractId")
    private Long contractId;

    @JsonProperty("ResultCode")
    private Integer code;

    @JsonProperty("ResultMessage")
    private String message;

    public ResponseDiscountClientsItem(Long contractId, Integer code, String message) {
        this.contractId = contractId;
        this.code = code;
        this.message = message;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
