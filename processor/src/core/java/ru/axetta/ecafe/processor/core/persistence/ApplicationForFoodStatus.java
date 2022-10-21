/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import org.apache.commons.lang.StringUtils;

public class ApplicationForFoodStatus {
    private ApplicationForFoodState applicationForFoodState;

    public ApplicationForFoodStatus() {

    }

    public ApplicationForFoodStatus(ApplicationForFoodState applicationForFoodState) {
        this.applicationForFoodState = applicationForFoodState;
    }

    public ApplicationForFoodState getApplicationForFoodState() {
        return applicationForFoodState;
    }

    public void setApplicationForFoodState(ApplicationForFoodState applicationForFoodState) {
        this.applicationForFoodState = applicationForFoodState;
    }

    public static ApplicationForFoodStatus fromString(String s) {
        return new ApplicationForFoodStatus(ApplicationForFoodState.fromCode(s));
    }

    @Override
    public String toString() {
        return applicationForFoodState.getCode();
    }

    @Override
    public boolean equals(Object object) {
        if (!(object instanceof ApplicationForFoodStatus)) {
            return false;
        }
        ApplicationForFoodStatus status = (ApplicationForFoodStatus) object;

        return this.applicationForFoodState.equals(status.getApplicationForFoodState());
    }
}
