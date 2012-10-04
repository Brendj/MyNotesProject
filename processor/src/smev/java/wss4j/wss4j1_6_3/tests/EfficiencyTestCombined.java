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
import wss4j.manager.SignatureManager;
import wss4j.manager.TestManager;
import wss4j.manager.TestManagerCombined;
import wss4j.utility.SpecUtility;
import wss4j.wss4j1_6_3.manager.SOAPXMLSignatureManager_1_6_3;

/*
 * Test provides different threads for signing and verifying of SOAP
 * XML documents as one operation synchronizing by queue.
 */
public class EfficiencyTestCombined {

	private static final int THREAD_COUNT = 5;
	private static final int WORKOUT_PERIOD = 30000;
	
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
	public static void main(String[] args) throws KeyStoreException, 
	NoSuchAlgorithmException, CertificateException, ClassNotFoundException, 
	UnrecoverableKeyException, FileNotFoundException, IOException {
		
		SignatureManager signatureManager = 
			new SOAPXMLSignatureManager_1_6_3(SpecUtility.DEFAULT_CRYPTO_PROPERTIES, 
				SpecUtility.DEFAULT_ALIAS, SpecUtility.DEFAULT_PASSWORD, SpecUtility.DEFAULT_PASSWORD);
		
		TestManager testCombined = new TestManagerCombined( "EfficiencyTestCombained", 
				THREAD_COUNT, WORKOUT_PERIOD, signatureManager);
		
		testCombined.execute();
		testCombined.report();
	}
}
