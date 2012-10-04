/**
 * VerifySignatureRequestType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package wss4j.gosuslugi.smev.SignatureTool.xsd;

public class VerifySignatureRequestType  implements java.io.Serializable {
    /* Сообщение для проверки целостности */
    private java.lang.String message;

    /* атрибут указывает проверять ли сертификат на валидность */
    private boolean isCertCheck;

    /* атрибут ws-security:actor для проверки */
    private java.lang.String actor;

    public VerifySignatureRequestType() {
    }

    public VerifySignatureRequestType(
           java.lang.String message,
           boolean isCertCheck,
           java.lang.String actor) {
           this.message = message;
           this.isCertCheck = isCertCheck;
           this.actor = actor;
    }


    /**
     * Gets the message value for this VerifySignatureRequestType.
     * 
     * @return message   * Сообщение для проверки целостности
     */
    public java.lang.String getMessage() {
        return message;
    }


    /**
     * Sets the message value for this VerifySignatureRequestType.
     * 
     * @param message   * Сообщение для проверки целостности
     */
    public void setMessage(java.lang.String message) {
        this.message = message;
    }


    /**
     * Gets the isCertCheck value for this VerifySignatureRequestType.
     * 
     * @return isCertCheck   * атрибут указывает проверять ли сертификат на валидность
     */
    public boolean isIsCertCheck() {
        return isCertCheck;
    }


    /**
     * Sets the isCertCheck value for this VerifySignatureRequestType.
     * 
     * @param isCertCheck   * атрибут указывает проверять ли сертификат на валидность
     */
    public void setIsCertCheck(boolean isCertCheck) {
        this.isCertCheck = isCertCheck;
    }


    /**
     * Gets the actor value for this VerifySignatureRequestType.
     * 
     * @return actor   * атрибут ws-security:actor для проверки
     */
    public java.lang.String getActor() {
        return actor;
    }


    /**
     * Sets the actor value for this VerifySignatureRequestType.
     * 
     * @param actor   * атрибут ws-security:actor для проверки
     */
    public void setActor(java.lang.String actor) {
        this.actor = actor;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof VerifySignatureRequestType)) return false;
        VerifySignatureRequestType other = (VerifySignatureRequestType) obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            ((this.message==null && other.getMessage()==null) || 
             (this.message!=null &&
              this.message.equals(other.getMessage()))) &&
            this.isCertCheck == other.isIsCertCheck() &&
            ((this.actor==null && other.getActor()==null) || 
             (this.actor!=null &&
              this.actor.equals(other.getActor())));
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
        if (getMessage() != null) {
            _hashCode += getMessage().hashCode();
        }
        _hashCode += (isIsCertCheck() ? Boolean.TRUE : Boolean.FALSE).hashCode();
        if (getActor() != null) {
            _hashCode += getActor().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(VerifySignatureRequestType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://smev.gosuslugi.ru/SignatureTool/xsd/", "VerifySignatureRequestType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("message");
        elemField.setXmlName(new javax.xml.namespace.QName("", "message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("isCertCheck");
        elemField.setXmlName(new javax.xml.namespace.QName("", "isCertCheck"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("actor");
        elemField.setXmlName(new javax.xml.namespace.QName("", "actor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
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
