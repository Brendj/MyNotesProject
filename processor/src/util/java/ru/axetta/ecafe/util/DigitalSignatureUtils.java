/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.CharEncoding;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.dom.DOMValidateContext;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Collections;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 22.07.2009
 * Time: 12:36:42
 * To change this template use File | Settings | File Templates.
 */
public class DigitalSignatureUtils {

    private static final String SIGNATURE_ALGORITHM = "RSA";
    private static final int KEY_SIZE = 1024;

    private DigitalSignatureUtils() {
    }

    public static KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(SIGNATURE_ALGORITHM);
        keyPairGenerator.initialize(KEY_SIZE, new SecureRandom());
        return keyPairGenerator.generateKeyPair();
    }

    public static String convertToString(Key key) throws Exception {
        return new String(Base64.encodeBase64(key.getEncoded()), CharEncoding.US_ASCII);
    }

    public static PublicKey convertToPublicKey(byte[] publicKeyData) throws Exception {
        X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(publicKeyData);
        KeyFactory keyFactory = KeyFactory.getInstance(SIGNATURE_ALGORITHM);
        return keyFactory.generatePublic(publicKeySpec);
    }

    public static PrivateKey convertToPrivateKey(byte[] privateKeyData) throws Exception {
        PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(privateKeyData);
        KeyFactory keyFactory = KeyFactory.getInstance(SIGNATURE_ALGORITHM);
        return keyFactory.generatePrivate(privateKeySpec);
    }

    public static PublicKey convertToPublicKey(String publicKeyData) throws Exception {
        return convertToPublicKey(Base64.decodeBase64(publicKeyData.getBytes(CharEncoding.US_ASCII)));
    }

    public static PrivateKey convertToPrivateKey(String publicKeyData) throws Exception {
        return convertToPrivateKey(Base64.decodeBase64(publicKeyData.getBytes(CharEncoding.US_ASCII)));
    }

    public static void sign(PrivateKey privateKey, Document document) throws Exception {
        // Create sign context
        DOMSignContext signContext = new DOMSignContext(privateKey, document.getDocumentElement());
        String providerName = System.getProperty("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");
        XMLSignatureFactory signatureFactory = XMLSignatureFactory
                .getInstance("DOM", (Provider) Class.forName(providerName).newInstance());
        // Specify digest method
        DigestMethod digestMethod = signatureFactory.newDigestMethod(DigestMethod.SHA1, null);
        Transform transform = signatureFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null);
        // Create referense to sign
        Reference reference = signatureFactory
                .newReference("", digestMethod, Collections.singletonList(transform), null, null);
        // Specify canonicalization method
        CanonicalizationMethod canonicalizationMethod = signatureFactory
                .newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null);
        // Specify signature method
        SignatureMethod signatureMethod = signatureFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null);
        SignedInfo signedInfo = signatureFactory
                .newSignedInfo(canonicalizationMethod, signatureMethod, Collections.singletonList(reference));
        // Create signature
        XMLSignature signature = signatureFactory.newXMLSignature(signedInfo, null);
        // Perform sign in
        signature.sign(signContext);
    }

    public static boolean verify(PublicKey publicKey, Document document) throws Exception {
        // Find single Signature element
        Node signatureNode = findSignatureNode(document);
        // Create validation context
        DOMValidateContext validateContext = new DOMValidateContext(publicKey, signatureNode);
        // Unmarshaling the XML Signature
        String providerName = System.getProperty("jsr105Provider", "org.jcp.xml.dsig.internal.dom.XMLDSigRI");
        XMLSignatureFactory signatureFactory = XMLSignatureFactory
                .getInstance("DOM", (Provider) Class.forName(providerName).newInstance());
        XMLSignature signature = signatureFactory.unmarshalXMLSignature(validateContext);
        // Perform validation
        return signature.validate(validateContext);
    }

    private static Node findSignatureNode(Document document) throws Exception {
        NodeList signatureNodes = document.getElementsByTagNameNS(XMLSignature.XMLNS, "Signature");
        if (null == signatureNodes || 1 != signatureNodes.getLength()) {
            return null;
        }
        return signatureNodes.item(0);
    }

    public static boolean hasSignature(Document document) throws Exception {
        return null != findSignatureNode(document);
    }

}