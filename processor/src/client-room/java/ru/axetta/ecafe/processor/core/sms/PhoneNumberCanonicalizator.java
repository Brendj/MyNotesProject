/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms;

import org.apache.commons.lang.StringUtils;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 13.10.2009
 * Time: 15:42:35
 * To change this template use File | Settings | File Templates.
 */
public class PhoneNumberCanonicalizator {

    public static String canonicalize(String phoneNumber) throws Exception {
        String result = StringUtils.trim(phoneNumber);
        result = StringUtils.removeStart(result, "+");
        result = StringUtils.remove(result, ' ');
        result = StringUtils.remove(result, '-');
        result = StringUtils.remove(result, '(');
        result = StringUtils.remove(result, ')');
        int length = StringUtils.length(result);
        if (11 == length) {
            if (StringUtils.startsWith(result, "8")) {
                result = "7" + StringUtils.substring(result, 1);
            }
        } else {
            if (10 == length) {
                result = "7" + result;
            }
        }
        return result;
    }
}