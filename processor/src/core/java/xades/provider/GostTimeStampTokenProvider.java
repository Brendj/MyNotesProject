/**
 * $RCSfileGostTimeStampTokenProvider.java,v $
 * version $Revision: 36379 $
 * created 03.06.2015 16:13 by afevma
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
import xades4j.providers.TimeStampTokenGenerationException;
import xades4j.providers.TimeStampTokenProvider;

import org.bouncycastle.tsp.*;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Map;

/**
 * Класс для создания штампа времени.
 *
 * @author Copyright 2004-2015 Crypto-Pro. All rights reserved.
 * @.Version
 * @see xades4j.providers.impl.DefaultTimeStampTokenProvider
 */
public class GostTimeStampTokenProvider implements TimeStampTokenProvider {

    /**
     * Провайдер хеширования.
     */
    private final String messageDigestProvider;

    /**
     * Генератор штампа времени.
     */
    private final TimeStampRequestGenerator tsRequestGenerator;

    /**
     * Список пар "oid_алгоритма_хеширования=адрес_tsp_службы".
     * Используется для установления некоего соответствия между
     * версией алгоритма и службой. Переменная призвана разнообразить
     * и расширить пример.
     */
    private final Map<String, String> digestTsaMap;

    /**
     * OID алгоритма хеширования.
     */
    private String digestAlgOid = null;

    /**
     * Конструктор.
     *
     * @param tsaMap Список соответствий алгоритмов
     * хеширования и адресов TSP служб.
     * @param digestProvider Провайдер хеширования.
     * @throws NoSuchProviderException
     */
    public GostTimeStampTokenProvider(Map<String, String> tsaMap, String
        digestProvider) throws NoSuchProviderException {

        this.digestTsaMap = tsaMap;
        this.messageDigestProvider = digestProvider;
        this.tsRequestGenerator = new TimeStampRequestGenerator();
        this.tsRequestGenerator.setCertReq(true);
    }

    @Override
    public TimeStampTokenRes getTimeStampToken(byte[] tsDigestInput, String digestAlgUri)
        throws TimeStampTokenGenerationException {

        try {

            digestAlgOid = GostXAdESUtility.digestUri2Digest(digestAlgUri);

            final MessageDigest messageDigest = MessageDigest.
                getInstance(digestAlgOid, messageDigestProvider);

            final byte[] digest = messageDigest.digest(tsDigestInput);

            final TimeStampRequest tsRequest = tsRequestGenerator.generate(
                digestAlgOid, digest, BigInteger.valueOf(System.currentTimeMillis()));

            final InputStream responseStream = getResponse(tsRequest.getEncoded());
            final TimeStampResponse tsResponse = new TimeStampResponse(responseStream);

            if(tsResponse.getStatus() != 0 && tsResponse.getStatus() != 1) {
                throw new TimeStampTokenGenerationException("Time stamp token not granted. " +
                    tsResponse.getStatusString());
            } // if
            else {

                tsResponse.validate(tsRequest);
                final TimeStampToken tsToken = tsResponse.getTimeStampToken();

                return new TimeStampTokenRes(tsToken.getEncoded(),
                    tsToken.getTimeStampInfo().getGenTime());

            } // else

        } catch (TSPException e) {
            throw new TimeStampTokenGenerationException("Invalid time stamp response", e);
        } catch (IOException e) {
            throw new TimeStampTokenGenerationException("Encoding error", e);
        } catch (NoSuchAlgorithmException e) {
            throw new TimeStampTokenGenerationException("Digest algorithm not supported", e);
        } catch (NoSuchProviderException e) {
            throw new TimeStampTokenGenerationException("Digest algorithm not supported", e);
        }

    }

    private InputStream getResponse(byte[] encodedRequest) throws TimeStampTokenGenerationException {

        try {

            final HttpURLConnection connection = this.getHttpConnection();

            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-type", "application/timestamp-query");
            connection.setRequestProperty("Content-length", String.valueOf(encodedRequest.length));

            final OutputStream out = connection.getOutputStream();

            out.write(encodedRequest);
            out.flush();

            if(connection.getResponseCode() != 200) {
                throw new TimeStampTokenGenerationException(String.format("TSA returned HTTP %d %s",
                    connection.getResponseCode(), connection.getResponseMessage()));
            } // if
            else {
                return new BufferedInputStream(connection.getInputStream());
            } // else

        } catch (IOException e) {
            throw new TimeStampTokenGenerationException("Error when connecting to the TSA", e);
        }
    }

    /**
     * Создание подключения.
     *
     * @return подключение.
     * @throws IOException
     */
    private HttpURLConnection getHttpConnection() throws IOException {
        final URL url = new URL(this.getTSAUrl());
        return (HttpURLConnection)url.openConnection();
    }

    /**
     * Получение адреса службы. Адрес берется из списка
     * соответствий, чтобы разнообразить и расширить пример.
     *
     * @return адрес службы.
     */
    protected String getTSAUrl() {

        final String tsaUrl = GostXAdESUtility.digestOid2TsaUrl(
            digestTsaMap, digestAlgOid);

        //System.out.println("Used TSA url: " + tsaUrl +
        //    "(by digest oid: " + digestAlgOid + ")");

        return tsaUrl;

    }

}
