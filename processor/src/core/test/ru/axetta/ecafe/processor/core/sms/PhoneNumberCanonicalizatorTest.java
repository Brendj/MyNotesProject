/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

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

    @Test
    public void testSMSText(){
        String text = "Повторный выход из школы 10:08 (08107716 Ростиашвили Андрей). Баланс: 38,45";
        List<String> texts = new ArrayList<String>();
        if (text.length() > 68) {
            //text = text.substring(0, 67) + "..";
            for (int i=0;;i+=69){
                int j = i+69;
                if(j<text.length()){
                    texts.add(text.substring(i, j));
                } else {
                    texts.add(text.substring(i));
                    break;
                }
            }
        } else {
            texts.add(text);
        }
        for (String str: texts){
            System.out.println(str);
        }
    }
}
