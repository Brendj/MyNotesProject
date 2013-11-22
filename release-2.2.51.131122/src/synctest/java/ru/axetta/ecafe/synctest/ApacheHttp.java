/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.synctest;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import com.sun.net.ssl.TrustManagerFactory;
import com.sun.net.ssl.TrustManager;
import com.sun.net.ssl.X509TrustManager;

import org.apache.commons.httpclient.ConnectTimeoutException;
import org.apache.commons.httpclient.params.HttpConnectionParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.httpclient.protocol.SecureProtocolSocketFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.net.ssl.SSLContext;
import com.sun.net.ssl.TrustManager;


public class ApacheHttp {


    public static class EasyX509TrustManager implements X509TrustManager {

        private X509TrustManager standardTrustManager = null;

        /**
         * Log object for this class.
         */
        private static final Log LOG = LogFactory.getLog(EasyX509TrustManager.class);

        /**
         * Constructor for EasyX509TrustManager.
         */
        public EasyX509TrustManager(KeyStore keystore) throws NoSuchAlgorithmException, KeyStoreException {
            super();
            TrustManagerFactory factory = TrustManagerFactory.getInstance("SunX509");
            factory.init(keystore);
            TrustManager[] trustmanagers = factory.getTrustManagers();
            if (trustmanagers.length == 0) {
                throw new NoSuchAlgorithmException("SunX509 trust manager not supported");
            }
            this.standardTrustManager = (X509TrustManager) trustmanagers[0];
        }

        /**
         * @see com.sun.net.ssl.X509TrustManager#isClientTrusted(X509Certificate[])
         */
        public boolean isClientTrusted(X509Certificate[] certificates) {
            return this.standardTrustManager.isClientTrusted(certificates);
        }

        /**
         * @see com.sun.net.ssl.X509TrustManager#isServerTrusted(X509Certificate[])
         */
        public boolean isServerTrusted(X509Certificate[] certificates) {
            return true;
            /*if ((certificates != null) && LOG.isDebugEnabled()) {
                LOG.debug("Server certificate chain:");
                for (int i = 0; i < certificates.length; i++) {
                    LOG.debug("X509Certificate[" + i + "]=" + certificates[i]);
                }
            }
            if ((certificates != null) && (certificates.length == 1)) {
                X509Certificate certificate = certificates[0];
                try {
                    certificate.checkValidity();
                } catch (CertificateException e) {
                    LOG.error(e.toString());
                    return false;
                }
                return true;
            } else {
                return true;
            }   */
        }

        /**
         * @see com.sun.net.ssl.X509TrustManager#getAcceptedIssuers()
         */
        public X509Certificate[] getAcceptedIssuers() {
            return this.standardTrustManager.getAcceptedIssuers();
        }
    }


    public static class EasySSLProtocolSocketFactory implements SecureProtocolSocketFactory {

        /**
         * Log object for this class.
         */
        private static final Log LOG = LogFactory.getLog(EasySSLProtocolSocketFactory.class);

        private SSLContext sslcontext = null;

        /**
         * Constructor for EasySSLProtocolSocketFactory.
         */
        public EasySSLProtocolSocketFactory() {
            super();
        }

        private static SSLContext createEasySSLContext() {
            try {
                SSLContext context = SSLContext.getInstance("SSL");
                context.init(null, new TrustManager[]{new EasyX509TrustManager(null)}, null);
                return context;
            } catch (Exception e) {
                LOG.error(e.getMessage(), e);
                throw new RuntimeException(e.toString());
            }
        }

        private SSLContext getSSLContext() {
            if (this.sslcontext == null) {
                this.sslcontext = createEasySSLContext();
            }
            return this.sslcontext;
        }

        public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort)
                throws IOException, UnknownHostException {
            return getSSLContext().getSocketFactory().createSocket(host, port, clientHost, clientPort);
        }

        public Socket createSocket(String host, int port, InetAddress clientHost, int clientPort, HttpConnectionParams httpConnectionParams)
                throws IOException, UnknownHostException, ConnectTimeoutException {
            return getSSLContext().getSocketFactory().createSocket(host, port, clientHost, clientPort);
        }

        /**
         * @see SecureProtocolSocketFactory#createSocket(java.lang.String, int)
         */
        public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
            return getSSLContext().getSocketFactory().createSocket(host, port);
        }

        /**
         * @see SecureProtocolSocketFactory#createSocket(java.net.Socket, java.lang.String, int, boolean)
         */
        public Socket createSocket(Socket socket, String host, int port, boolean autoClose)
                throws IOException, UnknownHostException {
            return getSSLContext().getSocketFactory().createSocket(socket, host, port, autoClose);
        }
    }
}
