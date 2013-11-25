/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.smpp.client;

interface SMPPClient extends Error {

    public static final int STATE_OFFLINE = 0;
    public static final int STATE_CONNECT = 1;
    public static final int STATE_ONLINE  = 2;

    public SendResult sendOTA(String message, long msisdn, String key1, String key2, byte[] tar, byte[] spi, byte[] key,
            byte[] tpscts);

    public SendResult send(String message, long msisdn);

    public int getStatus();

    public int start(String sourceAddress, String smscIPAddress, int smscPort, String systemId, String systemType, String serviceType, String password,
            String sourceAddressTon, String sourceAddressNpi);

    public int stop();

}
