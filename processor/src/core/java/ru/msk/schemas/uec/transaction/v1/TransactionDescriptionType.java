
package ru.msk.schemas.uec.transaction.v1;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import ru.msk.schemas.uec.common.v1.AdditionalDataType;
import ru.msk.schemas.uec.identification.v1.HolderIdDescriptionType;
import ru.msk.schemas.uec.identification.v1.MacType;


/**
 * Сведения о транзакции
 * 
 * <p>Java class for TransactionDescriptionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TransactionDescriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="transactionSourceDescription" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionSourceDescriptionType"/>
 *         &lt;element name="transactionIdDescription" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionIdDescriptionType"/>
 *         &lt;element name="transactionTypeDescription" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionTypeDescriptionType"/>
 *         &lt;element name="holderDescription" type="{http://schemas.msk.ru/uec/identification/v1}HolderIdDescriptionType"/>
 *         &lt;element name="deviceDescription" type="{http://schemas.msk.ru/uec/transaction/v1}DeviceDescriptionType" minOccurs="0"/>
 *         &lt;element name="accountingDescription" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="accountingDescriptionItem" type="{http://schemas.msk.ru/uec/transaction/v1}AccountingDescriptionItemType" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="relationDescription" type="{http://schemas.msk.ru/uec/transaction/v1}RelationDescriptionType" minOccurs="0"/>
 *         &lt;element name="transactionStatusDescription" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionStatusDescriptionType" minOccurs="0"/>
 *         &lt;element name="macDescription" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="mac" type="{http://schemas.msk.ru/uec/identification/v1}MacType" minOccurs="0"/>
 *                   &lt;element name="samMACDescription" type="{http://schemas.msk.ru/uec/transaction/v1}SAMMACDescriptionType" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="additionalInfo" type="{http://schemas.msk.ru/uec/common/v1}AdditionalDataType" minOccurs="0"/>
 *         &lt;element name="transactionTags" type="{http://schemas.msk.ru/uec/transaction/v1}TagType" minOccurs="0"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TransactionDescriptionType", propOrder = {

})
public class TransactionDescriptionType {

    @XmlElement(required = true)
    protected TransactionSourceDescriptionType transactionSourceDescription;
    @XmlElement(required = true)
    protected TransactionIdDescriptionType transactionIdDescription;
    @XmlElement(required = true)
    protected TransactionTypeDescriptionType transactionTypeDescription;
    @XmlElement(required = true)
    protected HolderIdDescriptionType holderDescription;
    protected DeviceDescriptionType deviceDescription;
    protected TransactionDescriptionType.AccountingDescription accountingDescription;
    protected RelationDescriptionType relationDescription;
    protected TransactionStatusDescriptionType transactionStatusDescription;
    protected TransactionDescriptionType.MacDescription macDescription;
    protected AdditionalDataType additionalInfo;
    protected TagType transactionTags;

    /**
     * Gets the value of the transactionSourceDescription property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionSourceDescriptionType }
     *     
     */
    public TransactionSourceDescriptionType getTransactionSourceDescription() {
        return transactionSourceDescription;
    }

    /**
     * Sets the value of the transactionSourceDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionSourceDescriptionType }
     *     
     */
    public void setTransactionSourceDescription(TransactionSourceDescriptionType value) {
        this.transactionSourceDescription = value;
    }

    /**
     * Gets the value of the transactionIdDescription property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionIdDescriptionType }
     *     
     */
    public TransactionIdDescriptionType getTransactionIdDescription() {
        return transactionIdDescription;
    }

    /**
     * Sets the value of the transactionIdDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionIdDescriptionType }
     *     
     */
    public void setTransactionIdDescription(TransactionIdDescriptionType value) {
        this.transactionIdDescription = value;
    }

    /**
     * Gets the value of the transactionTypeDescription property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionTypeDescriptionType }
     *     
     */
    public TransactionTypeDescriptionType getTransactionTypeDescription() {
        return transactionTypeDescription;
    }

    /**
     * Sets the value of the transactionTypeDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionTypeDescriptionType }
     *     
     */
    public void setTransactionTypeDescription(TransactionTypeDescriptionType value) {
        this.transactionTypeDescription = value;
    }

    /**
     * Gets the value of the holderDescription property.
     * 
     * @return
     *     possible object is
     *     {@link HolderIdDescriptionType }
     *     
     */
    public HolderIdDescriptionType getHolderDescription() {
        return holderDescription;
    }

    /**
     * Sets the value of the holderDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link HolderIdDescriptionType }
     *     
     */
    public void setHolderDescription(HolderIdDescriptionType value) {
        this.holderDescription = value;
    }

    /**
     * Gets the value of the deviceDescription property.
     * 
     * @return
     *     possible object is
     *     {@link DeviceDescriptionType }
     *     
     */
    public DeviceDescriptionType getDeviceDescription() {
        return deviceDescription;
    }

    /**
     * Sets the value of the deviceDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link DeviceDescriptionType }
     *     
     */
    public void setDeviceDescription(DeviceDescriptionType value) {
        this.deviceDescription = value;
    }

    /**
     * Gets the value of the accountingDescription property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionDescriptionType.AccountingDescription }
     *     
     */
    public TransactionDescriptionType.AccountingDescription getAccountingDescription() {
        return accountingDescription;
    }

    /**
     * Sets the value of the accountingDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionDescriptionType.AccountingDescription }
     *     
     */
    public void setAccountingDescription(TransactionDescriptionType.AccountingDescription value) {
        this.accountingDescription = value;
    }

    /**
     * Gets the value of the relationDescription property.
     * 
     * @return
     *     possible object is
     *     {@link RelationDescriptionType }
     *     
     */
    public RelationDescriptionType getRelationDescription() {
        return relationDescription;
    }

    /**
     * Sets the value of the relationDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link RelationDescriptionType }
     *     
     */
    public void setRelationDescription(RelationDescriptionType value) {
        this.relationDescription = value;
    }

    /**
     * Gets the value of the transactionStatusDescription property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionStatusDescriptionType }
     *     
     */
    public TransactionStatusDescriptionType getTransactionStatusDescription() {
        return transactionStatusDescription;
    }

    /**
     * Sets the value of the transactionStatusDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionStatusDescriptionType }
     *     
     */
    public void setTransactionStatusDescription(TransactionStatusDescriptionType value) {
        this.transactionStatusDescription = value;
    }

    /**
     * Gets the value of the macDescription property.
     * 
     * @return
     *     possible object is
     *     {@link TransactionDescriptionType.MacDescription }
     *     
     */
    public TransactionDescriptionType.MacDescription getMacDescription() {
        return macDescription;
    }

    /**
     * Sets the value of the macDescription property.
     * 
     * @param value
     *     allowed object is
     *     {@link TransactionDescriptionType.MacDescription }
     *     
     */
    public void setMacDescription(TransactionDescriptionType.MacDescription value) {
        this.macDescription = value;
    }

    /**
     * Gets the value of the additionalInfo property.
     * 
     * @return
     *     possible object is
     *     {@link AdditionalDataType }
     *     
     */
    public AdditionalDataType getAdditionalInfo() {
        return additionalInfo;
    }

    /**
     * Sets the value of the additionalInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link AdditionalDataType }
     *     
     */
    public void setAdditionalInfo(AdditionalDataType value) {
        this.additionalInfo = value;
    }

    /**
     * Gets the value of the transactionTags property.
     * 
     * @return
     *     possible object is
     *     {@link TagType }
     *     
     */
    public TagType getTransactionTags() {
        return transactionTags;
    }

    /**
     * Sets the value of the transactionTags property.
     * 
     * @param value
     *     allowed object is
     *     {@link TagType }
     *     
     */
    public void setTransactionTags(TagType value) {
        this.transactionTags = value;
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
     *         &lt;element name="accountingDescriptionItem" type="{http://schemas.msk.ru/uec/transaction/v1}AccountingDescriptionItemType" maxOccurs="unbounded"/>
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
        "accountingDescriptionItem"
    })
    public static class AccountingDescription {

        @XmlElement(required = true)
        protected List<AccountingDescriptionItemType> accountingDescriptionItem;

        /**
         * Gets the value of the accountingDescriptionItem property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the accountingDescriptionItem property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getAccountingDescriptionItem().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AccountingDescriptionItemType }
         * 
         * 
         */
        public List<AccountingDescriptionItemType> getAccountingDescriptionItem() {
            if (accountingDescriptionItem == null) {
                accountingDescriptionItem = new ArrayList<AccountingDescriptionItemType>();
            }
            return this.accountingDescriptionItem;
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
     *         &lt;element name="mac" type="{http://schemas.msk.ru/uec/identification/v1}MacType" minOccurs="0"/>
     *         &lt;element name="samMACDescription" type="{http://schemas.msk.ru/uec/transaction/v1}SAMMACDescriptionType" minOccurs="0"/>
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
        "mac",
        "samMACDescription"
    })
    public static class MacDescription {

        protected MacType mac;
        protected SAMMACDescriptionType samMACDescription;

        /**
         * Gets the value of the mac property.
         * 
         * @return
         *     possible object is
         *     {@link MacType }
         *     
         */
        public MacType getMac() {
            return mac;
        }

        /**
         * Sets the value of the mac property.
         * 
         * @param value
         *     allowed object is
         *     {@link MacType }
         *     
         */
        public void setMac(MacType value) {
            this.mac = value;
        }

        /**
         * Gets the value of the samMACDescription property.
         * 
         * @return
         *     possible object is
         *     {@link SAMMACDescriptionType }
         *     
         */
        public SAMMACDescriptionType getSamMACDescription() {
            return samMACDescription;
        }

        /**
         * Sets the value of the samMACDescription property.
         * 
         * @param value
         *     allowed object is
         *     {@link SAMMACDescriptionType }
         *     
         */
        public void setSamMACDescription(SAMMACDescriptionType value) {
            this.samMACDescription = value;
        }

    }

}
