
package generated.ru.mos.rnip.xsd.services.export_refunds._2_1;

import generated.ru.mos.rnip.xsd.common._2_1.ExportRequestType;
import generated.ru.mos.rnip.xsd.searchconditions._2_1.RefundsExportConditions;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Common/2.1.1}ExportRequestType">
 *       &lt;sequence>
 *         &lt;element ref="{http://rnip.mos.ru/xsd/SearchConditions/2.1.1}RefundsExportConditions"/>
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
    "refundsExportConditions"
})
@XmlRootElement(name = "ExportRefundsRequest")
public class ExportRefundsRequest
    extends ExportRequestType
{

    @XmlElement(name = "RefundsExportConditions", namespace = "http://rnip.mos.ru/xsd/SearchConditions/2.1.1", required = true)
    protected RefundsExportConditions refundsExportConditions;
    @XmlAttribute(name = "external")
    protected Boolean external;

    /**
     * Условия для предоставления информации о возврате
     * 
     * @return
     *     possible object is
     *     {@link RefundsExportConditions }
     *     
     */
    public RefundsExportConditions getRefundsExportConditions() {
        return refundsExportConditions;
    }

    /**
     * Sets the value of the refundsExportConditions property.
     * 
     * @param value
     *     allowed object is
     *     {@link RefundsExportConditions }
     *     
     */
    public void setRefundsExportConditions(RefundsExportConditions value) {
        this.refundsExportConditions = value;
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
