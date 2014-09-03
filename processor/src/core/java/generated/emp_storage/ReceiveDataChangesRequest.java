
package generated.emp_storage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ReceiveDataChangesRequest complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ReceiveDataChangesRequest">
 *   &lt;complexContent>
 *     &lt;extension base="{http://emp.mos.ru/schemas/storage/request/common.xsd}BaseRequest">
 *       &lt;sequence>
 *         &lt;element name="catalogName" type="{http://emp.mos.ru/schemas/storage/entity/catalog.xsd}catalogName"/>
 *         &lt;element name="paging" type="{http://emp.mos.ru/schemas/storage/request/common.xsd}Paging" minOccurs="0"/>
 *         &lt;element name="changeSequence" type="{http://www.w3.org/2001/XMLSchema}long"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ReceiveDataChangesRequest", namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", propOrder = {
    "catalogName",
    "paging",
    "changeSequence"
})
public class ReceiveDataChangesRequest
    extends BaseRequest
{

    @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", required = true)
    protected String catalogName;
    @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd")
    protected Paging paging;
    @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd")
    protected long changeSequence;

    /**
     * Gets the value of the catalogName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getCatalogName() {
        return catalogName;
    }

    /**
     * Sets the value of the catalogName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setCatalogName(String value) {
        this.catalogName = value;
    }

    /**
     * Gets the value of the paging property.
     * 
     * @return
     *     possible object is
     *     {@link Paging }
     *     
     */
    public Paging getPaging() {
        return paging;
    }

    /**
     * Sets the value of the paging property.
     * 
     * @param value
     *     allowed object is
     *     {@link Paging }
     *     
     */
    public void setPaging(Paging value) {
        this.paging = value;
    }

    /**
     * Gets the value of the changeSequence property.
     * 
     */
    public long getChangeSequence() {
        return changeSequence;
    }

    /**
     * Sets the value of the changeSequence property.
     * 
     */
    public void setChangeSequence(long value) {
        this.changeSequence = value;
    }

}
