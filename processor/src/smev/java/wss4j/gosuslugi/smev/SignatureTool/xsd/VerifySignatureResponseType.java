/**
 * VerifySignatureResponseType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package wss4j.gosuslugi.smev.SignatureTool.xsd;

public class VerifySignatureResponseType  implements java.io.Serializable {
    /* Результат выполнения операции */
    private wss4j.nvg.idecs.identityservice.ws.types.Error error;

    /* Сертификат в кодировке Base64 */
    private java.lang.String certificate;

    public VerifySignatureResponseType() {
    }

    public VerifySignatureResponseType(
           wss4j.nvg.idecs.identityservice.ws.types.Error error,
           java.lang.String certificate) {
           this.error = error;
           this.certificate = certificate;
    }


    /**
     * Gets the error value for this VerifySignatureResponseType.
     * 
     * @return error   * Результат выполнения операции
     */
    public wss4j.nvg.idecs.identityservice.ws.types.Error getError() {
        return error;
    }


    /**
     * Sets the error value for this VerifySignatureResponseType.
     * 
     * @param error   * Результат выполнения операции
     */
    public void setError(wss4j.nvg.idecs.identityservice.ws.types.Error error) {
        this.error = error;
    }


    /**
     * Gets the certificate value for this VerifySignatureResponseType.
     * 
     * @return certificate   * Сертификат в кодировке Base64
     */
    public java.lang.String getCertificate() {
        return certificate;
    }


    /**
     * Sets the certificate value for this VerifySignatureResponseType.
     * 
     * @param certificate   * Сертификат в кодировке Base64
     */
    public void setCertificate(java.lang.String certificate) {
        this.certificate = certificate;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VerifySignatureResponseType)) return false;
        VerifySignatureResponseType other = (VerifySignatureResponseType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.error==null && other.getError()==null) || 
             (this.error!=null &&
              this.error.equals(other.getError()))) &&
            ((this.certificate==null && other.getCertificate()==null) || 
             (this.certificate!=null &&
              this.certificate.equals(other.getCertificate())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        if (__hashCodeCalc) {
            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        if (getError() != null) {
            _hashCode += getError().hashCode();
        }
        if (getCertificate() != null) {
            _hashCode += getCertificate().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VerifySignatureResponseType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://smev.gosuslugi.ru/SignatureTool/xsd/", "VerifySignatureResponseType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("error");
        elemField.setXmlName(new javax.xml.namespace.QName("", "error"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://idecs.nvg.ru/identityservice/ws/types/", "Error"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("certificate");
        elemField.setXmlName(new javax.xml.namespace.QName("", "certificate"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}
