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

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import org.w3c.dom.Document;
import wss4j.manager.SignatureManager;
import wss4j.utility.SpecUtility;
import wss4j.wss4j1_6_3.manager.SOAPXMLSignatureManager_1_6_3;

/**
 * Class for signing & verifying of SOAP XML document.
 */
public class WSS4J_SignVerifySOAP {

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws CertificateException 
	 * @throws NoSuchAlgorithmException 
	 * @throws KeyStoreException 
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws UnrecoverableKeyException 
	 */
	public static void main(String[] args) throws KeyStoreException, NoSuchAlgorithmException, 
	CertificateException, ClassNotFoundException, UnrecoverableKeyException, 
	FileNotFoundException, IOException {

		// Initialize JCP
		SpecUtility.initJCP();
		
		System.out.println("###### Test WSS4J_SignVerifySOAP 1.6.3 is begun ######");
		
		// Load key store
		SignatureManager manager = 
			new SOAPXMLSignatureManager_1_6_3(SpecUtility.DEFAULT_CRYPTO_PROPERTIES, 
				SpecUtility.DEFAULT_ALIAS, SpecUtility.DEFAULT_PASSWORD, SpecUtility.DEFAULT_PASSWORD);
		
		// Sign XML SOAP document
        Document signedDoc = manager.signDoc(manager.getMessage());
        String outputString = org.apache.ws.security.util.XMLUtils.PrettyDocumentToString(signedDoc);
        System.out.println("Signed document: ");
        System.out.println(outputString);
		
        // Verify signature in XML SOAP document
        boolean printCert = true;
        boolean result = manager.verifyDoc(signedDoc, printCert);
        System.out.println("\nVerified: " + result);
		
		System.out.println("###### Test WSS4J_SignVerifySOAP is finished ######");
	}
}
