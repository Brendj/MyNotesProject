/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.service;

import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.dto.CreateOrUpdateGuardianRequest;
import ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.dto.CreateOrUpdateGuardianResponse;
import ru.axetta.ecafe.processor.web.partner.schoolapi.guardians.dto.DeleteGuardianResponse;

public interface SchoolApiGuardiansService {
    DeleteGuardianResponse deleteGuardian(long idOfRecord, User user);
    CreateOrUpdateGuardianResponse createOrUpdateGuardian(CreateOrUpdateGuardianRequest createOrUpdateGuardianRequest, User user);
}
