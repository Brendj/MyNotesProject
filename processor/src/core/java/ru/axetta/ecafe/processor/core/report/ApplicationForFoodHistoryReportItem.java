/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.ApplicationForFoodHistory;

import java.util.Date;

public class ApplicationForFoodHistoryReportItem extends ApplicationForFoodReportItem {
    private Date sendDate;
    private ApplicationForFoodHistory applicationForFoodHistory;

    public ApplicationForFoodHistoryReportItem(ApplicationForFoodHistory applicationForFoodHistory) {
        this.createdDate = applicationForFoodHistory.getCreatedDate();
        this.applicationForFoodStatus = applicationForFoodHistory.getStatus();
        this.sendDate = applicationForFoodHistory.getSendDate();
        this.applicationForFoodHistory = applicationForFoodHistory;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public ApplicationForFoodHistory getApplicationForFoodHistory() {
        return applicationForFoodHistory;
    }

    public void setApplicationForFoodHistory(ApplicationForFoodHistory applicationForFoodHistory) {
        this.applicationForFoodHistory = applicationForFoodHistory;
    }
}
