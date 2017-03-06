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
    //private int dayForbidChange; // Количество дней, в течение которых запрещено редактировать заявки
    private int hoursForbidChange; // Количество часов, в течение которых запрещено редактировать заявки
    private int daysToForbidChangeInPos; // Количество рабочих дней блокировки баланса с учетом стоимости питания, отмеченного в циклограмме
    private boolean sixWorkWeek; // Шестидневный план рабочих дней
    private int dayCreateVP; //Количество дней, на которые создаются заявки вариативного питания
    private int hoursForbidVP; //Количество часов, в течение которых запрещено редактировать заявки вариативного питания

    public SubscriberFeedingSettingSettingValue(String[] values) throws ParseException {
        super(values);
    }

    @Override
    protected void parse(String[] values) throws ParseException {
        this.dayRequest = Integer.parseInt(values[0]);
        this.dayDeActivate = Integer.parseInt(values[1]);
        this.enableFeeding = values[2].equals("1");
        //this.dayForbidChange = Integer.parseInt(values[3]);
        this.hoursForbidChange = Integer.parseInt(values[3]);

        if (values.length < 5) {
            sixWorkWeek = false;
        } else {
            this.sixWorkWeek = values[4].equals("1");
        }

        if (values.length < 6) {
            this.daysToForbidChangeInPos = 0;
        } else {
            this.daysToForbidChangeInPos = Integer.parseInt(values[5]);
        }

        if (values.length < 7) {
            this.dayCreateVP = 0;
        } else {
            this.dayCreateVP = Integer.parseInt(values[6]);
        }

        if (values.length < 8) {
            this.hoursForbidVP = 0;
        } else {
            this.hoursForbidVP = Integer.parseInt(values[7]);
        }
    }

    @Override
    public String build() {
        //return dayRequest + ";" + dayDeActivate + ";" + (enableFeeding ? 1 : 0) + ";" + dayForbidChange + ";";
        return dayRequest + ";" + dayDeActivate + ";" + (enableFeeding ? 1 : 0) + ";" + hoursForbidChange + ";" + (
                sixWorkWeek ? 1 : 0) + ";" + daysToForbidChangeInPos + ";" + dayCreateVP + ";" + hoursForbidVP + ";";
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
}
