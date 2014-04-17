/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.doGroups;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.RegistryTalon;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 14.08.13
 * Time: 13:39
 */

public class SettingsGroup extends AbstractGroup {

    @Override
    protected void fill() {
        doClassMap.put("ECafeSettings", new DOSyncClass(ECafeSettings.class, 0));
        doClassMap.put("RegistryTalon", new DOSyncClass(RegistryTalon.class, 0));
    }
}
