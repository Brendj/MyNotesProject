
package generated.pos;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ListOfComplaintOrders complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ListOfComplaintOrders">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="O" type="{http://soap.integra.partner.web.processor.ecafe.axetta.ru/}ListOfComplaintOrdersExt" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListOfComplaintOrders", propOrder = {
    "o"
})
public class ListOfComplaintOrders {

    @XmlElement(name = "O")
    protected List<ListOfComplaintOrdersExt> o;

    /**
     * Gets the value of the o property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the o property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getO().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ListOfComplaintOrdersExt }
     * 
     * 
     */
    public List<ListOfComplaintOrdersExt> getO() {
        if (o == null) {
            o = new ArrayList<ListOfComplaintOrdersExt>();
        }
        return this.o;
    }

}
