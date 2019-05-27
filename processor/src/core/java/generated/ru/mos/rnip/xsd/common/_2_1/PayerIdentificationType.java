
package generated.ru.mos.rnip.xsd.common._2_1;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * Тип для идентификации плательщика
 * 
 * <p>Java class for PayerIdentificationType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PayerIdentificationType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ServiceCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element name="SimpleParameter" type="{http://rnip.mos.ru/xsd/Common/2.1.0}SimpleParameterType"/>
 *           &lt;element name="ComplexParameter" type="{http://rnip.mos.ru/xsd/Common/2.1.0}ComplexParameterType"/>
 *         &lt;/choice>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PayerIdentificationType", propOrder = {
    "serviceCode",
    "simpleParameterOrComplexParameter"
})
public class PayerIdentificationType {

    @XmlElement(name = "ServiceCode")
    protected String serviceCode;
    @XmlElements({
        @XmlElement(name = "SimpleParameter", type = SimpleParameterType.class),
        @XmlElement(name = "ComplexParameter", type = ComplexParameterType.class)
    })
    protected List<ParameterType> simpleParameterOrComplexParameter;

    /**
     * Gets the value of the serviceCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceCode() {
        return serviceCode;
    }

    /**
     * Sets the value of the serviceCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceCode(String value) {
        this.serviceCode = value;
    }

    /**
     * Gets the value of the simpleParameterOrComplexParameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the simpleParameterOrComplexParameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSimpleParameterOrComplexParameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SimpleParameterType }
     * {@link ComplexParameterType }
     * 
     * 
     */
    public List<ParameterType> getSimpleParameterOrComplexParameter() {
        if (simpleParameterOrComplexParameter == null) {
            simpleParameterOrComplexParameter = new ArrayList<ParameterType>();
        }
        return this.simpleParameterOrComplexParameter;
    }

}
