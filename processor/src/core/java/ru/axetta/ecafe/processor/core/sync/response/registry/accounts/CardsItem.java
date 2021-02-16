/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response.registry.accounts;

import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.CardState;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Visitor;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

/**
 * User: Shamil
 * Date: 29.04.15
 * Time: 22:13
 */
public class CardsItem {

    public static final String SYNC_NAME = "CI";

    private long cardNo;          //Номер карты (уникален для системы)
    private int cardType;         //Тип карты: 1 – Mifare
    private int state;            //Статус карты
    private Long idOfClient;      //Идентификатор клиента - опционально
    private String lockReason;    //Причина блокировки. Опционально, указывается только для заблокированной карты.
    private Date validDate;       //Дата и время окончания срока действия карты - опционально
    private Date issueDate;       //Дата и время выдачи карты – опционально
    private Long orgOwner;        //Организация – владелец  карты – опционально
    private boolean isTemp;       //Признак, что карта временная
    private Long visistorId;      //Идентификатор посетителя – опционально
    private Long contractId;      //Номер лицевого счета клиента – опционально
    private Long printedNo;       //Номер, нанесенный на карту - опционально
    private Boolean isLongUid;    //Признак длинного идентификатора карты - опционально
    private Long idOfCard;        //Идентификатор карты в БД процессинга
    private Integer transitionState;    //Состояние перехода
    private Integer cardSignCertNum; //номер сертификата эцп


    public CardsItem(long cardNo, int cardType, int state, long idOfClient, String lockReason, Date validDate,
            Date issueDate, long orgOwner, boolean temp, Long visistorId, Long contractId, Long printedNo, boolean isLongUid) {
        this.cardNo = cardNo;
        this.cardType = cardType;
        this.state = state;
        this.idOfClient = idOfClient;
        this.lockReason = lockReason;
        this.validDate = validDate;
        this.issueDate = issueDate;
        this.orgOwner = orgOwner;
        isTemp = temp;
        this.visistorId = visistorId;
        this.contractId = contractId;
        this.printedNo = printedNo;
        this.isLongUid = isLongUid;
    }

    public CardsItem(Card card, Client client) {
        this(card);
        if(client != null){
            this.contractId = client.getContractId();
            this.idOfClient = client.getIdOfClient();
        }else {
            this.idOfClient = null;
        }

        if(card.getOrg() != null){
            this.orgOwner = card.getOrg().getIdOfOrg();
        }else {
            this.orgOwner = client.getOrg().getIdOfOrg();
        }
    }
    public CardsItem(Card card){
        this.cardNo = card.getCardNo();
        this.cardType = card.getCardType();
        this.state = card.getState();
        if(card.getOrg() != null){
            this.orgOwner = card.getOrg().getIdOfOrg();
        }
        this.lockReason = card.getLockReason();
        this.validDate = card.getValidTime();
        this.issueDate = card.getIssueTime();
        isTemp = (card.getState() == CardState.TEMPISSUED.getValue());
        this.printedNo = card.getCardPrintedNo();
        if (null != card.getIsLongUid()) {
            this.isLongUid = card.getIsLongUid();
        }
        this.idOfCard = card.getIdOfCard();
        this.transitionState = card.getTransitionState();
        this.cardSignCertNum = card.getCardSignCertNum();
    }

    public CardsItem(Card card, Visitor visitor){
        this(card);
        this.visistorId = visitor.getIdOfVisitor();
    }


    public long getCardNo() {
        return cardNo;
    }

    public void setCardNo(long cardNo) {
        this.cardNo = cardNo;
    }

    public int getCardType() {
        return cardType;
    }

    public void setCardType(int cardType) {
        this.cardType = cardType;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public String getLockReason() {
        return lockReason;
    }

    public void setLockReason(String lockReason) {
        this.lockReason = lockReason;
    }

    public Date getValidDate() {
        return validDate;
    }

    public void setValidDate(Date validDate) {
        this.validDate = validDate;
    }

    public Date getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(Date issueDate) {
        this.issueDate = issueDate;
    }

    public Long getOrgOwner() {
        return orgOwner;
    }

    public void setOrgOwner(Long orgOwner) {
        this.orgOwner = orgOwner;
    }

    public boolean isTemp() {
        return isTemp;
    }

    public void setTemp(boolean temp) {
        isTemp = temp;
    }

    public Long getVisistorId() {
        return visistorId;
    }

    public void setVisistorId(Long visistorId) {
        this.visistorId = visistorId;
    }

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getPrintedNo() {
        return printedNo;
    }

    public void setPrintedNo(Long printedNo) {
        this.printedNo = printedNo;
    }

    public boolean isLongUid() {
        return isLongUid;
    }

    public void setLongUid(boolean longUid) {
        isLongUid = longUid;
    }

    public Long getIdOfCard() {
        return idOfCard;
    }

    public void setIdOfCard(Long idOfCard) {
        this.idOfCard = idOfCard;
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement(SYNC_NAME);
        element.setAttribute("CardNo", Long.toString(this.cardNo));
        element.setAttribute("CardType", Long.toString(this.cardType));
        element.setAttribute("State", Long.toString(this.state));
        if (idOfClient != null) {
            element.setAttribute("IdOfClient", this.idOfClient.toString());
        }
        if (lockReason != null) {
            element.setAttribute("LockReason", this.lockReason);
        }
        if (validDate != null) {
            element.setAttribute("ValidDate", CalendarUtils.dateTimeToString(this.validDate));
        }
        if (issueDate != null) {
            element.setAttribute("IssueDate", CalendarUtils.dateTimeToString(this.issueDate));
        }
        if (orgOwner != null) {
            element.setAttribute("OrgOwner", this.orgOwner.toString());
        }
        element.setAttribute("IsTemp", "" + (this.isTemp ? 1 : 0));
        if (visistorId != null) {
            element.setAttribute("VisistorId", this.visistorId.toString());
        }
        if (contractId != null) {
            element.setAttribute("ContractId", this.contractId.toString());
        }
        if (printedNo != null) {
            element.setAttribute("PrintedNo", this.printedNo.toString());
        }
        if (isLongUid != null) {
            element.setAttribute("IsLongUid", "" + (this.isLongUid ? 1 : 0));
        }
        if (null != idOfCard) {
            element.setAttribute("P_CardId", this.idOfCard.toString());
        }
        if (null != transitionState) {
            element.setAttribute("Trn_State", this.transitionState.toString());
        }
        if (null != cardSignCertNum) {
            element.setAttribute("CardSignCertNum", this.getCardSignCertNum().toString());
        }

        return element;
    }

    public Integer getCardSignCertNum() {
        return cardSignCertNum;
    }

    public void setCardSignCertNum(Integer cardSignCertNum) {
        this.cardSignCertNum = cardSignCertNum;
    }
}
