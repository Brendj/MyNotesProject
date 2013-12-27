package ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings;

import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 18.12.13
 * Time: 16:45
 * To change this template use File | Settings | File Templates.
 */
public class SubscriberFeedingSettingSettingValue  extends AbstractParserBySettingValue{

    private int dayRequest; // Количество дней, на которые оформляются заявки на поставку
    private int dayDeActivate;   // Количество дней, пропустив которые, клиент приостанавливает свою подписку
    private boolean enableFeeding;   // Включить автоматическую приостановку/возобновление подписок на услугу АП в зависимости от посещения учреждения
    private int dayForbidChange; // Количество дней, в течение которых запрещено редактировать заявки

    public SubscriberFeedingSettingSettingValue(String[] values) throws ParseException {
        super(values);
    }

    @Override
    protected void parse(String[] values) throws ParseException {
        this.dayRequest = Integer.parseInt(values[0]);
        this.dayDeActivate = Integer.parseInt(values[1]);
        this.enableFeeding = values[2].equals("1");
        this.dayForbidChange = 1; //Integer.parseInt(values[3]);
    }

    @Override
    public String build() {
        return dayRequest + ";" + dayDeActivate + ";" + (enableFeeding ? 1 : 0) + ";" + dayForbidChange + ";";
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

    public int getDayForbidChange() {
        return dayForbidChange;
    }

    public void setDayForbidChange(int dayForbidChange) {
        this.dayForbidChange = dayForbidChange;
    }
}
