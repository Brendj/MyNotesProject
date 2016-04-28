package xades;

import xades.config.IXAdESConfig;
import xades.provider.GostTimeStampTokenProvider;
import xades.util.GostXAdESUtility;
import xades4j.UnsupportedAlgorithmException;
import xades4j.algorithms.Algorithm;
import xades4j.algorithms.EnvelopedSignatureTransform;
import xades4j.algorithms.ExclusiveCanonicalXMLWithoutComments;
import xades4j.algorithms.GenericAlgorithm;
import xades4j.production.*;
import xades4j.properties.DataObjectDesc;
import xades4j.providers.KeyingDataProvider;
import xades4j.providers.impl.DefaultAlgorithmsProviderEx;
import xades4j.providers.impl.DefaultMessageDigestProvider;
import xades4j.providers.impl.DirectKeyingDataProvider;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Map;

public class Signer {

    /**
     * Создание и проверка подписи формата XAdES
     * со штампом времени.
     *
     * @param xAdESConfig Конфигурация контейнера.
     * @param sourceXmlBin Исходный подписываемый документ.
     * @param signingId Подписываемый узел.
     * @param digest2TsaUrlMap Список пар "oid_алгоритма_хеширования=
     * адрес_tsp_службы". Задает список соответствий между алгоритмом
     * хеширования и адресом TSP службы, чтобы разнообразить и расширить
     * пример.
     * online-проверку цепочки сертификатов.
     * @return документ с подписью.
     * @throws Exception
     */
    public static Document sign(IXAdESConfig xAdESConfig,
                                InputStream sourceXmlBin, String signingId, Map<String, String>
            digest2TsaUrlMap) throws Exception {
        // 2. Ключ подписи и сертификат.

        final KeyStore keyStore = KeyStore.getInstance(
            xAdESConfig.getKeyStoreType(), xAdESConfig.getDefaultProvider());

        keyStore.load(null, null);

        // Ключ подписи.
        final PrivateKey privateKey = (PrivateKey) keyStore.getKey(
            xAdESConfig.getSignatureContainer().getAlias(),
            xAdESConfig.getSignatureContainer().getPassword());

        // Сертификат для проверки.
        final X509Certificate cert = (X509Certificate) keyStore.getCertificate(
            xAdESConfig.getSignatureContainer().getAlias());

        return sign(privateKey, cert, xAdESConfig.getDefaultProvider(), sourceXmlBin, signingId, digest2TsaUrlMap);
    }

    /**
     * Создание и проверка подписи формата XAdES
     * со штампом времени.
     *
     * @param privateKey Ключ для подписи
     * @param cert Сертификат для подписи
     * @param providerName Криптопровайдер
     * @param sourceXmlBin Исходный подписываемый документ.
     * @param signingId Подписываемый узел.
     * @param digest2TsaUrlMap Список пар "oid_алгоритма_хеширования=
     * адрес_tsp_службы". Задает список соответствий между алгоритмом
     * хеширования и адресом TSP службы, чтобы разнообразить и расширить
     * пример.
     * online-проверку цепочки сертификатов.
     * @return документ с подписью.
     * @throws Exception
     */
    public static Document sign(PrivateKey privateKey, X509Certificate cert, final String providerName,
                                InputStream sourceXmlBin, String signingId, Map<String, String>
            digest2TsaUrlMap) throws Exception {


        final DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware(true);

        //************************************ Подпись ************************************

        // 1. Документ и узел подписи.

        // Исходный документ.
        final Document sourceDocument = dbFactory.newDocumentBuilder().
            parse(sourceXmlBin);

        final XPathFactory factory = XPathFactory.newInstance();
        final XPath xpath = factory.newXPath();

        // Подписываемый узел (предположительно, FinalPayment с неким Id).
        final XPathExpression expr = xpath.compile(String.format("//*[@Id='%s']", signingId));
        final NodeList nodes = (NodeList) expr.evaluate(sourceDocument, XPathConstants.NODESET);

        if (nodes.getLength() == 0) {
            throw new Exception("Can't find node with id: " + signingId);
        } // if

        final Node nodeToSign = nodes.item(0);
        ((Element)nodeToSign).setIdAttribute("Id", true);
        // final Node sigParent = nodeToSign.getParentNode();
        final String referenceURI = "#" + signingId;

        // 3. Алгоритмы.

        final KeyingDataProvider keyingProvider = new DirectKeyingDataProvider(cert, privateKey);
        final XadesSigningProfile sigProf = new XadesTSigningProfile(keyingProvider)

                // time-stamp provider. Дополнительно задается список соответствий между
                // алгоритмом хеширования и адресом TSP службы, чтобы разнообразить и
                // расширить пример.
                .withTimeStampTokenProvider(new GostTimeStampTokenProvider(
                    digest2TsaUrlMap, providerName))

                // digest provider
                .withDigestEngineProvider(new DefaultMessageDigestProvider() { // digest

                    @Override
                    public MessageDigest getEngine(String digestAlgorithmURI) throws UnsupportedAlgorithmException {
                        String digestAlgOid;
                        if (providerName.equals("GPR")) digestAlgOid = "GOST3410"; // GPR
                        else digestAlgOid = GostXAdESUtility.digestUri2Digest(digestAlgorithmURI); // JCP
                        try {

                            return MessageDigest.getInstance(digestAlgOid);
                        } catch (NoSuchAlgorithmException e) {
                            throw new UnsupportedAlgorithmException(e.getMessage(), digestAlgorithmURI, e);
                        }
                    }

                })

                .withAlgorithmsProviderEx(new DefaultAlgorithmsProviderEx() { // algorithms

                    private String digestUrn = null;

                    @Override
                    public Algorithm getSignatureAlgorithm(String keyAlgorithmName)
                        throws UnsupportedAlgorithmException {

                        digestUrn = GostXAdESUtility.key2DigestUrn(keyAlgorithmName);
                        final String signatureUrn = GostXAdESUtility.key2SignatureUrn(keyAlgorithmName);

                        return new GenericAlgorithm(signatureUrn);
                    }

                    @Override
                    public String getDigestAlgorithmForReferenceProperties() {
                        return digestUrn;
                    }

                    public String getDigestAlgorithmForDataObjsReferences() {
                        return digestUrn;
                    }

                    public String getDigestAlgorithmForTimeStampProperties() {
                        return digestUrn;
                    }

                    @Override
                    public Algorithm getCanonicalizationAlgorithmForSignature() {
                        return new ExclusiveCanonicalXMLWithoutComments();
                    }

                    @Override
                    public Algorithm getCanonicalizationAlgorithmForTimeStampProperties() {
                        return new ExclusiveCanonicalXMLWithoutComments();
                    }

                });

        // 4. Подпись.

        final XadesSigner signer = sigProf.newSigner();

        final DataObjectDesc dataObj = new DataObjectReference(referenceURI);
        dataObj.withTransform(new EnvelopedSignatureTransform());
        // dataObj.withTransform(new ExclusiveCanonicalXMLWithoutComments());

        final SignedDataObjects dataObjects = new SignedDataObjects(dataObj);

        signer.sign(dataObjects, nodeToSign);

        return sourceDocument;
    }

}
