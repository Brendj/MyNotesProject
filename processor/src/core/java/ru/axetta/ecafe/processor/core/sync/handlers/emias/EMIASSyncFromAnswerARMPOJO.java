/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.emias;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class EMIASSyncFromAnswerARMPOJO {
    private Long idEventEMIAS;
    private String errormessage;

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("Record");
        element.setAttribute("idEventEMIAS",idEventEMIAS.toString());
        element.setAttribute("errormessage", errormessage == null ? "" : errormessage);
        return element;
    }


    public Long getIdEventEMIAS() {
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
}
