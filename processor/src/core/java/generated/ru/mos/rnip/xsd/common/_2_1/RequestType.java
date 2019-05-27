
package generated.ru.mos.rnip.xsd.common._2_1;

import generated.ru.mos.rnip.xsd.services.forced_acknowledgement._2_1.ForcedAcknowledgementRequest;
import generated.ru.mos.rnip.xsd.services.import_catalog._2_1.ImportCatalogRequest;
import generated.ru.mos.rnip.xsd.services.import_certificates._2_1.ImportCertificateRequest;
import generated.ru.mos.rnip.xsd.services.import_charges._2_1.ImportChargesRequest;
import generated.ru.mos.rnip.xsd.services.import_chargestemplate._2_1.ChargeCreationRequest;
import generated.ru.mos.rnip.xsd.services.import_payments._2_1.ImportPaymentsRequest;
import generated.ru.mos.rnip.xsd.services.import_refunds._2_1.ImportRefundsRequest;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for RequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="Id" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}ID">
 *             &lt;maxLength value="50"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="timestamp" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="senderIdentifier" use="required" type="{http://rnip.mos.ru/xsd/Common/2.1.0}URNType" />
 *       &lt;attribute name="senderRole">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;maxLength value="10"/>
 *             &lt;pattern value="\w{1,10}"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RequestType")
@XmlSeeAlso({
    ImportRefundsRequest.class,
    ImportPaymentsRequest.class,
    ImportChargesRequest.class,
    ImportCertificateRequest.class,
    ImportCatalogRequest.class,
    ForcedAcknowledgementRequest.class,
    ExportRequestType.class,
    ChargeCreationRequest.class
})
public class RequestType {

    @XmlAttribute(name = "Id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    protected String id;
    @XmlAttribute(name = "timestamp", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timestamp;
    @XmlAttribute(name = "senderIdentifier", required = true)
    protected String senderIdentifier;
    @XmlAttribute(name = "senderRole")
    protected String senderRole;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the timestamp property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getTimestamp() {
        return timestamp;
    }

    /**
     * Sets the value of the timestamp property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setTimestamp(XMLGregorianCalendar value) {
        this.timestamp = value;
    }

    /**
     * Gets the value of the senderIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSenderIdentifier() {
        return senderIdentifier;
    }

    /**
     * Sets the value of the senderIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSenderIdentifier(String value) {
        this.senderIdentifier = value;
    }

    /**
     * Gets the value of the senderRole property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSenderRole() {
        return senderRole;
    }

    /**
     * Sets the value of the senderRole property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSenderRole(String value) {
        this.senderRole = value;
    }

}
