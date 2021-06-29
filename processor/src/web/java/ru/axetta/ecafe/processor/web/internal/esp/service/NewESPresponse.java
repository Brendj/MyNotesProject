/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.esp.service;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class NewESPresponse {
    @JsonProperty("msg")
    private String msg;
    @JsonProperty("type")
    private String type;
    @JsonProperty("id")
    private String id;
    @JsonProperty("additional_data")
    private List<String> additional_data;

    @JsonProperty("msg")
    public String getMsg() {
        return msg;
    }

    @JsonProperty("msg")
    public void setMsg(String msg) {
        this.msg = msg;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("id")
    public String getId() {
        return id;
    }

    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }

    @JsonProperty("additional_data")
    public List<String> getAdditional_data() {
        return additional_data;
    }
    @JsonProperty("additional_data")
    public void setAdditional_data(List<String> additional_data) {
        this.additional_data = additional_data;
    }
}
