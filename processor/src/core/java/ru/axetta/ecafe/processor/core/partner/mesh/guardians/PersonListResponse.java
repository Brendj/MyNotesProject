package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

import java.util.List;

public class PersonListResponse extends MeshGuardianResponse {

    private List<MeshGuardianPerson> response;

    public PersonListResponse() {
        super();
    }

    public PersonListResponse(List<MeshGuardianPerson> response) {
        this.setResponse(response);
    }

    public PersonListResponse okResponse() {
        this.setCode(OK_CODE);
        this.setMessage(OK_MESSAGE);
        return this;
    }

    public PersonListResponse notEnoughClientDataResponse(String detailMessage) {
        this.setCode(NOT_ENOUGH_CLIENT_DATA_CODE);
        this.setMessage(NOT_ENOUGH_CLIENT_DATA_MESSAGE);
        return this;
    }

    public PersonListResponse internalErrorResponse() {
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

    public List<MeshGuardianPerson> getResponse() {
        return response;
    }

    public void setResponse(List<MeshGuardianPerson> response) {
        this.response = response;
    }
}
