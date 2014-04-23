
package generated.nsiws2.com.rstyle.nsi.beans;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for StudentSearch complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="StudentSearch">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://rstyle.com/nsi/beans}searchPredicate" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "StudentSearch", propOrder = {
    "searchPredicate"
})
public class StudentSearch {

    protected List<SearchPredicate> searchPredicate;

    /**
     * Gets the value of the searchPredicate property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the searchPredicate property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSearchPredicate().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link SearchPredicate }
     * 
     * 
     */
    public List<SearchPredicate> getSearchPredicate() {
        if (searchPredicate == null) {
            searchPredicate = new ArrayList<SearchPredicate>();
        }
        return this.searchPredicate;
    }

}
