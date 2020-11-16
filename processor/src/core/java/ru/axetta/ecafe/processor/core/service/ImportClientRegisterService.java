/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.persistence.ClientGuardianHistory;
import ru.axetta.ecafe.processor.core.persistence.RegistryChangeError;

import java.util.List;

public interface ImportClientRegisterService {
    StringBuffer syncClientsWithRegistry(long idOfOrg, boolean performChanges, StringBuffer logBuffer,
            boolean manualCheckout) throws Exception;

    RegistryChangeError getRegistryChangeError(Long idOfRegistryChangeError);

    List<RegistryChangeCallback> applyRegistryChangeBatch(List<Long> changesList, boolean fullNameValidation,
            String groupName, ClientGuardianHistory clientGuardianHistory) throws Exception;
}
