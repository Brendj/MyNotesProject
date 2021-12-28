/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.client.responsedto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@Schema(name = "NotificationResponseDTO", description = "Список доступных типов уведомлений")
public class NotificationResponseDTO {

    @Schema(title = "Наименование уведомления", example = "Оповещение о пополнениях")
    private String nameOfNotification;

    @Schema(title = "Тип уведомления", example = "1000000000")
    private Long typeOfNotification;

    public Long getTypeOfNotification() {
        return typeOfNotification;
    }

    public void setTypeOfNotification(Long typeOfNotification) {
        this.typeOfNotification = typeOfNotification;
    }

    public String getNameOfNotification() {
        return nameOfNotification;
    }

    public void setNameOfNotification(String nameOfNotification) {
        this.nameOfNotification = nameOfNotification;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NotificationResponseDTO that = (NotificationResponseDTO) o;
        return Objects.equals(typeOfNotification, that.typeOfNotification) && Objects.equals(nameOfNotification, that.nameOfNotification);
    }

    @Override
    public int hashCode() {
        return Objects.hash(typeOfNotification, nameOfNotification);
    }
}
