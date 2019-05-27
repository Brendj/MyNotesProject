
package generated.ru.mos.rnip.xsd.services.export_payments._2_1;

import generated.ru.mos.rnip.xsd.common._2_1.ExportRequestType;
import generated.ru.mos.rnip.xsd.searchconditions._2_1.PaymentsExportConditions;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Common/2.1.0}ExportRequestType">
 *       &lt;sequence>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/SearchConditions/2.1.0}PaymentsExportConditions"/>
 *       &lt;/sequence>
 *       &lt;attribute name="external" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "paymentsExportConditions"
})
@XmlRootElement(name = "ExportPaymentsRequest")
public class ExportPaymentsRequest
    extends ExportRequestType
{

    @XmlElement(name = "PaymentsExportConditions", namespace = "http://rnip.mos.ru/xsd/SearchConditions/2.1.0", required = true)
    protected PaymentsExportConditions paymentsExportConditions;
    @XmlAttribute(name = "external")
    protected Boolean external;

    /**
     * Условия для предоставления информации об уплате
     * 
     * @return
     *     possible object is
     *     {@link PaymentsExportConditions }
     *     
     */
    public PaymentsExportConditions getPaymentsExportConditions() {
        return paymentsExportConditions;
    }

    /**
     * Sets the value of the paymentsExportConditions property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentsExportConditions }
     *     
     */
    public void setPaymentsExportConditions(PaymentsExportConditions value) {
        this.paymentsExportConditions = value;
    }

    /**
     * Gets the value of the external property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isExternal() {
        return external;
    }

    /**
     * Sets the value of the external property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setExternal(Boolean value) {
        this.external = value;
    }

}
