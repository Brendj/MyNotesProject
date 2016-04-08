
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.exportincomesresponse;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ExportIncomesResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExportIncomesResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Incomes">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence minOccurs="0">
 *                   &lt;element name="IncometInfo" maxOccurs="unbounded">
 *                     &lt;complexType>
 *                       &lt;complexContent>
 *                         &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                           &lt;sequence>
 *                             &lt;element name="IncomeData" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
 *                             &lt;element name="IncomeSignature" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *                           &lt;/sequence>
 *                         &lt;/restriction>
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
@XmlType(name = "ExportIncomesResponseType", propOrder = {
    "incomes"
})
public class ExportIncomesResponseType {

    @XmlElement(name = "Incomes", required = true)
    protected Incomes incomes;

    /**
     * Gets the value of the incomes property.
     * 
     * @return
     *     possible object is
     *     {@link generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.exportincomesresponse.ExportIncomesResponseType.Incomes }
     *     
     */
    public Incomes getIncomes() {
        return incomes;
    }

    /**
     * Sets the value of the incomes property.
     * 
     * @param value
     *     allowed object is
     *     {@link generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.exportincomesresponse.ExportIncomesResponseType.Incomes }
     *     
     */
    public void setIncomes(Incomes value) {
        this.incomes = value;
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
     *         &lt;element name="IncometInfo" maxOccurs="unbounded">
     *           &lt;complexType>
     *             &lt;complexContent>
     *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *                 &lt;sequence>
     *                   &lt;element name="IncomeData" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
     *                   &lt;element name="IncomeSignature" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
     *                 &lt;/sequence>
     *               &lt;/restriction>
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
        "incometInfo"
    })
    public static class Incomes {

        @XmlElement(name = "IncometInfo")
        protected List<IncometInfo> incometInfo;
        @XmlAttribute(required = true)
        protected boolean hasMore;

        /**
         * Gets the value of the incometInfo property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the incometInfo property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getIncometInfo().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.exportincomesresponse.ExportIncomesResponseType.Incomes.IncometInfo }
         * 
         * 
         */
        public List<IncometInfo> getIncometInfo() {
            if (incometInfo == null) {
                incometInfo = new ArrayList<IncometInfo>();
            }
            return this.incometInfo;
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
         *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
         *       &lt;sequence>
         *         &lt;element name="IncomeData" type="{http://www.w3.org/2001/XMLSchema}base64Binary"/>
         *         &lt;element name="IncomeSignature" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
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
            "incomeData",
            "incomeSignature"
        })
        public static class IncometInfo {

            @XmlElement(name = "IncomeData", required = true)
            protected byte[] incomeData;
            @XmlElement(name = "IncomeSignature")
            protected byte[] incomeSignature;

            /**
             * Gets the value of the incomeData property.
             * 
             * @return
             *     possible object is
             *     byte[]
             */
            public byte[] getIncomeData() {
                return incomeData;
            }

            /**
             * Sets the value of the incomeData property.
             * 
             * @param value
             *     allowed object is
             *     byte[]
             */
            public void setIncomeData(byte[] value) {
                this.incomeData = ((byte[]) value);
            }

            /**
             * Gets the value of the incomeSignature property.
             * 
             * @return
             *     possible object is
             *     byte[]
             */
            public byte[] getIncomeSignature() {
                return incomeSignature;
            }

            /**
             * Sets the value of the incomeSignature property.
             * 
             * @param value
             *     allowed object is
             *     byte[]
             */
            public void setIncomeSignature(byte[] value) {
                this.incomeSignature = ((byte[]) value);
            }

        }

    }

}
