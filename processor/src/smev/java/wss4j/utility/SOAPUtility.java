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

package wss4j.utility;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import org.apache.axis.Message;
import org.apache.axis.message.SOAPEnvelope;

/**
 * Class with auxiliary SOAP functions.
 */
public class SOAPUtility {

	/**
	 * Simple XML SOAP example.
	 */
	public static final String SOAPMSG = 
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" 
        + "<SOAP-ENV:Envelope "
        +   "xmlns:SOAP-ENV=\"http://schemas.xmlsoap.org/soap/envelope/\" "
        +   "xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" "
        +   "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">" 
        +   "<SOAP-ENV:Body>" 
        +       "<add xmlns=\"http://ws.apache.org/counter/counter_port_type\">" 
        +           "<value xmlns=\"\">15</value>" 
        +       "</add>" 
        +   "</SOAP-ENV:Body>" 
        + "</SOAP-ENV:Envelope>";
	
	/** Function checks 'org.apache.ws.security.crypto.provider' option in 
	 * 'crypto.properties' to determine wss4j version: 
	 * 		if Merlin's base class is AbstractCrypto, then wss4j version is 1.5.11, 
	 * 		else if Merlin's base class is CryptoBase, then version is 1.6.3.
	 * @param className - name of provider (Merlin).
	 * @return - true, if version is 1.5.11.
	 * @throws ClassNotFoundException
	 */
	public static boolean is_1_5_11( String className ) throws ClassNotFoundException {
	
		Class classType = Class.forName(className);
		Class superClassType = classType.getSuperclass();
		String superClassName = superClassType.getSimpleName();
		
		// If properties contains MerlinEx class
		if ( superClassName.equalsIgnoreCase("Merlin") ) {
			return is_1_5_11(superClassType.getName());
		}
		
		if ( superClassName.equalsIgnoreCase("AbstractCrypto") ) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * Function creates SOAP XML message from string.
	 * @return SOAP XML message.
	 * @throws Exception
	 */
	public static SOAPEnvelope getSOAPEnvelopeFromString(String message) throws Exception {
        
    	InputStream input = new ByteArrayInputStream(message.getBytes());
        Message msg = new Message(input);
        return msg.getSOAPEnvelope();
        
    }
	
	/**
	 * Function creates SOAP XML message reading file .
	 * @param fileName - SOAP XML file name.
	 * @return SOAP XML message.
	 * @throws Exception
	 */
	public static SOAPEnvelope getSOAPEnvelopeFromFile(String fileName) throws Exception {
    	
    	InputStream input = new FileInputStream( new File(fileName) );
        Message msg = new Message(input);
        return msg.getSOAPEnvelope();
        
    }
}
