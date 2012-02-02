
package ru.msk.schemas.uec.transactionservice.v1;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import ru.msk.schemas.uec.identification.v1.OrganizationType;


/**
 * Тарифный план
 * 
 * <p>Java class for TariffBindingsItemType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TariffBindingsItemType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="serviceProvider" type="{http://schemas.msk.ru/uec/TransactionService/v1}ServiceProviderType" minOccurs="0"/>
 *         &lt;element name="serviceCustomer" type="{http://schemas.msk.ru/uec/TransactionService/v1}CustomerDescriptionType" minOccurs="0"/>
 *         &lt;element name="spType" type="{http://schemas.msk.ru/uec/identification/v1}OrganizationType" minOccurs="0"/>
 *         &lt;element name="scType" type="{http://schemas.msk.ru/uec/identification/v1}OrganizationType" minOccurs="0"/>
 *         &lt;element name="spShare" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="scShare" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="spWeight" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="scWeight" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="snils" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="metaData" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="metaDataItem" type="{http://schemas.msk.ru/uec/TransactionService/v1}TariffBindingsMetaDataItemType" maxOccurs="unbounded"/>
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
@XmlType(name = "TariffBindingsItemType", propOrder = {
    "serviceProvider",
    "serviceCustomer",
    "spType",
    "scType",
    "spShare",
    "scShare",
    "spWeight",
    "scWeight",
    "snils",
    "metaData"
})
public class TariffBindingsItemType {

    protected ServiceProviderType serviceProvider;
    protected CustomerDescriptionType serviceCustomer;
    protected OrganizationType spType;
    protected OrganizationType scType;
    protected BigDecimal spShare;
    protected BigDecimal scShare;
    protected BigDecimal spWeight;
    protected BigDecimal scWeight;
    protected String snils;
    protected TariffBindingsItemType.MetaData metaData;

    /**
     * Gets the value of the serviceProvider property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceProviderType }
     *     
     */
    public ServiceProviderType getServiceProvider() {
        return serviceProvider;
    }

    /**
     * Sets the value of the serviceProvider property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceProviderType }
     *     
     */
    public void setServiceProvider(ServiceProviderType value) {
        this.serviceProvider = value;
    }

    /**
     * Gets the value of the serviceCustomer property.
     * 
     * @return
     *     possible object is
     *     {@link CustomerDescriptionType }
     *     
     */
    public CustomerDescriptionType getServiceCustomer() {
        return serviceCustomer;
    }

    /**
     * Sets the value of the serviceCustomer property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomerDescriptionType }
     *     
     */
    public void setServiceCustomer(CustomerDescriptionType value) {
        this.serviceCustomer = value;
    }

    /**
     * Gets the value of the spType property.
     * 
     * @return
     *     possible object is
     *     {@link OrganizationType }
     *     
     */
    public OrganizationType getSpType() {
        return spType;
    }

    /**
     * Sets the value of the spType property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganizationType }
     *     
     */
    public void setSpType(OrganizationType value) {
        this.spType = value;
    }

    /**
     * Gets the value of the scType property.
     * 
     * @return
     *     possible object is
     *     {@link OrganizationType }
     *     
     */
    public OrganizationType getScType() {
        return scType;
    }

    /**
     * Sets the value of the scType property.
     * 
     * @param value
     *     allowed object is
     *     {@link OrganizationType }
     *     
     */
    public void setScType(OrganizationType value) {
        this.scType = value;
    }

    /**
     * Gets the value of the spShare property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSpShare() {
        return spShare;
    }

    /**
     * Sets the value of the spShare property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSpShare(BigDecimal value) {
        this.spShare = value;
    }

    /**
     * Gets the value of the scShare property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getScShare() {
        return scShare;
    }

    /**
     * Sets the value of the scShare property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setScShare(BigDecimal value) {
        this.scShare = value;
    }

    /**
     * Gets the value of the spWeight property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSpWeight() {
        return spWeight;
    }

    /**
     * Sets the value of the spWeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSpWeight(BigDecimal value) {
        this.spWeight = value;
    }

    /**
     * Gets the value of the scWeight property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getScWeight() {
        return scWeight;
    }

    /**
     * Sets the value of the scWeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setScWeight(BigDecimal value) {
        this.scWeight = value;
    }

    /**
     * Gets the value of the snils property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSnils() {
        return snils;
    }

    /**
     * Sets the value of the snils property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSnils(String value) {
        this.snils = value;
    }

    /**
     * Gets the value of the metaData property.
     * 
     * @return
     *     possible object is
     *     {@link TariffBindingsItemType.MetaData }
     *     
     */
    public TariffBindingsItemType.MetaData getMetaData() {
        return metaData;
    }

    /**
     * Sets the value of the metaData property.
     * 
     * @param value
     *     allowed object is
     *     {@link TariffBindingsItemType.MetaData }
     *     
     */
    public void setMetaData(TariffBindingsItemType.MetaData value) {
        this.metaData = value;
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
     *         &lt;element name="metaDataItem" type="{http://schemas.msk.ru/uec/TransactionService/v1}TariffBindingsMetaDataItemType" maxOccurs="unbounded"/>
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
        protected List<TariffBindingsMetaDataItemType> metaDataItem;

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
         * {@link TariffBindingsMetaDataItemType }
         * 
         * 
         */
        public List<TariffBindingsMetaDataItemType> getMetaDataItem() {
            if (metaDataItem == null) {
                metaDataItem = new ArrayList<TariffBindingsMetaDataItemType>();
            }
            return this.metaDataItem;
        }

    }

}
