/**
 * $RCSfileGostPKIXCertificateValidationProvider.java,v $
 * version $Revision: 36379 $
 * created 04.06.2015 11:53 by afevma
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

import xades4j.providers.*;
import xades4j.verification.UnexpectedJCAException;

import javax.security.auth.x500.X500Principal;
import java.security.*;
import java.security.cert.*;
import java.util.*;

/**
 * Служебный класс для реализации провайдера
 * проверки цепочки сертификатов.
 * Для отладки.
 *
 * @author Copyright 2004-2015 Crypto-Pro. All rights reserved.
 * @.Version
 * @see xades4j.providers.impl.PKIXCertificateValidationProvider
 */
public class GostPKIXCertificateValidationProvider implements CertificateValidationProvider {

    private static final String REV_ALGORITHM = "CPPKIX";
    private static final int DEFAULT_MAX_PATH_LENGTH = 6;

    private final KeyStore trustAnchors;
    private final boolean revocationEnabled;
    private final int maxPathLength;
    private final CertStore[] intermediateCertsAndCRLs;
    private final CertPathBuilder certPathBuilder;
    private final String signatureProvider;

    /**
     * Конструктор.
     *
     * @param trustAnchors Список корневых сертификатов.
     * @param revocationEnabled True, если следует выполнить проверку на отзыв.
     * @param certPathBuilderProvider Провайдер построения цепочки.
     * @param signatureProvider Провайдер проверки подписи.
     * @param intermediateCertsAndCRLs Список дополнительных сертификатов и CRL.
     * @throws NoSuchAlgorithmException
     * @throws NoSuchProviderException
     */
    public GostPKIXCertificateValidationProvider(KeyStore trustAnchors,
        boolean revocationEnabled, String certPathBuilderProvider,
        String signatureProvider, CertStore... intermediateCertsAndCRLs)
        throws NoSuchAlgorithmException, NoSuchProviderException {

        if(null == trustAnchors) {
            throw new NullPointerException("Trust anchors cannot be null");
        } // if
        else {

            this.trustAnchors = trustAnchors;
            this.revocationEnabled = revocationEnabled;
            this.maxPathLength = DEFAULT_MAX_PATH_LENGTH;

            this.certPathBuilder = certPathBuilderProvider == null
                ? CertPathBuilder.getInstance(REV_ALGORITHM)
                : CertPathBuilder.getInstance(REV_ALGORITHM, certPathBuilderProvider);

            this.signatureProvider = signatureProvider;
            this.intermediateCertsAndCRLs = intermediateCertsAndCRLs;

        } // else

    }

    @Override
    public ValidationData validate(X509CertSelector certSelector, Date validationDate,
        Collection<X509Certificate> otherCerts) throws CertificateValidationException,
        UnexpectedJCAException {

        PKIXBuilderParameters builderParams;

        try {
            builderParams = new PKIXBuilderParameters(trustAnchors, certSelector);
        } catch (KeyStoreException e) {
            throw new CannotBuildCertificationPathException(certSelector,
                "Trust anchors KeyStore is not initialized", e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new CannotBuildCertificationPathException(certSelector,
                "Trust anchors KeyStore has no trusted certificate entries", e);
        }

        PKIXCertPathBuilderResult builderRes;

        try {

            if(otherCerts != null) {
                final CollectionCertStoreParameters certPath = new CollectionCertStoreParameters(otherCerts);
                final CertStore othersCertStore = CertStore.getInstance("Collection", certPath);
                builderParams.addCertStore(othersCertStore);
            } // if

            int n = 0;

            while (true) {

                if (n >= intermediateCertsAndCRLs.length) {

                    builderParams.setRevocationEnabled(revocationEnabled);
                    builderParams.setMaxPathLength(maxPathLength);
                    builderParams.setDate(validationDate);
                    builderParams.setSigProvider(signatureProvider);

                    builderRes = (PKIXCertPathBuilderResult)certPathBuilder.build(builderParams);
                    break;

                } // if

                builderParams.addCertStore(intermediateCertsAndCRLs[n]);
                ++n;
            } // while

        } catch (CertPathBuilderException e) {
            throw new CannotBuildCertificationPathException(certSelector, e.getMessage(), e);
        } catch (InvalidAlgorithmParameterException e) {
            throw new CannotSelectCertificateException(certSelector, e);
        } catch (NoSuchAlgorithmException e) {
            throw new UnexpectedJCAException("No provider for Collection CertStore", e);
        }

        final List certList = builderRes.getCertPath().getCertificates();
        final ArrayList resultList = new ArrayList(certList);

        resultList.add(builderRes.getTrustAnchor().getTrustedCert());

        return revocationEnabled
            ? new ValidationData(resultList, getCRLsForCertPath(resultList, validationDate))
            : new ValidationData(resultList);

    }

    /**
     * Получение списка CRL для проверки цепочки.
     *
     * @param certPath Цепочка сертификатов.
     * @param validationDate Дата, на момент которой
     * цепочка должна быть валидна.
     * @return список CRL.
     * @throws CertificateValidationException
     */
    private Collection<X509CRL> getCRLsForCertPath(List<X509Certificate> certPath,
        Date validationDate) throws CertificateValidationException {

        final HashMap issuersCerts = new HashMap(certPath.size() - 1);

        for (int crlSelector = 0; crlSelector < certPath.size() - 1; ++crlSelector) {
            issuersCerts.put((certPath.get(crlSelector)).getIssuerX500Principal(),
                certPath.get(crlSelector + 1));
        } // for

        final X509CRLSelector crlSelector = new X509CRLSelector();
        final Iterator crlIterator = issuersCerts.keySet().iterator();

        while (crlIterator.hasNext()) {
            X500Principal crlPrincipal = (X500Principal)crlIterator.next();
            crlSelector.addIssuer(crlPrincipal);
        } // while

        crlSelector.setDateAndTime(validationDate);
        final HashSet set = new HashSet();

        try {

            for (int i = 0; i < intermediateCertsAndCRLs.length; ++i) {
                final Collection crl = intermediateCertsAndCRLs[i].getCRLs(crlSelector);
                set.addAll(Collections.checkedCollection(crl, X509CRL.class));
            } // for

        } catch (CertStoreException e) {
            throw new CertificateValidationException(null, "Cannot get CRLs", e);
        }

        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {

            final X509CRL crl = (X509CRL)iterator.next();

            try {

                final X509Certificate cert = (X509Certificate)issuersCerts.get(crl.getIssuerX500Principal());

                if(null == signatureProvider) {
                    crl.verify(cert.getPublicKey());
                } // if
                else {
                    crl.verify(cert.getPublicKey(), signatureProvider);
                } // else

            } catch (Exception e) {
                throw new CertificateValidationException(null, "Invalid CRL signature from " +
                    crl.getIssuerX500Principal().getName(), e);
            }

        } // while

        return set;
    }

}
