
package generated.etp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArrayOfDictionaryItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfDictionaryItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DictionaryItem" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}DictionaryItem" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfDictionaryItem", propOrder = {
    "dictionaryItem"
})
public class ArrayOfDictionaryItem {

    @XmlElement(name = "DictionaryItem", nillable = true)
    protected List<DictionaryItem> dictionaryItem;

    /**
     * Gets the value of the dictionaryItem property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the dictionaryItem property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getDictionaryItem().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DictionaryItem }
     * 
     * 
     */
    public List<DictionaryItem> getDictionaryItem() {
        if (dictionaryItem == null) {
            dictionaryItem = new ArrayList<DictionaryItem>();
        }
        return this.dictionaryItem;
    }

}
