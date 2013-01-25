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

package ru.axetta.ecafe.processor.core.service;

import java.io.*;
import java.net.URL;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
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
import org.hibernate.Session;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.org.apache.xml.internal.security.Init;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xpath.internal.XPathAPI;
import generated.rnip.roskazna.smevunifoservice.UnifoTransferMsg;
import generated.rnip.roskazna.xsd.exportpaymentsresponse.ExportPaymentsResponse;
import generated.rnip.roskazna.xsd.paymentinfo.PaymentInfoType;
import ru.CryptoPro.JCP.JCP;
import ru.CryptoPro.JCP.tools.Array;

import ru.axetta.ecafe.processor.core.OnlinePaymentProcessor;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ClientPayment;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;

import javax.xml.namespace.QName;
import javax.xml.soap.*;
import javax.xml.parsers.DocumentBuilderFactory;

@Component
@Scope("singleton")
public class LoadPaymentsService {

    /**
     * Файл с документом для подписи.
     */
    //private final static String inSOAPFile = System.getProperty("user.dir") + "/data/soap_net.xml";
    private final static String RNIP_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private final static String inSOAPFile = "META-INF/rnip/getPayments_byDate.xml";//;
    /**
     * Адрес тестового сервиса СМЭВ.
     */
    private final static String URL_ADDR = "http://193.47.154.2:7003/UnifoSecProxy_WAR/SmevUnifoService";
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(LoadPaymentsService.class);
    private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");


    public static List<String> PAYMENT_PARAMS = new ArrayList<String>();

    static {
        PAYMENT_PARAMS.add("SupplierBillID");
        PAYMENT_PARAMS.add("Amount");
        PAYMENT_PARAMS.add("PaymentDate");
        PAYMENT_PARAMS.add("NUM_DOGOVOR");  // Это номер договора в нашей БД
        PAYMENT_PARAMS.add("SRV_CODE");     // Здесь содержится
    }

