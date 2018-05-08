/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.card.request;

import ru.axetta.ecafe.processor.core.persistence.CardRequest;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 28.03.16
 * Time: 12:58
 * To change this template use File | Settings | File Templates.
 */
public class ResCardRequestItem {
    private Integer typeCard;
    private Date createdDate;
    private String mobile;
    private Long version;
    private Long idOfClient;
    private Date cardIssueDate;
    private Boolean deletedState;
    private Long cardNo;
    private String guid;

    public ResCardRequestItem() {

    }

    public ResCardRequestItem(CardRequest cr) {
        this.setTypeCard(cr.getTypeCard());
        this.setCreatedDate(cr.getCreatedDate());
        this.setMobile(cr.getMobile());
        this.setVersion(cr.getVersion());
        this.setIdOfClient(cr.getClient().getIdOfClient());
        this.setCardIssueDate(cr.getCardIssueDate());
        this.setDeletedState(cr.getDeletedState());
        this.setGuid(cr.getGuid());
        this.setCardNo(cr.getCard() == null ? null : cr.getCard().getCardNo());
    }

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "Guid", guid);
        XMLUtils.setAttributeIfNotNull(element, "V", version);
        XMLUtils.setAttributeIfNotNull(element, "TypeCard", typeCard);
        XMLUtils.setAttributeIfNotNull(element, "Mobile", mobile);
        XMLUtils.setAttributeIfNotNull(element, "IdOfClient", idOfClient);
        XMLUtils.setAttributeIfNotNull(element, "CardNo", cardNo);
        if (createdDate != null) {
            XMLUtils.setAttributeIfNotNull(element, "CreatedDate", CalendarUtils.dateShortToStringFullYear(createdDate));
        }
        if (cardIssueDate != null) {
            XMLUtils.setAttributeIfNotNull(element, "CardIssueDate", CalendarUtils.dateShortToStringFullYear(cardIssueDate));
        }
        if (deletedState) {
            XMLUtils.setAttributeIfNotNull(element, "D", deletedState);
        }

        return element;
    }

    public Integer getTypeCard() {
        return typeCard;
    }

    public void setTypeCard(Integer typeCard) {
        this.typeCard = typeCard;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Date getCardIssueDate() {
        return cardIssueDate;
    }

    public void setCardIssueDate(Date cardIssueDate) {
        this.cardIssueDate = cardIssueDate;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    public Long getCardNo() {
        return cardNo;
    }

    public void setCardNo(Long cardNo) {
        this.cardNo = cardNo;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }
}
