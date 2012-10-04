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

import org.w3c.dom.Document;
import wss4j.manager.effeciency.EfficiencyThread;

/*
 * Class produces signed SOAP XML document and verify its signature. It also
 * calculates time of execution and count of operations. The type of
 * class is "combined".
 */
public class CombinedSignatureManager extends EfficiencyThread {

	private final int WAIT_TIMEOUT = 5;
	
	public CombinedSignatureManager(String name, SignatureManager signatureManager, 
			long workoutTimeout) {
		super(name, null, "combined", signatureManager, workoutTimeout);
	}

	synchronized private void printOperationResult(boolean result) {
		System.out.println("Document verification failed in CombainedSignatureManager thread " + this.name + 
				" (" + result + ")");

	}
	
	@Override
	public void run() {
		
		while (this.threadIsActive &&
				(java.lang.System.currentTimeMillis() - startTime)<workoutTimeout ) {
			
			messageCount++;
			Document signedDoc = signatureManager.signDoc(signatureManager.getMessage());
			boolean verResult = signatureManager.verifyDoc(signedDoc, false);
			if (!verResult) {
				printOperationResult(verResult);
			}
			
			try {
				Thread.sleep(WAIT_TIMEOUT);
			} catch (InterruptedException e) {
				System.out.print("Thread.sleep failed in CombainedSignatureManager thread " + this.name + 
						" : " + e.getMessage());
			}
		}
		
		finish();
	}
}
