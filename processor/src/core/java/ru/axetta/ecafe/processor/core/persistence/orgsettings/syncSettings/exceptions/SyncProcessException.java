/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.exceptions;

import ru.axetta.ecafe.processor.core.sync.handlers.syncsettings.request.ProcessResultEnum;

public abstract class SyncProcessException extends RuntimeException {

    public abstract ProcessResultEnum getExceptionRes();

    protected SyncProcessException(String s){
        super(s);
    }
}
