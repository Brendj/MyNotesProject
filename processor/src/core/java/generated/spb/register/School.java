
package generated.spb.register;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 *                         ���������� ��
 *                     
 * 
 * <p>Java class for school complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="school">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="school_id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="pupils" type="{http://85.143.161.170:8080/webservice/food_benefits_full/wsdl}pupils"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "school", propOrder = {
    "schoolId",
    "pupils"
})
public class School {

    @XmlElement(name = "school_id", required = true)
    protected String schoolId;
    @XmlElement(required = true)
    protected Pupils pupils;

    /**
     * Gets the value of the schoolId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSchoolId() {
        return schoolId;
    }

    /**
     * Sets the value of the schoolId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSchoolId(String value) {
        this.schoolId = value;
    }

    /**
     * Gets the value of the pupils property.
     * 
     * @return
     *     possible object is
     *     {@link Pupils }
     *     
     */
    public Pupils getPupils() {
        return pupils;
    }

    /**
     * Sets the value of the pupils property.
     * 
     * @param value
     *     allowed object is
     *     {@link Pupils }
     *     
     */
    public void setPupils(Pupils value) {
        this.pupils = value;
    }

}
