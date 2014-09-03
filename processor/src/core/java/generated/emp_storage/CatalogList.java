
package generated.emp_storage;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Список каталогов
 * 
 * <p>Java class for CatalogList complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CatalogList">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="catalog" type="{http://emp.mos.ru/schemas/storage/entity/catalog.xsd}Catalog" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CatalogList", namespace = "http://emp.mos.ru/schemas/storage/entity/catalog.xsd", propOrder = {
    "catalog"
})
@XmlSeeAlso({
    generated.emp_storage.SelectCatalogsResponse.Result.class
})
public class CatalogList {

    @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/entity/catalog.xsd", required = true)
    protected List<Catalog> catalog;

    /**
     * Gets the value of the catalog property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the catalog property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getCatalog().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Catalog }
     * 
     * 
     */
    public List<Catalog> getCatalog() {
        if (catalog == null) {
            catalog = new ArrayList<Catalog>();
        }
        return this.catalog;
    }

}
