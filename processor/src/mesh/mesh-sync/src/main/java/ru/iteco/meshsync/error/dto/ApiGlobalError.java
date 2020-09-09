package ru.iteco.meshsync.error.dto;

import org.springframework.validation.ObjectError;

class ApiGlobalError extends ApiErrorImpl {

    private ApiGlobalError(ObjectError e) {
        super(e);
        super.setType(ApiErrorType.GLOBAL_ERROR);
    }

    static ApiGlobalError valueOf(ObjectError e) {
        return new ApiGlobalError(e);
    }
}
