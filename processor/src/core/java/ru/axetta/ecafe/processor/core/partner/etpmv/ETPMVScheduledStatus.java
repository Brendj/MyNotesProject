/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.etpmv;

import ru.axetta.ecafe.processor.core.persistence.ApplicationForFoodDeclineReason;
import ru.axetta.ecafe.processor.core.persistence.ApplicationForFoodState;

public class ETPMVScheduledStatus {
    private String serviceNumber;
    private ApplicationForFoodState state;
    private ApplicationForFoodDeclineReason reason;

    public ETPMVScheduledStatus(String serviceNumber, ApplicationForFoodState state, ApplicationForFoodDeclineReason reason) {
        this.serviceNumber = serviceNumber;
        this.state = state;
        this.reason = reason;
    }

    public String getServiceNumber() {
        return serviceNumber;
    }

    public void setServiceNumber(String serviceNumber) {
        this.serviceNumber = serviceNumber;
    }

    public ApplicationForFoodState getState() {
        return state;
    }

    public void setState(ApplicationForFoodState state) {
        this.state = state;
    }

    public ApplicationForFoodDeclineReason getReason() {
        return reason;
    }

    public void setReason(ApplicationForFoodDeclineReason reason) {
        this.reason = reason;
    }
}
