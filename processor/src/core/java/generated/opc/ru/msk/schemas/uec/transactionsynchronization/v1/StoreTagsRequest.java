
package generated.opc.ru.msk.schemas.uec.transactionsynchronization.v1;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import generated.opc.ru.msk.schemas.uec.transaction.v1.TagType;
import generated.opc.ru.msk.schemas.uec.transaction.v1.TransactionIdDescriptionType;
import generated.opc.ru.msk.schemas.uec.transaction.v1.TransactionSourceDescriptionType;


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
 *         &lt;element name="addTag" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="transactionSourceDescription" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionSourceDescriptionType"/>
 *                   &lt;element name="transactionIdDescription" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionIdDescriptionType"/>
 *                   &lt;element name="tags" type="{http://schemas.msk.ru/uec/transaction/v1}TagType"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="removeTag" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="transactionSourceDescription" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionSourceDescriptionType"/>
 *                   &lt;element name="transactionIdDescription" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionIdDescriptionType"/>
 *                   &lt;element name="tags" type="{http://schemas.msk.ru/uec/transaction/v1}TagType"/>
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
    "addTag",
    "removeTag"
})
@XmlRootElement(name = "storeTagsRequest")
public class StoreTagsRequest {

    protected List<StoreTagsRequest.AddTag> addTag;
    protected List<StoreTagsRequest.RemoveTag> removeTag;

    /**
     * Gets the value of the addTag property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the addTag property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAddTag().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StoreTagsRequest.AddTag }
     * 
     * 
     */
    public List<StoreTagsRequest.AddTag> getAddTag() {
        if (addTag == null) {
            addTag = new ArrayList<StoreTagsRequest.AddTag>();
        }
        return this.addTag;
    }

    /**
     * Gets the value of the removeTag property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the removeTag property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRemoveTag().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StoreTagsRequest.RemoveTag }
     * 
     * 
     */
    public List<StoreTagsRequest.RemoveTag> getRemoveTag() {
        if (removeTag == null) {
            removeTag = new ArrayList<StoreTagsRequest.RemoveTag>();
        }
        return this.removeTag;
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
     *         &lt;element name="transactionSourceDescription" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionSourceDescriptionType"/>
     *         &lt;element name="transactionIdDescription" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionIdDescriptionType"/>
     *         &lt;element name="tags" type="{http://schemas.msk.ru/uec/transaction/v1}TagType"/>
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
        "transactionSourceDescription",
        "transactionIdDescription",
        "tags"
    })
    public static class AddTag {

        @XmlElement(required = true)
        protected TransactionSourceDescriptionType transactionSourceDescription;
        @XmlElement(required = true)
        protected TransactionIdDescriptionType transactionIdDescription;
        @XmlElement(required = true)
        protected TagType tags;

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
         * Gets the value of the tags property.
         * 
         * @return
         *     possible object is
         *     {@link TagType }
         *     
         */
        public TagType getTags() {
            return tags;
        }

        /**
         * Sets the value of the tags property.
         * 
         * @param value
         *     allowed object is
         *     {@link TagType }
         *     
         */
        public void setTags(TagType value) {
            this.tags = value;
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
     *         &lt;element name="transactionSourceDescription" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionSourceDescriptionType"/>
     *         &lt;element name="transactionIdDescription" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionIdDescriptionType"/>
     *         &lt;element name="tags" type="{http://schemas.msk.ru/uec/transaction/v1}TagType"/>
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
        "transactionSourceDescription",
        "transactionIdDescription",
        "tags"
    })
    public static class RemoveTag {

        @XmlElement(required = true)
        protected TransactionSourceDescriptionType transactionSourceDescription;
        @XmlElement(required = true)
        protected TransactionIdDescriptionType transactionIdDescription;
        @XmlElement(required = true)
        protected TagType tags;

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
         * Gets the value of the tags property.
         * 
         * @return
         *     possible object is
         *     {@link TagType }
         *     
         */
        public TagType getTags() {
            return tags;
        }

        /**
         * Sets the value of the tags property.
         * 
         * @param value
         *     allowed object is
         *     {@link TagType }
         *     
         */
        public void setTags(TagType value) {
            this.tags = value;
        }

    }

}
