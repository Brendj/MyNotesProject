
package generated.ru.mos.rnip.xsd.services.export_charges._2_4;

import generated.ru.mos.rnip.xsd.charge._2_1.ChargeType;
import generated.ru.mos.rnip.xsd.common._2_1.ResponseType;

import javax.xml.bind.annotation.*;
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
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Common/2.4.0}ResponseType">
 *       &lt;choice minOccurs="0">
 *         &lt;element name="ChargeInfo" maxOccurs="100">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://rnip.mos.ru/xsd/Charge/2.4.0}ChargeType">
 *                 &lt;sequence>
 *                   &lt;element name="ReconcileWithoutPayment" maxOccurs="100" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="reconcileID" use="required" type="{http://rnip.mos.ru/xsd/Common/2.4.0}reconcileIDType" />
 *                           &lt;attribute name="amountReconcile" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="amountToPay" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
 *                 &lt;attribute name="acknowledgmentStatus" type="{http://rnip.mos.ru/xsd/Common/2.4.0}AcknowledgmentStatusType" />
 *                 &lt;attribute name="requisiteCheckCode" type="{http://rnip.mos.ru/xsd/Common/2.4.0}RequisiteCheckCodeType" />
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="ChargeOffense" maxOccurs="100">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://rnip.mos.ru/xsd/Charge/2.4.0}AdditionalOffense"/>
 *                 &lt;/sequence>
 *                 &lt;attribute name="supplierBillID" use="required" type="{http://rnip.mos.ru/xsd/Common/2.4.0}SupplierBillIDType" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/choice>
 *       &lt;attribute name="hasMore" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="needReRequest" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
        "chargeInfo",
        "chargeOffense"
})
@XmlRootElement(name = "ExportChargesResponse", namespace = "urn://rnip.mos.ru/xsd/services/export-charges/2.4.0")
public class ExportChargesResponse
        extends ResponseType
{

    @XmlElement(name = "ChargeInfo", namespace = "urn://rnip.mos.ru/xsd/services/export-charges/2.4.0")
    protected List<ExportChargesResponse.ChargeInfo> chargeInfo;
    @XmlElement(name = "ChargeOffense", namespace = "urn://rnip.mos.ru/xsd/services/export-charges/2.4.0")
    protected List<ExportChargesResponse.ChargeOffense> chargeOffense;
    @XmlAttribute(name = "hasMore", required = true)
    protected boolean hasMore;
    @XmlAttribute(name = "needReRequest")
    protected Boolean needReRequest;

    /**
     * Gets the value of the chargeInfo property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the chargeInfo property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getChargeInfo().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExportChargesResponse.ChargeInfo }
     *
     *
     */
    public List<ExportChargesResponse.ChargeInfo> getChargeInfo() {
        if (chargeInfo == null) {
            chargeInfo = new ArrayList<ExportChargesResponse.ChargeInfo>();
        }
        return this.chargeInfo;
    }

    /**
     * Gets the value of the chargeOffense property.
     *
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the chargeOffense property.
     *
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getChargeOffense().add(newItem);
     * </pre>
     *
     *
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExportChargesResponse.ChargeOffense }
     *
     *
     */
    public List<ExportChargesResponse.ChargeOffense> getChargeOffense() {
        if (chargeOffense == null) {
            chargeOffense = new ArrayList<ExportChargesResponse.ChargeOffense>();
        }
        return this.chargeOffense;
    }

    /**
     * Gets the value of the hasMore property.
     *
     */
    public boolean isHasMore() {
        return hasMore;
    }

    /**
     * Sets the value of the hasMore property.
     *
     */
    public void setHasMore(boolean value) {
        this.hasMore = value;
    }

    /**
     * Gets the value of the needReRequest property.
     *
     * @return
     *     possible object is
     *     {@link Boolean }
     *
     */
    public boolean isNeedReRequest() {
        if (needReRequest == null) {
            return false;
        } else {
            return needReRequest;
        }
    }

    /**
     * Sets the value of the needReRequest property.
     *
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *
     */
    public void setNeedReRequest(Boolean value) {
        this.needReRequest = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     *
     * <p>The following schema fragment specifies the expected content contained within this class.
     *
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://rnip.mos.ru/xsd/Charge/2.4.0}ChargeType">
     *       &lt;sequence>
     *         &lt;element name="ReconcileWithoutPayment" maxOccurs="100" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="reconcileID" use="required" type="{http://rnip.mos.ru/xsd/Common/2.4.0}reconcileIDType" />
     *                 &lt;attribute name="amountReconcile" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="amountToPay" use="required" type="{http://www.w3.org/2001/XMLSchema}long" />
     *       &lt;attribute name="acknowledgmentStatus" type="{http://rnip.mos.ru/xsd/Common/2.4.0}AcknowledgmentStatusType" />
     *       &lt;attribute name="requisiteCheckCode" type="{http://rnip.mos.ru/xsd/Common/2.4.0}RequisiteCheckCodeType" />
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "reconcileWithoutPayment"
    })
    public static class ChargeInfo
            extends ChargeType
    {

        @XmlElement(name = "ReconcileWithoutPayment", namespace = "urn://rnip.mos.ru/xsd/services/export-charges/2.4.0")
        protected List<ExportChargesResponse.ChargeInfo.ReconcileWithoutPayment> reconcileWithoutPayment;
        @XmlAttribute(name = "amountToPay", required = true)
        protected long amountToPay;
        @XmlAttribute(name = "acknowledgmentStatus")
        protected String acknowledgmentStatus;
        @XmlAttribute(name = "requisiteCheckCode")
        protected String requisiteCheckCode;

        /**
         * Gets the value of the reconcileWithoutPayment property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the reconcileWithoutPayment property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getReconcileWithoutPayment().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ExportChargesResponse.ChargeInfo.ReconcileWithoutPayment }
         *
         *
         */
        public List<ExportChargesResponse.ChargeInfo.ReconcileWithoutPayment> getReconcileWithoutPayment() {
            if (reconcileWithoutPayment == null) {
                reconcileWithoutPayment = new ArrayList<ExportChargesResponse.ChargeInfo.ReconcileWithoutPayment>();
            }
            return this.reconcileWithoutPayment;
        }

        /**
         * Gets the value of the amountToPay property.
         *
         */
        public long getAmountToPay() {
            return amountToPay;
        }

        /**
         * Sets the value of the amountToPay property.
         *
         */
        public void setAmountToPay(long value) {
            this.amountToPay = value;
        }

        /**
         * Gets the value of the acknowledgmentStatus property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getAcknowledgmentStatus() {
            return acknowledgmentStatus;
        }

        /**
         * Sets the value of the acknowledgmentStatus property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setAcknowledgmentStatus(String value) {
            this.acknowledgmentStatus = value;
        }

        /**
         * Gets the value of the requisiteCheckCode property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getRequisiteCheckCode() {
            return requisiteCheckCode;
        }

        /**
         * Sets the value of the requisiteCheckCode property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setRequisiteCheckCode(String value) {
            this.requisiteCheckCode = value;
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
         *       &lt;attribute name="reconcileID" use="required" type="{http://rnip.mos.ru/xsd/Common/2.4.0}reconcileIDType" />
         *       &lt;attribute name="amountReconcile" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         *
         *
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class ReconcileWithoutPayment {

            @XmlAttribute(name = "reconcileID", required = true)
            protected String reconcileID;
            @XmlAttribute(name = "amountReconcile", required = true)
            @XmlSchemaType(name = "unsignedLong")
            protected BigInteger amountReconcile;

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
     *         &lt;element ref="{http://rnip.mos.ru/xsd/Charge/2.4.0}AdditionalOffense"/>
     *       &lt;/sequence>
     *       &lt;attribute name="supplierBillID" use="required" type="{http://rnip.mos.ru/xsd/Common/2.4.0}SupplierBillIDType" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     *
     *
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
            "additionalOffense"
    })
    public static class ChargeOffense {

        @XmlElement(name = "AdditionalOffense", namespace = "http://rnip.mos.ru/xsd/Charge/2.4.0", required = true)
        protected OffenseType additionalOffense;
        @XmlAttribute(name = "supplierBillID", required = true)
        protected String supplierBillID;

        /**
         * Gets the value of the additionalOffense property.
         *
         * @return
         *     possible object is
         *     {@link OffenseType }
         *
         */
        public OffenseType getAdditionalOffense() {
            return additionalOffense;
        }

        /**
         * Sets the value of the additionalOffense property.
         *
         * @param value
         *     allowed object is
         *     {@link OffenseType }
         *
         */
        public void setAdditionalOffense(OffenseType value) {
            this.additionalOffense = value;
        }

        /**
         * Gets the value of the supplierBillID property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getSupplierBillID() {
            return supplierBillID;
        }

        /**
         * Sets the value of the supplierBillID property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setSupplierBillID(String value) {
            this.supplierBillID = value;
        }

    }

}
