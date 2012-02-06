
package generated.nfp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * Данные ПФ РФ 
 * 
 * <p>Java class for PensionFundType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="PensionFundType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="snils" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="createSnilsFlag" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PensionFundType", propOrder = {
    "snils",
    "createSnilsFlag"
})
public class PensionFundType {

    @XmlElement(required = true)
    protected String snils;
    protected boolean createSnilsFlag;

    /**
     * Gets the value of the snils property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSnils() {
        return snils;
    }

    /**
     * Sets the value of the snils property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSnils(String value) {
        this.snils = value;
    }

    /**
     * Gets the value of the createSnilsFlag property.
     * 
     */
    public boolean isCreateSnilsFlag() {
        return createSnilsFlag;
    }

    /**
     * Sets the value of the createSnilsFlag property.
     * 
     */
    public void setCreateSnilsFlag(boolean value) {
        this.createSnilsFlag = value;
    }

}
