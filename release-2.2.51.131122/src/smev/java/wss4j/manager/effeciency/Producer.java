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
 * Class produces signed SOAP XML document and adds it into queue.
 * The type of class is "producer".
 */
public class Producer extends EfficiencyThread {
	
	private final int WAIT_TIMEOUT = 5;
	
	public Producer(String name, Vector<Document> queue, 
			SignatureManager signatureManager, long workoutTimeout) {
		super(name, queue, "producer", signatureManager, workoutTimeout);
	}
	
	public void run() {

		while (this.threadIsActive &&
				(java.lang.System.currentTimeMillis() - startTime)<workoutTimeout) {
			
			synchronized(queue) {
				
				messageCount++;
				Document signedDoc = signatureManager.signDoc(signatureManager.getMessage());
				queue.add(signedDoc);
				queue.notifyAll();
				
			}
			
			try {
				Thread.sleep(WAIT_TIMEOUT);
			} catch (InterruptedException e) {
				System.out.print("Thread.sleep failed in Producer thread " + name + " : " + e.getMessage());
			}
		}

		finish();
	}
}
