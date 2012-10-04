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

package wss4j.manager.effeciency;

import java.util.Vector;
import org.w3c.dom.Document;
import wss4j.manager.SignatureManager;

/*
 * Class processes signed SOAP XML document reading from queue (signature
 * verification). The type of class is "processor".
 */
public class Processor extends EfficiencyThread {

	private final int WAIT_TIMEOUT = 3000;
	
	public Processor(String name, Vector<Document> queue, 
			SignatureManager signatureManager, long workoutTimeout) {
		super(name, queue, "processor", signatureManager, workoutTimeout);
	}
	
	public void run() {
		
		synchronized(queue) {
		
			while (this.threadIsActive &&
					(java.lang.System.currentTimeMillis() - startTime)<workoutTimeout) {
				
				try {
					queue.wait(WAIT_TIMEOUT);
				} catch (InterruptedException e) {
					System.out.println("Queue.wait failed in Processor thread " + name + " : " + e.getMessage());
				}
				
				if (queue.size() > 0) {
					
					messageCount++;
					Document signedDoc = queue.firstElement();
					queue.remove(0);
					
					boolean verResult = signatureManager.verifyDoc(signedDoc, false);
					if (!verResult) {
						System.out.println("Verification failed in Processor thread " + name + " (" + 
								verResult + ")");
					}
				}
			}
		}

		finish();
	}
}
