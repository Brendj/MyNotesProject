/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp.type;


import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Created by a.voinov on 27.08.2019.
 */

@JsonIgnoreProperties(ignoreUnknown = true)
public class EMPReqestSmsType extends EmpResult{
    @JsonProperty("destination")
    private String destination;
    @JsonProperty("message")
    private String message;
    @JsonProperty("source")
    private String source;

    public EMPReqestSmsType(String destination, String message, String source) {
        this.destination = destination;
        this.message = message;
        this.source = source;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}