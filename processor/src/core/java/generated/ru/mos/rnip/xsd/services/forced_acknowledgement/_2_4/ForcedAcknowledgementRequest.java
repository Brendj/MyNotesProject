
package generated.ru.mos.rnip.xsd.services.forced_acknowledgement._2_4;

import generated.ru.mos.rnip.xsd.common._2_1.RequestType;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.math.BigInteger;
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
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Common/2.4.0}RequestType">
 *       &lt;choice>
 *         &lt;element name="Reconcile" maxOccurs="100">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice>
 *                   &lt;sequence>
 *                     &lt;element name="PaymentId" type="{http://rnip.mos.ru/xsd/Common/2.4.0}PaymentIdType" maxOccurs="100"/>
 *                   &lt;/sequence>
 *                   &lt;element name="PaymentNotLoaded">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>boolean">
 *                           &lt;attribute name="amountReconcile" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/choice>
 *                 &lt;attribute name="supplierBillId" use="required">
 *                   &lt;simpleType>
 *                     &lt;union memberTypes=" {http://rnip.mos.ru/xsd/Common/2.4.0}SupplierBillIDType">
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
 *                     &lt;element name="PaymentId" type="{http://rnip.mos.ru/xsd/Common/2.4.0}PaymentIdType" maxOccurs="100"/>
 *                   &lt;/sequence>
 *                   &lt;element name="PaymentNotLoaded">
 *                     &lt;complexType>
 *                       &lt;simpleContent>
 *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>boolean">
 *                           &lt;attribute name="reconcileID" type="{http://rnip.mos.ru/xsd/Common/2.4.0}reconcileIDType" />
 *                         &lt;/extension>
 *                       &lt;/simpleContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/choice>
 *                 &lt;attribute name="supplierBillId" use="required">
 *                   &lt;simpleType>
 *                     &lt;union memberTypes=" {http://rnip.mos.ru/xsd/Common/2.4.0}SupplierBillIDType">
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
 *                   &lt;element name="PaymentDataInfo" maxOccurs="100">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence minOccurs="0">
 *                             &lt;element name="ServiceData" type="{http://rnip.mos.ru/xsd/Common/2.4.0}ServiceDataType"/>
 *                           &lt;/sequence>
 *                           &lt;attribute name="paymentId" use="required" type="{http://rnip.mos.ru/xsd/Common/2.4.0}PaymentIdType" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
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
 *                   &lt;element name="PaymentDataID" maxOccurs="100">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="paymentId" use="required" type="{http://rnip.mos.ru/xsd/Common/2.4.0}PaymentIdType" />
 *                           &lt;attribute name="serviceDataID" type="{http://rnip.mos.ru/xsd/Common/2.4.0}serviceDataIDType" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/choice>
 *       &lt;attribute name="originatorId" type="{http://rnip.mos.ru/xsd/Common/2.4.0}URNType" />
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
@XmlRootElement(name = "ForcedAcknowledgementRequest", namespace = "urn://rnip.mos.ru/xsd/services/forced-acknowledgement/2.4.0")
public class ForcedAcknowledgementRequest
        extends RequestType
{

    @XmlElement(name = "Reconcile", namespace = "urn://rnip.mos.ru/xsd/services/forced-acknowledgement/2.4.0")
    protected List<Reconcile> reconcile;
    @XmlElement(name = "AnnulmentReconcile", namespace = "urn://rnip.mos.ru/xsd/services/forced-acknowledgement/2.4.0")
    protected List<AnnulmentReconcile> annulmentReconcile;
    @XmlElement(name = "ServiceProvided", namespace = "urn://rnip.mos.ru/xsd/services/forced-acknowledgement/2.4.0")
    protected ServiceProvided serviceProvided;
    @XmlElement(name = "AnnulmentServiceProvided", namespace = "urn://rnip.mos.ru/xsd/services/forced-acknowledgement/2.4.0")
    protected AnnulmentServiceProvided annulmentServiceProvided;
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
     * {@link Reconcile }
     *
     *
     */
    public List<Reconcile> getReconcile() {
        if (reconcile == null) {
            reconcile = new ArrayList<Reconcile>();
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
     * {@link AnnulmentReconcile }
     *
     *
     */
    public List<AnnulmentReconcile> getAnnulmentReconcile() {
        if (annulmentReconcile == null) {
            annulmentReconcile = new ArrayList<AnnulmentReconcile>();
        }
        return this.annulmentReconcile;
    }

    /**
     * Gets the value of the serviceProvided property.
     *
     * @return
     *     possible object is
     *     {@link ServiceProvided }
     *
     */
    public ServiceProvided getServiceProvided() {
        return serviceProvided;
    }

    /**
     * Sets the value of the serviceProvided property.
     *
     * @param value
     *     allowed object is
     *     {@link ServiceProvided }
     *
     */
    public void setServiceProvided(ServiceProvided value) {
        this.serviceProvided = value;
    }

    /**
     * Gets the value of the annulmentServiceProvided property.
     *
     * @return
     *     possible object is
     *     {@link AnnulmentServiceProvided }
     *
     */
    public AnnulmentServiceProvided getAnnulmentServiceProvided() {
        return annulmentServiceProvided;
    }

    /**
     * Sets the value of the annulmentServiceProvided property.
     *
     * @param value
     *     allowed object is
     *     {@link AnnulmentServiceProvided }
     *
     */
    public void setAnnulmentServiceProvided(AnnulmentServiceProvided value) {
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
     *           &lt;element name="PaymentId" type="{http://rnip.mos.ru/xsd/Common/2.4.0}PaymentIdType" maxOccurs="100"/>
     *         &lt;/sequence>
     *         &lt;element name="PaymentNotLoaded">
     *           &lt;complexType>
     *             &lt;simpleContent>
     *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>boolean">
     *                 &lt;attribute name="reconcileID" type="{http://rnip.mos.ru/xsd/Common/2.4.0}reconcileIDType" />
     *               &lt;/extension>
     *             &lt;/simpleContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/choice>
     *       &lt;attribute name="supplierBillId" use="required">
     *         &lt;simpleType>
     *           &lt;union memberTypes=" {http://rnip.mos.ru/xsd/Common/2.4.0}SupplierBillIDType">
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

        @XmlElement(name = "PaymentId", namespace = "urn://rnip.mos.ru/xsd/services/forced-acknowledgement/2.4.0")
        protected List<String> paymentId;
        @XmlElement(name = "PaymentNotLoaded", namespace = "urn://rnip.mos.ru/xsd/services/forced-acknowledgement/2.4.0")
        protected PaymentNotLoaded paymentNotLoaded;
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
         *     {@link PaymentNotLoaded }
         *
         */
        public PaymentNotLoaded getPaymentNotLoaded() {
            return paymentNotLoaded;
        }

        /**
         * Sets the value of the paymentNotLoaded property.
         *
         * @param value
         *     allowed object is
         *     {@link PaymentNotLoaded }
         *
         */
        public void setPaymentNotLoaded(PaymentNotLoaded value) {
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


        /**
         * <p>Java class for anonymous complex type.
         *
         * <p>The following schema fragment specifies the expected content contained within this class.
         *
         * <pre>
         * &lt;complexType>
         *   &lt;simpleContent>
         *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>boolean">
         *       &lt;attribute name="reconcileID" type="{http://rnip.mos.ru/xsd/Common/2.4.0}reconcileIDType" />
         *     &lt;/extension>
         *   &lt;/simpleContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
                "value"
        })
        public static class PaymentNotLoaded {

            @XmlValue
            protected boolean value;
            @XmlAttribute(name = "reconcileID")
            protected String reconcileID;

            /**
             * Gets the value of the value property.
             *
             */
            public boolean isValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             *
             */
            public void setValue(boolean value) {
                this.value = value;
            }

            /**
             * Gets the value of the reconcileID property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getReconcileID() {
                return reconcileID;
            }

            /**
             * Sets the value of the reconcileID property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setReconcileID(String value) {
                this.reconcileID = value;
            }

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
     *         &lt;element name="PaymentDataID" maxOccurs="100">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="paymentId" use="required" type="{http://rnip.mos.ru/xsd/Common/2.4.0}PaymentIdType" />
     *                 &lt;attribute name="serviceDataID" type="{http://rnip.mos.ru/xsd/Common/2.4.0}serviceDataIDType" />
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
            "paymentDataID"
    })
    public static class AnnulmentServiceProvided {

        @XmlElement(name = "PaymentDataID", namespace = "urn://rnip.mos.ru/xsd/services/forced-acknowledgement/2.4.0", required = true)
        protected List<PaymentDataID> paymentDataID;

        /**
         * Gets the value of the paymentDataID property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the paymentDataID property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPaymentDataID().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link PaymentDataID }
         *
         *
         */
        public List<PaymentDataID> getPaymentDataID() {
            if (paymentDataID == null) {
                paymentDataID = new ArrayList<PaymentDataID>();
            }
            return this.paymentDataID;
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
         *       &lt;attribute name="paymentId" use="required" type="{http://rnip.mos.ru/xsd/Common/2.4.0}PaymentIdType" />
         *       &lt;attribute name="serviceDataID" type="{http://rnip.mos.ru/xsd/Common/2.4.0}serviceDataIDType" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class PaymentDataID {

            @XmlAttribute(name = "paymentId", required = true)
            protected String paymentId;
            @XmlAttribute(name = "serviceDataID")
            protected String serviceDataID;

            /**
             * Gets the value of the paymentId property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getPaymentId() {
                return paymentId;
            }

            /**
             * Sets the value of the paymentId property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setPaymentId(String value) {
                this.paymentId = value;
            }

            /**
             * Gets the value of the serviceDataID property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getServiceDataID() {
                return serviceDataID;
            }

            /**
             * Sets the value of the serviceDataID property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setServiceDataID(String value) {
                this.serviceDataID = value;
            }

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
     *           &lt;element name="PaymentId" type="{http://rnip.mos.ru/xsd/Common/2.4.0}PaymentIdType" maxOccurs="100"/>
     *         &lt;/sequence>
     *         &lt;element name="PaymentNotLoaded">
     *           &lt;complexType>
     *             &lt;simpleContent>
     *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>boolean">
     *                 &lt;attribute name="amountReconcile" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
     *               &lt;/extension>
     *             &lt;/simpleContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/choice>
     *       &lt;attribute name="supplierBillId" use="required">
     *         &lt;simpleType>
     *           &lt;union memberTypes=" {http://rnip.mos.ru/xsd/Common/2.4.0}SupplierBillIDType">
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

        @XmlElement(name = "PaymentId", namespace = "urn://rnip.mos.ru/xsd/services/forced-acknowledgement/2.4.0")
        protected List<String> paymentId;
        @XmlElement(name = "PaymentNotLoaded", namespace = "urn://rnip.mos.ru/xsd/services/forced-acknowledgement/2.4.0")
        protected PaymentNotLoaded paymentNotLoaded;
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
         *     {@link PaymentNotLoaded }
         *
         */
        public PaymentNotLoaded getPaymentNotLoaded() {
            return paymentNotLoaded;
        }

        /**
         * Sets the value of the paymentNotLoaded property.
         *
         * @param value
         *     allowed object is
         *     {@link PaymentNotLoaded }
         *
         */
        public void setPaymentNotLoaded(PaymentNotLoaded value) {
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


        /**
         * <p>Java class for anonymous complex type.
         *
         * <p>The following schema fragment specifies the expected content contained within this class.
         *
         * <pre>
         * &lt;complexType>
         *   &lt;simpleContent>
         *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>boolean">
         *       &lt;attribute name="amountReconcile" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
         *     &lt;/extension>
         *   &lt;/simpleContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
                "value"
        })
        public static class PaymentNotLoaded {

            @XmlValue
            protected boolean value;
            @XmlAttribute(name = "amountReconcile")
            @XmlSchemaType(name = "unsignedLong")
            protected BigInteger amountReconcile;

            /**
             * Gets the value of the value property.
             *
             */
            public boolean isValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             *
             */
            public void setValue(boolean value) {
                this.value = value;
            }

            /**
             * Gets the value of the amountReconcile property.
             *
             * @return
             *     possible object is
             *     {@link BigInteger }
             *
             */
            public BigInteger getAmountReconcile() {
                return amountReconcile;
            }

            /**
             * Sets the value of the amountReconcile property.
             *
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *
             */
            public void setAmountReconcile(BigInteger value) {
                this.amountReconcile = value;
            }

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
     *         &lt;element name="PaymentDataInfo" maxOccurs="100">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence minOccurs="0">
     *                   &lt;element name="ServiceData" type="{http://rnip.mos.ru/xsd/Common/2.4.0}ServiceDataType"/>
     *                 &lt;/sequence>
     *                 &lt;attribute name="paymentId" use="required" type="{http://rnip.mos.ru/xsd/Common/2.4.0}PaymentIdType" />
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
            "paymentDataInfo"
    })
    public static class ServiceProvided {

        @XmlElement(name = "PaymentDataInfo", namespace = "urn://rnip.mos.ru/xsd/services/forced-acknowledgement/2.4.0", required = true)
        protected List<PaymentDataInfo> paymentDataInfo;

        /**
         * Gets the value of the paymentDataInfo property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the paymentDataInfo property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getPaymentDataInfo().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link PaymentDataInfo }
         *
         *
         */
        public List<PaymentDataInfo> getPaymentDataInfo() {
            if (paymentDataInfo == null) {
                paymentDataInfo = new ArrayList<PaymentDataInfo>();
            }
            return this.paymentDataInfo;
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
         *       &lt;sequence minOccurs="0">
         *         &lt;element name="ServiceData" type="{http://rnip.mos.ru/xsd/Common/2.4.0}ServiceDataType"/>
         *       &lt;/sequence>
         *       &lt;attribute name="paymentId" use="required" type="{http://rnip.mos.ru/xsd/Common/2.4.0}PaymentIdType" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
                "serviceData"
        })
        public static class PaymentDataInfo {

            @XmlElement(name = "ServiceData", namespace = "urn://rnip.mos.ru/xsd/services/forced-acknowledgement/2.4.0")
            protected ServiceDataType serviceData;
            @XmlAttribute(name = "paymentId", required = true)
            protected String paymentId;

            /**
             * Gets the value of the serviceData property.
             *
             * @return
             *     possible object is
             *     {@link ServiceDataType }
             *
             */
            public ServiceDataType getServiceData() {
                return serviceData;
            }

            /**
             * Sets the value of the serviceData property.
             *
             * @param value
             *     allowed object is
             *     {@link ServiceDataType }
             *
             */
            public void setServiceData(ServiceDataType value) {
                this.serviceData = value;
            }

            /**
             * Gets the value of the paymentId property.
             *
             * @return
             *     possible object is
             *     {@link String }
             *
             */
            public String getPaymentId() {
                return paymentId;
            }

            /**
             * Sets the value of the paymentId property.
             *
             * @param value
             *     allowed object is
             *     {@link String }
             *
             */
            public void setPaymentId(String value) {
                this.paymentId = value;
            }

        }

    }

}