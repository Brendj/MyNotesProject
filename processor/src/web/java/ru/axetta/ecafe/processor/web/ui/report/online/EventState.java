/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.online;

/**
 * Created by anvarov on 03.05.18.
 */
public class EventState {

    public static String[] values = {
            "вход", "выход", "проход запрещен", "взлом турникета", "событие без прохода", "отказ от прохода",
            "повторный вход", "повторный выход", "обнаружен на подносе карты внутри здания",
            "отмечен в классном журнале через внешнюю систему", "отмечен учителем внутри здания", "запрос на вход",
            "запрос на выход"};

    public static String[] values() {
        return values;
    }
}
