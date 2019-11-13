/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.manager.modifier;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.settings.ECafeSettings;

/**
 * Created by i.semenov on 29.10.2019.
 */
public class ModifierTypeFactory {

    public static DistributedObjectModifier createModifier(DistributedObject distributedObject) {
        if (distributedObject instanceof ECafeSettings) {
            return new EcafeSettingsModifier();
        } else {
            return new CommonModifier();
        }
    }
}
