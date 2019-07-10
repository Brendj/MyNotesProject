/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.balance.hold;

import ru.axetta.ecafe.processor.core.persistence.ClientBalanceHold;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.DateFormat;
import java.util.Date;

public class ClientBalanceHoldItem {
    private Long idOfClient;
    private Long idOfDeclarer;
    private String phoneOfDeclarer;
    private String guid;
    private Long holdSum;
    private Long idOfOldOrg;
    private Long idOfNewOrg;
    private Date createdDate;
    private Long version;
    private Integer createStatus;
    private Integer requestStatus;
    private String declarerInn;
    private String declarerAccount;
    private String declarerBank;
    private String declarerBik;
    private String declarerCorrAccount;
    private String errorMessage;
    private Integer resCode;
    private Long idOfOrgLastChange;

    public ClientBalanceHoldItem(ClientBalanceHold clientBalanceHold) {
        this.idOfClient = clientBalanceHold.getClient().getIdOfClient();
        if (clientBalanceHold.getDeclarer() != null) this.idOfDeclarer = clientBalanceHold.getDeclarer().getIdOfClient();
        this.phoneOfDeclarer = clientBalanceHold.getPhoneOfDeclarer();
        this.guid = clientBalanceHold.getGuid();
        this.holdSum = clientBalanceHold.getHoldSum();
        this.idOfOldOrg = clientBalanceHold.getOldOrg().getIdOfOrg();
        if (clientBalanceHold.getNewOrg() != null) this.idOfNewOrg = clientBalanceHold.getNewOrg().getIdOfOrg();
        this.createdDate = clientBalanceHold.getCreatedDate();
        this.version = clientBalanceHold.getVersion();
        this.createStatus = clientBalanceHold.getCreateStatus().ordinal();
        this.requestStatus = clientBalanceHold.getRequestStatus().ordinal();
        this.declarerInn = clientBalanceHold.getDeclarerInn();
        this.declarerAccount = clientBalanceHold.getDeclarerAccount();
        this.declarerBank = clientBalanceHold.getDeclarerBank();
        this.declarerBik = clientBalanceHold.getDeclarerBik();
        this.declarerCorrAccount = clientBalanceHold.getDeclarerCorrAccount();
        this.idOfOrgLastChange = clientBalanceHold.getIdOfOrgLastChange();
    }

    public ClientBalanceHoldItem(Long idOfClient, Long idOfDeclarer, String phoneOfDeclarer, String guid, Long holdSum,
            Long idOfOldOrg, Long idOfNewOrg, Date createdDate, Long version, Integer createStatus, Integer requestStatus,
            String declarerInn, String declarerAccount, String declarerBank, String declarerBik, String declarerCorrAccount,
            String errorMessage, Long idOfOrgLastChange) {
        this.idOfClient = idOfClient;
        this.idOfDeclarer = idOfDeclarer;
        this.phoneOfDeclarer = phoneOfDeclarer;
        this.guid = guid;
        this.holdSum = holdSum;
        this.idOfOldOrg = idOfOldOrg;
        this.idOfNewOrg = idOfNewOrg;
        this.createdDate = createdDate;
        this.version = version;
        this.createStatus = createStatus;
        this.requestStatus = requestStatus;
        this.declarerInn = declarerInn;
        this.declarerAccount = declarerAccount;
        this.declarerBank = declarerBank;
        this.declarerBik = declarerBik;
        this.declarerCorrAccount = declarerCorrAccount;
        this.errorMessage = errorMessage;
        this.idOfOrgLastChange = idOfOrgLastChange;
    }

    public ClientBalanceHoldItem(String guid, Integer resCode, String errorMessage, Long version) {
        this.guid = guid;
        this.resCode = resCode;
        this.errorMessage = errorMessage;
        this.version = version;
    }

