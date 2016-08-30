package ru.axetta.ecafe.processor.core.sms.emp.type;

import ru.axetta.ecafe.processor.core.persistence.Client;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 24.03.16
 * Time: 11:04
 * To change this template use File | Settings | File Templates.
 */
public class EMPSummaryWeeklyEventType extends EMPAbstractEventType {
    protected static final String NAME = "Итоговые данные о событиях прохода и питания обучающегося в ОО за неделю";
    protected static final String TEXT = "Здравствуйте! В период %Startdate% - %Enddate% Ваш ребенок %surname% %name% "
        + "присутствовал в ОО следующее количество дней: amountEntereventsDate%. Среднее время, проведенное в ОО: %amountEntereventsTimeMed%; "
        + "максимальное время, проведенное в ОО: %amountEntereventsTimeMax%; минимальное время, проведенное в ОО: %amountEntereventsTimeMin%.\n"
        + "Движения по лицевому счету %account%: баланс на начало недели: %balanceStartdate%; баланс на конец недели: %balance%; "
        + "сумма всех покупок: %amountBuyAll%; средняя сумма покупок: %amountBuyAllMed%; лимит трат: %limit%. "
        + "Количество дней, когда было получено горячее питание: %amountComplexDate%. Топ меню 1 %TopMenu1%, %AmountMenu1%, "
        + "%AmountSummMenu1%. Топ меню 2 %TopMenu2%, %AmountMenu2%, %AmountSummMenu2%. Топ меню 3 %TopMenu3%, %AmountMenu3%, "
        + "%AmountSummMenu3%. Топ меню 4 %TopMenu4%, %AmountMenu4%, %AmountSummMenu4%. Топ меню 5 %TopMenu5%, %AmountMenu5%, %AmountSummMenu5%";

    public EMPSummaryWeeklyEventType() {
        stream = STREAM;
        type = EMPEventTypeFactory.SUMMARY_WEEKLY_EVENT;
        name = NAME;
        text = TEXT;
    }

    @Override
    public void parse(Client client, Map<String, Object> additionalParams) {
        parseClientSimpleInfo(client);
    }

    @Override
    public void parse(Client child, Client guardian, Map<String, Object> additionalParams) {
        parseChildAndGuardianInfo(child, guardian);
    }
}
