
/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package generated.smev.gisgmp.client.ru.roskazna.gisgmp.xsd._116.catalog;

import javax.xml.bind.annotation.*;


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
 *         &lt;element ref="{http://roskazna.ru/gisgmp/xsd/116/Catalog}ServiceCatalog" minOccurs="0"/>
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
    "serviceCatalog"
})
@XmlRootElement(name = "CatalogFullInqMsgRs")
public class CatalogFullInqMsgRs {

    @XmlElement(name = "ServiceCatalog")
    protected ServiceCatalogType serviceCatalog;

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
