/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.HashMap;
import java.util.Map;

public enum ApplicationForFoodState {
    FILED(1010, "Подано", ""),
    TRY_TO_REGISTER(1040, "Заявление и документы доставлены в ОИВ", "Подано. Заявление передано в ОИВ и находится на рассмотрении."),
    DELIVERY_ERROR(103099, "Технический сбой", "К сожалению, произошел технический сбой и заявление не может быть доставлено. Просим вас отправить заявление повторно."),
    REGISTERED(1050, "Заявление зарегистрировано", "Ваше заявление зарегистрировано и принято к рассмотрению. Срок рассмотрения заявления до 8 рабочих дней."),
    PAUSED(1060, "Приостановлено", "Сообщаем Вам, что для рассмотрения Управляющим советом образовательной организации вопроса о предоставлении "
            + "Вашему ребенку питания за счет средств бюджета города Москвы необходимо представить в образовательную организацию в течение пяти "
            + "рабочих дней (от даты поступления уведомления в личный кабинет Портала) оригиналы документов, подтверждающих право на льготное "
            + "питание. Для согласования времени посещения образовательной организации предлагаем обратиться к классному руководителю."),
    RESUME(1160, "Возобновлено", "Предоставление услуги возобновлено."),
    DENIED(1080, "Услуга оказана. Решение отрицательное", ""),
    OK(1075, "Услуга оказана. Решение положительное.", "«Разрешено предоставление питания за счет средств бюджета города Москвы. "
            + "Разрешение вступает в силу с рабочего дня, следующего за днем направления данного уведомления."),
    INFORMATION_REQUEST_SENDED(7704, "Запрос сведений по МВ направлен", "Запрос сведений по МВ направлен."),
    INFORMATION_REQUEST_RECEIVED(7705, "Сведения на запрос по МВ получены", "Сведения на запрос по МВ получены."),
    RESULT_PROCESSING(1052, "Формирование результата", "Формирование результата.");

    private final Integer code;
    private final String description;
    private final String note;
    private static Map<Integer,ApplicationForFoodState> mapInt = new HashMap<Integer,ApplicationForFoodState>();
    private static Map<String,ApplicationForFoodState> mapStr = new HashMap<String,ApplicationForFoodState>();
    static {
        for (ApplicationForFoodState value : ApplicationForFoodState.values()) {
            mapInt.put(value.getCode(), value);
            mapStr.put(value.toString(), value);
        }
    }

    ApplicationForFoodState(Integer code, String description, String note) {
        this.code = code;
        this.description = description;
        this.note = note;
    }

    public Integer getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getNote() {
        return note;
    }

    public static ApplicationForFoodState fromCode(Integer id) {
        return mapInt.get(id);
    }

    public static ApplicationForFoodState fromDescription(String description) {
        return mapStr.get(description);
    }
}
