
/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.dashboard;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for eduInstItemInfo complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="eduInstItemInfo">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="firstFullSyncTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="lastBalanceSyncTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="lastFullSyncTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="lastOperationTimePerPaymentSystem">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="entry" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                             &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
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
 *         &lt;element name="lastSyncErrors" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="numberOfPaidMealsPerNumOfStaff" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="numberOfPaidMealsPerNumOfStudents" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="numberOfPassagesPerNumOfStaff" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="numberOfPassagesPerNumOfStudents" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="numberOfReducedPriceMealsPerNumOfStaff" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *         &lt;element name="numberOfReducedPriceMealsPerNumOfStudents" type="{http://www.w3.org/2001/XMLSchema}double"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "eduInstItemInfo", propOrder = {
    "firstFullSyncTime",
    "lastBalanceSyncTime",
    "lastFullSyncTime",
    "lastOperationTimePerPaymentSystem",
    "lastSyncErrors",
    "numberOfPaidMealsPerNumOfStaff",
    "numberOfPaidMealsPerNumOfStudents",
    "numberOfPassagesPerNumOfStaff",
    "numberOfPassagesPerNumOfStudents",
    "numberOfReducedPriceMealsPerNumOfStaff",
    "numberOfReducedPriceMealsPerNumOfStudents"
})
public class EduInstItemInfo {

    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar firstFullSyncTime;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastBalanceSyncTime;
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastFullSyncTime;
    @XmlElement(required = true)
    protected EduInstItemInfo.LastOperationTimePerPaymentSystem lastOperationTimePerPaymentSystem;
    @XmlElement(nillable = true)
    protected List<String> lastSyncErrors;
    protected double numberOfPaidMealsPerNumOfStaff;
    protected double numberOfPaidMealsPerNumOfStudents;
    protected double numberOfPassagesPerNumOfStaff;
    protected double numberOfPassagesPerNumOfStudents;
    protected double numberOfReducedPriceMealsPerNumOfStaff;
    protected double numberOfReducedPriceMealsPerNumOfStudents;

    /**
     * Gets the value of the firstFullSyncTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFirstFullSyncTime() {
        return firstFullSyncTime;
    }

    /**
     * Sets the value of the firstFullSyncTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFirstFullSyncTime(XMLGregorianCalendar value) {
        this.firstFullSyncTime = value;
    }

    /**
     * Gets the value of the lastBalanceSyncTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastBalanceSyncTime() {
        return lastBalanceSyncTime;
    }

    /**
     * Sets the value of the lastBalanceSyncTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastBalanceSyncTime(XMLGregorianCalendar value) {
        this.lastBalanceSyncTime = value;
    }

    /**
     * Gets the value of the lastFullSyncTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastFullSyncTime() {
        return lastFullSyncTime;
    }

    /**
     * Sets the value of the lastFullSyncTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastFullSyncTime(XMLGregorianCalendar value) {
        this.lastFullSyncTime = value;
    }

    /**
     * Gets the value of the lastOperationTimePerPaymentSystem property.
     * 
     * @return
     *     possible object is
     *     {@link EduInstItemInfo.LastOperationTimePerPaymentSystem }
     *     
     */
    public EduInstItemInfo.LastOperationTimePerPaymentSystem getLastOperationTimePerPaymentSystem() {
        return lastOperationTimePerPaymentSystem;
    }

    /**
     * Sets the value of the lastOperationTimePerPaymentSystem property.
     * 
     * @param value
     *     allowed object is
     *     {@link EduInstItemInfo.LastOperationTimePerPaymentSystem }
     *     
     */
    public void setLastOperationTimePerPaymentSystem(EduInstItemInfo.LastOperationTimePerPaymentSystem value) {
        this.lastOperationTimePerPaymentSystem = value;
    }

    /**
     * Gets the value of the lastSyncErrors property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the lastSyncErrors property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLastSyncErrors().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getLastSyncErrors() {
        if (lastSyncErrors == null) {
            lastSyncErrors = new ArrayList<String>();
        }
        return this.lastSyncErrors;
    }

    /**
     * Gets the value of the numberOfPaidMealsPerNumOfStaff property.
     * 
     */
    public double getNumberOfPaidMealsPerNumOfStaff() {
        return numberOfPaidMealsPerNumOfStaff;
    }

