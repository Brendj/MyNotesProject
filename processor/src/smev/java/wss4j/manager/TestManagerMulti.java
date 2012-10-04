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
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import org.w3c.dom.Document;
import wss4j.manager.effeciency.EfficiencyThread;
import wss4j.manager.effeciency.Processor;
import wss4j.manager.effeciency.Producer;

/*
 * Test collects statistics about operations of signing and verifying
 * of XML documents in different threads synchronized by queue of documents.
 */
public class TestManagerMulti extends TestManager {

	protected List<Vector<Document>> queues = new ArrayList<Vector<Document>>();
	protected Vector<EfficiencyThread> threads = new Vector<EfficiencyThread>();
	protected long producerCount = 0;
	protected long processorCount = 0;
	protected long workoutPeriod = 0;
	
	public TestManagerMulti(String name, long prodCount, long procCount,
		long workPeriod, SignatureManager signatureManager) throws KeyStoreException, 
		NoSuchAlgorithmException, CertificateException, ClassNotFoundException {
		
		super(name, signatureManager);
		producerCount = prodCount;
		processorCount = procCount;
		workoutPeriod = workPeriod;
	}
	
	protected void callRealTestExecution() {
		
		threads.clear();
		queues.add(new Vector<Document>());

		for (int i = 0; i < producerCount; i++) {
			threads.add(new Producer( "producer_" + (i + 1), queues.get(0), 
					signatureManager, workoutPeriod ));
		}
		
		for (int i = 0; i < processorCount; i++) {
			threads.add(new Processor( "processor_" + (i + 1), queues.get(0), 
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
		
		long signedDocCount = 0;
		long verifiedDocCount = 0;
		long producerCount = 0;
		long processorCount = 0;
		
		for (int i = 0; i < threads.size(); i++) {
			
			if (threads.get(i).getType().equalsIgnoreCase("producer")) {
				producerCount++;
				signedDocCount += threads.get(i).getMessageCount();
			}
			else if (threads.get(i).getType().equalsIgnoreCase("processor")) {
				processorCount++;
				verifiedDocCount += threads.get(i).getMessageCount();
			}
		}
		
		System.out.println("###### Report: ######");

		System.out.println("Count of documents which were signed: " + signedDocCount + 
				" in " + producerCount + " thread(s)");
		System.out.println("Count of documents which were verified: " + verifiedDocCount + 
				" in " + processorCount + " thread(s)");
		System.out.println("Total count of operations: " + (signedDocCount + verifiedDocCount));
		System.out.println("Total (signing & verifying) time: " + decFormat.format(getTestTimeInSec()) + " sec in " + 
				producerCount + " thread(s)");
		System.out.println("Rate of (signing & verifying) operation: " + 
				decFormat.format((double)(signedDocCount + verifiedDocCount)/getTestTimeInSec()) + " op/s");
	}
}
