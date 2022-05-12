package ru.axetta.ecafe.processor.core.partner.mesh.guardians;

public class PersonResponse extends MeshGuardianResponse {
    private String meshGuid;

    public PersonResponse() {

    }

    public PersonResponse(Integer code, String message) {
        super(code, message);
    }

    public PersonResponse(String meshGuid) {
        this.meshGuid = meshGuid;
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

    public String getMeshGuid() {
        return meshGuid;
    }

    public void setMeshGuid(String meshGuid) {
        this.meshGuid = meshGuid;
    }
}
