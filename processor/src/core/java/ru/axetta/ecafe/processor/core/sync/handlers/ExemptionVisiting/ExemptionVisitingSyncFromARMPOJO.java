/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.ExemptionVisiting;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ExemptionVisitingSyncFromARMPOJO {
    private Long idEMIAS;
    private Boolean accepted;

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("Record");
        element.setAttribute("idEMIAS",idEMIAS.toString());
        element.setAttribute("accepted", accepted == null ? "false" : accepted.toString());
        return element;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }

    public Long getIdEMIAS() {
        return idEMIAS;
    }

    public void setIdEMIAS(Long idEMIAS) {
        this.idEMIAS = idEMIAS;
    }
}
