
package generated.ru.mos.rnip.xsd.services.export_catalog._2_1;

import generated.ru.mos.rnip.xsd.common._2_1.ExportRequestType;
import generated.ru.mos.rnip.xsd.searchconditions._2_1.CatalogExportConditions;

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
 *         &lt;element ref="{http://rnip.mos.ru/xsd/SearchConditions/2.1.1}CatalogExportConditions"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "catalogExportConditions"
})
@XmlRootElement(name = "ExportCatalogRequest")
public class ExportCatalogRequest
    extends ExportRequestType
{

    @XmlElement(name = "CatalogExportConditions", namespace = "http://rnip.mos.ru/xsd/SearchConditions/2.1.1", required = true)
    protected CatalogExportConditions catalogExportConditions;

    /**
     * Условия для предоставления информации о каталоге
     * 
     * @return
     *     possible object is
     *     {@link CatalogExportConditions }
     *     
     */
    public CatalogExportConditions getCatalogExportConditions() {
        return catalogExportConditions;
    }

    /**
     * Sets the value of the catalogExportConditions property.
     * 
     * @param value
     *     allowed object is
     *     {@link CatalogExportConditions }
     *     
     */
    public void setCatalogExportConditions(CatalogExportConditions value) {
        this.catalogExportConditions = value;
    }

}
