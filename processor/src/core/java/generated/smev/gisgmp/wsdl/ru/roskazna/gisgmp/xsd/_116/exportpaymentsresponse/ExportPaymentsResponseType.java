
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.exportpaymentsresponse;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ExportPaymentsResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExportPaymentsResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Payments">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence minOccurs="0">
 *                   &lt;element name="PaymentInfo" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="PaymentData" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *                             &lt;element name="PaymentSignature" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *                             &lt;element name="PaymentStatus" maxOccurs="unbounded" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;attribute name=" name" use="required">
 *                                       &lt;simpleType>
 *                                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                           &lt;minLength value="1"/>
 *                                           &lt;maxLength value="63"/>
 *                                         &lt;/restriction>
 *                                       &lt;/simpleType>
 *                                     &lt;/attribute>
 *                                     &lt;attribute name=" value">
 *                                       &lt;simpleType>
 *                                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                           &lt;minLength value="0"/>
 *                                           &lt;maxLength value="25"/>
 *                                         &lt;/restriction>
 *                                       &lt;/simpleType>
 *                                     &lt;/attribute>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="hasMore" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                 &lt;attribute name="needReRequest" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
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
@XmlType(name = "ExportPaymentsResponseType", propOrder = {
    "payments"
})
public class ExportPaymentsResponseType {

    @XmlElement(name = "Payments", required = true)
    protected Payments payments;

    /**
     * Gets the value of the payments property.
     * 
     * @return
     *     possible object is
     *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.exportpaymentsresponse.ExportPaymentsResponseType.Payments }
     *     
     */
    public Payments getPayments() {
        return payments;
    }

    /**
     * Sets the value of the payments property.
     * 
     * @param value
     *     allowed object is
     *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.exportpaymentsresponse.ExportPaymentsResponseType.Payments }
     *     
     */
    public void setPayments(Payments value) {
        this.payments = value;
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
     *         &lt;element name="PaymentInfo" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="PaymentData" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
     *                   &lt;element name="PaymentSignature" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
     *                   &lt;element name="PaymentStatus" maxOccurs="unbounded" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;attribute name=" name" use="required">
     *                             &lt;simpleType>
     *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                                 &lt;minLength value="1"/>
     *                                 &lt;maxLength value="63"/>
     *                               &lt;/restriction>
     *                             &lt;/simpleType>
     *                           &lt;/attribute>
     *                           &lt;attribute name=" value">
     *                             &lt;simpleType>
     *                               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                                 &lt;minLength value="0"/>
     *                                 &lt;maxLength value="25"/>
     *                               &lt;/restriction>
     *                             &lt;/simpleType>
     *                           &lt;/attribute>
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
     *       &lt;attribute name="hasMore" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *       &lt;attribute name="needReRequest" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
     *     &lt;/restriction>
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
    public static class Payments {

        @XmlElement(name = "PaymentInfo")
        protected List<PaymentInfo> paymentInfo;
        @XmlAttribute(required = true)
        protected boolean hasMore;
        @XmlAttribute
        protected Boolean needReRequest;

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
         * {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.exportpaymentsresponse.ExportPaymentsResponseType.Payments.PaymentInfo }
         * 
         * 
         */
        public List<PaymentInfo> getPaymentInfo() {
            if (paymentInfo == null) {
                paymentInfo = new ArrayList<PaymentInfo>();
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
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="PaymentData" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
         *         &lt;element name="PaymentSignature" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
         *         &lt;element name="PaymentStatus" maxOccurs="unbounded" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;attribute name=" name" use="required">
         *                   &lt;simpleType>
         *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *                       &lt;minLength value="1"/>
         *                       &lt;maxLength value="63"/>
         *                     &lt;/restriction>
         *                   &lt;/simpleType>
         *                 &lt;/attribute>
         *                 &lt;attribute name=" value">
         *                   &lt;simpleType>
         *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *                       &lt;minLength value="0"/>
         *                       &lt;maxLength value="25"/>
         *                     &lt;/restriction>
         *                   &lt;/simpleType>
         *                 &lt;/attribute>
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
            "paymentData",
            "paymentSignature",
            "paymentStatus"
        })
        public static class PaymentInfo {

            @XmlElement(name = "PaymentData", required = true)
            protected byte[] paymentData;
            @XmlElement(name = "PaymentSignature")
            protected byte[] paymentSignature;
            @XmlElement(name = "PaymentStatus")
            protected List<PaymentStatus> paymentStatus;

            /**
             * Gets the value of the paymentData property.
             * 
             * @return
             *     possible object is
             *     byte[]
             */
            public byte[] getPaymentData() {
                return paymentData;
            }

            /**
             * Sets the value of the paymentData property.
             * 
             * @param value
             *     allowed object is
             *     byte[]
             */
            public void setPaymentData(byte[] value) {
                this.paymentData = ((byte[]) value);
            }

            /**
             * Gets the value of the paymentSignature property.
             * 
             * @return
             *     possible object is
             *     byte[]
             */
            public byte[] getPaymentSignature() {
                return paymentSignature;
            }

            /**
             * Sets the value of the paymentSignature property.
             * 
             * @param value
             *     allowed object is
             *     byte[]
             */
            public void setPaymentSignature(byte[] value) {
                this.paymentSignature = ((byte[]) value);
            }

            /**
             * Gets the value of the paymentStatus property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the paymentStatus property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getPaymentStatus().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.exportpaymentsresponse.ExportPaymentsResponseType.Payments.PaymentInfo.PaymentStatus }
             * 
             * 
             */
            public List<PaymentStatus> getPaymentStatus() {
                if (paymentStatus == null) {
                    paymentStatus = new ArrayList<PaymentStatus>();
                }
                return this.paymentStatus;
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
             *       &lt;attribute name=" name" use="required">
             *         &lt;simpleType>
             *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
             *             &lt;minLength value="1"/>
             *             &lt;maxLength value="63"/>
             *           &lt;/restriction>
             *         &lt;/simpleType>
             *       &lt;/attribute>
             *       &lt;attribute name=" value">
             *         &lt;simpleType>
             *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
             *             &lt;minLength value="0"/>
             *             &lt;maxLength value="25"/>
             *           &lt;/restriction>
             *         &lt;/simpleType>
             *       &lt;/attribute>
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class PaymentStatus {

                @XmlAttribute(name = " name", required = true)
                protected String _0020Name;
                @XmlAttribute(name = " value")
                protected String _0020Value;

                /**
                 * Gets the value of the 0020Name property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String get_0020Name() {
                    return _0020Name;
                }

                /**
                 * Sets the value of the 0020Name property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void set_0020Name(String value) {
                    this._0020Name = value;
                }

                /**
                 * Gets the value of the 0020Value property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link String }
                 *     
                 */
                public String get_0020Value() {
                    return _0020Value;
                }

                /**
                 * Sets the value of the 0020Value property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link String }
                 *     
                 */
                public void set_0020Value(String value) {
                    this._0020Value = value;
                }

            }

        }

    }

}