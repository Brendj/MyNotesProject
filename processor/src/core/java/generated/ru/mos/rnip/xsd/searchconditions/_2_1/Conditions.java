
package generated.ru.mos.rnip.xsd.searchconditions._2_1;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for Conditions complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Conditions">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="ChargesConditions" type="{http://rnip.mos.ru/xsd/SearchConditions/2.1.1}ChargesConditionsType"/>
 *         &lt;element name="PayersConditions" type="{http://rnip.mos.ru/xsd/SearchConditions/2.1.1}PayersConditionsType"/>
 *         &lt;element name="PaymentsConditions" type="{http://rnip.mos.ru/xsd/SearchConditions/2.1.1}PaymentsConditionsType"/>
 *         &lt;element name="TimeConditions" type="{http://rnip.mos.ru/xsd/SearchConditions/2.1.1}TimeConditionsType"/>
 *         &lt;element name="RefundsConditions" type="{http://rnip.mos.ru/xsd/SearchConditions/2.1.1}RefundsConditionsType"/>
 *         &lt;element name="CatalogConditions" type="{http://rnip.mos.ru/xsd/SearchConditions/2.1.1}CatalogConditionsType"/>
 *       &lt;/choice>
 *       &lt;attribute name="kind" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Conditions", propOrder = {
    "chargesConditions",
    "payersConditions",
    "paymentsConditions",
    "timeConditions",
    "refundsConditions",
    "catalogConditions"
})
@XmlSeeAlso({
    RefundsExportConditions.class,
    QuittancesExportConditions.class,
    PaymentsExportConditions.class,
    ChargesExportConditions.class,
    CatalogExportConditions.class
})
public abstract class Conditions {

    @XmlElement(name = "ChargesConditions")
    protected ChargesConditionsType chargesConditions;
    @XmlElement(name = "PayersConditions")
    protected PayersConditionsType payersConditions;
    @XmlElement(name = "PaymentsConditions")
    protected PaymentsConditionsType paymentsConditions;
    @XmlElement(name = "TimeConditions")
    protected TimeConditionsType timeConditions;
    @XmlElement(name = "RefundsConditions")
    protected RefundsConditionsType refundsConditions;
    @XmlElement(name = "CatalogConditions")
    protected CatalogConditionsType catalogConditions;
    @XmlAttribute(name = "kind", required = true)
    protected String kind;

    /**
     * Gets the value of the chargesConditions property.
     * 
     * @return
     *     possible object is
     *     {@link ChargesConditionsType }
     *     
     */
    public ChargesConditionsType getChargesConditions() {
        return chargesConditions;
    }

    /**
     * Sets the value of the chargesConditions property.
     * 
     * @param value
     *     allowed object is
     *     {@link ChargesConditionsType }
     *     
     */
    public void setChargesConditions(ChargesConditionsType value) {
        this.chargesConditions = value;
    }

    /**
     * Gets the value of the payersConditions property.
     * 
     * @return
     *     possible object is
     *     {@link PayersConditionsType }
     *     
     */
    public PayersConditionsType getPayersConditions() {
        return payersConditions;
    }

    /**
     * Sets the value of the payersConditions property.
     * 
     * @param value
     *     allowed object is
     *     {@link PayersConditionsType }
     *     
     */
    public void setPayersConditions(PayersConditionsType value) {
        this.payersConditions = value;
    }

    /**
     * Gets the value of the paymentsConditions property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentsConditionsType }
     *     
     */
    public PaymentsConditionsType getPaymentsConditions() {
        return paymentsConditions;
    }

    /**
     * Sets the value of the paymentsConditions property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentsConditionsType }
     *     
     */
    public void setPaymentsConditions(PaymentsConditionsType value) {
        this.paymentsConditions = value;
    }

    /**
     * Gets the value of the timeConditions property.
     * 
     * @return
     *     possible object is
     *     {@link TimeConditionsType }
     *     
     */
    public TimeConditionsType getTimeConditions() {
        return timeConditions;
    }

    /**
     * Sets the value of the timeConditions property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeConditionsType }
     *     
     */
    public void setTimeConditions(TimeConditionsType value) {
        this.timeConditions = value;
    }

    /**
     * Gets the value of the refundsConditions property.
     * 
     * @return
     *     possible object is
     *     {@link RefundsConditionsType }
     *     
     */
    public RefundsConditionsType getRefundsConditions() {
        return refundsConditions;
    }

    /**
     * Sets the value of the refundsConditions property.
     * 
     * @param value
     *     allowed object is
     *     {@link RefundsConditionsType }
     *     
     */
    public void setRefundsConditions(RefundsConditionsType value) {
        this.refundsConditions = value;
    }

    /**
     * Gets the value of the catalogConditions property.
     * 
     * @return
     *     possible object is
     *     {@link CatalogConditionsType }
     *     
     */
    public CatalogConditionsType getCatalogConditions() {
        return catalogConditions;
    }

    /**
     * Sets the value of the catalogConditions property.
     * 
     * @param value
     *     allowed object is
     *     {@link CatalogConditionsType }
     *     
     */
    public void setCatalogConditions(CatalogConditionsType value) {
        this.catalogConditions = value;
    }

    /**
     * Gets the value of the kind property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getKind() {
        return kind;
    }

    /**
     * Sets the value of the kind property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setKind(String value) {
        this.kind = value;
    }

}
