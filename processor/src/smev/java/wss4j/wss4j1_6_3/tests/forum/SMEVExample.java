/**
 * Copyright 2004-2012 Crypto-Pro. All rights reserved.
 * Этот файл содержит информацию, являющуюся
 * собственностью компании Крипто-Про.
 *
 * Любая часть этого файла не может быть скопирована,
 * исправлена, переведена на другие языки,
 * локализована или модифицирована любым способом,
 * откомпилирована, передана по сети с или на
 * любую компьютерную систему без предварительного
 * заключения соглашения с компанией Крипто-Про.
 */

package wss4j.wss4j1_6_3.tests.forum;

import java.io.*;
import java.net.URL;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.CanonicalizationMethod;
import javax.xml.crypto.dsig.Reference;
import javax.xml.crypto.dsig.SignedInfo;
import javax.xml.crypto.dsig.Transform;
import javax.xml.crypto.dsig.XMLSignatureFactory;
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

import org.apache.ws.security.message.WSSecHeader;
import org.apache.ws.security.message.token.X509Security;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import generated.rnip.roskazna.smevunifoservice.UnifoTransferMsg;
import generated.rnip.roskazna.xsd.exportpaymentsresponse.ExportPaymentsResponse;
import generated.rnip.roskazna.xsd.paymentinfo.PaymentInfoType;
import generated.rnip.roskazna.xsd.responsetemplate.ResponseTemplate;
import ru.CryptoPro.JCP.JCP;
import ru.CryptoPro.JCP.tools.Array;
import ru.CryptoPro.JCPxml.xmldsig.JCPXMLDSigInit;
import wss4j.gosuslugi.smev.SignatureTool.SignatureTool;
import wss4j.gosuslugi.smev.SignatureTool.SignatureToolService;
import wss4j.gosuslugi.smev.SignatureTool.SignatureToolServiceLocator;
import wss4j.gosuslugi.smev.SignatureTool.xsd.VerifySignatureRequestType;
import wss4j.gosuslugi.smev.SignatureTool.xsd.VerifySignatureResponseType;
import wss4j.utility.SpecUtility;

import ru.axetta.ecafe.processor.core.utils.Base64;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.parsers.DocumentBuilderFactory;

public class SMEVExample {

    /**
     * Файл с документом для подписи.
     */
    //private final static String inSOAPFile = System.getProperty("user.dir") + "/data/soap_net.xml";
    //private final static String inSOAPFile = "D:/2/editCategory.xml";
    private final static String inSOAPFile = "D:/2/getPayments_byDate.xml";
    /**
     * Адрес тестового сервиса СМЭВ.
     */
    private final static String smevService = "http://193.47.154.2:7003/UnifoSecProxy_WAR/SmevUnifoService";
    //private final static String smevService = "http://localhost:7777/gateway/services/SID0003038";
    //private final static String smevService = "http://188.254.16.92:7777/gateway/services/SID0003038";
    /**
     * Нужно ли проверять подпись онлайн в сервисе СМЭВ.
     */
    private final static boolean checkOnline = true;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

        String v = "ddd [RNIP_CODE=00402] dqw";
        String cc = "";
        if (v.indexOf ("[RNIP_CODE=") > -1)
            {
            cc = v.substring (v.indexOf ("[RNIP_CODE=") + "[RNIP_CODE=".length (),
                                     v.indexOf ("]", v.indexOf ("[RNIP_CODE=")));
            }

        /*** Инициализация ***/
        org.apache.xml.security.Init.init();

		// Инициализация Transforms алгоритмов.
		com.sun.org.apache.xml.internal.security.Init.init();
		
		// Инициализация JCP XML провайдера.
		/*if(!JCPXMLDSigInit.isInitialized()) {
    		JCPXMLDSigInit.init();
		}*/

        // Инициализация ключевого контейнера.
        KeyStore keyStore = KeyStore.getInstance(JCP.HD_STORE_NAME);
        keyStore.load(null, null);

        // Получение ключа и сертификата.
        PrivateKey privateKey = (PrivateKey)keyStore.getKey("test",
                "test".toCharArray());
        X509Certificate cert = (X509Certificate) keyStore.getCertificate("test");

