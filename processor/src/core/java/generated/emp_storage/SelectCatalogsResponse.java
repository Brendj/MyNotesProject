
package generated.emp_storage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SelectCatalogsResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SelectCatalogsResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://emp.mos.ru/schemas/storage/request/common.xsd}BaseResponse">
 *       &lt;sequence>
 *         &lt;element name="result" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://emp.mos.ru/schemas/storage/entity/catalog.xsd}CatalogList">
 *                 &lt;sequence>
 *                   &lt;element name="hasMoreCatalogs" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
 *                 &lt;/sequence>
 *               &lt;/extension>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "SelectCatalogsResponse", propOrder = {
    "result"
})
public class SelectCatalogsResponse
    extends BaseResponse
{

    protected SelectCatalogsResponse.Result result;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link SelectCatalogsResponse.Result }
     *     
     */
    public SelectCatalogsResponse.Result getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link SelectCatalogsResponse.Result }
     *     
     */
    public void setResult(SelectCatalogsResponse.Result value) {
        this.result = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;extension base="{http://emp.mos.ru/schemas/storage/entity/catalog.xsd}CatalogList">
     *       &lt;sequence>
     *         &lt;element name="hasMoreCatalogs" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
        "hasMoreCatalogs"
    })
    public static class Result
        extends CatalogList
    {

        protected boolean hasMoreCatalogs;

        /**
         * Gets the value of the hasMoreCatalogs property.
         * 
         */
        public boolean isHasMoreCatalogs() {
            return hasMoreCatalogs;
        }

        /**
         * Sets the value of the hasMoreCatalogs property.
         * 
         */
        public void setHasMoreCatalogs(boolean value) {
            this.hasMoreCatalogs = value;
        }

    }

}
