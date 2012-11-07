/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.smpp.client;

import java.util.Arrays;

/**
 * Ticket message class
 */
public class Ticket extends MSG implements DecryptableMSG {
    private int RPL;
    private int RHL;
    private int SPI;
    private int KIC;
    private int KID;
    private String TAR;
    private byte[] CNTR_AND_PCNTR;
    private byte[] checkSum;

    private byte[] decrypted;
    private byte[] kc;

    public Ticket(String messageId, String msisdn, int RPL, int RHL, int SPI, int KIC, int KID, String TAR, byte[] CNTR_AND_PCNTR, byte[] data, byte[] checkSum, int ussd_sessid, String ussd_menuPath) {
        super(MessageType.Ticket, messageId, msisdn, ussd_sessid, ussd_menuPath);
        this.RPL = RPL;
        this.RHL = RHL;
        this.SPI = SPI;
        this.KIC = KIC;
        this.KID = KID;
        this.TAR = TAR;
        this.CNTR_AND_PCNTR = CNTR_AND_PCNTR;
        this.data = data;
        this.checkSum = checkSum;
        this.decrypted = null;
    }

    public int getRPL() {
        return RPL;
    }

    public int getRHL() {
        return RHL;
    }

    public int getSPI() {
        return SPI;
    }

    public int getKIC() {
        return KIC;
    }

    public int getKID() {
        return KID;
    }

    public String getTAR() {
        return TAR;
    }

    public byte[] getCNTR_AND_PCNTR() {
        return CNTR_AND_PCNTR;
    }

    public byte[] getBody() {
        return data;
    }

    public byte[] getDecryptedBody(byte[] kc) throws Exception {
        checkIsDecrypted(kc);
        return Arrays.copyOfRange(decrypted, CNTR_AND_PCNTR.length + checkSum.length, CNTR_AND_PCNTR.length + checkSum.length + data.length - decrypted[CNTR_AND_PCNTR.length - 1]);
    }

    public byte[] getDecryptedCNTR(byte[] kc) throws Exception {
        checkIsDecrypted(kc);
        return Arrays.copyOfRange(decrypted, 0, CNTR_AND_PCNTR.length - 1);
    }

    public byte[] getDecryptedChecksum(byte[] kc) throws Exception {
        checkIsDecrypted(kc);
        return Arrays.copyOfRange(decrypted, CNTR_AND_PCNTR.length, CNTR_AND_PCNTR.length + checkSum.length);
    }

    private synchronized void checkIsDecrypted(byte[] kc) throws Exception {
        if (decrypted == null || !Arrays.equals(this.kc, kc)) {
            this.kc = kc;
            byte[] encryptedData = Arrays.copyOf(CNTR_AND_PCNTR, CNTR_AND_PCNTR.length + checkSum.length + data.length);
            System.arraycopy(checkSum, 0, encryptedData, CNTR_AND_PCNTR.length, checkSum.length);
            System.arraycopy(data, 0, encryptedData, CNTR_AND_PCNTR.length + checkSum.length, data.length);
            decrypted = decrypt(this.kc, encryptedData);
        }
    }

}
