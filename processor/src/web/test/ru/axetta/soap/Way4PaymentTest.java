/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.soap;

import junit.framework.TestCase;

import ru.axetta.ecafe.processor.core.utils.Base64;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;

public class Way4PaymentTest extends TestCase {

    public Way4PaymentTest(String testName) {
        super(testName);
    }

    @Override
    protected void setUp() throws Exception {
        KeyStore ks = KeyStore.getInstance("pkcs12");
        ks.load(new FileInputStream("C:\\Temp\\certs\\ispp_test_org_demo.pfx"), "1".toCharArray());

        KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
        kmf.init(ks, "1".toCharArray());

        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
            new X509TrustManager() {
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
                public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {}
                public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs, String authType) {}
            }
        };
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(kmf.getKeyManagers(), trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void tearDown() throws Exception {
    }

    public void testErrorPayment() {
        System.out.println("[Error (duplicate id) payment test]");
        try {
            String STAN="12345";
            String clientId = "200485";
            String opId = "1", termId="ATM00005";
            String sum="10.00", date="20120401", time="100000";
            String balanceRequest = "function=bank_account&STAN="+STAN+"&RRN="+opId+"&PHONE="+clientId+"&AMOUNT="+sum+"&CURRENCY=RUR"+"&TERMINAL="+termId;
            System.out.println("- Check request: "+balanceRequest);
            String rspBal = sendRequest(balanceRequest);
            System.out.println("- Response: "+rspBal);
            String commitPaymentRequest = "function=bank_payment&STAN="+STAN+"&RRN="+opId+"&ACCOUNT="+clientId+"&PHONE="+clientId+"&AMOUNT="+sum+"&CURRENCY=RUR&DATE="+date+"&TIME="+time+"&TERMINAL="+termId;
            System.out.println("- Payment request");
            System.out.println("- Request: "+commitPaymentRequest);
            String rspPay = sendRequest(commitPaymentRequest);
            System.out.println("- Response: " + rspPay);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void testErrorInvalidClientPayment() {
        System.out.println("[Error (invalid client) payment test]");
        try {
            String STAN="12345";
            String clientId = "111";
            String opId = ""+System.currentTimeMillis(), termId="ATM00005";
            String sum="10.00", date="20120401", time="100000";
            String balanceRequest = "function=bank_account&STAN="+STAN+"&RRN="+opId+"&PHONE="+clientId+"&AMOUNT="+sum+"&CURRENCY=RUR"+"&TERMINAL="+termId;
            System.out.println("- Check request: "+balanceRequest);
            String rspBal = sendRequest(balanceRequest);
            System.out.println("- Response: "+rspBal);
            String commitPaymentRequest = "function=bank_payment&STAN="+STAN+"&RRN="+opId+"&ACCOUNT="+clientId+"&PHONE="+clientId+"&AMOUNT="+sum+"&CURRENCY=RUR&DATE="+date+"&TIME="+time+"&TERMINAL="+termId;
            System.out.println("- Payment request");
            System.out.println("- Request: "+commitPaymentRequest);
            String rspPay = sendRequest(commitPaymentRequest);
            System.out.println("- Response: " + rspPay);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void testEcafeSoap() {
        System.out.println("[Good payment test]");
        try {
            String STAN="12345";
            String clientId = "200485";
            String opId = ""+System.currentTimeMillis(), termId="ATM00005";
            String sum="10.00", date="20120401", time="100000";
            String balanceRequest = "function=bank_account&STAN="+STAN+"&RRN="+opId+"&PHONE="+clientId+"&AMOUNT="+sum+"&CURRENCY=RUR"+"&TERMINAL="+termId;
            System.out.println("- Check request: "+balanceRequest);
            String rspBal = sendRequest(balanceRequest);
            int pos = rspBal.indexOf("PARAM3");
            int pos2 = rspBal.indexOf(";", pos);
            String param3=rspBal.substring(pos+7, pos2);
            String fio = new String(Base64.decode(param3), "UTF-8");
            System.out.println("PARAM3 decode from base64 to UTF-8: "+fio);
            pos = rspBal.indexOf("PARAM4");
            pos2 = rspBal.indexOf("<", pos);
            String param4=rspBal.substring(pos+7, pos2);
            String balance = new String(Base64.decode(param4), "UTF-8");
            System.out.println("PARAM4 decode from base64 to UTF-8: "+balance);
            System.out.println("- Response: "+rspBal);
            String commitPaymentRequest = "function=bank_payment&STAN="+STAN+"&RRN="+opId+"&ACCOUNT="+clientId+"&PHONE="+clientId+"&AMOUNT="+sum+"&CURRENCY=RUR&DATE="+date+"&TIME="+time+"&TERMINAL="+termId;
            System.out.println("- Payment request");
            System.out.println("- Request: "+commitPaymentRequest);
            String rspPay = sendRequest(commitPaymentRequest);
            System.out.println("- Response: " + rspPay);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String sendRequest(String request) throws Exception {
        URL yahoo = new URL("https://localhost:8443/processor/payment-way4");
        URLConnection yc = yahoo.openConnection();
        yc.setDoOutput(true);
        OutputStream out = yc.getOutputStream();
        out.write(request.getBytes());
        out.close();
        BufferedReader in = new BufferedReader(
                                new InputStreamReader(
                                yc.getInputStream()));
        String inputLine, result="";

        while ((inputLine = in.readLine()) != null)
            result+=inputLine;
        in.close();
        return result;
    }

}

