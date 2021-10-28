/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import ru.CryptoPro.JCP.JCP;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RNIPSecuritySOAPHandlerV21 extends RNIPSecuritySOAPHandler implements SOAPHandler<SOAPMessageContext> {

    private static final Logger logger = LoggerFactory.getLogger(RNIPSecuritySOAPHandlerV21.class);
    public static final String SIGN_ID = "I_52d85fa5-18ae-11e5-b50b-bcaec5d977ce";
    public static final String CONFIG = "resource/jcp.xml";

    public RNIPSecuritySOAPHandlerV21(String containerAlias, String containerPassword, IRNIPMessageToLog messageLogger) {
        super(containerAlias, containerPassword, messageLogger);
    }

    @Override
    protected void initKeys() throws Exception {
        if (privateKey == null) {
            Security.insertProviderAt(new JCP(), 1);
            keyStore = KeyStore.getInstance((new JCP()).HD_STORE_NAME);
            keyStore.load(null, null);
            privateKey = (PrivateKey) keyStore.getKey(containerAlias, containerPassword.toCharArray());
            cert = (X509Certificate) keyStore.getCertificate(containerAlias);
            org.apache.xml.security.Init.init();
        }
    }

    protected void additionalTaskOnOutboundMessage(Document doc) {
        //В версии 2.1 ничего не делаем
    }

    protected void additionalTaskOnInboundMessage(Document doc) {
        //В версии 2.1 ничего не делаем
    }

    @Override
    public boolean handleMessage(SOAPMessageContext smc) {

        final Boolean outboundProperty = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);
        final SOAPPart soapPart = smc.getMessage().getSOAPPart();
        Document doc = null;
        try {
            if (outboundProperty) {
                initKeys();

                doc = soapPart.getEnvelope().getOwnerDocument();

                final org.apache.xml.security.transforms.Transforms transforms =
                        new org.apache.xml.security.transforms.Transforms(doc);
                transforms
                        .addTransform(org.apache.xml.security.transforms.Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);

                final Provider xmlDSigProvider = new ru.CryptoPro.JCPxml.dsig.internal.dom.XMLDSigRI();
                final XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM", xmlDSigProvider);

                final List<Transform> transformList = new ArrayList<Transform>();
                final Transform transformC14N = fac.newTransform(
                        org.apache.xml.security.transforms.Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS,
                        (XMLStructure) null);
                transformList.add(transformC14N);

                additionalTaskOnOutboundMessage(doc);

                Element token = (Element)doc.getElementsByTagName("ns2:CallerInformationSystemSignature").item(0);
                Element assertionNode = (Element)doc.getElementsByTagName("ns2:SenderProvidedRequestData").item(0);
                try {
                    assertionNode.setIdAttribute("Id", true);
                } catch (NullPointerException npe) {
                    assertionNode = (Element)doc.getElementsByTagName("ns2:OriginalMessageID").item(0);
                    try {
                        assertionNode.setIdAttribute("Id", true);
                    } catch (NullPointerException npe2) {
                        assertionNode = (Element)doc.getElementsByTagName("AckTargetMessage").item(0);
                        try {
                            assertionNode.setIdAttribute("Id", true);
                        }
                        catch (NullPointerException npe3)
                        {
                            assertionNode = (Element)doc.getElementsByTagNameNS("*", "AckTargetMessage").item(0);
                            assertionNode.setIdAttribute("Id", true);
                        }
                    }
                }

                // Ссылка на подписываемые данные с алгоритмом хеширования ГОСТ 34.11.
                final Reference ref =
                        fac.newReference("#" + SIGN_ID, fac.newDigestMethod(ru.CryptoPro.JCPxml.Consts.URN_GOST_DIGEST_2012_256, null), transformList, null, null);
                // Задаём алгоритм подписи:
                final SignedInfo si = fac.newSignedInfo(
                        fac.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null),
                        fac.newSignatureMethod(ru.CryptoPro.JCPxml.Consts.URN_GOST_SIGN_2012_256, null), Collections.singletonList(ref));

                //Создаём узел ds:KeyInfo с информацией о сертификате:
                final KeyInfoFactory kif = fac.getKeyInfoFactory();
                final X509Data x509d = kif.newX509Data(Collections.singletonList(cert));
                final KeyInfo ki = kif.newKeyInfo(Collections.singletonList(x509d));

                //В качестве параметров алгоритм подписи, ключ и сертификат
                final javax.xml.crypto.dsig.XMLSignature sig = fac.newXMLSignature(si, ki);
                final DOMSignContext signContext = new DOMSignContext(privateKey, token);
                //Подписываем
                sig.sign(signContext);

                String msg = RNIPSecuritySOAPHandler.toString(doc);

                smc.getMessage().getSOAPPart().setContent(new StreamSource(new ByteArrayInputStream(msg.getBytes("UTF-8"))));

                messageLogger.LogPacket(msg, IRNIPMessageToLog.MESSAGE_OUT);

            } else {
                doc = soapPart.getEnvelope().getOwnerDocument();
                additionalTaskOnInboundMessage(doc);
                String msg = toString(doc);
                messageLogger.LogPacket(msg, IRNIPMessageToLog.MESSAGE_IN);
            }
        } catch (Exception e) {
            logger.error("Error in handle Rnip message", e);
            if (doc != null) {
                try {
                    String msg = RNIPSecuritySOAPHandler.toString(doc);
                    smc.getMessage().getSOAPPart().setContent(new StreamSource(new ByteArrayInputStream(msg.getBytes("UTF-8"))));
                    messageLogger.LogPacket(msg, IRNIPMessageToLog.MESSAGE_OUT);
                } catch (Exception e1) {
                    logger.error("Error in logging packet:", e1);
                }
            }
        }

        return true;
    }

}
