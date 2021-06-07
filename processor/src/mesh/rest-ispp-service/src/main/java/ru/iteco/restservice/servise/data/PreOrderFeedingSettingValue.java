package ru.iteco.restservice.servise.data;

import ru.iteco.restservice.model.enums.SettingsIds;

import java.text.ParseException;

/**
 * Created by nuc on 04.06.2021.
 */
public class PreOrderFeedingSettingValue {
    private int daysCount;  // кол-во дней, на которые должны создаваться заявки
    private int forbiddenDaysCount; // кол-во дней, на которые нельзя редактировать заявки

    public PreOrderFeedingSettingValue(String value) throws ParseException {
        String[] values = value.split(";");
        parse(values);
    }

    protected void parse(String[] values) throws ParseException {
        this.daysCount = Integer.parseInt(values[0]);
        this.forbiddenDaysCount = Integer.parseInt(values[1]);
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
