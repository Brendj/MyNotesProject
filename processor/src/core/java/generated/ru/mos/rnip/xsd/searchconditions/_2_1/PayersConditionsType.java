
package generated.ru.mos.rnip.xsd.searchconditions._2_1;

import generated.ru.mos.rnip.xsd.common._2_1.KBKlist;
import generated.ru.mos.rnip.xsd.common._2_1.PayerIdentificationType;
import generated.ru.mos.rnip.xsd.common._2_1.TimeIntervalType;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for PayersConditionsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PayersConditionsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;choice>
 *           &lt;element name="PayerIdentifier" type="{http://rnip.mos.ru/xsd/Common/2.1.1}PayerIdentifierType" maxOccurs="100"/>
 *           &lt;element name="PayerIdentification" type="{http://rnip.mos.ru/xsd/Common/2.1.1}PayerIdentificationType"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.1.1}TimeInterval" minOccurs="0"/>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.1.1}KBKlist" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PayersConditionsType", propOrder = {
    "payerIdentifier",
    "payerIdentification",
    "timeInterval",
    "kbKlist"
})
public class PayersConditionsType {

    @XmlElement(name = "PayerIdentifier")
    protected List<String> payerIdentifier;
    @XmlElement(name = "PayerIdentification")
    protected PayerIdentificationType payerIdentification;
    @XmlElement(name = "TimeInterval", namespace = "http://rnip.mos.ru/xsd/Common/2.1.1")
    protected TimeIntervalType timeInterval;
    @XmlElement(name = "KBKlist", namespace = "http://rnip.mos.ru/xsd/Common/2.1.1")
    protected KBKlist kbKlist;

    /**
     * Gets the value of the payerIdentifier property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the payerIdentifier property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPayerIdentifier().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getPayerIdentifier() {
        if (payerIdentifier == null) {
            payerIdentifier = new ArrayList<String>();
        }
        return this.payerIdentifier;
    }

    /**
     * Gets the value of the payerIdentification property.
     * 
     * @return
     *     possible object is
     *     {@link PayerIdentificationType }
     *     
     */
    public PayerIdentificationType getPayerIdentification() {
        return payerIdentification;
    }

    /**
     * Sets the value of the payerIdentification property.
     * 
     * @param value
     *     allowed object is
     *     {@link PayerIdentificationType }
     *     
     */
    public void setPayerIdentification(PayerIdentificationType value) {
        this.payerIdentification = value;
    }

    /**
     * Временной интервал, за который запрашивается информация из ГИС ГМП
     * 					
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

}
