/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.good.request;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 24.02.14
 * Time: 12:19
 * To change this template use File | Settings | File Templates.
 */
class RequestItem {
    final String number;
    final Date doneDate;
    final Date lastCreate;
    final Date lastUpdate;

    public RequestItem(String number, Date doneDate, Date lastCreate, Date lastUpdate) {
        this.number = number;
        this.doneDate = doneDate;
        this.lastCreate = lastCreate;
        this.lastUpdate = lastUpdate;
    }
}
