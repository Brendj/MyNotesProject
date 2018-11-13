
package generated.etp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * ��������� ����������
 * 
 * <p>Java class for RequestResult complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestResult">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Result" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}DictionaryItem" minOccurs="0"/>
 *         &lt;element name="DeclineReasons" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}ArrayOfDictionaryItem" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestResult", propOrder = {
    "result",
    "declineReasons"
})
public class RequestResult {

    @XmlElement(name = "Result")
    protected DictionaryItem result;
    @XmlElement(name = "DeclineReasons")
    protected ArrayOfDictionaryItem declineReasons;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link DictionaryItem }
     *     
     */
    public DictionaryItem getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link DictionaryItem }
     *     
     */
    public void setResult(DictionaryItem value) {
        this.result = value;
    }

    /**
     * Gets the value of the declineReasons property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfDictionaryItem }
     *     
     */
    public ArrayOfDictionaryItem getDeclineReasons() {
        return declineReasons;
    }

    /**
     * Sets the value of the declineReasons property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfDictionaryItem }
     *     
     */
    public void setDeclineReasons(ArrayOfDictionaryItem value) {
        this.declineReasons = value;
    }

}
