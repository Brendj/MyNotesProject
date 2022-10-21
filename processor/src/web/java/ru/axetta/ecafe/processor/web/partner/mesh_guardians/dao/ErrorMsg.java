package ru.axetta.ecafe.processor.web.partner.mesh_guardians.dao;

import java.net.HttpURLConnection;

public class ErrorMsg implements IDAOEntity {
    private final String error;
    private final Integer code;

    public ErrorMsg(Integer code, String error) {
        this.error = error;
        this.code = code;
    }

    public String getError() {
        return error;
    }

    public Integer getCode() {
        return code;
    }

    public static ErrorMsg notFound() {
        return new ErrorMsg(HttpURLConnection.HTTP_NOT_FOUND, "Not Found");
    }

    public static ErrorMsg badRequest() {
        return new ErrorMsg(HttpURLConnection.HTTP_BAD_REQUEST, "Bad Request");
    }

    public static ErrorMsg unauthorized() {
        return new ErrorMsg(HttpURLConnection.HTTP_UNAUTHORIZED, "Unauthorized");
    }

    public static ErrorMsg internalError() {
        return new ErrorMsg(HttpURLConnection.HTTP_INTERNAL_ERROR, "Internal Error");
    }
}
