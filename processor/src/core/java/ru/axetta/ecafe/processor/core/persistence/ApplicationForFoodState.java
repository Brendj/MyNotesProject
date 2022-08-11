/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

public enum ApplicationForFoodState {
    FILED("1010", "Подано", ""),
    TRY_TO_REGISTER("1040", "Заявление доставлено в ОИВ", "Подано. Заявление передано в ОИВ и находится на рассмотрении."),
    DELIVERY_ERROR("103099", "Технический сбой", "К сожалению, произошел технический сбой и заявление не может быть доставлено. Просим вас отправить заявление повторно."),
    REGISTERED("1050", "Заявление зарегистрировано", "Ваше заявление зарегистрировано и принято к рассмотрению. Срок рассмотрения заявления до 8 рабочих дней."),
    PAUSED("1060", "Приостановлено", "Сообщаем вам, что для рассмотрения Управляющим советом образовательной организации вопроса о предоставлении вашему ребенку "
            + "питания за счет средств бюджета города Москвы необходимо представить в образовательную организацию в течение пяти рабочих дней "
            + "(от даты поступления уведомления в Личный кабинет Портала) оригиналы документов, подтверждающих право на льготное питание. "
            + "Для согласования времени посещения образовательной организации предлагаем обратиться к классному руководителю."),
    RESUME("1160", "Возобновлено", "Предоставление услуги возобновлено."),
    DENIED_BENEFIT("1080.1", "Услуга оказана. Решение отрицательное", "Основание для отказа: По результатам межведомственной проверки льготный статус ребенка не подтвержден"),
    DENIED_GUARDIANSHIP("1080.2", "Услуга оказана. Решение отрицательное", "Основание для отказа: По результатам межведомственной проверки не подтверждено родство."),
    DENIED_OLD("1080.3", "Услуга оказана. Решение отрицательное", "Основание для отказа: По результатам межведомственного взаимодействия с Департаментом труда и социальной защиты населения города "
            + "Москвы льготный статус ребенка не подтвержден"),
    OK("1075", "Услуга оказана. Решение положительное", "Разрешено предоставление питания за счет средств бюджета города Москвы. "
            + "Разрешение вступает в силу с рабочего дня, следующего за днем направления данного уведомления."),
    INFORMATION_REQUEST_SENDED("7704", "Запрос сведений по МВ направлен", "Запрос сведений по МВ направлен."),
    INFORMATION_REQUEST_RECEIVED("7705", "Сведения на запрос по МВ получены", "Сведения на запрос по МВ получены."),
    RESULT_PROCESSING("1052", "Формирование результата", "Формирование результата."),
    GUARDIANSHIP_REQUEST_SENDED("7704.2", "Запрос сведений по МВ направлен", "ПФР. Сведения о СНИЛС застрахованного лица с учётом дополнительных сведений о месте рождения, документе, удостоверяющем личность."),
    DOC_VALIDITY_REQUEST_SENDED("7704.1", "Запрос сведений по МВ направлен", "МВД. Сведения о действительности паспорта гражданина РФ.");

    private final String code;
    private final String description;
    private final String note;
    private static Map<String,ApplicationForFoodState> mapCode = new HashMap<String,ApplicationForFoodState>();
    private static Map<String,ApplicationForFoodState> mapStr = new HashMap<String,ApplicationForFoodState>();
    static {
        for (ApplicationForFoodState value : ApplicationForFoodState.values()) {
            mapCode.put(value.getCode(), value);
            mapStr.put(value.toString(), value);
        }
    }

    ApplicationForFoodState(String code, String description, String note) {
        this.code = code;
        this.description = description;
        this.note = note;
    }

    public Integer getPureCode() {
        if (code.contains(".")) {
            return Integer.valueOf(StringUtils.substringBefore(code, "."));
        } else {
            return Integer.valueOf(code);
        }
    }

    public String getReason() {
        if (code.contains(".")) {
            return StringUtils.substringAfter(code, ".");
        } else {
            return null;
        }
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    public String getNote() {
        return note;
    }

    public static ApplicationForFoodState fromCode(String id) {
        return mapCode.get(id);
    }

    public static ApplicationForFoodState fromDescription(String description) {
        return mapStr.get(description);
    }
}
