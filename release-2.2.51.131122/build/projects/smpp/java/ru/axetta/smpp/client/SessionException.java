/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.smpp.client;

import java.io.IOException;

class SessionException extends IOException implements Error {


    public final int errCode;

    public SessionException() {
        errCode = ERRCODE_NOT_SET;
    }

    public SessionException(String message) {
        super(message);
        errCode = ERRCODE_NOT_SET;
    }

    public SessionException(String message, Throwable cause) {
        super(message, cause);
        errCode = ERRCODE_NOT_SET;
    }

    public SessionException(String message, int errCode) {
        super(message);
        this.errCode = errCode;
    }

    public SessionException(Throwable cause) {
        super(cause);
        errCode = ERRCODE_NOT_SET;
    }
}
