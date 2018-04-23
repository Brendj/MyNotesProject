/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.preorder.dataflow;

/**
 * Created by i.semenov on 23.04.2018.
 */
public class ESIADataVO {
    private String esiaElkAccessToken;
    private String esiaElkAccessTokenExp;
    private String esiaLkApiAccessToken;
    private String esiaLkApiAccessTokenExp;
    private String esiaSfAccessToken;
    private String esiaSfAccessTokenExp;
    private String esiaRefreshToken;
    private String esiaAccessToken;
    private String esiaAccessTokenExp;
    private String esiaPersonTrusted;
    private String esiaid;

    public ESIADataVO() {

    }

    public String getEsiaElkAccessToken() {
        return esiaElkAccessToken;
    }

    public void setEsiaElkAccessToken(String esiaElkAccessToken) {
        this.esiaElkAccessToken = esiaElkAccessToken;
    }

    public String getEsiaElkAccessTokenExp() {
        return esiaElkAccessTokenExp;
    }

    public void setEsiaElkAccessTokenExp(String esiaElkAccessTokenExp) {
        this.esiaElkAccessTokenExp = esiaElkAccessTokenExp;
    }

    public String getEsiaLkApiAccessToken() {
        return esiaLkApiAccessToken;
    }

    public void setEsiaLkApiAccessToken(String esiaLkApiAccessToken) {
        this.esiaLkApiAccessToken = esiaLkApiAccessToken;
    }

    public String getEsiaLkApiAccessTokenExp() {
        return esiaLkApiAccessTokenExp;
    }

    public void setEsiaLkApiAccessTokenExp(String esiaLkApiAccessTokenExp) {
        this.esiaLkApiAccessTokenExp = esiaLkApiAccessTokenExp;
    }

    public String getEsiaSfAccessToken() {
        return esiaSfAccessToken;
    }

    public void setEsiaSfAccessToken(String esiaSfAccessToken) {
        this.esiaSfAccessToken = esiaSfAccessToken;
    }

    public String getEsiaSfAccessTokenExp() {
        return esiaSfAccessTokenExp;
    }

    public void setEsiaSfAccessTokenExp(String esiaSfAccessTokenExp) {
        this.esiaSfAccessTokenExp = esiaSfAccessTokenExp;
    }

    public String getEsiaRefreshToken() {
        return esiaRefreshToken;
    }

    public void setEsiaRefreshToken(String esiaRefreshToken) {
        this.esiaRefreshToken = esiaRefreshToken;
    }

    public String getEsiaAccessToken() {
        return esiaAccessToken;
    }

    public void setEsiaAccessToken(String esiaAccessToken) {
        this.esiaAccessToken = esiaAccessToken;
    }

    public String getEsiaAccessTokenExp() {
        return esiaAccessTokenExp;
    }

    public void setEsiaAccessTokenExp(String esiaAccessTokenExp) {
        this.esiaAccessTokenExp = esiaAccessTokenExp;
    }

    public String getEsiaPersonTrusted() {
        return esiaPersonTrusted;
    }

    public void setEsiaPersonTrusted(String esiaPersonTrusted) {
        this.esiaPersonTrusted = esiaPersonTrusted;
    }

    public String getEsiaid() {
        return esiaid;
    }

    public void setEsiaid(String esiaid) {
        this.esiaid = esiaid;
    }
}
