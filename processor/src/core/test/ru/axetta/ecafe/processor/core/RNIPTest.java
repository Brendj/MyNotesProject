/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core;

import com.sun.org.apache.xml.internal.security.Init;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xpath.internal.XPathAPI;
import junit.framework.TestCase;
import ru.CryptoPro.JCP.tools.Array;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.service.RNIPLoadPaymentsService;
import ru.axetta.ecafe.processor.core.test.synch.JUnit4ClassRunner;

import org.apache.ws.security.message.WSSecHeader;
import org.apache.ws.security.message.token.X509Security;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.ContextConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.crypto.KeySelector;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RunWith(JUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/META-INF/context.xml" })
public class RNIPTest {

    @Test
    public void testSign() throws Exception {
        String modifyCatalogFileName = "C:\\Work\\Work.dev\\SVN\\ecafe-jb7\\processor\\src\\web\\web\\WEB-INF\\classes\\META-INF\\rnip\\modifyCatalog.xml";
        String createCatalogFileName = "C:\\Work\\Work.dev\\SVN\\ecafe-jb7\\processor\\src\\web\\web\\WEB-INF\\classes\\META-INF\\rnip\\createCatalog.xml";
        String loadPaymentFileName = "C:\\Work\\Work.dev\\SVN\\ecafe-jb7\\processor\\src\\web\\web\\WEB-INF\\classes\\META-INF\\rnip\\getPayments_byDate.xml";
        RNIPLoadPaymentsService s = new RNIPLoadPaymentsService();
        SOAPMessage msg = s.signRequest(new StreamSource(new FileInputStream(modifyCatalogFileName)), RNIPLoadPaymentsService.REQUEST_MODIFY_CATALOG);
        Array.writeFile("C:/test_modifyCatalog.signed.xml", RNIPLoadPaymentsService.messageToString(msg).getBytes("UTF-8"));

        msg=s.signRequest(new StreamSource(new FileInputStream(createCatalogFileName)), RNIPLoadPaymentsService.REQUEST_CREATE_CATALOG);
        Array.writeFile("C:/test_createCatalog.signed.xml", RNIPLoadPaymentsService.messageToString(msg).getBytes("UTF-8"));

        msg= s.signRequest(new StreamSource(new FileInputStream(loadPaymentFileName)), RNIPLoadPaymentsService.REQUEST_LOAD_PAYMENTS);
        Array.writeFile("C:/test_loadPayment.signed.xml", RNIPLoadPaymentsService.messageToString(msg).getBytes("UTF-8"));
    }
}
