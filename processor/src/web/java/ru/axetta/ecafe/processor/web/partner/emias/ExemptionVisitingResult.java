/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.emias;

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
        this.code = code;
        this.message = message;
    }

    public List<ExemptionVisitingDay> getExemptionVisitingResultDays() {
        return exemptionVisitingResultDays;
    }

    public void setExemptionVisitingResultDays(List<ExemptionVisitingDay> exemptionVisitingResultDays) {
        this.exemptionVisitingResultDays = exemptionVisitingResultDays;
    }
}
