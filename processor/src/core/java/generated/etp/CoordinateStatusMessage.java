
package generated.etp;

import javax.xml.bind.annotation.*;


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
 *         &lt;element name="CoordinateStatusDataMessage" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}CoordinateStatusData" minOccurs="0"/>
 *         &lt;element name="Files" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}ArrayOfCoordinateFile" minOccurs="0"/>
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
    "coordinateStatusDataMessage",
    "files"
})
@XmlRootElement(name = "CoordinateStatusMessage")
public class CoordinateStatusMessage {

    @XmlElement(name = "CoordinateStatusDataMessage")
    protected CoordinateStatusData coordinateStatusDataMessage;
    @XmlElement(name = "Files")
    protected ArrayOfCoordinateFile files;

    /**
     * Gets the value of the coordinateStatusDataMessage property.
     * 
     * @return
     *     possible object is
     *     {@link CoordinateStatusData }
     *     
     */
    public CoordinateStatusData getCoordinateStatusDataMessage() {
        return coordinateStatusDataMessage;
    }

    /**
     * Sets the value of the coordinateStatusDataMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link CoordinateStatusData }
     *     
     */
    public void setCoordinateStatusDataMessage(CoordinateStatusData value) {
        this.coordinateStatusDataMessage = value;
    }

    /**
     * Gets the value of the files property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfCoordinateFile }
     *     
     */
    public ArrayOfCoordinateFile getFiles() {
        return files;
    }

    /**
     * Sets the value of the files property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfCoordinateFile }
     *     
     */
    public void setFiles(ArrayOfCoordinateFile value) {
        this.files = value;
    }

}