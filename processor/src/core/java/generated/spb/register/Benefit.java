
package generated.spb.register;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * 
 * 			code - ��� ������
 * 			adate - ���� ������ �������� ������
 * 			bdate - ���� ��������� �������� ������
 *                     
 * 
 * <p>Java class for benefit complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="benefit">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="code" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="adate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="bdate" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "benefit", propOrder = {
    "code",
    "adate",
    "bdate"
})
public class Benefit {

    @XmlElement(required = true)
    protected String code;
    @XmlElement(required = true)
    protected String adate;
    @XmlElement(required = true)
    protected String bdate;

    /**
     * Gets the value of the code property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCode() {
        return code;
    }

    /**
     * Sets the value of the code property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCode(String value) {
        this.code = value;
    }

    /**
     * Gets the value of the adate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAdate() {
        return adate;
    }

    /**
     * Sets the value of the adate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAdate(String value) {
        this.adate = value;
    }

    /**
     * Gets the value of the bdate property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getBdate() {
        return bdate;
    }

    /**
     * Sets the value of the bdate property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setBdate(String value) {
        this.bdate = value;
    }

}
