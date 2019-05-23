package ru.axetta.ecafe.processor.core.service;

import com.sun.org.apache.xpath.internal.XPathAPI;
import ru.CryptoPro.JCP.JCP;

import org.apache.ws.security.message.WSSecHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.*;

public class RNIPSecuritySOAPHandler implements SOAPHandler<SOAPMessageContext> {

    private static final Logger logger = LoggerFactory.getLogger(RNIPSecuritySOAPHandler.class);

    protected static final String WS_SECURITY_SECEXT_URI =
            "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    protected static final String WS_SECURITY_UTILITY_URI =
            "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
    protected static final String ENCODING_TYPE_ATTRIBUTE =
            "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary";
    protected static final String VALUE_TYPE_ATTRIBUTE =
            "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3";

    protected static final String MESSAGE_NS = "http://smev.gosuslugi.ru/rev120315";
    protected static final String NS_SMEV = "http://roskazna.ru/gisgmp/02000000/SmevGISGMPService/";
    protected static final String NS_INC = "http://www.w3.org/2004/08/xop/include";

    protected IRNIPMessageToLog messageLogger;
    protected String containerAlias;
    protected String containerPassword;
    protected static PrivateKey privateKey;
    protected static KeyStore keyStore;
    protected static X509Certificate cert;


    public  RNIPSecuritySOAPHandler(String containerAlias, String containerPassword, IRNIPMessageToLog messageLogger) {
        this.containerAlias = containerAlias;
        this.containerPassword = containerPassword;
        this.messageLogger = messageLogger;
    }

    @Override
    public Set<QName> getHeaders() {
        return Collections.emptySet();
    }

