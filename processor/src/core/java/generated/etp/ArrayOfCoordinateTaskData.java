
package generated.etp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for ArrayOfCoordinateTaskData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ArrayOfCoordinateTaskData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="CoordinateTaskData" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}CoordinateTaskData" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ArrayOfCoordinateTaskData", propOrder = {
    "coordinateTaskData"
})
public class ArrayOfCoordinateTaskData {

    @XmlElement(name = "CoordinateTaskData", nillable = true)
    protected List<CoordinateTaskData> coordinateTaskData;

    /**
     * Gets the value of the coordinateTaskData property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the coordinateTaskData property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCoordinateTaskData().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link CoordinateTaskData }
     * 
     * 
     */
    public List<CoordinateTaskData> getCoordinateTaskData() {
        if (coordinateTaskData == null) {
            coordinateTaskData = new ArrayList<CoordinateTaskData>();
        }
        return this.coordinateTaskData;
    }

}
