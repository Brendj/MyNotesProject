/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.smpp.client;

/**
 * PoR message
 */
public class PoR extends MSG {

    int RPL;
    int RHL;
    String TAR;
    byte[] CNTR_AND_PCNTR;
    byte respStatusCode;
    byte[] checkSum;

    public PoR(String messageId, String msisdn, int RPL, int RHL, String TAR, byte[] CNTR_AND_PCNTR, byte[] data, byte respStatusCode, byte[] checkSum, int ussd_sessid, String ussd_menuPath) {
        super(MessageType.PoR, messageId, msisdn, ussd_sessid, ussd_menuPath);
        this.RPL = RPL;
        this.RHL = RHL;
        this.TAR = TAR;
        this.CNTR_AND_PCNTR = CNTR_AND_PCNTR;
        this.data = data;
        this.respStatusCode = respStatusCode;
        this.checkSum = checkSum;
    }

    public int getRPL() {
        return RPL;
    }

    public int getRHL() {
        return RHL;
    }

    public String getTAR() {
        return TAR;
    }

    public byte[] getCNTR_AND_PCNTR() {
        return CNTR_AND_PCNTR;
    }

    public byte getRespStatusCode() {
        return respStatusCode;
    }

    public byte[] getBody() {
        return data;
    }

    public byte[] getCheckSum() {
        return checkSum;
    }
}
