/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.atol;

import java.util.Date;

/**
 * Created by nuc on 12.08.2019.
 */
public class AtolToken {
    private Date date;
    private String token;

    public AtolToken(String token) {
        this.date = new Date();
        this.token = token;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
