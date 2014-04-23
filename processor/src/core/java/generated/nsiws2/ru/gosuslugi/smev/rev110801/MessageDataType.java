
package generated.nsiws2.ru.gosuslugi.smev.rev110801;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MessageDataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MessageDataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://smev.gosuslugi.ru/rev110801}AppData" minOccurs="0"/>
 *         &lt;element ref="{http://smev.gosuslugi.ru/rev110801}AppDocument" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MessageDataType", propOrder = {
    "appData",
    "appDocument"
})
public class MessageDataType {

    @XmlElement(name = "AppData")
    protected AppDataType appData;
    @XmlElement(name = "AppDocument")
    protected AppDocumentType appDocument;

    /**
     * Gets the value of the appData property.
     * 
     * @return
     *     possible object is
     *     {@link AppDataType }
     *     
     */
    public AppDataType getAppData() {
        return appData;
    }

    /**
     * Sets the value of the appData property.
     * 
     * @param value
     *     allowed object is
     *     {@link AppDataType }
     *     
     */
    public void setAppData(AppDataType value) {
        this.appData = value;
    }

    /**
     * Gets the value of the appDocument property.
     * 
     * @return
     *     possible object is
     *     {@link AppDocumentType }
     *     
     */
    public AppDocumentType getAppDocument() {
        return appDocument;
    }

    /**
     * Sets the value of the appDocument property.
     * 
     * @param value
     *     allowed object is
     *     {@link AppDocumentType }
     *     
     */
    public void setAppDocument(AppDocumentType value) {
        this.appDocument = value;
    }

}
