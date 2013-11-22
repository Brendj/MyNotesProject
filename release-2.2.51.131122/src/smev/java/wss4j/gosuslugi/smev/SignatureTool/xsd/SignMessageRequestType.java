/**
 * SignMessageRequestType.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package wss4j.gosuslugi.smev.SignatureTool.xsd;

public class SignMessageRequestType  implements java.io.Serializable {
    /* Сообщение для подписи */
    private java.lang.String message;

    /* атрибут ws-security:actor для секции ws:security
     * 						 в которую будет добавлена подпись */
    private java.lang.String actor;

    /* Элементы для подписи */
    private wss4j.gosuslugi.smev.SignatureTool.xsd.Part4SignType[] part4Sign;

    /* Алиас сертификата. */
    private java.lang.String certAlias;

    /* Алиас приватного ключа. */
    private java.lang.String privateKeyAlias;

    /* Пароль для доступа к приватному ключу */
    private java.lang.String privateKeyPassword;

    public SignMessageRequestType() {
    }

    public SignMessageRequestType(
           java.lang.String message,
           java.lang.String actor,
           wss4j.gosuslugi.smev.SignatureTool.xsd.Part4SignType[] part4Sign,
           java.lang.String certAlias,
           java.lang.String privateKeyAlias,
           java.lang.String privateKeyPassword) {
           this.message = message;
           this.actor = actor;
           this.part4Sign = part4Sign;
           this.certAlias = certAlias;
           this.privateKeyAlias = privateKeyAlias;
           this.privateKeyPassword = privateKeyPassword;
    }


    /**
     * Gets the message value for this SignMessageRequestType.
     * 
     * @return message   * Сообщение для подписи
     */
    public java.lang.String getMessage() {
        return message;
    }


    /**
     * Sets the message value for this SignMessageRequestType.
     * 
     * @param message   * Сообщение для подписи
     */
    public void setMessage(java.lang.String message) {
        this.message = message;
    }


    /**
     * Gets the actor value for this SignMessageRequestType.
     * 
     * @return actor   * атрибут ws-security:actor для секции ws:security
     * 						 в которую будет добавлена подпись
     */
    public java.lang.String getActor() {
        return actor;
    }


    /**
     * Sets the actor value for this SignMessageRequestType.
     * 
     * @param actor   * атрибут ws-security:actor для секции ws:security
     * 						 в которую будет добавлена подпись
     */
    public void setActor(java.lang.String actor) {
        this.actor = actor;
    }


    /**
     * Gets the part4Sign value for this SignMessageRequestType.
     * 
     * @return part4Sign   * Элементы для подписи
     */
    public wss4j.gosuslugi.smev.SignatureTool.xsd.Part4SignType[] getPart4Sign() {
        return part4Sign;
    }


    /**
     * Sets the part4Sign value for this SignMessageRequestType.
     * 
     * @param part4Sign   * Элементы для подписи
     */
    public void setPart4Sign(wss4j.gosuslugi.smev.SignatureTool.xsd.Part4SignType[] part4Sign) {
        this.part4Sign = part4Sign;
    }

    public wss4j.gosuslugi.smev.SignatureTool.xsd.Part4SignType getPart4Sign(int i) {
        return this.part4Sign[i];
    }

    public void setPart4Sign(int i, wss4j.gosuslugi.smev.SignatureTool.xsd.Part4SignType _value) {
        this.part4Sign[i] = _value;
    }


    /**
     * Gets the certAlias value for this SignMessageRequestType.
     * 
     * @return certAlias   * Алиас сертификата.
     */
    public java.lang.String getCertAlias() {
        return certAlias;
    }


    /**
     * Sets the certAlias value for this SignMessageRequestType.
     * 
     * @param certAlias   * Алиас сертификата.
     */
    public void setCertAlias(java.lang.String certAlias) {
        this.certAlias = certAlias;
    }


    /**
     * Gets the privateKeyAlias value for this SignMessageRequestType.
     * 
     * @return privateKeyAlias   * Алиас приватного ключа.
     */
    public java.lang.String getPrivateKeyAlias() {
        return privateKeyAlias;
    }


    /**
     * Sets the privateKeyAlias value for this SignMessageRequestType.
     * 
     * @param privateKeyAlias   * Алиас приватного ключа.
     */
    public void setPrivateKeyAlias(java.lang.String privateKeyAlias) {
        this.privateKeyAlias = privateKeyAlias;
    }


    /**
     * Gets the privateKeyPassword value for this SignMessageRequestType.
     * 
     * @return privateKeyPassword   * Пароль для доступа к приватному ключу
     */
    public java.lang.String getPrivateKeyPassword() {
        return privateKeyPassword;
    }


    /**
     * Sets the privateKeyPassword value for this SignMessageRequestType.
     * 
     * @param privateKeyPassword   * Пароль для доступа к приватному ключу
     */
    public void setPrivateKeyPassword(java.lang.String privateKeyPassword) {
        this.privateKeyPassword = privateKeyPassword;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        if (!(obj instanceof SignMessageRequestType)) return false;
        SignMessageRequestType other = (SignMessageRequestType) obj;
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
            ((this.actor==null && other.getActor()==null) || 
             (this.actor!=null &&
              this.actor.equals(other.getActor()))) &&
            ((this.part4Sign==null && other.getPart4Sign()==null) || 
             (this.part4Sign!=null &&
              java.util.Arrays.equals(this.part4Sign, other.getPart4Sign()))) &&
            ((this.certAlias==null && other.getCertAlias()==null) || 
             (this.certAlias!=null &&
              this.certAlias.equals(other.getCertAlias()))) &&
            ((this.privateKeyAlias==null && other.getPrivateKeyAlias()==null) || 
             (this.privateKeyAlias!=null &&
              this.privateKeyAlias.equals(other.getPrivateKeyAlias()))) &&
            ((this.privateKeyPassword==null && other.getPrivateKeyPassword()==null) || 
             (this.privateKeyPassword!=null &&
              this.privateKeyPassword.equals(other.getPrivateKeyPassword())));
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
        if (getActor() != null) {
            _hashCode += getActor().hashCode();
        }
        if (getPart4Sign() != null) {
            for (int i=0;
                 i<java.lang.reflect.Array.getLength(getPart4Sign());
                 i++) {
                java.lang.Object obj = java.lang.reflect.Array.get(getPart4Sign(), i);
                if (obj != null &&
                    !obj.getClass().isArray()) {
                    _hashCode += obj.hashCode();
                }
            }
        }
        if (getCertAlias() != null) {
            _hashCode += getCertAlias().hashCode();
        }
        if (getPrivateKeyAlias() != null) {
            _hashCode += getPrivateKeyAlias().hashCode();
        }
        if (getPrivateKeyPassword() != null) {
            _hashCode += getPrivateKeyPassword().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(SignMessageRequestType.class, true);

    static {
        typeDesc.setXmlType(new javax.xml.namespace.QName("http://smev.gosuslugi.ru/SignatureTool/xsd/", "SignMessageRequestType"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("message");
        elemField.setXmlName(new javax.xml.namespace.QName("", "message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("actor");
        elemField.setXmlName(new javax.xml.namespace.QName("", "actor"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("part4Sign");
        elemField.setXmlName(new javax.xml.namespace.QName("", "part4Sign"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://smev.gosuslugi.ru/SignatureTool/xsd/", "Part4SignType"));
        elemField.setNillable(false);
        elemField.setMaxOccursUnbounded(true);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("certAlias");
        elemField.setXmlName(new javax.xml.namespace.QName("", "certAlias"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("privateKeyAlias");
        elemField.setXmlName(new javax.xml.namespace.QName("", "privateKeyAlias"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("privateKeyPassword");
        elemField.setXmlName(new javax.xml.namespace.QName("", "privateKeyPassword"));
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
