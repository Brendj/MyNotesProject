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
import java.text.DecimalFormat;
import wss4j.utility.SpecUtility;

/*
 * Class calculates time of test execution. callRealTestExecution must be
 * defined in derived class. It can print report about efficiency of test.
 */
public abstract class TestManager {

	protected String testName = null;
	protected double testTime = 0;
	protected DecimalFormat decFormat = new DecimalFormat("#.##");
	protected SignatureManager signatureManager = null;
	
	public TestManager(String name, SignatureManager signatureManager) throws KeyStoreException, 
	NoSuchAlgorithmException, CertificateException, ClassNotFoundException {
		
		SpecUtility.initJCP();
		
		testName = name;
		this.signatureManager = signatureManager;
	}
	
	protected String getTestName() {
		return testName;
	}
	
	protected double getTestTimeInSec() {
		return testTime*0.001;
	}
	
	public void execute() {
		
		System.out.println("Test " + testName + " is run!");
		long begTime = java.lang.System.currentTimeMillis();
		
		callRealTestExecution();
		
		testTime = (double)(java.lang.System.currentTimeMillis() - begTime);
		System.out.println("Test " + testName + " is finished!");
	}
	
	protected abstract void callRealTestExecution();	
	public abstract void report();
}
