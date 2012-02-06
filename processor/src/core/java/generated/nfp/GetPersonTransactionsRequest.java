
package generated.nfp;

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
 *         &lt;element name="holderDescription" type="{http://schemas.msk.ru/uec/identification/v1}HolderIdDescriptionType"/>
 *         &lt;element name="transactionSourceDescription" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionSourceDescriptionType" minOccurs="0"/>
 *         &lt;element name="transactionIdDescription" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionIdDescriptionType" minOccurs="0"/>
 *         &lt;element name="transactionTypeDescription" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionTypeDescriptionType" minOccurs="0"/>
 *         &lt;element name="dateFrom" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="dateTo" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="block" type="{http://schemas.msk.ru/uec/transaction/v1}TransactionBlockType"/>
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
    "holderDescription",
    "transactionSourceDescription",
    "transactionIdDescription",
    "transactionTypeDescription",
    "dateFrom",
    "dateTo",
    "block"
})
@XmlRootElement(name = "getPersonTransactionsRequest", namespace = "http://schemas.msk.ru/uec/TransactionService/v1")
public class GetPersonTransactionsRequest {

    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1", required = true)
    protected HolderIdDescriptionType holderDescription;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1")
    protected TransactionSourceDescriptionType transactionSourceDescription;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1")
    protected TransactionIdDescriptionType transactionIdDescription;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1")
    protected TransactionTypeDescriptionType transactionTypeDescription;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1", required = true)
    protected XMLGregorianCalendar dateFrom;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1", required = true)
    protected XMLGregorianCalendar dateTo;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1")
    protected long block;

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

}
