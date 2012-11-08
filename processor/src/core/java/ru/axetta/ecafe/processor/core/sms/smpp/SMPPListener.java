/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.smpp;

import ru.axetta.smpp.client.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smpp.Data;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 07.11.12
 * Time: 15:26
 * To change this template use File | Settings | File Templates.
 */
public class SMPPListener implements Listener{

    private final static Logger logger = LoggerFactory.getLogger(SMPPListener.class);
    private HashMap<String, Boolean> messageStack = new LinkedHashMap<String, Boolean>();

    public HashMap<String, Boolean> getMessageStack() {
        return messageStack;
    }

    public void received(MSG message, long receiveTime, long processTime) {
        switch (message.getType()) {
            case Text:{
                Text msg = (Text)message;
                try {
                    switch (msg.getDataCoding()) {
                        case 0: logger.info(
                                "message(coding=" + msg.getDataCoding() + "): " + new String(msg.getTextBytes(),
                                        Data.ENC_GSM7BIT));break;
                        case 8: logger.info(
                                "message(coding=" + msg.getDataCoding() + "): " + new String(msg.getTextBytes(),
                                        Data.ENC_UTF16_BE)); break;
                        default: logger.info(
                                "message(coding=" + msg.getDataCoding() + "): " + Client.toStr(msg.getTextBytes()));
                    }
                    messageStack.put(msg.getMsisdn(),msg.isDeliveryReport());
                } catch (Exception ex) {
                    logger.error("Error:", ex);
                }
            }
            break;
            case PoR:{
                PoR msg = (PoR)message;
                logger.error("PoR message: "+ Client.toStr(msg.getBody()));
            }
            break;
            case Ticket: {
                Ticket msg = (Ticket)message;
                logger.error("Ticket message: "+ Client.toStr(msg.getBody()));
            }
            break;
            case Empty: {
                Empty msg = (Empty)message;
                logger.error("Empty message (error)");
            }
            break;
        }
    }

    public void error() {
        //System.err.println("aaaa!!! error!!! client disconnected");
        logger.error("client disconnected");
    }

    //private static IvParameterSpec iv = new IvParameterSpec(new byte[]{0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0});
    //
    //private static byte[] decrypt(byte[] data, String kc) throws Exception {
    //    byte[] kb = Main.hexToByte(kc + kc.substring(0, 16));
    //    SecretKeySpec key = new SecretKeySpec(kb,"DESede");
    //    Cipher dec = Cipher.getInstance("DESede/CBC/NoPadding");
    //    dec.init(Cipher.DECRYPT_MODE, key, iv);
    //    return dec.doFinal(data);
    //}

}