        /*** Подготовка документа ***/

        MessageFactory mf = MessageFactory.newInstance();

        SOAPMessage message = mf.createMessage();
        SOAPPart soapPart = message.getSOAPPart();

        // Читаем сообщение из файла.
        FileInputStream is = new FileInputStream(inSOAPFile);
        soapPart.setContent(doMacroReplacement (new StreamSource(is)));
        message.getSOAPPart().getEnvelope().addNamespaceDeclaration("ds", "http://www.w3.org/2000/09/xmldsig#");

        // Формируем заголовок.
        WSSecHeader header = new WSSecHeader();
        header.setActor("http://smev.gosuslugi.ru/actors/smev");
        header.setMustUnderstand(false);

        // Получаем документ.
        Document doc = message.getSOAPPart().getEnvelope().getOwnerDocument();
        header.insertSecurityHeader(message.getSOAPPart().getEnvelope().getOwnerDocument());

        // Подписываемый элемент.
        Element token = header.getSecurityHeader();

        /*** Подпись данных ***/

        // Загрузка провайдера.
        Provider xmlDSigProvider = new ru.CryptoPro.JCPxml.dsig.internal.dom.XMLDSigRI();

        // Преобразования над документом.
        final Transforms transforms = new Transforms(doc);
        transforms.addTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS);

        XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM", xmlDSigProvider);

        // Преобразования над блоком SignedInfo
        List<Transform> transformList = new ArrayList<Transform>();
        Transform transformC14N =
                fac.newTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS, (XMLStructure) null);
        transformList.add(transformC14N);

        // Ссылка на подписываемые данные.
        Reference ref = fac.newReference("#body",
            fac.newDigestMethod("http://www.w3.org/2001/04/xmldsig-more#gostr3411", null),
            transformList, null, null);

        // Блок SignedInfo.
        SignedInfo si = fac.newSignedInfo( fac.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE,
            (C14NMethodParameterSpec) null),
            fac.newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411", null),
            Collections.singletonList(ref));

        // Блок KeyInfo.
        KeyInfoFactory kif = fac.getKeyInfoFactory();
        X509Data x509d = kif.newX509Data(Collections.singletonList(cert));
        KeyInfo ki = kif.newKeyInfo(Collections.singletonList(x509d));

        // Подпись данных.
        javax.xml.crypto.dsig.XMLSignature sig = fac.newXMLSignature(si, ki);
        DOMSignContext signContext = new DOMSignContext(privateKey, token);
        sig.sign(signContext);

        // Блок подписи Signature.
        Element sigE = (Element) XPathAPI.selectSingleNode(signContext.getParent(), "//ds:Signature");
        // Блок данных KeyInfo.
        Node keyE = XPathAPI.selectSingleNode(sigE, "//ds:KeyInfo", sigE);

        // Элемент SenderCertificate, который должен содержать сертификат.
        Element cerVal = (Element) XPathAPI.selectSingleNode(token, "//*[@wsu:Id='SenderCertificate']");
        cerVal.setTextContent(XPathAPI.selectSingleNode(keyE, "//ds:X509Certificate", keyE).getFirstChild().getNodeValue());

        // Удаляем элементы KeyInfo, попавшие в тело документа. Они должны быть только в header.
        keyE.removeChild(XPathAPI.selectSingleNode(keyE, "//ds:X509Data", keyE));

        NodeList chl = keyE.getChildNodes();

        for (int i = 0; i < chl.getLength(); i++) {
            keyE.removeChild(chl.item(i));
        }

        // Блок KeyInfo содержит указание на проверку подписи с помощью сертификата SenderCertificate.
        Node str = keyE.appendChild(doc.createElementNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
                "wsse:SecurityTokenReference"));
        Element strRef = (Element)str.appendChild(doc.createElementNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
                "wsse:Reference"));

        strRef.setAttribute("ValueType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3");
        strRef.setAttribute("URI", "#SenderCertificate");
        header.getSecurityHeader().appendChild(sigE);

        // Получение документа в виде строки и сохранение в файл.
        //String msg = org.apache.ws.security.util.XMLUtils.PrettyDocumentToString(doc);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        String msg = writer.getBuffer().toString().replaceAll("\n|\r", "");

        Array.writeFile(inSOAPFile + ".signed.uri.xml", msg.getBytes("utf-8"));

        /*** а) Проверка подписи (локально) ***/

        // Получение блока, содержащего сертификат.
        final Element wssecontext = doc.createElementNS(null, "namespaceContext");
        wssecontext.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:"+"wsse".trim(),
                "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
        NodeList secnodeList = XPathAPI.selectNodeList(doc.getDocumentElement(), "//wsse:Security");

        // Поиск элемента сертификата.
        Element r = null;
        Element el = null;
        if( secnodeList != null&&secnodeList.getLength()>0 ) {

            String actorAttr = null;

            for( int i = 0; i<secnodeList.getLength(); i++ ) {

                el = (Element) secnodeList.item(i);
                actorAttr = el.getAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "actor");

                if(actorAttr != null&&actorAttr.equals("http://smev.gosuslugi.ru/actors/smev")) {

                    r = (Element)XPathAPI.selectSingleNode(el, "//wsse:BinarySecurityToken[1]", wssecontext);
                    break;
                }
            }
        }

        if(r == null) {
            return;
        }

        // Получение сертификата.
        final X509Security x509 = new X509Security(r);

        cert = (X509Certificate) CertificateFactory.getInstance("X.509")
                .generateCertificate(new ByteArrayInputStream(x509.getToken()));

        if (cert == null) {
            throw new Exception("Сертификат не найден.");
        }

        System.out.println("Verify by: " + cert.getSubjectDN());

        // Поиск элемента с подписью.
        NodeList nl = doc.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "Signature");
        if (nl.getLength() == 0) {
            throw new Exception("Не найден элемент Signature.");
        }

        // Задаем открытый ключ для проверки подписи.
        fac = XMLSignatureFactory.getInstance("DOM", xmlDSigProvider);
        DOMValidateContext valContext = new DOMValidateContext(KeySelector.singletonKeySelector(cert.getPublicKey()), nl.item(0));
        javax.xml.crypto.dsig.XMLSignature signature = fac.unmarshalXMLSignature(valContext);

        // Проверяем подпись.
        System.out.println( "Verified locally: " + signature.validate(valContext));

        /*** б) Проверка подписи (СМЭВ) ***/

        if (checkOnline) {

            // Используем веб-клиент СМЭВ.
            SignatureToolService sts = new SignatureToolServiceLocator();

            // Задаем адрес тестового сервиса.
            SignatureTool st = sts.getSignatureToolPort(new URL(smevService));

            // Передаем документ, при этом зарещаем проверять сертификат.
            send (msg, message);
            /*VerifySignatureRequestType vsrType = new VerifySignatureRequestType(msg, true, "http://smev.gosuslugi.ru/actors/smev");
            VerifySignatureResponseType result = st.verifySignature(vsrType);

            // Результат проверки подписи сервисом СМЭВ.
            System.out.println("Verified by SMEV: code = " + result.getError().getErrorCode() +
            ", message = " + result.getError().getErrorMessage());*/
        }
	}

    public static void send (String xmlText, SOAPMessage mmm)
        {
            try {
                // Load the XML text into a DOM DocumentItem
                DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
                builderFactory.setNamespaceAware(true);
                /*InputStream stream  = new ByteArrayInputStream(xmlText.getBytes());
                DocumentItem doc = builderFactory.newDocumentBuilder().parse(stream);*/

                // Use SAAJ to convert DocumentItem to SOAPElement
                SOAPConnectionFactory sfc = SOAPConnectionFactory.newInstance();
                SOAPConnection connection = sfc.createConnection();
                /*MessageFactory msgFactory = MessageFactory.newInstance();
                SOAPMessage    message    = msgFactory.createMessage();
                SOAPBody       soapBody   = message.getSOAPBody();*/

                /*SOAPHeader sh = sm.getSOAPHeader();
                SOAPBody sb = sm.getSOAPBody();
                sh.detachNode();
                QName bodyName = new QName("http://quoteCompany.com", "GetQuote", "d");
                SOAPBodyElement bodyElement = sb.addBodyElement(bodyName);
                QName qn = new QName("aName");
                SOAPElement quotation = bodyElement.addChildElement(qn);

                quotation.addTextNode("TextMode");

                System.out.println("\n Soap Request:\n");
                sm.writeTo(System.out);
                System.out.println();*/

                URL endpoint = new URL("http://193.47.154.2:7003/UnifoSecProxy_WAR/SmevUnifoService");


                SOAPMessage response = connection.call(mmm, endpoint);



                JAXBContext jc = JAXBContext.newInstance(UnifoTransferMsg.class);
                Unmarshaller u = jc.createUnmarshaller();
                Object o = u.unmarshal(response.getSOAPBody().getFirstChild());

                System.out.println(o);

                UnifoTransferMsg m = (UnifoTransferMsg)o;
                jc = JAXBContext.newInstance(PaymentInfoType.class);
                u = jc.createUnmarshaller();
                List <ExportPaymentsResponse.Payments.PaymentInfo> piList = ((ExportPaymentsResponse)m.getMessageData().getAppData().getExportDataResponse().getResponseTemplate()).getPayments().getPaymentInfo();
                for (ExportPaymentsResponse.Payments.PaymentInfo pi : piList)
                    {
                    String paymentInfoStr = new String (pi.getPaymentData (), "UTF-8");
                    InputStream stream  = new ByteArrayInputStream (paymentInfoStr.getBytes());
                    InputSource is = new InputSource (stream);
                    is.setEncoding("UTF-8");
                    Document doc = builderFactory.newDocumentBuilder ().parse (is);
                    parsePayment (doc);
                    }
                /*for (ExportPaymentsResponse.Payments.PaymentInfo pi : ((ExportPaymentsResponse)m.getMessageData().getAppData().getExportDataResponse().getResponseTemplate()).getPayments().getPaymentInfo()) {
                    Object payment = u.unmarshal(new ByteArrayInputStream(pi.getPaymentData()));
                    System.out.println(payment);
                }*/
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }


    public static List <String> PAYMENT_PARAMS = new ArrayList <String> ();
    static
        {
        PAYMENT_PARAMS.add ("SupplierBillID");
        PAYMENT_PARAMS.add ("Amount");
        PAYMENT_PARAMS.add ("PaymentDate");
        PAYMENT_PARAMS.add ("NUM_DOGOVOR");  // Это номер договора в нашей БД
        PAYMENT_PARAMS.add ("SRV_CODE");     // Здесь содержится
        }
    public static void parsePayment (Document doc)
        {
        Map<String, String> vals = new HashMap<String, String>();
        Node root = doc.getFirstChild ();
        for (int i=0; i<root.getChildNodes ().getLength(); i++)
            {
                Node n = root.getChildNodes().item(i);
                for (String param : PAYMENT_PARAMS)
                {
                    if (n.getNodeName().equals(param) ||
                            n.getNodeName().equals("AdditionalData"))
                    {
                        String v = n.getFirstChild().getNodeValue();
                        if (n.getNodeName().equals("AdditionalData"))
                        {
                            if (n.getFirstChild().getFirstChild().getNodeValue().equals("NUM_DOGOVOR"))
                            {
                                v = n.getChildNodes().item(1).getFirstChild().getNodeValue();
                                param = "NUM_DOGOVOR";
                            }
                            if (n.getFirstChild().getFirstChild().getNodeValue().equals("SRV_CODE"))
                            {
                                v = n.getChildNodes().item(1).getFirstChild().getNodeValue();
                                param = "SRV_CODE";
                            }
                        }
                        if (!vals.containsKey (param))
                        {
                            vals.put(param, v);
                        }
                    }
                }
            }
        int dwq =1;
        }


    public static StreamSource doMacroReplacement (StreamSource ss) throws Exception
    {
        InputStream is = ss.getInputStream ();
        byte[] data = new byte[is.available()];
        is.read (data);

        String content = new String(data);
        if (content.indexOf ("%START_DATE%") > 1)
        {
            content.replaceAll ("%START_DATE%", "2011-03-11T09:15:25.0Z");
        }
        StreamSource res = new StreamSource ();
        res.setReader (new StringReader (content));
        return res;
    }
    }
