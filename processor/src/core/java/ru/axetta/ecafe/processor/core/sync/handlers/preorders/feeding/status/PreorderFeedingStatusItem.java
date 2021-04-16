/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.preorders.feeding.status;

import ru.axetta.ecafe.processor.core.sync.handlers.SyncPacketUtils;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.text.SimpleDateFormat;
import java.util.Date;

public class PreorderFeedingStatusItem {
    private Date date;
    private String guid;
    private Integer status;
    private Integer storno;
    private Long version;
    private Boolean deletedState;
    private String errorMessage;
    private Long orgOwner;

    public PreorderFeedingStatusItem(Date date, String guid, Integer status, Integer storno, Long version,
            Boolean deletedState, Long orgOwner, String errorMessage) {
        this.date = date;
        this.guid = guid;
        this.status = status;
        this.storno = storno;
        this.version = version;
        this.deletedState = deletedState;
        this.orgOwner = orgOwner;
        this.errorMessage = errorMessage;
    }

    public static PreorderFeedingStatusItem build(Node itemNode, Long orgOwner) {
        StringBuilder sbError = new StringBuilder();
        Date date = SyncPacketUtils.readDateValue(itemNode, "Date", sbError, true);
        String guid = SyncPacketUtils.readStringValue(itemNode, "Guid", sbError, true);
        Integer status = SyncPacketUtils.readIntegerValue(itemNode, "Status", sbError, false);
        Integer storno = SyncPacketUtils.readIntegerValue(itemNode, "Storno", sbError, false);
        Long version = SyncPacketUtils.readLongValue(itemNode, "V", sbError, false);
        Boolean deletedState = SyncPacketUtils.getDeletedState(itemNode, sbError);
        return new PreorderFeedingStatusItem(date, guid, status, storno, version, deletedState, orgOwner, sbError.toString());
    }

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "Date", new SimpleDateFormat("dd.MM.yyyy").format(date));
        XMLUtils.setAttributeIfNotNull(element, "Guid", guid);
        XMLUtils.setAttributeIfNotNull(element, "Status", status);
        XMLUtils.setAttributeIfNotNull(element, "Storno", storno);
        XMLUtils.setAttributeIfNotNull(element, "V", version);
        if (deletedState != null) XMLUtils.setAttributeIfNotNull(element, "D", deletedState ? "1" : "0");
        return element;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof PreorderFeedingStatusItem)) {
            return false;
        }
        final PreorderFeedingStatusItem that = (PreorderFeedingStatusItem) o;
        return guid.equals(that.getGuid());
    }

    @Override
    public int hashCode() {
        return guid.hashCode();
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getStorno() {
        return storno;
    }

    public void setStorno(Integer storno) {
        this.storno = storno;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Long getOrgOwner() {
        return orgOwner;
    }

    public void setOrgOwner(Long orgOwner) {
        this.orgOwner = orgOwner;
    }
}
