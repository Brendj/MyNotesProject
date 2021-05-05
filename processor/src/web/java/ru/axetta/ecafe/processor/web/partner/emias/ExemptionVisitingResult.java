/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.emias;

import java.util.ArrayList;
import java.util.List;

/**
 * User: shamil
 * Date: 13.05.15
 * Time: 11:17
 */
public class ExemptionVisitingResult extends ResponseItem {
    private List<ExemptionVisitingDay> exemptionVisitingResultDays;

    public ExemptionVisitingResult() {
    }

    public ExemptionVisitingResult(int code, String message) {
        this.resultCode = code;
        this.description = message;
    }

    public List<ExemptionVisitingDay> getExemptionVisitingResultDays() {
        if (exemptionVisitingResultDays == null)
            exemptionVisitingResultDays = new ArrayList<>();
        return exemptionVisitingResultDays;
    }

    public void setExemptionVisitingResultDays(List<ExemptionVisitingDay> exemptionVisitingResultDays) {
        this.exemptionVisitingResultDays = exemptionVisitingResultDays;
    }
}
