
package generated.ru.mos.rnip.xsd.searchconditions._2_1;

import generated.ru.mos.rnip.xsd.common._2_1.KBKlist;
import generated.ru.mos.rnip.xsd.common._2_1.TimeIntervalType;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for TimeConditionsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="TimeConditionsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.1.0}TimeInterval"/>
 *         &lt;element name="Beneficiary" maxOccurs="10" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;attribute name="inn" use="required" type="{http://rnip.mos.ru/xsd/Common/2.1.0}INNType" />
 *                 &lt;attribute name="kpp" type="{http://rnip.mos.ru/xsd/Common/2.1.0}KPPType" />
 *                 &lt;attribute name="subsystemId" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.1.0}KBKlist" minOccurs="0"/>
 *         &lt;element name="ServicesCodesList" type="{http://rnip.mos.ru/xsd/SearchConditions/2.1.0}ServicesConditionsType" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "TimeConditionsType", propOrder = {
    "timeInterval",
    "beneficiary",
    "kbKlist",
    "servicesCodesList"
})
public class TimeConditionsType {

    @XmlElement(name = "TimeInterval", namespace = "http://rnip.mos.ru/xsd/Common/2.1.0", required = true)
    protected TimeIntervalType timeInterval;
    @XmlElement(name = "Beneficiary")
    protected List<TimeConditionsType.Beneficiary> beneficiary;
    @XmlElement(name = "KBKlist", namespace = "http://rnip.mos.ru/xsd/Common/2.1.0")
    protected KBKlist kbKlist;
    @XmlElement(name = "ServicesCodesList")
    protected ServicesConditionsType servicesCodesList;

    /**
     * Gets the value of the timeInterval property.
     * 
     * @return
     *     possible object is
     *     {@link TimeIntervalType }
     *     
     */
    public TimeIntervalType getTimeInterval() {
        return timeInterval;
    }

    /**
     * Sets the value of the timeInterval property.
     * 
     * @param value
     *     allowed object is
     *     {@link TimeIntervalType }
     *     
     */
    public void setTimeInterval(TimeIntervalType value) {
        this.timeInterval = value;
    }

    /**
     * Gets the value of the beneficiary property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the beneficiary property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getBeneficiary().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link TimeConditionsType.Beneficiary }
     * 
     * 
     */
    public List<TimeConditionsType.Beneficiary> getBeneficiary() {
        if (beneficiary == null) {
            beneficiary = new ArrayList<TimeConditionsType.Beneficiary>();
        }
        return this.beneficiary;
    }

    /**
     * Перечень КБК
     * 
     * @return
     *     possible object is
     *     {@link KBKlist }
     *     
     */
    public KBKlist getKBKlist() {
        return kbKlist;
    }

    /**
     * Sets the value of the kbKlist property.
     * 
     * @param value
     *     allowed object is
     *     {@link KBKlist }
     *     
     */
    public void setKBKlist(KBKlist value) {
        this.kbKlist = value;
    }

    /**
     * Gets the value of the servicesCodesList property.
     * 
     * @return
     *     possible object is
     *     {@link ServicesConditionsType }
     *     
     */
    public ServicesConditionsType getServicesCodesList() {
        return servicesCodesList;
    }

    /**
     * Sets the value of the servicesCodesList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServicesConditionsType }
     *     
     */
    public void setServicesCodesList(ServicesConditionsType value) {
        this.servicesCodesList = value;
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
     *       &lt;attribute name="inn" use="required" type="{http://rnip.mos.ru/xsd/Common/2.1.0}INNType" />
     *       &lt;attribute name="kpp" type="{http://rnip.mos.ru/xsd/Common/2.1.0}KPPType" />
     *       &lt;attribute name="subsystemId" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "")
    public static class Beneficiary {

        @XmlAttribute(name = "inn", required = true)
        protected String inn;
        @XmlAttribute(name = "kpp")
        protected String kpp;
        @XmlAttribute(name = "subsystemId")
        protected String subsystemId;

        /**
         * Gets the value of the inn property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getInn() {
            return inn;
        }

        /**
         * Sets the value of the inn property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setInn(String value) {
            this.inn = value;
        }

        /**
         * Gets the value of the kpp property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getKpp() {
            return kpp;
        }

        /**
         * Sets the value of the kpp property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setKpp(String value) {
            this.kpp = value;
        }

        /**
         * Gets the value of the subsystemId property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getSubsystemId() {
            return subsystemId;
        }

        /**
         * Sets the value of the subsystemId property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setSubsystemId(String value) {
            this.subsystemId = value;
        }

    }

}
