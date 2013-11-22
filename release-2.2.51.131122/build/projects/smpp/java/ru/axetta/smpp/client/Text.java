/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.smpp.client;

import org.smpp.Data;

import java.io.UnsupportedEncodingException;

/**
 * PoR message
 */
public class Text extends MSG {

    private byte dataCoding;

    public Text(String messageId, String msisdn, byte[] data, byte dataCoding, int ussd_sessid, String ussd_menuPath) {
        super(MessageType.Text, messageId, msisdn, ussd_sessid, ussd_menuPath);
        this.data = data;
        this.dataCoding = dataCoding;
        try {
            this.isDeliveryReport = new String(data, this.dataCoding == 0x8 ? Data.ENC_UTF16_BE : Data.ENC_GSM7BIT).matches(".*id:[a-fA-F0-9]+.*dlvrd:[a-zA-Z0-9]+.*stat:[a-zA-Z]+.*");
        } catch (UnsupportedEncodingException ignored) {
        }
    }

    /**
     * Message text getter.
     *
     * @return message text.
     */
    public byte[] getTextBytes() {
        return data;
    }

    /**
     * Data coding getter. 8 if UCS2 (russian).
     *
     * @return Data coding received from SMSC.
     */
    public byte getDataCoding() {
        return dataCoding;
    }
}
