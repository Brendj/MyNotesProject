package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

import java.util.List;

public class IdListResponse extends MeshGuardianResponse {

    private List<AgentIdResponse> agentResponse;
    private List<DocumentIdResponse> documentResponse;


    public IdListResponse() {
        super();
    }

    public IdListResponse(Integer code, String message) {
        super(code, message);
    }

    public IdListResponse okResponse() {
        this.setCode(OK_CODE);
        this.setMessage(OK_MESSAGE);
        return this;
    }

    public IdListResponse internalErrorResponse() {
        this.setCode(INTERNAL_ERROR_CODE);
        this.setMessage(INTERNAL_ERROR_MESSAGE);
        return this;
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

    public List<AgentIdResponse> getAgentResponse() {
        return agentResponse;
    }

    public void setAgentResponse(List<AgentIdResponse> agentResponse) {
        this.agentResponse = agentResponse;
    }

    public List<DocumentIdResponse> getDocumentResponse() {
        return documentResponse;
    }

    public void setDocumentResponse(List<DocumentIdResponse> documentResponse) {
        this.documentResponse = documentResponse;
    }
}
