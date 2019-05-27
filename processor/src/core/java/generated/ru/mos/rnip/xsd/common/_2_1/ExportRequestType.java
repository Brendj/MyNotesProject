
package generated.ru.mos.rnip.xsd.common._2_1;

import generated.ru.mos.rnip.xsd.services.export_catalog._2_1.ExportCatalogRequest;
import generated.ru.mos.rnip.xsd.services.export_charges._2_1.ExportChargesRequest;
import generated.ru.mos.rnip.xsd.services.export_payments._2_1.ExportPaymentsRequest;
import generated.ru.mos.rnip.xsd.services.export_quittances._2_1.ExportQuittancesRequest;
import generated.ru.mos.rnip.xsd.services.export_refunds._2_1.ExportRefundsRequest;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for ExportRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ExportRequestType">
 *   &lt;complexContent>
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Common/2.1.0}RequestType">
 *       &lt;sequence>
 *         &lt;element name="Paging" type="{http://rnip.mos.ru/xsd/Common/2.1.0}PagingType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="originatorId" type="{http://rnip.mos.ru/xsd/Common/2.1.0}URNType" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ExportRequestType", propOrder = {
    "paging"
})
@XmlSeeAlso({
    ExportRefundsRequest.class,
    ExportQuittancesRequest.class,
    ExportPaymentsRequest.class,
    ExportChargesRequest.class,
    ExportCatalogRequest.class
})
public class ExportRequestType
    extends RequestType
{

    @XmlElement(name = "Paging")
    protected PagingType paging;
    @XmlAttribute(name = "originatorId")
    protected String originatorId;

    /**
     * Gets the value of the paging property.
     * 
     * @return
     *     possible object is
     *     {@link PagingType }
     *     
     */
    public PagingType getPaging() {
        return paging;
    }

    /**
     * Sets the value of the paging property.
     * 
     * @param value
     *     allowed object is
     *     {@link PagingType }
     *     
     */
    public void setPaging(PagingType value) {
        this.paging = value;
    }

    /**
     * Gets the value of the originatorId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getOriginatorId() {
        return originatorId;
    }

    /**
     * Sets the value of the originatorId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setOriginatorId(String value) {
        this.originatorId = value;
    }

}
