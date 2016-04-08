
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.pgu_datarequest;

import generated.smev.gisgmp.wsdl.org.w3._2000._09.xmldsig_.SignatureType;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;


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
 *                                       &lt;sequence>
 *                                         &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Common}PayerIdentifier" maxOccurs="100"/>
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
    protected Filter filter;
    @XmlElement(name = "Paging")
    protected Paging paging;
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
     *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.DataRequest.Filter }
     *     
     */
    public Filter getFilter() {
        return filter;
    }

    /**
     * Sets the value of the filter property.
     * 
     * @param value
     *     allowed object is
     *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.DataRequest.Filter }
     *     
     */
    public void setFilter(Filter value) {
        this.filter = value;
    }

    /**
     * Gets the value of the paging property.
     * 
     * @return
     *     possible object is
     *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.DataRequest.Paging }
     *     
     */
    public Paging getPaging() {
        return paging;
    }

    /**
     * Sets the value of the paging property.
     * 
     * @param value
     *     allowed object is
     *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.DataRequest.Paging }
     *     
     */
    public void setPaging(Paging value) {
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
     *                             &lt;sequence>
     *                               &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Common}PayerIdentifier" maxOccurs="100"/>
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
        protected Conditions conditions;
        @XmlElement(name = "AdditionRestrictions")
        protected AdditionRestrictions additionRestrictions;

        /**
         * Gets the value of the conditions property.
         * 
         * @return
         *     possible object is
         *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.DataRequest.Filter.Conditions }
         *     
         */
        public Conditions getConditions() {
            return conditions;
        }

        /**
         * Sets the value of the conditions property.
         * 
         * @param value
         *     allowed object is
         *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.DataRequest.Filter.Conditions }
         *     
         */
        public void setConditions(Conditions value) {
            this.conditions = value;
        }

        /**
         * Gets the value of the additionRestrictions property.
         * 
         * @return
         *     possible object is
         *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.DataRequest.Filter.AdditionRestrictions }
         *     
         */
        public AdditionRestrictions getAdditionRestrictions() {
            return additionRestrictions;
        }

        /**
         * Sets the value of the additionRestrictions property.
         * 
         * @param value
         *     allowed object is
         *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.DataRequest.Filter.AdditionRestrictions }
         *     
         */
        public void setAdditionRestrictions(AdditionRestrictions value) {
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
            protected SubordinateIdList subordinateIdList;
            @XmlElement(name = "KBKClassifier")
            protected KBKClassifier kbkClassifier;
            @XmlElement(name = "OKTMOClassifier")
            protected OKTMOClassifier oktmoClassifier;
            @XmlElement(name = "Exclude")
            protected String exclude;

            /**
             * Gets the value of the subordinateIdList property.
             * 
             * @return
             *     possible object is
             *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.DataRequest.Filter.AdditionRestrictions.SubordinateIdList }
             *     
             */
            public SubordinateIdList getSubordinateIdList() {
                return subordinateIdList;
            }

            /**
             * Sets the value of the subordinateIdList property.
             * 
             * @param value
             *     allowed object is
             *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.DataRequest.Filter.AdditionRestrictions.SubordinateIdList }
             *     
             */
            public void setSubordinateIdList(SubordinateIdList value) {
                this.subordinateIdList = value;
            }

            /**
             * Gets the value of the kbkClassifier property.
             * 
             * @return
             *     possible object is
             *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.DataRequest.Filter.AdditionRestrictions.KBKClassifier }
             *     
             */
            public KBKClassifier getKBKClassifier() {
                return kbkClassifier;
            }

            /**
             * Sets the value of the kbkClassifier property.
             * 
             * @param value
             *     allowed object is
             *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.DataRequest.Filter.AdditionRestrictions.KBKClassifier }
             *     
             */
            public void setKBKClassifier(KBKClassifier value) {
                this.kbkClassifier = value;
            }

            /**
             * Gets the value of the oktmoClassifier property.
             * 
             * @return
             *     possible object is
             *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.DataRequest.Filter.AdditionRestrictions.OKTMOClassifier }
             *     
             */
            public OKTMOClassifier getOKTMOClassifier() {
                return oktmoClassifier;
            }

            /**
             * Sets the value of the oktmoClassifier property.
             * 
             * @param value
             *     allowed object is
             *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.DataRequest.Filter.AdditionRestrictions.OKTMOClassifier }
             *     
             */
            public void setOKTMOClassifier(OKTMOClassifier value) {
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
                    @XmlElement(name = "TaxpayerIdentification", type = TaxpayerIdentification.class),
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
                 * {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.DataRequest.Filter.AdditionRestrictions.SubordinateIdList.TaxpayerIdentification }
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
         *                   &lt;sequence>
         *                     &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Common}PayerIdentifier" maxOccurs="100"/>
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
            "timeslot"
        })
        public static class Conditions {

            @XmlElement(name = "ChargesIdentifiers")
            protected ChargesIdentifiers chargesIdentifiers;
            @XmlElement(name = "Payers")
            protected Payers payers;
            @XmlElement(name = "Timeslot")
            protected Timeslot timeslot;

            /**
             * Gets the value of the chargesIdentifiers property.
             * 
             * @return
             *     possible object is
             *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.DataRequest.Filter.Conditions.ChargesIdentifiers }
             *     
             */
            public ChargesIdentifiers getChargesIdentifiers() {
                return chargesIdentifiers;
            }

            /**
             * Sets the value of the chargesIdentifiers property.
             * 
             * @param value
             *     allowed object is
             *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.DataRequest.Filter.Conditions.ChargesIdentifiers }
             *     
             */
            public void setChargesIdentifiers(ChargesIdentifiers value) {
                this.chargesIdentifiers = value;
            }

            /**
             * Gets the value of the payers property.
             * 
             * @return
             *     possible object is
             *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.DataRequest.Filter.Conditions.Payers }
             *     
             */
            public Payers getPayers() {
                return payers;
            }

            /**
             * Sets the value of the payers property.
             * 
             * @param value
             *     allowed object is
             *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.DataRequest.Filter.Conditions.Payers }
             *     
             */
            public void setPayers(Payers value) {
                this.payers = value;
            }

            /**
             * Gets the value of the timeslot property.
             * 
             * @return
             *     possible object is
             *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.DataRequest.Filter.Conditions.Timeslot }
             *     
             */
            public Timeslot getTimeslot() {
                return timeslot;
            }

            /**
             * Sets the value of the timeslot property.
             * 
             * @param value
             *     allowed object is
             *     {@link generated.smev.gisgmp.wsdl.ru.roskazna.gisgmp.xsd._116.pgu_datarequest.DataRequest.Filter.Conditions.Timeslot }
             *     
             */
            public void setTimeslot(Timeslot value) {
                this.timeslot = value;
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
             *       &lt;sequence>
             *         &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Common}PayerIdentifier" maxOccurs="100"/>
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
                "payerIdentifier"
            })
            public static class Payers {

                @XmlElement(name = "PayerIdentifier", namespace = "http://roskazna.ru/gisgmp/xsd/116/Common", required = true)
                protected List<String> payerIdentifier;

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
                 *     {@link javax.xml.datatype.XMLGregorianCalendar }
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
                 *     {@link javax.xml.datatype.XMLGregorianCalendar }
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
                 *     {@link javax.xml.datatype.XMLGregorianCalendar }
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
                 *     {@link javax.xml.datatype.XMLGregorianCalendar }
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
