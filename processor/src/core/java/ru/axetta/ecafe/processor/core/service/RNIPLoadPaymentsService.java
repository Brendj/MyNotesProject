/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service;

import com.sun.org.apache.xml.internal.security.Init;
import com.sun.org.apache.xml.internal.security.transforms.Transforms;
import com.sun.org.apache.xpath.internal.XPathAPI;
import generated.rnip.roskazna.smevunifoservice.UnifoTransferMsg;
import generated.rnip.roskazna.xsd.errinfo.ErrInfo;
import generated.rnip.roskazna.xsd.exportpaymentsresponse.ExportPaymentsResponse;
import generated.rnip.roskazna.xsd.paymentinfo.PaymentInfoType;
import ru.CryptoPro.JCP.tools.Array;

import ru.axetta.ecafe.processor.core.OnlinePaymentProcessor;
import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.PaymentProcessResult;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.persistence.dao.contragent.ContragentReadOnlyRepository;
import ru.axetta.ecafe.processor.core.persistence.service.contragent.ContragentService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.ws.security.message.WSSecHeader;
import org.apache.ws.security.message.token.X509Security;
import org.hibernate.Session;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import javax.xml.crypto.KeySelector;
import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.soap.*;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.math.BigDecimal;
import java.net.URL;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.Security;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Primary
@Component("RNIPLoadPaymentsService")
@Scope("singleton")
public class RNIPLoadPaymentsService {

    /**
     * Файл с документом для подписи.
     */
    protected final static String RNIP_DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss";
    protected final static String RNIP_DATE_FORMAT = "yyyy-MM-dd";
    //private final static String RNIP_DATE_IMPORT_RESTRICT_FORMAT = "yyyy-MM-dd'T'";
    private final static String LOAD_PAYMENTS_TEMPLATE = "META-INF/rnip/getPayments_byDate.xml";
    public final static String CREATE_CATALOG_TEMPLATE = "META-INF/rnip/createCatalog.xml";
    private final static String MODIFY_CATALOG_TEMPLATE = "META-INF/rnip/modifyCatalog.xml";

    //public final static String CREATE_CATALOG_TEMPLATE_V116 = "META-INF/rnip/createCatalog_v116.xml";
    //private final static String MODIFY_CATALOG_TEMPLATE_V116 = "META-INF/rnip/modifyCatalog_v116.xml";
    ////
    public static final int REQUEST_CREATE_CATALOG=0, REQUEST_MODIFY_CATALOG=1,
            REQUEST_LOAD_PAYMENTS=2, REQUEST_LOAD_PAYMENTS_MODIFIED=3;
    ////
    private String URL_ADDR = null;//"http://193.47.154.2:7003/UnifoSecProxy_WAR/SmevUnifoService";
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(RNIPLoadPaymentsService.class);
    private ThreadLocal<DateFormat> dateFormat = new ThreadLocal<DateFormat>() {
        @Override protected DateFormat initialValue() { return new SimpleDateFormat("dd.MM.yyyy HH:mm:ss"); }
    };
    public static final String RNIP_INPUT_FILE = "/rnip.in.signed";
    public static final String RNIP_OUTPUT_FILE = "/rnip.out.signed";
    public static final String ERRORS_OUTPUT_FILE = "/rnip.errors";
    public static final String RNIP_DIR = "/rnip/";
    public static final String SERVICE_NAME = "РНИП";

    private static final String CHANGE_STATUS_NEW = "1";    //Поле ChangeStatus в ответе РНИП. 1 - новый платеж;
    private static final String CHANGE_STATUS_CHANGE = "2";                                  //2 - корректировка
    private static final String CHANGE_STATUS_CANCEL = "3";                                  //3 - аннулирование
    //Константы, соответствующие данным пакета РНИП
    protected static final String SYSTEM_IDENTIFIER_KEY = "SystemIdentifier";
    protected static final String AMOUNT_KEY = "Amount";
    protected static final String PAYMENT_DATE_KEY = "PaymentDate";
    protected static final String PAYMENT_TO_KEY = "PAYMENT_TO";
    protected static final String SRV_CODE_KEY = "SRV_CODE";
    protected static final String BIK_KEY = "BIK";
    protected static final String CHANGE_STATUS_KEY = "ChangeStatus";

    public long paymentRunTotalIterator = 0;
    public int valueToRunModifiedPayments = 60; //запуск запроса на корректировочные и аннулированные платежи - каждый 60-й запуск в импорте

    public static List<String> PAYMENT_PARAMS = new ArrayList<String>();

    static {
        PAYMENT_PARAMS.add(SYSTEM_IDENTIFIER_KEY);     // Идентификатор платежа в РНИП (уникаклен)
        PAYMENT_PARAMS.add(AMOUNT_KEY);               //  Сумма платежа
        PAYMENT_PARAMS.add(PAYMENT_DATE_KEY);          // Дата платежа
        PAYMENT_PARAMS.add(PAYMENT_TO_KEY);           // Это номер договора в нашей БД
        PAYMENT_PARAMS.add(SRV_CODE_KEY);             // Здесь содержится идентификатор контрагента
        PAYMENT_PARAMS.add(BIK_KEY);                  // БИК банка
        PAYMENT_PARAMS.add(CHANGE_STATUS_KEY);         // если ChangeStatus=1 (изменение) - ищем платеж, регистрируем корректировку
    }

