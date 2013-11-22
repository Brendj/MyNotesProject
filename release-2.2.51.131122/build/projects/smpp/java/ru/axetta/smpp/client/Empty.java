/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.smpp.client;

/**
 * Empty message
 */
public class Empty extends MSG {

    public Empty(String messageId, String msisdn, int ussd_sessid, String ussd_menuPath) {
        super(MessageType.Empty, messageId, msisdn, ussd_sessid, ussd_menuPath);
    }
}
