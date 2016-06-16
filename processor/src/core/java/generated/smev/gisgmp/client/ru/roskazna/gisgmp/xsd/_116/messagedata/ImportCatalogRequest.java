
package generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.messagedata;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;
import generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.catalog.ServiceCatalogType;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;choice>
 *         &lt;element name="Changes" type="{http://roskazna.ru/gisgmp/xsd/116/Catalog}ServiceCatalog_Type"/>
 *         &lt;element name="ServiceCatalog" type="{http://roskazna.ru/gisgmp/xsd/116/Catalog}ServiceCatalog_Type"/>
 *       &lt;/choice>
 *     &lt;/restriction>
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
public class ImportCatalogRequest {

    @XmlElementRef(name = "Changes", namespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData", type = JAXBElement.class)
    protected JAXBElement<ServiceCatalogType> changes;
    @XmlElement(name = "ServiceCatalog", namespace = "http://roskazna.ru/gisgmp/xsd/116/MessageData")
    protected ServiceCatalogType serviceCatalog;

    /**
     * Gets the value of the changes property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link ServiceCatalogType }{@code >}
     *     
     */
    public JAXBElement<ServiceCatalogType> getChanges() {
        return changes;
    }

    /**
     * Sets the value of the changes property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link ServiceCatalogType }{@code >}
     *     
     */
    public void setChanges(JAXBElement<ServiceCatalogType> value) {
        this.changes = ((JAXBElement<ServiceCatalogType> ) value);
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
