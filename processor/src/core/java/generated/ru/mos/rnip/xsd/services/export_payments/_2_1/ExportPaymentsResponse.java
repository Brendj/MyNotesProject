
package generated.ru.mos.rnip.xsd.services.export_payments._2_1;

import generated.ru.mos.rnip.xsd.common._2_1.ResponseType;
import generated.ru.mos.rnip.xsd.payment._2_1.PaymentType;

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
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Common/2.1.1}ResponseType">
 *       &lt;sequence minOccurs="0">
 *         &lt;element name="PaymentInfo" maxOccurs="100">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://rnip.mos.ru/xsd/Payment/2.1.1}PaymentType">
 *                 &lt;sequence>
 *                   &lt;element name="AcknowledgmentInfo" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;choice>
 *                             &lt;element name="SupplierBillID" type="{http://rnip.mos.ru/xsd/Common/2.1.1}SupplierBillIDType"/>
 *                             &lt;element name="ServiceProvided" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *                           &lt;/choice>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="RefundInfo" maxOccurs="20" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="refundId" use="required" type="{http://rnip.mos.ru/xsd/Common/2.1.1}RefundIdType" />
 *                           &lt;attribute name="amount" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *       &lt;attribute name="hasMore" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "paymentInfo"
})
@XmlRootElement(name = "ExportPaymentsResponse")
public class ExportPaymentsResponse
    extends ResponseType
{

    @XmlElement(name = "PaymentInfo")
    protected List<ExportPaymentsResponse.PaymentInfo> paymentInfo;
    @XmlAttribute(name = "hasMore", required = true)
    protected boolean hasMore;

    /**
     * Gets the value of the paymentInfo property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the paymentInfo property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPaymentInfo().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExportPaymentsResponse.PaymentInfo }
     * 
     * 
     */
    public List<ExportPaymentsResponse.PaymentInfo> getPaymentInfo() {
        if (paymentInfo == null) {
            paymentInfo = new ArrayList<ExportPaymentsResponse.PaymentInfo>();
        }
        return this.paymentInfo;
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
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://rnip.mos.ru/xsd/Payment/2.1.1}PaymentType">
     *       &lt;sequence>
     *         &lt;element name="AcknowledgmentInfo" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;choice>
     *                   &lt;element name="SupplierBillID" type="{http://rnip.mos.ru/xsd/Common/2.1.1}SupplierBillIDType"/>
     *                   &lt;element name="ServiceProvided" type="{http://www.w3.org/2001/XMLSchema}string"/>
     *                 &lt;/choice>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="RefundInfo" maxOccurs="20" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="refundId" use="required" type="{http://rnip.mos.ru/xsd/Common/2.1.1}RefundIdType" />
     *                 &lt;attribute name="amount" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *     &lt;/extension>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "acknowledgmentInfo",
        "refundInfo"
    })
    public static class PaymentInfo
        extends PaymentType
    {

        @XmlElement(name = "AcknowledgmentInfo")
        protected ExportPaymentsResponse.PaymentInfo.AcknowledgmentInfo acknowledgmentInfo;
        @XmlElement(name = "RefundInfo")
        protected List<ExportPaymentsResponse.PaymentInfo.RefundInfo> refundInfo;

        /**
         * Gets the value of the acknowledgmentInfo property.
         * 
         * @return
         *     possible object is
         *     {@link ExportPaymentsResponse.PaymentInfo.AcknowledgmentInfo }
         *     
         */
        public ExportPaymentsResponse.PaymentInfo.AcknowledgmentInfo getAcknowledgmentInfo() {
            return acknowledgmentInfo;
        }

        /**
         * Sets the value of the acknowledgmentInfo property.
         * 
         * @param value
         *     allowed object is
         *     {@link ExportPaymentsResponse.PaymentInfo.AcknowledgmentInfo }
         *     
         */
        public void setAcknowledgmentInfo(ExportPaymentsResponse.PaymentInfo.AcknowledgmentInfo value) {
            this.acknowledgmentInfo = value;
        }

        /**
         * Gets the value of the refundInfo property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the refundInfo property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRefundInfo().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ExportPaymentsResponse.PaymentInfo.RefundInfo }
         * 
         * 
         */
        public List<ExportPaymentsResponse.PaymentInfo.RefundInfo> getRefundInfo() {
            if (refundInfo == null) {
                refundInfo = new ArrayList<ExportPaymentsResponse.PaymentInfo.RefundInfo>();
            }
            return this.refundInfo;
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
         *         &lt;element name="SupplierBillID" type="{http://rnip.mos.ru/xsd/Common/2.1.1}SupplierBillIDType"/>
         *         &lt;element name="ServiceProvided" type="{http://www.w3.org/2001/XMLSchema}string"/>
         *       &lt;/choice>
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "supplierBillID",
            "serviceProvided"
        })
        public static class AcknowledgmentInfo {

            @XmlElement(name = "SupplierBillID")
            protected String supplierBillID;
            @XmlElement(name = "ServiceProvided")
            protected String serviceProvided;

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

            /**
             * Gets the value of the serviceProvided property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getServiceProvided() {
                return serviceProvided;
            }

            /**
             * Sets the value of the serviceProvided property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setServiceProvided(String value) {
                this.serviceProvided = value;
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
         *       &lt;attribute name="refundId" use="required" type="{http://rnip.mos.ru/xsd/Common/2.1.1}RefundIdType" />
         *       &lt;attribute name="amount" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class RefundInfo {

            @XmlAttribute(name = "refundId", required = true)
            protected String refundId;
            @XmlAttribute(name = "amount", required = true)
            @XmlSchemaType(name = "unsignedLong")
            protected BigInteger amount;

            /**
             * Gets the value of the refundId property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getRefundId() {
                return refundId;
            }

            /**
             * Sets the value of the refundId property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setRefundId(String value) {
                this.refundId = value;
            }

            /**
             * Gets the value of the amount property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getAmount() {
                return amount;
            }

            /**
             * Sets the value of the amount property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setAmount(BigInteger value) {
                this.amount = value;
            }

        }

    }

}
