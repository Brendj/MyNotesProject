/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.util;

public enum GroupManagementErrors {
    ORG_NOT_FOUND(101, "Организация не найдена."),
    GROUP_NOT_FOUND(102,"Группа не найдена"),
    USER_NOT_FOUND(103,"Пользователь не найден"),
    BUNCH_NOT_FOUND(104, "Связка не найдена"),
    EMPLOYEE_NOT_FOUND(105, "Сотрудник не найден"),
    EMPLOYEES_NOT_FOUND(106, "Сотрудники не найдены"),
    GROUPS_NOT_FOUND(107, "Группы не найдены"),
    GROUP_IS_EXISTS(108, "Данная группа уже есть в организации"),
    DISCOUNT_NOT_FOUND(109, "Льгота не найдена"),
    DISCOUNT_NOT_MODIFY(110, "По данной льготе запрещены изменения"),
    VALIDATION_ERROR(111, "Ошибка валидации запроса"),
    CLIENTS_NOT_FOUND(112, "Клиенты не найдены"),
    PLANORDERS_NOT_FOUND(113, "Планы питаний не найдены"),
    DATE_VALIDATION_ERROR(114, "Неверный формат даты");

    private int errorCode;
    private String errorMessage;

    private GroupManagementErrors(int errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
