/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.emias;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EMIASSyncFromARMPOJO {
    private Long idEventEMIAS;
    private Boolean accepted;

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("Record");
        element.setAttribute("idEventEMIAS",idEventEMIAS.toString());
        element.setAttribute("accepted", accepted == null ? "false" : accepted.toString());
        return element;
    }


    public Long getIdEventEMIAS() {
        return idEventEMIAS;
    }

    public void setIdEventEMIAS(Long idEventEMIAS) {
        this.idEventEMIAS = idEventEMIAS;
    }

    public Boolean getAccepted() {
        return accepted;
    }

    public void setAccepted(Boolean accepted) {
        this.accepted = accepted;
    }
}
