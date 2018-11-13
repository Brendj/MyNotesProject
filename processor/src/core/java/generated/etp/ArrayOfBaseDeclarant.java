
package generated.etp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArrayOfBaseDeclarant complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfBaseDeclarant">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="BaseDeclarant" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}BaseDeclarant" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfBaseDeclarant", propOrder = {
    "baseDeclarant"
})
public class ArrayOfBaseDeclarant {

    @XmlElement(name = "BaseDeclarant", nillable = true)
    protected List<BaseDeclarant> baseDeclarant;

    /**
     * Gets the value of the baseDeclarant property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the baseDeclarant property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBaseDeclarant().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link BaseDeclarant }
     * 
     * 
     */
    public List<BaseDeclarant> getBaseDeclarant() {
        if (baseDeclarant == null) {
            baseDeclarant = new ArrayList<BaseDeclarant>();
        }
        return this.baseDeclarant;
    }

}
