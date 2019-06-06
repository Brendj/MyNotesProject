
package generated.ru.mos.rnip.xsd.services.forced_acknowledgement._2_1;

import generated.ru.mos.rnip.xsd.common._2_1.RequestType;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Common/2.1.1}RequestType">
 *       &lt;choice>
 *         &lt;element name="Reconcile" maxOccurs="100">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;sequence>
 *                     &lt;element name="PaymentId" type="{http://rnip.mos.ru/xsd/Common/2.1.1}PaymentIdType" maxOccurs="100"/>
 *                   &lt;/sequence>
 *                   &lt;element name="PaymentNotLoaded" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *                 &lt;/choice>
 *                 &lt;attribute name="supplierBillId" use="required">
 *                   &lt;simpleType>
 *                     &lt;union memberTypes=" {http://rnip.mos.ru/xsd/Common/2.1.1}SupplierBillIDType">
 *                       &lt;simpleType>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                           &lt;pattern value="\d{15}"/>
 *                         &lt;/restriction>
 *                       &lt;/simpleType>
 *                     &lt;/union>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="Id" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="AnnulmentReconcile" maxOccurs="100">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;sequence>
 *                     &lt;element name="PaymentId" type="{http://rnip.mos.ru/xsd/Common/2.1.1}PaymentIdType" maxOccurs="100"/>
 *                   &lt;/sequence>
 *                   &lt;element name="PaymentNotLoaded" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *                 &lt;/choice>
 *                 &lt;attribute name="supplierBillId" use="required">
 *                   &lt;simpleType>
 *                     &lt;union memberTypes=" {http://rnip.mos.ru/xsd/Common/2.1.1}SupplierBillIDType">
 *                       &lt;simpleType>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                           &lt;pattern value="\d{15}"/>
 *                         &lt;/restriction>
 *                       &lt;/simpleType>
 *                     &lt;/union>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="Id" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ServiceProvided">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="PaymentId" type="{http://rnip.mos.ru/xsd/Common/2.1.1}PaymentIdType" maxOccurs="100"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="AnnulmentServiceProvided">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="PaymentId" type="{http://rnip.mos.ru/xsd/Common/2.1.1}PaymentIdType" maxOccurs="100"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/choice>
 *       &lt;attribute name="originatorId" type="{http://rnip.mos.ru/xsd/Common/2.1.1}URNType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "reconcile",
    "annulmentReconcile",
    "serviceProvided",
    "annulmentServiceProvided"
})
@XmlRootElement(name = "ForcedAcknowledgementRequest")
public class ForcedAcknowledgementRequest
    extends RequestType
{

    @XmlElement(name = "Reconcile")
    protected List<ForcedAcknowledgementRequest.Reconcile> reconcile;
    @XmlElement(name = "AnnulmentReconcile")
    protected List<ForcedAcknowledgementRequest.AnnulmentReconcile> annulmentReconcile;
    @XmlElement(name = "ServiceProvided")
    protected ForcedAcknowledgementRequest.ServiceProvided serviceProvided;
    @XmlElement(name = "AnnulmentServiceProvided")
    protected ForcedAcknowledgementRequest.AnnulmentServiceProvided annulmentServiceProvided;
    @XmlAttribute(name = "originatorId")
    protected String originatorId;

    /**
     * Gets the value of the reconcile property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the reconcile property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getReconcile().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ForcedAcknowledgementRequest.Reconcile }
     * 
     * 
     */
    public List<ForcedAcknowledgementRequest.Reconcile> getReconcile() {
        if (reconcile == null) {
            reconcile = new ArrayList<ForcedAcknowledgementRequest.Reconcile>();
        }
        return this.reconcile;
    }

    /**
     * Gets the value of the annulmentReconcile property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the annulmentReconcile property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAnnulmentReconcile().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ForcedAcknowledgementRequest.AnnulmentReconcile }
     * 
     * 
     */
    public List<ForcedAcknowledgementRequest.AnnulmentReconcile> getAnnulmentReconcile() {
        if (annulmentReconcile == null) {
            annulmentReconcile = new ArrayList<ForcedAcknowledgementRequest.AnnulmentReconcile>();
        }
        return this.annulmentReconcile;
    }

    /**
     * Gets the value of the serviceProvided property.
     * 
     * @return
     *     possible object is
     *     {@link ForcedAcknowledgementRequest.ServiceProvided }
     *     
     */
    public ForcedAcknowledgementRequest.ServiceProvided getServiceProvided() {
        return serviceProvided;
    }

    /**
     * Sets the value of the serviceProvided property.
     * 
     * @param value
     *     allowed object is
     *     {@link ForcedAcknowledgementRequest.ServiceProvided }
     *     
     */
    public void setServiceProvided(ForcedAcknowledgementRequest.ServiceProvided value) {
        this.serviceProvided = value;
    }

    /**
     * Gets the value of the annulmentServiceProvided property.
     * 
     * @return
     *     possible object is
     *     {@link ForcedAcknowledgementRequest.AnnulmentServiceProvided }
     *     
     */
    public ForcedAcknowledgementRequest.AnnulmentServiceProvided getAnnulmentServiceProvided() {
        return annulmentServiceProvided;
    }

    /**
     * Sets the value of the annulmentServiceProvided property.
     * 
     * @param value
     *     allowed object is
     *     {@link ForcedAcknowledgementRequest.AnnulmentServiceProvided }
     *     
     */
    public void setAnnulmentServiceProvided(ForcedAcknowledgementRequest.AnnulmentServiceProvided value) {
        this.annulmentServiceProvided = value;
    }

    /**
     * Gets the value of the originatorId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOriginatorId() {
        return originatorId;
    }

    /**
     * Sets the value of the originatorId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOriginatorId(String value) {
        this.originatorId = value;
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
     *         &lt;sequence>
     *           &lt;element name="PaymentId" type="{http://rnip.mos.ru/xsd/Common/2.1.1}PaymentIdType" maxOccurs="100"/>
     *         &lt;/sequence>
     *         &lt;element name="PaymentNotLoaded" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
     *       &lt;/choice>
     *       &lt;attribute name="supplierBillId" use="required">
     *         &lt;simpleType>
     *           &lt;union memberTypes=" {http://rnip.mos.ru/xsd/Common/2.1.1}SupplierBillIDType">
     *             &lt;simpleType>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                 &lt;pattern value="\d{15}"/>
     *               &lt;/restriction>
     *             &lt;/simpleType>
     *           &lt;/union>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="Id" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "paymentId",
        "paymentNotLoaded"
    })
    public static class AnnulmentReconcile {

        @XmlElement(name = "PaymentId")
        protected List<String> paymentId;
        @XmlElement(name = "PaymentNotLoaded")
        protected Boolean paymentNotLoaded;
        @XmlAttribute(name = "supplierBillId", required = true)
        protected String supplierBillId;
        @XmlAttribute(name = "Id", required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        protected String id;

        /**
         * Gets the value of the paymentId property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the paymentId property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPaymentId().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getPaymentId() {
            if (paymentId == null) {
                paymentId = new ArrayList<String>();
            }
            return this.paymentId;
        }

        /**
         * Gets the value of the paymentNotLoaded property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isPaymentNotLoaded() {
            return paymentNotLoaded;
        }

        /**
         * Sets the value of the paymentNotLoaded property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setPaymentNotLoaded(Boolean value) {
            this.paymentNotLoaded = value;
        }

        /**
         * Gets the value of the supplierBillId property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSupplierBillId() {
            return supplierBillId;
        }

        /**
         * Sets the value of the supplierBillId property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSupplierBillId(String value) {
            this.supplierBillId = value;
        }

        /**
         * Gets the value of the id property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the value of the id property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setId(String value) {
            this.id = value;
        }

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
     *         &lt;element name="PaymentId" type="{http://rnip.mos.ru/xsd/Common/2.1.1}PaymentIdType" maxOccurs="100"/>
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
        "paymentId"
    })
    public static class AnnulmentServiceProvided {

        @XmlElement(name = "PaymentId", required = true)
        protected List<String> paymentId;

        /**
         * Gets the value of the paymentId property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the paymentId property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPaymentId().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getPaymentId() {
            if (paymentId == null) {
                paymentId = new ArrayList<String>();
            }
            return this.paymentId;
        }

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
     *         &lt;sequence>
     *           &lt;element name="PaymentId" type="{http://rnip.mos.ru/xsd/Common/2.1.1}PaymentIdType" maxOccurs="100"/>
     *         &lt;/sequence>
     *         &lt;element name="PaymentNotLoaded" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
     *       &lt;/choice>
     *       &lt;attribute name="supplierBillId" use="required">
     *         &lt;simpleType>
     *           &lt;union memberTypes=" {http://rnip.mos.ru/xsd/Common/2.1.1}SupplierBillIDType">
     *             &lt;simpleType>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                 &lt;pattern value="\d{15}"/>
     *               &lt;/restriction>
     *             &lt;/simpleType>
     *           &lt;/union>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="Id" use="required" type="{http://www.w3.org/2001/XMLSchema}ID" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "paymentId",
        "paymentNotLoaded"
    })
    public static class Reconcile {

        @XmlElement(name = "PaymentId")
        protected List<String> paymentId;
        @XmlElement(name = "PaymentNotLoaded")
        protected Boolean paymentNotLoaded;
        @XmlAttribute(name = "supplierBillId", required = true)
        protected String supplierBillId;
        @XmlAttribute(name = "Id", required = true)
        @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
        @XmlID
        @XmlSchemaType(name = "ID")
        protected String id;

        /**
         * Gets the value of the paymentId property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the paymentId property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPaymentId().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getPaymentId() {
            if (paymentId == null) {
                paymentId = new ArrayList<String>();
            }
            return this.paymentId;
        }

        /**
         * Gets the value of the paymentNotLoaded property.
         * 
         * @return
         *     possible object is
         *     {@link Boolean }
         *     
         */
        public Boolean isPaymentNotLoaded() {
            return paymentNotLoaded;
        }

        /**
         * Sets the value of the paymentNotLoaded property.
         * 
         * @param value
         *     allowed object is
         *     {@link Boolean }
         *     
         */
        public void setPaymentNotLoaded(Boolean value) {
            this.paymentNotLoaded = value;
        }

        /**
         * Gets the value of the supplierBillId property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSupplierBillId() {
            return supplierBillId;
        }

        /**
         * Sets the value of the supplierBillId property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSupplierBillId(String value) {
            this.supplierBillId = value;
        }

        /**
         * Gets the value of the id property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getId() {
            return id;
        }

        /**
         * Sets the value of the id property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setId(String value) {
            this.id = value;
        }

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
     *         &lt;element name="PaymentId" type="{http://rnip.mos.ru/xsd/Common/2.1.1}PaymentIdType" maxOccurs="100"/>
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
        "paymentId"
    })
    public static class ServiceProvided {

        @XmlElement(name = "PaymentId", required = true)
        protected List<String> paymentId;

        /**
         * Gets the value of the paymentId property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the paymentId property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPaymentId().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link String }
         * 
         * 
         */
        public List<String> getPaymentId() {
            if (paymentId == null) {
                paymentId = new ArrayList<String>();
            }
            return this.paymentId;
        }

    }

}
