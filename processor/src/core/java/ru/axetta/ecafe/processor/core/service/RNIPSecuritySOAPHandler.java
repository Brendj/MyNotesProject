package ru.axetta.ecafe.processor.core.service;

import com.sun.org.apache.xpath.internal.XPathAPI;
import xades.Signer;
import xades.config.IXAdESConfig;
import xades.config.XAdESConfig;
import xades.config.container.ISignatureContainer;
import xades.util.GostXAdESUtility;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;

import org.apache.ws.security.message.WSSecHeader;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.algorithms.SignatureAlgorithm;
import org.apache.xml.security.exceptions.AlgorithmAlreadyRegisteredException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

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
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
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

    private static final String WS_SECURITY_SECEXT_URI =
            "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd";
    private static final String WS_SECURITY_UTILITY_URI =
            "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd";
    private static final String ENCODING_TYPE_ATTRIBUTE =
            "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary";
    private static final String VALUE_TYPE_ATTRIBUTE =
            "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3";
    private static final String DIGEST_METHOD = "http://www.w3.org/2001/04/xmldsig-more#gostr3411";
    private static final String SIGNATURE_METHOD = "http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411";

    private static final String MESSAGE_NS = "http://smev.gosuslugi.ru/rev120315";
    private static final String NS_SMEV = "http://roskazna.ru/gisgmp/02000000/SmevGISGMPService/";
    private static final String NS_INC = "http://www.w3.org/2004/08/xop/include";

    private IRNIPMessageToLog messageLogger;
    private String containerAlias;
    private String containerPassword;
    private static PrivateKey privateKey;
    private static KeyStore keyStore;
    private static X509Certificate cert;


    public RNIPSecuritySOAPHandler(String containerAlias, String containerPassword, IRNIPMessageToLog messageLogger) {
        this.containerAlias = containerAlias;
        this.containerPassword = containerPassword;
        this.messageLogger = messageLogger;
    }

    @Override
    public Set<QName> getHeaders() {
        return Collections.emptySet();
    }

    final Map<String, String> MAP_DIGEST_OID_2_TSA_URL =
            new LinkedHashMap<String, String>() {{
                put(GostXAdESUtility.GOST_DIGEST_OID, RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_TSA_SERVER));
                // put(JCP.GOST_DIGEST_2012_256_OID, Configuration.TSA_DEFAULT_ADDRESS); // >= JCP 2.0
                // put(JCP.GOST_DIGEST_2012_512_OID, Configuration.TSA_DEFAULT_ADDRESS); // > >= JCP 2.0
            }};

    @Override
    public boolean handleMessage(SOAPMessageContext smc) {

        final Boolean outboundProperty = (Boolean) smc.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        try {
            if (outboundProperty) {
                if (privateKey == null) {
                    Security.insertProviderAt(new ru.CryptoPro.JCP.JCP(), 1);
                    String store = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_STORE_NAME);
                    //keyStore = KeyStore.getInstance(JCP.HD_STORE_NAME);
                    keyStore = KeyStore.getInstance(store);
                    keyStore.load(null, null);
                    privateKey = (PrivateKey) keyStore.getKey(containerAlias, containerPassword.toCharArray());
                    cert = (X509Certificate) keyStore.getCertificate(containerAlias);
                    org.apache.xml.security.Init.init();

                    try {
                        SignatureAlgorithm.register("http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411", "ru.CryptoPro.JCPxml.xmldsig.SignatureGostR34102001$SignatureGostR34102001GostR3411");
                        SignatureAlgorithm.register("urn:ietf:params:xml:ns:cpxmlsec:algorithms:gostr34102001-gostr3411", "ru.CryptoPro.JCPxml.xmldsig.SignatureGostR34102001$SignatureGostURN");
                        JCEMapper.register("http://www.w3.org/2001/04/xmldsig-more#gostr3411", new JCEMapper.Algorithm("REQUIRED", "GOST3411", "MessageDigest"));
                        JCEMapper.register("http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411", new JCEMapper.Algorithm("REQUIRED", "GOST3411withGOST3410EL", "Signature"));
                    } catch (AlgorithmAlreadyRegisteredException ignore) {
                    }
                }
                final SOAPPart soapPart = smc.getMessage().getSOAPPart();

                final Document source_doc = soapPart.getEnvelope().getOwnerDocument();
                //добавляем неймспейсы к Message и MessageData
                String sss = toString(source_doc)
                        .replaceAll("<Message>", String.format("<pmes:Message xmlns:pmes=\"%s\">", MESSAGE_NS))
                        .replaceAll("</Message>", "</pmes:Message>")
                        .replaceAll("<MessageData>",
                                String.format("<pmesd:MessageData xmlns:pmesd=\"%s\">", MESSAGE_NS))
                        .replaceAll("</MessageData>", "</pmesd:MessageData>")
                        .replaceAll(" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:type=\"ns4:GISGMPTransferMsgRequest\"", "");
                //org.apache.ws.security.util.XMLUtils.PrettyDocumentToString(source_doc);

                InputStream in = new ByteArrayInputStream(sss.getBytes("UTF-8"));
                String elementId = "I_52d85fa5-18ae-11e5-b50b-bcaec5d977ce";
                IXAdESConfig xadesConfig = new XAdESConfig(
                        "JCP", RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_STORE_NAME), new ISignatureContainer() {
                    @Override
                    public String getAlias() {
                        return RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_ALIAS);
                    }

                    @Override
                    public char[] getPassword() {
                        return RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_PASSWORD).toCharArray();
                    }

                    @Override
                    public String getTsaAddress() {
                        return null;
                    }
                });

                boolean useXadesT = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_IMPORT_RNIP_USE_XADEST_ON);
                Document signed_doc;
                if (useXadesT) {
                    signed_doc = Signer.sign(privateKey, cert, xadesConfig.getDefaultProvider(), in,
                        elementId, MAP_DIGEST_OID_2_TSA_URL);
                } else {
                    signed_doc = newDocumentFromInputStream(in);
                }

                DOMSource domSource = new DOMSource(signed_doc);
                soapPart.setContent(domSource);

                //////////////////
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

                final Element sec = header.insertSecurityHeader(soapPart);
                final Document doc = soapPart.getEnvelope().getOwnerDocument();
                final Element secToken = (Element) sec
                        .appendChild(doc.createElementNS(WS_SECURITY_SECEXT_URI, "wsse:BinarySecurityToken"));
                secToken.setAttribute("EncodingType", ENCODING_TYPE_ATTRIBUTE);
                secToken.setAttribute("ValueType", VALUE_TYPE_ATTRIBUTE);
                secToken.setAttribute("wsu:Id", "CertId");
                header.getSecurityHeader().appendChild(secToken);

                final Provider xmlDSigProvider = new ru.CryptoPro.JCPxml.dsig.internal.dom.XMLDSigRI();
                org.apache.xml.security.Init.init();
                final org.apache.xml.security.transforms.Transforms transforms =
                        new org.apache.xml.security.transforms.Transforms(doc);
                transforms
                        .addTransform(org.apache.xml.security.transforms.Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);
                final XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM", xmlDSigProvider);

                final List<Transform> transformList = new ArrayList<Transform>();
                final Transform transformC14N = fac.newTransform(
                        org.apache.xml.security.transforms.Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS,
                        (XMLStructure) null);
                transformList.add(transformC14N);

                final Reference ref =
                        fac.newReference("#body", fac.newDigestMethod(DIGEST_METHOD, null), transformList, null, null);
                final SignedInfo si = fac.newSignedInfo(
                        fac.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null),
                        fac.newSignatureMethod(SIGNATURE_METHOD, null), Collections.singletonList(ref));

                final KeyInfoFactory kif = fac.getKeyInfoFactory();
                final X509Data x509d = kif.newX509Data(Collections.singletonList(cert));
                final KeyInfo ki = kif.newKeyInfo(Collections.singletonList(x509d));

                final Element token = (Element) smc.getMessage().getSOAPHeader().getChildElements().next();
                final javax.xml.crypto.dsig.XMLSignature sig = fac.newXMLSignature(si, ki);
                final DOMSignContext signContext = new DOMSignContext(privateKey, token);
                sig.sign(signContext);

                final Element sigE = (Element) XPathAPI.selectSingleNode(signContext.getParent(), "//ds:Signature");
                final Node keyE = XPathAPI.selectSingleNode(sigE, "//ds:KeyInfo", sigE);
                token.getFirstChild().setTextContent(
                        XPathAPI.selectSingleNode(keyE, "//ds:X509Certificate", keyE).getFirstChild().getNodeValue());
                keyE.removeChild(XPathAPI.selectSingleNode(keyE, "//ds:X509Data", keyE));
                final NodeList chl = keyE.getChildNodes();
                for (int i = 0; i < chl.getLength(); i++) {
                    keyE.removeChild(chl.item(i));
                }
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

    private Document newDocumentFromInputStream(InputStream in) {
        DocumentBuilderFactory factory = null;
        DocumentBuilder builder = null;
        Document ret = null;

        try {
            factory = DocumentBuilderFactory.newInstance();
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

        try {
            ret = builder.parse(new InputSource(in));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret;
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
