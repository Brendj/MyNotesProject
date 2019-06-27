
package generated.ru.mos.rnip.xsd.services.import_catalog._2_1;

import generated.ru.mos.rnip.xsd.catalog._2_1.ServiceCatalogType;
import generated.ru.mos.rnip.xsd.common._2_1.RequestType;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Common/2.1.1}RequestType">
 *       &lt;choice>
 *         &lt;element name="Changes" type="{http://rnip.mos.ru/xsd/Catalog/2.1.1}ServiceCatalogType"/>
 *         &lt;element name="ServiceCatalog" type="{http://rnip.mos.ru/xsd/Catalog/2.1.1}ServiceCatalogType"/>
 *       &lt;/choice>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "changes",
    "serviceCatalog"
})
@XmlRootElement(name = "ImportCatalogRequest")
public class ImportCatalogRequest
    extends RequestType
{

    @XmlElement(name = "Changes")
    protected ServiceCatalogType changes;
    @XmlElement(name = "ServiceCatalog")
    protected ServiceCatalogType serviceCatalog;

    /**
     * Gets the value of the changes property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceCatalogType }
     *     
     */
    public ServiceCatalogType getChanges() {
        return changes;
    }

    /**
     * Sets the value of the changes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceCatalogType }
     *     
     */
    public void setChanges(ServiceCatalogType value) {
        this.changes = value;
    }

    /**
     * Gets the value of the serviceCatalog property.
     * 
     * @return
     *     possible object is
     *     {@link ServiceCatalogType }
     *     
     */
    public ServiceCatalogType getServiceCatalog() {
        return serviceCatalog;
    }

    /**
     * Sets the value of the serviceCatalog property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServiceCatalogType }
     *     
     */
    public void setServiceCatalog(ServiceCatalogType value) {
        this.serviceCatalog = value;
    }

}
