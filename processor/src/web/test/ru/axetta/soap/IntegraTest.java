/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.soap;


import com.sun.xml.internal.ws.developer.JAXWSProperties;
import junit.framework.TestCase;
import junit.framework.TestResult;

import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.web.partner.integra.dataflowtest.*;

import javax.xml.ws.BindingProvider;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class IntegraTest extends TestCase {

    @Override
    protected void setUp() throws Exception {
        //Way4PaymentTest.setUpClientSSL();
        PaymentWSTest.setUpSSL();
    }

    public void testGetSummary() throws Exception {
        System.setProperty("com.sun.xml.ws.transport.http.client.HttpTransportPipe.dump", "true");
        ClientRoomControllerWSService service = new ClientRoomControllerWSService();
        ClientRoomController port
                = service.getClientRoomControllerWSPort();
        ((BindingProvider)port).getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, "https://78.46.34.200:8443/processor/soap/client");
        ((BindingProvider)port).getRequestContext().put(JAXWSProperties.HOSTNAME_VERIFIER, new PaymentWSTest.TestHostnameVerifier());
        Map context = ((BindingProvider) port).getRequestContext();
        context.put(BindingProvider.USERNAME_PROPERTY, "testuser");
        context.put(BindingProvider.PASSWORD_PROPERTY, "testpass");

        //String password = Client.encryptPassword("7613912");
        long clientId=200485L;//7613912L
        //Result ra = port.authorizeClient(, "Абазов Амир Аниуарович");
        Result ra = port.authorizeClient(clientId, "Петров Петр Иванович");
        System.out.println("AUTH CLIENT: " + ra.getResultCode()+":"+ra.getDescription());
        ClientSummaryResult r = port.getSummary(clientId);
        System.out.println("CLIENT: " + r.getClientSummary().getFirstName());
    }
}
