
/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package generated.spb.meal;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


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
 *         &lt;element name="identityInfo">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="organizationUid" type="{http://service.petersburgedu.ru/webservice/mealTypes}guidType"/>
 *                   &lt;element name="studentUid" type="{http://service.petersburgedu.ru/webservice/mealTypes}notNullStringType"/>
 *                   &lt;element name="userToken" type="{http://service.petersburgedu.ru/webservice/mealTypes}notNullStringType"/>
 *                   &lt;element name="cardUid" type="{http://service.petersburgedu.ru/webservice/mealTypes}notNullStringType"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="transaction" type="{http://service.petersburgedu.ru/webservice/mealTypes}transactionType" maxOccurs="unbounded"/>
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
    "identityInfo",
    "transaction"
})
@XmlRootElement(name = "mealData")
public class MealData {

    @XmlElement(required = true)
    protected MealData.IdentityInfo identityInfo;
    @XmlElement(required = true)
    protected List<TransactionType> transaction;

    /**
     * Gets the value of the identityInfo property.
     * 
     * @return
     *     possible object is
     *     {@link MealData.IdentityInfo }
     *     
     */
    public MealData.IdentityInfo getIdentityInfo() {
        return identityInfo;
    }

    /**
     * Sets the value of the identityInfo property.
     * 
     * @param value
     *     allowed object is
     *     {@link MealData.IdentityInfo }
     *     
     */
    public void setIdentityInfo(MealData.IdentityInfo value) {
        this.identityInfo = value;
    }

    /**
     * Gets the value of the transaction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the transaction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getTransaction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TransactionType }
     * 
     * 
     */
    public List<TransactionType> getTransaction() {
        if (transaction == null) {
            transaction = new ArrayList<TransactionType>();
        }
        return this.transaction;
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
     *         &lt;element name="organizationUid" type="{http://service.petersburgedu.ru/webservice/mealTypes}guidType"/>
     *         &lt;element name="studentUid" type="{http://service.petersburgedu.ru/webservice/mealTypes}notNullStringType"/>
     *         &lt;element name="userToken" type="{http://service.petersburgedu.ru/webservice/mealTypes}notNullStringType"/>
     *         &lt;element name="cardUid" type="{http://service.petersburgedu.ru/webservice/mealTypes}notNullStringType"/>
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
        "organizationUid",
        "studentUid",
        "userToken",
        "cardUid"
    })
    public static class IdentityInfo {

        @XmlElement(required = true)
        protected String organizationUid;
        @XmlElement(required = true)
        protected String studentUid;
        @XmlElement(required = true)
        protected String userToken;
        @XmlElement(required = true)
        protected String cardUid;

        /**
         * Gets the value of the organizationUid property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getOrganizationUid() {
            return organizationUid;
        }

        /**
         * Sets the value of the organizationUid property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setOrganizationUid(String value) {
            this.organizationUid = value;
        }

        /**
         * Gets the value of the studentUid property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getStudentUid() {
            return studentUid;
        }

        /**
         * Sets the value of the studentUid property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setStudentUid(String value) {
            this.studentUid = value;
        }

        /**
         * Gets the value of the userToken property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getUserToken() {
            return userToken;
        }

        /**
         * Sets the value of the userToken property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setUserToken(String value) {
            this.userToken = value;
        }

        /**
         * Gets the value of the cardUid property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getCardUid() {
            return cardUid;
        }

        /**
         * Sets the value of the cardUid property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setCardUid(String value) {
            this.cardUid = value;
        }

    }

}
