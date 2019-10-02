/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.syncsettings.request;

public enum ProcessResultEnum {
    OK(0, "OK"),
    CONTENT_TYPE_IS_NULL(100, "Не задан тип контента синхронизации"),
    CONTENT_TYPE_NOT_EXIST(105, "Заданный тип не существует"),
    AMBIGUITY_IN_CONCRETE_TIME_AND_EVERY_SECOND(110,
            "Задано перечисление времени запуска синхранизации одновременно с переодичностью, однако допустимо использование только одного параметра"),
    INCORRECT_EVERY_SECOND(115, "Переодичность запуска синхранизации не должно быть ровно 0"),
    AMBIGUITY_IN_START_AND_END_OF_LIMIT_HOUR(120,
            "Время начала и время конца запуска синхранизации должны быть заданы парно, либо не заданы вовсе"),
    INCORRECT_START_OR_END_OF_LIMIT_HOUR(125,
            "Время начала и время конца запуска синхранизации должны лежать в пределах от 0 до 24 включительно"),
    INTERNAL_ERROR(500, "Внутренняя ошибка сервера");


    ProcessResultEnum(Integer code, String description){
        this.code = code;
        this.description = description;
    }

    private Integer code;
    private String description;

    public Integer getCode() {
        return code;
    }

    @Override
    public String toString(){
        return description;
    }
}