    public static boolean isOn() {
        return RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_IMPORT_RNIP_PAYMENTS_ON);
    }


    public static void setOn(boolean on) {
        RuntimeContext.getInstance()
                .setOptionValueWithSave(Option.OPTION_IMPORT_RNIP_PAYMENTS_ON, "" + (on ? "1" : "0"));
    }


    private void setLastUpdateDate(Date date) {
        RuntimeContext.getInstance()
                .setOptionValueWithSave(Option.OPTION_IMPORT_RNIP_PAYMENTS_TIME, dateFormat.format(date));
    }


    private Date getLastUpdateDate() {
        try {
            String d = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_TIME);
            if (d == null || d.length() < 1) {
                return new Date(0);
            }
            return dateFormat.parse(d);
        } catch (Exception e) {
            logger.error("Failed to parse date from options", e);
        }
        return new Date(0);
    }


    public void run() {
        if (!RuntimeContext.getInstance().isMainNode() || !isOn()) {
            return;
        }
        Date updateTime = new Date(System.currentTimeMillis());

        SOAPMessage response = null;
        try {
            response = executeRequest(updateTime);
        } catch (Exception e) {
            logger.error("Failed to request data from RNIP service", e);
        }

        if (response == null) {
            setLastUpdateDate(new Date(System.currentTimeMillis()));
            return;
        }

        List<Map<String, String>> payments = null;
        try {
            payments = parsePayments(response);
        } catch (Exception e) {
            logger.error("Failed to parse payments from soap message", e);
        }

        if (payments == null || payments.size() < 1) {
            setLastUpdateDate(updateTime);
            return;
        }

        addPaymentsToDb(payments);
        setLastUpdateDate(updateTime);
    }

    public SOAPMessage executeRequest(Date updateTime) throws Exception {

        /*** Инициализация ***/
        Init.init();

        // Инициализация Transforms алгоритмов.
        com.sun.org.apache.xml.internal.security.Init.init();

        // Инициализация ключевого контейнера.
        KeyStore keyStore = KeyStore.getInstance(JCP.HD_STORE_NAME);
        keyStore.load(null, null);

        // Получение ключа и сертификата.
        PrivateKey privateKey = (PrivateKey) keyStore.getKey("test", "test".toCharArray());
        X509Certificate cert = (X509Certificate) keyStore.getCertificate("test");

        /*** Подготовка документа ***/
        MessageFactory mf = MessageFactory.newInstance();
        SOAPMessage message = mf.createMessage();
        SOAPPart soapPart = message.getSOAPPart();

        // Читаем сообщение из файла.
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(inSOAPFile);
        //FileInputStream fis = new FileInputStream(is);
        soapPart.setContent(doMacroReplacement(updateTime, new StreamSource(is)));
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
        Transform transformC14N = fac.newTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS, (XMLStructure) null);
        transformList.add(transformC14N);
        // Ссылка на подписываемые данные.
        Reference ref = fac
                .newReference("#body", fac.newDigestMethod("http://www.w3.org/2001/04/xmldsig-more#gostr3411", null),
                        transformList, null, null);
        // Блок SignedInfo.
        SignedInfo si = fac.newSignedInfo(
                fac.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null),
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
        cerVal.setTextContent(
                XPathAPI.selectSingleNode(keyE, "//ds:X509Certificate", keyE).getFirstChild().getNodeValue());
        // Удаляем элементы KeyInfo, попавшие в тело документа. Они должны быть только в header.
        keyE.removeChild(XPathAPI.selectSingleNode(keyE, "//ds:X509Data", keyE));
        NodeList chl = keyE.getChildNodes();
        for (int i = 0; i < chl.getLength(); i++) {
            keyE.removeChild(chl.item(i));
        }

        // Блок KeyInfo содержит указание на проверку подписи с помощью сертификата SenderCertificate.
        Node str = keyE.appendChild(
                doc.createElementNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
                        "wsse:SecurityTokenReference"));
        Element strRef = (Element) str.appendChild(
                doc.createElementNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd",
                        "wsse:Reference"));
        strRef.setAttribute("ValueType",
                "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3");
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

        //Array.writeFile(inSOAPFile + ".signed.uri.xml", msg.getBytes("utf-8"));

        /*** а) Проверка подписи (локально) ***/
        // Получение блока, содержащего сертификат.
        final Element wssecontext = doc.createElementNS(null, "namespaceContext");
        wssecontext.setAttributeNS("http://www.w3.org/2000/xmlns/", "xmlns:" + "wsse".trim(),
                "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
        NodeList secnodeList = XPathAPI.selectNodeList(doc.getDocumentElement(), "//wsse:Security");

        // Поиск элемента сертификата.
        Element r = null;
        Element el = null;
        if (secnodeList != null && secnodeList.getLength() > 0) {
            String actorAttr = null;
            for (int i = 0; i < secnodeList.getLength(); i++) {
                el = (Element) secnodeList.item(i);
                actorAttr = el.getAttributeNS("http://schemas.xmlsoap.org/soap/envelope/", "actor");
                if (actorAttr != null && actorAttr.equals("http://smev.gosuslugi.ru/actors/smev")) {
                    r = (Element) XPathAPI.selectSingleNode(el, "//wsse:BinarySecurityToken[1]", wssecontext);
                    break;
                }
            }
        }

        if (r == null) {
            return null;
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
        DOMValidateContext valContext = new DOMValidateContext(KeySelector.singletonKeySelector(cert.getPublicKey()),
                nl.item(0));
        javax.xml.crypto.dsig.XMLSignature signature = fac.unmarshalXMLSignature(valContext);

        // Проверяем подпись.
        System.out.println("Verified locally: " + signature.validate(valContext));
        return send(message);
    }


    public SOAPMessage send(SOAPMessage message) throws Exception {
        // Use SAAJ to convert Document to SOAPElement
        SOAPConnectionFactory sfc = SOAPConnectionFactory.newInstance();
        SOAPConnection connection = sfc.createConnection();
        URL endpoint = new URL(URL_ADDR);
        SOAPMessage response = connection.call(message, endpoint);
        connection.close();
        connection = null;
        return response;
    }


    public List<Map<String, String>> parsePayments(SOAPMessage response) throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);

        JAXBContext jc = JAXBContext.newInstance(UnifoTransferMsg.class);
        Unmarshaller u = jc.createUnmarshaller();
        Object o = u.unmarshal(response.getSOAPBody().getFirstChild());

        UnifoTransferMsg m = (UnifoTransferMsg) o;
        jc = JAXBContext.newInstance(PaymentInfoType.class);
        u = jc.createUnmarshaller();
        if (((ExportPaymentsResponse) m.getMessageData().getAppData().
                getExportDataResponse().getResponseTemplate()).getPayments() == null) {
            return Collections.EMPTY_LIST;
        }
        List<ExportPaymentsResponse.Payments.PaymentInfo> piList = ((ExportPaymentsResponse) m.getMessageData()
                .getAppData().
                        getExportDataResponse().getResponseTemplate()).getPayments().getPaymentInfo();
        List<Map<String, String>> result = new ArrayList<Map<String, String>>();
        for (ExportPaymentsResponse.Payments.PaymentInfo pi : piList) {
            String paymentInfoStr = new String(pi.getPaymentData(), "cp1251");
            InputStream stream = new ByteArrayInputStream(paymentInfoStr.getBytes());
            InputSource is = new InputSource(stream);
            is.setEncoding("UTF-8");
            Document doc = builderFactory.newDocumentBuilder().parse(is);
            result.add(parsePayment(doc));
        }
        return result;
    }


    public Map<String, String> parsePayment(Document doc) {
        Map<String, String> vals = new HashMap<String, String>();
        Node root = doc.getFirstChild();
        for (int i = 0; i < root.getChildNodes().getLength(); i++) {
            Node n = root.getChildNodes().item(i);
            for (String param : PAYMENT_PARAMS) {
                if (n.getNodeName().equals(param) || n.getNodeName().equals("AdditionalData")) {
                    String v = n.getFirstChild().getNodeValue();
                    if (n.getNodeName().equals("AdditionalData")) {
                        if (n.getFirstChild().getFirstChild().getNodeValue().equals("NUM_DOGOVOR")) {
                            v = n.getChildNodes().item(1).getFirstChild().getNodeValue();
                            param = "NUM_DOGOVOR";
                        }
                        if (n.getFirstChild().getFirstChild().getNodeValue().equals("SRV_CODE")) {
                            v = n.getChildNodes().item(1).getFirstChild().getNodeValue();
                            param = "SRV_CODE";
                        }
                    }
                    if (!vals.containsKey(param)) {
                        vals.put(param, v);
                    }
                }
            }
        }
        return vals;
    }


    public void addPaymentsToDb(List<Map<String, String>> payments) {
        RuntimeContext runtimeContext = null;
        Session session = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            session = runtimeContext.createPersistenceSession();
        } catch (Exception e) {
            logger.error("Failed to receive DB connection", e);
            return;
        }

        List<Contragent> contrgents = DAOService.getInstance().getContragentsList();


        for (Map<String, String> p : payments) {
            String paymentID = p.get("SupplierBillID");
            String paymentDate = p.get("PaymentDate");
            try {
                String contragentKey = p.get("SRV_CODE").substring(5, 10);
                long idOfContragent = getContragentByRNIPCode(contragentKey, contrgents);
                if (idOfContragent == 0) {
                    continue;
                }
                long idOfClient = DAOUtils.findClientByContractId(session, Long.parseLong(p.get("NUM_DOGOVOR")))
                        .getIdOfClient();
                long amt = Long.parseLong(p.get("Amount"));
                OnlinePaymentProcessor.PayRequest req = new OnlinePaymentProcessor.PayRequest(
                        OnlinePaymentProcessor.PayRequest.V_0, false, idOfContragent, null,
                        ClientPayment.ATM_PAYMENT_METHOD, idOfClient, paymentID, paymentDate + "/" + paymentID, amt,
                        false);
                OnlinePaymentProcessor.PayResponse resp = runtimeContext.getOnlinePaymentProcessor()
                        .processPayRequest(req);
                logger.info(String.format("Request (%s) processed: %s", req == null ? "null" : req.toString(),
                        resp == null ? "null" : resp.toString()));
            } catch (Exception e) {
                logger.error("Failed to insert payment #" + paymentID + " into database", e);
            }
        }
    }


    public long getContragentByRNIPCode(String contragentKey, List<Contragent> contragents) {
        for (Contragent c : contragents) {
            String v = c.getRemarks();
            String cc = "";
            if (v != null && v.indexOf("{RNIP=") > -1) {
                cc = v.substring(v.indexOf("{RNIP=") + "{RNIP=".length(),
                        v.indexOf("}", v.indexOf("{RNIP=") + "{RNIP=".length()));
            }
            if (cc.equals(contragentKey)) {
                return c.getIdOfContragent();
            }
        }
        return 0L;
    }


    public StreamSource doMacroReplacement(Date updateTime, StreamSource ss) throws Exception {
        InputStream is = ss.getInputStream();
        byte[] data = new byte[is.available()];
        is.read(data);

        String content = new String(data);
        if (content.indexOf("%START_DATE%") > 1) {
            String str = new SimpleDateFormat(RNIP_DATE_FORMAT).format(getLastUpdateDate());
            content = content.replaceAll("%START_DATE%", str);
        }
        if (content.indexOf("%END_DATE%") > 1) {
            String str = new SimpleDateFormat(RNIP_DATE_FORMAT).format(new Date(System.currentTimeMillis()));
            content = content.replaceAll("%END_DATE%", str);
        }
        StreamSource res = new StreamSource();
        res.setReader(new StringReader(content));
        return res;
    }
}
