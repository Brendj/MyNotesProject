/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.dtiszn;

import ru.axetta.ecafe.processor.core.persistence.ClientDTISZNDiscountStatus;
import ru.axetta.ecafe.processor.core.persistence.ClientDtisznDiscountInfo;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.text.DateFormat;
import java.util.Date;

public class ClientDiscountDTSZNItem {
    private Long idOfClient;
    private Long discountCode;
    private ClientDTISZNDiscountStatus status;
    private Date dateStart;
    private Date dateEnd;
    private Long version;
    private Boolean archived;
    private Date lastDiscountUpdate;

    public ClientDiscountDTSZNItem() {

    }

    public ClientDiscountDTSZNItem(ClientDtisznDiscountInfo info) {
        this.idOfClient = info.getClient().getIdOfClient();
        this.discountCode = info.getDtisznCode();
        this.status = info.getStatus();
        this.dateStart = info.getDateStart();
        this.dateEnd = info.getDateEnd();
        this.version = info.getVersion();
        this.archived = info.getArchived();
        this.lastDiscountUpdate = info.getLastUpdate();
    }

    public Element toElement(Document document) {
        Element element = document.createElement("DI");
        DateFormat timeFormat = CalendarUtils.getDateTimeFormatLocal();

        if (null != idOfClient) {
            element.setAttribute("ClientId", Long.toString(idOfClient));
        }
        if (null != discountCode) {
            element.setAttribute("Code", Long.toString(discountCode));
        }
        if (null != status) {
            element.setAttribute("State", Integer.toString(status.getValue()));
        }
        if (null != dateStart) {
            element.setAttribute("FDate", timeFormat.format(dateStart));
        }
        if (null != dateEnd) {
            element.setAttribute("LDate", timeFormat.format(dateEnd));
        }
        if (null != version) {
            element.setAttribute("V", Long.toString(version));
        }
        if (null != archived) {
            element.setAttribute("D", Boolean.toString(archived));
        }
        if (null != lastDiscountUpdate) {
            element.setAttribute("LastDiscountUpdate", timeFormat.format(lastDiscountUpdate));
        }
        return element;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getDiscountCode() {
        return discountCode;
    }

    public void setDiscountCode(Long discountCode) {
        this.discountCode = discountCode;
    }

    public ClientDTISZNDiscountStatus getStatus() {
        return status;
    }

    public void setStatus(ClientDTISZNDiscountStatus status) {
        this.status = status;
    }

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public Date getLastDiscountUpdate() {
        return lastDiscountUpdate;
    }

    public void setLastDiscountUpdate(Date lastDiscountUpdate) {
        this.lastDiscountUpdate = lastDiscountUpdate;
    }
}
