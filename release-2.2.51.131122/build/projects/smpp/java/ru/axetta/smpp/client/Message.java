/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.smpp.client;

import ru.oksoft.myOTA.Security0348;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Класс сообщения.
 */
class Message {

    public String[] message;
    public byte[][] bmessage;
    public final long id;
    public final byte sar_id;
    public final byte type;

    public byte ussd = 0;
    public int ussd_sessid = 0;
    public String ussd_menuPath = "";

    public static final byte GSM7BIT = 0;
    public static final byte UCS2 = 1;
    public static final byte BINARY = 2;
    public static final byte OTA = 3;

    public boolean isLong;
    public int num, len, throtCount, systemErrorCount, slaErrCount, mqfErrCount, otherErrCount;

    public Message(String message, long id, boolean ucs2) {
        this.message = new String[]{message};
        this.bmessage = null;
        this.id = id;
        this.sar_id = (byte)((this.id & 0xFF) == 0? 50 : (this.id & 0xFF));
        this.type = ucs2 ? UCS2 : GSM7BIT;
        checkLen();
        num = 0;
        len = this.message.length;
    }

    public Message(byte[] message, long id) {
        this.message = null;
        this.bmessage = new byte[][]{message};
        this.id = id;
        this.sar_id = (byte)((this.id & 0xFF) == 0? 50 : (this.id & 0xFF));
        this.type = BINARY;
        checkLen();
        num = 0;
        len = bmessage.length;
    }

    public Message(byte[] message, long id, String kc, String kd, byte[] tar, byte[] spi, byte[] key, byte[] tpscts) {
        ArrayList<byte[]> segments = cipher(message, kc, kd, tar == null ? TAR : tar, spi == null ? SPI : spi, key == null ? KEY : key, tpscts == null ? TPSCTS : tpscts);
        this.message = null;
        this.bmessage = new byte[segments.size()][];
        System.err.print("ota hex: ");
        for (int i = 0, max = segments.size(); i < max; ++i) {
            this.bmessage[i] = segments.get(i);
            System.err.print(toStr(this.bmessage[i]));
        }
        System.err.println();
        this.id = id;
        this.sar_id = (byte)((this.id & 0xFF) == 0? 50 : (this.id & 0xFF));
        this.type = OTA;
        this.num = 0;
        this.len = bmessage.length;
        this.isLong = len > 1;
    }

    public String getMessage() {
        return num > message.length? null : message[num++];
    }

    public byte[] getBinaryMessage() {
        return num > bmessage.length ? null : bmessage[num++];
    }

    private void checkLen() {
        switch (type) {
            case GSM7BIT:
                if (getLenForGSM7(message[0]) > 160) {
                    isLong = true;
                    String[] s = new String[(int)Math.ceil((double)getLenForGSM7(message[0])/153)];
                    int i = 0, max;
                    while (message[0].length() > 0) {
                        max = Math.min(153, message[0].length());
                        while (getLenForGSM7(message[0].substring(0, max)) > 153) {
                            max--;
                        }
                        s[i++] = message[0].substring(0, max);
                        message[0] = message[0].substring(max);
                    }
                    message = s;
                }
                break;
            case UCS2:
                if (message[0].length() > 70) {
                    isLong = true;
                    int max = Session.Config.USE63MAX ? 63 : 67;
                    String[] s = new String[(int)Math.ceil((double)message[0].length()/max)];
                    int i = 0;
                    while (message[0].length() > 0) {
                        s[i++] = message[0].substring(0, Math.min(max, message[0].length()));
                        message[0] = message[0].substring(Math.min(max, message[0].length()));
                    }
                    message = s;
                }
                break;
            case BINARY:
                if (bmessage[0].length > 134) {//140) {
                    isLong = true;
                    byte[][] arr = new byte[(int)Math.ceil((double)bmessage[0].length/134)][];
                    for (int i = 0, j = 0; i < bmessage[0].length;) {
                        arr[j] = new byte[Math.min(arr[0].length, bmessage[0].length-i)];
                        System.arraycopy(bmessage[0], i, arr[j], 0, arr[j].length);
                        i += arr[j++].length;
                    }
                    bmessage = arr;
                }
        }
    }

    private int getLenForGSM7(String s) {
        String longGsmSymbols = "^{}\\[\u007e\u005d\u007c";
        int len = s.length();
        for (int i = 0, max = len; i < max; ++i) {
            if (longGsmSymbols.indexOf(s.charAt(i)) != -1) {
                len++;
            }
        }
        return len;
    }


    private static final int CLEN = 0x6C;
    private static final byte[] TAR = {0x52, 0x4A, 0x45};
    private static final byte[] SPI = {0x06, 0x21};
    private static final byte[] KEY = {0x15, 0x15};
    private static final byte[] TPSCTS = {1, 1, 1, 1, 1, 1, 0};

    private static ArrayList<byte[]> cipher(byte[] data, String kc, String kd, byte[] tar, byte[] spi, byte[] key, byte[] tpscts) {
        Security0348 SEC = new Security0348();
        int dataLen = data.length;
        int offset = 0;
        boolean CONCAT;
        if ((CONCAT = dataLen > CLEN)) {
            SEC.initProperties("nil", 0, tar, spi, key, tpscts, CONCAT);
            dataLen = SEC.setConcat(data, 0);
        } else {
            SEC.initProperties("nil", 0, tar, spi, key, tpscts, CONCAT);
        }
        SEC.setKey("KIC", "1/1/DES-CBC/" + kc);
        SEC.setKey("KID", "1/2/DES-CBC/" + kd);

        ArrayList<byte[]> res = new ArrayList<byte[]>();
        ByteArrayOutputStream baos;
        int l;
        while (offset < dataLen) {
            l = CLEN;
            if (offset + l > dataLen) {
                l = dataLen - offset;
            }
            if (CONCAT) {
                baos = SEC.getConcatTPDU(data, offset, l);
            } else {
                baos = SEC.getTPDU(data, offset, l);
            }
            offset += l;
            try {
                baos.close();
            } catch (IOException ignored) {}
            res.add(baos.toByteArray());
        }
        return res;
    }

    private String toStr(byte[] b) {
        String dump = "";
        try {
            int dataLen = b.length;
            for (int i = 0; i < dataLen; i++) {
                dump += Character.forDigit((b[i] >> 4) & 0x0f, 16);
                dump += Character.forDigit(b[i] & 0x0f, 16);
            }
        } catch (Throwable t) {
            dump = "Throwable caught when dumping = " + t;
        }
        return dump;
    }
}