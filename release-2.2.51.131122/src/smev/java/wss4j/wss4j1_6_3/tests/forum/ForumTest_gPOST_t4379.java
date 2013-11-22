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

package wss4j.wss4j1_6_3.tests.forum;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.crypto.XMLStructure;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPMessage;
import javax.xml.soap.SOAPPart;
import javax.xml.transform.stream.StreamSource;

import org.apache.ws.security.message.WSSecHeader;
import org.apache.xml.security.transforms.Transforms;
import org.apache.xpath.XPathAPI;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import ru.CryptoPro.JCP.JCP;
import ru.CryptoPro.JCP.tools.Array;
import wss4j.gosuslugi.smev.SignatureTool.SignatureTool;
import wss4j.gosuslugi.smev.SignatureTool.SignatureToolService;
import wss4j.gosuslugi.smev.SignatureTool.SignatureToolServiceLocator;
import wss4j.gosuslugi.smev.SignatureTool.xsd.VerifySignatureRequestType;
import wss4j.gosuslugi.smev.SignatureTool.xsd.VerifySignatureResponseType;
import wss4j.manager.SignatureManager;
import wss4j.utility.SpecUtility;
import wss4j.wss4j1_6_3.manager.SOAPXMLSignatureManager_1_6_3;

public class ForumTest_gPOST_t4379 {

    /**
     * Файл с документом для подписи.
     */
    private final static String inSOAPFile = System.getProperty("user.dir") + "/data/unsignedRequest.xml";
    /**
     * Адрес тестового сервиса СМЭВ.
     */
    private final static String smevService = "http://188.254.16.92:7777/gateway/services/SID0003038";
    /**
     * Нужно ли проверять подпись онлайн в сервисе СМЭВ.
     */
    private final static boolean checkOnline = true;
	
	public static String convertStreamToString(InputStream is) throws Exception {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();
	    String line = null;

        while ((line = reader.readLine()) != null) {
	      sb.append(line + "\n");
	    }

	    is.close();

        return sb.toString();
	  }
	
