
/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package generated.spb.meal;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.math.BigInteger;


/**
 * 
 *                 ��������, ���������� �� �������� ����� ��� �������������� ������ �������� �������
 *             
 * 
 * <p>Java class for transactionType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="transactionType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="transactionId" type="{http://svc.edu.n3demo.ru/service/webservice/mealTypes}notNullStringType"/>
 *         &lt;element name="transactionDate" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="balance" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="amount" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="cardName" type="{http://svc.edu.n3demo.ru/service/webservice/mealTypes}notNullStringType"/>
 *         &lt;element name="foodName" type="{http://svc.edu.n3demo.ru/service/webservice/mealTypes}notNullStringType"/>
 *         &lt;element name="foodAmount" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *         &lt;element name="directionType" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://svc.edu.n3demo.ru/service/webservice/mealTypes}notNullStringType">
 *               &lt;enumeration value="income"/>
 *               &lt;enumeration value="expense"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
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
@XmlType(name = "transactionType", namespace = "http://svc.edu.n3demo.ru/service/webservice/mealTypes", propOrder = {
    "transactionId",
    "transactionDate",
    "balance",
    "amount",
    "cardName",
    "foodName",
    "foodAmount",
    "directionType"
})
public class TransactionType {

    @XmlElement(required = true)
    protected String transactionId;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar transactionDate;
    @XmlElement(required = true)
    protected BigDecimal balance;
    @XmlElement(required = true)
    protected BigDecimal amount;
    @XmlElement(required = true)
    protected String cardName;
    @XmlElement(required = true)
    protected String foodName;
    @XmlElement(required = true)
    protected BigInteger foodAmount;
    protected String directionType;

    /**
     * Gets the value of the transactionId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTransactionId() {
        return transactionId;
    }

    /**
     * Sets the value of the transactionId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTransactionId(String value) {
        this.transactionId = value;
    }

    /**
     * Gets the value of the transactionDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTransactionDate() {
        return transactionDate;
    }

    /**
     * Sets the value of the transactionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTransactionDate(XMLGregorianCalendar value) {
        this.transactionDate = value;
    }

    /**
     * Gets the value of the balance property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getBalance() {
        return balance;
    }

    /**
     * Sets the value of the balance property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setBalance(BigDecimal value) {
        this.balance = value;
    }

    /**
     * Gets the value of the amount property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAmount(BigDecimal value) {
        this.amount = value;
    }

    /**
     * Gets the value of the cardName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCardName() {
        return cardName;
    }

    /**
     * Sets the value of the cardName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCardName(String value) {
        this.cardName = value;
    }

    /**
     * Gets the value of the foodName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFoodName() {
        return foodName;
    }

    /**
     * Sets the value of the foodName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFoodName(String value) {
        this.foodName = value;
    }

    /**
     * Gets the value of the foodAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getFoodAmount() {
        return foodAmount;
    }

    /**
     * Sets the value of the foodAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setFoodAmount(BigInteger value) {
        this.foodAmount = value;
    }

    /**
     * Gets the value of the directionType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDirectionType() {
        return directionType;
    }

    /**
     * Sets the value of the directionType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDirectionType(String value) {
        this.directionType = value;
    }

}
