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

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.net.URL;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
import javax.xml.transform.stream.StreamSource;

import org.apache.ws.security.message.WSSecHeader;
import org.apache.ws.security.message.token.X509Security;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.CryptoPro.JCP.JCP;
import ru.CryptoPro.JCP.tools.Array;
import ru.CryptoPro.JCPxml.xmldsig.JCPXMLDSigInit;
import wss4j.gosuslugi.smev.SignatureTool.SignatureTool;
import wss4j.gosuslugi.smev.SignatureTool.SignatureToolService;
import wss4j.gosuslugi.smev.SignatureTool.SignatureToolServiceLocator;
import wss4j.gosuslugi.smev.SignatureTool.xsd.VerifySignatureRequestType;
import wss4j.gosuslugi.smev.SignatureTool.xsd.VerifySignatureResponseType;
import wss4j.utility.SpecUtility;

public class SMEVExample {

    /**
     * Файл с документом для подписи.
     */
    private final static String inSOAPFile = System.getProperty("user.dir") + "/data/soap_net.xml";
    /**
     * Адрес тестового сервиса СМЭВ.
     */
    private final static String smevService = "http://localhost:7777/gateway/services/SID0003038";
    //private final static String smevService = "http://188.254.16.92:7777/gateway/services/SID0003038";
    /**
     * Нужно ли проверять подпись онлайн в сервисе СМЭВ.
     */
    private final static boolean checkOnline = true;

	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {

        /*** Инициализация ***/

		// Инициализация Transforms алгоритмов.
		com.sun.org.apache.xml.internal.security.Init.init();
		
		// Инициализация JCP XML провайдера.
		if(!JCPXMLDSigInit.isInitialized()) {
    		JCPXMLDSigInit.init();
		}

        // Инициализация ключевого контейнера.
        KeyStore keyStore = KeyStore.getInstance(JCP.HD_STORE_NAME);
        keyStore.load(null, null);

        // Получение ключа и сертификата.
        PrivateKey privateKey = (PrivateKey)keyStore.getKey(SpecUtility.DEFAULT_ALIAS,
                SpecUtility.DEFAULT_PASSWORD);
        X509Certificate cert = (X509Certificate) keyStore.getCertificate(SpecUtility.DEFAULT_ALIAS);

        /*** Подготовка документа ***/

        MessageFactory mf = MessageFactory.newInstance();

        SOAPMessage message = mf.createMessage();
        SOAPPart soapPart = message.getSOAPPart();

        // Читаем сообщение из файла.
        FileInputStream is = new FileInputStream(inSOAPFile);
        soapPart.setContent(new StreamSource(is));

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
        String msg = org.apache.ws.security.util.XMLUtils.PrettyDocumentToString(doc);

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
            VerifySignatureRequestType vsrType = new VerifySignatureRequestType(msg, true, "http://smev.gosuslugi.ru/actors/smev");
                    VerifySignatureResponseType result = st.verifySignature(vsrType);

                    // Результат проверки подписи сервисом СМЭВ.
                    System.out.println("Verified by SMEV: code = " + result.getError().getErrorCode() +
                    ", message = " + result.getError().getErrorMessage());
        }
	}
}
