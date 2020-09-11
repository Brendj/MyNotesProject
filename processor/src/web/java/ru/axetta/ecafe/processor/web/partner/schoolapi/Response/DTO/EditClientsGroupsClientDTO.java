/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.Response.DTO;

import org.codehaus.jackson.annotate.JsonProperty;

public class EditClientsGroupsClientDTO {

    @JsonProperty("ContractId")
    private Long contractId;

    @JsonProperty("ResultCode")
    private int resultCode;

    @JsonProperty("ResultMessage")
    private String resultMessage;

    public EditClientsGroupsClientDTO(){
    }

    public EditClientsGroupsClientDTO(Long contractId){
        this.contractId = contractId;
        this.resultCode = 0;
        this.resultMessage = "OK";
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public String getResultMessage() {
        return resultMessage;
    }

    public void setResultMessage(String resultMessage) {
        this.resultMessage = resultMessage;
    }
}
