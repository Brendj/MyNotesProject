
package generated.nfp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Дополнительные сведения 
 * 
 * <p>Java class for AdditionalInfoType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AdditionalInfoType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="receivingOfficeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="createDSignatureFlag" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *         &lt;element name="registrationCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="officeCode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="formFillingDate" type="{http://www.w3.org/2001/XMLSchema}date"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AdditionalInfoType", propOrder = {
    "receivingOfficeCode",
    "createDSignatureFlag",
    "registrationCode",
    "officeCode",
    "formFillingDate"
})
public class AdditionalInfoType {

    @XmlElement(required = true)
    protected String receivingOfficeCode;
    protected boolean createDSignatureFlag;
    @XmlElement(required = true)
    protected String registrationCode;
    @XmlElement(required = true)
    protected String officeCode;
    @XmlElement(required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar formFillingDate;

    /**
     * Gets the value of the receivingOfficeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getReceivingOfficeCode() {
        return receivingOfficeCode;
    }

    /**
     * Sets the value of the receivingOfficeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setReceivingOfficeCode(String value) {
        this.receivingOfficeCode = value;
    }

    /**
     * Gets the value of the createDSignatureFlag property.
     * 
     */
    public boolean isCreateDSignatureFlag() {
        return createDSignatureFlag;
    }

    /**
     * Sets the value of the createDSignatureFlag property.
     * 
     */
    public void setCreateDSignatureFlag(boolean value) {
        this.createDSignatureFlag = value;
    }

    /**
     * Gets the value of the registrationCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRegistrationCode() {
        return registrationCode;
    }

    /**
     * Sets the value of the registrationCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRegistrationCode(String value) {
        this.registrationCode = value;
    }

    /**
     * Gets the value of the officeCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOfficeCode() {
        return officeCode;
    }

    /**
     * Sets the value of the officeCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOfficeCode(String value) {
        this.officeCode = value;
    }

    /**
     * Gets the value of the formFillingDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFormFillingDate() {
        return formFillingDate;
    }

    /**
     * Sets the value of the formFillingDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFormFillingDate(XMLGregorianCalendar value) {
        this.formFillingDate = value;
    }

}
