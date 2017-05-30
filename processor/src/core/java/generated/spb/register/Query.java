
package generated.spb.register;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;all>
 *         &lt;element name="school" type="{http://85.143.161.170:8080/webservice/food_benefits_full/wsdl}query_school"/>
 *       &lt;/all>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {

})
@XmlRootElement(name = "query")
public class Query {

    @XmlElement(required = true)
    protected QuerySchool school;

    /**
     * Gets the value of the school property.
     * 
     * @return
     *     possible object is
     *     {@link QuerySchool }
     *     
     */
    public QuerySchool getSchool() {
        return school;
    }

    /**
     * Sets the value of the school property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuerySchool }
     *     
     */
    public void setSchool(QuerySchool value) {
        this.school = value;
    }

}
