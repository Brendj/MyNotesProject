/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.smartwatch;

public class EnterEventsItem {
    private Integer passDirection;
    private Long evtDateTime;
    private Long idOfClient;
    private Long idOfCard;
    private String shortNameInfoService;
    private String shortAddress;

    public Integer getPassDirection() {
        return passDirection;
    }

    public void setPassDirection(Integer passDirection) {
        this.passDirection = passDirection;
    }

    public Long getEvtDateTime() {
        return evtDateTime;
    }

    public void setEvtDateTime(Long evtDateTime) {
        this.evtDateTime = evtDateTime;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getIdOfCard() {
        return idOfCard;
    }

    public void setIdOfCard(Long idOfCard) {
        this.idOfCard = idOfCard;
    }

    public String getShortNameInfoService() {
        return shortNameInfoService;
    }

    public void setShortNameInfoService(String shortNameInfoService) {
        this.shortNameInfoService = shortNameInfoService;
    }

    public String getShortAddress() {
        return shortAddress;
    }

    public void setShortAddress(String shortAddress) {
        this.shortAddress = shortAddress;
    }
}
