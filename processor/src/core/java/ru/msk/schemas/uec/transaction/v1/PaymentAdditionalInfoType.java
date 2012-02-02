
package ru.msk.schemas.uec.transaction.v1;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Дополнительная информация для платежа
 * 
 * <p>Java class for PaymentAdditionalInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PaymentAdditionalInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence maxOccurs="unbounded">
 *         &lt;element name="paymentInstruction">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;all>
 *                   &lt;element name="instructionCode">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="32"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                   &lt;element name="instructionValue">
 *                     &lt;simpleType>
 *                       &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *                         &lt;maxLength value="255"/>
 *                       &lt;/restriction>
 *                     &lt;/simpleType>
 *                   &lt;/element>
 *                 &lt;/all>
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
@XmlType(name = "PaymentAdditionalInfoType", propOrder = {
    "paymentInstruction"
})
public class PaymentAdditionalInfoType {

    @XmlElement(required = true)
    protected List<PaymentAdditionalInfoType.PaymentInstruction> paymentInstruction;

    /**
     * Gets the value of the paymentInstruction property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the paymentInstruction property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPaymentInstruction().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link PaymentAdditionalInfoType.PaymentInstruction }
     * 
     * 
     */
    public List<PaymentAdditionalInfoType.PaymentInstruction> getPaymentInstruction() {
        if (paymentInstruction == null) {
            paymentInstruction = new ArrayList<PaymentAdditionalInfoType.PaymentInstruction>();
        }
        return this.paymentInstruction;
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
     *       &lt;all>
     *         &lt;element name="instructionCode">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="32"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *         &lt;element name="instructionValue">
     *           &lt;simpleType>
     *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
     *               &lt;maxLength value="255"/>
     *             &lt;/restriction>
     *           &lt;/simpleType>
     *         &lt;/element>
     *       &lt;/all>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {

    })
    public static class PaymentInstruction {

        @XmlElement(required = true)
        protected String instructionCode;
        @XmlElement(required = true)
        protected String instructionValue;

        /**
         * Gets the value of the instructionCode property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getInstructionCode() {
            return instructionCode;
        }

        /**
         * Sets the value of the instructionCode property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setInstructionCode(String value) {
            this.instructionCode = value;
        }

        /**
         * Gets the value of the instructionValue property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getInstructionValue() {
            return instructionValue;
        }

        /**
         * Sets the value of the instructionValue property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setInstructionValue(String value) {
            this.instructionValue = value;
        }

    }

}
