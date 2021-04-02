/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.ExemptionVisiting;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ExemptionVisitingSyncFromARMPOJO {
    private Long idExemption;
    private Boolean accepted;

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("Record");
        element.setAttribute("idExemption", idExemption == null ? "" : idExemption.toString());
        element.setAttribute("accepted", accepted == null ? "false" : accepted.toString());
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
}
