package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

import java.util.List;

public class AgentListResponse extends MeshGuardianResponse {

    private List<AgentResponse> response;

    public AgentListResponse() {
        super();
    }

    public AgentListResponse(Integer code, String message) {
        super(code, message);
    }

    public AgentListResponse(List<AgentResponse> response) {
        this.setResponse(response);
    }

    public AgentListResponse okResponse() {
        this.setCode(OK_CODE);
        this.setMessage(OK_MESSAGE);
        return this;
    }

    public AgentListResponse internalErrorResponse() {
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

    public List<AgentResponse> getResponse() {
        return response;
    }

    public void setResponse(List<AgentResponse> response) {
        this.response = response;
    }
}
