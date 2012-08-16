/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.soap;


import com.sun.xml.internal.ws.developer.JAXWSProperties;
import junit.framework.TestCase;
import junit.framework.TestResult;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.*;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;

import javax.xml.ws.BindingProvider;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class IntegraTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        //Way4PaymentTest.setUpClientSSL();
        PaymentWSTest.setUpSSL();
    }

    public void testMD5password() throws Exception{
        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
        ClientRoomControllerWSService service = new ClientRoomControllerWSService();
        ClientRoomController port
                = service.getClientRoomControllerWSPort();
        ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "https://kgd.novayashkola.ru:18443/processor/soap/client");
        ((BindingProvider)port).getRequestContext().put(JAXWSProperties.HOSTNAME_VERIFIER, new PaymentWSTest.TestHostnameVerifier());
        Map context = ((BindingProvider) port).getRequestContext();
        context.put(BindingProvider.USERNAME_PROPERTY, "dnevnik.ru");
        context.put(BindingProvider.PASSWORD_PROPERTY, "D4inxdo3inid");
        String fullNameUpCase = "Тестеров Тест Тестович".replaceAll("\\s", "").toUpperCase();
        fullNameUpCase= fullNameUpCase+"Nb37wwZWufB";
        byte[] bytesOfMessage = fullNameUpCase.getBytes("UTF-8");
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] hash = md.digest(bytesOfMessage);
        BigInteger bigInt = new BigInteger(1, hash);
        String md5HashString = bigInt.toString(16);
        long clientId=18L;//200485L;//7613912L
        Result ra = port.authorizeClient(clientId, md5HashString);
        System.out.println("dnevnik.ru test");
        System.out.println("https://kgd.novayashkola.ru:18443/processor/soap/client");
        System.out.println("AUTH CLIENTID: " + clientId);
        System.out.println("AUTH CLIENT: " + ra.getResultCode()+":"+ra.getDescription());
        ClientSummaryResult r = port.getSummary(clientId);
        ClientSummaryExt client = r.getClientSummary();
        System.out.println("CLIENT FirstName: " + client.getFirstName());
        System.out.println("CLIENT LastName: " + client.getLastName());
        System.out.println("CLIENT MiddleName: " + client.getMiddleName());
    }

    public void testSHA1password() throws Exception {
        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
        ClientRoomControllerWSService service = new ClientRoomControllerWSService();
        ClientRoomController port
                = service.getClientRoomControllerWSPort();
        ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "https://localhost:8443/processor/soap/client");
        ((BindingProvider)port).getRequestContext().put(JAXWSProperties.HOSTNAME_VERIFIER, new PaymentWSTest.TestHostnameVerifier());
        Map context = ((BindingProvider) port).getRequestContext();
        context.put(BindingProvider.USERNAME_PROPERTY, "tbaltica");
        context.put(BindingProvider.PASSWORD_PROPERTY, "Dthntfe333");
        long clientId=91L;
        String plainPassword= "91";
        final byte[] plainPasswordBytes = plainPassword.getBytes(CharEncoding.UTF_8);
        final MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
        String sha1HashString = new String(Base64.encodeBase64(messageDigest.digest(plainPasswordBytes)), CharEncoding.US_ASCII);

        Result ra = port.authorizeClient(clientId, sha1HashString);
        System.out.println("tbaltica test");
        System.out.println("https://localhost:8443/processor/soap/client");
        System.out.println("AUTH CLIENT: " + ra.getResultCode()+":"+ra.getDescription());
        ClientSummaryResult r = port.getSummary(clientId);
        ClientSummaryExt client = r.getClientSummary();
        System.out.println("CLIENT FirstName: " + client.getFirstName());
        System.out.println("CLIENT LastName: " + client.getLastName());
        System.out.println("CLIENT MiddleName: " + client.getMiddleName());
        System.out.println("CLIENT PlainPassword: " + plainPassword);
        System.out.println("CLIENT CryptPassword: " + sha1HashString);
    }

    //private void testGetSummaryOld() throws Exception {
    //    System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
    //    ClientRoomControllerWSService service = new ClientRoomControllerWSService();
    //    ClientRoomController port
    //            = service.getClientRoomControllerWSPort();
    //    ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "https://kgd.novayashkola.ru:18443/processor/soap/client");
    //    //((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "https://127.0.0.1:8443/processor/soap/client");
    //    ((BindingProvider)port).getRequestContext().put(JAXWSProperties.HOSTNAME_VERIFIER, new PaymentWSTest.TestHostnameVerifier());
    //    Map context = ((BindingProvider) port).getRequestContext();
    //    context.put(BindingProvider.USERNAME_PROPERTY, "dnevnik.ru");
    //    context.put(BindingProvider.PASSWORD_PROPERTY, "D4inxdo3inid");
    //
    //    //context.put(BindingProvider.USERNAME_PROPERTY, "tbaltica");
    //    //context.put(BindingProvider.PASSWORD_PROPERTY, "Dthntfe333");
    //
    //    //String password = Client.encryptPassword("7613912");
    //    long clientId=18L;//200485L;//7613912L
    //    //Result ra = port.authorizeClient(, "Абазов Амир Аниуарович");
    //
    //    //String fullNameUpCase = "Петров Петр Иванович".replaceAll("\\s", "").toUpperCase();
    //    //fullNameUpCase= fullNameUpCase+"Nb37wwZWufB";
    //    String fullNameUpCase = "Тестеров Тест Тестович".replaceAll("\\s", "").toUpperCase();
    //    fullNameUpCase= fullNameUpCase+"Nb37wwZWufB";
    //    //final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
    //
    //    /*ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(fullNameUpCase.getBytes(CharEncoding.UTF_8));
    //    DigestInputStream digestInputStream = new DigestInputStream(arrayInputStream, messageDigest);
    //    ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
    //    IOUtils.copy(digestInputStream, arrayOutputStream);
    //    String md5HashString = new String(Base64.encodeBase64(arrayOutputStream.toByteArray()), CharEncoding.UTF_8);*/
    //
    //    //final MessageDigest messageDigest = MessageDigest.getInstance("MD5");
    //    //messageDigest.update();
    //
    //    //ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(messageDigest.digest());
    //    //DigestInputStream digestInputStream = new DigestInputStream(arrayInputStream, messageDigest);
    //    //ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
    //    //IOUtils.copy(digestInputStream, arrayOutputStream);
    //    //String md5HashString = new String(Base64.encodeBase64(arrayOutputStream.toByteArray()), CharEncoding.UTF_8);
    //
    //    byte[] bytesOfMessage = fullNameUpCase.getBytes("UTF-8");
    //    MessageDigest md = MessageDigest.getInstance("MD5");
    //    byte[] hash = md.digest(bytesOfMessage);
    //    BigInteger bigInt = new BigInteger(1, hash);
    //    String md5HashString = bigInt.toString(16);
    //
    //
    //    /*final byte[] plainPasswordBytes = "91".getBytes(CharEncoding.UTF_8);
    //    final MessageDigest messageDigest = MessageDigest.getInstance("SHA1");
    //    md5HashString = new String(Base64.encodeBase64(messageDigest.digest(plainPasswordBytes)), CharEncoding.US_ASCII);
    //    clientId = 91L;*/
    //
    //    Result ra = port.authorizeClient(clientId, md5HashString);
    //    System.out.println("AUTH CLIENT: " + ra.getResultCode()+":"+ra.getDescription());
    //    ClientSummaryResult r = port.getSummary(clientId);
    //    System.out.println("CLIENT: " + r.getClientSummary().getFirstName());
    //}
}
