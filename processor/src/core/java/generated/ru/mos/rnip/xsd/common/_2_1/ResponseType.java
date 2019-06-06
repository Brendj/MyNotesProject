
package generated.ru.mos.rnip.xsd.common._2_1;

import generated.ru.mos.rnip.xsd.services.export_catalog._2_1.ExportCatalogResponse;
import generated.ru.mos.rnip.xsd.services.export_charges._2_1.ExportChargesResponse;
import generated.ru.mos.rnip.xsd.services.export_payments._2_1.ExportPaymentsResponse;
import generated.ru.mos.rnip.xsd.services.export_quittances._2_1.ExportQuittancesResponse;
import generated.ru.mos.rnip.xsd.services.export_refunds._2_1.ExportRefundsResponse;
import generated.ru.mos.rnip.xsd.services.forced_acknowledgement._2_1.ForcedAcknowledgementResponse;
import generated.ru.mos.rnip.xsd.services.import_catalog._2_1.ImportCatalogResponse;
import generated.ru.mos.rnip.xsd.services.import_certificates._2_1.ImportCertificateResponse;
import generated.ru.mos.rnip.xsd.services.import_chargestemplate._2_1.ChargeCreationResponse;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * Основные параметры ответа на запрос
 * 
 * <p>Java class for ResponseType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ResponseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="Id" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}ID">
 *             &lt;maxLength value="50"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="RqId" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;maxLength value="50"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="recipientIdentifier" use="required" type="{http://rnip.mos.ru/xsd/Common/2.1.1}URNType" />
 *       &lt;attribute name="timestamp" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ResponseType")
@XmlSeeAlso({
    ImportPackageResponseType.class,
    ImportCertificateResponse.class,
    ImportCatalogResponse.class,
    ForcedAcknowledgementResponse.class,
    ExportRefundsResponse.class,
    ExportQuittancesResponse.class,
    ExportPaymentsResponse.class,
    ExportChargesResponse.class,
    ExportCatalogResponse.class,
    ChargeCreationResponse.class
})
public class ResponseType {

    @XmlAttribute(name = "Id", required = true)
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlID
    protected String id;
    @XmlAttribute(name = "RqId", required = true)
    protected String rqId;
    @XmlAttribute(name = "recipientIdentifier", required = true)
    protected String recipientIdentifier;
    @XmlAttribute(name = "timestamp", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar timestamp;

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
     * Gets the value of the rqId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRqId() {
        return rqId;
    }

    /**
     * Sets the value of the rqId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRqId(String value) {
        this.rqId = value;
    }

    /**
     * Gets the value of the recipientIdentifier property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRecipientIdentifier() {
        return recipientIdentifier;
    }

    /**
     * Sets the value of the recipientIdentifier property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRecipientIdentifier(String value) {
        this.recipientIdentifier = value;
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

}
