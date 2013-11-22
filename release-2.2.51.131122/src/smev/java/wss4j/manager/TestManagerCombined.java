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

package wss4j.manager;

import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.util.Vector;
import wss4j.manager.effeciency.EfficiencyThread;

/*
 * Test collects statistics about operations of signing and verifying
 * of XML documents in different threads executing simultaneously.
 */
public class TestManagerCombined extends TestManager {

	private Vector<EfficiencyThread> threads = new Vector<EfficiencyThread>();
	private long threadCount = 0;
	private long workoutPeriod = 0;
		
	public TestManagerCombined(String name, long thCount,
			long workPeriod, SignatureManager signatureManager) throws KeyStoreException, 
			NoSuchAlgorithmException, CertificateException, ClassNotFoundException {
		super(name, signatureManager);
		threadCount = thCount;
		workoutPeriod = workPeriod;
	}

	protected void callRealTestExecution() {
		
		threads.clear();
		
		for (int i = 0; i < threadCount; i++) {
			threads.add(new CombinedSignatureManager( "combained_" + (i + 1), 
					signatureManager, workoutPeriod ));
		}
		
		for (int i = 0; i < threads.size(); i++) {
			
			try {
				threads.get(i).getThread().join(workoutPeriod);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		for (int i = 0; i < threads.size(); i++) {
			
			threads.get(i).stop();
			
			try {
				threads.get(i).getThread().join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public void report() {
		
		long signedAndVerifiedDocCount = 0;
		for (int i = 0; i < threads.size(); i++) {
			signedAndVerifiedDocCount += threads.get(i).getMessageCount();
		}

		System.out.println("###### Report: ######");
		System.out.println("Count of documents which were signed and verified: " + 
				signedAndVerifiedDocCount + " in " + threads.size() + " thread(s)");
		System.out.println("Total (signing & verifying) time: " + decFormat.format(getTestTimeInSec()) + 
				" sec in " + threads.size() + " thread(s)");
		System.out.println("Rate of (signing & verifying) operation: " + 
				decFormat.format((double)signedAndVerifiedDocCount/getTestTimeInSec()) + 
				" op/s (1 action rate ~ " + 
				decFormat.format( (double)signedAndVerifiedDocCount*2/(getTestTimeInSec()) ) + " op/s)");
	}
}
