/**
 * Copyright 2004-2012 Crypto-Pro. All rights reserved.
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

package wss4j.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SignatureException;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertPath;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathValidator;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.CollectionCertStoreParameters;
import java.security.cert.PKIXCertPathValidatorResult;
import java.security.cert.PKIXParameters;
import java.security.cert.TrustAnchor;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import ru.CryptoPro.JCPxml.xmldsig.JCPXMLDSigInit;

/**
 * Class with auxiliary functions.
 */
public class SpecUtility {
	
	// All properties
	private static Properties props = new Properties();
	// Default properties. Alternative configuration is wss40 (+ our own variables related to
	// WSS4J files in /keys directory: 'wss40' is valid, 'wss40rev' is revoked)
	public static final String DEFAULT_CRYPTO_PROPERTIES = System.getProperty("user.dir") +
        "/data/WebContent/crypto.properties";
	public static String DEFAULT_STORETYPE = "HDImageStore";
	public static String DEFAULT_KEYSTORE = null;
	public static String DEFAULT_ALIAS = null;
	public static char[] DEFAULT_PASSWORD = null;
	public static String DEFAULT_CERT_FILE = null;
	public static String DEFAULT_CA_FILE = null;
	public static String DEFAULT_CRL_FILE = null;
	
	// Load all settings from crypto.properties
	static {
		
		try {
			
			props.load(new FileInputStream(DEFAULT_CRYPTO_PROPERTIES));
			
			// Standard WSS4J variables
			DEFAULT_STORETYPE = props.getProperty("org.apache.ws.security.crypto.merlin.keystore.type");
			DEFAULT_KEYSTORE = props.getProperty("org.apache.ws.security.crypto.merlin.file");
			DEFAULT_ALIAS = props.getProperty("org.apache.ws.security.crypto.merlin.keystore.alias");
			
			String password = props.getProperty("org.apache.ws.security.crypto.merlin.keystore.password");
			if (password != null) {
				DEFAULT_PASSWORD = password.toCharArray();
			}
			
			// Our own variables
			DEFAULT_CERT_FILE = props.getProperty("cert.file");
			DEFAULT_CA_FILE = props.getProperty("ca.file");
			DEFAULT_CRL_FILE = props.getProperty("crl.file");

		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
	/**
	 * Functions initializes XML JCP.
	 */
	public static void initJCP() {
		
		if(!JCPXMLDSigInit.isInitialized())
    		JCPXMLDSigInit.init();
		
		System.setProperty("com.ibm.security.enableCRLDP", "false");
	}

	/**
	 * Function loads store information from key store.
	 * @param storeType - type of key store (HDImageStore, JKS, PKCS12).
	 * @param store - store file.
	 * @param storePassword - password to store.
	 * @return loaded key store.
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static KeyStore loadKeyStore( String storeType, File store, char[] storePassword ) 
	throws KeyStoreException, NoSuchAlgorithmException, CertificateException, 
		FileNotFoundException, IOException {

		KeyStore keyStore = KeyStore.getInstance(storeType);
		FileInputStream inputStream = null;

		if (store != null)
			inputStream = new FileInputStream(store);

		keyStore.load( keyStore == null ? null : inputStream, storePassword );
		return keyStore;		 
	}
	
	/**
	 * Function verifies the user certificate chain 'certs'.
	 * @param crlFile - CRL file (can be null).
	 * @param certs - list of user certificates to be checked.
	 * @param cacerts - list of trusted certificates. 
	 * @param endPointCertificate - a certificate as last verifiable link in the chain (can be null).
	 * @return true, if chain is valid.
	 * @throws CRLException
	 * @throws FileNotFoundException
	 * @throws SignatureException 
	 * @throws NoSuchProviderException 
	 * @throws InvalidKeyException 
	 * @throws CertPathBuilderException 
	 */
	public static boolean validateCertPath(File crlFile,
			X509Certificate[] certs, X509Certificate[] cacerts,
			X509Certificate endPointCertificate) throws 
			CRLException, FileNotFoundException, InvalidKeyException, 
			NoSuchProviderException, SignatureException, CertPathBuilderException {
        
		PKIXCertPathValidatorResult bldResult = null;
		List<CertStore> storeCertCRL = null;
		CertStore crlCertStore = null;
    	boolean crlIsAdded = false;
    	boolean bResult = false;
		
		try {
          
    		CertificateFactory certFactory = CertificateFactory.getInstance("X509");
    		
    		/**
    		 * Add CRL in list.
    		 */
    		if ( crlFile != null ) {
    			
    			CRL crl = certFactory.generateCRL(new FileInputStream(crlFile));
    			if ( crl != null )
    				crlCertStore = CertStore.getInstance( "Collection", new CollectionCertStoreParameters(Collections.singletonList(crl)) );
    		
    			if (crlCertStore != null) {
    				storeCertCRL = Arrays.asList(crlCertStore);
    				crlIsAdded = true;
    			}
    		}
    		
            List<X509Certificate> certList = Arrays.asList(certs);
            CertPath path = certFactory.generateCertPath(certList);
            System.out.println("Certificate path length: " + path.getCertificates().size());
            
            Set<TrustAnchor> trustedAnchors = new HashSet<TrustAnchor>();

            if (cacerts != null) {
                for ( int i = 0; i < cacerts.length; ++i ) {
                    TrustAnchor anchor = 
                        new TrustAnchor(cacerts[i], 
                        		cacerts[i].getExtensionValue("2.5.29.30"));
                    if (anchor != null && !trustedAnchors.contains(anchor) )
                    	trustedAnchors.add(anchor);
                }
            }

            /* 
            if (certs != null) {
                for ( int i = 0; i < certs.length; ++i ) {
                	TrustAnchor anchor = 
                		new TrustAnchor(certs[i], 
                    		certs[i].getExtensionValue(Merlin.NAME_CONSTRAINTS_OID));
                	if ( anchor != null && !trustedAnchors.contains(anchor) )
                		trustedAnchors.add(anchor);
                }
            }
            */
            
            System.out.println("Trusted anchor deepth: " + trustedAnchors.size());
            
            if (certs != null && cacerts != null && 
            		certs.length == 1 && cacerts.length == 1) {
            	certs[0].checkValidity();
            	certs[0].verify(cacerts[0].getPublicKey());
            }
            
            PKIXParameters param = new PKIXParameters(trustedAnchors);
            param.setSigProvider(null);
            param.setRevocationEnabled(crlIsAdded);
            if (crlIsAdded)
            	 param.setCertStores(storeCertCRL);
          
            if (endPointCertificate != null) {
            	final X509CertSelector selector = new X509CertSelector();
            	selector.setCertificate(endPointCertificate);
            	param.setTargetCertConstraints(selector);
            }

            CertPathValidator certPathValidator = CertPathValidator.getInstance("PKIX");
            bldResult = (PKIXCertPathValidatorResult)certPathValidator.validate(path, param);
            /* MUST BE (bldResult != null)
            PublicKey publicKey = bldResult.getPublicKey();
            */
            bResult = true;
            
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        } catch (CertificateException ex) {
        	ex.printStackTrace();
        } catch (InvalidAlgorithmParameterException ex) {
        	ex.printStackTrace();
        } catch (CertPathValidatorException ex) {
        	ex.printStackTrace();
        	System.out.println("Index of certificate that caused exception: "
                    + ex.getIndex());
        }
        
        System.out.println("Path Validator result (public key algorithm): " + 
        		((bldResult != null) ? bldResult.getPublicKey().getAlgorithm() : null));

        return bResult;
    }
}
