
package generated.emp_storage;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetCatalogResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetCatalogResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://emp.mos.ru/schemas/storage/request/common.xsd}BaseResponse">
 *       &lt;sequence>
 *         &lt;element name="result" type="{http://emp.mos.ru/schemas/storage/entity/catalog.xsd}Catalog" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetCatalogResponse", propOrder = {
    "result"
})
public class GetCatalogResponse
    extends BaseResponse
{

    @XmlElementRef(name = "result", namespace = "http://emp.mos.ru/schemas/storage/request/catalog.xsd", type = JAXBElement.class)
    protected JAXBElement<Catalog> result;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Catalog }{@code >}
     *     
     */
    public JAXBElement<Catalog> getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Catalog }{@code >}
     *     
     */
    public void setResult(JAXBElement<Catalog> value) {
        this.result = ((JAXBElement<Catalog> ) value);
    }

}
