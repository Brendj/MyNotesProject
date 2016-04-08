
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.catalog;

import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.common.AdditionalDataType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.common.CommissionsType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.common.CurrencyCodeType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.common.Money;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.organization.PayeeType;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * ��� ��� �������� ������
 * 
 * <p>Java class for Service_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Service_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ServiceCategory" type="{http://roskazna.ru/gisgmp/xsd/116/Catalog}ServiceCategory_Type"/>
 *         &lt;element name="PaymentParameters" type="{http://roskazna.ru/gisgmp/xsd/116/Catalog}DescriptionParameters_Type" minOccurs="0"/>
 *         &lt;element name="CurrenciesServices" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="CurrencyCode" type="{http://roskazna.ru/gisgmp/xsd/116/Common}CurrencyCodeType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Organization}Payee"/>
 *         &lt;element name="PaymentKind">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
 *               &lt;totalDigits value="1"/>
 *               &lt;enumeration value="1"/>
 *               &lt;enumeration value="2"/>
 *               &lt;enumeration value="3"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="MinAmount" type="{http://roskazna.ru/gisgmp/xsd/116/Common}Money" minOccurs="0"/>
 *         &lt;element name="MaxAmount" type="{http://roskazna.ru/gisgmp/xsd/116/Common}Money" minOccurs="0"/>
 *         &lt;element name="Commissions" type="{http://roskazna.ru/gisgmp/xsd/116/Common}Commissions_Type" minOccurs="0"/>
 *         &lt;element name="Regions" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="Region" type="{http://roskazna.ru/gisgmp/xsd/116/Catalog}Region_Type" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="Subservices" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Catalog}Service" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Common}AdditionalData" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;minLength value="1"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="code" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;maxLength value="36"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="desc" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="expires" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="isActive" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="revisionDate" type="{http://www.w3.org/2001/XMLSchema}date" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Service_Type", propOrder = {
    "serviceCategory",
    "paymentParameters",
    "currenciesServices",
    "payee",
    "paymentKind",
    "minAmount",
    "maxAmount",
    "commissions",
    "regions",
    "subservices",
    "additionalData"
})
public class ServiceType {

    @XmlElement(name = "ServiceCategory", required = true)
    protected ServiceCategoryType serviceCategory;
    @XmlElement(name = "PaymentParameters")
    protected DescriptionParametersType paymentParameters;
    @XmlElement(name = "CurrenciesServices")
    protected CurrenciesServices currenciesServices;
    @XmlElement(name = "Payee", namespace = "http://roskazna.ru/gisgmp/xsd/116/Organization", required = true)
    protected PayeeType payee;
    @XmlElement(name = "PaymentKind", required = true)
    protected BigInteger paymentKind;
    @XmlElement(name = "MinAmount")
    protected Money minAmount;
    @XmlElement(name = "MaxAmount")
    protected Money maxAmount;
    @XmlElement(name = "Commissions")
    protected CommissionsType commissions;
    @XmlElement(name = "Regions")
    protected Regions regions;
    @XmlElement(name = "Subservices")
    protected Subservices subservices;
    @XmlElement(name = "AdditionalData", namespace = "http://roskazna.ru/gisgmp/xsd/116/Common")
    protected AdditionalDataType additionalData;
    @XmlAttribute(required = true)
    protected String name;
    @XmlAttribute(required = true)
    protected String code;
    @XmlAttribute
    protected String desc;
    @XmlAttribute
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar expires;
    @XmlAttribute(required = true)
    protected boolean isActive;
    @XmlAttribute
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar revisionDate;

    /**
     * Gets the value of the serviceCategory property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceCategoryType }
     *     
     */
    public ServiceCategoryType getServiceCategory() {
        return serviceCategory;
    }

    /**
     * Sets the value of the serviceCategory property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceCategoryType }
     *     
     */
    public void setServiceCategory(ServiceCategoryType value) {
        this.serviceCategory = value;
    }

    /**
     * Gets the value of the paymentParameters property.
     * 
     * @return
     *     possible object is
     *     {@link DescriptionParametersType }
     *     
     */
    public DescriptionParametersType getPaymentParameters() {
        return paymentParameters;
    }

    /**
     * Sets the value of the paymentParameters property.
     * 
     * @param value
     *     allowed object is
     *     {@link DescriptionParametersType }
     *     
     */
    public void setPaymentParameters(DescriptionParametersType value) {
        this.paymentParameters = value;
    }

    /**
     * Gets the value of the currenciesServices property.
     * 
     * @return
     *     possible object is
     *     {@link generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.catalog.ServiceType.CurrenciesServices }
     *     
     */
    public CurrenciesServices getCurrenciesServices() {
        return currenciesServices;
    }

    /**
     * Sets the value of the currenciesServices property.
     * 
     * @param value
     *     allowed object is
     *     {@link generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.catalog.ServiceType.CurrenciesServices }
     *     
     */
    public void setCurrenciesServices(CurrenciesServices value) {
        this.currenciesServices = value;
    }

    /**
     * ���������� �������
     * 
     * @return
     *     possible object is
     *     {@link PayeeType }
     *     
     */
    public PayeeType getPayee() {
        return payee;
    }

    /**
     * Sets the value of the payee property.
     * 
     * @param value
     *     allowed object is
     *     {@link PayeeType }
     *     
     */
    public void setPayee(PayeeType value) {
        this.payee = value;
    }

    /**
     * Gets the value of the paymentKind property.
     * 
     * @return
     *     possible object is
     *     {@link java.math.BigInteger }
     *     
     */
    public BigInteger getPaymentKind() {
        return paymentKind;
    }

