
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
 * <p>Java class for AsynchronousPaymentRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AsynchronousPaymentRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Requestex" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="cards" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="action" type="{http://www.w3.org/2001/XMLSchema}integer"/>
 *                             &lt;element name="idaction" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *                             &lt;element name="contractid" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *                             &lt;element name="amount" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
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
@XmlType(name = "AsynchronousPaymentRequest", propOrder = {
    "requestex"
})
public class AsynchronousPaymentRequest {

    @XmlElement(name = "Requestex")
    protected AsynchronousPaymentRequest.Requestex requestex;

    /**
     * Gets the value of the requestex property.
     *
     * @return
     *     possible object is
     *     {@link AsynchronousPaymentRequest.Requestex }
     *
     */
    public AsynchronousPaymentRequest.Requestex getRequestex() {
        return requestex;
    }

    /**
     * Sets the value of the requestex property.
     *
     * @param value
     *     allowed object is
     *     {@link AsynchronousPaymentRequest.Requestex }
     *
     */
    public void setRequestex(AsynchronousPaymentRequest.Requestex value) {
        this.requestex = value;
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
     *         &lt;element name="cards" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="action" type="{http://www.w3.org/2001/XMLSchema}integer"/>
     *                   &lt;element name="idaction" type="{http://www.w3.org/2001/XMLSchema}long"/>
     *                   &lt;element name="contractid" type="{http://www.w3.org/2001/XMLSchema}long"/>
     *                   &lt;element name="amount" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
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
        "cards"
    })
    public static class Requestex {

        @XmlElement(required = true)
        protected List<Cards> cards;

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
         * {@link AsynchronousPaymentRequest.Requestex.Cards }
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
         *         &lt;element name="action" type="{http://www.w3.org/2001/XMLSchema}integer"/>
         *         &lt;element name="idaction" type="{http://www.w3.org/2001/XMLSchema}long"/>
         *         &lt;element name="contractid" type="{http://www.w3.org/2001/XMLSchema}long"/>
         *         &lt;element name="amount" type="{http://www.w3.org/2001/XMLSchema}integer" minOccurs="0"/>
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
            "action",
            "idaction",
            "contractid",
            "amount"
        })
        public static class Cards {

            @XmlElement(required = true)
            protected BigInteger action;
            protected long idaction;
            protected long contractid;
            protected BigInteger amount;

            /**
             * Gets the value of the action property.
             *
             * @return
             *     possible object is
             *     {@link java.math.BigInteger }
             *
             */
            public BigInteger getAction() {
                return action;
            }

            /**
             * Sets the value of the action property.
             *
             * @param value
             *     allowed object is
             *     {@link java.math.BigInteger }
             *
             */
            public void setAction(BigInteger value) {
                this.action = value;
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
             * Gets the value of the amount property.
             *
             * @return
             *     possible object is
             *     {@link java.math.BigInteger }
             *
             */
            public BigInteger getAmount() {
                return amount;
            }

            /**
             * Sets the value of the amount property.
             *
             * @param value
             *     allowed object is
             *     {@link java.math.BigInteger }
             *     
             */
            public void setAmount(BigInteger value) {
                this.amount = value;
            }

        }

        @Override
        public String toString() {
            return "Requestex{" +
                    "cards=" + cards +
                    '}';
        }
    }

}
