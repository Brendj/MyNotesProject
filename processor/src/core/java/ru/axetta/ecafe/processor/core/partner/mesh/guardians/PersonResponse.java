package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

public class PersonResponse extends MeshGuardianResponse {
    private MeshGuardianPerson response;

    public PersonResponse() {

    }

    public PersonResponse(Integer code, String message) {
        super(code, message);
    }

    public PersonResponse(MeshGuardianPerson response) {
        this.response = response;
    }

    public PersonResponse okResponse() {
        this.setCode(OK_CODE);
        this.setMessage(OK_MESSAGE);
        return this;
    }

    public PersonResponse internalErrorResponse() {
        this.setCode(INTERNAL_ERROR_CODE);
        this.setMessage(INTERNAL_ERROR_MESSAGE);
        return this;
    }

    public PersonResponse internalErrorResponse(String message) {
        this.setCode(INTERNAL_ERROR_CODE);
        this.setMessage(INTERNAL_ERROR_MESSAGE + " " + message);
        return this;
    }

    public MeshGuardianPerson getResponse() {
        return response;
    }

    public void setResponse(MeshGuardianPerson response) {
        this.response = response;
    }
}
