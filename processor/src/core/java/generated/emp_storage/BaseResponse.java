
package generated.emp_storage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * Основная структура для всех ответов
 * 
 * <p>Java class for BaseResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BaseResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="errorCode" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="errorMessage" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BaseResponse", namespace = "http://emp.mos.ru/schemas/storage/request/common.xsd", propOrder = {
    "errorCode",
    "errorMessage"
})
@XmlSeeAlso({
    SelectEntriesResponse.class,
    ReceiveDataChangesResponse.class,
    UpdateEntriesResponse.class,
    DeleteEntriesResponse.class,
    AddEntriesResponse.class,
    SelectCatalogsResponse.class,
    DeleteCatalogResponse.class,
    UpdateCatalogResponse.class,
    AddCatalogResponse.class,
    AddIndexResponse.class,
    RemoveAttributeFromIndexResponse.class,
    AddAttributeToIndexResponse.class,
    DeleteAttributeResponse.class,
    GetCatalogResponse.class,
    DeleteIndexResponse.class,
    AddAttributeResponse.class
})
public class BaseResponse {

    @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/request/common.xsd")
    protected int errorCode;
    @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/request/common.xsd")
    protected String errorMessage;

    /**
     * Gets the value of the errorCode property.
     * 
     */
    public int getErrorCode() {
        return errorCode;
    }

    /**
     * Sets the value of the errorCode property.
     * 
     */
    public void setErrorCode(int value) {
        this.errorCode = value;
    }

    /**
     * Gets the value of the errorMessage property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getErrorMessage() {
        return errorMessage;
    }

    /**
     * Sets the value of the errorMessage property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setErrorMessage(String value) {
        this.errorMessage = value;
    }

}
