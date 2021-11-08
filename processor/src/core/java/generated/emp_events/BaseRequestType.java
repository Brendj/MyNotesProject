
package generated.emp_events;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BaseRequest_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BaseRequest_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="token" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BaseRequest_Type", propOrder = {
    "token"
})
@XmlSeeAlso({
    ActivateCitizenSubscriptionRequestType.class,
    GetCitizenProfileRequestType.class,
    DeactivateCitizenSubscriptionRequestType.class,
    GetSubscriptionStreamRequestType.class,
    SelectSubscriptionStreamsRequestType.class,
    DropCitizenSubscriptionSettingsRequestType.class,
    SelectCitizenSubscriptionsRequestType.class,
    GetSubscriptionStreamCategoryRequestType.class,
    GetStreamDictValueRequestType.class,
    SetUpCitizenSubscriptionSettingsRequestType.class,
    SetCitizenProfileRequestType.class,
    SelectStreamTariffsRequestType.class,
    SelectSubscriptionStreamCategoriesRequestType.class,
    SelectCitizenPaymentsRequestType.class,
    CreateCitizenProfileRequestType.class,
    SendSubscriptionStreamEventsRequestType.class,
    DeleteCitizenProfileOptionsRequestType.class
})
public class BaseRequestType {

    @XmlElement(required = true)
    protected String token;

    /**
     * Gets the value of the token property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the value of the token property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToken(String value) {
        this.token = value;
    }

}
