/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.sberbank_kgd;

import junit.framework.TestCase;

import ru.axetta.ecafe.processor.core.utils.Base64;

import org.junit.Test;

import javax.net.ssl.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.security.KeyStore;
import java.security.Security;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 04.09.12
 * Time: 12:39
 * To change this template use File | Settings | File Templates.
 */
public class SBKGDOnlinePaymentServletTest extends TestCase {

    public SBKGDOnlinePaymentServletTest(String testName) {
        super(testName);
    }

    public static void setUpSSL() {
        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
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
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            //HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void setUp() throws Exception {
        setUpSSL();
    }

    public void testErrorPayment() {
        System.out.println("[Error (duplicate id) payment test]");
        try {
            String commitPaymentRequest = "action=payment&number=00200022&amount=25.34&receipt=3567264&date=2005-09-20T15:53:00";
            System.out.println("- Payment request");
            System.out.println("- Request: "+commitPaymentRequest);
            String rspPay = sendRequest(commitPaymentRequest);
            System.out.println("- Response: " + rspPay);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void tearDown() throws Exception {
    }

    private String sendRequest(String request) throws Exception {
        URL yahoo = new URL("https://78.46.34.200:8443/processor/payment-sbkgd?"+request);
        URLConnection yc = yahoo.openConnection();
        yc.setDoOutput(true);
        InputStreamReader inputStreamReader = new InputStreamReader(yc.getInputStream());
        BufferedReader in = new BufferedReader(inputStreamReader);
        String inputLine, result="";
        result+=inputStreamReader.getEncoding()+"\n";

        while ((inputLine = in.readLine()) != null)
            result+=inputLine;
        in.close();
        return result;
    }

}
