/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.balance.hold;

import ru.axetta.ecafe.processor.core.persistence.ClientBalanceHold;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.hibernate.Session;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.DateFormat;
import java.util.Date;

public class ClientBalanceHoldItem {
    private Long idOfClient;
    private String guid;
    private Long holdSum;
    private Long idOfOldOrg;
    private Long idOfNewOrg;
    private Date createdDate;
    private Long version;
    private Integer createStatus;
    private Integer requestStatus;

    public ClientBalanceHoldItem(Session session, ClientBalanceHold clientBalanceHold) {
        this.idOfClient = clientBalanceHold.getClient().getIdOfClient();
        this.guid = clientBalanceHold.getGuid();
        this.holdSum = clientBalanceHold.getHoldSum();
        this.idOfOldOrg = clientBalanceHold.getOldOrg().getIdOfOrg();
        this.idOfNewOrg = clientBalanceHold.getNewOrg().getIdOfOrg();
        this.createdDate = clientBalanceHold.getCreatedDate();
        this.version = clientBalanceHold.getVersion();
        this.createStatus = clientBalanceHold.getCreateStatus().ordinal();
        this.requestStatus = clientBalanceHold.getRequestStatus().ordinal();
    }



    public Element toElement(Document document) throws Exception{
        Element element = document.createElement("CBH");
        DateFormat timeFormat = CalendarUtils.getDateTimeFormatLocal();

        if (null != idOfClient) {
            element.setAttribute("ClientId", Long.toString(idOfClient));
        }
        if (null != guid) {
            element.setAttribute("Guid", guid);
        }
        if (null != version) {
            element.setAttribute("Version", Long.toString(version));
        }
        if (null != holdSum) {
            element.setAttribute("HoldSum", Long.toString(holdSum));
        }
        if (null != idOfOldOrg) {
            element.setAttribute("OldOrgId", Long.toString(idOfOldOrg));
        }
        if (null != idOfNewOrg) {
            element.setAttribute("NewOrgId", Long.toString(idOfNewOrg));
        }
        if (null != createdDate) {
            element.setAttribute("CreatedDate", timeFormat.format(createdDate));
        }
        if (null != createStatus) {
            element.setAttribute("CreateStatus", Integer.toString(createStatus));
        }
        if (null != requestStatus) {
            element.setAttribute("RequestStatus", Integer.toString(requestStatus));
        }

        return element;
    }
}
