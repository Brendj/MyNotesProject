/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.ExemptionVisiting;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ExemptionVisitingSyncFromAnswerARMPOJO {
    private Long idExemption;
    private String errormessage;
    private Long version;

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("Record");
        element.setAttribute("idExemption", idExemption == null ? "" : idExemption.toString());
        element.setAttribute("errormessage", errormessage == null ? "" : errormessage);
        element.setAttribute("Version", version == null ? "0" : Long.toString(version));
        return element;
    }


    public Long getIdExemption() {
        if (idExemption == null)
            return 0L;
        return idExemption;
    }

    public void setIdExemption(Long idExemption) {
        this.idExemption = idExemption;
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
