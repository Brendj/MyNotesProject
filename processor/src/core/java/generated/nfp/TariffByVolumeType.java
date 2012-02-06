
package generated.nfp;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Тарификация за объём
 * 
 * <p>Java class for TariffByVolumeType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TariffByVolumeType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="amountFrom" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="amountTo" type="{http://www.w3.org/2001/XMLSchema}decimal" minOccurs="0"/>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *         &lt;element name="resourceCode" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="10"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="timeTarificationInterval" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="startTime" type="{http://www.w3.org/2001/XMLSchema}time"/>
 *                   &lt;element name="endTime" type="{http://www.w3.org/2001/XMLSchema}time"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
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
@XmlType(name = "TariffByVolumeType", namespace = "http://schemas.msk.ru/uec/TransactionService/v1", propOrder = {

})
public class TariffByVolumeType {

    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1", required = true)
    protected BigDecimal amountFrom;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1")
    protected BigDecimal amountTo;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1", required = true)
    protected BigDecimal value;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1")
    protected String resourceCode;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1")
    protected TariffByVolumeType.TimeTarificationInterval timeTarificationInterval;

    /**
     * Gets the value of the amountFrom property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAmountFrom() {
        return amountFrom;
    }

    /**
     * Sets the value of the amountFrom property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAmountFrom(BigDecimal value) {
        this.amountFrom = value;
    }

    /**
     * Gets the value of the amountTo property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getAmountTo() {
        return amountTo;
    }

    /**
     * Sets the value of the amountTo property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setAmountTo(BigDecimal value) {
        this.amountTo = value;
    }

    /**
     * Gets the value of the value property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getValue() {
        return value;
    }

    /**
     * Sets the value of the value property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setValue(BigDecimal value) {
        this.value = value;
    }

    /**
     * Gets the value of the resourceCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getResourceCode() {
        return resourceCode;
    }

    /**
     * Sets the value of the resourceCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setResourceCode(String value) {
        this.resourceCode = value;
    }

    /**
     * Gets the value of the timeTarificationInterval property.
     * 
     * @return
     *     possible object is
     *     {@link TariffByVolumeType.TimeTarificationInterval }
     *     
     */
    public TariffByVolumeType.TimeTarificationInterval getTimeTarificationInterval() {
        return timeTarificationInterval;
    }

    /**
     * Sets the value of the timeTarificationInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link TariffByVolumeType.TimeTarificationInterval }
     *     
     */
    public void setTimeTarificationInterval(TariffByVolumeType.TimeTarificationInterval value) {
        this.timeTarificationInterval = value;
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
     *         &lt;element name="startTime" type="{http://www.w3.org/2001/XMLSchema}time"/>
     *         &lt;element name="endTime" type="{http://www.w3.org/2001/XMLSchema}time"/>
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
        "startTime",
        "endTime"
    })
    public static class TimeTarificationInterval {

        @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1", required = true)
        @XmlSchemaType(name = "time")
        protected XMLGregorianCalendar startTime;
        @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1", required = true)
        @XmlSchemaType(name = "time")
        protected XMLGregorianCalendar endTime;

        /**
         * Gets the value of the startTime property.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getStartTime() {
            return startTime;
        }

        /**
         * Sets the value of the startTime property.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setStartTime(XMLGregorianCalendar value) {
            this.startTime = value;
        }

        /**
         * Gets the value of the endTime property.
         * 
         * @return
         *     possible object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public XMLGregorianCalendar getEndTime() {
            return endTime;
        }

        /**
         * Sets the value of the endTime property.
         * 
         * @param value
         *     allowed object is
         *     {@link XMLGregorianCalendar }
         *     
         */
        public void setEndTime(XMLGregorianCalendar value) {
            this.endTime = value;
        }

    }

}