	/**
	 * @param args
	 * @throws  
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {

		com.sun.org.apache.xml.internal.security.Init.init();
		SpecUtility.initJCP();
		
		KeyStore keyStore = KeyStore.getInstance(JCP.HD_STORE_NAME);
		keyStore.load(null, null);
		
		PrivateKey privateKey = (PrivateKey)keyStore.getKey(SpecUtility.DEFAULT_ALIAS, 
				SpecUtility.DEFAULT_PASSWORD);
		X509Certificate cert = (X509Certificate) keyStore.getCertificate(SpecUtility.DEFAULT_ALIAS);
			
		MessageFactory mf = MessageFactory.newInstance();

		SOAPMessage sm = mf.createMessage();
		SOAPPart soapPart = sm.getSOAPPart();  
		   
		FileInputStream is = new FileInputStream(inSOAPFile);
		// Set contents of message  
		soapPart.setContent(new StreamSource(is)); 
		
		sm.getSOAPPart().getEnvelope().addNamespaceDeclaration("wsse", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd");
		sm.getSOAPPart().getEnvelope().addNamespaceDeclaration("wsu", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd");
		sm.getSOAPPart().getEnvelope().addNamespaceDeclaration("ds", "http://www.w3.org/2000/09/xmldsig#");
		sm.getSOAPBody().setAttributeNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-utility-1.0.xsd", "wsu:Id", "body");
		
	    WSSecHeader header = new WSSecHeader();
	    header.setActor("http://smev.gosuslugi.ru/actors/smev");
	    header.setMustUnderstand(false);

	    Element sec = header.insertSecurityHeader(sm.getSOAPPart());
	    Document doc = sm.getSOAPPart().getEnvelope().getOwnerDocument();

	    Element token = (Element) sec.appendChild(doc.createElementNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "wsse:BinarySecurityToken"));
	    token.setAttribute("EncodingType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-soap-message-security-1.0#Base64Binary");
	    token.setAttribute("ValueType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3");
	    token.setAttribute("wsu:Id", "CertId");
		
		Provider xmlDSigProvider = new ru.CryptoPro.JCPxml.dsig.internal.dom.XMLDSigRI();

	    XMLSignatureFactory fac = XMLSignatureFactory.getInstance("DOM", xmlDSigProvider);

	    List<Transform> transformList = new ArrayList<Transform>();
	    Transform transform = fac.newTransform(Transform.ENVELOPED, (XMLStructure) null);
	    Transform transformC14N = fac.newTransform(Transforms.TRANSFORM_C14N_EXCL_OMIT_COMMENTS, (XMLStructure) null);
	    transformList.add(transform);
	    transformList.add(transformC14N);

	    Reference ref = fac.newReference("#body", fac.newDigestMethod("http://www.w3.org/2001/04/xmldsig-more#gostr3411", null),
	    		transformList, null, null);

	    // Make link to signing element
	    SignedInfo si = fac.newSignedInfo(fac.newCanonicalizationMethod(CanonicalizationMethod.EXCLUSIVE,
	        (C14NMethodParameterSpec) null),
	        fac.newSignatureMethod("http://www.w3.org/2001/04/xmldsig-more#gostr34102001-gostr3411", null),
	        Collections.singletonList(ref));

	    // Prepare key information to verify signature in future on other side
	    KeyInfoFactory kif = fac.getKeyInfoFactory();
	    X509Data x509d = kif.newX509Data(Collections.singletonList(cert));
	    KeyInfo ki = kif.newKeyInfo(Collections.singletonList(x509d));

	    // Create signature and sign by private key
	    javax.xml.crypto.dsig.XMLSignature sig = fac.newXMLSignature(si, ki);

	    Element l = doc.getElementById("body");
	    DOMSignContext signContext = new DOMSignContext(privateKey, token);
	    signContext.putNamespacePrefix(javax.xml.crypto.dsig.XMLSignature.XMLNS, "ds");
	    sig.sign(signContext);
	    
	    // Insert signature node in document
	    Element sigE = (Element) XPathAPI.selectSingleNode(signContext.getParent(), "//ds:Signature");
	    
	    Node keyE = XPathAPI.selectSingleNode(sigE, "//ds:KeyInfo", sigE);
	    token.appendChild(doc.createTextNode(XPathAPI.selectSingleNode(keyE, "//ds:X509Certificate", keyE).getFirstChild().getNodeValue()));
	    keyE.removeChild(XPathAPI.selectSingleNode(keyE, "//ds:X509Data", keyE));
	    NodeList chl = keyE.getChildNodes();

	    for (int i = 0; i < chl.getLength(); i++) {
	      keyE.removeChild(chl.item(i));
	    }

	    Node str = keyE.appendChild(doc.createElementNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "wsse:SecurityTokenReference"));
	    Element strRef = (Element) str.appendChild(doc.createElementNS("http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-wssecurity-secext-1.0.xsd", "wsse:Reference"));

	    strRef.setAttribute("ValueType", "http://docs.oasis-open.org/wss/2004/01/oasis-200401-wss-x509-token-profile-1.0#X509v3");
	    strRef.setAttribute("URI", "#CertId");
	    header.getSecurityHeader().appendChild(sigE);
	    
	    String mes = org.apache.ws.security.util.XMLUtils.PrettyDocumentToString(doc);
	    System.out.println( mes );

        Array.writeFile(inSOAPFile + ".signed.uri.xml", mes.getBytes("utf-8"));

        if (checkOnline) {

	        SignatureManager signatureManager =
				new SOAPXMLSignatureManager_1_6_3(SpecUtility.DEFAULT_CRYPTO_PROPERTIES, 
					SpecUtility.DEFAULT_ALIAS, SpecUtility.DEFAULT_PASSWORD, SpecUtility.DEFAULT_PASSWORD);

            System.out.println( signatureManager.verifyDoc(doc, true) );
	    
	        SignatureToolService sts = new SignatureToolServiceLocator();
		    SignatureTool st = sts.getSignatureToolPort(new URL(smevService));

            VerifySignatureRequestType vsrt = new VerifySignatureRequestType(mes, false,
			    "http://smev.gosuslugi.ru/actors/smev");
		    VerifySignatureResponseType r = st.verifySignature(vsrt);

            System.out.println(r.getError().getErrorMessage());
        }
	}

}
