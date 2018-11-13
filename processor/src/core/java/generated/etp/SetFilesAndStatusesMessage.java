
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
 *         &lt;element name="Files" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}ArrayOfCoordinateFile" minOccurs="0"/>
 *         &lt;element name="StatusesMessage" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}ArrayOfSetFilesAndStatusesData" minOccurs="0"/>
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
    "files",
    "statusesMessage"
})
@XmlRootElement(name = "SetFilesAndStatusesMessage")
public class SetFilesAndStatusesMessage {

    @XmlElement(name = "Files")
    protected ArrayOfCoordinateFile files;
    @XmlElement(name = "StatusesMessage")
    protected ArrayOfSetFilesAndStatusesData statusesMessage;

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

    /**
     * Gets the value of the statusesMessage property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfSetFilesAndStatusesData }
     *     
     */
    public ArrayOfSetFilesAndStatusesData getStatusesMessage() {
        return statusesMessage;
    }

    /**
     * Sets the value of the statusesMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfSetFilesAndStatusesData }
     *     
     */
    public void setStatusesMessage(ArrayOfSetFilesAndStatusesData value) {
        this.statusesMessage = value;
    }

}
