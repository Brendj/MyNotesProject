
package generated.ru.mos.rnip.xsd.searchconditions._2_1;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for AbstractBeneficiaryType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AbstractBeneficiaryType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="subsystemId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AbstractBeneficiaryType")
@XmlSeeAlso({
        generated.ru.mos.rnip.xsd.searchconditions._2_1.CatalogConditionsType.BeneficiaryWithInnKpp.class,
        generated.ru.mos.rnip.xsd.searchconditions._2_1.CatalogConditionsType.BeneficiaryWithPayeeId.class
})
public abstract class AbstractBeneficiaryType {

    @XmlAttribute(name = "subsystemId")
    protected String subsystemId;

    /**
     * Gets the value of the subsystemId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSubsystemId() {
        return subsystemId;
    }

    /**
     * Sets the value of the subsystemId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSubsystemId(String value) {
        this.subsystemId = value;
    }

}
