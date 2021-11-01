
package generated.emp_events;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Ответа на запрос создания профиля пользователя
 * 
 * <p>Java class for createCitizenProfileResponse_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="createCitizenProfileResponse_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{urn://subscription.api.emp.altarix.ru}BaseResponse_Type">
 *       &lt;sequence>
 *         &lt;element name="return">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="SSOID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *                   &lt;element name="citizenId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "createCitizenProfileResponse_Type", propOrder = {
    "_return"
})
public class CreateCitizenProfileResponseType
    extends BaseResponseType
{

    @XmlElement(name = "return", required = true, nillable = true)
    protected CreateCitizenProfileResponseType.Return _return;

    /**
     * Gets the value of the return property.
     * 
     * @return
     *     possible object is
     *     {@link CreateCitizenProfileResponseType.Return }
     *     
     */
    public CreateCitizenProfileResponseType.Return getReturn() {
        return _return;
    }

    /**
     * Sets the value of the return property.
     * 
     * @param value
     *     allowed object is
     *     {@link CreateCitizenProfileResponseType.Return }
     *     
     */
    public void setReturn(CreateCitizenProfileResponseType.Return value) {
        this._return = value;
    }


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
     *         &lt;element name="SSOID" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
     *         &lt;element name="citizenId" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
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
        "ssoid",
        "citizenId"
    })
    public static class Return {

        @XmlElement(name = "SSOID")
        protected String ssoid;
        protected Integer citizenId;

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

    }

}
