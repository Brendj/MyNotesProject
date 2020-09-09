package ru.iteco.meshsync.error.dto;

import org.springframework.validation.ObjectError;

public class ApiErrorImpl implements ApiError {
    private String message;

    private String code;

    private String objectName;

    private ApiErrorType type;

    ApiErrorImpl(ObjectError e) {
        this.message = e.getDefaultMessage();
        this.code = e.getCode();
        this.objectName = e.getObjectName();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getObjectName() {
        return objectName;
    }

    public void setObjectName(String objectName) {
        this.objectName = objectName;
    }

    public ApiErrorType getType() {
        return type;
    }

    protected void setType(ApiErrorType type) {
        this.type = type;
    }
}
