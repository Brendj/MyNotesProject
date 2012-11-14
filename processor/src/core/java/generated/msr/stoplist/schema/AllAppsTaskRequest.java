
package generated.msr.stoplist.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for allAppsTaskRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="allAppsTaskRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="identityType" type="{http://webservice.msr.com/sl/schema/}identityType" form="qualified"/>
 *         &lt;element name="app" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "allAppsTaskRequest", propOrder = {
    "identityType",
    "app"
})
public class AllAppsTaskRequest {

    @XmlElement(required = true)
    protected IdentityType identityType;
    @XmlElement(required = true)
    protected String app;

    /**
     * Gets the value of the identityType property.
     * 
     * @return
     *     possible object is
     *     {@link IdentityType }
     *     
     */
    public IdentityType getIdentityType() {
        return identityType;
    }

    /**
     * Sets the value of the identityType property.
     * 
     * @param value
     *     allowed object is
     *     {@link IdentityType }
     *     
     */
    public void setIdentityType(IdentityType value) {
        this.identityType = value;
    }

    /**
     * Gets the value of the app property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getApp() {
        return app;
    }

    /**
     * Sets the value of the app property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setApp(String value) {
        this.app = value;
    }

}
