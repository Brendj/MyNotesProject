/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.axetta.ecafe.processor.core.persistence.ClientsMobileHistory;
import ru.axetta.ecafe.processor.core.persistence.ClientGuardianHistory;
import ru.axetta.ecafe.processor.core.persistence.RegistryChangeError;

import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UnsupportedImportClientRegisterService implements ImportClientRegisterService {

    @Override
    public StringBuffer syncClientsWithRegistry(long idOfOrg, boolean performChanges, StringBuffer logBuffer,
            boolean manualCheckout) throws Exception {
        throw new UnsupportedOperationException("Операция синхронизации с реестрами не поддерживается");
    }

    @Override
    public RegistryChangeError getRegistryChangeError(Long idOfRegistryChangeError) {
        throw new UnsupportedOperationException("Операция обработки ошибок не поддерживается");
    }

    @Override
    public List<RegistryChangeCallback> applyRegistryChangeBatch(List<Long> changesList, boolean fullNameValidation,
            String groupName, ClientsMobileHistory clientsMobileHistory, ClientGuardianHistory clientGuardianHistory) throws Exception {
        throw new UnsupportedOperationException("Операция подтверждения разногласий не поддерживается");
    }
}