    @Override
    public boolean handleMessage(SOAPMessageContext smc) {

        final Boolean outboundProperty = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        try {
            if (outboundProperty) {
                if (privateKey == null) {
                    Security.insertProviderAt(new JCP(), 1);
                    keyStore = KeyStore.getInstance((new JCP()).HD_STORE_NAME);
                    //keyStore = KeyStore.getInstance(store);
                    keyStore.load(null, null);
                    privateKey = (PrivateKey) keyStore.getKey(containerAlias, containerPassword.toCharArray());
                    cert = (X509Certificate) keyStore.getCertificate(containerAlias);
                    org.apache.xml.security.Init.init();
                }
                //Получаем SOAP документ
                final SOAPPart soapPart = smc.getMessage().getSOAPPart();

                //Получаем xml значения из SOAP документа
                final Document source_doc = soapPart.getEnvelope().getOwnerDocument();
                //добавляем неймспейсы к Message и MessageData
                String sss = toString(source_doc)
                        .replaceAll("<Message>", String.format("<pmes:Message xmlns:pmes=\"%s\">", MESSAGE_NS))
                        .replaceAll("</Message>", "</pmes:Message>")
                        .replaceAll("<MessageData>",
                                String.format("<pmesd:MessageData xmlns:pmesd=\"%s\">", MESSAGE_NS))
                        .replaceAll("</MessageData>", "</pmesd:MessageData>")
                        .replaceAll(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns4:GISGMPTransferMsgRequest\"", "");

                //Сохраняем измененный SOAP документ
                InputStream in = new ByteArrayInputStream(sss.getBytes("UTF-8"));
                soapPart.setContent(new StreamSource(in));
                soapPart.getEnvelope().addNamespaceDeclaration("wsse", WS_SECURITY_SECEXT_URI);
                soapPart.getEnvelope().addNamespaceDeclaration("wsu", WS_SECURITY_UTILITY_URI);
                soapPart.getEnvelope().addNamespaceDeclaration("ds", "http://www.w3.org/2000/09/xmldsig#");
                soapPart.getEnvelope().addNamespaceDeclaration("smev", NS_SMEV);
                soapPart.getEnvelope().addNamespaceDeclaration("rev", MESSAGE_NS);
                soapPart.getEnvelope().addNamespaceDeclaration("inc", NS_INC);
                smc.getMessage().getSOAPBody().setAttributeNS(WS_SECURITY_UTILITY_URI, "wsu:Id", "body");

                final WSSecHeader header = new WSSecHeader();
                header.setActor("http://smev.gosuslugi.ru/actors/smev");
                header.setMustUnderstand(false);

                //Получаем текущий вариант SOAP запроса
                final Document doc = soapPart.getEnvelope().getOwnerDocument();

                //Добавляем заголовог для SOAP запроса
                final Element sec = header.insertSecurityHeader(soapPart);
                final Element secToken = (Element) sec
                        .appendChild(doc.createElementNS(WS_SECURITY_SECEXT_URI, "wsse:BinarySecurityToken"));
                secToken.setAttribute("EncodingType", ENCODING_TYPE_ATTRIBUTE);
                secToken.setAttribute("ValueType", VALUE_TYPE_ATTRIBUTE);
                secToken.setAttribute("wsu:Id", "CertId");
                header.getSecurityHeader().appendChild(secToken);

                Element token = header.getSecurityHeader();

                org.apache.xml.security.Init.init();

                //Преобразование документа к каноническому виду
                final org.apache.xml.security.transforms.Transforms transforms =
                        new org.apache.xml.security.transforms.Transforms(doc);
                transforms
                        .addTransform(org.apache.xml.security.transforms.Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);

                // Загрузка провайдера.
                final Provider xmlDSigProvider = new ru.CryptoPro.JCPxml.dsig.internal.dom.XMLDSigRI();

                final XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM", xmlDSigProvider);

                //Преобразования над узлом ds:SignedInfo:
                final List<Transform> transformList = new ArrayList<Transform>();
                final Transform transformC14N = fac.newTransform(
                        org.apache.xml.security.transforms.Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS,
                        (XMLStructure) null);
                transformList.add(transformC14N);

                // Ссылка на подписываемые данные с алгоритмом хеширования ГОСТ 34.11.
                final Reference ref =
                        fac.newReference("#body", fac.newDigestMethod(ru.CryptoPro.JCPxml.Consts.URN_GOST_DIGEST_2012_256, null), transformList, null, null);
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
                //Подписываем ;)
                sig.sign(signContext);

                // Узел подписи Signature.
                final Element sigE = (Element) XPathAPI.selectSingleNode(signContext.getParent(), "//ds:Signature");
                // Блок данных KeyInfo.
                final Node keyE = XPathAPI.selectSingleNode(sigE, "//ds:KeyInfo", sigE);

                token.getFirstChild().setTextContent(
                        XPathAPI.selectSingleNode(keyE, "//ds:X509Certificate", keyE).getFirstChild().getNodeValue());

                // Удаляем содержимое KeyInfo
                keyE.removeChild(XPathAPI.selectSingleNode(keyE, "//ds:X509Data", keyE));
                final NodeList chl = keyE.getChildNodes();
                for (int i = 0; i < chl.getLength(); i++) {
                    keyE.removeChild(chl.item(i));
                }
                // Узел KeyInfo содержит указание на проверку подписи с помощью сертификата SenderCertificate.
                final Node str =
                        keyE.appendChild(doc.createElementNS(WS_SECURITY_SECEXT_URI, "wsse:SecurityTokenReference"));
                final Element strRef =
                        (Element) str.appendChild(doc.createElementNS(WS_SECURITY_SECEXT_URI, "wsse:Reference"));
                strRef.setAttribute("ValueType", VALUE_TYPE_ATTRIBUTE);
                strRef.setAttribute("URI", "#CertId");
                token.appendChild(sigE);

                String msg = toString(doc);

                smc.getMessage().getSOAPPart().setContent(new StreamSource(new ByteArrayInputStream(msg.getBytes("UTF-8"))));

                messageLogger.LogPacket(msg, IRNIPMessageToLog.MESSAGE_OUT);

            } else {
                final SOAPPart soapPart = smc.getMessage().getSOAPPart();
                final Document doc = soapPart.getEnvelope().getOwnerDocument();
                String msg = toString(doc);
                messageLogger.LogPacket(msg, IRNIPMessageToLog.MESSAGE_IN);
            }
        } catch (Exception e) {
            logger.error("Error in handle Rnip message", e);
        }

        return true;
    }

    public static String toString(Document doc) {
        try {
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "no");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            transformer.transform(new DOMSource(doc), new StreamResult(sw));
            return sw.toString();
        } catch (Exception ex) {
            throw new RuntimeException("Error converting to String", ex);
        }
    }

    public static XMLGregorianCalendar toXmlGregorianCalendar(Date date) throws DatatypeConfigurationException {
        GregorianCalendar c = new GregorianCalendar();
        c.setTime(date);
        XMLGregorianCalendar xc= DatatypeFactory
                .newInstance().newXMLGregorianCalendarDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1, c.get(Calendar.DAY_OF_MONTH),
                        DatatypeConstants.FIELD_UNDEFINED);
        xc.setTime(c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), c.get(Calendar.SECOND));
        return xc;
    }

    @Override
    public boolean handleFault(SOAPMessageContext context) {
        return false;
    }

    @Override
    public void close(MessageContext context) {
    }

}
