
package ru.msk.schemas.uec.transactionservice.v1;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;
import ru.msk.schemas.uec.identification.v1.HolderIdDescriptionType;
import ru.msk.schemas.uec.transaction.v1.TransactionIdDescriptionType;
import ru.msk.schemas.uec.transaction.v1.TransactionTypeDescriptionType;


/**
 * Тип запроса по тарификационным расчетов
 * 
 * <p>Java class for getBillsRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="getBillsRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="dateFrom" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="dateTo" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="block" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionBlockType"/>
 *         &lt;element name="service" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionTypeDescriptionType" minOccurs="0"/>
 *         &lt;element name="holderIdDescription" type="{http://schemas.msk.ru/uec/identification/v1}HolderIdDescriptionType" minOccurs="0"/>
 *         &lt;element name="serviceProvider" type="{http://schemas.msk.ru/uec/TransactionService/v1}ServiceProviderType" minOccurs="0"/>
 *         &lt;element name="transactionIdDescription" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="transactionId" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionIdDescriptionType"/>
 *                   &lt;element name="system" type="{http://schemas.msk.ru/uec/TransactionService/v1}SystemType"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="serviceCustomer" type="{http://schemas.msk.ru/uec/TransactionService/v1}CustomerDescriptionType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "getBillsRequestType", propOrder = {
    "dateFrom",
    "dateTo",
    "block",
    "service",
    "holderIdDescription",
    "serviceProvider",
    "transactionIdDescription",
    "serviceCustomer"
})
public class GetBillsRequestType {

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateFrom;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateTo;
    protected long block;
    protected TransactionTypeDescriptionType service;
    protected HolderIdDescriptionType holderIdDescription;
    protected ServiceProviderType serviceProvider;
    protected GetBillsRequestType.TransactionIdDescription transactionIdDescription;
    protected CustomerDescriptionType serviceCustomer;

    /**
     * Gets the value of the dateFrom property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateFrom() {
        return dateFrom;
    }

    /**
     * Sets the value of the dateFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateFrom(XMLGregorianCalendar value) {
        this.dateFrom = value;
    }

    /**
     * Gets the value of the dateTo property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getDateTo() {
        return dateTo;
    }

    /**
     * Sets the value of the dateTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setDateTo(XMLGregorianCalendar value) {
        this.dateTo = value;
    }

    /**
     * Gets the value of the block property.
     * 
     */
    public long getBlock() {
        return block;
    }

    /**
     * Sets the value of the block property.
     * 
     */
    public void setBlock(long value) {
        this.block = value;
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
     * Gets the value of the holderIdDescription property.
     * 
     * @return
     *     possible object is
     *     {@link HolderIdDescriptionType }
     *     
     */
    public HolderIdDescriptionType getHolderIdDescription() {
        return holderIdDescription;
    }

    /**
     * Sets the value of the holderIdDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link HolderIdDescriptionType }
     *     
     */
    public void setHolderIdDescription(HolderIdDescriptionType value) {
        this.holderIdDescription = value;
    }

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
     * Gets the value of the transactionIdDescription property.
     * 
     * @return
     *     possible object is
     *     {@link GetBillsRequestType.TransactionIdDescription }
     *     
     */
    public GetBillsRequestType.TransactionIdDescription getTransactionIdDescription() {
        return transactionIdDescription;
    }

    /**
     * Sets the value of the transactionIdDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetBillsRequestType.TransactionIdDescription }
     *     
     */
    public void setTransactionIdDescription(GetBillsRequestType.TransactionIdDescription value) {
        this.transactionIdDescription = value;
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
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="transactionId" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionIdDescriptionType"/>
     *         &lt;element name="system" type="{http://schemas.msk.ru/uec/TransactionService/v1}SystemType"/>
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
        "transactionId",
        "system"
    })
    public static class TransactionIdDescription {

        @XmlElement(required = true)
        protected TransactionIdDescriptionType transactionId;
        @XmlElement(required = true)
        protected SystemType system;

        /**
         * Gets the value of the transactionId property.
         * 
         * @return
         *     possible object is
         *     {@link TransactionIdDescriptionType }
         *     
         */
        public TransactionIdDescriptionType getTransactionId() {
            return transactionId;
        }

        /**
         * Sets the value of the transactionId property.
         * 
         * @param value
         *     allowed object is
         *     {@link TransactionIdDescriptionType }
         *     
         */
        public void setTransactionId(TransactionIdDescriptionType value) {
            this.transactionId = value;
        }

        /**
         * Gets the value of the system property.
         * 
         * @return
         *     possible object is
         *     {@link SystemType }
         *     
         */
        public SystemType getSystem() {
            return system;
        }

        /**
         * Sets the value of the system property.
         * 
         * @param value
         *     allowed object is
         *     {@link SystemType }
         *     
         */
        public void setSystem(SystemType value) {
            this.system = value;
        }

    }

}
