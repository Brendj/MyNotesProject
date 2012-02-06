
package generated.nfp;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


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
 *         &lt;element name="transactionTypeDescription" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionTypeDescriptionType" minOccurs="0"/>
 *         &lt;element name="dateFrom" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="dateTo" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="block" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionBlockType"/>
 *         &lt;element name="transactionTypes" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="includeTransactionTypes" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="transactionType" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionTypeDescriptionType" maxOccurs="unbounded"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                   &lt;element name="excludeTransactionTypes" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="transactionType" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionTypeDescriptionType" maxOccurs="unbounded"/>
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
 *         &lt;element name="tags" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="includeTags" type="{http://schemas.msk.ru/uec/transaction/v1}TagType" minOccurs="0"/>
 *                   &lt;element name="excludeTags" type="{http://schemas.msk.ru/uec/transaction/v1}TagType" minOccurs="0"/>
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
    "transactionTypeDescription",
    "dateFrom",
    "dateTo",
    "block",
    "transactionTypes",
    "tags"
})
@XmlRootElement(name = "getTransactionsRequest", namespace = "http://schemas.msk.ru/uec/TransactionSynchronization/v1")
public class GetTransactionsRequest {

    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionSynchronization/v1")
    protected TransactionTypeDescriptionType transactionTypeDescription;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionSynchronization/v1", required = true)
    protected XMLGregorianCalendar dateFrom;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionSynchronization/v1", required = true)
    protected XMLGregorianCalendar dateTo;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionSynchronization/v1")
    protected long block;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionSynchronization/v1")
    protected GetTransactionsRequest.TransactionTypes transactionTypes;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionSynchronization/v1")
    protected GetTransactionsRequest.Tags tags;

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
     * Gets the value of the transactionTypes property.
     * 
     * @return
     *     possible object is
     *     {@link GetTransactionsRequest.TransactionTypes }
     *     
     */
    public GetTransactionsRequest.TransactionTypes getTransactionTypes() {
        return transactionTypes;
    }

    /**
     * Sets the value of the transactionTypes property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetTransactionsRequest.TransactionTypes }
     *     
     */
    public void setTransactionTypes(GetTransactionsRequest.TransactionTypes value) {
        this.transactionTypes = value;
    }

    /**
     * Gets the value of the tags property.
     * 
     * @return
     *     possible object is
     *     {@link GetTransactionsRequest.Tags }
     *     
     */
    public GetTransactionsRequest.Tags getTags() {
        return tags;
    }

