
package generated.spb.register;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * 
 *                         ������ ��������
 *                     
 * 
 * <p>Java class for pupils complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="pupils">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="pupil" type="{http://85.143.161.170:8080/webservice/food_benefits_full/wsdl}pupil" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "pupils", propOrder = {
    "pupil"
})
public class Pupils {

    protected List<Pupil> pupil;

    /**
     * Gets the value of the pupil property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the pupil property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPupil().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Pupil }
     * 
     * 
     */
    public List<Pupil> getPupil() {
        if (pupil == null) {
            pupil = new ArrayList<Pupil>();
        }
        return this.pupil;
    }

}
