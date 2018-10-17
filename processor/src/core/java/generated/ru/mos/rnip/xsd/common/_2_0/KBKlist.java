
package generated.ru.mos.rnip.xsd.common._2_0;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="KBK" type="{http://rnip.mos.ru/xsd/Common/2.0.1}KBKType" maxOccurs="10"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "kbk"
})
@XmlRootElement(name = "KBKlist")
public class KBKlist {

    @XmlElement(name = "KBK", required = true)
    protected List<String> kbk;

    /**
     * Gets the value of the kbk property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the kbk property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getKBK().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getKBK() {
        if (kbk == null) {
            kbk = new ArrayList<String>();
        }
        return this.kbk;
    }

}
