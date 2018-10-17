
package generated.ru.mos.rnip.xsd.searchconditions._2_0;

import generated.ru.mos.rnip.xsd.common._2_0.KBKlist;
import generated.ru.mos.rnip.xsd.common._2_0.TimeIntervalType;

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
 *           &lt;element name="PayerInn" type="{http://rnip.mos.ru/xsd/Common/2.0.1}INNType" maxOccurs="100"/>
 *           &lt;element name="PayerIdentifier" type="{http://rnip.mos.ru/xsd/Common/2.0.1}PayerIdentifierType" maxOccurs="100"/>
 *         &lt;/choice>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.0.1}TimeInterval" minOccurs="0"/>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/Common/2.0.1}KBKlist" minOccurs="0"/>
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
    "payerInn",
    "payerIdentifier",
    "timeInterval",
    "kbKlist"
})
public class PayersConditionsType {

    @XmlElement(name = "PayerInn")
    protected List<String> payerInn;
    @XmlElement(name = "PayerIdentifier")
    protected List<String> payerIdentifier;
    @XmlElement(name = "TimeInterval", namespace = "http://rnip.mos.ru/xsd/Common/2.0.1")
    protected TimeIntervalType timeInterval;
    @XmlElement(name = "KBKlist", namespace = "http://rnip.mos.ru/xsd/Common/2.0.1")
    protected KBKlist kbKlist;

    /**
     * Gets the value of the payerInn property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the payerInn property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPayerInn().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getPayerInn() {
        if (payerInn == null) {
            payerInn = new ArrayList<String>();
        }
        return this.payerInn;
    }

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
     * ��������� ��������, �� ������� ������������� ���������� �� ��� ���
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
     * �������� ���
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
