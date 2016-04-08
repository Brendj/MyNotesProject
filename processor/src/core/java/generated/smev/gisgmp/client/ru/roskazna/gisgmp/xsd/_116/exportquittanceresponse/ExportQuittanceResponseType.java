
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.exportquittanceresponse;

import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.quittance.QuittanceType;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ExportQuittanceResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExportQuittanceResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Quittances">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence minOccurs="0">
 *                   &lt;element name="Quittance" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;extension base="{http://roskazna.ru/gisgmp/xsd/116/Quittance}QuittanceType">
 *                           &lt;sequence>
 *                             &lt;element name="IsRevoked" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/extension>
 *                       &lt;/complexContent>
 *                     &lt;/complexType>
 *                   &lt;/element>
 *                 &lt;/sequence>
 *                 &lt;attribute name="hasMore" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
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
@XmlType(name = "ExportQuittanceResponseType", propOrder = {
    "quittances"
})
public class ExportQuittanceResponseType {

    @XmlElement(name = "Quittances", required = true)
    protected Quittances quittances;

    /**
     * Gets the value of the quittances property.
     * 
     * @return
     *     possible object is
     *     {@link generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.exportquittanceresponse.ExportQuittanceResponseType.Quittances }
     *     
     */
    public Quittances getQuittances() {
        return quittances;
    }

    /**
     * Sets the value of the quittances property.
     * 
     * @param value
     *     allowed object is
     *     {@link generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.exportquittanceresponse.ExportQuittanceResponseType.Quittances }
     *     
     */
    public void setQuittances(Quittances value) {
        this.quittances = value;
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
     *       &lt;sequence minOccurs="0">
     *         &lt;element name="Quittance" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;extension base="{http://roskazna.ru/gisgmp/xsd/116/Quittance}QuittanceType">
     *                 &lt;sequence>
     *                   &lt;element name="IsRevoked" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/extension>
     *             &lt;/complexContent>
     *           &lt;/complexType>
     *         &lt;/element>
     *       &lt;/sequence>
     *       &lt;attribute name="hasMore" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "quittance"
    })
    public static class Quittances {

        @XmlElement(name = "Quittance")
        protected List<Quittance> quittance;
        @XmlAttribute(required = true)
        protected boolean hasMore;

        /**
         * Gets the value of the quittance property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the quittance property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getQuittance().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.exportquittanceresponse.ExportQuittanceResponseType.Quittances.Quittance }
         * 
         * 
         */
        public List<Quittance> getQuittance() {
            if (quittance == null) {
                quittance = new ArrayList<Quittance>();
            }
            return this.quittance;
        }

        /**
         * Gets the value of the hasMore property.
         * 
         */
        public boolean isHasMore() {
            return hasMore;
        }

        /**
         * Sets the value of the hasMore property.
         * 
         */
        public void setHasMore(boolean value) {
            this.hasMore = value;
        }


        /**
         * <p>Java class for anonymous complex type.
         * 
         * <p>The following schema fragment specifies the expected content contained within this class.
         * 
         * <pre>
         * &lt;complexType>
         *   &lt;complexContent>
         *     &lt;extension base="{http://roskazna.ru/gisgmp/xsd/116/Quittance}QuittanceType">
         *       &lt;sequence>
         *         &lt;element name="IsRevoked" type="{http://www.w3.org/2001/XMLSchema}boolean" minOccurs="0"/>
         *       &lt;/sequence>
         *     &lt;/extension>
         *   &lt;/complexContent>
         * &lt;/complexType>
         * </pre>
         * 
         * 
         */
        @XmlAccessorType(XmlAccessType.FIELD)
        @XmlType(name = "", propOrder = {
            "isRevoked"
        })
        public static class Quittance
            extends QuittanceType
        {

            @XmlElement(name = "IsRevoked")
            protected Boolean isRevoked;

            /**
             * Gets the value of the isRevoked property.
             * 
             * @return
             *     possible object is
             *     {@link Boolean }
             *     
             */
            public Boolean isIsRevoked() {
                return isRevoked;
            }

            /**
             * Sets the value of the isRevoked property.
             * 
             * @param value
             *     allowed object is
             *     {@link Boolean }
             *     
             */
            public void setIsRevoked(Boolean value) {
                this.isRevoked = value;
            }

        }

    }

}
