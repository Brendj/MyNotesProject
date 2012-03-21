
package generated.ru.teralect;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


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
 *         &lt;element name="deliveryStatus" type="{http://sms.teralect.ru/}SmsDeliveryStatus" minOccurs="0"/>
 *         &lt;element name="rc" type="{http://sms.teralect.ru/}RetCode" minOccurs="0"/>
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
    "deliveryStatus",
    "rc"
})
@XmlRootElement(name = "GetDeliveryStatusResp")
public class GetDeliveryStatusResp {

    protected SmsDeliveryStatus deliveryStatus;
    protected RetCode rc;

    /**
     * Gets the value of the deliveryStatus property.
     * 
     * @return
     *     possible object is
     *     {@link SmsDeliveryStatus }
     *     
     */
    public SmsDeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    /**
     * Sets the value of the deliveryStatus property.
     * 
     * @param value
     *     allowed object is
     *     {@link SmsDeliveryStatus }
     *     
     */
    public void setDeliveryStatus(SmsDeliveryStatus value) {
        this.deliveryStatus = value;
    }

    /**
     * Gets the value of the rc property.
     * 
     * @return
     *     possible object is
     *     {@link RetCode }
     *     
     */
    public RetCode getRc() {
        return rc;
    }

    /**
     * Sets the value of the rc property.
     * 
     * @param value
     *     allowed object is
     *     {@link RetCode }
     *     
     */
    public void setRc(RetCode value) {
        this.rc = value;
    }

}
