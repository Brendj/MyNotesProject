
package generated.msr.stoplist.schema;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for appsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="appsResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="apps" type="{http://webservice.msr.com/sl/schema/}stopListAppReply" maxOccurs="unbounded" minOccurs="0" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "appsResponse", propOrder = {
    "apps"
})
public class AppsResponse {

    @XmlElement(nillable = true)
    protected List<StopListAppReply> apps;

    /**
     * Gets the value of the apps property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the apps property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getApps().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link StopListAppReply }
     * 
     * 
     */
    public List<StopListAppReply> getApps() {
        if (apps == null) {
            apps = new ArrayList<StopListAppReply>();
        }
        return this.apps;
    }

}
