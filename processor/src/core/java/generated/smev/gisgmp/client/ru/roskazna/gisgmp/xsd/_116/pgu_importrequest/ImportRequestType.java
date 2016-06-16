
package generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.pgu_importrequest;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.charge.ChargeType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.paymentinfo.PaymentType;


/**
 * ������ �� ������ ��������
 * 
 * <p>Java class for ImportRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ImportRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Package">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Document" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;choice>
 *                             &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Charge}Charge"/>
 *                             &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/PaymentInfo}FinalPayment"/>
 *                           &lt;/choice>
 *                           &lt;attribute name="originatorID" type="{http://roskazna.ru/gisgmp/xsd/116/Common}URNType" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
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
@XmlType(name = "ImportRequestType", propOrder = {
    "_package"
})
public class ImportRequestType {

    @XmlElement(name = "Package", required = true)
    protected ImportRequestType.Package _package;

    /**
     * Gets the value of the package property.
     * 
     * @return
     *     possible object is
     *     {@link ImportRequestType.Package }
     *     
     */
    public ImportRequestType.Package getPackage() {
        return _package;
    }

    /**
     * Sets the value of the package property.
     * 
     * @param value
     *     allowed object is
     *     {@link ImportRequestType.Package }
     *     
     */
    public void setPackage(ImportRequestType.Package value) {
        this._package = value;
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
     *         &lt;element name="Document" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;choice>
     *                   &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Charge}Charge"/>
     *                   &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/PaymentInfo}FinalPayment"/>
     *                 &lt;/choice>
     *                 &lt;attribute name="originatorID" type="{http://roskazna.ru/gisgmp/xsd/116/Common}URNType" />
     *               &lt;/restriction>
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
        "document"
    })
    public static class Package {

        @XmlElement(name = "Document", required = true)
        protected List<ImportRequestType.Package.Document> document;

        /**
         * Gets the value of the document property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the document property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getDocument().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ImportRequestType.Package.Document }
         * 
         * 
         */
        public List<ImportRequestType.Package.Document> getDocument() {
            if (document == null) {
                document = new ArrayList<ImportRequestType.Package.Document>();
            }
            return this.document;
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
         *       &lt;choice>
         *         &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Charge}Charge"/>
         *         &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/PaymentInfo}FinalPayment"/>
         *       &lt;/choice>
         *       &lt;attribute name="originatorID" type="{http://roskazna.ru/gisgmp/xsd/116/Common}URNType" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "charge",
            "finalPayment"
        })
        public static class Document {

            @XmlElement(name = "Charge", namespace = "http://roskazna.ru/gisgmp/xsd/116/Charge")
            protected ChargeType charge;
            @XmlElement(name = "FinalPayment", namespace = "http://roskazna.ru/gisgmp/xsd/116/PaymentInfo")
            protected PaymentType finalPayment;
            @XmlAttribute
            protected String originatorID;

            /**
             * Gets the value of the charge property.
             * 
             * @return
             *     possible object is
             *     {@link ChargeType }
             *     
             */
            public ChargeType getCharge() {
                return charge;
            }

            /**
             * Sets the value of the charge property.
             * 
             * @param value
             *     allowed object is
             *     {@link ChargeType }
             *     
             */
            public void setCharge(ChargeType value) {
                this.charge = value;
            }

            /**
             * Gets the value of the finalPayment property.
             * 
             * @return
             *     possible object is
             *     {@link PaymentType }
             *     
             */
            public PaymentType getFinalPayment() {
                return finalPayment;
            }

            /**
             * Sets the value of the finalPayment property.
             * 
             * @param value
             *     allowed object is
             *     {@link PaymentType }
             *     
             */
            public void setFinalPayment(PaymentType value) {
                this.finalPayment = value;
            }

            /**
             * Gets the value of the originatorID property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getOriginatorID() {
                return originatorID;
            }

            /**
             * Sets the value of the originatorID property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setOriginatorID(String value) {
                this.originatorID = value;
            }

        }

    }

}
