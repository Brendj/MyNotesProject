/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.ExemptionVisiting;

import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

public class ExemptionVisitingSyncFromARMPOJO {
    private Long idExemption;
    private Boolean accepted;
    private Date acceptedDateTime;

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("Record");
        element.setAttribute("idExemption", idExemption == null ? "" : idExemption.toString());
        element.setAttribute("accepted", accepted == null ? "false" : accepted.toString());
        element.setAttribute("acceptedDateTime", acceptedDateTime == null ? "null" : CalendarUtils.dateShortToStringFullYear(acceptedDateTime));
        return element;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public Long getIdExemption() {
        return idExemption;
    }

    public void setIdExemption(Long idExemption) {
        this.idExemption = idExemption;
    }

    public Date getAcceptedDateTime() {
        return acceptedDateTime;
    }

    public void setAcceptedDateTime(Date acceptedDateTime) {
        this.acceptedDateTime = acceptedDateTime;
    }
}
