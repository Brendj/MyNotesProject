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

/**
 * Simple class defines some methods to be used in thread tests.
 */
public abstract class SignatureManager {

	/**
	 * Function produces text XML SOAP message.
	 * @return XML SOAP message.
	 */
	public abstract String getMessage();
	
	/**
	 * Function sets signature in XML SOAP document.
	 * @param docStr - XML SOAP string.
	 * @return signed document.
	 */
	public abstract Document signDoc(String docStr);
	
	/**
	 * Function verifies signed XML SOAP document.
	 * @param signedDoc - signed document.
	 * @param printCert - option to print certificate.
	 * @return verification result.
	 */
	public abstract boolean verifyDoc(Document signedDoc, boolean printCert);
}
