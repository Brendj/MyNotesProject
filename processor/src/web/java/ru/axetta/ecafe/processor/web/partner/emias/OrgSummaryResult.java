/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.emias;

import ru.axetta.ecafe.processor.web.partner.emias.ResponseItem;

public class OrgSummaryResult extends ResponseItem {
    private long idEventEMIAS;
    OrgSummaryResult(int code, String message, long idEventEMIAS){
        this.code = code;
        this.message = message;
        this.idEventEMIAS = idEventEMIAS;
    }

    OrgSummaryResult(int code, String message){
        this.code = code;
        this.message = message;
    }

    OrgSummaryResult(){
    }

    public long getIdEventEMIAS() {
        return idEventEMIAS;
    }

    public void setIdEventEMIAS(long idEventEMIAS) {
        this.idEventEMIAS = idEventEMIAS;
    }
}
