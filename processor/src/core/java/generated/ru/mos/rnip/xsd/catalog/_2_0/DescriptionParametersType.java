
package generated.ru.mos.rnip.xsd.catalog._2_0;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for DescriptionParametersType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DescriptionParametersType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice maxOccurs="unbounded">
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Catalog/2.0.1}DescriptionSimpleParameter"/>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Catalog/2.0.1}DescriptionComplexParameter"/>
 *       &lt;/choice>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DescriptionParametersType", propOrder = {
    "descriptionSimpleParameterOrDescriptionComplexParameter"
})
public class DescriptionParametersType {

    @XmlElements({
        @XmlElement(name = "DescriptionSimpleParameter", type = DescriptionSimpleParameter.class),
        @XmlElement(name = "DescriptionComplexParameter", type = DescriptionComplexParameter.class)
    })
    protected List<DescriptionParameterType> descriptionSimpleParameterOrDescriptionComplexParameter;

    /**
     * Gets the value of the descriptionSimpleParameterOrDescriptionComplexParameter property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the descriptionSimpleParameterOrDescriptionComplexParameter property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDescriptionSimpleParameterOrDescriptionComplexParameter().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DescriptionSimpleParameter }
     * {@link DescriptionComplexParameter }
     * 
     * 
     */
    public List<DescriptionParameterType> getDescriptionSimpleParameterOrDescriptionComplexParameter() {
        if (descriptionSimpleParameterOrDescriptionComplexParameter == null) {
            descriptionSimpleParameterOrDescriptionComplexParameter = new ArrayList<DescriptionParameterType>();
        }
        return this.descriptionSimpleParameterOrDescriptionComplexParameter;
    }

}
