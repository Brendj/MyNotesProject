/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.restservice.db.repo.readonly;

import ru.iteco.restservice.model.ClientGuardian;
import ru.iteco.restservice.model.ClientGuardianNotificationSettings;
import ru.iteco.restservice.model.enums.ClientNotificationSettingType;

import org.springframework.data.repository.CrudRepository;

public interface ClientGuardianNotificationSettingsReadonlyRepo extends CrudRepository<ClientGuardianNotificationSettings, Long> {
    ClientGuardianNotificationSettings getClientGuardianNotificationSettingsByClientGuardianAndType(ClientGuardian clientGuardian,
            ClientNotificationSettingType type);
}
