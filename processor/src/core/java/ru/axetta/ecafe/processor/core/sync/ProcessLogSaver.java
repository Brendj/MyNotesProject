/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

@Component
@DependsOn("runtimeContext")
public class ProcessLogSaver {
    public void run() {
        RuntimeContext.getInstance().getSyncLogger().runRegisterSyncRequestInDb();
    }
}
