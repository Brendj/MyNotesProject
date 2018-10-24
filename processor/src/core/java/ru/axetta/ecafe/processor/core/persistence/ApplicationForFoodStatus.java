/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import org.apache.commons.lang.StringUtils;

public class ApplicationForFoodStatus {
    private ApplicationForFoodState applicationForFoodState;
    private ApplicationForFoodDeclineReason declineReason;

    public ApplicationForFoodStatus() {

    }

    public ApplicationForFoodStatus(ApplicationForFoodState applicationForFoodState, ApplicationForFoodDeclineReason declineReason) {
        this.applicationForFoodState = applicationForFoodState;
        this.declineReason = declineReason;
    }

    public ApplicationForFoodState getApplicationForFoodState() {
        return applicationForFoodState;
    }

    public void setApplicationForFoodState(ApplicationForFoodState applicationForFoodState) {
        this.applicationForFoodState = applicationForFoodState;
    }

    public ApplicationForFoodDeclineReason getDeclineReason() {
        return declineReason;
    }

    public void setDeclineReason(ApplicationForFoodDeclineReason declineReason) {
        this.declineReason = declineReason;
    }

    public static ApplicationForFoodStatus fromString(String s) {
        if (s.contains(".")) {
            String[] array = StringUtils.split(s, ".");
            if (2 != array.length)
                return null;
            return new ApplicationForFoodStatus(ApplicationForFoodState.fromCode(Integer.parseInt(array[0])),
                    ApplicationForFoodDeclineReason.fromInteger(Integer.parseInt(array[1])));
        }
        return new ApplicationForFoodStatus(ApplicationForFoodState.fromCode(Integer.parseInt(s)),
                null);
    }

    @Override
    public String toString() {
        if (null != declineReason) {
            return String.format("%d.%d", applicationForFoodState.getCode(), declineReason.getCode());
        }
        return  String.format("%d", applicationForFoodState.getCode());
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ApplicationForFoodStatus)) {
            return false;
        }
        ApplicationForFoodStatus status = (ApplicationForFoodStatus) object;

        if (null == this.declineReason) {
            if (null == status.getDeclineReason()) {
                return this.applicationForFoodState.equals(status.getApplicationForFoodState());
            }
            return false;
        }

        return this.applicationForFoodState.equals(status.getApplicationForFoodState()) &&
                this.declineReason.equals(status.getDeclineReason());
    }
}
