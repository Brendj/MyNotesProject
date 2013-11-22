
package generated.msr.stoplist.schema;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for areasResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="areasResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="areas" type="{http://webservice.msr.com/sl/schema/}stopListAreaReply" maxOccurs="unbounded" minOccurs="0" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "areasResponse", propOrder = {
    "areas"
})
public class AreasResponse {

    @XmlElement(nillable = true)
    protected List<StopListAreaReply> areas;

    /**
     * Gets the value of the areas property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the areas property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAreas().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StopListAreaReply }
     * 
     * 
     */
    public List<StopListAreaReply> getAreas() {
        if (areas == null) {
            areas = new ArrayList<StopListAreaReply>();
        }
        return this.areas;
    }

}
