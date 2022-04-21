package ru.axetta.ecafe.processor.core.partner.mesh;

public class MeshResponseWithStatusCode {
    private byte[] response;
    private int code;

    public MeshResponseWithStatusCode(byte[] response, int code) {
        this.response = response;
        this.code = code;
    }

    public byte[] getResponse() {
        return response;
    }

    public void setResponse(byte[] response) {
        this.response = response;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
