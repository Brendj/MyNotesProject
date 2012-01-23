/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.synctest;

import ru.axetta.ecafe.util.DigitalSignatureUtils;
import ru.axetta.ecafe.util.UriUtils;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.ByteArrayRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.net.ssl.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.net.URI;
import java.security.PrivateKey;
import java.security.PublicKey;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 28.07.2009
 * Time: 14:45:12
 * To change this template use File | Settings | File Templates.
 */
public class Main {

    public static void main(String[] args) {
        if (5 != args.length && 7 != args.length && 8 != args.length) {
            showUsage();
            return;
        }
        try {
            String processorUrl = args[0];
            String inputXmlFilename = args[1];
            String privateKeyFilename = args[2];
            String outputXmlFilename = args[3];
            String publicKeyFilename = args[4];
            String username = null;
            String password = null;
            String flags = null;
            if (5 < args.length) {
                username = args[5];
                if (6 < args.length) {
                    password = args[6];
                }
                if (7 < args.length) {
                    flags = args[7];
                }
            }
            if (flags != null) {
                if (flags.indexOf("-disable_cert_check") != -1) {
                    System.out.println("- Disabling SSL certificate check");
                    disableSSLCertCheck();
                }
            }

            Document requestDocument = readXmlDocument(inputXmlFilename);
            PrivateKey privateKey = readPrivateKey(privateKeyFilename);
            HttpMethod httpMethod = buildHttpMethod(privateKey, requestDocument, processorUrl, username, password);
            Document responseDocument = null;
            try {
                HttpClient httpClient = new HttpClient();
                int statusCode = httpClient.executeMethod(httpMethod);
                if (statusCode != HttpStatus.SC_OK) {
                    System.err
                            .println(String.format("HTTP request failed, status line: %s", httpMethod.getStatusLine()));
                } else {
                    responseDocument = readXmlDocument(httpMethod.getResponseBodyAsStream());
                }
            } catch (IOException e) {
                System.err.println("Failed to make HTTP request");
                e.printStackTrace();
            } finally {
                httpMethod.releaseConnection();
            }
            if (null != responseDocument) {
                PublicKey publicKey = readPublicKey(publicKeyFilename);
                if (!verifyResponse(publicKey, responseDocument)) {
                    System.err.println("Response verification failed!");
                }
                writeResonse(outputXmlFilename, responseDocument);
            }
        } catch (Exception e) {
            System.err.println("Failed");
            e.printStackTrace();
        }
    }

    private static void disableSSLCertCheck() {
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                        return null;
                    }

                    public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }

                    public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {
                    }
                }};

        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            System.out.println(e);
        }

        // Create all-trusting host name verifier
        HostnameVerifier allHostsValid = new HostnameVerifier() {
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        };

        // Install the all-trusting host verifier
        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

    }

    private static void showUsage() {
        System.out.println(
                "Usage: target-url input-filename input-private-key-filename output-filename output-public-key-filename [username password] [flags:-disable_cert_check]");
    }

    private static Document readXmlDocument(String filename) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        return documentBuilder.parse(new InputSource(new FileInputStream(filename)));
    }

    private static PrivateKey readPrivateKey(String filename) throws Exception {
        return DigitalSignatureUtils
                .convertToPrivateKey(IOUtils.toString(new FileInputStream(filename), CharEncoding.US_ASCII));
    }

    private static PublicKey readPublicKey(String filename) throws Exception {
        return DigitalSignatureUtils
                .convertToPublicKey(IOUtils.toString(new FileInputStream(filename), CharEncoding.US_ASCII));
    }

    private static HttpMethod buildHttpMethod(PrivateKey privateKey, Document requestDocument, String url,
            String username, String password) throws Exception {
        if (!DigitalSignatureUtils.hasSignature(requestDocument)) {
            DigitalSignatureUtils.sign(privateKey, requestDocument);
        }
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        // Output XML request to arrayOutputStream
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(new DOMSource(requestDocument), new StreamResult(arrayOutputStream));
        // Build HTTP POST method with arrayOutputStream as request entity
        URI methodUri = new URI(url);
        if (null != username) {
            methodUri = UriUtils.putParam(methodUri, "username", username);
            if (null != password) {
                methodUri = UriUtils.putParam(methodUri, "password", password);
            }
        }
        PostMethod httpMethod = new PostMethod(methodUri.toString());
        httpMethod.setRequestEntity(new ByteArrayRequestEntity(arrayOutputStream.toByteArray(), "text/xml"));
        return httpMethod;
    }

    private static Document readXmlDocument(InputStream inputStream) throws Exception {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        return documentBuilder.parse(new InputSource(inputStream));
    }

    private static boolean verifyResponse(PublicKey publicKey, Document responseDocument) throws Exception {
        return DigitalSignatureUtils.verify(publicKey, responseDocument);
    }

    private static void writeResonse(String filename, Document responseDocument) throws Exception {
        // Output XML response
        File outputFile = new File(filename);
        while (!outputFile.createNewFile()) {
            if (!outputFile.delete()) {
                System.err.println(String.format("Can't delete file \"%s\"", filename));
            }
        }
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(new DOMSource(responseDocument), new StreamResult(new FileOutputStream(outputFile)));
    }
}
