/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.statistics.telephone.number;

import ru.axetta.ecafe.processor.core.report.BasicReport;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 20.01.14
 * Time: 13:37
 * To change this template use File | Settings | File Templates.
 */
public class TelephoneNumberCountReport extends BasicReport {

    private final List<TelephoneNumberCountItem> telephoneNumberCountItems;

    public TelephoneNumberCountReport() {
        super();
        this.telephoneNumberCountItems = Collections.emptyList();
    }

    public TelephoneNumberCountReport(Date generateTime, long generateDuration,
            List<TelephoneNumberCountItem> telephoneNumberCountItems) {
        super(generateTime, generateDuration);
        this.telephoneNumberCountItems = telephoneNumberCountItems;
    }

    public List<TelephoneNumberCountItem> getTelephoneNumberCountItems() {
        return telephoneNumberCountItems;
    }
}
