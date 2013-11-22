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

package wss4j.wss4j1_6_3.bad;

import java.security.Provider;
import java.security.Security;
import java.util.List;

import org.apache.axis.message.SOAPEnvelope;
import org.apache.ws.security.WSConstants;
import org.apache.ws.security.WSSecurityEngine;
import org.apache.ws.security.WSSecurityEngineResult;
import org.apache.ws.security.WSSecurityException;
import org.apache.ws.security.components.crypto.Crypto;
import org.apache.ws.security.components.crypto.Merlin;
import org.apache.ws.security.message.WSSecHeader;
import org.apache.ws.security.message.WSSecSignature;
import org.apache.ws.security.util.WSSecurityUtil;
import org.w3c.dom.Document;
import wss4j.utility.SOAPUtility;
import wss4j.utility.SpecUtility;

/**
 * Attention! If test shows an exception about unsupported algorithm in newDigestMethod or/and
 * newSignatureMethod then: 
 * ...
 *	Provider pNew = new ru.CryptoPro.JCPxml.dsig.internal.dom.XMLDSigRI();
 *	Security.addProvider(pNew);
 *	...
 *	Security.getProvider("XMLDSig").put("XMLSignatureFactory.DOM", 
 *		        "ru.CryptoPro.JCPxml.dsig.internal.dom.DOMXMLSignatureFactory");
 *	Security.getProvider("XMLDSig").put("KeyInfoFactory.DOM", 
 *		        "ru.CryptoPro.JCPxml.dsig.internal.dom.DOMKeyInfoFactory");
 *	...
 * Example works correctly in java 7 (e.g. jdk1.7.0_02).
 */

/**
 * Example shows that xmlns is duplicated in every node.
 */
public class TestDubl {

	private static Crypto crypto = null;
	private static WSSecurityEngine secEngine = new WSSecurityEngine();
	
	/**
	 * @param args
	 * @throws Exception 
	 */
	public static void main(String[] args) throws Exception {

		// Initialize JCP
		com.sun.org.apache.xml.internal.security.Init.init();
		SpecUtility.initJCP();
		
		// Load CryptoPro XMLDSig service provider
		Provider provCryptoProRI = new ru.CryptoPro.JCPxml.dsig.internal.dom.XMLDSigRI();
		Security.addProvider(provCryptoProRI);
		
		// Override methods
		Security.getProvider("XMLDSig").put("XMLSignatureFactory.DOM", 
		        "ru.CryptoPro.JCPxml.dsig.internal.dom.DOMXMLSignatureFactory");
		Security.getProvider("XMLDSig").put("KeyInfoFactory.DOM", 
		        "ru.CryptoPro.JCPxml.dsig.internal.dom.DOMKeyInfoFactory");
		
		// Prepare SOAP message
		SOAPEnvelope unsignedEnvelope = SOAPUtility.getSOAPEnvelopeFromString(SOAPUtility.SOAPMSG);
		Document doc = unsignedEnvelope.getAsDocument();
	    WSSecHeader secHeader = new WSSecHeader();
	    secHeader.insertSecurityHeader(doc);
	    	 
	    // Create Merlin to execute cryptographic operations
	    Merlin merlin = new Merlin();
		merlin.setKeyStore(SpecUtility.loadKeyStore(SpecUtility.DEFAULT_STORETYPE, null, 
				SpecUtility.DEFAULT_PASSWORD));
		crypto = merlin;

		// Prepare object to sign SOAP message (alias, password, algorithm etc)
		WSSecSignature sign = new WSSecSignature();
		
		String pswrd = null;
		if (SpecUtility.DEFAULT_PASSWORD != null) {
			pswrd = new String(SpecUtility.DEFAULT_PASSWORD);
		}
		
		sign.setUserInfo(SpecUtility.DEFAULT_ALIAS, pswrd);
	    sign.setKeyIdentifierType(WSConstants.BST_DIRECT_REFERENCE);
	    sign.setSignatureAlgorithm(ru.CryptoPro.JCPxml.Consts.URI_GOST_SIGN);
	    sign.setDigestAlgo(ru.CryptoPro.JCPxml.Consts.URI_GOST_DIGEST);
	    
	    Document signedDoc = null;
	    
	    try {
	    	// Sign SOAP XML document
			signedDoc = sign.build(doc, crypto, secHeader);
		} catch (WSSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
		// See duplicated xmlns
	    String docStr = org.apache.ws.security.util.XMLUtils.PrettyDocumentToString(signedDoc);
	    System.out.println("Bad signed document: " + docStr);
	    
	    WSSecurityEngineResult actionResult = null;
		// Verify signature and see that it's wrong because of excess xmlns
		try {
			List<WSSecurityEngineResult> results = secEngine.processSecurityHeader(doc, null, null, crypto);
			actionResult = WSSecurityUtil.fetchActionResult(results, WSConstants.SIGN);
		
		} catch (WSSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Wrong document shows 'null'
		System.out.println("Verified: " + actionResult);
	}
}
