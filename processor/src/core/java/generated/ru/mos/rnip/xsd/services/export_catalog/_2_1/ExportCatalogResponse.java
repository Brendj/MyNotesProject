
package generated.ru.mos.rnip.xsd.services.export_catalog._2_1;

import generated.ru.mos.rnip.xsd.catalog._2_1.ErrorType;
import generated.ru.mos.rnip.xsd.catalog._2_1.ServiceType;
import generated.ru.mos.rnip.xsd.common._2_1.ResponseType;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Common/2.1.0}ResponseType">
 *       &lt;sequence>
 *         &lt;element name="Service" type="{http://rnip.mos.ru/xsd/Catalog/2.1.0}ServiceType" maxOccurs="100" minOccurs="0"/>
 *         &lt;element name="Error" type="{http://rnip.mos.ru/xsd/Catalog/2.1.0}ErrorType" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="hasMore" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *       &lt;attribute name="revisionDate" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "service",
    "error"
})
@XmlRootElement(name = "ExportCatalogResponse")
public class ExportCatalogResponse
    extends ResponseType
{

    @XmlElement(name = "Service")
    protected List<ServiceType> service;
    @XmlElement(name = "Error")
    protected ErrorType error;
    @XmlAttribute(name = "hasMore", required = true)
    protected boolean hasMore;
    @XmlAttribute(name = "revisionDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar revisionDate;

    /**
     * Gets the value of the service property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the service property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getService().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ServiceType }
     * 
     * 
     */
    public List<ServiceType> getService() {
        if (service == null) {
            service = new ArrayList<ServiceType>();
        }
        return this.service;
    }

    /**
     * Gets the value of the error property.
     * 
     * @return
     *     possible object is
     *     {@link ErrorType }
     *     
     */
    public ErrorType getError() {
        return error;
    }

    /**
     * Sets the value of the error property.
     * 
     * @param value
     *     allowed object is
     *     {@link ErrorType }
     *     
     */
    public void setError(ErrorType value) {
        this.error = value;
    }

    /**
     * Gets the value of the hasMore property.
     * 
     */
    public boolean isHasMore() {
        return hasMore;
    }

    /**
     * Sets the value of the hasMore property.
     * 
     */
    public void setHasMore(boolean value) {
        this.hasMore = value;
    }

    /**
     * Gets the value of the revisionDate property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getRevisionDate() {
        return revisionDate;
    }

    /**
     * Sets the value of the revisionDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setRevisionDate(XMLGregorianCalendar value) {
        this.revisionDate = value;
    }

}
