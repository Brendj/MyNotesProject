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

package wss4j.wss4j1_6_3.tests;

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
import java.security.cert.CRLException;
import java.security.cert.CertPathBuilderException;
import java.security.cert.CertPathValidatorException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Properties;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.CryptoFactory;
import org.apache.ws.security.components.crypto.CryptoType;
import wss4j.utility.SpecUtility;

/**
 * Class for verifying of validity of a certificate chain by two methods:
 * 	1) user function to verify a chain of certificates from any store or file (with/without CRL).
 * 	2) example of calling of the same function from class 'Merlin' (without CRL).
 */
public class ValidateCertificateChain {
	
	/**
	 * @param args
	 * @throws CertPathValidatorException 
	 * @throws CertPathBuilderException 
	 * @throws CRLException 
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws IOException 
	 * @throws KeyStoreException 
	 * @throws SignatureException 
	 * @throws NoSuchProviderException 
	 * @throws InvalidKeyException 
	 * @throws WSSecurityException 
	 */
	public static void main(String[] args) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, 
	CertificateException, CRLException, CertPathBuilderException, CertPathValidatorException, KeyStoreException, 
	IOException, InvalidKeyException, NoSuchProviderException, SignatureException, WSSecurityException {
		
		SpecUtility.initJCP();
		
		//Test #1
		//runTestRSA();
		runTestGOST();
		
		//Test #2
		/* Using Merlin
		runTestRSA_IfCAIsInCacertsAndIfUseMerlinByProperties();
		runTestGOST_IfCAIsInCacertsAndIfUseMerlinByProperties();
		*/
	}
	
	/**
	 * Function verifies RSA certificate chain with/without CRL (can be null). If certificate is 
	 * revoked, therefore it throws exception.
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws CRLException
	 * @throws SignatureException 
	 * @throws NoSuchProviderException 
	 * @throws InvalidKeyException 
	 * @throws CertPathBuilderException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws WSSecurityException 
	 */
	public static void runTestRSA() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, 
	FileNotFoundException, IOException, CRLException, InvalidKeyException, NoSuchProviderException, 
	SignatureException, CertPathBuilderException, InvalidAlgorithmParameterException, WSSecurityException {
		
		System.out.println("###### 1. Test runTestRSA is begun ######");

		// Load key store to extract client certificate
		File store = null;
		if (SpecUtility.DEFAULT_KEYSTORE != null) {
			store = new File(SpecUtility.DEFAULT_KEYSTORE);
		}
		
		KeyStore clientKeyStore = SpecUtility.loadKeyStore( SpecUtility.DEFAULT_STORETYPE, 
				store, SpecUtility.DEFAULT_PASSWORD );
		X509Certificate clientCertificate = (X509Certificate)clientKeyStore.getCertificate(SpecUtility.DEFAULT_ALIAS);
		
		// Load trusted certificate from file
		FileInputStream trustedCertStream = null;
		if (SpecUtility.DEFAULT_CA_FILE != null) {
			trustedCertStream = new FileInputStream( new File(SpecUtility.DEFAULT_CA_FILE) );
		}
		else {
			throw new IOException("Default CA file is not found.");
		}
		
		CertificateFactory factory = CertificateFactory.getInstance("X509");
		X509Certificate trustedCertificate = (X509Certificate)factory.generateCertificate( trustedCertStream );
		
		// Build chain
        X509Certificate[] certificateChain = new X509Certificate[2];
        certificateChain[0] = clientCertificate;
        certificateChain[1] = trustedCertificate;
        
        X509Certificate[] trustedCertificates = new X509Certificate[1];
        trustedCertificates[0] = trustedCertificate;
        
        /**
         * First parameter can be null.  Verify certificate chain using CRL file.
         */
        File crl = null;
		if (SpecUtility.DEFAULT_CRL_FILE != null) {
			crl = new File(SpecUtility.DEFAULT_CRL_FILE);
		}
		
        boolean bResult = SpecUtility.validateCertPath( crl, certificateChain, trustedCertificates, null );
        System.out.println("Test result: " + bResult);
        
        System.out.println("###### Test runTestRSA is finished ######");
	}
	
