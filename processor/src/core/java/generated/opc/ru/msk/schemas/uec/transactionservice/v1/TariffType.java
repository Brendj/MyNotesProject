
package generated.opc.ru.msk.schemas.uec.transactionservice.v1;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Сведения о тарифе
 * 
 * <p>Java class for TariffType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TariffType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="100"/>
 *               &lt;minLength value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="calcPeriod" type="{http://schemas.msk.ru/uec/TransactionService/v1}CalcPeriodCode" minOccurs="0"/>
 *         &lt;element name="subscriptionValue" type="{http://schemas.msk.ru/uec/TransactionService/v1}AmountType" minOccurs="0"/>
 *         &lt;element name="validFrom" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="validTo" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="service" type="{http://schemas.msk.ru/uec/TransactionService/v1}ServiceIdentificationType"/>
 *         &lt;element name="serviceProviderCode" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;minLength value="1"/>
 *               &lt;maxLength value="36"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="isActive" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="modelCalculation" type="{http://schemas.msk.ru/uec/TransactionService/v1}ModelCalculationCode"/>
 *         &lt;element name="currency">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="3"/>
 *               &lt;minLength value="1"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="metaData" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="metaDataItem" type="{http://schemas.msk.ru/uec/TransactionService/v1}TariffMetaDataItemType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="tariffByFixedValue" type="{http://schemas.msk.ru/uec/TransactionService/v1}TariffByFixedValueType" minOccurs="0"/>
 *         &lt;element name="tariffByVolumes" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="tariffByVolumeItem" type="{http://schemas.msk.ru/uec/TransactionService/v1}TariffByVolumeType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="tariffBindingSPList" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="tariffBindingSPItem" type="{http://schemas.msk.ru/uec/TransactionService/v1}TariffBindingSPItemType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="tariffBindingSCList" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="tariffBindingSCItem" type="{http://schemas.msk.ru/uec/TransactionService/v1}TariffBindingSCItemType" maxOccurs="unbounded"/>
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
@XmlType(name = "TariffType", propOrder = {
    "name",
    "calcPeriod",
    "subscriptionValue",
    "validFrom",
    "validTo",
    "service",
    "serviceProviderCode",
    "isActive",
    "modelCalculation",
    "currency",
    "metaData",
    "tariffByFixedValue",
    "tariffByVolumes",
    "tariffBindingSPList",
    "tariffBindingSCList"
})
public class TariffType {

    @XmlElement(required = true)
    protected String name;
    protected CalcPeriodCode calcPeriod;
    protected BigDecimal subscriptionValue;
    @XmlElement(required = true)
    protected XMLGregorianCalendar validFrom;
    protected XMLGregorianCalendar validTo;
    @XmlElement(required = true)
    protected ServiceIdentificationType service;
    protected String serviceProviderCode;
    @XmlElement(defaultValue = "true")
    protected boolean isActive;
    @XmlElement(required = true)
    protected ModelCalculationCode modelCalculation;
    @XmlElement(required = true)
    protected String currency;
    protected TariffType.MetaData metaData;
    protected TariffByFixedValueType tariffByFixedValue;
    protected TariffType.TariffByVolumes tariffByVolumes;
    protected TariffType.TariffBindingSPList tariffBindingSPList;
    protected TariffType.TariffBindingSCList tariffBindingSCList;

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
     * Gets the value of the calcPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link CalcPeriodCode }
     *     
     */
    public CalcPeriodCode getCalcPeriod() {
        return calcPeriod;
    }

    /**
     * Sets the value of the calcPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link CalcPeriodCode }
     *     
     */
    public void setCalcPeriod(CalcPeriodCode value) {
        this.calcPeriod = value;
    }

    /**
     * Gets the value of the subscriptionValue property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSubscriptionValue() {
        return subscriptionValue;
    }

    /**
     * Sets the value of the subscriptionValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSubscriptionValue(BigDecimal value) {
        this.subscriptionValue = value;
    }

    /**
     * Gets the value of the validFrom property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getValidFrom() {
        return validFrom;
    }

    /**
     * Sets the value of the validFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setValidFrom(XMLGregorianCalendar value) {
        this.validFrom = value;
    }

    /**
     * Gets the value of the validTo property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getValidTo() {
        return validTo;
    }

    /**
     * Sets the value of the validTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setValidTo(XMLGregorianCalendar value) {
        this.validTo = value;
    }

    /**
     * Gets the value of the service property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceIdentificationType }
     *     
     */
    public ServiceIdentificationType getService() {
        return service;
    }

    /**
     * Sets the value of the service property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceIdentificationType }
     *     
     */
    public void setService(ServiceIdentificationType value) {
        this.service = value;
    }

    /**
     * Gets the value of the serviceProviderCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceProviderCode() {
        return serviceProviderCode;
    }

    /**
     * Sets the value of the serviceProviderCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceProviderCode(String value) {
        this.serviceProviderCode = value;
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
     * Gets the value of the modelCalculation property.
     * 
     * @return
     *     possible object is
     *     {@link ModelCalculationCode }
     *     
     */
    public ModelCalculationCode getModelCalculation() {
        return modelCalculation;
    }

    /**
     * Sets the value of the modelCalculation property.
     * 
     * @param value
     *     allowed object is
     *     {@link ModelCalculationCode }
     *     
     */
    public void setModelCalculation(ModelCalculationCode value) {
        this.modelCalculation = value;
    }

    /**
     * Gets the value of the currency property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * Sets the value of the currency property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCurrency(String value) {
        this.currency = value;
    }

    /**
     * Gets the value of the metaData property.
     * 
     * @return
     *     possible object is
     *     {@link TariffType.MetaData }
     *     
     */
    public TariffType.MetaData getMetaData() {
        return metaData;
    }

    /**
     * Sets the value of the metaData property.
     * 
     * @param value
     *     allowed object is
     *     {@link TariffType.MetaData }
     *     
     */
    public void setMetaData(TariffType.MetaData value) {
        this.metaData = value;
    }

    /**
     * Gets the value of the tariffByFixedValue property.
     * 
     * @return
     *     possible object is
     *     {@link TariffByFixedValueType }
     *     
     */
    public TariffByFixedValueType getTariffByFixedValue() {
        return tariffByFixedValue;
    }

    /**
     * Sets the value of the tariffByFixedValue property.
     * 
     * @param value
     *     allowed object is
     *     {@link TariffByFixedValueType }
     *     
     */
    public void setTariffByFixedValue(TariffByFixedValueType value) {
        this.tariffByFixedValue = value;
    }

    /**
     * Gets the value of the tariffByVolumes property.
     * 
     * @return
     *     possible object is
     *     {@link TariffType.TariffByVolumes }
     *     
     */
    public TariffType.TariffByVolumes getTariffByVolumes() {
        return tariffByVolumes;
    }

    /**
     * Sets the value of the tariffByVolumes property.
     * 
     * @param value
     *     allowed object is
     *     {@link TariffType.TariffByVolumes }
     *     
     */
    public void setTariffByVolumes(TariffType.TariffByVolumes value) {
        this.tariffByVolumes = value;
    }

    /**
     * Gets the value of the tariffBindingSPList property.
     * 
     * @return
     *     possible object is
     *     {@link TariffType.TariffBindingSPList }
     *     
     */
    public TariffType.TariffBindingSPList getTariffBindingSPList() {
        return tariffBindingSPList;
    }

    /**
     * Sets the value of the tariffBindingSPList property.
     * 
     * @param value
     *     allowed object is
     *     {@link TariffType.TariffBindingSPList }
     *     
     */
    public void setTariffBindingSPList(TariffType.TariffBindingSPList value) {
        this.tariffBindingSPList = value;
    }

    /**
     * Gets the value of the tariffBindingSCList property.
     * 
     * @return
     *     possible object is
     *     {@link TariffType.TariffBindingSCList }
     *     
     */
    public TariffType.TariffBindingSCList getTariffBindingSCList() {
        return tariffBindingSCList;
    }

    /**
     * Sets the value of the tariffBindingSCList property.
     * 
     * @param value
     *     allowed object is
     *     {@link TariffType.TariffBindingSCList }
     *     
     */
    public void setTariffBindingSCList(TariffType.TariffBindingSCList value) {
        this.tariffBindingSCList = value;
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
     *         &lt;element name="metaDataItem" type="{http://schemas.msk.ru/uec/TransactionService/v1}TariffMetaDataItemType" maxOccurs="unbounded"/>
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
        "metaDataItem"
    })
    public static class MetaData {

        @XmlElement(required = true)
        protected List<TariffMetaDataItemType> metaDataItem;

        /**
         * Gets the value of the metaDataItem property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the metaDataItem property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getMetaDataItem().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TariffMetaDataItemType }
         * 
         * 
         */
        public List<TariffMetaDataItemType> getMetaDataItem() {
            if (metaDataItem == null) {
                metaDataItem = new ArrayList<TariffMetaDataItemType>();
            }
            return this.metaDataItem;
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
     *         &lt;element name="tariffBindingSCItem" type="{http://schemas.msk.ru/uec/TransactionService/v1}TariffBindingSCItemType" maxOccurs="unbounded"/>
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
        "tariffBindingSCItem"
    })
    public static class TariffBindingSCList {

        @XmlElement(required = true)
        protected List<TariffBindingSCItemType> tariffBindingSCItem;

        /**
         * Gets the value of the tariffBindingSCItem property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the tariffBindingSCItem property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getTariffBindingSCItem().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TariffBindingSCItemType }
         * 
         * 
         */
        public List<TariffBindingSCItemType> getTariffBindingSCItem() {
            if (tariffBindingSCItem == null) {
                tariffBindingSCItem = new ArrayList<TariffBindingSCItemType>();
            }
            return this.tariffBindingSCItem;
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
     *         &lt;element name="tariffBindingSPItem" type="{http://schemas.msk.ru/uec/TransactionService/v1}TariffBindingSPItemType" maxOccurs="unbounded"/>
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
        "tariffBindingSPItem"
    })
    public static class TariffBindingSPList {

        @XmlElement(required = true)
        protected List<TariffBindingSPItemType> tariffBindingSPItem;

        /**
         * Gets the value of the tariffBindingSPItem property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the tariffBindingSPItem property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getTariffBindingSPItem().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TariffBindingSPItemType }
         * 
         * 
         */
        public List<TariffBindingSPItemType> getTariffBindingSPItem() {
            if (tariffBindingSPItem == null) {
                tariffBindingSPItem = new ArrayList<TariffBindingSPItemType>();
            }
            return this.tariffBindingSPItem;
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
     *         &lt;element name="tariffByVolumeItem" type="{http://schemas.msk.ru/uec/TransactionService/v1}TariffByVolumeType" maxOccurs="unbounded"/>
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
        "tariffByVolumeItem"
    })
    public static class TariffByVolumes {

        @XmlElement(required = true)
        protected List<TariffByVolumeType> tariffByVolumeItem;

        /**
         * Gets the value of the tariffByVolumeItem property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the tariffByVolumeItem property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getTariffByVolumeItem().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TariffByVolumeType }
         * 
         * 
         */
        public List<TariffByVolumeType> getTariffByVolumeItem() {
            if (tariffByVolumeItem == null) {
                tariffByVolumeItem = new ArrayList<TariffByVolumeType>();
            }
            return this.tariffByVolumeItem;
        }

    }

}
