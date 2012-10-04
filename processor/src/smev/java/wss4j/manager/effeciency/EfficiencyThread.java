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
 * Base class, which extends standard implementation of thread. It contains
 * single signature provider to produce and verify signatures in SOAP
 * XML documents.
 */
public abstract class EfficiencyThread implements Runnable {

	protected Thread thread;
	protected String name;
	protected Vector<Document> queue;
	volatile protected boolean threadIsActive;
	protected long messageCount;
	protected SignatureManager signatureManager = null;
	private String type;
	protected long workoutTimeout = 0;
	protected long startTime = 0;
	
	protected EfficiencyThread(String name, Vector<Document> queue, String type, 
			SignatureManager signatureManager, long workoutTimeout) {
		this.name = name;
		this.type = type;
		this.queue = queue;
		this.threadIsActive = true;
		this.messageCount = 0;
		this.workoutTimeout = workoutTimeout;
		this.startTime = java.lang.System.currentTimeMillis();
		this.signatureManager = signatureManager;
		this.thread = new Thread(this, name);
		this.thread.start();
	}

	@Override
	public abstract void run();
	
	public void stop() {
		this.threadIsActive = false;
	}
	
	public Thread getThread() {
		return this.thread;
	}
	
	public String getName() {
		return this.name;
	}
	
	public long getMessageCount() {
		return this.messageCount;
	}
	
	public String getType() {
		return this.type;
	}
	
	synchronized protected void finish() {
		System.out.println("Thread " + this.name + " is stopped. Message count: " + this.messageCount);
	}
}
