
package generated.pos;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ListOfComplaintIterations complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ListOfComplaintIterations">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="I" type="{http://soap.integra.partner.web.processor.ecafe.axetta.ru/}ListOfComplaintIterationsExt" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListOfComplaintIterations", propOrder = {
    "i"
})
public class ListOfComplaintIterations {

    @XmlElement(name = "I")
    protected List<ListOfComplaintIterationsExt> i;

    /**
     * Gets the value of the i property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the i property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getI().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ListOfComplaintIterationsExt }
     * 
     * 
     */
    public List<ListOfComplaintIterationsExt> getI() {
        if (i == null) {
            i = new ArrayList<ListOfComplaintIterationsExt>();
        }
        return this.i;
    }

}
