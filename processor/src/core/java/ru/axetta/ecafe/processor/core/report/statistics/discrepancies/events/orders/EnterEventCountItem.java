/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.discrepancies.events.orders;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.02.14
 * Time: 15:51
 * To change this template use File | Settings | File Templates.
 */
class EnterEventCountItem {

    private Long totalCount;
    private final Date doneDate;
    private Long count;

    EnterEventCountItem(Long totalCount, Date doneDate) {
        this.totalCount = totalCount;
        this.doneDate = doneDate;
        count=1L;
    }

    public Long getTotalCount() {
        return totalCount;
    }

    public void setTotalCountAndCount(Long totalCount) {
        count++;
        this.totalCount = totalCount;
    }

    public void setTotalCount(Long totalCount) {
        this.totalCount = totalCount;
    }

    Date getDoneDate() {
        return doneDate;
    }

    Long getCount() {
        return count;
    }
}
