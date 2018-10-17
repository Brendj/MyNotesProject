
package generated.ru.mos.rnip.xsd.searchconditions._2_0;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for CatalogConditionsType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CatalogConditionsType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ServicesCodesList" type="{http://rnip.mos.ru/xsd/SearchConditions/2.0.1}ServicesConditionsType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="allDateCatalog" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CatalogConditionsType", propOrder = {
    "servicesCodesList"
})
public class CatalogConditionsType {

    @XmlElement(name = "ServicesCodesList")
    protected ServicesConditionsType servicesCodesList;
    @XmlAttribute(name = "allDateCatalog")
    protected Boolean allDateCatalog;

    /**
     * Gets the value of the servicesCodesList property.
     * 
     * @return
     *     possible object is
     *     {@link ServicesConditionsType }
     *     
     */
    public ServicesConditionsType getServicesCodesList() {
        return servicesCodesList;
    }

    /**
     * Sets the value of the servicesCodesList property.
     * 
     * @param value
     *     allowed object is
     *     {@link ServicesConditionsType }
     *     
     */
    public void setServicesCodesList(ServicesConditionsType value) {
        this.servicesCodesList = value;
    }

    /**
     * Gets the value of the allDateCatalog property.
     * 
     * @return
     *     possible object is
     *     {@link Boolean }
     *     
     */
    public Boolean isAllDateCatalog() {
        return allDateCatalog;
    }

    /**
     * Sets the value of the allDateCatalog property.
     * 
     * @param value
     *     allowed object is
     *     {@link Boolean }
     *     
     */
    public void setAllDateCatalog(Boolean value) {
        this.allDateCatalog = value;
    }

}
