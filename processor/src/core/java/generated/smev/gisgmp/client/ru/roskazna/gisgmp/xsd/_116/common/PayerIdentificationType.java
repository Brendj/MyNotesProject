
package generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.common;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 * ��� ��� ������������� �����������
 * 
 * <p>Java class for PayerIdentification_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PayerIdentification_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ServiceCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;choice maxOccurs="unbounded">
 *           &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Common}SimpleParameter"/>
 *           &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Common}ComplexParameter"/>
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
@XmlType(name = "PayerIdentification_Type", propOrder = {
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
