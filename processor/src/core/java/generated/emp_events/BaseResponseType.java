
package generated.emp_events;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for BaseResponse_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BaseResponse_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="errorCode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="errorMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BaseResponse_Type", propOrder = {
    "errorCode",
    "errorMessage"
})
@XmlSeeAlso({
    GetCitizenProfileResponseType.class,
    SelectSubscriptionStreamsResponseType.class,
    SetUpCitizenSubscriptionSettingsResponseType.class,
    SendSubscriptionStreamEventsResponseType.class,
    CreateCitizenProfileResponseType.class,
    SetCitizenProfileResponseType.class,
    SelectCitizenSubscriptionsResponseType.class,
    GetSubscriptionStreamCategoryResponseType.class,
    ActivateCitizenSubscriptionResponseType.class,
    DeactivateCitizenSubscriptionResponseType.class,
    GetSubscriptionStreamResponseType.class,
    SelectSubscriptionStreamCategoriesResponseType.class,
    DeleteCitizenProfileOptionsResponseType.class,
    DropCitizenSubscriptionSettingsResponseType.class
})
public class BaseResponseType {

    protected int errorCode;
    protected String errorMessage;

    /**
     * Gets the value of the errorCode property.
     * 
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the value of the errorCode property.
     * 
     */
    public void setErrorCode(int value) {
        this.errorCode = value;
    }

    /**
     * Gets the value of the errorMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the value of the errorMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorMessage(String value) {
        this.errorMessage = value;
    }

}