    public Element toElement(Document document) throws Exception{
        Element element = document.createElement("CBH");
        DateFormat timeFormat = CalendarUtils.getDateTimeFormatLocal();

        if (null != idOfClient) {
            element.setAttribute("ClientId", Long.toString(idOfClient));
        }
        if (null != idOfDeclarer) {
            element.setAttribute("DeclarerId", Long.toString(idOfDeclarer));
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
        if (null != phoneOfDeclarer) {
            element.setAttribute("DeclarerPhone", phoneOfDeclarer);
        }
        if (null != declarerInn) {
            element.setAttribute("DeclarerInn", declarerInn);
        }
        if (null != declarerAccount) {
            element.setAttribute("DeclarerAccount", declarerAccount);
        }
        if (null != declarerBank) {
            element.setAttribute("DeclarerBank", declarerBank);
        }
        if (null != declarerBik) {
            element.setAttribute("DeclarerBik", declarerBik);
        }
        if (null != declarerCorrAccount) {
            element.setAttribute("DeclarerCorrAccount", declarerCorrAccount);
        }
        if (null != resCode) {
            element.setAttribute("ResCode", Integer.toString(resCode));
        }
        if (null != errorMessage) {
            element.setAttribute("ErrorMessage", errorMessage);
        }
        if (null != idOfOrgLastChange) {
            element.setAttribute("LastChangeOrgId", Long.toString(idOfOrgLastChange));
        }

        return element;
    }

    public String getPhoneOfDeclarer() {
        return phoneOfDeclarer;
    }

    public void setPhoneOfDeclarer(String phoneOfDeclarer) {
        this.phoneOfDeclarer = phoneOfDeclarer;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getIdOfDeclarer() {
        return idOfDeclarer;
    }

    public void setIdOfDeclarer(Long idOfDeclarer) {
        this.idOfDeclarer = idOfDeclarer;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getHoldSum() {
        return holdSum;
    }

    public void setHoldSum(Long holdSum) {
        this.holdSum = holdSum;
    }

    public Long getIdOfOldOrg() {
        return idOfOldOrg;
    }

    public void setIdOfOldOrg(Long idOfOldOrg) {
        this.idOfOldOrg = idOfOldOrg;
    }

    public Long getIdOfNewOrg() {
        return idOfNewOrg;
    }

    public void setIdOfNewOrg(Long idOfNewOrg) {
        this.idOfNewOrg = idOfNewOrg;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Integer getCreateStatus() {
        return createStatus;
    }

    public void setCreateStatus(Integer createStatus) {
        this.createStatus = createStatus;
    }

    public Integer getRequestStatus() {
        return requestStatus;
    }

    public void setRequestStatus(Integer requestStatus) {
        this.requestStatus = requestStatus;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getResCode() {
        return resCode;
    }

    public void setResCode(Integer resCode) {
        this.resCode = resCode;
    }

    public String getDeclarerInn() {
        return declarerInn;
    }

    public void setDeclarerInn(String declarerInn) {
        this.declarerInn = declarerInn;
    }

    public String getDeclarerAccount() {
        return declarerAccount;
    }

    public void setDeclarerAccount(String declarerAccount) {
        this.declarerAccount = declarerAccount;
    }

    public String getDeclarerBank() {
        return declarerBank;
    }

    public void setDeclarerBank(String declarerBank) {
        this.declarerBank = declarerBank;
    }

    public String getDeclarerBik() {
        return declarerBik;
    }

    public void setDeclarerBik(String declarerBik) {
        this.declarerBik = declarerBik;
    }

    public String getDeclarerCorrAccount() {
        return declarerCorrAccount;
    }

    public void setDeclarerCorrAccount(String declarerCorrAccount) {
        this.declarerCorrAccount = declarerCorrAccount;
    }

    public Long getIdOfOrgLastChange() {
        return idOfOrgLastChange;
    }

    public void setIdOfOrgLastChange(Long idOfOrgLastChange) {
        this.idOfOrgLastChange = idOfOrgLastChange;
    }
}
