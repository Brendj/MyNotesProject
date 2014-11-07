/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
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
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.ws.security.message.WSSecHeader;
import org.apache.ws.security.message.token.X509Security;
import org.hibernate.Session;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.sun.org.apache.xml.internal.security.Init;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xpath.internal.XPathAPI;
import generated.rnip.roskazna.smevunifoservice.UnifoTransferMsg;
import generated.rnip.roskazna.xsd.errinfo.ErrInfo;
import generated.rnip.roskazna.xsd.exportpaymentsresponse.ExportPaymentsResponse;
import generated.rnip.roskazna.xsd.paymentinfo.PaymentInfoType;
import generated.rnip.roskazna.xsd.responsetemplate.ResponseTemplate;
import ru.CryptoPro.JCP.JCP;
import ru.CryptoPro.JCP.tools.Array;

import ru.axetta.ecafe.processor.core.OnlinePaymentProcessor;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Client;
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
public class RNIPLoadPaymentsService {

    
    /**
     * Файл с документом для подписи.
     */
    private final static String RNIP_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    private final static String RNIP_DATE_FORMAT = "yyyy-MM-dd";
    private final static String LOAD_PAYMENTS_TEMPLATE = "META-INF/rnip/getPayments_byDate.xml";
    public final static String CREATE_CATALOG_TEMPLATE = "META-INF/rnip/createCatalog.xml";
    private final static String MODIFY_CATALOG_TEMPLATE = "META-INF/rnip/modifyCatalog.xml";
    ////
    public static final int REQUEST_CREATE_CATALOG=0, REQUEST_MODIFY_CATALOG=1, REQUEST_LOAD_PAYMENTS=2;
    ////
    //private final static String LOAD_PAYMENTS_TEMPLATE = "D:/2/test.xml";// !!!!!!!!!! ЗАМенить !!!!!!!!!
    /**
     * Адрес тестового сервиса СМЭВ.
     */
    private String URL_ADDR = null;//"http://193.47.154.2:7003/UnifoSecProxy_WAR/SmevUnifoService";
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RNIPLoadPaymentsService.class);
    private DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    public static final String RNIP_INPUT_FILE = "/rnip.in.signed.xml";
    public static final String RNIP_OUTPUT_FILE = "/rnip.out.signed.xml";


    public static List<String> PAYMENT_PARAMS = new ArrayList<String>();

    static {
        PAYMENT_PARAMS.add("SystemIdentifier");     // Идентификатор платежа в РНИП (уникаклен)
        PAYMENT_PARAMS.add("Amount");               //  Сумма платежа
        PAYMENT_PARAMS.add("PaymentDate");          // Дата платежа
        PAYMENT_PARAMS.add("NUM_DOGOVOR");          // Это номер договора в нашей БД
        PAYMENT_PARAMS.add("Srv_Code");             // Здесь содержится
        PAYMENT_PARAMS.add("BIK");                  // БИК банка
    }

    protected void info(String str, Object ... args) {
        //if(logger.isInfoEnabled()) {
        try {
            logger.info(String.format(str, args));
        } catch (Exception e) {
            StringBuilder argsStr = new StringBuilder();
            for(Object arg : args) {
                if(argsStr.length() > 0) {
                    argsStr.append(", ");
                }
                argsStr.append(arg.toString());
            }
            logger.info(str + "{" + argsStr + "}");
        }
        //}
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


    private void setLastUpdateDate(Contragent contragent, Date date) {
        /*RuntimeContext.getInstance()
                .setOptionValueWithSave(Option.OPTION_IMPORT_RNIP_PAYMENTS_TIME, dateFormat.format(date));*/
        contragent.setLastRNIPUpdate(dateFormat.format(date));
        DAOService.getInstance().saveEntity(contragent);
    }


    private Date getLastUpdateDate() {
        try {
            info("Получение даты последней выгрузки..");
            String d = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_TIME);
            if (d == null || d.length() < 1) {
                return new Date(0);
            }
            info("Последняя выгрузка состоялась %s", d);
            return dateFormat.parse(d);
        } catch (Exception e) {
            logger.error("Failed to parse date from options", e);
        }
        return new Date(0);
        /*Calendar cal = new GregorianCalendar();
        cal.set(Calendar.YEAR, 2013);
        cal.set(Calendar.MONTH, 3);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();*/
    }


    private Date getLastUpdateDate(Contragent contragent) {
        try {
            info("Получение даты последней выгрузки для контрагента %s..", contragent.getContragentName());
            String d = contragent.getLastRNIPUpdate();//RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_TIME);
            info("Получение даты последней выгрузки для контрагента %s..", contragent.getContragentName());
            if (d == null || d.length() < 1) {
                info("Для контрагента %s загрузок не было, используется 0 мс.", contragent.getContragentName());
                return new Date(0);
            }
            info("Последняя дата выгрузки для контрагента %s состоялась %s", contragent.getContragentName(), d);
            return dateFormat.parse(d);
        } catch (Exception e) {
            logger.error("Failed to parse date from options", e);
        }
        return new Date(0);
    }


    public void createCatalogForContragent (Contragent contragent) throws IllegalStateException, Exception {
        info("Попытка создания каталога для контрагента %s..", contragent.getContragentName());
        String RNIPIdOfContragent = getRNIPIdFromRemarks(contragent.getRemarks());
        if (RNIPIdOfContragent == null || RNIPIdOfContragent.length() < 1) {
            logger.error("Попытка подключить контрагента " + contragent + " к сервису загрузок платежей из РНИП. Ошибка: необходимо "
                         + "указать идентификатор контрагента в ИС ПП - в Ремарки добавить {RNIP=id_контрагента_в_РНИП}");
            return;
        }

        //  Отправка запроса на получение платежей
        SOAPMessage response = null;
        try {
            response = executeRequest(new Date(System.currentTimeMillis()), REQUEST_CREATE_CATALOG, contragent);
        } catch (Exception e) {
            logger.error("Failed to request data from RNIP service", e);
            throw new IllegalStateException("Failed to access RNIP service", e);
        }

        info("Ответ на попытку создания каталога для контрагента %s получен, разбор..", contragent.getContragentName());
        String soapError = checkError (response);
        if (soapError != null && soapError.length () > 1) {
            logger.error(String.format("Ошибка при добавлении каталога для контрагента %s: %s", contragent.getContragentName(), soapError));
            throw new IllegalStateException ("Ошибка во время обращения к РНИП: " + soapError);
        }
        info("Каталог для контрагента %s создан", contragent.getContragentName());
    }


    public void modifyCatalogForContragent (Contragent contragent) throws IllegalStateException, Exception {
        info("Попытка изменения каталога для контрагента %s..", contragent.getContragentName());
        String RNIPIdOfContragent = getRNIPIdFromRemarks(contragent.getRemarks());
        if (RNIPIdOfContragent == null || RNIPIdOfContragent.length() < 1) {
            logger.error("Попытка подключить контрагента " + contragent + " к сервису загрузки платежей из РНИП. Ошибка: необходимо "
                    + "указать идентификатор контрагента в ИС ПП - в Заметки добавить {RNIP=id_контрагента_в_РНИП}");
            return;
        }

        //  Отправка запроса на получение платежей
        SOAPMessage response = null;
        try {
            response = executeRequest(new Date(System.currentTimeMillis()), REQUEST_MODIFY_CATALOG, contragent);
        } catch (Exception e) {
            logger.error("Failed to request data from RNIP service", e);
            throw new IllegalStateException("Failed to access RNIP service", e);
        }

        info("Ответ на попытку изменения каталога для контрагента %s получен, разбор..", contragent.getContragentName());
        String soapError = checkError (response);
        if (soapError != null) {
            logger.error(String.format("Ошибка при изменении каталога для контрагента %s: %s", contragent.getContragentName(), soapError));
            throw new IllegalStateException ("Ошибка во время обращения к РНИП: " + soapError);
        }
        info("Каталог для контрагента %s изменен", contragent.getContragentName());
    }


    public void run() {
        if (!RuntimeContext.getInstance().isMainNode() || !isOn()) {
            return;
        }

        info("Загрузка платежей РНИП..");
        for (Contragent contragent : DAOService.getInstance().getContragentsList()) {
            try {
                RuntimeContext.getAppContext().getBean(RNIPLoadPaymentsService.class).receiveContragentPayments(contragent);
            } catch (Exception e) {
                logger.error("Failed to receive or proceed payments", e);
            }
        }
        info("Загрузка платежей РНИП завершена");
    }

    @Transactional
    public void receiveContragentPayments(Contragent contragent) throws Exception{
        Date updateTime = new Date(System.currentTimeMillis());
        //  Получаем id контрагента в системе РНИП - он будет использоваться при отправке запроса
        String RNIPIdOfContragent = getRNIPIdFromRemarks(contragent.getRemarks());
        if (RNIPIdOfContragent == null || RNIPIdOfContragent.length() < 1) {
            return;
        }


        info("Попытка получения платежей для контрагента %s", contragent.getContragentName());
        //  Отправка запроса на получение платежей
        SOAPMessage response = null;
        try {
            response = executeRequest(updateTime, REQUEST_LOAD_PAYMENTS, contragent);
        } catch (Exception e) {
            logger.error("Failed to request data from RNIP service", e);
        }

        if (response == null) {
            return;
        }

        info("Ответ на получение платежей для контрагента %s получен, разбор..", contragent.getContragentName());
        try {
            String soapError = checkError (response);
            if (soapError != null) {
                logger.error("Произошла ошибка при запросе в РНИП на получение платежей: " + soapError);
                return;
            }
        } catch (Exception e) {
            return;
        }

        info("Разбор новых платежей для контрагента %s..", contragent.getContragentName());
        // Если платежи есть, то обрабатываем их
        List<Map<String, String>> payments = null;
        try {
            payments = parsePayments(response);
        } catch (Exception e) {
            logger.error(
                    String.format("Не удалось разобрать платежи для контрагента %s", contragent.getContragentName()), e);
            return;
        }
        info("Получено %s новых платежей для контрагента %s, применение..", payments.size(), contragent.getContragentName());
        //  И записываем в БД
        addPaymentsToDb(payments);


        //  Обновляем дату последней загрузки платежей
        setLastUpdateDate(contragent, updateTime);
        info("Все новые платежи для контрагента %s обработаны", contragent.getContragentName());
    }


    public SOAPMessage executeRequest(Date updateTime, int requestType, Contragent contragent) throws Exception {
        String fileName;
        if (requestType==REQUEST_MODIFY_CATALOG) {
            fileName = MODIFY_CATALOG_TEMPLATE;
        } else if (requestType==REQUEST_CREATE_CATALOG) {
            fileName = CREATE_CATALOG_TEMPLATE;
        } else if (requestType==REQUEST_LOAD_PAYMENTS) {
            fileName = LOAD_PAYMENTS_TEMPLATE;
        } else {
            throw new Exception("Invalid request type: "+requestType);
        }
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName);
        SOAPMessage out = signRequest(doMacroReplacement(updateTime, new StreamSource(is), contragent), requestType);
        Array.writeFile(RNIP_OUTPUT_FILE, RNIPLoadPaymentsService.messageToString(out).getBytes("UTF-8"));
        SOAPMessage in = send(out);
        Array.writeFile(RNIP_INPUT_FILE, RNIPLoadPaymentsService.messageToString(in).getBytes("UTF-8"));
        return in;
    }
    
    public static String messageToString(SOAPMessage msg) throws Exception {
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        StringWriter writer = new StringWriter();
        Document doc = msg.getSOAPPart().getEnvelope().getOwnerDocument();
        transformer.transform(new DOMSource(doc), new StreamResult(writer));
        return  writer.getBuffer().toString().replaceAll("\n|\r", "");
    }

    PrivateKey privateKey;
    X509Certificate cert;

    public SOAPMessage signRequest(StreamSource requestData, int requestType) throws Exception {
        String elementForSign=null;
        if (requestType==REQUEST_MODIFY_CATALOG) {
            elementForSign = "Changes";
        } else if (requestType==REQUEST_CREATE_CATALOG) {
            elementForSign = "ServiceCatalog";
        }

        if (privateKey==null) {
            /*** Инициализация ***/
            Init.init();

            // Инициализация Transforms алгоритмов.
            com.sun.org.apache.xml.internal.security.Init.init();

            // Инициализация ключевого контейнера.
            String store = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_STORE_NAME);
            KeyStore keyStore = KeyStore.getInstance(store);
            keyStore.load(null, null);

            // Получение ключа и сертификата.
            String alias = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_ALIAS);
            String pass = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_PASSWORD);
            privateKey = (PrivateKey) keyStore.getKey(alias, pass.toCharArray());
            cert = (X509Certificate) keyStore.getCertificate(alias);
        }

        /*** Подготовка документа ***/
        MessageFactory mf = MessageFactory.newInstance();
        SOAPMessage message = mf.createMessage();
        SOAPPart soapPart = message.getSOAPPart();

        // Читаем сообщение из файла.
        //FileInputStream fis = new FileInputStream(fileName);
        soapPart.setContent(requestData);//new StreamSource(fis)));
        message.getSOAPPart().getEnvelope().addNamespaceDeclaration("ds", "http://www.w3.org/2000/09/xmldsig#"); // !!!!!!! Замиенить !!!!!!!!

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
        ////
        // Преобразования над блоком SignedInfo
        List<Transform> transformList = new ArrayList<Transform>();
        Transform transformC14N = fac.newTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS, (XMLStructure) null);
        transformList.add(transformC14N);
        
        
        //// Подпись внутренних элементов
        if (elementForSign!=null) {
            Reference ref2 = fac
                    .newReference("", fac.newDigestMethod("http://www.w3.org/2001/04/xmldsig-more#gostr3411", null),
                            transformList, null, null);
            // Блок SignedInfo.
            SignedInfo si2 = fac.newSignedInfo(
                    fac.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE, (C14NMethodParameterSpec) null),
                    fac.newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411", null),
                    Collections.singletonList(ref2));
            // Блок KeyInfo.
            KeyInfoFactory kif2 = fac.getKeyInfoFactory();
            X509Data x509d2 = kif2.newX509Data(Collections.singletonList(cert));
            KeyInfo ki2 = kif2.newKeyInfo(Collections.singletonList(x509d2));
            // Подпись данных.
            javax.xml.crypto.dsig.XMLSignature sig2 = fac.newXMLSignature(si2, ki2);
    
            NodeList nodeList = doc.getElementsByTagName(elementForSign);
            for (int n=0;n<nodeList.getLength();++n) {
                Element dataEl = (Element)nodeList.item(n);
                DOMSignContext signContext = new DOMSignContext(privateKey, dataEl);
                sig2.sign(signContext);
            }
        }
        
        
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

        //Array.writeFile("C:/1/test.xml.signed.uri.xml", msg.getBytes("utf-8")); // !!!!!!!! ЗАМЕНИТЬ !!!!!!!!

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

        //System.out.println("Verify by: " + cert.getSubjectDN());

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
        //System.out.println("Verified locally: " + signature.validate(valContext));
        return message;
    }


    public SOAPMessage send(SOAPMessage message) throws Exception {
        // Use SAAJ to convert Document to SOAPElement
        SOAPConnectionFactory sfc = SOAPConnectionFactory.newInstance();
        SOAPConnection connection = sfc.createConnection();
        URL_ADDR = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_URL);
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
            //String paymentInfoStr = new String(pi.getPaymentData(), "cp1251");        !!!!!!!!!!! БЫЛО !!!!!!!!!!!!!!
            String paymentInfoStr = new String(pi.getPaymentData(), "utf8");
            /*InputStream stream = new ByteArrayInputStream(paymentInfoStr.getBytes());
            InputSource is = new InputSource(stream);
            is.setEncoding("UTF-8");                                                    !!!!!!!!!!! БЫЛО !!!!!!!!!!!!!!*/
            Document doc = builderFactory.newDocumentBuilder().parse(new InputSource(new StringReader(paymentInfoStr)));
            result.add(parsePayment(doc));
        }
        return result;
    }

    public Map<String, String> parsePayment(Document doc) {
        Map<String, String> vals = new HashMap<String, String>();
        parseNode(doc.getChildNodes(), vals);
        return vals;
    }

    private void parseNode (NodeList nodelist, Map<String, String> vals) {
        for (int i=0; i<nodelist.getLength(); i++) {
            Node node = nodelist.item(i);

            if (node.hasChildNodes() && node.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE) {
                String n = node.getNodeName();
                String v = node.getChildNodes().item(0).getNodeValue();
                for (String param : PAYMENT_PARAMS) {
                    if (param.equals(n)) {
                        vals.put(n, v);
                        break;
                    }
                }
            }
            else {
                if (node.getNodeName().equals("AdditionalData")) {
                    String nameLabel = node.getChildNodes().item(0).getNodeName();
                    String nameValue = node.getChildNodes().item(0).getChildNodes().item(0).getNodeValue();
                    if (nameLabel.equals("Name")) {
                        for (String param : PAYMENT_PARAMS) {
                            if (param.equals (nameValue)) {
                                String valName = node.getChildNodes().item(1).getNodeName();
                                String valValue = node.getChildNodes().item(1).getChildNodes().item(0).getNodeValue();
                                vals.put(nameValue, valValue);
                                break;
                            }
                        }
                    }
                }

            parseNode (node.getChildNodes(), vals);
            }
        }
    }


    public void addPaymentsToDb(List<Map<String, String>> payments) throws Exception {
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
            String paymentID = "";
            paymentID             = p.get("SystemIdentifier").trim();//SupplierBillID
            String paymentDate    = p.get("PaymentDate").trim();
            String contragentKey  = p.get("Srv_Code").substring(5, 10).trim();
            String bic            = p.get("BIK");
            String amount         = p.get("Amount");
            long idOfContragent  = getContragentByRNIPCode(contragentKey, contrgents);
            if (idOfContragent == 0) {
                continue;
            }
            String contractId = p.get("NUM_DOGOVOR");
            Client client = DAOUtils.findClientByContractId(session, Long.parseLong(contractId));
            info("Обработка платежа: SystemIdentifier=%s, PaymentDate=%s, Srv_Code=%s, BIK=%s, NUM_DOGOVOR=%s, Amount=%s ..",
                    paymentID, paymentDate, contragentKey, bic, contractId, amount);
            if (client == null) {
                throw new Exception ("Клиент с номером контракта " + p.get("NUM_DOGOVOR") + " не найден");
            }
            Long idOfPaymentContragent = null;
            Contragent payContragent = DAOService.getInstance().getContragentByBIC(bic);
            if (payContragent != null) {
                idOfPaymentContragent = payContragent.getIdOfContragent();
            }
            else {
                logger.error("По полученному БИК " + bic + " от РНИП, не найдено ни одного контрагента");
                Contragent rnipContragent = DAOService.getInstance().getRNIPContragent();
                if (rnipContragent != null) {
                    idOfContragent = rnipContragent.getIdOfContragent();
                }
            }
            long amt = Long.parseLong(amount);
            OnlinePaymentProcessor.PayRequest req = new OnlinePaymentProcessor.PayRequest(
                    OnlinePaymentProcessor.PayRequest.V_0, false, idOfPaymentContragent, idOfContragent,
                    ClientPayment.ATM_PAYMENT_METHOD,
                    Long.parseLong(p.get("NUM_DOGOVOR")), /* должен использоваться idofclient, но в OnlinePaymentProcessor, перепутаны местами два аргумента,
                                                            поэтому используется Long.parseLong(p.get("NUM_DOGOVOR")) */
                    paymentID, paymentDate + "/" + paymentID, amt,
                    false);
            OnlinePaymentProcessor.PayResponse resp = runtimeContext.getOnlinePaymentProcessor()
                    .processPayRequest(req);
            info("Платеж SystemIdentifier=%s обработан", paymentID);
            /*logger.info(String.format("Request (%s) processed: %s", req == null ? "null" : req.toString(),
                    resp == null ? "null" : resp.toString()));*/
        }
    }


    public long getContragentByRNIPCode(String contragentKey, List<Contragent> contragents) {
        for (Contragent c : contragents) {
            String cc = getRNIPIdFromRemarks (c.getRemarks());
            if (cc != null && cc.equals(contragentKey)) {
                return c.getIdOfContragent();
            }
        }
        return 0L;
    }


    public StreamSource doMacroReplacement(Date updateTime, StreamSource ss, Contragent contragent) throws Exception {
        InputStream is = ss.getInputStream();
        byte[] data = new byte[is.available()];
        is.read(data);

        String content = new String(data, "UTF-8");
        if (content.indexOf("%START_DATE%") > 1) {
            String str = new SimpleDateFormat(RNIP_DATE_TIME_FORMAT).format(getLastUpdateDate(contragent));
            //String str = new SimpleDateFormat(RNIP_DATE_FORMAT).format(new Date(System.currentTimeMillis() - 986400000));
            content = content.replaceAll("%START_DATE%", formatString(str.trim()));
        }
        if (content.indexOf("%END_DATE%") > 1) {
            String str = new SimpleDateFormat(RNIP_DATE_TIME_FORMAT).format(new Date(System.currentTimeMillis()));
            //String str = new SimpleDateFormat(RNIP_DATE_FORMAT).format(new Date(System.currentTimeMillis() + 986400000));
            content = content.replaceAll("%END_DATE%", formatString(str.trim()));
        }
        if (content.indexOf("%CONTRAGENT_ID%") > 1) {
            String id = getRNIPIdFromRemarks(contragent.getRemarks());
            content = content.replaceAll("%CONTRAGENT_ID%", formatString(id == null ? "" : id));
        }
        if (content.indexOf("%FINANCE_PROVIDER%") > 1) {
            content = content.replaceAll("%FINANCE_PROVIDER%", formatString(contragent.getBank()));
        }
        if (content.indexOf("%CONTRAGENT_NAME%") > 1) {
            content = content.replaceAll("%CONTRAGENT_NAME%", formatString(contragent.getContragentName()));
        }
        if (content.indexOf("%FINANCE_ACCOUNT%") > 1) {
            content = content.replaceAll("%FINANCE_ACCOUNT%", formatString(contragent.getAccount()));
        }
        if (content.indexOf("%KORR_FINANCE_ACCOUNT%") > 1) {
            content = content.replaceAll("%KORR_FINANCE_ACCOUNT%", formatString(contragent.getCorrAccount()));
        }
        if (content.indexOf("%KBK%") > 1) {
            content = content.replaceAll("%KBK%", "00000000000000000000");
        }
        if (content.indexOf("%INN%") > 1) {
            content = content.replaceAll("%INN%", formatString(contragent.getInn()));
        }
        if (content.indexOf("%KPP%") > 1) {
            content = content.replaceAll("%KPP%", formatString(contragent.getKpp()));
        }
        if (content.indexOf("%OKATO%") > 1) {
            content = content.replaceAll("%OKATO%", formatString(contragent.getOkato()));
        }
        if (content.indexOf("%OGRN%") > 1) {
            content = content.replaceAll("%OGRN%", formatString(contragent.getOgrn()));
        }
        if (content.indexOf("%BIK%") > 1) {
            content = content.replaceAll("%BIK%", formatString(contragent.getBic()));
        }
        if (content.indexOf("%COMISSION_PERCENTS%") > 1) {
            String comissionStr = getRNIPComissionFromRemarks(contragent.getRemarks());
            double comission = 0D;
            try {
                comission = Double.parseDouble(comissionStr);
            } catch (Exception e) {
                comission = 0D;
            }
            String cStr = new BigDecimal(comission).setScale(1, BigDecimal.ROUND_HALF_DOWN).toString();
            content = content.replaceAll("%COMISSION_PERCENTS%", formatString(cStr.trim()));
        }
        if(content.indexOf("%CURRENT_DATE%") > 1) {
            String str = new SimpleDateFormat(RNIP_DATE_FORMAT).format(new Date(System.currentTimeMillis()));
            content = content.replaceAll("%CURRENT_DATE%", formatString(str));
        }
        if(content.indexOf("%CURRENT_DATE_TIME%") > 1) {
            String str = new SimpleDateFormat(RNIP_DATE_TIME_FORMAT).format(new Date(System.currentTimeMillis()));
            content = content.replaceAll("%CURRENT_DATE_TIME%", formatString(str));
        }


        StreamSource res = new StreamSource();
        res.setReader(new StringReader(content));
        return res;
    }

    public String formatString(String str) {
        try {
            return StringEscapeUtils.escapeXml(str);//URLEncoder.encode(str, "UTF-8");
        } catch (Exception e) {
            return str;
        }
    }


    public String checkError (SOAPMessage response) throws Exception {
        if (response == null) {
            return null;
        }

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);

        JAXBContext jc = JAXBContext.newInstance(UnifoTransferMsg.class);
        Unmarshaller u = jc.createUnmarshaller();
        Object o = u.unmarshal(response.getSOAPBody().getFirstChild());

        UnifoTransferMsg m = (UnifoTransferMsg) o;
        jc = JAXBContext.newInstance(PaymentInfoType.class);
        u = jc.createUnmarshaller();
        try {
            ErrInfo e = m.getMessageData().getAppData().getImportDataResponse().getTicket().getRequestProcessResult();
            if (e == null || e.getErrorCode() == null || e.getErrorCode().length() < 1) {
                return null;
            }

            return "[" + e.getErrorCode() + "] " + e.getErrorDescription();
        } catch (Exception e) {
            return null;
        }
    }


    public static final String getRNIPComissionFromRemarks (String remark) {
        String comission = null;
        if (remark.indexOf("{RNIP_Commission=") > -1) {
            comission = remark.substring(remark.indexOf("{RNIP_Commission=") + "{RNIP_Commission=".length(),
                    remark.indexOf("}", remark.indexOf("{RNIP_Commission=") + "{RNIP_Commission=".length()));
        }
        if (comission == null || comission.length() < 1) {
            return null;
        }
        return comission;
    }


    public static final String getRNIPIdFromRemarks (String remark) {
        String RNIPIdOfContragent = null;
        if (remark != null && remark.length() > 0 && remark.indexOf("{RNIP=") > -1) {
            RNIPIdOfContragent = remark.substring(remark.indexOf("{RNIP=") + "{RNIP=".length(),
                    remark.indexOf("}", remark.indexOf("{RNIP=") + "{RNIP=".length()));
        }
        if (RNIPIdOfContragent == null || RNIPIdOfContragent.length() < 1) {
            return null;
        }
        return RNIPIdOfContragent;
    }


    public static final String getRNIPIdFromRemarks (Session session, Long idOfContragent) {
        Contragent contragent = (Contragent) session.load(Contragent.class, idOfContragent);
        return getRNIPIdFromRemarks (contragent.getRemarks());
    }
}
