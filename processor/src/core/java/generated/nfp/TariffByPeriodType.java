
package generated.nfp;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Периодические выплаты фиксированных сумм
 * 
 * <p>Java class for TariffByPeriodType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TariffByPeriodType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="periodCode">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="10"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="value" type="{http://www.w3.org/2001/XMLSchema}decimal"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TariffByPeriodType", namespace = "http://schemas.msk.ru/uec/TransactionService/v1", propOrder = {

})
public class TariffByPeriodType {

    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1", required = true)
    protected String periodCode;
    @XmlElement(namespace = "http://schemas.msk.ru/uec/TransactionService/v1", required = true)
    protected BigDecimal value;

    /**
     * Gets the value of the periodCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPeriodCode() {
        return periodCode;
    }

    /**
     * Sets the value of the periodCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPeriodCode(String value) {
        this.periodCode = value;
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

}
