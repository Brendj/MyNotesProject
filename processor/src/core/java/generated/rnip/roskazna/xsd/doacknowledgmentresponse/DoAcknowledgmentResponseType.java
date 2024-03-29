//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vhudson-jaxb-ri-2.1-325 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2013.01.14 at 07:18:21 PM MSK 
//


package generated.rnip.roskazna.xsd.doacknowledgmentresponse;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import generated.rnip.w3._2000._09.xmldsig_.SignatureType;
import generated.rnip.roskazna.xsd.quittance.QuittanceType;
import generated.rnip.roskazna.xsd.responsetemplate.ResponseTemplate;


/**
 * <p>Java class for DoAcknowledgmentResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DoAcknowledgmentResponseType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://roskazna.ru/xsd/ResponseTemplate}ResponseTemplate">
 *       &lt;sequence>
 *         &lt;element name="Quittances" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Quittance" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;extension base="{http://roskazna.ru/xsd/Quittance}QuittanceType">
 *                         &lt;/extension>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}Signature" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DoAcknowledgmentResponseType", propOrder = {
    "quittances",
    "signature"
})
public class DoAcknowledgmentResponseType
    extends ResponseTemplate
{

    @XmlElement(name = "Quittances")
    protected DoAcknowledgmentResponseType.Quittances quittances;
    @XmlElement(name = "Signature", namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected SignatureType signature;

    /**
     * Gets the value of the quittances property.
     * 
     * @return
     *     possible object is
     *     {@link DoAcknowledgmentResponseType.Quittances }
     *     
     */
    public DoAcknowledgmentResponseType.Quittances getQuittances() {
        return quittances;
    }

    /**
     * Sets the value of the quittances property.
     * 
     * @param value
     *     allowed object is
     *     {@link DoAcknowledgmentResponseType.Quittances }
     *     
     */
    public void setQuittances(DoAcknowledgmentResponseType.Quittances value) {
        this.quittances = value;
    }

    /**
     * Gets the value of the signature property.
     * 
     * @return
     *     possible object is
     *     {@link SignatureType }
     *     
     */
    public SignatureType getSignature() {
        return signature;
    }

    /**
     * Sets the value of the signature property.
     * 
     * @param value
     *     allowed object is
     *     {@link SignatureType }
     *     
     */
    public void setSignature(SignatureType value) {
        this.signature = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="Quittance" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;extension base="{http://roskazna.ru/xsd/Quittance}QuittanceType">
     *               &lt;/extension>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "quittance"
    })
    public static class Quittances {

        @XmlElement(name = "Quittance", required = true)
        protected List<DoAcknowledgmentResponseType.Quittances.Quittance> quittance;

        /**
         * Gets the value of the quittance property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the quittance property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getQuittance().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link DoAcknowledgmentResponseType.Quittances.Quittance }
         * 
         * 
         */
        public List<DoAcknowledgmentResponseType.Quittances.Quittance> getQuittance() {
            if (quittance == null) {
                quittance = new ArrayList<DoAcknowledgmentResponseType.Quittances.Quittance>();
            }
            return this.quittance;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;extension base="{http://roskazna.ru/xsd/Quittance}QuittanceType">
         *     &lt;/extension>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class Quittance
            extends QuittanceType
        {


        }

    }

}
