
package generated.nsiws.nsi.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Response complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Response">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="error" type="{http://rstyle.com/nsi/beans}NSIError" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Response", propOrder = {
    "error"
})
@XmlSeeAlso({
    GeneralResponse.class,
    LoginResponse.class,
    GetSpecResponse.class
})
public class Response {

    protected NSIError error;

    /**
     * Gets the value of the error property.
     * 
     * @return
     *     possible object is
     *     {@link NSIError }
     *     
     */
    public NSIError getError() {
        return error;
    }

    /**
     * Sets the value of the error property.
     * 
     * @param value
     *     allowed object is
     *     {@link NSIError }
     *     
     */
    public void setError(NSIError value) {
        this.error = value;
    }

}
