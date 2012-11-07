/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.smpp.client;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.util.Arrays;

/**
 * Received message class
 */
public abstract class MSG {
    protected static byte[] decrypt(byte[] kic, byte[] data) throws Exception {
        byte[] keyData = Arrays.copyOf(kic, kic.length + kic.length / 2);
        System.arraycopy(kic, 0, keyData, kic.length, kic.length / 2);
        java.security.Key key = new javax.crypto.spec.SecretKeySpec(keyData, "DESede");
        Cipher c = Cipher.getInstance("DESede/CBC/NoPadding");
        c.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(new byte[8]));
        return c.doFinal(data);
    }

    /**
     * Message type enumerator
     */
    public enum MessageType {Text, PoR, Ticket, Empty}

    private final MessageType type;
    private final int ussd_sessid;
    private final String ussd_menuPath;
    private String messageId;
    private String msisdn;
    protected boolean isDeliveryReport;
    protected byte[] data;

    public MSG(MessageType type, String messageId, String msisdn, int ussd_sessid, String ussd_menuPath) {
        this.type = type;
        this.messageId = messageId;
        this.msisdn = msisdn;
        this.isDeliveryReport = false;
        this.ussd_sessid = ussd_sessid;
        this.ussd_menuPath = ussd_menuPath;
    }

    /**
     * Message type getter.
     * @return message type (using enum MSG.MessageType)
     */
    public MessageType getType() {
        return type;
    }

    public boolean isDeliveryReport() {
        return isDeliveryReport;
    }

    public String getMessageId() {
        return messageId;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public int getUssd_sessid(){
        return ussd_sessid;
    }

    public String getUssd_menuPath() {
        return ussd_menuPath;
    }
}
