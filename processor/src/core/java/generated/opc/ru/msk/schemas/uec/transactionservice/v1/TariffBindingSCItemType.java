
package generated.opc.ru.msk.schemas.uec.transactionservice.v1;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Взаиморасчет Заказчиков услуг
 * 
 * <p>Java class for TariffBindingSCItemType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TariffBindingSCItemType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="customer" type="{http://schemas.msk.ru/uec/TransactionService/v1}TariffBindingSCIdentificationType"/>
 *         &lt;element name="scShare">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal">
 *               &lt;minInclusive value="0"/>
 *               &lt;maxInclusive value="1"/>
 *               &lt;fractionDigits value="5"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="scWeight" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal">
 *               &lt;minInclusive value="0"/>
 *               &lt;maxInclusive value="1"/>
 *               &lt;fractionDigits value="5"/>
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
@XmlType(name = "TariffBindingSCItemType", propOrder = {
    "customer",
    "scShare",
    "scWeight"
})
public class TariffBindingSCItemType {

    @XmlElement(required = true)
    protected TariffBindingSCIdentificationType customer;
    @XmlElement(required = true)
    protected BigDecimal scShare;
    protected BigDecimal scWeight;

    /**
     * Gets the value of the customer property.
     * 
     * @return
     *     possible object is
     *     {@link TariffBindingSCIdentificationType }
     *     
     */
    public TariffBindingSCIdentificationType getCustomer() {
        return customer;
    }

    /**
     * Sets the value of the customer property.
     * 
     * @param value
     *     allowed object is
     *     {@link TariffBindingSCIdentificationType }
     *     
     */
    public void setCustomer(TariffBindingSCIdentificationType value) {
        this.customer = value;
    }

    /**
     * Gets the value of the scShare property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getScShare() {
        return scShare;
    }

    /**
     * Sets the value of the scShare property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setScShare(BigDecimal value) {
        this.scShare = value;
    }

    /**
     * Gets the value of the scWeight property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getScWeight() {
        return scWeight;
    }

    /**
     * Sets the value of the scWeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setScWeight(BigDecimal value) {
        this.scWeight = value;
    }

}
