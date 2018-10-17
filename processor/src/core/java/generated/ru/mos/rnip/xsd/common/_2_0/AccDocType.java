
package generated.ru.mos.rnip.xsd.common._2_0;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for AccDocType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AccDocType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="accDocNo">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;pattern value="\d{1,6}"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="accDocDate" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AccDocType")
public class AccDocType {

    @XmlAttribute(name = "accDocNo")
    protected String accDocNo;
    @XmlAttribute(name = "accDocDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar accDocDate;

    /**
     * Gets the value of the accDocNo property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAccDocNo() {
        return accDocNo;
    }

    /**
     * Sets the value of the accDocNo property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAccDocNo(String value) {
        this.accDocNo = value;
    }

    /**
     * Gets the value of the accDocDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getAccDocDate() {
        return accDocDate;
    }

    /**
     * Sets the value of the accDocDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setAccDocDate(XMLGregorianCalendar value) {
        this.accDocDate = value;
    }

}
