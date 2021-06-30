/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.token.security.util;

import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

public class JwtAuthenticationErrorDTO {
    private final int errorCode;
    private final String errorMessage;

    public JwtAuthenticationErrorDTO(int errorCode, String errorMessage){
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public JwtAuthenticationErrorDTO(JwtAuthenticationErrors error) {
        this(error.getErrorCode(), error.getErrorMessage());
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode){
        return;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage){
        return;
    }

    @Override
    public String toString(){
        try {
            return new ObjectMapper().writeValueAsString(this);
        } catch (IOException e) {
            return errorMessage;
        }
    }
}
