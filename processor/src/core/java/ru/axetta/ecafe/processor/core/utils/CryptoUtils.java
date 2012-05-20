/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by IntelliJ IDEA.
 * User: Artyom
 * Date: 05.05.12
 * Time: 18:53
 * To change this template use File | Settings | File Templates.
 */
public class CryptoUtils {

    public static String MD5(String arg) throws NoSuchAlgorithmException {
        MessageDigest m = MessageDigest.getInstance("MD5");
        byte[] data = arg.getBytes();
        m.update(data, 0, data.length);
        BigInteger i = new BigInteger(1, m.digest());
        return String.format("%1$032X", i);
    }
}
