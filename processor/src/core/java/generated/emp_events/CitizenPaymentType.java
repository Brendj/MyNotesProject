
package generated.emp_events;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for CitizenPayment_Type complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CitizenPayment_Type">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="tariff�ode" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="paymentAmount" type="{http://www.w3.org/2001/XMLSchema}positiveInteger"/>
 *         &lt;element name="paymentNarrative" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="paymentSystemIdentifier" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="paymentTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="resources">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="resource" type="{urn://subscription.api.emp.altarix.ru}CitizenPaymentResource_Type" maxOccurs="unbounded"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CitizenPayment_Type", propOrder = {
    "tariffCode",
    "paymentAmount",
    "paymentNarrative",
    "paymentSystemIdentifier",
    "paymentTime",
    "resources"
})
public class CitizenPaymentType {

    @XmlElement(required = true)
    protected String tariffCode;
    @XmlElement(required = true)
    @XmlSchemaType(name = "positiveInteger")
    protected BigInteger paymentAmount;
    @XmlElement(required = true)
    protected String paymentNarrative;
    @XmlElement(required = true)
    protected String paymentSystemIdentifier;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar paymentTime;
    @XmlElement(required = true)
    protected CitizenPaymentType.Resources resources;

    /**
     * Gets the value of the tariff�ode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getTariffCode() {
        return tariffCode;
    }

    /**
     * Sets the value of the tariff�ode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setTariffCode(String value) {
        this.tariffCode = value;
    }

    /**
     * Gets the value of the paymentAmount property.
     * 
     * @return
     *     possible object is
     *     {@link BigInteger }
     *     
     */
    public BigInteger getPaymentAmount() {
        return paymentAmount;
    }

    /**
     * Sets the value of the paymentAmount property.
     * 
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *     
     */
    public void setPaymentAmount(BigInteger value) {
        this.paymentAmount = value;
    }

    /**
     * Gets the value of the paymentNarrative property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentNarrative() {
        return paymentNarrative;
    }

    /**
     * Sets the value of the paymentNarrative property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentNarrative(String value) {
        this.paymentNarrative = value;
    }

    /**
     * Gets the value of the paymentSystemIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPaymentSystemIdentifier() {
        return paymentSystemIdentifier;
    }

    /**
     * Sets the value of the paymentSystemIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPaymentSystemIdentifier(String value) {
        this.paymentSystemIdentifier = value;
    }

    /**
     * Gets the value of the paymentTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getPaymentTime() {
        return paymentTime;
    }

    /**
     * Sets the value of the paymentTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setPaymentTime(XMLGregorianCalendar value) {
        this.paymentTime = value;
    }

    /**
     * Gets the value of the resources property.
     * 
     * @return
     *     possible object is
     *     {@link CitizenPaymentType.Resources }
     *     
     */
    public CitizenPaymentType.Resources getResources() {
        return resources;
    }

    /**
     * Sets the value of the resources property.
     * 
     * @param value
     *     allowed object is
     *     {@link CitizenPaymentType.Resources }
     *     
     */
    public void setResources(CitizenPaymentType.Resources value) {
        this.resources = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="resource" type="{urn://subscription.api.emp.altarix.ru}CitizenPaymentResource_Type" maxOccurs="unbounded"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "resource"
    })
    public static class Resources {

        @XmlElement(required = true)
        protected List<CitizenPaymentResourceType> resource;

        /**
         * Gets the value of the resource property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the resource property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getResource().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link CitizenPaymentResourceType }
         * 
         * 
         */
        public List<CitizenPaymentResourceType> getResource() {
            if (resource == null) {
                resource = new ArrayList<CitizenPaymentResourceType>();
            }
            return this.resource;
        }

    }

}
