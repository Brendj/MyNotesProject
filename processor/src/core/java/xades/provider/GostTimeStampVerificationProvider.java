/**
 * $RCSfileGostTimeStampVerificationProvider.java,v $
 * version $Revision: 36379 $
 * created 03.06.2015 16:25 by afevma
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
package xades.provider;

import xades.util.GostXAdESUtility;
import xades4j.XAdES4jException;
import xades4j.providers.*;

import org.bouncycastle.asn1.ASN1InputStream;
import org.bouncycastle.asn1.cms.ContentInfo;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cms.SignerId;
import org.bouncycastle.cms.jcajce.JcaSimpleSignerInfoVerifierBuilder;
import org.bouncycastle.cms.jcajce.JcaX509CertSelectorConverter;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.tsp.TSPException;
import org.bouncycastle.tsp.TimeStampToken;
import org.bouncycastle.tsp.TimeStampTokenInfo;
import org.bouncycastle.util.Selector;
import org.bouncycastle.util.Store;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Класс для проверки штампа времени.
 *
 * @author Copyright 2004-2015 Crypto-Pro. All rights reserved.
 * @.Version
 */
public class GostTimeStampVerificationProvider implements TimeStampVerificationProvider {

    /**
     * Провайдер для проверки цепочки сертификатов.
     */
    private final CertificateValidationProvider certificateValidationProvider;

    /**
     * Провайдер хеширования.
     */
    private final String messageDigestProvider;

    /**
     * Провайдер проверки подписи штампа времени.
     */
    private final JcaSimpleSignerInfoVerifierBuilder signerInfoVerifierBuilder;

    private final JcaX509CertificateConverter x509CertificateConverter;
    private final JcaX509CertSelectorConverter x509CertSelectorConverter;

    /**
     * Конструктор.
     *
     * @param certificateValidationProvider Провайдер построения и проверки цепочки.
     * @param digestProvider Провайдер хеширования.
     * @throws NoSuchProviderException
     */
    public GostTimeStampVerificationProvider(CertificateValidationProvider
                                                     certificateValidationProvider, String digestProvider) throws NoSuchProviderException {

        this.certificateValidationProvider = certificateValidationProvider;
        this.messageDigestProvider = digestProvider;

        final Provider bcProvider = new BouncyCastleProvider();
        this.signerInfoVerifierBuilder = (new JcaSimpleSignerInfoVerifierBuilder()).setProvider(bcProvider);

        this.x509CertificateConverter = (new JcaX509CertificateConverter()).setProvider(bcProvider);
        this.x509CertSelectorConverter = new JcaX509CertSelectorConverter();

    }

    public Date verifyToken(byte[] timeStampToken, byte[] tsDigestInput)
            throws TimeStampTokenVerificationException {

        TimeStampToken tsToken;
        ASN1InputStream tsaCertStream;

        try {

            tsaCertStream = new ASN1InputStream(timeStampToken);
            final ContentInfo tsTokenInfo = ContentInfo.getInstance(tsaCertStream.readObject());

            tsaCertStream.close();
            tsToken = new TimeStampToken(tsTokenInfo);

        } catch (IOException e) {
            throw new TimeStampTokenStructureException("Error parsing encoded token", e);
        } catch (TSPException e) {
            throw new TimeStampTokenStructureException("Invalid token", e);
        }

        X509Certificate tsaCert;

        try {

            final LinkedList<X509Certificate> tsTokenInfo = new LinkedList<X509Certificate>();
            final Store certStore = tsToken.getCertificates();

            final Iterator certStoreIterator = certStore.getMatches(new AllCertificatesSelector()).iterator();
            while(true) {

                if(!certStoreIterator.hasNext()) {

                    final SignerId signerId = tsToken.getSID();
                    final X509CertSelector selector = x509CertSelectorConverter.getCertSelector(signerId);

                    final ValidationData validationData = certificateValidationProvider.validate(
                            selector, tsToken.getTimeStampInfo().getGenTime(), tsTokenInfo);

                    tsaCert = validationData.getCerts().get(0);
                    break;

                } // if

                final X509CertificateHolder certHolder = (X509CertificateHolder) certStoreIterator.next();
                X509Certificate cert = x509CertificateConverter.getCertificate(certHolder);

                final X509Certificate certImpl = (X509Certificate) CertificateFactory.getInstance("X.509")
                        .generateCertificate(new ByteArrayInputStream(cert.getEncoded()));

                tsTokenInfo.add(certImpl);
            }
        } catch (CertificateException e) {
            throw new TimeStampTokenVerificationException(e.getMessage(), e);
        } catch (XAdES4jException e) {
            throw new TimeStampTokenTSACertException("cannot validate TSA certificate", e);
        }

        /*
            Не работает, т.к. расширение time-stamping в TSP
            сертификате - некритическое.

        try {
            final SignerInformationVerifier signerInformationVerifier = signerInfoVerifierBuilder.build(tsaCert);
            tsToken.validate(signerInformationVerifier);
        } catch (TSPValidationException var8) {
            throw new TimeStampTokenSignatureException("Invalid token signature or certificate", var8);
        } catch (Exception var9) {
            throw new TimeStampTokenVerificationException("Error when verifying the token signature", var9);
        }
        */

        final TimeStampTokenInfo tsTokenInfo = tsToken.getTimeStampInfo();

        try {

            final String digestAlgOid = GostXAdESUtility.
                    digestUri2Digest(tsTokenInfo.getMessageImprintAlgOID());

            final MessageDigest messageDigest = MessageDigest.
                    getInstance(digestAlgOid, messageDigestProvider);

            if(!Arrays.equals(messageDigest.digest(tsDigestInput), tsTokenInfo.getMessageImprintDigest())) {
                throw new TimeStampTokenDigestException();
            } // if

        } catch (NoSuchAlgorithmException e) {
            throw new TimeStampTokenVerificationException("The token\'s digest algorithm is not supported", e);
        } catch (NoSuchProviderException e) {
            throw new TimeStampTokenVerificationException("The token\'s digest algorithm is not supported", e);
        }

        return tsTokenInfo.getGenTime();

    }

    /**
     * Класс селектора, отбирающего все элементы.
     */
    private static class AllCertificatesSelector implements Selector {

        /**
         * Конструктор.
         */
        private AllCertificatesSelector() {
            ;
        }

        @Override
        public boolean match(Object o) {
            return true;
        }

        @Override
        public Object clone() {
            return this;
        }

    }

}
