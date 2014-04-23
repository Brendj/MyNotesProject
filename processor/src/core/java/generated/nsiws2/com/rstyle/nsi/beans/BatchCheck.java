
package generated.nsiws2.com.rstyle.nsi.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BatchCheck complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BatchCheck">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="uid" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="checkInterval" type="{http://www.w3.org/2001/XMLSchema}long" minOccurs="0"/>
 *         &lt;element name="message" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="processTime" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BatchCheck", propOrder = {
    "uid",
    "checkInterval",
    "message",
    "processTime"
})
public class BatchCheck {

    @XmlElement(required = true)
    protected String uid;
    protected Long checkInterval;
    protected String message;
    protected String processTime;

    /**
     * Gets the value of the uid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getUid() {
        return uid;
    }

    /**
     * Sets the value of the uid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setUid(String value) {
        this.uid = value;
    }

    /**
     * Gets the value of the checkInterval property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getCheckInterval() {
        return checkInterval;
    }

    /**
     * Sets the value of the checkInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setCheckInterval(Long value) {
        this.checkInterval = value;
    }

    /**
     * Gets the value of the message property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getMessage() {
        return message;
    }

    /**
     * Sets the value of the message property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setMessage(String value) {
        this.message = value;
    }

    /**
     * Gets the value of the processTime property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProcessTime() {
        return processTime;
    }

    /**
     * Sets the value of the processTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProcessTime(String value) {
        this.processTime = value;
    }

}