    /**
     * Sets the value of the paymentKind property.
     * 
     * @param value
     *     allowed object is
     *     {@link java.math.BigInteger }
     *     
     */
    public void setPaymentKind(BigInteger value) {
        this.paymentKind = value;
    }

    /**
     * Gets the value of the minAmount property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getMinAmount() {
        return minAmount;
    }

    /**
     * Sets the value of the minAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setMinAmount(Money value) {
        this.minAmount = value;
    }

    /**
     * Gets the value of the maxAmount property.
     * 
     * @return
     *     possible object is
     *     {@link Money }
     *     
     */
    public Money getMaxAmount() {
        return maxAmount;
    }

    /**
     * Sets the value of the maxAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link Money }
     *     
     */
    public void setMaxAmount(Money value) {
        this.maxAmount = value;
    }

    /**
     * Gets the value of the commissions property.
     * 
     * @return
     *     possible object is
     *     {@link CommissionsType }
     *     
     */
    public CommissionsType getCommissions() {
        return commissions;
    }

    /**
     * Sets the value of the commissions property.
     * 
     * @param value
     *     allowed object is
     *     {@link CommissionsType }
     *     
     */
    public void setCommissions(CommissionsType value) {
        this.commissions = value;
    }

    /**
     * Gets the value of the regions property.
     * 
     * @return
     *     possible object is
     *     {@link generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.catalog.ServiceType.Regions }
     *     
     */
    public Regions getRegions() {
        return regions;
    }

    /**
     * Sets the value of the regions property.
     * 
     * @param value
     *     allowed object is
     *     {@link generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.catalog.ServiceType.Regions }
     *     
     */
    public void setRegions(Regions value) {
        this.regions = value;
    }

    /**
     * Gets the value of the subservices property.
     * 
     * @return
     *     possible object is
     *     {@link generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.catalog.ServiceType.Subservices }
     *     
     */
    public Subservices getSubservices() {
        return subservices;
    }

    /**
     * Sets the value of the subservices property.
     * 
     * @param value
     *     allowed object is
     *     {@link generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.catalog.ServiceType.Subservices }
     *     
     */
    public void setSubservices(Subservices value) {
        this.subservices = value;
    }

    /**
     * Gets the value of the additionalData property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalDataType }
     *     
     */
    public AdditionalDataType getAdditionalData() {
        return additionalData;
    }

    /**
     * Sets the value of the additionalData property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalDataType }
     *     
     */
    public void setAdditionalData(AdditionalDataType value) {
        this.additionalData = value;
    }

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the desc property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDesc() {
        return desc;
    }

    /**
     * Sets the value of the desc property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDesc(String value) {
        this.desc = value;
    }

    /**
     * Gets the value of the expires property.
     * 
     * @return
     *     possible object is
     *     {@link javax.xml.datatype.XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getExpires() {
        return expires;
    }

    /**
     * Sets the value of the expires property.
     * 
     * @param value
     *     allowed object is
     *     {@link javax.xml.datatype.XMLGregorianCalendar }
     *     
     */
    public void setExpires(XMLGregorianCalendar value) {
        this.expires = value;
    }

    /**
     * Gets the value of the isActive property.
     * 
     */
    public boolean isIsActive() {
        return isActive;
    }

    /**
     * Sets the value of the isActive property.
     * 
     */
    public void setIsActive(boolean value) {
        this.isActive = value;
    }

    /**
     * Gets the value of the revisionDate property.
     * 
     * @return
     *     possible object is
     *     {@link javax.xml.datatype.XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRevisionDate() {
        return revisionDate;
    }

    /**
     * Sets the value of the revisionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link javax.xml.datatype.XMLGregorianCalendar }
     *     
     */
    public void setRevisionDate(XMLGregorianCalendar value) {
        this.revisionDate = value;
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
     *         &lt;element name="CurrencyCode" type="{http://roskazna.ru/gisgmp/xsd/116/Common}CurrencyCodeType" maxOccurs="unbounded"/>
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
        "currencyCode"
    })
    public static class CurrenciesServices {

        @XmlElement(name = "CurrencyCode", required = true)
        protected List<CurrencyCodeType> currencyCode;

        /**
         * Gets the value of the currencyCode property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the currencyCode property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCurrencyCode().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link CurrencyCodeType }
         * 
         * 
         */
        public List<CurrencyCodeType> getCurrencyCode() {
            if (currencyCode == null) {
                currencyCode = new ArrayList<CurrencyCodeType>();
            }
            return this.currencyCode;
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
     *         &lt;element name="Region" type="{http://roskazna.ru/gisgmp/xsd/116/Catalog}Region_Type" maxOccurs="unbounded"/>
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
        "region"
    })
    public static class Regions {

        @XmlElement(name = "Region", required = true)
        protected List<RegionType> region;

        /**
         * Gets the value of the region property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the region property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getRegion().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link RegionType }
         * 
         * 
         */
        public List<RegionType> getRegion() {
            if (region == null) {
                region = new ArrayList<RegionType>();
            }
            return this.region;
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
     *         &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Catalog}Service" maxOccurs="unbounded"/>
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
        "service"
    })
    public static class Subservices {

        @XmlElement(name = "Service", required = true)
        protected List<ServiceType> service;

        /**
         * Gets the value of the service property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the service property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getService().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.catalog.ServiceType }
         * 
         * 
         */
        public List<ServiceType> getService() {
            if (service == null) {
                service = new ArrayList<ServiceType>();
            }
            return this.service;
        }

    }

}
