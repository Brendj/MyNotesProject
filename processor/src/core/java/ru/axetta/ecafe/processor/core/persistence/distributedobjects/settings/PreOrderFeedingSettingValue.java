/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings;

import java.text.ParseException;

public class PreOrderFeedingSettingValue extends AbstractParserBySettingValue{

    private int daysCount;  // кол-во дней, на которые должны создаваться заявки
    private int forbiddenDaysCount; // кол-во дней, на которые нельзя редактировать заявки

    public PreOrderFeedingSettingValue(String[] values) throws ParseException {
        super(values);
    }

    @Override
    protected void parse(String[] values) throws ParseException {
        this.daysCount = Integer.parseInt(values[0]);
        this.forbiddenDaysCount = Integer.parseInt(values[1]);
    }

    @Override
    public String build() {
        return daysCount+";"+forbiddenDaysCount+";";
    }

    @Override
    public boolean check() {
        return true;
    }

    public int getDaysCount() {
        return daysCount;
    }

    public void setDaysCount(int daysCount) {
        this.daysCount = daysCount;
    }

    public int getForbiddenDaysCount() {
        return forbiddenDaysCount;
    }

    public void setForbiddenDaysCount(int forbiddenDaysCount) {
        this.forbiddenDaysCount = forbiddenDaysCount;
    }
}
