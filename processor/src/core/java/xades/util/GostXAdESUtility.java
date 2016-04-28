/**
 * $RCSfileXAdESUtility.java,v $
 * version $Revision: 36379 $
 * created 03.06.2015 16:27 by afevma
 * last modified $Date: 2012-05-30 12:19:27 +0400 (Ср, 30 май 2012) $ by $Author: afevma $
 *
 * Copyright 2004-2015 Crypto-Pro. All rights reserved.
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
package xades.util;


import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.w3c.dom.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * Служебный интерфейс с константами и вспомогательными
 * функциями.
 *
 * @author Copyright 2004-2015 Crypto-Pro. All rights reserved.
 * @.Version
 */
public class GostXAdESUtility implements IXAdESCommon {

    /**
     * Фабрика сертификатов.
     */
    public static final CertificateFactory CERTIFICATE_FACTORY;

    /**
     * Адрес тестового сервиса.
     */
    private final static String GIS_GMP_SERVICE =
        "http://smev-mvf.test.gosuslugi.ru:7777/gateway/services/SID0003663";

    public static final java.lang.String GOST_DIGEST_OID = "1.2.643.2.2.9";
    /**
     * Список пар "oid_алгоритма_хеширования=urn_алгоритма_хеширования".
     */
    public static final Map<String, String> MAP_DIGEST_OID_2_DIGEST_URN =
        new LinkedHashMap<String, String>() {{

        put(GOST_DIGEST_OID, URI_GOST_DIGEST);
        // put(JCP.GOST_DIGEST_2012_256_OID, Consts.URN_GOST_DIGEST_2012_256); // >= JCP 2.0
        // put(JCP.GOST_DIGEST_2012_512_OID, Consts.URN_GOST_DIGEST_2012_512); // >= JCP 2.0

    }};

    /**
     * Список пар "oid_алгоритма_ключа=urn_алгоритма_подписи".
     */
    public static final Map<String, String> MAP_KEY_ALG_2_SIGN_URN =
        new LinkedHashMap<String, String>() {{

            put("1.2.643.2.2.19",   URI_GOST_SIGN); // < JCP 2.0
            put("GOST3410",   URI_GOST_SIGN); // < JCP 2.0
            put("GOST3410DH", URI_GOST_SIGN); // < JCP 2.0
            put("GOST3410EL", URI_GOST_SIGN);
            //put(JCP.GOST_EL_DH_NAME,     Consts.URI_GOST_SIGN);
            // put(JCP.GOST_EL_2012_256_NAME, Consts.URN_GOST_SIGN_2012_256);  // >= JCP 2.0
            // put(JCP.GOST_DH_2012_256_NAME, Consts.URN_GOST_SIGN_2012_256);  // >= JCP 2.0
            // put(JCP.GOST_EL_2012_512_NAME, Consts.URN_GOST_SIGN_2012_512);  // >= JCP 2.0
            // put(JCP.GOST_DH_2012_512_NAME, Consts.URN_GOST_SIGN_2012_512);  // >= JCP 2.0

    }};

    public static final String URI_GOST_DIGEST="http://www.w3.org/2001/04/xmldsig-more#gostr3411";
    public static final String URI_GOST_SIGN="http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411";
    /**
     * Список пар "oid_алгоритма_ключа=urn_алгоритма_хеширования".
     */
    public static final Map<String, String> MAP_KEY_ALG_2_DIGEST_URN =
        new LinkedHashMap<String, String>() {{

            put("1.2.643.2.2.19",   URI_GOST_DIGEST); // < JCP 2.0
            put("GOST3410",   URI_GOST_DIGEST); // < JCP 2.0
            put("GOST3410DH", URI_GOST_DIGEST); // < JCP 2.0
            put("GOST3410EL", URI_GOST_DIGEST);
            //put(JCP.GOST_EL_DH_NAME,     Consts.URI_GOST_DIGEST);
            // put(JCP.GOST_EL_2012_256_NAME, Consts.URN_GOST_DIGEST_2012_256); // >= JCP 2.0
            // put(JCP.GOST_DH_2012_256_NAME, Consts.URN_GOST_DIGEST_2012_256); // >= JCP 2.0
            // put(JCP.GOST_EL_2012_512_NAME, Consts.URN_GOST_DIGEST_2012_512); // >= JCP 2.0
            // put(JCP.GOST_DH_2012_512_NAME, Consts.URN_GOST_DIGEST_2012_512); // >= JCP 2.0

    }};

    /**
     * Список пар "oid_алгоритма_хеширования=url_tsp_службы".
     */
    public static final Map<String, String> MAP_DIGEST_OID_2_TSA_URL =
        new LinkedHashMap<String, String>() {{

            put(GOST_DIGEST_OID, "http://www.cryptopro.ru:80/tsp/");
            // put(JCP.GOST_DIGEST_2012_256_OID, Configuration.TSA_DEFAULT_ADDRESS); // >= JCP 2.0
            // put(JCP.GOST_DIGEST_2012_512_OID, Configuration.TSA_DEFAULT_ADDRESS); // > >= JCP 2.0

    }};

    static {

        try {
            CERTIFICATE_FACTORY = CertificateFactory.getInstance("X.509");
        } catch (CertificateException e) {
            throw new RuntimeException(e);
        }

        // BasicConfigurator.configure(); // для wss4j

        // Инициализация JCP XML провайдера.
        // com.sun.org.apache.xml.internal.security.Init.init(); < JCP 2.0
        /*if(!JCPXMLDSigInit.isInitialized()) {
            JCPXMLDSigInit.init();
        } // if

        // ID resolver, JCPxml < 2.0
        org.apache.xml.security.utils.resolver.ResourceResolver.register(
            new xades.utility.DocumentIdResolver(), true);

        // Загрузка провайдера XML DSig (для wss4j).
        //Security.addProvider(xmlDSigRi);

        // Переопределяем свойства встроенного XML DSig провайдера (для wss4j).
        Security.getProvider("XMLDSig").put("XMLSignatureFactory.DOM",
            "ru.CryptoPro.JCPxml.dsig.internal.dom.DOMXMLSignatureFactory");
        Security.getProvider("XMLDSig").put("KeyInfoFactory.DOM",
            "ru.CryptoPro.JCPxml.dsig.internal.dom.DOMKeyInfoFactory");
*/
    }

