/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response.registry.accounts;

import ru.axetta.ecafe.processor.core.persistence.Card;
import ru.axetta.ecafe.processor.core.persistence.CardState;
import ru.axetta.ecafe.processor.core.persistence.Client;
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


    public CardsItem(long cardNo, int cardType, int state, long idOfClient, String lockReason, Date validDate,
            Date issueDate, long orgOwner, boolean temp, Long visistorId, Long contractId, Long printedNo) {
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
    }

    public CardsItem(Card card, Client client) {
        this.cardNo = card.getCardNo();
        this.cardType = card.getCardType();
        this.state = card.getState();
        if(client != null){
            this.idOfClient = client.getIdOfClient();
        }else {
            this.idOfClient = null;
        }
        this.lockReason = card.getLockReason();
        this.validDate = card.getValidTime();
        this.issueDate = card.getIssueTime();
        if(card.getOrg() != null){
            this.orgOwner = card.getOrg().getIdOfOrg();
        }else {
            this.orgOwner = client.getOrg().getIdOfOrg();
        }
        isTemp = (card.getState() == CardState.ISSUEDTEMP.getValue());
        //this.visistorId = card.visistorId;
        if(client != null){
            this.contractId = client.getContractId();
            this.idOfClient = client.getIdOfClient();
        }
        this.printedNo = card.getCardPrintedNo();
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

        return element;
    }
}
