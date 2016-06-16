
package generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.pgu_chargesresponse;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ExportChargesResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExportChargesResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Charges">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence minOccurs="0">
 *                   &lt;element name="ChargeInfo" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="ChargeData" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *                             &lt;element name="ChargeSignature" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *                             &lt;element name="AmountToPay" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *                             &lt;element name="QuittanceWithPaymentStatus" minOccurs="0">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                   &lt;length value="1"/>
 *                                   &lt;enumeration value="1"/>
 *                                   &lt;enumeration value="2"/>
 *                                   &lt;enumeration value="3"/>
 *                                   &lt;enumeration value="4"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                             &lt;element name="IsRevoked" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;simpleContent>
 *                                   &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>boolean">
 *                                     &lt;attribute name="date" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *                                   &lt;/extension>
 *                                 &lt;/simpleContent>
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
@XmlType(name = "ExportChargesResponseType", propOrder = {
    "charges"
})
public class ExportChargesResponseType {

    @XmlElement(name = "Charges", required = true)
    protected ExportChargesResponseType.Charges charges;

    /**
     * Gets the value of the charges property.
     * 
     * @return
     *     possible object is
     *     {@link ExportChargesResponseType.Charges }
     *     
     */
    public ExportChargesResponseType.Charges getCharges() {
        return charges;
    }

    /**
     * Sets the value of the charges property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExportChargesResponseType.Charges }
     *     
     */
    public void setCharges(ExportChargesResponseType.Charges value) {
        this.charges = value;
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
     *         &lt;element name="ChargeInfo" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="ChargeData" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
     *                   &lt;element name="ChargeSignature" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
     *                   &lt;element name="AmountToPay" type="{http://www.w3.org/2001/XMLSchema}long"/>
     *                   &lt;element name="QuittanceWithPaymentStatus" minOccurs="0">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                         &lt;length value="1"/>
     *                         &lt;enumeration value="1"/>
     *                         &lt;enumeration value="2"/>
     *                         &lt;enumeration value="3"/>
     *                         &lt;enumeration value="4"/>
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
     *                   &lt;/element>
     *                   &lt;element name="IsRevoked" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;simpleContent>
     *                         &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>boolean">
     *                           &lt;attribute name="date" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
     *                         &lt;/extension>
     *                       &lt;/simpleContent>
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
        "chargeInfo"
    })
    public static class Charges {

        @XmlElement(name = "ChargeInfo")
        protected List<ExportChargesResponseType.Charges.ChargeInfo> chargeInfo;
        @XmlAttribute(required = true)
        protected boolean hasMore;
        @XmlAttribute
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
         * {@link ExportChargesResponseType.Charges.ChargeInfo }
         * 
         * 
         */
        public List<ExportChargesResponseType.Charges.ChargeInfo> getChargeInfo() {
            if (chargeInfo == null) {
                chargeInfo = new ArrayList<ExportChargesResponseType.Charges.ChargeInfo>();
            }
            return this.chargeInfo;
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
         *         &lt;element name="ChargeData" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
         *         &lt;element name="ChargeSignature" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
         *         &lt;element name="AmountToPay" type="{http://www.w3.org/2001/XMLSchema}long"/>
         *         &lt;element name="QuittanceWithPaymentStatus" minOccurs="0">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *               &lt;length value="1"/>
         *               &lt;enumeration value="1"/>
         *               &lt;enumeration value="2"/>
         *               &lt;enumeration value="3"/>
         *               &lt;enumeration value="4"/>
         *             &lt;/restriction>
         *           &lt;/simpleType>
         *         &lt;/element>
         *         &lt;element name="IsRevoked" minOccurs="0">
         *           &lt;complexType>
         *             &lt;simpleContent>
         *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>boolean">
         *                 &lt;attribute name="date" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
         *               &lt;/extension>
         *             &lt;/simpleContent>
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
            "chargeData",
            "chargeSignature",
            "amountToPay",
            "quittanceWithPaymentStatus",
            "isRevoked"
        })
        public static class ChargeInfo {

            @XmlElement(name = "ChargeData", required = true)
            protected byte[] chargeData;
            @XmlElement(name = "ChargeSignature")
            protected byte[] chargeSignature;
            @XmlElement(name = "AmountToPay")
            protected long amountToPay;
            @XmlElement(name = "QuittanceWithPaymentStatus")
            protected String quittanceWithPaymentStatus;
            @XmlElement(name = "IsRevoked")
            protected ExportChargesResponseType.Charges.ChargeInfo.IsRevoked isRevoked;

            /**
             * Gets the value of the chargeData property.
             * 
             * @return
             *     possible object is
             *     byte[]
             */
            public byte[] getChargeData() {
                return chargeData;
            }

            /**
             * Sets the value of the chargeData property.
             * 
             * @param value
             *     allowed object is
             *     byte[]
             */
            public void setChargeData(byte[] value) {
                this.chargeData = ((byte[]) value);
            }

            /**
             * Gets the value of the chargeSignature property.
             * 
             * @return
             *     possible object is
             *     byte[]
             */
            public byte[] getChargeSignature() {
                return chargeSignature;
            }

            /**
             * Sets the value of the chargeSignature property.
             * 
             * @param value
             *     allowed object is
             *     byte[]
             */
            public void setChargeSignature(byte[] value) {
                this.chargeSignature = ((byte[]) value);
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
             * Gets the value of the quittanceWithPaymentStatus property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getQuittanceWithPaymentStatus() {
                return quittanceWithPaymentStatus;
            }

            /**
             * Sets the value of the quittanceWithPaymentStatus property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setQuittanceWithPaymentStatus(String value) {
                this.quittanceWithPaymentStatus = value;
            }

            /**
             * Gets the value of the isRevoked property.
             * 
             * @return
             *     possible object is
             *     {@link ExportChargesResponseType.Charges.ChargeInfo.IsRevoked }
             *     
             */
            public ExportChargesResponseType.Charges.ChargeInfo.IsRevoked getIsRevoked() {
                return isRevoked;
            }

            /**
             * Sets the value of the isRevoked property.
             * 
             * @param value
             *     allowed object is
             *     {@link ExportChargesResponseType.Charges.ChargeInfo.IsRevoked }
             *     
             */
            public void setIsRevoked(ExportChargesResponseType.Charges.ChargeInfo.IsRevoked value) {
                this.isRevoked = value;
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
             *       &lt;attribute name="date" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
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
            public static class IsRevoked {

                @XmlValue
                protected boolean value;
                @XmlAttribute
                @XmlSchemaType(name = "dateTime")
                protected XMLGregorianCalendar date;

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
                 * Gets the value of the date property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public XMLGregorianCalendar getDate() {
                    return date;
                }

                /**
                 * Sets the value of the date property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public void setDate(XMLGregorianCalendar value) {
                    this.date = value;
                }

            }

        }

    }

}
