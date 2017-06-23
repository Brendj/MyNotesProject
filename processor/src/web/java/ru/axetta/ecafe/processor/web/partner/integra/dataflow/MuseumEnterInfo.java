/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

/**
 * Created by i.semenov on 01.06.2017.
 */
public class MuseumEnterInfo extends Result {

    private String guid;
    private Long cardStatusCode;
    private String cardStatusName;

    public MuseumEnterInfo() {

    }

    public MuseumEnterInfo(Long resultCode, String description) {
        this.resultCode = resultCode;
        this.description = description;
    }

    public MuseumEnterInfo(Long resultCode, String description, String guid, Long cardStatusCode, String cardStatusName) {
        this.resultCode = resultCode;
        this.description = description;
        this.setCardStatusCode(cardStatusCode);
        this.setCardStatusName(cardStatusName);
        this.guid = guid;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getCardStatusCode() {
        return cardStatusCode;
    }

    public void setCardStatusCode(Long cardStatusCode) {
        this.cardStatusCode = cardStatusCode;
    }

    public String getCardStatusName() {
        return cardStatusName;
    }

    public void setCardStatusName(String cardStatusName) {
        this.cardStatusName = cardStatusName;
    }
}
