/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.internal.esp.service;

import org.codehaus.jackson.annotate.JsonProperty;

import java.util.List;

public class NewESPForService {
    @JsonProperty("problem")
    private String problem;
    @JsonProperty("template")
    private String template;
    @JsonProperty("description")
    private String description;
    @JsonProperty("user")
    private NewESPUserInfo user;
    @JsonProperty("attachments")
    private List<String> attachments;


    @JsonProperty("problem")
    public String getProblem() {
        return problem;
    }

    @JsonProperty("problem")
    public void setProblem(String problem) {
        this.problem = problem;
    }

    @JsonProperty("template")
    public String getTemplate() {
        return template;
    }

    @JsonProperty("template")
    public void setTemplate(String template) {
        this.template = template;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("user")
    public NewESPUserInfo getUser() {
        return user;
    }

    @JsonProperty("user")
    public void setUser(NewESPUserInfo user) {
        this.user = user;
    }

    @JsonProperty("attachments")
    public List<String> getAttachments() {
        return attachments;
    }

    @JsonProperty("attachments")
    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }
}
