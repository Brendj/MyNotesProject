/**
 * SignatureToolServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package wss4j.gosuslugi.smev.SignatureTool;

public class SignatureToolServiceLocator extends org.apache.axis.client.Service implements wss4j.gosuslugi.smev.SignatureTool.SignatureToolService {

    public SignatureToolServiceLocator() {
    }


    public SignatureToolServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public SignatureToolServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for SignatureToolPort
    private java.lang.String SignatureToolPort_address = "http://d00smevapp01:9999/gateway/services/SID0003038";

    public java.lang.String getSignatureToolPortAddress() {
        return SignatureToolPort_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String SignatureToolPortWSDDServiceName = "SignatureToolPort";

    public java.lang.String getSignatureToolPortWSDDServiceName() {
        return SignatureToolPortWSDDServiceName;
    }

    public void setSignatureToolPortWSDDServiceName(java.lang.String name) {
        SignatureToolPortWSDDServiceName = name;
    }

    public wss4j.gosuslugi.smev.SignatureTool.SignatureTool getSignatureToolPort() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(SignatureToolPort_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getSignatureToolPort(endpoint);
    }

    public wss4j.gosuslugi.smev.SignatureTool.SignatureTool getSignatureToolPort(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            wss4j.gosuslugi.smev.SignatureTool.SignatureToolBindingStub _stub = new wss4j.gosuslugi.smev.SignatureTool.SignatureToolBindingStub(portAddress, this);
            _stub.setPortName(getSignatureToolPortWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setSignatureToolPortEndpointAddress(java.lang.String address) {
        SignatureToolPort_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (wss4j.gosuslugi.smev.SignatureTool.SignatureTool.class.isAssignableFrom(serviceEndpointInterface)) {
                wss4j.gosuslugi.smev.SignatureTool.SignatureToolBindingStub _stub = new wss4j.gosuslugi.smev.SignatureTool.SignatureToolBindingStub(new java.net.URL(SignatureToolPort_address), this);
                _stub.setPortName(getSignatureToolPortWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("SignatureToolPort".equals(inputPortName)) {
            return getSignatureToolPort();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://smev.gosuslugi.ru/SignatureTool/", "SignatureToolService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://smev.gosuslugi.ru/SignatureTool/", "SignatureToolPort"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("SignatureToolPort".equals(portName)) {
            setSignatureToolPortEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
