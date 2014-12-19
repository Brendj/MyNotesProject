
/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.service.regularPaymentService.kzn_bankkazani.ru.bankofkazan;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for AsynchronousPaymentResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AsynchronousPaymentResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Responseex" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="errorcode" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
 *                   &lt;element name="errortext" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="cards" maxOccurs="unbounded" minOccurs="0">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="contractid" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *                             &lt;element name="idaction" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *                             &lt;element name="errorcode" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                             &lt;element name="errortext" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AsynchronousPaymentResponse", propOrder = {
    "responseex"
})
public class AsynchronousPaymentResponse {

    @XmlElement(name = "Responseex")
    protected AsynchronousPaymentResponse.Responseex responseex;

    /**
     * Gets the value of the responseex property.
     *
     * @return
     *     possible object is
     *     {@link AsynchronousPaymentResponse.Responseex }
     *
     */
    public AsynchronousPaymentResponse.Responseex getResponseex() {
        return responseex;
    }

    /**
     * Sets the value of the responseex property.
     *
     * @param value
     *     allowed object is
     *     {@link AsynchronousPaymentResponse.Responseex }
     *
     */
    public void setResponseex(AsynchronousPaymentResponse.Responseex value) {
        this.responseex = value;
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
     *         &lt;element name="errorcode" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
     *         &lt;element name="errortext" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="cards" maxOccurs="unbounded" minOccurs="0">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="contractid" type="{http://www.w3.org/2001/XMLSchema}long"/>
     *                   &lt;element name="idaction" type="{http://www.w3.org/2001/XMLSchema}long"/>
     *                   &lt;element name="errorcode" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *                   &lt;element name="errortext" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
        "errorcode",
        "errortext",
        "cards"
    })
    public static class Responseex {

        protected BigInteger errorcode;
        protected String errortext;
        @XmlElement(nillable = true)
        protected List<Cards> cards;

        /**
         * Gets the value of the errorcode property.
         *
         * @return
         *     possible object is
         *     {@link java.math.BigInteger }
         *
         */
        public BigInteger getErrorcode() {
            return errorcode;
        }

        /**
         * Sets the value of the errorcode property.
         *
         * @param value
         *     allowed object is
         *     {@link java.math.BigInteger }
         *
         */
        public void setErrorcode(BigInteger value) {
            this.errorcode = value;
        }

        /**
         * Gets the value of the errortext property.
         *
         * @return
         *     possible object is
         *     {@link String }
         *
         */
        public String getErrortext() {
            return errortext;
        }

        /**
         * Sets the value of the errortext property.
         *
         * @param value
         *     allowed object is
         *     {@link String }
         *
         */
        public void setErrortext(String value) {
            this.errortext = value;
        }

        /**
         * Gets the value of the cards property.
         *
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the cards property.
         *
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getCards().add(newItem);
         * </pre>
         *
         *
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link AsynchronousPaymentResponse.Responseex.Cards }
         *
         *
         */
        public List<Cards> getCards() {
            if (cards == null) {
                cards = new ArrayList<Cards>();
            }
            return this.cards;
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
         *         &lt;element name="contractid" type="{http://www.w3.org/2001/XMLSchema}long"/>
         *         &lt;element name="idaction" type="{http://www.w3.org/2001/XMLSchema}long"/>
         *         &lt;element name="errorcode" type="{http://www.w3.org/2001/XMLSchema}integer"/>
         *         &lt;element name="errortext" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
            "contractid",
            "idaction",
            "errorcode",
            "errortext"
        })
        public static class Cards {

            protected long contractid;
            protected long idaction;
            @XmlElement(required = true)
            protected BigInteger errorcode;
            @XmlElement(required = true)
            protected String errortext;

            /**
             * Gets the value of the contractid property.
             *
             */
            public long getContractid() {
                return contractid;
            }

            /**
             * Sets the value of the contractid property.
             *
             */
            public void setContractid(long value) {
                this.contractid = value;
            }

            /**
             * Gets the value of the idaction property.
             *
             */
            public long getIdaction() {
                return idaction;
            }

            /**
             * Sets the value of the idaction property.
             *
             */
            public void setIdaction(long value) {
                this.idaction = value;
            }

            /**
             * Gets the value of the errorcode property.
             *
             * @return
             *     possible object is
             *     {@link java.math.BigInteger }
             *
             */
            public BigInteger getErrorcode() {
                return errorcode;
            }

            /**
             * Sets the value of the errorcode property.
             *
             * @param value
             *     allowed object is
             *     {@link java.math.BigInteger }
             *     
             */
            public void setErrorcode(BigInteger value) {
                this.errorcode = value;
            }

            /**
             * Gets the value of the errortext property.
             * 
             * @return
             *     possible object is
             *     {@link String }
             *     
             */
            public String getErrortext() {
                return errortext;
            }

            /**
             * Sets the value of the errortext property.
             * 
             * @param value
             *     allowed object is
             *     {@link String }
             *     
             */
            public void setErrortext(String value) {
                this.errortext = value;
            }

        }

    }

}
