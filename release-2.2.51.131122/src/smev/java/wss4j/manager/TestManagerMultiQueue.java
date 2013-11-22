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
import org.w3c.dom.Document;

import wss4j.manager.effeciency.Processor;
import wss4j.manager.effeciency.Producer;

/*
 * Test collects statistics about operations of signing and verifying
 * of XML documents in different threads. Every pair of producer and
 * processor is synchronized by queue of documents.
 */
public class TestManagerMultiQueue extends TestManagerMulti {

	private long queueCount = 0;
	
	public TestManagerMultiQueue(String name, long queueCount,
			long workPeriod, SignatureManager signatureManager) throws KeyStoreException, 
			NoSuchAlgorithmException, CertificateException, ClassNotFoundException {
		super(name, queueCount, queueCount, workPeriod, signatureManager);
		this.queueCount = queueCount;
	}

	protected void callRealTestExecution() {
		
		threads.clear();
		
		for (long i = 0; i < queueCount; i++) {
			queues.add(new Vector<Document>());
		}

		for (int i = 0; i < producerCount; i++) {
			threads.add(new Producer( "producer_" + (i + 1), queues.get(i), 
					signatureManager, workoutPeriod ));
		}

		for (int i = 0; i < processorCount; i++) {
			threads.add(new Processor( "processor_" + (i + 1), queues.get(i), 
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
}
