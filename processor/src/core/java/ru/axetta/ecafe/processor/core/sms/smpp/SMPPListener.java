/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms.smpp;

import ru.axetta.smpp.client.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 07.11.12
 * Time: 15:26
 * To change this template use File | Settings | File Templates.
 */
public class SMPPListener implements Listener{

    private final static Logger logger = LoggerFactory.getLogger(SMPPListener.class);

    public void received(MSG message, long receiveTime, long processTime) {
        switch (message.getType()) {
            case Text:{
                Text msg = (Text)message;
                //System.err.println("message(coding="+msg.getDataCoding()+"): "+ Client.toStr(msg.getTextBytes()));
                logger.error("message(coding="+msg.getDataCoding()+"): "+ Client.toStr(msg.getTextBytes()));
            }
            break;
            case PoR:{
                PoR msg = (PoR)message;
                //System.err.println("PoR message: "+ Client.toStr(msg.getBody()));
                logger.error("PoR message: "+ Client.toStr(msg.getBody()));
            }
            break;
            case Ticket: {
                Ticket msg = (Ticket)message;
                //System.err.println("Ticket message: "+ Client.toStr(msg.getBody()));
                logger.error("Ticket message: "+ Client.toStr(msg.getBody()));
            }
            break;
            case Empty: {
                Empty msg = (Empty)message;
                //System.err.println("Empty message (error)");
                logger.error("Empty message (error)");
            }
            break;
        }
    }

    public void error() {
        //System.err.println("aaaa!!! error!!! client disconnected");
        logger.error("aaaa!!! error!!! client disconnected");
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
