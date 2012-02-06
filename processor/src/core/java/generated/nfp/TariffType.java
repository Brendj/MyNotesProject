
package generated.nfp;

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
 *               &lt;maxLength value="80"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="code">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="30"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="modelCalculation">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="4"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="validFrom" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="validTo" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="service" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionTypeDescriptionType"/>
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
 *         &lt;element name="tariffByPeriods" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="tariffByPeriodItem" type="{http://schemas.msk.ru/uec/TransactionService/v1}TariffByPeriodType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="tariffBindings" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="tariffBindingsItem" type="{http://schemas.msk.ru/uec/TransactionService/v1}TariffBindingsItemType" maxOccurs="unbounded"/>
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
@XmlType(name = "TariffType", namespace = "http://schemas.msk.ru/uec/TransactionService/v1", propOrder = {
    "name",
    "code",
    "modelCalculation",
    "validFrom",
    "validTo",
    "service",
    "tariffByFixedValue",
    "tariffByVolumes",
    "tariffByPeriods",
    "tariffBindings"
})
public class TariffType {

    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1", required = true)
    protected String name;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1", required = true)
    protected String code;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1", required = true)
    protected String modelCalculation;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1", required = true)
    protected XMLGregorianCalendar validFrom;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1")
    protected XMLGregorianCalendar validTo;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1", required = true)
    protected TransactionTypeDescriptionType service;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1")
    protected TariffByFixedValueType tariffByFixedValue;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1")
    protected TariffType.TariffByVolumes tariffByVolumes;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1")
    protected TariffType.TariffByPeriods tariffByPeriods;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1")
    protected TariffType.TariffBindings tariffBindings;

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
     * Gets the value of the modelCalculation property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getModelCalculation() {
        return modelCalculation;
    }

    /**
     * Sets the value of the modelCalculation property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setModelCalculation(String value) {
        this.modelCalculation = value;
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
     *     {@link TransactionTypeDescriptionType }
     *     
     */
    public TransactionTypeDescriptionType getService() {
        return service;
    }

    /**
     * Sets the value of the service property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionTypeDescriptionType }
     *     
     */
    public void setService(TransactionTypeDescriptionType value) {
        this.service = value;
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
     * Gets the value of the tariffByPeriods property.
     * 
     * @return
     *     possible object is
     *     {@link TariffType.TariffByPeriods }
     *     
     */
    public TariffType.TariffByPeriods getTariffByPeriods() {
        return tariffByPeriods;
    }

    /**
     * Sets the value of the tariffByPeriods property.
     * 
     * @param value
     *     allowed object is
     *     {@link TariffType.TariffByPeriods }
     *     
     */
    public void setTariffByPeriods(TariffType.TariffByPeriods value) {
        this.tariffByPeriods = value;
    }

    /**
     * Gets the value of the tariffBindings property.
     * 
     * @return
     *     possible object is
     *     {@link TariffType.TariffBindings }
     *     
     */
    public TariffType.TariffBindings getTariffBindings() {
        return tariffBindings;
    }

    /**
     * Sets the value of the tariffBindings property.
     * 
     * @param value
     *     allowed object is
     *     {@link TariffType.TariffBindings }
     *     
     */
    public void setTariffBindings(TariffType.TariffBindings value) {
        this.tariffBindings = value;
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
     *         &lt;element name="tariffBindingsItem" type="{http://schemas.msk.ru/uec/TransactionService/v1}TariffBindingsItemType" maxOccurs="unbounded"/>
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
        "tariffBindingsItem"
    })
    public static class TariffBindings {

        @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1", required = true)
        protected List<TariffBindingsItemType> tariffBindingsItem;

        /**
         * Gets the value of the tariffBindingsItem property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the tariffBindingsItem property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getTariffBindingsItem().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TariffBindingsItemType }
         * 
         * 
         */
        public List<TariffBindingsItemType> getTariffBindingsItem() {
            if (tariffBindingsItem == null) {
                tariffBindingsItem = new ArrayList<TariffBindingsItemType>();
            }
            return this.tariffBindingsItem;
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
     *         &lt;element name="tariffByPeriodItem" type="{http://schemas.msk.ru/uec/TransactionService/v1}TariffByPeriodType" maxOccurs="unbounded"/>
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
        "tariffByPeriodItem"
    })
    public static class TariffByPeriods {

        @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1", required = true)
        protected List<TariffByPeriodType> tariffByPeriodItem;

        /**
         * Gets the value of the tariffByPeriodItem property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the tariffByPeriodItem property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getTariffByPeriodItem().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link TariffByPeriodType }
         * 
         * 
         */
        public List<TariffByPeriodType> getTariffByPeriodItem() {
            if (tariffByPeriodItem == null) {
                tariffByPeriodItem = new ArrayList<TariffByPeriodType>();
            }
            return this.tariffByPeriodItem;
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

        @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1", required = true)
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
