package xades;

import xades.config.IXAdESConfig;
import xades.provider.GostPKIXCertificateValidationProvider;
import xades.provider.GostTimeStampVerificationProvider;
import xades.util.GostXAdESUtility;
import xades4j.UnsupportedAlgorithmException;
import xades4j.providers.CertificateValidationProvider;
import xades4j.providers.impl.DefaultMessageDigestProvider;
import xades4j.verification.XadesVerificationProfile;
import xades4j.verification.XadesVerifier;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.crypto.dsig.XMLSignature;
import java.security.KeyStore;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertStore;
import java.security.cert.CollectionCertStoreParameters;
import java.util.Collection;

public class Verifier {
    /**
     *
     * @param xAdESConfig
     * @param trustStorePath Путь к файлу хранилища корневых сертификатов.
     * @param trustStorePassword Пароль к хранилищу корневых сертификатов.
     * @param intermediateCertsAndCRLs Список дополнительных сертификатов
     * и CRL.
     * @param enableOnlineRevocationCheck True, если следует выполнить
     * @param sourceDocument
     * @param enableOnlineRevocationCheck
     * @return
     * @throws Exception
     */
    public static void verify(IXAdESConfig xAdESConfig, String trustStorePath, char[] trustStorePassword, Collection intermediateCertsAndCRLs, Document sourceDocument, boolean enableOnlineRevocationCheck) throws Exception {
        // Включаем возможность онлайновой проверки.
        System.setProperty("com.sun.security.enableCRLDP", Boolean.toString(enableOnlineRevocationCheck));
        System.setProperty("com.ibm.security.enableCRLDP", Boolean.toString(enableOnlineRevocationCheck));

        //************************************ Проверка ************************************

        // 1. Подписанный документ.

        final Document verifyDocument = sourceDocument;

        // Узел с подписью (предположительно, один).
        final NodeList nl =
            verifyDocument.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");

        if (nl.getLength() == 0) {
            throw new Exception("Cannot find Signature element");
        } // if

        // 2. Сертификаты.

        // Хранилище корневых сертификатов.
        final KeyStore trustStore = GostXAdESUtility.
            loadCertStore(trustStorePath, trustStorePassword);

        final CertStore intermediateCertsAndCRLStore = CertStore.getInstance("Collection",
            new CollectionCertStoreParameters(intermediateCertsAndCRLs));

        // Построение и проверка цепочки. Проверка всегда включена,
        // используем либо online проверку, либо файлы CRL.
        final CertificateValidationProvider validationProvider =
             // для отладки
             new GostPKIXCertificateValidationProvider(trustStore, true, "RevCheck",
              xAdESConfig.getDefaultProvider(), intermediateCertsAndCRLStore);
             //new PKIXCertificateValidationProvider(trustStore, true, intermediateCertsAndCRLStore);

        final XadesVerificationProfile verProf = new XadesVerificationProfile(validationProvider)

                // time-stamp validation
                .withTimeStampTokenVerifier(new GostTimeStampVerificationProvider(
                        validationProvider, xAdESConfig.getDefaultProvider()))

                // digest
                .withDigestEngineProvider(new DefaultMessageDigestProvider() {

                    @Override
                    public MessageDigest getEngine(String digestAlgorithmURI) throws UnsupportedAlgorithmException {

                        final String digestAlgOid = GostXAdESUtility.digestUri2Digest(digestAlgorithmURI);

                        try {
                            return MessageDigest.getInstance(digestAlgOid);
                        } catch (NoSuchAlgorithmException e) {
                            throw new UnsupportedAlgorithmException(e.getMessage(), digestAlgorithmURI, e);
                        }

                    }

                });

        // 3. Проверка подписи.

        final XadesVerifier verifier = verProf.newVerifier();
        final Element signatureElement = (Element) nl.item(0); // предположительно, один узел с подписью

        verifier.verify(signatureElement, null);
        System.out.println("Validation of XAdES-T completed.");
    }
}
