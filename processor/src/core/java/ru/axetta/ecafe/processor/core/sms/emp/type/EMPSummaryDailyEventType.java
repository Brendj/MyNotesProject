/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.emp.type;

import ru.axetta.ecafe.processor.core.persistence.Client;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 23.03.16
 * Time: 10:28
 * To change this template use File | Settings | File Templates.
 */
public class EMPSummaryDailyEventType extends EMPAbstractEventType {
    protected static final String NAME = "Итоговые данные о событиях прохода и питания обучающегося в ОО за сутки";
    /*protected static final String TEXT = "Здравствуйте! В период %Startdate% Ваш ребенок %surname% %name% присутствовал в ОО следующее количество дней: " +
            "%amountEntereventsDate%. Вход %StartTime%, выход %EndTime%, время, проведенное в ОО, %amountEntereventsTime%. " +
            "Движения по лицевому счету %account%: баланс на начало дня: %balanceStartdate%; баланс на конец дня: %balance%; " +
            "сумма всех покупок: %amountBuyAll%; лимит трат: %limit%. Количество дней, когда было получено горячее питание: %amountComplexDate%.";*/

    protected static final String TEXT = "Добрый день, %guardian_name% %guardian_surname%! "
            + "Хотим рассказать, как прошел у вашего ребенка этот день в школе: %name% "
            + "%DayWeeks% %date%. %StartTime% Пришел в школу %EndTime% Покинул школу "
            + "Остаток на лицевом счете по состоянию на 19:00 %date% %balance% руб. "
            + "%menuDetail% Итого Сумма расходов по электронной карте за день составила %amountBuyAll% руб. "
            + "Подробная детализация доступна в личном кабинете. "
            + "При сохранении текущей динамики расходов пополнение потребуется %balanceOnDays%";

    public EMPSummaryDailyEventType() {
        stream = STREAM;
        type = EMPEventTypeFactory.SUMMARY_DAILY_EVENT;
        name = NAME;
        text = TEXT;
    }

    @Override
    public void parse(Client client) {
        parseClientSimpleInfo(client, type);
    }

    @Override
    public void parse(Client child, Client guardian) {
        parseChildAndGuardianInfo(child, guardian, type);
    }
}
