/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.error;

import java.io.Serializable;
import java.util.Date;

public class WebApplicationErrorResponse implements Serializable {
    private String type;
    private Integer status;
    private String title;
    private String detail;
    private String instance;
    private Date timestamp;

    public WebApplicationErrorResponse(String type, int status, String title, String detail, String instance) {
        this.type = type;
        this.status = status;
        this.title = title;
        this.detail = detail;
        this.instance = instance;
        this.timestamp = new Date();
    }

    public Integer getStatus() {
        return status;
    }

    public String getTitle() {
        return title;
    }

    public String getDetail() {
        return detail;
    }

    public String getInstance() {
        return instance;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public String getType() {
        return type;
    }

}
