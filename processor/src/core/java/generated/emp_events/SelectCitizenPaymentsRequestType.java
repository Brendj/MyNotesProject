
package generated.emp_events;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * «апрос получени€ платежей пользовател€
 * 
 * <p>Java class for selectCitizenPaymentsRequest_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="selectCitizenPaymentsRequest_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{urn://subscription.api.emp.altarix.ru}BaseRequest_Type">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="SSOID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *           &lt;element name="citizenId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *           &lt;element name="msisdn" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;/choice>
 *         &lt;element name="streamId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "selectCitizenPaymentsRequest_Type", propOrder = {
    "ssoid",
    "citizenId",
    "msisdn",
    "streamId"
})
public class SelectCitizenPaymentsRequestType
    extends BaseRequestType
{

    @XmlElement(name = "SSOID")
    protected String ssoid;
    protected Integer citizenId;
    protected Long msisdn;
    protected int streamId;

    /**
     * Gets the value of the ssoid property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSSOID() {
        return ssoid;
    }

    /**
     * Sets the value of the ssoid property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSSOID(String value) {
        this.ssoid = value;
    }

    /**
     * Gets the value of the citizenId property.
     * 
     * @return
     *     possible object is
     *     {@link Integer }
     *     
     */
    public Integer getCitizenId() {
        return citizenId;
    }

    /**
     * Sets the value of the citizenId property.
     * 
     * @param value
     *     allowed object is
     *     {@link Integer }
     *     
     */
    public void setCitizenId(Integer value) {
        this.citizenId = value;
    }

    /**
     * Gets the value of the msisdn property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getMsisdn() {
        return msisdn;
    }

    /**
     * Sets the value of the msisdn property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setMsisdn(Long value) {
        this.msisdn = value;
    }

    /**
     * Gets the value of the streamId property.
     * 
     */
    public int getStreamId() {
        return streamId;
    }

    /**
     * Sets the value of the streamId property.
     * 
     */
    public void setStreamId(int value) {
        this.streamId = value;
    }

}
