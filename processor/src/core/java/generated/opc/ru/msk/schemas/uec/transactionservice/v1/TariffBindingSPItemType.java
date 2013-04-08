
package generated.opc.ru.msk.schemas.uec.transactionservice.v1;

import java.math.BigDecimal;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Взаиморасчет Поставщиков услуг
 * 
 * <p>Java class for TariffBindingSPItemType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TariffBindingSPItemType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="provider" type="{http://schemas.msk.ru/uec/TransactionService/v1}TariffBindingSPIdentificationType"/>
 *         &lt;element name="spShare">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}decimal">
 *               &lt;minInclusive value="0"/>
 *               &lt;maxInclusive value="1"/>
 *               &lt;fractionDigits value="5"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="spWeight" minOccurs="0">
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
@XmlType(name = "TariffBindingSPItemType", propOrder = {
    "provider",
    "spShare",
    "spWeight"
})
public class TariffBindingSPItemType {

    @XmlElement(required = true)
    protected TariffBindingSPIdentificationType provider;
    @XmlElement(required = true)
    protected BigDecimal spShare;
    protected BigDecimal spWeight;

    /**
     * Gets the value of the provider property.
     * 
     * @return
     *     possible object is
     *     {@link TariffBindingSPIdentificationType }
     *     
     */
    public TariffBindingSPIdentificationType getProvider() {
        return provider;
    }

    /**
     * Sets the value of the provider property.
     * 
     * @param value
     *     allowed object is
     *     {@link TariffBindingSPIdentificationType }
     *     
     */
    public void setProvider(TariffBindingSPIdentificationType value) {
        this.provider = value;
    }

    /**
     * Gets the value of the spShare property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSpShare() {
        return spShare;
    }

    /**
     * Sets the value of the spShare property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSpShare(BigDecimal value) {
        this.spShare = value;
    }

    /**
     * Gets the value of the spWeight property.
     * 
     * @return
     *     possible object is
     *     {@link BigDecimal }
     *     
     */
    public BigDecimal getSpWeight() {
        return spWeight;
    }

    /**
     * Sets the value of the spWeight property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigDecimal }
     *     
     */
    public void setSpWeight(BigDecimal value) {
        this.spWeight = value;
    }

}