    /**
     * Sets the value of the numberOfPaidMealsPerNumOfStaff property.
     * 
     */
    public void setNumberOfPaidMealsPerNumOfStaff(double value) {
        this.numberOfPaidMealsPerNumOfStaff = value;
    }

    /**
     * Gets the value of the numberOfPaidMealsPerNumOfStudents property.
     * 
     */
    public double getNumberOfPaidMealsPerNumOfStudents() {
        return numberOfPaidMealsPerNumOfStudents;
    }

    /**
     * Sets the value of the numberOfPaidMealsPerNumOfStudents property.
     * 
     */
    public void setNumberOfPaidMealsPerNumOfStudents(double value) {
        this.numberOfPaidMealsPerNumOfStudents = value;
    }

    /**
     * Gets the value of the numberOfPassagesPerNumOfStaff property.
     * 
     */
    public double getNumberOfPassagesPerNumOfStaff() {
        return numberOfPassagesPerNumOfStaff;
    }

    /**
     * Sets the value of the numberOfPassagesPerNumOfStaff property.
     * 
     */
    public void setNumberOfPassagesPerNumOfStaff(double value) {
        this.numberOfPassagesPerNumOfStaff = value;
    }

    /**
     * Gets the value of the numberOfPassagesPerNumOfStudents property.
     * 
     */
    public double getNumberOfPassagesPerNumOfStudents() {
        return numberOfPassagesPerNumOfStudents;
    }

    /**
     * Sets the value of the numberOfPassagesPerNumOfStudents property.
     * 
     */
    public void setNumberOfPassagesPerNumOfStudents(double value) {
        this.numberOfPassagesPerNumOfStudents = value;
    }

    /**
     * Gets the value of the numberOfReducedPriceMealsPerNumOfStaff property.
     * 
     */
    public double getNumberOfReducedPriceMealsPerNumOfStaff() {
        return numberOfReducedPriceMealsPerNumOfStaff;
    }

    /**
     * Sets the value of the numberOfReducedPriceMealsPerNumOfStaff property.
     * 
     */
    public void setNumberOfReducedPriceMealsPerNumOfStaff(double value) {
        this.numberOfReducedPriceMealsPerNumOfStaff = value;
    }

    /**
     * Gets the value of the numberOfReducedPriceMealsPerNumOfStudents property.
     * 
     */
    public double getNumberOfReducedPriceMealsPerNumOfStudents() {
        return numberOfReducedPriceMealsPerNumOfStudents;
    }

    /**
     * Sets the value of the numberOfReducedPriceMealsPerNumOfStudents property.
     * 
     */
    public void setNumberOfReducedPriceMealsPerNumOfStudents(double value) {
        this.numberOfReducedPriceMealsPerNumOfStudents = value;
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
     *         &lt;element name="entry" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *                   &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
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
        "entry"
    })
    public static class LastOperationTimePerPaymentSystem {

        protected List<EduInstItemInfo.LastOperationTimePerPaymentSystem.Entry> entry;

        /**
         * Gets the value of the entry property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the entry property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getEntry().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link EduInstItemInfo.LastOperationTimePerPaymentSystem.Entry }
         * 
         * 
         */
        public List<EduInstItemInfo.LastOperationTimePerPaymentSystem.Entry> getEntry() {
            if (entry == null) {
                entry = new ArrayList<EduInstItemInfo.LastOperationTimePerPaymentSystem.Entry>();
            }
            return this.entry;
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
         *         &lt;element name="key" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
         *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
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
            "key",
            "value"
        })
        public static class Entry {

            protected String key;
            @XmlSchemaType(name = "dateTime")
            protected XMLGregorianCalendar value;

            /**
             * Gets the value of the key property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getKey() {
                return key;
            }

            /**
             * Sets the value of the key property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setKey(String value) {
                this.key = value;
            }

            /**
             * Gets the value of the value property.
             * 
             * @return
             *     possible object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public XMLGregorianCalendar getValue() {
                return value;
            }

            /**
             * Sets the value of the value property.
             * 
             * @param value
             *     allowed object is
             *     {@link XMLGregorianCalendar }
             *     
             */
            public void setValue(XMLGregorianCalendar value) {
                this.value = value;
            }

        }

    }

}
