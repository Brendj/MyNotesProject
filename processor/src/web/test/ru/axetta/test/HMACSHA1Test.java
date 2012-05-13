/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.test;

import junit.framework.TestCase;

import ru.axetta.ecafe.util.DigitalSignatureUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class HMACSHA1Test extends TestCase {
    public static void testHmac() throws Exception {
        String amount = "123.0";
        String currency = "RUR";
        String redirectUrl = "https://3ds2.mmbank.ru:8443/cgi-bin/cgi_link";
        String terminalId = "30000078";
        String paymentId = "906";
        String merchName = "Информационный город";
        String merchURL = "dit.mos.ru";
        long lContractId=200485;
        String paymentDesc = "Пополнение л/с "+"Иванова А.П."+" ("+lContractId+")";
        String order = ""+System.currentTimeMillis();
        String trType = "6";
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyyMMddHHmmss");
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        String timestamp = dateFormatGmt.format(new Date());

        String nonce = "7A9593E88E7C958D";
        String backRef = "https://localhost:8443/processor/client-room/mos/bm/bankcard.jsp?action=complete";

        String data= amount.length()+amount+currency.length()+currency+order.length()+order+paymentDesc.getBytes().length+paymentDesc+merchName.getBytes().length+merchName+merchURL.length()+merchURL+"-"+terminalId.length()+terminalId+"-"+trType.length()+trType+"--"+timestamp.length()+timestamp+nonce.length()+nonce+backRef.length()+backRef+paymentId.length()+paymentId+(""+lContractId).length()+lContractId+"-";

        ////
        String key="00112233445566778899AABBCCDDEEFF";
//        String data="511.483USD677144616IT Books. Qty: 217Books Online Inc.14www.sample.com1512345678901234589999999919pgw@mail.sample.com11--142003010515302116F2B2DD7E603A7ADA33https://www.sample.com/shop/reply";
        String hmac = DigitalSignatureUtils.generateHmac("HmacSHA1", key, data.getBytes("UTF-8"));
        System.out.println(hmac);
        ////
        //SimpleDateFormat dateFormatGmt = new SimpleDateFormat("yyyyMMddHHmmss");
        //dateFormatGmt.setTimeZone(TimeZone.getTimeZone("UTC"));
        //System.out.println(dateFormatGmt.format(new Date()));
    }
}