    public static RNIPLoadPaymentsService getInstance() {
        return RuntimeContext.getAppContext().getBean("RNIPLoadPaymentsService", RNIPLoadPaymentsService.class);
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
        boolean isOn = RuntimeContext.getInstance().getOptionValueBool(Option.OPTION_IMPORT_RNIP_PAYMENTS_ON);
        if(!isOn) {
            return false;
        }


        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String instance = runtimeContext.getNodeName();
        String reqInstance = runtimeContext.getOptionValueString(Option.OPTION_IMPORT_RNIP_PROCESSOR_INSTANCE);
        if (StringUtils.isBlank(instance) || StringUtils.isBlank(reqInstance) || !instance.trim()
                .equals(reqInstance.trim())) {
            return false;
        }
        return true;
    }


    public static void setOn(boolean on) {
        RuntimeContext.getInstance()
                .setOptionValueWithSave(Option.OPTION_IMPORT_RNIP_PAYMENTS_ON, "" + (on ? "1" : "0"));
    }

    protected Date getLastUpdateDate(int requestType, Contragent contragent) {
        try {
            info("Получение даты последней выгрузки для контрагента %s..", contragent.getContragentName());
            String d;
            switch (requestType) {
                case REQUEST_LOAD_PAYMENTS_MODIFIED :
                    d = contragent.getContragentSync().getLastModifiesUpdate();
                    break;
                case REQUEST_LOAD_PAYMENTS :
                case REQUEST_CREATE_CATALOG :
                case REQUEST_MODIFY_CATALOG :
                default :
                    d = contragent.getContragentSync().getLastRNIPUpdate();
            }
            //String d = contragent.getContragentSync().getLastRNIPUpdate();
            if(d == null || StringUtils.isBlank(d)) {
                return new Date(0);
            }
            if (d == null || d.length() < 1) {
                info("Для контрагента %s загрузок не было, используется 0 мс.", contragent.getContragentName());
                return new Date(0);
            }
            info("Последняя дата выгрузки для контрагента %s состоялась %s. Версия записи - %s", contragent.getContragentName(),
                    d, contragent.getContragentSync().getVersion());
            DateFormat safeDateFormat = dateFormat.get();
            return safeDateFormat.parse(d);
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
        Object response = null;
        try {
            Date lastUpdateDate = getLastUpdateDate(REQUEST_CREATE_CATALOG, contragent);
            response = executeRequest(new Date(System.currentTimeMillis()), REQUEST_CREATE_CATALOG, contragent, lastUpdateDate);
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
        info("Каталог для контрагента %s добавлен в очередь обработки РНиП", contragent.getContragentName());
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
        Object response = null;
        try {
            Date lastUpdateDate = getLastUpdateDate(REQUEST_MODIFY_CATALOG, contragent);
            response = executeRequest(new Date(System.currentTimeMillis()), REQUEST_MODIFY_CATALOG, contragent, lastUpdateDate);
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
        info("Каталог для контрагента %s добавлен в очередь обработки РНиП", contragent.getContragentName());
    }

    public void runRequests() {
        run(null, null);
    }

    public void run(Date startDate, Date endDate) {
        if (!isOn()) {
            return;
        }
        boolean isAutoRun = (startDate == null);
        paymentRunTotalIterator++;
        long l = System.currentTimeMillis();
        info("Загрузка платежей РНИП..");
        SecurityJournalProcess process = SecurityJournalProcess.createJournalRecordStart(
                SecurityJournalProcess.EventType.RNIP, new Date());
        process.saveWithSuccess(true);
        boolean isSuccessEnd = true;
        Boolean resultReceivePayments;
        RNIPVersion versionRNIP = RNIPVersion.getType(RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_WORKING_VERSION));
        RNIPLoadPaymentsService rnipLoadPaymentsService = getRNIPServiceBean(); //RuntimeContext.getAppContext().getBean(RNIPLoadPaymentsService.class);
        for (Contragent contragent : ContragentReadOnlyRepository.getInstance().getContragentsList()) {
            try {
                resultReceivePayments = rnipLoadPaymentsService
                        .receiveContragentPayments(REQUEST_LOAD_PAYMENTS, contragent, startDate, endDate, 1);
                isSuccessEnd = isSuccessEnd && resultReceivePayments;
                if ((paymentRunTotalIterator % valueToRunModifiedPayments == 0 || !isAutoRun) && versionRNIP.equals(RNIPVersion.RNIP_V116)) {
                    rnipLoadPaymentsService
                            .receiveContragentPayments(REQUEST_LOAD_PAYMENTS_MODIFIED, contragent, startDate,
                                    endDate, 1);
                }
            } catch (Exception e) {
                isSuccessEnd = false;
                logger.error("Failed to receive or proceed payments", e);
            }
        }
        if (this instanceof RNIPLoadPaymentsServiceV21) isSuccessEnd = true;
        SecurityJournalProcess processEnd = SecurityJournalProcess.createJournalRecordEnd(
                SecurityJournalProcess.EventType.RNIP, new Date());
        processEnd.saveWithSuccess(isSuccessEnd);
        l = System.currentTimeMillis() - l;
        if(l > 50000){
            logger.warn("RNIPLoadPaymentsService time:" + l);
        }
        info("Загрузка платежей РНИП завершена");
    }

    //returns true если не было ошибок, иначе false
    public Boolean receiveContragentPayments(int requestType, Contragent contragent, Date startDate, Date endDate, int paging) throws Exception{
        //  Получаем id контрагента в системе РНИП - он будет использоваться при отправке запроса
        String RNIPIdOfContragent = getRNIPIdFromRemarks(contragent.getRemarks());
        if (RNIPIdOfContragent == null || RNIPIdOfContragent.length() < 1) {
            return true; //ошибки нет, у контрагента нет ремарки рнипа
        }
        Date lastUpdateDate = getLastUpdateDate(requestType, contragent);

        info("Попытка получения платежей для контрагента %s", contragent.getContragentName());
        //  Отправка запроса на получение платежей
        Object response = null;
        try {
            response = executeRequest(requestType, contragent, lastUpdateDate, startDate, endDate, 1);
        } catch (Exception e) {
            logger.error("Failed to request data from RNIP service", e);
        }

        if (response == null) {
            return false;
        }

        info("Ответ на получение платежей для контрагента %s получен, разбор..", contragent.getContragentName());
        try {
            String soapError = checkError(response);
            if (soapError != null) {
                logger.error("Произошла ошибка при запросе в РНИП на получение платежей: " + soapError);
                return false;
            }
        } catch (Exception e) {
            return false;
        }

        info("Разбор новых платежей для контрагента %s..", contragent.getContragentName());
        // Если платежи есть, то обрабатываем их
        RNIPPaymentsResponse res = null;
        try {
            res = parsePayments(response);
        } catch (Exception e) {
            logger.error(
                    String.format("Не удалось разобрать платежи для контрагента %s", contragent.getContragentName()), e);
            return false;
        }
        info("Получено %s новых платежей для контрагента %s, применение..", res.getPayments().size(), contragent.getContragentName());
        //  И записываем в БД
        boolean isAutoRun = (startDate == null);
        addPaymentsToDb(res.getPayments(), isAutoRun);

        //Сохранение по новому даты-времени lastRnipUpdate для контрагента в БД
        //Если это автоматический запуск, то меняем дату последнего получения платежей контрагента
        //А если это ручной запуск за выбранный период времени, то дату последнего получения платежей не трогаем
        if (isAutoRun) {
            saveEndDate(requestType, contragent, lastUpdateDate, res.getRnipDate());
        }
        //Сохранили

        info("Все новые платежи для контрагента %s обработаны", contragent.getContragentName());
        return true;
    }

    protected void saveEndDate(int requestType, Contragent contragent, Date lastUpdateDate, Date dateFromRnip) {
        Date edate = getEndDateByStartDate(getStartDateByLastUpdateDate(lastUpdateDate));
        if (dateFromRnip != null && dateFromRnip.before(edate)) {
            edate = dateFromRnip;
        }
        if (lastUpdateDate.before(edate)) {
            info("Сохраняем новую дату последней загрузки платежей контрагента %s - %s. Версия записи перед сохранением - %s. " +
                    "lastUpdateDate = %s, rnipDate = %s",
                    contragent.getContragentName(),
                    CalendarUtils.dateTimeToString(edate),
                    contragent.getContragentSync().getVersion(),
                    CalendarUtils.dateTimeToString(lastUpdateDate),
                    CalendarUtils.dateTimeToString(dateFromRnip));
            switch (requestType) {
                case REQUEST_LOAD_PAYMENTS_MODIFIED :
                    ContragentService.getInstance().setLastModifiesUpdate(contragent, edate);
                    break;
                case REQUEST_LOAD_PAYMENTS :
                default :
                    ContragentService.getInstance().setLastRNIPUpdate(contragent, edate);
            }
        }
    }

    private String getTemplateFileName(int requestType) throws Exception {
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
        RNIPVersion version = RNIPVersion.getType(RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_WORKING_VERSION));
        switch (version) {
            case RNIP_V115:
                break;
            case RNIP_V116:
                fileName = fileName.replace(".xml", "_v116.xml");
                break;
        }
        return fileName;
    }

    public String getRNIPUrl() {
        return RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_URL);
    }


