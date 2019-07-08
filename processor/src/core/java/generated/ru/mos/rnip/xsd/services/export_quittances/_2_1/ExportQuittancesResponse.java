
package generated.ru.mos.rnip.xsd.services.export_quittances._2_1;

import generated.ru.mos.rnip.xsd.common._2_1.ResponseType;
import generated.ru.mos.rnip.xsd.quittance._2_1.QuittanceType;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
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
 *         &lt;element name="Quittance" type="{http://rnip.mos.ru/xsd/Quittance/2.1.1}QuittanceType" maxOccurs="100" minOccurs="0"/>
 *         &lt;element name="PossibleData" maxOccurs="100" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="ComparisonResult" maxOccurs="100">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;attribute name="paymentId" use="required">
 *                             &lt;simpleType>
 *                               &lt;union memberTypes=" {http://rnip.mos.ru/xsd/Common/2.1.1}PaymentIdType">
 *                                 &lt;simpleType>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                     &lt;enumeration value="PaymentNotLoaded"/>
 *                                   &lt;/restriction>
 *                                 &lt;/simpleType>
 *                               &lt;/union>
 *                             &lt;/simpleType>
 *                           &lt;/attribute>
 *                           &lt;attribute name="comparisonWeight" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
 *                           &lt;attribute name="comparisonDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *                           &lt;attribute name="amountPayment" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
 *                           &lt;attribute name="kbk" type="{http://rnip.mos.ru/xsd/Common/2.1.1}KBKType" />
 *                           &lt;attribute name="oktmo" type="{http://rnip.mos.ru/xsd/Common/2.1.1}OKTMOType" />
 *                           &lt;attribute name="accountNumber" type="{http://rnip.mos.ru/xsd/Common/2.1.1}AccountNumType" />
 *                           &lt;attribute name="bik" type="{http://rnip.mos.ru/xsd/Common/2.1.1}BIKType" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="supplierBillID" use="required" type="{http://rnip.mos.ru/xsd/Common/2.1.1}SupplierBillIDType" />
 *                 &lt;attribute name="totalAmount" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
 *               &lt;/restriction>
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
    "quittance",
    "possibleData"
})
@XmlRootElement(name = "ExportQuittancesResponse")
public class ExportQuittancesResponse
    extends ResponseType
{

    @XmlElement(name = "Quittance")
    protected List<QuittanceType> quittance;
    @XmlElement(name = "PossibleData")
    protected List<ExportQuittancesResponse.PossibleData> possibleData;
    @XmlAttribute(name = "hasMore", required = true)
    protected boolean hasMore;

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
     * {@link QuittanceType }
     * 
     * 
     */
    public List<QuittanceType> getQuittance() {
        if (quittance == null) {
            quittance = new ArrayList<QuittanceType>();
        }
        return this.quittance;
    }

    /**
     * Gets the value of the possibleData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the possibleData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPossibleData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ExportQuittancesResponse.PossibleData }
     * 
     * 
     */
    public List<ExportQuittancesResponse.PossibleData> getPossibleData() {
        if (possibleData == null) {
            possibleData = new ArrayList<ExportQuittancesResponse.PossibleData>();
        }
        return this.possibleData;
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
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="ComparisonResult" maxOccurs="100">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;attribute name="paymentId" use="required">
     *                   &lt;simpleType>
     *                     &lt;union memberTypes=" {http://rnip.mos.ru/xsd/Common/2.1.1}PaymentIdType">
     *                       &lt;simpleType>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                           &lt;enumeration value="PaymentNotLoaded"/>
     *                         &lt;/restriction>
     *                       &lt;/simpleType>
     *                     &lt;/union>
     *                   &lt;/simpleType>
     *                 &lt;/attribute>
     *                 &lt;attribute name="comparisonWeight" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
     *                 &lt;attribute name="comparisonDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
     *                 &lt;attribute name="amountPayment" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
     *                 &lt;attribute name="kbk" type="{http://rnip.mos.ru/xsd/Common/2.1.1}KBKType" />
     *                 &lt;attribute name="oktmo" type="{http://rnip.mos.ru/xsd/Common/2.1.1}OKTMOType" />
     *                 &lt;attribute name="accountNumber" type="{http://rnip.mos.ru/xsd/Common/2.1.1}AccountNumType" />
     *                 &lt;attribute name="bik" type="{http://rnip.mos.ru/xsd/Common/2.1.1}BIKType" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="supplierBillID" use="required" type="{http://rnip.mos.ru/xsd/Common/2.1.1}SupplierBillIDType" />
     *       &lt;attribute name="totalAmount" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "comparisonResult"
    })
    public static class PossibleData {

        @XmlElement(name = "ComparisonResult", required = true)
        protected List<ExportQuittancesResponse.PossibleData.ComparisonResult> comparisonResult;
        @XmlAttribute(name = "supplierBillID", required = true)
        protected String supplierBillID;
        @XmlAttribute(name = "totalAmount")
        @XmlSchemaType(name = "unsignedLong")
        protected BigInteger totalAmount;

        /**
         * Gets the value of the comparisonResult property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the comparisonResult property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getComparisonResult().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ExportQuittancesResponse.PossibleData.ComparisonResult }
         * 
         * 
         */
        public List<ExportQuittancesResponse.PossibleData.ComparisonResult> getComparisonResult() {
            if (comparisonResult == null) {
                comparisonResult = new ArrayList<ExportQuittancesResponse.PossibleData.ComparisonResult>();
            }
            return this.comparisonResult;
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

        /**
         * Gets the value of the totalAmount property.
         * 
         * @return
         *     possible object is
         *     {@link BigInteger }
         *     
         */
        public BigInteger getTotalAmount() {
            return totalAmount;
        }

        /**
         * Sets the value of the totalAmount property.
         * 
         * @param value
         *     allowed object is
         *     {@link BigInteger }
         *     
         */
        public void setTotalAmount(BigInteger value) {
            this.totalAmount = value;
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
         *       &lt;attribute name="paymentId" use="required">
         *         &lt;simpleType>
         *           &lt;union memberTypes=" {http://rnip.mos.ru/xsd/Common/2.1.1}PaymentIdType">
         *             &lt;simpleType>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *                 &lt;enumeration value="PaymentNotLoaded"/>
         *               &lt;/restriction>
         *             &lt;/simpleType>
         *           &lt;/union>
         *         &lt;/simpleType>
         *       &lt;/attribute>
         *       &lt;attribute name="comparisonWeight" use="required" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
         *       &lt;attribute name="comparisonDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
         *       &lt;attribute name="amountPayment" type="{http://www.w3.org/2001/XMLSchema}unsignedLong" />
         *       &lt;attribute name="kbk" type="{http://rnip.mos.ru/xsd/Common/2.1.1}KBKType" />
         *       &lt;attribute name="oktmo" type="{http://rnip.mos.ru/xsd/Common/2.1.1}OKTMOType" />
         *       &lt;attribute name="accountNumber" type="{http://rnip.mos.ru/xsd/Common/2.1.1}AccountNumType" />
         *       &lt;attribute name="bik" type="{http://rnip.mos.ru/xsd/Common/2.1.1}BIKType" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "")
        public static class ComparisonResult {

            @XmlAttribute(name = "paymentId", required = true)
            protected String paymentId;
            @XmlAttribute(name = "comparisonWeight", required = true)
            @XmlSchemaType(name = "unsignedLong")
            protected BigInteger comparisonWeight;
            @XmlAttribute(name = "comparisonDate")
            @XmlSchemaType(name = "dateTime")
            protected XMLGregorianCalendar comparisonDate;
            @XmlAttribute(name = "amountPayment")
            @XmlSchemaType(name = "unsignedLong")
            protected BigInteger amountPayment;
            @XmlAttribute(name = "kbk")
            protected String kbk;
            @XmlAttribute(name = "oktmo")
            protected String oktmo;
            @XmlAttribute(name = "accountNumber")
            protected String accountNumber;
            @XmlAttribute(name = "bik")
            protected String bik;

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
             * Gets the value of the comparisonWeight property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getComparisonWeight() {
                return comparisonWeight;
            }

            /**
             * Sets the value of the comparisonWeight property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setComparisonWeight(BigInteger value) {
                this.comparisonWeight = value;
            }

            /**
             * Gets the value of the comparisonDate property.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getComparisonDate() {
                return comparisonDate;
            }

            /**
             * Sets the value of the comparisonDate property.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setComparisonDate(XMLGregorianCalendar value) {
                this.comparisonDate = value;
            }

            /**
             * Gets the value of the amountPayment property.
             * 
             * @return
             *     possible object is
             *     {@link BigInteger }
             *     
             */
            public BigInteger getAmountPayment() {
                return amountPayment;
            }

            /**
             * Sets the value of the amountPayment property.
             * 
             * @param value
             *     allowed object is
             *     {@link BigInteger }
             *     
             */
            public void setAmountPayment(BigInteger value) {
                this.amountPayment = value;
            }

            /**
             * Gets the value of the kbk property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getKbk() {
                return kbk;
            }

            /**
             * Sets the value of the kbk property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setKbk(String value) {
                this.kbk = value;
            }

            /**
             * Gets the value of the oktmo property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getOktmo() {
                return oktmo;
            }

            /**
             * Sets the value of the oktmo property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setOktmo(String value) {
                this.oktmo = value;
            }

            /**
             * Gets the value of the accountNumber property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getAccountNumber() {
                return accountNumber;
            }

            /**
             * Sets the value of the accountNumber property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setAccountNumber(String value) {
                this.accountNumber = value;
            }

            /**
             * Gets the value of the bik property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getBik() {
                return bik;
            }

            /**
             * Sets the value of the bik property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setBik(String value) {
                this.bik = value;
            }

        }

    }

}
