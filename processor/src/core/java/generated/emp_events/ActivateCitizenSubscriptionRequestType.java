
package generated.emp_events;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


/**
 *
 * 
 * <p>Java class for activateCitizenSubscriptionRequest_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="activateCitizenSubscriptionRequest_Type">
 *   &lt;complexContent>
 *     &lt;extension base="{urn://subscription.api.emp.altarix.ru}BaseRequest_Type">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="SSOID" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *           &lt;element name="citizenId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *           &lt;element name="msisdn" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *         &lt;/choice>
 *         &lt;element name="streamId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="settings" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="setting" type="{urn://subscription.api.emp.altarix.ru}StreamSetting_Type" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="options" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;choice maxOccurs="unbounded">
 *                   &lt;element name="optionId" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *                   &lt;element name="option" type="{urn://subscription.api.emp.altarix.ru}CitizenProfileOptionBase_Type"/>
 *                 &lt;/choice>
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
@XmlType(name = "activateCitizenSubscriptionRequest_Type", propOrder = {
    "ssoid",
    "citizenId",
    "msisdn",
    "streamId",
    "settings",
    "options"
})
public class ActivateCitizenSubscriptionRequestType
    extends BaseRequestType
{

    @XmlElement(name = "SSOID")
    protected String ssoid;
    protected Integer citizenId;
    protected Long msisdn;
    protected int streamId;
    protected ActivateCitizenSubscriptionRequestType.Settings settings;
    protected ActivateCitizenSubscriptionRequestType.Options options;

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

    /**
     * Gets the value of the settings property.
     * 
     * @return
     *     possible object is
     *     {@link ActivateCitizenSubscriptionRequestType.Settings }
     *     
     */
    public ActivateCitizenSubscriptionRequestType.Settings getSettings() {
        return settings;
    }

    /**
     * Sets the value of the settings property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActivateCitizenSubscriptionRequestType.Settings }
     *     
     */
    public void setSettings(ActivateCitizenSubscriptionRequestType.Settings value) {
        this.settings = value;
    }

    /**
     * Gets the value of the options property.
     * 
     * @return
     *     possible object is
     *     {@link ActivateCitizenSubscriptionRequestType.Options }
     *     
     */
    public ActivateCitizenSubscriptionRequestType.Options getOptions() {
        return options;
    }

    /**
     * Sets the value of the options property.
     * 
     * @param value
     *     allowed object is
     *     {@link ActivateCitizenSubscriptionRequestType.Options }
     *     
     */
    public void setOptions(ActivateCitizenSubscriptionRequestType.Options value) {
        this.options = value;
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
     *       &lt;choice maxOccurs="unbounded">
     *         &lt;element name="optionId" type="{http://www.w3.org/2001/XMLSchema}int"/>
     *         &lt;element name="option" type="{urn://subscription.api.emp.altarix.ru}CitizenProfileOptionBase_Type"/>
     *       &lt;/choice>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "optionIdOrOption"
    })
    public static class Options {

        @XmlElements({
            @XmlElement(name = "optionId", type = Integer.class),
            @XmlElement(name = "option", type = CitizenProfileOptionBaseType.class)
        })
        protected List<Object> optionIdOrOption;

        /**
         * Gets the value of the optionIdOrOption property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the optionIdOrOption property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getOptionIdOrOption().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link Integer }
         * {@link CitizenProfileOptionBaseType }
         * 
         * 
         */
        public List<Object> getOptionIdOrOption() {
            if (optionIdOrOption == null) {
                optionIdOrOption = new ArrayList<Object>();
            }
            return this.optionIdOrOption;
        }

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
     *         &lt;element name="setting" type="{urn://subscription.api.emp.altarix.ru}StreamSetting_Type" maxOccurs="unbounded"/>
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
        "setting"
    })
    public static class Settings {

        @XmlElement(required = true)
        protected List<StreamSettingType> setting;

        /**
         * Gets the value of the setting property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the setting property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getSetting().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link StreamSettingType }
         * 
         * 
         */
        public List<StreamSettingType> getSetting() {
            if (setting == null) {
                setting = new ArrayList<StreamSettingType>();
            }
            return this.setting;
        }

    }

}
