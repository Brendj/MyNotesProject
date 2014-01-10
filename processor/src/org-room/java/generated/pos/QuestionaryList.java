
package generated.pos;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for QuestionaryList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="QuestionaryList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Q" type="{http://soap.integra.partner.web.processor.ecafe.axetta.ru/}QuestionaryItem" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "QuestionaryList", propOrder = {
    "q"
})
public class QuestionaryList {

    @XmlElement(name = "Q")
    protected List<QuestionaryItem> q;

    /**
     * Gets the value of the q property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the q property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getQ().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link QuestionaryItem }
     * 
     * 
     */
    public List<QuestionaryItem> getQ() {
        if (q == null) {
            q = new ArrayList<QuestionaryItem>();
        }
        return this.q;
    }

}
