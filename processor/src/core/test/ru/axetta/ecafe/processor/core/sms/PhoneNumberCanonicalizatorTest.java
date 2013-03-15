/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms;

import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 15.03.13
 * Time: 17:01
 * To change this template use File | Settings | File Templates.
 */
public class PhoneNumberCanonicalizatorTest {

    @Test
    public void testCanonicalize() throws Exception {
        System.out.println(PhoneNumberCanonicalizator.canonicalize("80000000000"));
        System.out.println(PhoneNumberCanonicalizator.canonicalize("8(000)0000001"));
        System.out.println(PhoneNumberCanonicalizator.canonicalize("7(000)0000002"));
        System.out.println(PhoneNumberCanonicalizator.canonicalize("+7(000)0000003"));
        System.out.println(PhoneNumberCanonicalizator.canonicalize("+70000000004"));
    }
}
