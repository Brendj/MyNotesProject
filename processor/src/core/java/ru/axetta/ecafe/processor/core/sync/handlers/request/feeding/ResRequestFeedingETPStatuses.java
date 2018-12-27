/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.request.feeding;

import ru.axetta.ecafe.processor.core.persistence.ApplicationForFood;
import ru.axetta.ecafe.processor.core.persistence.ApplicationForFoodStatus;

/**
 * Created by i.semenov on 27.12.2018.
 */
public class ResRequestFeedingETPStatuses {
    private ApplicationForFood applicationForFood;
    private ApplicationForFoodStatus status;


    public ResRequestFeedingETPStatuses(ApplicationForFood applicationForFood, ApplicationForFoodStatus status) {
        this.applicationForFood = applicationForFood;
        this.status = status;
    }

    public ApplicationForFood getApplicationForFood() {
        return applicationForFood;
    }

    public void setApplicationForFood(ApplicationForFood applicationForFood) {
        this.applicationForFood = applicationForFood;
    }

    public ApplicationForFoodStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationForFoodStatus status) {
        this.status = status;
    }
}
