
package ru.msk.schemas.uec.transaction.v1;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import ru.msk.schemas.uec.identification.v1.LegalIdDescriptionType;


/**
 * Ссылки для связи с другими событиями и информационными системами
 * 
 * <p>Java class for RelationDescriptionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RelationDescriptionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="previousTransaction" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;all>
 *                   &lt;element name="transactionSourceDescription" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionSourceDescriptionType"/>
 *                   &lt;element name="transactionId" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionIdDescriptionType"/>
 *                 &lt;/all>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="internalTransactionId" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="relationOrganization" type="{http://schemas.msk.ru/uec/identification/v1}LegalIdDescriptionType" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RelationDescriptionType", propOrder = {
    "previousTransaction",
    "internalTransactionId",
    "relationOrganization"
})
public class RelationDescriptionType {

    protected List<RelationDescriptionType.PreviousTransaction> previousTransaction;
    protected List<String> internalTransactionId;
    protected List<LegalIdDescriptionType> relationOrganization;

    /**
     * Gets the value of the previousTransaction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the previousTransaction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPreviousTransaction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RelationDescriptionType.PreviousTransaction }
     * 
     * 
     */
    public List<RelationDescriptionType.PreviousTransaction> getPreviousTransaction() {
        if (previousTransaction == null) {
            previousTransaction = new ArrayList<RelationDescriptionType.PreviousTransaction>();
        }
        return this.previousTransaction;
    }

    /**
     * Gets the value of the internalTransactionId property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the internalTransactionId property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInternalTransactionId().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getInternalTransactionId() {
        if (internalTransactionId == null) {
            internalTransactionId = new ArrayList<String>();
        }
        return this.internalTransactionId;
    }

    /**
     * Gets the value of the relationOrganization property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the relationOrganization property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRelationOrganization().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link LegalIdDescriptionType }
     * 
     * 
     */
    public List<LegalIdDescriptionType> getRelationOrganization() {
        if (relationOrganization == null) {
            relationOrganization = new ArrayList<LegalIdDescriptionType>();
        }
        return this.relationOrganization;
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
     *       &lt;all>
     *         &lt;element name="transactionSourceDescription" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionSourceDescriptionType"/>
     *         &lt;element name="transactionId" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionIdDescriptionType"/>
     *       &lt;/all>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {

    })
    public static class PreviousTransaction {

        @XmlElement(required = true)
        protected TransactionSourceDescriptionType transactionSourceDescription;
        @XmlElement(required = true)
        protected TransactionIdDescriptionType transactionId;

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

    }

}
