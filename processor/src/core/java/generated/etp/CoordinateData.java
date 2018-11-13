
package generated.etp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * ���������
 * 
 * <p>Java class for CoordinateData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CoordinateData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Service" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}RequestService"/>
 *         &lt;element name="SignService" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}RequestServiceForSign"/>
 *         &lt;element name="Signature" type="{http://www.w3.org/2001/XMLSchema}anyType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="Status" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}RequestStatus" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CoordinateData", propOrder = {
    "service",
    "signService",
    "signature",
    "status"
})
public class CoordinateData {

    @XmlElement(name = "Service", required = true)
    protected RequestService service;
    @XmlElement(name = "SignService", required = true)
    protected RequestServiceForSign signService;
    @XmlElement(name = "Signature")
    protected List<Object> signature;
    @XmlElement(name = "Status")
    protected RequestStatus status;

    /**
     * Gets the value of the service property.
     * 
     * @return
     *     possible object is
     *     {@link RequestService }
     *     
     */
    public RequestService getService() {
        return service;
    }

    /**
     * Sets the value of the service property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestService }
     *     
     */
    public void setService(RequestService value) {
        this.service = value;
    }

    /**
     * Gets the value of the signService property.
     * 
     * @return
     *     possible object is
     *     {@link RequestServiceForSign }
     *     
     */
    public RequestServiceForSign getSignService() {
        return signService;
    }

    /**
     * Sets the value of the signService property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestServiceForSign }
     *     
     */
    public void setSignService(RequestServiceForSign value) {
        this.signService = value;
    }

    /**
     * Gets the value of the signature property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the signature property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getSignature().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Object }
     * 
     * 
     */
    public List<Object> getSignature() {
        if (signature == null) {
            signature = new ArrayList<Object>();
        }
        return this.signature;
    }

    /**
     * Gets the value of the status property.
     * 
     * @return
     *     possible object is
     *     {@link RequestStatus }
     *     
     */
    public RequestStatus getStatus() {
        return status;
    }

    /**
     * Sets the value of the status property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestStatus }
     *     
     */
    public void setStatus(RequestStatus value) {
        this.status = value;
    }

}
