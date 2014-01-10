
package generated.pos;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ListOfGoods complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ListOfGoods">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="G" type="{http://soap.integra.partner.web.processor.ecafe.axetta.ru/}ListOfGoodsExt" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListOfGoods", propOrder = {
    "g"
})
public class ListOfGoods {

    @XmlElement(name = "G")
    protected List<ListOfGoodsExt> g;

    /**
     * Gets the value of the g property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the g property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getG().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ListOfGoodsExt }
     * 
     * 
     */
    public List<ListOfGoodsExt> getG() {
        if (g == null) {
            g = new ArrayList<ListOfGoodsExt>();
        }
        return this.g;
    }

}
