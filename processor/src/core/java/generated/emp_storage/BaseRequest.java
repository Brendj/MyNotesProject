
package generated.emp_storage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Основная структура для всех запросов
 * 
 * <p>Java class for BaseRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BaseRequest">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="token" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BaseRequest", namespace = "http://emp.mos.ru/schemas/storage/request/common.xsd", propOrder = {
    "token"
})
@XmlSeeAlso({
    UpdateEntriesRequest.class,
    DeleteEntriesRequest.class,
    ReceiveDataChangesRequest.class,
    SelectEntriesRequest.class,
    AddEntriesRequest.class,
    AddAttributeToIndexRequest.class,
    RemoveAttributeFromIndexRequest.class,
    GetCatalogRequest.class,
    SelectCatalogsRequest.class,
    UpdateCatalogRequest.class,
    AddAttributeRequest.class,
    DeleteAttributeRequest.class,
    DeleteIndexRequest.class,
    AddIndexRequest.class,
    DeleteCatalogRequest.class,
    AddCatalogRequest.class
})
public class BaseRequest {

    @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/request/common.xsd", required = true)
    protected String token;

    /**
     * Gets the value of the token property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getToken() {
        return token;
    }

    /**
     * Sets the value of the token property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setToken(String value) {
        this.token = value;
    }

}
