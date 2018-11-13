
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
 *         &lt;element name="CoordinateTaskDataMessage" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}CoordinateTaskData" minOccurs="0"/>
 *         &lt;element name="ServiceHeader" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}Headers" minOccurs="0"/>
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
    "coordinateTaskDataMessage",
    "serviceHeader",
    "files"
})
@XmlRootElement(name = "CoordinateTaskMessage")
public class CoordinateTaskMessage {

    @XmlElement(name = "CoordinateTaskDataMessage")
    protected CoordinateTaskData coordinateTaskDataMessage;
    @XmlElement(name = "ServiceHeader")
    protected Headers serviceHeader;
    @XmlElement(name = "Files")
    protected ArrayOfCoordinateFile files;

    /**
     * Gets the value of the coordinateTaskDataMessage property.
     * 
     * @return
     *     possible object is
     *     {@link CoordinateTaskData }
     *     
     */
    public CoordinateTaskData getCoordinateTaskDataMessage() {
        return coordinateTaskDataMessage;
    }

    /**
     * Sets the value of the coordinateTaskDataMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link CoordinateTaskData }
     *     
     */
    public void setCoordinateTaskDataMessage(CoordinateTaskData value) {
        this.coordinateTaskDataMessage = value;
    }

    /**
     * Gets the value of the serviceHeader property.
     * 
     * @return
     *     possible object is
     *     {@link Headers }
     *     
     */
    public Headers getServiceHeader() {
        return serviceHeader;
    }

    /**
     * Sets the value of the serviceHeader property.
     * 
     * @param value
     *     allowed object is
     *     {@link Headers }
     *     
     */
    public void setServiceHeader(Headers value) {
        this.serviceHeader = value;
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
