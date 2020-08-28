package ru.iteco.nsisync.error.dto;

import org.springframework.validation.FieldError;
import org.springframework.web.multipart.MultipartFile;

public class ApiFieldError extends ApiErrorImpl {
    private String field;

    private Object value;

    private ApiFieldError(FieldError e) {
        super(e);
        super.setType(ApiErrorType.FIELD_ERROR);
        this.field = e.getField();
        Object obj = e.getRejectedValue();
        if (obj instanceof MultipartFile) {
            obj = ((MultipartFile) obj).getOriginalFilename();
        }
        this.value = obj;
    }

    static ApiFieldError valueOf(FieldError e) {
        return new ApiFieldError(e);
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
