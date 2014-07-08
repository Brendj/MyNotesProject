
package generated.nsiws_delta;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for DataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="DataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="containerDelta" type="{http://rstyle.com/nsi/delta}ContainerDelta"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "DataType", propOrder = {
    "containerDelta"
})
public class DataType {

    @XmlElement(required = true, namespace = "http://rstyle.com/nsi/delta")
    protected ContainerDelta containerDelta;

    /**
     * Gets the value of the containerDelta property.
     * 
     * @return
     *     possible object is
     *     {@link ContainerDelta }
     *     
     */
    public ContainerDelta getContainerDelta() {
        return containerDelta;
    }

    /**
     * Sets the value of the containerDelta property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContainerDelta }
     *     
     */
    public void setContainerDelta(ContainerDelta value) {
        this.containerDelta = value;
    }

}
