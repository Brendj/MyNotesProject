package ru.axetta.ecafe.processor.web.internal;

public class GuardianMeshGuidResponse extends ResponseItem{

    private String meshGuid;

    public GuardianMeshGuidResponse() {
    }

    public GuardianMeshGuidResponse(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public GuardianMeshGuidResponse(String meshGuid){
        this.meshGuid = meshGuid;
        this.code = OK;
        this.message = OK_MESSAGE;
    }

    public String getMeshGuid() {
        return meshGuid;
    }

    public void setMeshGuid(String meshGuid) {
        this.meshGuid = meshGuid;
    }
}
