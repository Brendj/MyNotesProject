
package generated.msr.stoplist.schema;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for areasTaskRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="areasTaskRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="identityType" type="{http://webservice.msr.com/sl/schema/}identityType" form="qualified"/>
 *         &lt;element name="app" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="area" type="{http://www.w3.org/2001/XMLSchema}string" form="qualified"/>
 *         &lt;element name="fromDate" type="{http://www.w3.org/2001/XMLSchema}date" form="qualified"/>
 *         &lt;element name="toDate" type="{http://www.w3.org/2001/XMLSchema}date" form="qualified"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "areasTaskRequest", propOrder = {
    "identityType",
    "app",
    "area",
    "fromDate",
    "toDate"
})
public class AreasTaskRequest {

    @XmlElement(required = true)
    protected IdentityType identityType;
    @XmlElement(required = true)
    protected String app;
    @XmlElement(required = true)
    protected String area;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar fromDate;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar toDate;

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

    /**
     * Gets the value of the area property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getArea() {
        return area;
    }

    /**
     * Sets the value of the area property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setArea(String value) {
        this.area = value;
    }

    /**
     * Gets the value of the fromDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFromDate() {
        return fromDate;
    }

    /**
     * Sets the value of the fromDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFromDate(XMLGregorianCalendar value) {
        this.fromDate = value;
    }

    /**
     * Gets the value of the toDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getToDate() {
        return toDate;
    }

    /**
     * Sets the value of the toDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setToDate(XMLGregorianCalendar value) {
        this.toDate = value;
    }

}
