/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.esp.service;

import org.codehaus.jackson.annotate.JsonProperty;

public class NewESPUserInfo {
    @JsonProperty("email")
    private String email;
    @JsonProperty("fio")
    private String fio;

    @JsonProperty("email")
    public String getEmail() {
        return email;
    }

    @JsonProperty("email")
    public void setEmail(String email) {
        this.email = email;
    }

    @JsonProperty("fio")
    public String getFio() {
        return fio;
    }

    @JsonProperty("fio")
    public void setFio(String fio) {
        this.fio = fio;
    }
}
