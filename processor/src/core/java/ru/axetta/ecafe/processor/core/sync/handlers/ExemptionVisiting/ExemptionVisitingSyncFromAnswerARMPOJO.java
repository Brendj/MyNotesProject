/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.ExemptionVisiting;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ExemptionVisitingSyncFromAnswerARMPOJO {
    private Long idEventEMIAS;
    private String errormessage;
    private Long version;

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("Record");
        element.setAttribute("idEMIAS", idEventEMIAS == null ? "" : idEventEMIAS.toString());
        element.setAttribute("errormessage", errormessage == null ? "" : errormessage);
        element.setAttribute("Version", version == null ? "0" : Long.toString(version));
        return element;
    }


    public Long getIdEventEMIAS() {
        if (idEventEMIAS == null)
            return 0L;
        return idEventEMIAS;
    }

    public void setIdEventEMIAS(Long idEventEMIAS) {
        this.idEventEMIAS = idEventEMIAS;
    }

    public String getErrormessage() {
        return errormessage;
    }

    public void setErrormessage(String errormessage) {
        this.errormessage = errormessage;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
