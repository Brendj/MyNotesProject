/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.card;

import java.util.List;

public class ErrorCartSign {
    private String typeError;
    private List<String> parametrs;
    private String insideError;
    private String message;

    public String getTypeError() {
        return typeError;
    }

    public void setTypeError(String typeError) {
        this.typeError = typeError;
    }

    public List<String> getParametrs() {
        return parametrs;
    }

    public void setParametrs(List<String> parametrs) {
        this.parametrs = parametrs;
    }

    public String getInsideError() {
        return insideError;
    }

    public void setInsideError(String insideError) {
        this.insideError = insideError;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
