/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.esp.service;

import org.codehaus.jackson.annotate.JsonProperty;

public class InfoESPresponse {
    @JsonProperty("id")
    private String id;
    @JsonProperty("user_id")
    private String user_id;
    @JsonProperty("sd")
    private String sd;
    @JsonProperty("created_at")
    private String created_at;
    @JsonProperty("closed_at")
    private String closed_at;
    @JsonProperty("data")
    private String data;
    @JsonProperty("solution")
    private String solution;
    @JsonProperty("status")
    private String status;
    @JsonProperty("id")
    public String getId() {
        return id;
    }
    @JsonProperty("id")
    public void setId(String id) {
        this.id = id;
    }
    @JsonProperty("user_id")
    public String getUser_id() {
        return user_id;
    }
    @JsonProperty("user_id")
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    @JsonProperty("sd")
    public String getSd() {
        return sd;
    }
    @JsonProperty("sd")
    public void setSd(String sd) {
        this.sd = sd;
    }
    @JsonProperty("created_at")
    public String getCreated_at() {
        return created_at;
    }
    @JsonProperty("created_at")
    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }
    @JsonProperty("closed_at")
    public String getClosed_at() {
        return closed_at;
    }
    @JsonProperty("closed_at")
    public void setClosed_at(String closed_at) {
        this.closed_at = closed_at;
    }
    @JsonProperty("data")
    public String getData() {
        return data;
    }
    @JsonProperty("data")
    public void setData(String data) {
        this.data = data;
    }
    @JsonProperty("solution")
    public String getSolution() {
        return solution;
    }
    @JsonProperty("solution")
    public void setSolution(String solution) {
        this.solution = solution;
    }
    @JsonProperty("status")
    public String getStatus() {
        return status;
    }
    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }
}
