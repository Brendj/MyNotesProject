package generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.pgu_datarequest;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlID;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;

import generated.smev.gisgmp.client.org.w3._2000._09.xmldsig_.SignatureType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.common.PayerIdentificationType;


/**
 * <p>Java class for DataRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Filter">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Conditions">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;choice minOccurs="0">
 *                               &lt;element name="ChargesIdentifiers">
 *                                 &lt;complexType>
 *                                   &lt;complexContent>
 *                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                       &lt;sequence>
 *                                         &lt;element name="SupplierBillID" maxOccurs="100">
 *                                           &lt;simpleType>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
 *                                               &lt;minLength value="1"/>
 *                                               &lt;maxLength value="25"/>
 *                                               &lt;pattern value="\c{20}"/>
 *                                               &lt;pattern value="\c{25}"/>
 *                                             &lt;/restriction>
 *                                           &lt;/simpleType>
 *                                         &lt;/element>
 *                                       &lt;/sequence>
 *                                     &lt;/restriction>
 *                                   &lt;/complexContent>
 *                                 &lt;/complexType>
 *                               &lt;/element>
 *                               &lt;element name="Payers">
 *                                 &lt;complexType>
 *                                   &lt;complexContent>
 *                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                       &lt;choice>
 *                                         &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Common}PayerIdentifier" maxOccurs="100"/>
 *                                         &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Common}PayerIdentification" maxOccurs="100"/>
 *                                       &lt;/choice>
 *                                     &lt;/restriction>
 *                                   &lt;/complexContent>
 *                                 &lt;/complexType>
 *                               &lt;/element>
 *                               &lt;element name="ServicesCodesList">
 *                                 &lt;complexType>
 *                                   &lt;complexContent>
 *                                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                       &lt;sequence>
 *                                         &lt;element name="ServiceCode" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="100"/>
 *                                       &lt;/sequence>
 *                                     &lt;/restriction>
 *                                   &lt;/complexContent>
 *                                 &lt;/complexType>
 *                               &lt;/element>
 *                             &lt;/choice>
 *                             &lt;element name="Timeslot" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;attribute name="startDate" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *                                     &lt;attribute name="endDate" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                           &lt;attribute name="AllDateCatalog" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="AdditionRestrictions" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="SubordinateIdList" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;choice maxOccurs="100">
 *                                       &lt;element name="TaxpayerIdentification">
 *                                         &lt;complexType>
 *                                           &lt;complexContent>
 *                                             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                               &lt;attribute name="inn" use="required" type="{http://roskazna.ru/gisgmp/xsd/116/Common}INNType" />
 *                                               &lt;attribute name="kpp" type="{http://roskazna.ru/gisgmp/xsd/116/Common}KPPType" />
 *                                             &lt;/restriction>
 *                                           &lt;/complexContent>
 *                                         &lt;/complexType>
 *                                       &lt;/element>
 *                                       &lt;element name="PayeeID">
 *                                         &lt;simpleType>
 *                                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                             &lt;minLength value="6"/>
 *                                             &lt;whiteSpace value="collapse"/>
 *                                             &lt;maxLength value="32"/>
 *                                           &lt;/restriction>
 *                                         &lt;/simpleType>
 *                                       &lt;/element>
 *                                     &lt;/choice>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="KBKClassifier" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="KBK" type="{http://roskazna.ru/gisgmp/xsd/116/Common}KBKType" maxOccurs="100"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="OKTMOClassifier" minOccurs="0">
 *                               &lt;complexType>
 *                                 &lt;complexContent>
 *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                                     &lt;sequence>
 *                                       &lt;element name="OKTMO" type="{http://roskazna.ru/gisgmp/xsd/116/Common}OKTMOType" maxOccurs="100"/>
 *                                     &lt;/sequence>
 *                                   &lt;/restriction>
 *                                 &lt;/complexContent>
 *                               &lt;/complexType>
 *                             &lt;/element>
 *                             &lt;element name="Exclude" minOccurs="0">
 *                               &lt;simpleType>
 *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                                   &lt;enumeration value="ZERO-UIN"/>
 *                                 &lt;/restriction>
 *                               &lt;/simpleType>
 *                             &lt;/element>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Paging" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="pageNumber" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                       &lt;minInclusive value="1"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *                 &lt;attribute name="pageLength" use="required">
 *                   &lt;simpleType>
 *                     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *                       &lt;minInclusive value="1"/>
 *                     &lt;/restriction>
 *                   &lt;/simpleType>
 *                 &lt;/attribute>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://www.w3.org/2000/09/xmldsig#}Signature" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="Id">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}ID">
 *             &lt;maxLength value="50"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="kind" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;enumeration value="PAYMENT"/>
 *             &lt;enumeration value="PAYMENTMODIFIED"/>
 *             &lt;enumeration value="PAYMENTUNMATCHED"/>
 *             &lt;enumeration value="PAYMENTCANCELLED"/>
 *             &lt;enumeration value="PAYMENTMAINCHARGE"/>
 *             &lt;enumeration value="CHARGE"/>
 *             &lt;enumeration value="CHARGENOTFULLMATCHED"/>
 *             &lt;enumeration value="QUITTANCE"/>
 *             &lt;enumeration value="ALLQUITTANCE"/>
 *             &lt;enumeration value="CHARGESTATUS"/>
 *             &lt;enumeration value="CHARGE-PRIOR"/>
 *             &lt;enumeration value="CHARGE-PRIOR-NOTFULLMATCHED"/>
 *             &lt;enumeration value="CHARGE-PRIOR-STATUS"/>
 *             &lt;enumeration value="TEMP-CHARGING"/>
 *             &lt;enumeration value="TEMP-CHARGING-STATUS"/>
 *             &lt;enumeration value="TEMP-CHARGING-NOTFULLMATCHED"/>
 *             &lt;enumeration value="CATALOG"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="originatorID" type="{http://roskazna.ru/gisgmp/xsd/116/Common}URNType" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataRequest", propOrder = {
    "filter",
    "paging",
    "signature"
})
public class DataRequest {

    @XmlElement(name = "Filter", required = true)
    protected DataRequest.Filter filter;
    @XmlElement(name = "Paging")
    protected DataRequest.Paging paging;
    @XmlElement(name = "Signature", namespace = "http://www.w3.org/2000/09/xmldsig#")
    protected SignatureType signature;
    @XmlAttribute(name = "Id")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    protected String id;
    @XmlAttribute(required = true)
    protected String kind;
    @XmlAttribute
    protected String originatorID;

    /**
     * Gets the value of the filter property.
     * 
     * @return
     *     possible object is
     *     {@link DataRequest.Filter }
     *     
     */
    public DataRequest.Filter getFilter() {
        return filter;
    }

    /**
     * Sets the value of the filter property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataRequest.Filter }
     *     
     */
    public void setFilter(DataRequest.Filter value) {
        this.filter = value;
    }

    /**
     * Gets the value of the paging property.
     * 
     * @return
     *     possible object is
     *     {@link DataRequest.Paging }
     *     
     */
    public DataRequest.Paging getPaging() {
        return paging;
    }

    /**
     * Sets the value of the paging property.
     * 
     * @param value
     *     allowed object is
     *     {@link DataRequest.Paging }
     *     
     */
    public void setPaging(DataRequest.Paging value) {
        this.paging = value;
    }

    /**
     * ������� ��������� ���������� ��������������, ��������������� ������
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
     * Gets the value of the kind property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKind() {
        return kind;
    }

    /**
     * Sets the value of the kind property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKind(String value) {
        this.kind = value;
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
     *         &lt;element name="Conditions">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;choice minOccurs="0">
     *                     &lt;element name="ChargesIdentifiers">
     *                       &lt;complexType>
     *                         &lt;complexContent>
     *                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                             &lt;sequence>
     *                               &lt;element name="SupplierBillID" maxOccurs="100">
     *                                 &lt;simpleType>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
     *                                     &lt;minLength value="1"/>
     *                                     &lt;maxLength value="25"/>
     *                                     &lt;pattern value="\c{20}"/>
     *                                     &lt;pattern value="\c{25}"/>
     *                                   &lt;/restriction>
     *                                 &lt;/simpleType>
     *                               &lt;/element>
     *                             &lt;/sequence>
     *                           &lt;/restriction>
     *                         &lt;/complexContent>
     *                       &lt;/complexType>
     *                     &lt;/element>
     *                     &lt;element name="Payers">
     *                       &lt;complexType>
     *                         &lt;complexContent>
     *                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                             &lt;choice>
     *                               &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Common}PayerIdentifier" maxOccurs="100"/>
     *                               &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Common}PayerIdentification" maxOccurs="100"/>
     *                             &lt;/choice>
     *                           &lt;/restriction>
     *                         &lt;/complexContent>
     *                       &lt;/complexType>
     *                     &lt;/element>
     *                     &lt;element name="ServicesCodesList">
     *                       &lt;complexType>
     *                         &lt;complexContent>
     *                           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                             &lt;sequence>
     *                               &lt;element name="ServiceCode" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="100"/>
     *                             &lt;/sequence>
     *                           &lt;/restriction>
     *                         &lt;/complexContent>
     *                       &lt;/complexType>
     *                     &lt;/element>
     *                   &lt;/choice>
     *                   &lt;element name="Timeslot" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;attribute name="startDate" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
     *                           &lt;attribute name="endDate" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                 &lt;/sequence>
     *                 &lt;attribute name="AllDateCatalog" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="AdditionRestrictions" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="SubordinateIdList" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;choice maxOccurs="100">
     *                             &lt;element name="TaxpayerIdentification">
     *                               &lt;complexType>
     *                                 &lt;complexContent>
     *                                   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                                     &lt;attribute name="inn" use="required" type="{http://roskazna.ru/gisgmp/xsd/116/Common}INNType" />
     *                                     &lt;attribute name="kpp" type="{http://roskazna.ru/gisgmp/xsd/116/Common}KPPType" />
     *                                   &lt;/restriction>
     *                                 &lt;/complexContent>
     *                               &lt;/complexType>
     *                             &lt;/element>
     *                             &lt;element name="PayeeID">
     *                               &lt;simpleType>
     *                                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                                   &lt;minLength value="6"/>
     *                                   &lt;whiteSpace value="collapse"/>
     *                                   &lt;maxLength value="32"/>
     *                                 &lt;/restriction>
     *                               &lt;/simpleType>
     *                             &lt;/element>
     *                           &lt;/choice>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="KBKClassifier" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="KBK" type="{http://roskazna.ru/gisgmp/xsd/116/Common}KBKType" maxOccurs="100"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="OKTMOClassifier" minOccurs="0">
     *                     &lt;complexType>
     *                       &lt;complexContent>
     *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                           &lt;sequence>
     *                             &lt;element name="OKTMO" type="{http://roskazna.ru/gisgmp/xsd/116/Common}OKTMOType" maxOccurs="100"/>
     *                           &lt;/sequence>
     *                         &lt;/restriction>
     *                       &lt;/complexContent>
     *                     &lt;/complexType>
     *                   &lt;/element>
     *                   &lt;element name="Exclude" minOccurs="0">
     *                     &lt;simpleType>
     *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *                         &lt;enumeration value="ZERO-UIN"/>
     *                       &lt;/restriction>
     *                     &lt;/simpleType>
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
    @XmlType(name = "", propOrder = {
        "conditions",
        "additionRestrictions"
    })
    public static class Filter {

        @XmlElement(name = "Conditions", required = true)
        protected DataRequest.Filter.Conditions conditions;
        @XmlElement(name = "AdditionRestrictions")
        protected DataRequest.Filter.AdditionRestrictions additionRestrictions;

        /**
         * Gets the value of the conditions property.
         * 
         * @return
         *     possible object is
         *     {@link DataRequest.Filter.Conditions }
         *     
         */
        public DataRequest.Filter.Conditions getConditions() {
            return conditions;
        }

        /**
         * Sets the value of the conditions property.
         * 
         * @param value
         *     allowed object is
         *     {@link DataRequest.Filter.Conditions }
         *     
         */
        public void setConditions(DataRequest.Filter.Conditions value) {
            this.conditions = value;
        }

        /**
         * Gets the value of the additionRestrictions property.
         * 
         * @return
         *     possible object is
         *     {@link DataRequest.Filter.AdditionRestrictions }
         *     
         */
        public DataRequest.Filter.AdditionRestrictions getAdditionRestrictions() {
            return additionRestrictions;
        }

        /**
         * Sets the value of the additionRestrictions property.
         * 
         * @param value
         *     allowed object is
         *     {@link DataRequest.Filter.AdditionRestrictions }
         *     
         */
        public void setAdditionRestrictions(DataRequest.Filter.AdditionRestrictions value) {
            this.additionRestrictions = value;
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
         *         &lt;element name="SubordinateIdList" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;choice maxOccurs="100">
         *                   &lt;element name="TaxpayerIdentification">
         *                     &lt;complexType>
         *                       &lt;complexContent>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                           &lt;attribute name="inn" use="required" type="{http://roskazna.ru/gisgmp/xsd/116/Common}INNType" />
         *                           &lt;attribute name="kpp" type="{http://roskazna.ru/gisgmp/xsd/116/Common}KPPType" />
         *                         &lt;/restriction>
         *                       &lt;/complexContent>
         *                     &lt;/complexType>
         *                   &lt;/element>
         *                   &lt;element name="PayeeID">
         *                     &lt;simpleType>
         *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *                         &lt;minLength value="6"/>
         *                         &lt;whiteSpace value="collapse"/>
         *                         &lt;maxLength value="32"/>
         *                       &lt;/restriction>
         *                     &lt;/simpleType>
         *                   &lt;/element>
         *                 &lt;/choice>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="KBKClassifier" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="KBK" type="{http://roskazna.ru/gisgmp/xsd/116/Common}KBKType" maxOccurs="100"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="OKTMOClassifier" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;sequence>
         *                   &lt;element name="OKTMO" type="{http://roskazna.ru/gisgmp/xsd/116/Common}OKTMOType" maxOccurs="100"/>
         *                 &lt;/sequence>
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *         &lt;element name="Exclude" minOccurs="0">
         *           &lt;simpleType>
         *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
         *               &lt;enumeration value="ZERO-UIN"/>
         *             &lt;/restriction>
         *           &lt;/simpleType>
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
            "subordinateIdList",
            "kbkClassifier",
            "oktmoClassifier",
            "exclude"
        })
        public static class AdditionRestrictions {

            @XmlElement(name = "SubordinateIdList")
            protected DataRequest.Filter.AdditionRestrictions.SubordinateIdList subordinateIdList;
            @XmlElement(name = "KBKClassifier")
            protected DataRequest.Filter.AdditionRestrictions.KBKClassifier kbkClassifier;
            @XmlElement(name = "OKTMOClassifier")
            protected DataRequest.Filter.AdditionRestrictions.OKTMOClassifier oktmoClassifier;
            @XmlElement(name = "Exclude")
            protected String exclude;

            /**
             * Gets the value of the subordinateIdList property.
             * 
             * @return
             *     possible object is
             *     {@link DataRequest.Filter.AdditionRestrictions.SubordinateIdList }
             *     
             */
            public DataRequest.Filter.AdditionRestrictions.SubordinateIdList getSubordinateIdList() {
                return subordinateIdList;
            }

            /**
             * Sets the value of the subordinateIdList property.
             * 
             * @param value
             *     allowed object is
             *     {@link DataRequest.Filter.AdditionRestrictions.SubordinateIdList }
             *     
             */
            public void setSubordinateIdList(DataRequest.Filter.AdditionRestrictions.SubordinateIdList value) {
                this.subordinateIdList = value;
            }

            /**
             * Gets the value of the kbkClassifier property.
             * 
             * @return
             *     possible object is
             *     {@link DataRequest.Filter.AdditionRestrictions.KBKClassifier }
             *     
             */
            public DataRequest.Filter.AdditionRestrictions.KBKClassifier getKBKClassifier() {
                return kbkClassifier;
            }

            /**
             * Sets the value of the kbkClassifier property.
             * 
             * @param value
             *     allowed object is
             *     {@link DataRequest.Filter.AdditionRestrictions.KBKClassifier }
             *     
             */
            public void setKBKClassifier(DataRequest.Filter.AdditionRestrictions.KBKClassifier value) {
                this.kbkClassifier = value;
            }

            /**
             * Gets the value of the oktmoClassifier property.
             * 
             * @return
             *     possible object is
             *     {@link DataRequest.Filter.AdditionRestrictions.OKTMOClassifier }
             *     
             */
            public DataRequest.Filter.AdditionRestrictions.OKTMOClassifier getOKTMOClassifier() {
                return oktmoClassifier;
            }

            /**
             * Sets the value of the oktmoClassifier property.
             * 
             * @param value
             *     allowed object is
             *     {@link DataRequest.Filter.AdditionRestrictions.OKTMOClassifier }
             *     
             */
            public void setOKTMOClassifier(DataRequest.Filter.AdditionRestrictions.OKTMOClassifier value) {
                this.oktmoClassifier = value;
            }

            /**
             * Gets the value of the exclude property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getExclude() {
                return exclude;
            }

            /**
             * Sets the value of the exclude property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setExclude(String value) {
                this.exclude = value;
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
             *         &lt;element name="KBK" type="{http://roskazna.ru/gisgmp/xsd/116/Common}KBKType" maxOccurs="100"/>
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
                "kbk"
            })
            public static class KBKClassifier {

                @XmlElement(name = "KBK", required = true)
                protected List<String> kbk;

                /**
                 * Gets the value of the kbk property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the kbk property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getKBK().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link String }
                 * 
                 * 
                 */
                public List<String> getKBK() {
                    if (kbk == null) {
                        kbk = new ArrayList<String>();
                    }
                    return this.kbk;
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
             *         &lt;element name="OKTMO" type="{http://roskazna.ru/gisgmp/xsd/116/Common}OKTMOType" maxOccurs="100"/>
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
                "oktmo"
            })
            public static class OKTMOClassifier {

                @XmlElement(name = "OKTMO", required = true)
                protected List<String> oktmo;

                /**
                 * Gets the value of the oktmo property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the oktmo property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getOKTMO().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link String }
                 * 
                 * 
                 */
                public List<String> getOKTMO() {
                    if (oktmo == null) {
                        oktmo = new ArrayList<String>();
                    }
                    return this.oktmo;
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
             *       &lt;choice maxOccurs="100">
             *         &lt;element name="TaxpayerIdentification">
             *           &lt;complexType>
             *             &lt;complexContent>
             *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
             *                 &lt;attribute name="inn" use="required" type="{http://roskazna.ru/gisgmp/xsd/116/Common}INNType" />
             *                 &lt;attribute name="kpp" type="{http://roskazna.ru/gisgmp/xsd/116/Common}KPPType" />
             *               &lt;/restriction>
             *             &lt;/complexContent>
             *           &lt;/complexType>
             *         &lt;/element>
             *         &lt;element name="PayeeID">
             *           &lt;simpleType>
             *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
             *               &lt;minLength value="6"/>
             *               &lt;whiteSpace value="collapse"/>
             *               &lt;maxLength value="32"/>
             *             &lt;/restriction>
             *           &lt;/simpleType>
             *         &lt;/element>
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
                "taxpayerIdentificationOrPayeeID"
            })
            public static class SubordinateIdList {

                @XmlElements({
                    @XmlElement(name = "TaxpayerIdentification", type = DataRequest.Filter.AdditionRestrictions.SubordinateIdList.TaxpayerIdentification.class),
                    @XmlElement(name = "PayeeID", type = String.class)
                })
                protected List<Object> taxpayerIdentificationOrPayeeID;

                /**
                 * Gets the value of the taxpayerIdentificationOrPayeeID property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the taxpayerIdentificationOrPayeeID property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getTaxpayerIdentificationOrPayeeID().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link DataRequest.Filter.AdditionRestrictions.SubordinateIdList.TaxpayerIdentification }
                 * {@link String }
                 * 
                 * 
                 */
                public List<Object> getTaxpayerIdentificationOrPayeeID() {
                    if (taxpayerIdentificationOrPayeeID == null) {
                        taxpayerIdentificationOrPayeeID = new ArrayList<Object>();
                    }
                    return this.taxpayerIdentificationOrPayeeID;
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
                 *       &lt;attribute name="inn" use="required" type="{http://roskazna.ru/gisgmp/xsd/116/Common}INNType" />
                 *       &lt;attribute name="kpp" type="{http://roskazna.ru/gisgmp/xsd/116/Common}KPPType" />
                 *     &lt;/restriction>
                 *   &lt;/complexContent>
                 * &lt;/complexType>
                 * </pre>
                 * 
                 * 
                 */
                @XmlAccessorType(XmlAccessType.FIELD)
                @XmlType(name = "")
                public static class TaxpayerIdentification {

                    @XmlAttribute(required = true)
                    protected String inn;
                    @XmlAttribute
                    protected String kpp;

                    /**
                     * Gets the value of the inn property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getInn() {
                        return inn;
                    }

                    /**
                     * Sets the value of the inn property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setInn(String value) {
                        this.inn = value;
                    }

                    /**
                     * Gets the value of the kpp property.
                     * 
                     * @return
                     *     possible object is
                     *     {@link String }
                     *     
                     */
                    public String getKpp() {
                        return kpp;
                    }

                    /**
                     * Sets the value of the kpp property.
                     * 
                     * @param value
                     *     allowed object is
                     *     {@link String }
                     *     
                     */
                    public void setKpp(String value) {
                        this.kpp = value;
                    }

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
         *         &lt;choice minOccurs="0">
         *           &lt;element name="ChargesIdentifiers">
         *             &lt;complexType>
         *               &lt;complexContent>
         *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                   &lt;sequence>
         *                     &lt;element name="SupplierBillID" maxOccurs="100">
         *                       &lt;simpleType>
         *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
         *                           &lt;minLength value="1"/>
         *                           &lt;maxLength value="25"/>
         *                           &lt;pattern value="\c{20}"/>
         *                           &lt;pattern value="\c{25}"/>
         *                         &lt;/restriction>
         *                       &lt;/simpleType>
         *                     &lt;/element>
         *                   &lt;/sequence>
         *                 &lt;/restriction>
         *               &lt;/complexContent>
         *             &lt;/complexType>
         *           &lt;/element>
         *           &lt;element name="Payers">
         *             &lt;complexType>
         *               &lt;complexContent>
         *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                   &lt;choice>
         *                     &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Common}PayerIdentifier" maxOccurs="100"/>
         *                     &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Common}PayerIdentification" maxOccurs="100"/>
         *                   &lt;/choice>
         *                 &lt;/restriction>
         *               &lt;/complexContent>
         *             &lt;/complexType>
         *           &lt;/element>
         *           &lt;element name="ServicesCodesList">
         *             &lt;complexType>
         *               &lt;complexContent>
         *                 &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                   &lt;sequence>
         *                     &lt;element name="ServiceCode" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="100"/>
         *                   &lt;/sequence>
         *                 &lt;/restriction>
         *               &lt;/complexContent>
         *             &lt;/complexType>
         *           &lt;/element>
         *         &lt;/choice>
         *         &lt;element name="Timeslot" minOccurs="0">
         *           &lt;complexType>
         *             &lt;complexContent>
         *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *                 &lt;attribute name="startDate" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
         *                 &lt;attribute name="endDate" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
         *               &lt;/restriction>
         *             &lt;/complexContent>
         *           &lt;/complexType>
         *         &lt;/element>
         *       &lt;/sequence>
         *       &lt;attribute name="AllDateCatalog" type="{http://www.w3.org/2001/XMLSchema}boolean" />
         *     &lt;/restriction>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "chargesIdentifiers",
            "payers",
            "servicesCodesList",
            "timeslot"
        })
        public static class Conditions {

            @XmlElement(name = "ChargesIdentifiers")
            protected DataRequest.Filter.Conditions.ChargesIdentifiers chargesIdentifiers;
            @XmlElement(name = "Payers")
            protected DataRequest.Filter.Conditions.Payers payers;
            @XmlElement(name = "ServicesCodesList")
            protected DataRequest.Filter.Conditions.ServicesCodesList servicesCodesList;
            @XmlElement(name = "Timeslot")
            protected DataRequest.Filter.Conditions.Timeslot timeslot;
            @XmlAttribute(name = "AllDateCatalog")
            protected Boolean allDateCatalog;

            /**
             * Gets the value of the chargesIdentifiers property.
             * 
             * @return
             *     possible object is
             *     {@link DataRequest.Filter.Conditions.ChargesIdentifiers }
             *     
             */
            public DataRequest.Filter.Conditions.ChargesIdentifiers getChargesIdentifiers() {
                return chargesIdentifiers;
            }

            /**
             * Sets the value of the chargesIdentifiers property.
             * 
             * @param value
             *     allowed object is
             *     {@link DataRequest.Filter.Conditions.ChargesIdentifiers }
             *     
             */
            public void setChargesIdentifiers(DataRequest.Filter.Conditions.ChargesIdentifiers value) {
                this.chargesIdentifiers = value;
            }

            /**
             * Gets the value of the payers property.
             * 
             * @return
             *     possible object is
             *     {@link DataRequest.Filter.Conditions.Payers }
             *     
             */
            public DataRequest.Filter.Conditions.Payers getPayers() {
                return payers;
            }

            /**
             * Sets the value of the payers property.
             * 
             * @param value
             *     allowed object is
             *     {@link DataRequest.Filter.Conditions.Payers }
             *     
             */
            public void setPayers(DataRequest.Filter.Conditions.Payers value) {
                this.payers = value;
            }

            /**
             * Gets the value of the servicesCodesList property.
             * 
             * @return
             *     possible object is
             *     {@link DataRequest.Filter.Conditions.ServicesCodesList }
             *     
             */
            public DataRequest.Filter.Conditions.ServicesCodesList getServicesCodesList() {
                return servicesCodesList;
            }

            /**
             * Sets the value of the servicesCodesList property.
             * 
             * @param value
             *     allowed object is
             *     {@link DataRequest.Filter.Conditions.ServicesCodesList }
             *     
             */
            public void setServicesCodesList(DataRequest.Filter.Conditions.ServicesCodesList value) {
                this.servicesCodesList = value;
            }

            /**
             * Gets the value of the timeslot property.
             * 
             * @return
             *     possible object is
             *     {@link DataRequest.Filter.Conditions.Timeslot }
             *     
             */
            public DataRequest.Filter.Conditions.Timeslot getTimeslot() {
                return timeslot;
            }

            /**
             * Sets the value of the timeslot property.
             * 
             * @param value
             *     allowed object is
             *     {@link DataRequest.Filter.Conditions.Timeslot }
             *     
             */
            public void setTimeslot(DataRequest.Filter.Conditions.Timeslot value) {
                this.timeslot = value;
            }

            /**
             * Gets the value of the allDateCatalog property.
             * 
             * @return
             *     possible object is
             *     {@link Boolean }
             *     
             */
            public Boolean isAllDateCatalog() {
                return allDateCatalog;
            }

            /**
             * Sets the value of the allDateCatalog property.
             * 
             * @param value
             *     allowed object is
             *     {@link Boolean }
             *     
             */
            public void setAllDateCatalog(Boolean value) {
                this.allDateCatalog = value;
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
             *         &lt;element name="SupplierBillID" maxOccurs="100">
             *           &lt;simpleType>
             *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}token">
             *               &lt;minLength value="1"/>
             *               &lt;maxLength value="25"/>
             *               &lt;pattern value="\c{20}"/>
             *               &lt;pattern value="\c{25}"/>
             *             &lt;/restriction>
             *           &lt;/simpleType>
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
                "supplierBillID"
            })
            public static class ChargesIdentifiers {

                @XmlElement(name = "SupplierBillID", required = true)
                @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
                protected List<String> supplierBillID;

                /**
                 * Gets the value of the supplierBillID property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the supplierBillID property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getSupplierBillID().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link String }
                 * 
                 * 
                 */
                public List<String> getSupplierBillID() {
                    if (supplierBillID == null) {
                        supplierBillID = new ArrayList<String>();
                    }
                    return this.supplierBillID;
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
             *         &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Common}PayerIdentifier" maxOccurs="100"/>
             *         &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Common}PayerIdentification" maxOccurs="100"/>
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
                "payerIdentifier",
                "payerIdentification"
            })
            public static class Payers {

                @XmlElement(name = "PayerIdentifier", namespace = "http://roskazna.ru/gisgmp/xsd/116/Common")
                protected List<String> payerIdentifier;
                @XmlElement(name = "PayerIdentification", namespace = "http://roskazna.ru/gisgmp/xsd/116/Common")
                protected List<PayerIdentificationType> payerIdentification;

                /**
                 * ������������� ����������� Gets the value of the payerIdentifier property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the payerIdentifier property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getPayerIdentifier().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link String }
                 * 
                 * 
                 */
                public List<String> getPayerIdentifier() {
                    if (payerIdentifier == null) {
                        payerIdentifier = new ArrayList<String>();
                    }
                    return this.payerIdentifier;
                }

                /**
                 * Gets the value of the payerIdentification property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the payerIdentification property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getPayerIdentification().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link PayerIdentificationType }
                 * 
                 * 
                 */
                public List<PayerIdentificationType> getPayerIdentification() {
                    if (payerIdentification == null) {
                        payerIdentification = new ArrayList<PayerIdentificationType>();
                    }
                    return this.payerIdentification;
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
             *         &lt;element name="ServiceCode" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="100"/>
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
                "serviceCode"
            })
            public static class ServicesCodesList {

                @XmlElement(name = "ServiceCode", required = true)
                protected List<String> serviceCode;

                /**
                 * Gets the value of the serviceCode property.
                 * 
                 * <p>
                 * This accessor method returns a reference to the live list,
                 * not a snapshot. Therefore any modification you make to the
                 * returned list will be present inside the JAXB object.
                 * This is why there is not a <CODE>set</CODE> method for the serviceCode property.
                 * 
                 * <p>
                 * For example, to add a new item, do as follows:
                 * <pre>
                 *    getServiceCode().add(newItem);
                 * </pre>
                 * 
                 * 
                 * <p>
                 * Objects of the following type(s) are allowed in the list
                 * {@link String }
                 * 
                 * 
                 */
                public List<String> getServiceCode() {
                    if (serviceCode == null) {
                        serviceCode = new ArrayList<String>();
                    }
                    return this.serviceCode;
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
             *       &lt;attribute name="startDate" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
             *       &lt;attribute name="endDate" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
             *     &lt;/restriction>
             *   &lt;/complexContent>
             * &lt;/complexType>
             * </pre>
             * 
             * 
             */
            @XmlAccessorType(XmlAccessType.FIELD)
            @XmlType(name = "")
            public static class Timeslot {

                @XmlAttribute(required = true)
                @XmlSchemaType(name = "dateTime")
                protected XMLGregorianCalendar startDate;
                @XmlAttribute(required = true)
                @XmlSchemaType(name = "dateTime")
                protected XMLGregorianCalendar endDate;

                /**
                 * Gets the value of the startDate property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public XMLGregorianCalendar getStartDate() {
                    return startDate;
                }

                /**
                 * Sets the value of the startDate property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public void setStartDate(XMLGregorianCalendar value) {
                    this.startDate = value;
                }

                /**
                 * Gets the value of the endDate property.
                 * 
                 * @return
                 *     possible object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public XMLGregorianCalendar getEndDate() {
                    return endDate;
                }

                /**
                 * Sets the value of the endDate property.
                 * 
                 * @param value
                 *     allowed object is
                 *     {@link XMLGregorianCalendar }
                 *     
                 */
                public void setEndDate(XMLGregorianCalendar value) {
                    this.endDate = value;
                }

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
     *       &lt;attribute name="pageNumber" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *             &lt;minInclusive value="1"/>
     *           &lt;/restriction>
     *         &lt;/simpleType>
     *       &lt;/attribute>
     *       &lt;attribute name="pageLength" use="required">
     *         &lt;simpleType>
     *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
     *             &lt;minInclusive value="1"/>
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
    public static class Paging {

        @XmlAttribute(required = true)
        protected int pageNumber;
        @XmlAttribute(required = true)
        protected int pageLength;

        /**
         * Gets the value of the pageNumber property.
         * 
         */
        public int getPageNumber() {
            return pageNumber;
        }

        /**
         * Sets the value of the pageNumber property.
         * 
         */
        public void setPageNumber(int value) {
            this.pageNumber = value;
        }

        /**
         * Gets the value of the pageLength property.
         * 
         */
        public int getPageLength() {
            return pageLength;
        }

        /**
         * Sets the value of the pageLength property.
         * 
         */
        public void setPageLength(int value) {
            this.pageLength = value;
        }

    }

}
