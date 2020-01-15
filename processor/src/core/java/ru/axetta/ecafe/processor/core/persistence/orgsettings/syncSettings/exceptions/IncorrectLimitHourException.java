/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.exceptions;

import ru.axetta.ecafe.processor.core.sync.handlers.syncsettings.request.ProcessResultEnum;

public class IncorrectLimitHourException extends SyncProcessException {
    private static final String MESSAGE = "LimitStartHour or LimitEndHour must by between 0 and 24";

    public IncorrectLimitHourException(){
        super(MESSAGE);
    }

    @Override
    public ProcessResultEnum getExceptionRes() {
        return ProcessResultEnum.INCORRECT_START_OR_END_OF_LIMIT_HOUR;
    }
}