    public Object executeRequest(Date updateTime, int requestType, Contragent contragent, Date updateDate) throws Exception {
        return executeRequest(requestType, contragent, updateDate, null, null, 1);
    }

    public Object executeRequest(int requestType, Contragent contragent, Date updateDate, Date startDate, Date endDate, int paging) throws Exception {
        String fileName = getTemplateFileName(requestType);
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName);
        SOAPMessage out = signRequest(
                doMacroReplacement(new StreamSource(is), contragent, updateDate, startDate, endDate, requestType), requestType);
        long timestamp = System.currentTimeMillis();

        File dir = new File(RNIP_DIR);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        String paymentDirPath = RNIP_DIR + contragent.getContragentName() + "_"+ CalendarUtils.formatToDateShortUnderscoreFormat(new Date()) + "/";
        paymentDirPath = paymentDirPath.replace(" ","_");
        paymentDirPath = paymentDirPath.replace("\"","_");
        File paymentDir = new File(paymentDirPath);
        if(!paymentDir.exists()) {
            paymentDir.mkdirs();
        }
        Array.writeFile(paymentDirPath + RNIP_OUTPUT_FILE + "_" + CalendarUtils.formatTimeUnderscoreToString(timestamp) + ".xml", RNIPLoadPaymentsService.messageToString(out).getBytes("UTF-8"));
        SOAPMessage in = send(out);
        Array.writeFile(paymentDirPath + RNIP_INPUT_FILE + "_" + CalendarUtils.formatTimeUnderscoreToString(timestamp) + ".xml", RNIPLoadPaymentsService.messageToString(in).getBytes("UTF-8"));
        return in;
    }

    public static RNIPLoadPaymentsService getRNIPServiceBean() {
        return RuntimeContext.getAppContext().getBean("RNIPLoadPaymentsServiceV22", RNIPLoadPaymentsServiceV22.class);
        /*RNIPVersion version = RNIPVersion.getType(RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_WORKING_VERSION));
        switch (version) {
            case RNIP_V115:
                return RuntimeContext.getAppContext().getBean("RNIPLoadPaymentsService", RNIPLoadPaymentsService.class);
            case RNIP_V116:
                return RuntimeContext.getAppContext().getBean("RNIPLoadPaymentsServiceV116", RNIPLoadPaymentsServiceV116.class);
            case RNIP_V21:
                return RuntimeContext.getAppContext().getBean("RNIPLoadPaymentsServiceV21", RNIPLoadPaymentsServiceV21.class);
            case RNIP_V22:
                return RuntimeContext.getAppContext().getBean("RNIPLoadPaymentsServiceV22", RNIPLoadPaymentsServiceV22.class);
            case RNIP_V24:
                return RuntimeContext.getAppContext().getBean("RNIPLoadPaymentsServiceV24", RNIPLoadPaymentsServiceV24.class);
        }
        return null;*/
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
            Security.insertProviderAt(new ru.CryptoPro.JCP.JCP(), 1);
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
        //URL_ADDR = RuntimeContext.getInstance().getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_URL);
        URL_ADDR = getRNIPUrl();
        URL endpoint = new URL(URL_ADDR);
        SOAPMessage response = connection.call(message, endpoint);
        connection.close();
        connection = null;
        return response;
    }


    protected Date getRnipDate(XMLGregorianCalendar rnipCal) {
        if(rnipCal == null) {
            return null;
        }
        return rnipCal.toGregorianCalendar().getTime();
    }


    public RNIPPaymentsResponse parsePayments(Object response) throws Exception {
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);

        JAXBContext jc = JAXBContext.newInstance(UnifoTransferMsg.class);
        Unmarshaller u = jc.createUnmarshaller();
        Object o = u.unmarshal(((SOAPMessage)response).getSOAPBody().getFirstChild());

        UnifoTransferMsg m = (UnifoTransferMsg) o;
        jc = JAXBContext.newInstance(PaymentInfoType.class);
        u = jc.createUnmarshaller();


        ExportPaymentsResponse data =
                ((ExportPaymentsResponse) m.getMessageData().getAppData().getExportDataResponse().getResponseTemplate());
        Date rnipDate = getRnipDate(data.getPostBlock().getTimeStamp());
        if (data.getPayments() == null) {
            return new RNIPPaymentsResponse(Collections.EMPTY_LIST, rnipDate);
        }
        List<ExportPaymentsResponse.Payments.PaymentInfo> piList = data.getPayments().getPaymentInfo();
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

        RNIPPaymentsResponse res = new RNIPPaymentsResponse(result, rnipDate);
        return res;
    }

    public Map<String, String> parsePayment(Document doc) {
        Map<String, String> vals = new HashMap<String, String>();
        parseNode(doc.getChildNodes(), vals);
        return vals;
    }

    protected void parseNode (NodeList nodelist, Map<String, String> vals) {
        for (int i=0; i<nodelist.getLength(); i++) {
            Node node = nodelist.item(i);

            if (node.hasChildNodes() && node.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE) {
                String n = node.getNodeName();
                String v = node.getChildNodes().item(0).getNodeValue();
                if(n.equals(BIK_KEY) && vals.containsKey(n)) {
                    break;
                }
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

    protected File getErrorFile() {
        File dir = new File(RNIP_DIR);
        if(!dir.exists()) {
            if(!dir.mkdirs()) {
                return null;
            }
        }
        File f = new File(dir, ERRORS_OUTPUT_FILE + ".log");
        if(!f.exists()) {
            try {
                if(!f.createNewFile()) {
                    return null;
                }
            } catch (Exception e) {
                logger.error("Failed to create file", e);
                return null;
            }
        }
        return f;
    }

    protected FileWriter openErrorFile(File f) {
        if(f == null) {
            return null;
        }
        try {
            FileWriter fw = new FileWriter(f, true);
            return fw;
        } catch (Exception e) {
            logger.error("Failed to init writer for errors file", e);
        }
        return null;
    }

    public void addPaymentsToDb(List<Map<String, String>> payments, Boolean isAutoRun) throws Exception {

        List<Contragent> contragents = DAOReadonlyService.getInstance().getContragentsList();
        FileWriter errorWriter = openErrorFile(getErrorFile());

        for (Map<String, String> p : payments) {
            try {
                processOnePayment(p, errorWriter, contragents, isAutoRun);
            }
            catch (Exception e) {
                //если происходит ошибка при обработке какого-либо платежа, ее кидаем в лог, а оставшиеся платежи обрабатываем как обычно
                if (errorWriter != null) {
                    errorWriter.write(String.format("Ошибка при обработке платежа %s\r\n", e.getMessage()));
                }
                continue;
            }
        }
        if (errorWriter != null) {
            errorWriter.flush();
            errorWriter.close();
        }
    }

    private Map<String, String> copyMap(Map<String, String> map) {
        Map<String, String> resMap = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : map.entrySet())
        {
            resMap.put(entry.getKey(), entry.getValue());
        }
        return resMap;
    }

    private Boolean isPaymentAllowedByDate(Map<String, String> map) {
        //Проверка платежа на ограничение по дате из настроек
        try {
            String pDate = map.get(PAYMENT_DATE_KEY);
            SimpleDateFormat formatter = new SimpleDateFormat(RNIP_DATE_FORMAT);
            Date rnipPaymentDate = formatter.parse(pDate);

            Integer days = RuntimeContext.getInstance().getOptionValueInt(Option.OPTION_DAYS_RESTRICTION_PAYMENT_DATE_IMPORT);
            if (days == null) return true;

            Date currentTime = new Date(System.currentTimeMillis());
            Calendar cal = Calendar.getInstance();
            cal.setTime(currentTime);
            cal.add(Calendar.HOUR, -(24 * days));
            Date redDate = cal.getTime();
            if (rnipPaymentDate.before(redDate)) {
                return false;
            } else {
                return true;
            }
        }
        catch (Exception e) {
            logger.info(String.format("Ошибка при проверке платежа %s на допустимый период по дате (%s)",
                    map.get(SYSTEM_IDENTIFIER_KEY), map.get(PAYMENT_DATE_KEY)));
            return true; //если ошибка, такой платеж обрабатываем дальше
        }
    }

    private void processOnePayment(Map<String, String> p, FileWriter errorWriter, List<Contragent> contragents, Boolean isAutoRun) throws Exception {
        String workDate = new SimpleDateFormat("dd.MM.yyy HH:mm:ss").format(new Date(System.currentTimeMillis()));
        errorWriter.write(workDate + ": -- " + p.toString() + "\r\n");
        if (isAutoRun && !isPaymentAllowedByDate(p)) {
            String message = String.format(
                    "Платеж %s, дата платежа - %s не обработан из-за ограничения по дате платежа в настройках",
                    p.get(SYSTEM_IDENTIFIER_KEY), p.get(PAYMENT_DATE_KEY));
            logger.info(message);
            errorWriter.write(workDate + ": " + message + "\r\n");
            return;
        }
        String paymentID             = p.get(SYSTEM_IDENTIFIER_KEY).trim();

        String changeStatus = p.get(CHANGE_STATUS_KEY);
        if (changeStatus == null || changeStatus.isEmpty() || !(changeStatus.equals(CHANGE_STATUS_NEW)
                || changeStatus.equals(CHANGE_STATUS_CHANGE) || changeStatus.equals(CHANGE_STATUS_CANCEL))) {
            String message = String.format("Неизвестный тип платежа %s", p.get(SYSTEM_IDENTIFIER_KEY));
            logger.info(message);
            errorWriter.write(workDate + ": " + message + "\r\n");
            return;
        }
        Boolean doRegiserPayment = true;
        Map sourceMap = copyMap(p);

        if (changeStatus.equals(CHANGE_STATUS_CHANGE) || changeStatus.equals(CHANGE_STATUS_CANCEL)) {
            doRegiserPayment = false;
            //Если этот платеж - изменение, пробуем зарегистрировать корректировку
            List<ClientPayment> exPayments = DAOReadonlyService.getInstance().findClientPaymentsByPaymentId(null, paymentID);
            if (exPayments != null && !exPayments.isEmpty()) {
                //Найден 1 или более платежей по идентификатору, делаем корректировку
                ClientPayment payment = exPayments.get(0); //берем последний по дате платеж
                Integer corrNumber = getNextCorrIdByPayment(payment)[0];

                doRegiserPayment = cancelPayment(p, payment, errorWriter, contragents);  /// ------!!!!!!--------///
                if (changeStatus.equals(CHANGE_STATUS_CANCEL)) {
                    doRegiserPayment = false;
                }

                if (corrNumber > -1) {
                    //Готовим новый idOfPayment = ид платежа + "/коррекция-<n>"
                    p = sourceMap;
                    String payId = p.get(SYSTEM_IDENTIFIER_KEY) + ClientPayment.CORRECTION_SUBSTRING + corrNumber.toString();
                    p.put(SYSTEM_IDENTIFIER_KEY, payId);
                }
                if (Long.parseLong(p.get(AMOUNT_KEY)) == 0) {
                    doRegiserPayment = false; //не регистрируем корректировку с нулевой суммой
                }
            } else {
                String message = String.format("Невозможно провести корректировку. Не найден платеж с идентификатором %s", p.get(
                        SYSTEM_IDENTIFIER_KEY));
                logger.error(message);
                errorWriter.write(workDate + ": " + message + "\r\n");
            }

        }
        Boolean regPaymentSuccessful = true;
        if (doRegiserPayment) {
            regPaymentSuccessful = registerPayment(p, errorWriter, contragents, false);
        }
        if (!regPaymentSuccessful) {
            logger.info(String.format("Платеж РНИП с SystemIdentifier=%s не сохранен в БД", p.get(PAYMENT_PARAMS.get(0))));
        }
    }

    private Boolean registerPayment(Map<String, String> p, FileWriter errorWriter, List<Contragent> contragents, Boolean allowNegativeSum) throws Exception {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        String workDate = new SimpleDateFormat("dd.MM.yyy HH:mm:ss").format(new Date(System.currentTimeMillis()));
        String paymentID = "";
        if(p.size() < 1) {
            logger.error("Получен пустой платеж от РНИП (без данных) или его данные не удалось обработать");
            return false;
        }
        paymentID             = p.get(SYSTEM_IDENTIFIER_KEY).trim();//SupplierBillID
        String paymentDate    = p.get(PAYMENT_DATE_KEY).trim();
        String contragentKey  = p.get(SRV_CODE_KEY);
        if(contragentKey == null || contragentKey.length() < 1) {
            String str = String.format("Не удалось обработать платеж %s - код контрагента отсутствует", paymentID);
            logger.error(str);
            errorWriter.write(String.format("%s: %s\r\n", workDate, str));
            return false;
        }
        //contragentKey        = contragentKey.substring(5, 10).trim();
        String bic           = p.get(BIK_KEY);
        String amount        = p.get(AMOUNT_KEY);
        long idOfContragent  = getContragentByRNIPCode(contragentKey, contragents);
        if (idOfContragent == 0) {
            String str = String.format("Не удалось обработать платеж %s - не найден контрагент по коду = %s", paymentID, contragentKey);
            logger.error(str);
            errorWriter.write(String.format("%s: %s\r\n", workDate, str));
            return false;
        }
        String contractId = p.get(PAYMENT_TO_KEY);
        if(!StringUtils.isBlank(contractId)){
            contractId = contractId.trim();
            Client client = DAOReadonlyService.getInstance().getClientByContractId(Long.parseLong(contractId));//DAOUtils.findClientByContractId(session, Long.parseLong(contractId));
            info("Обработка платежа: SystemIdentifier=%s, PaymentDate=%s, SRV_CODE=%s, BIK=%s, PAYMENT_TO=%s, Amount=%s ..",
                    paymentID, paymentDate, contragentKey, bic, contractId, amount);
            if (client == null) {
                //throw new Exception ("Клиент с номером контракта " + p.get(PAYMENT_TO_KEY) + " не найден");
                errorWriter.write(String.format("%s: Клиент с номером контракта %s не найден\r\n", workDate, p.get(PAYMENT_TO_KEY)));
                return false;
            }
        }else {
            errorWriter.write(String.format(
                    "%s: поставщик %s идентификатор %s без номера ЛС (отсутствует атрибут PAYMENT_TO)", workDate,
                    contragentKey, paymentID));
            return false;
        }
        Long idOfPaymentContragent = null;
        Contragent payContragent = DAOReadonlyService.getInstance().getContragentByBIC(bic);

        if (payContragent != null) {
            idOfPaymentContragent = payContragent.getIdOfContragent();
        }
        else {
            logger.error("По полученному БИК " + bic + " от РНИП, не найдено ни одного контрагента");
            errorWriter.write(String.format("%s: По полученному БИК %s от РНИП, не найдено ни одного контрагента\r\n", workDate, bic));
            Contragent rnipContragent = DAOReadonlyService.getInstance().getRNIPContragent();
            if (rnipContragent != null) {
                //idOfContragent = rnipContragent.getIdOfContragent();
                idOfPaymentContragent = rnipContragent.getIdOfContragent();
            }
        }
        if(idOfPaymentContragent == null) {
            logger.error(String.format("По БИК %s не найдено контрагента, так же для ИС ПП не указан контрагент по умолчанию "
                    + "(указывается в настройках RNIP_DEFAULT). Платеж не может быть обработан!", bic));
            return false;
        }

        long amt = Long.parseLong(amount);
        OnlinePaymentProcessor.PayRequest req = new OnlinePaymentProcessor.PayRequest(
                OnlinePaymentProcessor.PayRequest.V_0, false, idOfPaymentContragent, idOfContragent,
                ClientPayment.ATM_PAYMENT_METHOD,
                Long.parseLong(contractId), /* должен использоваться idofclient, но в OnlinePaymentProcessor, перепутаны местами два аргумента,
                                                       поэтому используется Long.parseLong(p.get(PAYMENT_TO_KEY)) */
                paymentID, SERVICE_NAME + "/" + paymentDate + "/" + bic, amt,
                allowNegativeSum);
        OnlinePaymentProcessor.PayResponse resp = runtimeContext.getOnlinePaymentProcessor()
                .processPayRequest(req);
        if(resp.getResultCode() == PaymentProcessResult.OK.getCode()) {
            info("Платеж SystemIdentifier=%s обработан. Присвоен id %s", paymentID, resp.getPaymentId());
            return true;
        } else {
            logger.error(String.format("Платеж SystemIdentifier=%s обработан. Присвоен id %s. Произошла ошибка с кодом %s", paymentID, resp.getPaymentId(), resp.getResultCode()));
            return false;
        }
    }

    private Integer[] getNextCorrIdByPayment(ClientPayment payment) {
        String idOfPayment = payment.getIdOfPayment();
        Integer nextNumber;
        int qq = idOfPayment.indexOf(ClientPayment.CORRECTION_SUBSTRING);
        if (qq == -1) {
            nextNumber = 0;
        } else {
            try {
                nextNumber = Integer.parseInt(idOfPayment.substring(qq + ClientPayment.CORRECTION_SUBSTRING.length())) + 1;
            }
            catch (Exception e) {
                nextNumber = 0;
            }
        }
        return new Integer[] {nextNumber, qq};
    }

    private boolean cancelPayment(Map<String, String> map, ClientPayment payment, FileWriter errorWriter, List<Contragent> contragents) throws Exception {
        /*в idOfPayment вносим исходный idOfPayment+"/отмена-0". Если исходный idOfPayment содержит "/коррекция-<n>", добавляем суффикс "-1", то есть для
        idOfPayment=123 -> idOfPayment=123/отмена-0
        для idOfPayment=123/коррекция-1 -> idOfPayment=123/отмена-1
        и т.д.*/
        String workDate = new SimpleDateFormat("dd.MM.yyy HH:mm:ss").format(new Date(System.currentTimeMillis()));
        try {
            Integer[] nextNumbers = getNextCorrIdByPayment(payment);
            Integer nextNumber = nextNumbers[0];
            String idOfPayment = payment.getIdOfPayment();
            String baseIdOfPayment;
            if (nextNumber == 0) {
                baseIdOfPayment = idOfPayment;
            } else {
                baseIdOfPayment = idOfPayment.substring(0, nextNumbers[1]);
            }
            String newIdOfPayment = baseIdOfPayment + ClientPayment.CANCEL_SUBSTRING + nextNumber.toString();
            if (DAOReadonlyService.getInstance().isCancelPaymentExists(newIdOfPayment)) {
                return false;
            }
            Long cancelSum = - payment.getPaySum();
            map.put(AMOUNT_KEY, cancelSum.toString());
            map.put(SYSTEM_IDENTIFIER_KEY, newIdOfPayment);
            return registerPayment(map, errorWriter, contragents, true);

            //logger.info(String.format("Произведена отмена платежа %s", payment.getIdOfPayment()));
        }
        catch (Exception e) {
            String message = String.format("Can't register cancel payment for payment ID=%s", payment.getIdOfPayment());
            logger.error(message, e);
            errorWriter.write(workDate + ": " + message + "\r\n");
            return false;
        }
    }

    private ClientPayment cancelPayment(ClientPayment payment) {
        /*в idOfPayment вносим исходный idOfPayment+"/отмена-0". Если исходный idOfPayment содержит "/коррекция-<n>", добавляем суффикс "-1", то есть для
        idOfPayment=123 -> idOfPayment=123/отмена-0
        для idOfPayment=123/коррекция-1 -> idOfPayment=123/отмена-1
        и т.д.*/
        try {
            Integer[] nextNumbers = getNextCorrIdByPayment(payment);
            Integer nextNumber = nextNumbers[0];
            String idOfPayment = payment.getIdOfPayment();
            String baseIdOfPayment;
            if (nextNumber == 0) {
                baseIdOfPayment = idOfPayment;
            } else {
                baseIdOfPayment = idOfPayment.substring(0, nextNumbers[1]);
            }
            String newIdOfPayment = baseIdOfPayment + ClientPayment.CANCEL_SUBSTRING + nextNumber.toString();
            if (DAOReadonlyService.getInstance().isCancelPaymentExists(newIdOfPayment)) {
                return null;
            }
            Long cancelSum = - payment.getPaySum();

            ClientPayment cPayment = new ClientPayment(payment.getTransaction(), payment.getPaymentMethod(), cancelSum, ClientPayment.CANCELLED_PAYMENT,
                    payment.getCreateTime(), newIdOfPayment, payment.getContragent(), payment.getContragentReceiver(),
                    payment.getAddPaymentMethod(), payment.getAddIdOfPayment());
            logger.info(String.format("Произведена отмена платежа %s", payment.getIdOfPayment()));
            return cPayment;
        }
        catch (Exception e) {
            logger.error(String.format("Can't register cancel payment for payment ID=%s", payment.getIdOfPayment()), e);
            return null;
        }
    }

    public long getContragentByRNIPCode(String contragentKey, List<Contragent> contragents) {
        contragentKey = contragentKey.substring(5, 10).trim();
        for (Contragent c : contragents) {
            String cc = getRNIPIdFromRemarks (c.getRemarks());
            if (cc != null && cc.equals(contragentKey)) {
                return c.getIdOfContragent();
            }
        }
        return 0L;
    }

    protected Date getEndDateByStartDate(Date start_date) {
        Date curtime = new Date(System.currentTimeMillis());
        Calendar cal = Calendar.getInstance();
        cal.setTime(start_date);
        cal.add(Calendar.HOUR_OF_DAY, 6);
        Date time2 = cal.getTime();
        if (curtime.before(time2)) {
            return curtime;
        }
        else {
            return time2;
        }
    }

    protected Date getStartDateByLastUpdateDate(Date updateDate) {
        Date lastUpdateDate = new Date(updateDate.getTime());
        lastUpdateDate = CalendarUtils.addSeconds(lastUpdateDate, -20);
        return lastUpdateDate;
    }

    public StreamSource doMacroReplacement(StreamSource ss, Contragent contragent, Date updateDate, Date startDate, Date endDate, int requestType) throws Exception {
        InputStream is = ss.getInputStream();
        byte[] data = new byte[is.available()];
        is.read(data);

        String content = new String(data, "UTF-8");
        Date start_date = null;
        if (content.indexOf("%START_DATE%") > 1) {
            String str;

            if(startDate == null){
                logger.warn("Auto start time");
                /*Date lastUpdateDate = new Date(updateDate.getTime());
                lastUpdateDate = CalendarUtils.addMinute(lastUpdateDate, -1);*/
                Date lastUpdateDate = getStartDateByLastUpdateDate(updateDate);
                str = new SimpleDateFormat(RNIP_DATE_TIME_FORMAT).format(lastUpdateDate);
            }else {
                logger.warn("Manual start: "+startDate);
                str = new SimpleDateFormat(RNIP_DATE_TIME_FORMAT).format(startDate);
            }
            //String str = new SimpleDateFormat(RNIP_DATE_FORMAT).format(new Date(System.currentTimeMillis() - 986400000));
            content = content.replaceAll("%START_DATE%", formatString(str.trim()));
            DateFormat format = new SimpleDateFormat(RNIP_DATE_TIME_FORMAT);
            start_date = format.parse(str);
        }
        if (content.indexOf("%END_DATE%") > 1) {
            String str;
            if(endDate == null){
                logger.warn("Auto end time");
                if (requestType == REQUEST_LOAD_PAYMENTS) {
                    /*Date curtime = new Date(System.currentTimeMillis());
                    Calendar cal = Calendar.getInstance();
                    cal.setTime(start_date);
                    cal.add(Calendar.HOUR, 6);
                    Date time2 = cal.getTime();
                    if (curtime.before(time2)) {
                        str = new SimpleDateFormat(RNIP_DATE_TIME_FORMAT).format(curtime);
                    }
                    else {
                        str = new SimpleDateFormat(RNIP_DATE_TIME_FORMAT).format(time2);
                    }*/
                    str = new SimpleDateFormat(RNIP_DATE_TIME_FORMAT).format(getEndDateByStartDate(start_date));
                }
                else {
                    str= new SimpleDateFormat(RNIP_DATE_TIME_FORMAT).format(new Date(System.currentTimeMillis()));
                }
            }else {
                logger.warn("Manual end: "+endDate);
                str= new SimpleDateFormat(RNIP_DATE_TIME_FORMAT).format(endDate);
            }
            //String str = new SimpleDateFormat(RNIP_DATE_FORMAT).format(new Date(System.currentTimeMillis() + 986400000));
            content = content.replaceAll("%END_DATE%", formatString(str.trim()));
        }
        if (content.indexOf("%CONTRAGENT_ID%") > 1) {
            String id = getRNIPIdFromRemarks(contragent.getRemarks());
            content = content.replaceAll("%CONTRAGENT_ID%", formatString(id == null ? "" : id));
        }
        if (content.indexOf("%CONTRAGENT_BMID%") > 1) {
            String id = getRNIPBmIdFromRemarks(contragent.getRemarks());
            content = content.replaceAll("%CONTRAGENT_BMID%", formatString(id == null ? "" : id));
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
        if (content.indexOf("%OKTMO%") > 1) {
            content = content.replaceAll("%OKTMO%", formatString(contragent.getOktmo()));
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

    protected String formatString(String str) {
        try {
            return StringEscapeUtils.escapeXml(str);//URLEncoder.encode(str, "UTF-8");
        } catch (Exception e) {
            return str;
        }
    }


    public String checkError (Object response) throws Exception {
        if (response == null) {
            return null;
        }

        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setNamespaceAware(true);

        JAXBContext jc = JAXBContext.newInstance(UnifoTransferMsg.class);
        Unmarshaller u = jc.createUnmarshaller();
        Object o = u.unmarshal(((SOAPMessage)response).getSOAPBody().getFirstChild());

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


    public String getRNIPIdFromRemarks (String remark) {
        return getValueByNameFromRemars(remark, "RNIP");
    }

    public static final String getRNIPBmIdFromRemarks (String remark) {
        return getValueByNameFromRemars(remark, "BMID");
    }

    protected static final String getValueByNameFromRemars(String remark, String name) {
        String val = null;
        if (remark != null && remark.length() > 0 && remark.indexOf("{" + name + "=") > -1) {
            val = remark.substring(remark.indexOf("{" + name + "=") + ("{" + name + "=").length(),
                    remark.indexOf("}", remark.indexOf("{" + name + "=") + ("{" + name + "=").length()));
        }
        if (val == null || val.length() < 1) {
            return null;
        }
        return val;
    }




    public final String getRNIPIdFromRemarks (Session session, Long idOfContragent) {
        Contragent contragent = (Contragent) session.load(Contragent.class, idOfContragent);
        return getRNIPIdFromRemarks(contragent.getRemarks());
    }

    protected static class RNIPPaymentsResponse {
        List<Map<String, String>> payments;
        Date rnipDate;

        protected RNIPPaymentsResponse(List<Map<String, String>> payments, Date rnipDate) {
            this.payments = payments;
            this.rnipDate = rnipDate;
        }

        public List<Map<String, String>> getPayments() {
            return payments;
        }

        public Date getRnipDate() {
            return rnipDate;
        }
    }
}