	/**
	 * Function verifies GOST 34.10 certificate chain with/without CRL (can be null). wss40rev is 
	 * revoked, therefore it throws exception.
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws CertificateException
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws CRLException
	 * @throws SignatureException 
	 * @throws NoSuchProviderException 
	 * @throws InvalidKeyException 
	 * @throws CertPathBuilderException 
	 * @throws InvalidAlgorithmParameterException 
	 * @throws WSSecurityException 
	 */
	public static void runTestGOST() throws KeyStoreException, NoSuchAlgorithmException, CertificateException, 
	FileNotFoundException, IOException, CRLException, InvalidKeyException, NoSuchProviderException, 
	SignatureException, CertPathBuilderException, InvalidAlgorithmParameterException, WSSecurityException {
		
		System.out.println("###### 2. Test runTestGOST is begun ######");
		
		CertificateFactory factory = CertificateFactory.getInstance("X509");
		// Load client and trusted certificates from file
		FileInputStream certStream = null;
		if (SpecUtility.DEFAULT_CERT_FILE != null) {
			certStream = new FileInputStream( new File(SpecUtility.DEFAULT_CERT_FILE) );
		}
		else {
			throw new IOException("Default certificate file is not found.");
		}
		
		FileInputStream caStream = null;
		if (SpecUtility.DEFAULT_CA_FILE != null) {
			caStream = new FileInputStream( new File(SpecUtility.DEFAULT_CA_FILE) );
		}
		else {
			throw new IOException("Default CA file is not found.");
		}
		
		X509Certificate clientCertificate = (X509Certificate)factory.generateCertificate( certStream );
		X509Certificate trustedCertificate = (X509Certificate)factory.generateCertificate( caStream );
		
		// Build chain
        X509Certificate[] certificateChain = new X509Certificate[2];
        certificateChain[0] = clientCertificate;
        certificateChain[1] = trustedCertificate;
        
        X509Certificate[] trustedCertificates = new X509Certificate[1];
        trustedCertificates[0] = trustedCertificate;

        /**
         * First parameter can be null. Verify certificate chain using CRL file.
         */
        File crl = null;
		if (SpecUtility.DEFAULT_CRL_FILE != null) {
			crl = new File(SpecUtility.DEFAULT_CRL_FILE);
		}
		
        boolean bResult = SpecUtility.validateCertPath( crl, certificateChain, 
        		trustedCertificates, null );
        System.out.println("Test result: " + bResult);
        
        System.out.println("###### Test runTestGOST is finished ######");
        
	}
	
	/**
	 * Function verifies RSA certificate chain without CRL. Merlin constructor loads key stores (user & 
	 * cacerts). Root certificates are always stored in 'jre/lib/security/cacerts', therefore you should 
	 * install root certificates in cacerts. There is no default constructor for Merlin in wss4j 1.5.11, 
	 * so 'crypto.properties' is easy way to extract client certificate & private key from key store. 
	 * If there are not certificates in key store, then chain won't be builded.
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws CertificateException 
	 * @throws WSSecurityException 
	 */
	public static void runTestRSA_IfCAIsInCacertsAndIfUseMerlinByProperties() throws FileNotFoundException, 
	IOException, CertificateException, WSSecurityException {
	
		System.out.println("###### 3. Test runTestRSA_IfCAIsInCacertsAndIfUseMerlinByProperties is begun ######");

		// Load properties
		Properties properties = new Properties();
		properties.load( new FileInputStream(SpecUtility.DEFAULT_CRYPTO_PROPERTIES) );
		Crypto crypto = CryptoFactory.getInstance(properties);
	
		/**
		 * Without CRL. Verify certificate chain using Merlin.
		 */
		CryptoType cryptoType = new CryptoType(CryptoType.TYPE.ALIAS);
        cryptoType.setAlias(SpecUtility.DEFAULT_ALIAS);
		boolean bResult = crypto.verifyTrust( crypto.getX509Certificates(cryptoType), false );
        System.out.println("Test result: " + bResult);
        
        System.out.println("###### Test runTestRSA_IfCAIsInCacertsAndIfUseMerlinByProperties is finished ######");
	}
	
	public static void runTestGOST_IfCAIsInCacertsAndIfUseMerlinByProperties() throws FileNotFoundException, 
	IOException, CertificateException, WSSecurityException {
	
		System.out.println("###### 4. Test runTestGOST_IfCAIsInCacertsAndIfUseMerlinByProperties is begun ######");
		
		// Load properties
		Properties properties = new Properties();
		properties.load( new FileInputStream( SpecUtility.DEFAULT_CRYPTO_PROPERTIES ) );
		Crypto crypto = CryptoFactory.getInstance(properties);
		
		/**
		 * Without CRL. Verify certificate chain using Merlin.
		 */
		CryptoType cryptoType = new CryptoType(CryptoType.TYPE.ALIAS);
        cryptoType.setAlias(SpecUtility.DEFAULT_ALIAS);
		boolean bResult = crypto.verifyTrust( crypto.getX509Certificates(cryptoType), false );
        System.out.println("Test result: " + bResult);
        
        System.out.println("###### Test runTestGOST_IfCAIsInCacertsAndIfUseMerlinByProperties is finished ######");
	}
}
