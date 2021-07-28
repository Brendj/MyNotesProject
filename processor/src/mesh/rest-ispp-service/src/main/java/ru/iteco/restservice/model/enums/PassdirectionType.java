/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.model.enums;

public enum PassdirectionType {
    ENTRY(0,"Вход"),
    EXIT(1, "Выход"),
    PASSAGE_IS_FORBIDDEN(2, "Проход запрещен"),
    TURNSTILE_IS_BROKEN(3, "Взлом турникета"),
    EVENT_WITHOUT_PASSAGE(4, "Событие без прохода"),
    PASSAGE_RUFUSAL(5, "Отказ от прохода"),
    RE_ENTRY(6, "Повторный вход"),
    RE_EXIT(7, "Повторный выход"),
    QUERY_FOR_ENTER(8, "Запрос на вход"),
    QUERY_FOR_EXIT(9, "Запрос на выход"),
    DETECTED_INSIDE(100, "Обнаружен на подносе карты внутри здания"),
    CHECKED_BY_TEACHER_EXT(101,"Отмечен в классном журнале через внешнюю систему"),
    CHECKED_BY_TEACHER_INT(102,"Отмечен учителем внутри здания"),
    ENTRY_WITHOUT_CARD(112, "Проход без карты"),
    BLACK_LIST(202, "Посетитель из черного списка");

    private final Integer direction;
    private final String directionText;

    PassdirectionType(Integer direction, String directionText) {
        this.direction = direction;
        this.directionText = directionText;
    }

    public Integer getDirection() {
        return direction;
    }

    public String getDirectionText() {
        return directionText;
    }

    @Override
    public String toString() {
        return directionText;
    }
}
