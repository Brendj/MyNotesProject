/**
 * SignatureTool.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package wss4j.gosuslugi.smev.SignatureTool;

public interface SignatureTool extends java.rmi.Remote {
    public wss4j.gosuslugi.smev.SignatureTool.xsd.VerifySignatureResponseType verifySignature(wss4j.gosuslugi.smev.SignatureTool.xsd.VerifySignatureRequestType parameters) throws java.rmi.RemoteException;
    public wss4j.gosuslugi.smev.SignatureTool.xsd.SignMessageResponseType signMessage(wss4j.gosuslugi.smev.SignatureTool.xsd.SignMessageRequestType parameters) throws java.rmi.RemoteException;
}
