/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.controller.client.responsedto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Objects;

@Schema(name = "NotificationResponseErrorDTO", description = "Неподдерживаемые типы уведомлений")
public class NotificationResponseErrorDTO {

    @Schema(title = "Код настройки", example = "1000000000")
    private Long settingsCode;

    public Long getSettingsCode() {
        return settingsCode;
    }

    public void setSettingsCode(Long settingsCode) {
        this.settingsCode = settingsCode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NotificationResponseErrorDTO that = (NotificationResponseErrorDTO) o;
        return Objects.equals(settingsCode, that.settingsCode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(settingsCode);
    }
}