    /**
     * Sets the value of the tags property.
     * 
     * @param value
     *     allowed object is
     *     {@link GetTransactionsRequest.Tags }
     *     
     */
    public void setTags(GetTransactionsRequest.Tags value) {
        this.tags = value;
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
     *         &lt;element name="includeTags" type="{http://schemas.msk.ru/uec/transaction/v1}TagType" minOccurs="0"/>
     *         &lt;element name="excludeTags" type="{http://schemas.msk.ru/uec/transaction/v1}TagType" minOccurs="0"/>
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
        "includeTags",
        "excludeTags"
    })
    public static class Tags {

        @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionSynchronization/v1")
        protected TagType includeTags;
        @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionSynchronization/v1")
        protected TagType excludeTags;

        /**
         * Gets the value of the includeTags property.
         * 
         * @return
         *     possible object is
         *     {@link TagType }
         *     
         */
        public TagType getIncludeTags() {
            return includeTags;
        }

        /**
         * Sets the value of the includeTags property.
         * 
         * @param value
         *     allowed object is
         *     {@link TagType }
         *     
         */
        public void setIncludeTags(TagType value) {
            this.includeTags = value;
        }

        /**
         * Gets the value of the excludeTags property.
         * 
         * @return
         *     possible object is
         *     {@link TagType }
         *     
         */
        public TagType getExcludeTags() {
            return excludeTags;
        }

        /**
         * Sets the value of the excludeTags property.
         * 
         * @param value
         *     allowed object is
         *     {@link TagType }
         *     
         */
        public void setExcludeTags(TagType value) {
            this.excludeTags = value;
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
     *         &lt;element name="includeTransactionTypes" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="transactionType" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionTypeDescriptionType" maxOccurs="unbounded"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *         &lt;element name="excludeTransactionTypes" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="transactionType" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionTypeDescriptionType" maxOccurs="unbounded"/>
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
        "includeTransactionTypes",
        "excludeTransactionTypes"
    })
    public static class TransactionTypes {

        @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionSynchronization/v1")
        protected GetTransactionsRequest.TransactionTypes.IncludeTransactionTypes includeTransactionTypes;
        @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionSynchronization/v1")
        protected GetTransactionsRequest.TransactionTypes.ExcludeTransactionTypes excludeTransactionTypes;

        /**
         * Gets the value of the includeTransactionTypes property.
         * 
         * @return
         *     possible object is
         *     {@link GetTransactionsRequest.TransactionTypes.IncludeTransactionTypes }
         *     
         */
        public GetTransactionsRequest.TransactionTypes.IncludeTransactionTypes getIncludeTransactionTypes() {
            return includeTransactionTypes;
        }

        /**
         * Sets the value of the includeTransactionTypes property.
         * 
         * @param value
         *     allowed object is
         *     {@link GetTransactionsRequest.TransactionTypes.IncludeTransactionTypes }
         *     
         */
        public void setIncludeTransactionTypes(GetTransactionsRequest.TransactionTypes.IncludeTransactionTypes value) {
            this.includeTransactionTypes = value;
        }

        /**
         * Gets the value of the excludeTransactionTypes property.
         * 
         * @return
         *     possible object is
         *     {@link GetTransactionsRequest.TransactionTypes.ExcludeTransactionTypes }
         *     
         */
        public GetTransactionsRequest.TransactionTypes.ExcludeTransactionTypes getExcludeTransactionTypes() {
            return excludeTransactionTypes;
        }

        /**
         * Sets the value of the excludeTransactionTypes property.
         * 
         * @param value
         *     allowed object is
         *     {@link GetTransactionsRequest.TransactionTypes.ExcludeTransactionTypes }
         *     
         */
        public void setExcludeTransactionTypes(GetTransactionsRequest.TransactionTypes.ExcludeTransactionTypes value) {
            this.excludeTransactionTypes = value;
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
         *         &lt;element name="transactionType" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionTypeDescriptionType" maxOccurs="unbounded"/>
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
            "transactionType"
        })
        public static class ExcludeTransactionTypes {

            @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionSynchronization/v1", required = true)
            protected List<TransactionTypeDescriptionType> transactionType;

            /**
             * Gets the value of the transactionType property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the transactionType property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getTransactionType().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link TransactionTypeDescriptionType }
             * 
             * 
             */
            public List<TransactionTypeDescriptionType> getTransactionType() {
                if (transactionType == null) {
                    transactionType = new ArrayList<TransactionTypeDescriptionType>();
                }
                return this.transactionType;
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
         *         &lt;element name="transactionType" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionTypeDescriptionType" maxOccurs="unbounded"/>
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
            "transactionType"
        })
        public static class IncludeTransactionTypes {

            @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionSynchronization/v1", required = true)
            protected List<TransactionTypeDescriptionType> transactionType;

            /**
             * Gets the value of the transactionType property.
             * 
             * <p>
             * This accessor method returns a reference to the live list,
             * not a snapshot. Therefore any modification you make to the
             * returned list will be present inside the JAXB object.
             * This is why there is not a <CODE>set</CODE> method for the transactionType property.
             * 
             * <p>
             * For example, to add a new item, do as follows:
             * <pre>
             *    getTransactionType().add(newItem);
             * </pre>
             * 
             * 
             * <p>
             * Objects of the following type(s) are allowed in the list
             * {@link TransactionTypeDescriptionType }
             * 
             * 
             */
            public List<TransactionTypeDescriptionType> getTransactionType() {
                if (transactionType == null) {
                    transactionType = new ArrayList<TransactionTypeDescriptionType>();
                }
                return this.transactionType;
            }

        }

    }

}
