
package generated.nsiws2.ru.gosuslugi.smev.rev110801;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ExtMessageDataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExtMessageDataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AppData" type="{http://smev.gosuslugi.ru/rev110801}ExtAppDataType"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExtMessageDataType", propOrder = {
    "appData"
})
public class ExtMessageDataType {

    @XmlElement(name = "AppData", required = true)
    protected ExtAppDataType appData;

    /**
     * Gets the value of the appData property.
     * 
     * @return
     *     possible object is
     *     {@link ExtAppDataType }
     *     
     */
    public ExtAppDataType getAppData() {
        return appData;
    }

    /**
     * Sets the value of the appData property.
     * 
     * @param value
     *     allowed object is
     *     {@link ExtAppDataType }
     *     
     */
    public void setAppData(ExtAppDataType value) {
        this.appData = value;
    }

}
