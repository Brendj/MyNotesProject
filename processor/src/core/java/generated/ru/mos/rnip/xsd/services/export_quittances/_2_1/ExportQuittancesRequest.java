
package generated.ru.mos.rnip.xsd.services.export_quittances._2_1;

import generated.ru.mos.rnip.xsd.common._2_1.ExportRequestType;
import generated.ru.mos.rnip.xsd.searchconditions._2_1.QuittancesExportConditions;

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
 *         &lt;element ref="{http://rnip.mos.ru/xsd/SearchConditions/2.1.1}QuittancesExportConditions"/>
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
    "quittancesExportConditions"
})
@XmlRootElement(name = "ExportQuittancesRequest")
public class ExportQuittancesRequest
    extends ExportRequestType
{

    @XmlElement(name = "QuittancesExportConditions", namespace = "http://rnip.mos.ru/xsd/SearchConditions/2.1.1", required = true)
    protected QuittancesExportConditions quittancesExportConditions;
    @XmlAttribute(name = "external")
    protected Boolean external;

    /**
     * Условия для предоставления информации о результатах квитирования
     * 								
     * 
     * @return
     *     possible object is
     *     {@link QuittancesExportConditions }
     *     
     */
    public QuittancesExportConditions getQuittancesExportConditions() {
        return quittancesExportConditions;
    }

    /**
     * Sets the value of the quittancesExportConditions property.
     * 
     * @param value
     *     allowed object is
     *     {@link QuittancesExportConditions }
     *     
     */
    public void setQuittancesExportConditions(QuittancesExportConditions value) {
        this.quittancesExportConditions = value;
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