    /**
     * Функция поиска oid'а алгоритма хеширования по urn
     * алгоритма хеширования.
     *
     * @param digestUri Urn алгоритма хеширования.
     * @return oid алгоритма.
     */
    public static String digestUri2Digest(ASN1ObjectIdentifier digestUri) {
        return digestUri2Digest(digestUri.getId());
    }

    /**
     * Функция поиска oid'а алгоритма хеширования по urn
     * алгоритма хеширования.
     *
     * @param digestUri Urn алгоритма хеширования.
     * @return oid алгоритма.
     */
    public static String digestUri2Digest(String digestUri) {

        if (MAP_DIGEST_OID_2_DIGEST_URN.containsValue(digestUri)) {

            final Set<Map.Entry<String, String>> entrySet =
                MAP_DIGEST_OID_2_DIGEST_URN.entrySet();

            for (Map.Entry<String, String> entry : entrySet) {
                if (entry.getValue().equals(digestUri)) {
                    return entry.getKey();
                } // if
            } // for

        } // if

        return digestUri;

    }

    /**
     * Функция поиска urn алгоритма подписи по алгоритму ключа.
     *
     * @param keyAlg Алгоритм ключа.
     * @return urn алгоритма.
     */
    public static String key2SignatureUrn(String keyAlg) {

        if (MAP_KEY_ALG_2_SIGN_URN.containsKey(keyAlg)) {
            return MAP_KEY_ALG_2_SIGN_URN.get(keyAlg);
        } // if

        return keyAlg;

    }

    /**
     * Функция поиска urn алгоритма хеширования по алгоритму ключа.
     *
     * @param keyAlg Алгоритм ключа.
     * @return urn алгоритма.
     */
    public static String key2DigestUrn(String keyAlg) {

        if (MAP_KEY_ALG_2_DIGEST_URN.containsKey(keyAlg)) {
            return MAP_KEY_ALG_2_DIGEST_URN.get(keyAlg);
        } // if

        return keyAlg;

    }

    /**
     * Функция поиска адреса TSP службы по oid'у алгоритма хеширования.
     *
     * @param tsaMap Список пар "oid_алгоритма_хеширования=адрес_tsp_службы".
     * @param digestOid Oid алгоритма хеширования.
     * @return адрес TSP службы.
     */
    public static String digestOid2TsaUrl(Map<String, String> tsaMap,
        String digestOid) {

        if (tsaMap.containsKey(digestOid)) {
            return tsaMap.get(digestOid);
        } // if

        return digestOid;

    }

    /**
     * Получение списка промежуточных сертификатов и CRL.
     *
     * @param path Папка с файлами.
     * @param needCrl True, если проверять и грузить CRL.
     * @param storeList Заполняемый список.
     * @throws Exception
     */
    public static void loadIntermediateCertificateAndCrlList(
        String path, boolean needCrl, Collection storeList)
        throws Exception {

        final File rootDirectory = new File(path);
        if (rootDirectory.exists()) {

            final File[] files = rootDirectory.listFiles();
            if (files != null && files.length > 0) {

                for (File file : files) {
                    readObject(file, storeList, needCrl);
                } // for

            } // if
            else {
                readObject(rootDirectory, storeList, needCrl);
            } // else

        } // if

    }

    /**
     * Чтение содержимого сертификата или CRL из файла.
     *
     * @param file Читаемый файл.
     * @param storeList Заполняемый список.
     * @param needCrl True, если проверять и грузить CRL.
     * @throws Exception
     */
    private static void readObject(File file, Collection storeList,
        boolean needCrl) throws Exception {

        final String fileExt = file.getName().substring(file.getName().length()-4);

        if (Arrays.asList(".cer", ".crt", ".der").contains(fileExt)) {

            final X509Certificate cert = (X509Certificate) CERTIFICATE_FACTORY.
                generateCertificate(new FileInputStream(file));

            storeList.add(cert);

        } // if
        else if (needCrl && fileExt.equalsIgnoreCase(".crl")) {

            final X509CRL crl = (X509CRL) CERTIFICATE_FACTORY.
                generateCRL(new FileInputStream(file));

            storeList.add(crl);

        } // else

    }

    /**
     * Чтение хранилища сертификатов.
     *
     * @param certStorePath Путь к хранилищу.
     * @param password Пароль к хранилищу.
     * @return открытое хранилище.
     * @throws Exception
     */
    public static KeyStore loadCertStore(String certStorePath, char[]
        password) throws Exception {

        final FileInputStream inputStream = new FileInputStream(certStorePath);
        final KeyStore ks = KeyStore.getInstance("CertStore");

        ks.load(inputStream, password);
        inputStream.close();

        return ks;

    }

    /**
     * Вывод содержимого документа в файл.
     *
     * @param document Документ.
     * @param filePath Файл для записи.
     * @throws Exception
     */
    public static void logDoc(Document document, String filePath) throws Exception {

        final OutputStream os = new FileOutputStream(filePath);
        final Transformer trans = TransformerFactory.newInstance().newTransformer();
        trans.transform(new DOMSource(document), new StreamResult(os));

    }

}
