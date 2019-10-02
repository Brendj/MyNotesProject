/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.exceptions;

import ru.axetta.ecafe.processor.core.sync.handlers.syncsettings.request.ProcessResultEnum;

public class IncorrectEverySecondsException extends SyncProcessException {
    private static final String MESSAGE = "EverySeconds equal 0";

    public IncorrectEverySecondsException(){
        super(MESSAGE);
    }

    @Override
    public ProcessResultEnum getExceptionRes() {
        return ProcessResultEnum.INCORRECT_EVERY_SECOND;
    }
}
