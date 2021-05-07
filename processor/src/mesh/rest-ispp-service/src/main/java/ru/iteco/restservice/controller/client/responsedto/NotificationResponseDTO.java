/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.client.responsedto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@Schema(name = "NotificationResponseDTO", description = "Параметры настроек оповещения клиента")
public class NotificationResponseDTO {

    @Schema(title = "Код настройки", example = "1000000000")
    private Long settingsCode;

    @Schema(title = "Расшифровка кода", example = "Оповещение о пополнениях")
    private String settingsName;

    public Long getSettingsCode() {
        return settingsCode;
    }

    public void setSettingsCode(Long settingsCode) {
        this.settingsCode = settingsCode;
    }

    public String getSettingsName() {
        return settingsName;
    }

    public void setSettingsName(String settingsName) {
        this.settingsName = settingsName;
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
        return Objects.equals(settingsCode, that.settingsCode) && Objects.equals(settingsName, that.settingsName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(settingsCode, settingsName);
    }
}
