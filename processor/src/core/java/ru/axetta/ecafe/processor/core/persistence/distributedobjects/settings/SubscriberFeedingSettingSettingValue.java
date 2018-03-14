package ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings;

import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 18.12.13
 * Time: 16:45
 * To change this template use File | Settings | File Templates.
 */
public class SubscriberFeedingSettingSettingValue extends AbstractParserBySettingValue {

    private int dayRequest; // Количество дней, на которые оформляются заявки на поставку
    private int dayDeActivate;   // Количество дней, пропустив которые, клиент приостанавливает свою подписку
    private boolean enableFeeding;   // Включить автоматическую приостановку/возобновление подписок на услугу АП в зависимости от посещения учреждения
    private int hoursForbidChange; // Количество часов, в течение которых запрещено редактировать заявки
    private boolean sixWorkWeek; // Шестидневный план рабочих дней
    private int daysToForbidChangeInPos; // Количество рабочих дней блокировки баланса с учетом стоимости питания, отмеченного в циклограмме
    private int dayCreateVP; //Количество дней, на которые создаются заявки вариативного питания
    private int hoursForbidVP; //Количество часов, в течение которых запрещено редактировать заявки вариативного питания
    private int hoursForbidPP; //Количество часов, в течение которых запрещено редактировать заявки по предзаказам

    //значения по умолчанию
    private static final int DAY_REQUEST = 5; //5;2;0;1;0;3
    private static final int DAY_DEACTIVATE = 2;
    private static final boolean ENABLE_FEEDING = false;
    private static final int HOURS_FORBID_CHANGE = 1;
    private static final boolean SIX_WORK_WEEK = false;
    private static final int DAYS_FORBID_CHANGE_POS = 3;
    private static final int DAY_CREATE_VP = 0;
    private static final int HOURS_FORBID_VP = 0;
    private static final int HOURS_FORBID_PP = 0;

    public SubscriberFeedingSettingSettingValue(String[] values) throws ParseException {
        super(values);
    }

    @Override
    protected void parse(String[] values) throws ParseException {
        this.dayRequest = safeParseInt(values, 0, DAY_REQUEST);
        this.dayDeActivate = safeParseInt(values, 1, DAY_DEACTIVATE);
        this.enableFeeding = safeParseBoolean(values, 2, ENABLE_FEEDING);
        this.hoursForbidChange = safeParseInt(values, 3, HOURS_FORBID_CHANGE);
        this.sixWorkWeek = safeParseBoolean(values, 4, SIX_WORK_WEEK);
        this.daysToForbidChangeInPos = safeParseInt(values, 5, DAYS_FORBID_CHANGE_POS);
        this.dayCreateVP = safeParseInt(values, 6, DAY_CREATE_VP);
        this.hoursForbidVP = safeParseInt(values, 7, HOURS_FORBID_VP);
        this.hoursForbidPP = safeParseInt(values, 8, HOURS_FORBID_PP);
    }

    private int safeParseInt(String[] values, int index, int default_value) {
        try {
            return Integer.parseInt(values[index]);
        } catch (Exception e) {
            return default_value;
        }
    }

    private boolean safeParseBoolean(String[] values, int index, boolean default_value) {
        try {
            return values[index].equals("1");
        } catch (Exception e) {
            return default_value;
        }
    }

    @Override
    public String build() {
        //return dayRequest + ";" + dayDeActivate + ";" + (enableFeeding ? 1 : 0) + ";" + dayForbidChange + ";";
        return dayRequest + ";" + dayDeActivate + ";" + (enableFeeding ? 1 : 0) + ";" + hoursForbidChange + ";" + (
                sixWorkWeek ? 1 : 0) + ";" + daysToForbidChangeInPos + ";" + dayCreateVP + ";" + hoursForbidVP + ";"
                + hoursForbidPP + ";";
    }

    @Override
    public boolean check() {
        return true;
    }

    public int getDayRequest() {
        return dayRequest;
    }

    public void setDayRequest(int dayRequest) {
        this.dayRequest = dayRequest;
    }

    public int getDayDeActivate() {
        return dayDeActivate;
    }

    public void setDayDeActivate(int dayDeActivate) {
        this.dayDeActivate = dayDeActivate;
    }

    public boolean isEnableFeeding() {
        return enableFeeding;
    }

    public void setEnableFeeding(boolean enableFeeding) {
        this.enableFeeding = enableFeeding;
    }

    //public int getDayForbidChange() {
    //    return dayForbidChange;
    //}
    //
    //public void setDayForbidChange(int dayForbidChange) {
    //    this.dayForbidChange = dayForbidChange;
    //}

    public int getHoursForbidChange() {
        return hoursForbidChange;
    }

    public int getDaysForbidChange() {
        int dayForbidChange = 0;
        if (hoursForbidChange < 24) {
            dayForbidChange++;
        } else {
            dayForbidChange = (hoursForbidChange % 24 == 0 ? hoursForbidChange / 24 : hoursForbidChange / 24 + 1);
        }
        return dayForbidChange ;
    }

    public void setHoursForbidChange(int hoursForbidChange) {
        this.hoursForbidChange = hoursForbidChange;
    }

    public int getDaysToForbidChangeInPos() {
        return daysToForbidChangeInPos;
    }

    public void setDaysToForbidChangeInPos(int daysToForbidChangeInPos) {
        this.daysToForbidChangeInPos = daysToForbidChangeInPos;
    }

    public boolean isSixWorkWeek() {
        return sixWorkWeek;
    }

    public void setSixWorkWeek(boolean sixWorkWeek) {
        this.sixWorkWeek = sixWorkWeek;
    }

    public int getDayCreateVP() {
        return dayCreateVP;
    }

    public void setDayCreateVP(int dayCreateVP) {
        this.dayCreateVP = dayCreateVP;
    }

    public int getHoursForbidVP() {
        return hoursForbidVP;
    }

    public void setHoursForbidVP(int hoursForbidVP) {
        this.hoursForbidVP = hoursForbidVP;
    }

    public int getHoursForbidPP() {
        return hoursForbidPP;
    }

    public void setHoursForbidPP(int hoursForbidPP) {
        this.hoursForbidPP = hoursForbidPP;
    }
}
