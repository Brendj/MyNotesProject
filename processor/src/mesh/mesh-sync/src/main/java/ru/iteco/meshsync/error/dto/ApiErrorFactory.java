package ru.iteco.meshsync.error.dto;

import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

public class ApiErrorFactory {

    public static ApiError buildFromObjectError(ObjectError e) {
        if(e instanceof FieldError) {
            return ApiFieldError.valueOf((FieldError) e);
        } else {
            return ApiGlobalError.valueOf(e);
        }
    }
}
