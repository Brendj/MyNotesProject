/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.requestsAndOrdersReport;

/**
 * Created with IntelliJ IDEA.
 * User: ziganshin
 * Date: 08.04.15
 * Time: 17:40
 * To change this template use File | Settings | File Templates.
 */
public class NoDataFoundException extends Exception {

    public NoDataFoundException() {
        super();
    }

    public NoDataFoundException(String message) {
        super(message);
    }

    public NoDataFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoDataFoundException(Throwable cause) {
        super(cause);
    }
}
