
package generated.emp_storage;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for SelectEntriesResponse complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SelectEntriesResponse">
 *   &lt;complexContent>
 *     &lt;extension base="{http://emp.mos.ru/schemas/storage/request/common.xsd}BaseResponse">
 *       &lt;sequence>
 *         &lt;element name="result" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;extension base="{http://emp.mos.ru/schemas/storage/entity/entry.xsd}EntryList">
 *                 &lt;sequence>
 *                   &lt;element name="hasMoreEntries" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
@XmlType(name = "SelectEntriesResponse", namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd", propOrder = {
    "result"
})
public class SelectEntriesResponse
    extends BaseResponse
{

    @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd")
    protected SelectEntriesResponse.Result result;

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link SelectEntriesResponse.Result }
     *     
     */
    public SelectEntriesResponse.Result getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link SelectEntriesResponse.Result }
     *     
     */
    public void setResult(SelectEntriesResponse.Result value) {
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
     *     &lt;extension base="{http://emp.mos.ru/schemas/storage/entity/entry.xsd}EntryList">
     *       &lt;sequence>
     *         &lt;element name="hasMoreEntries" type="{http://www.w3.org/2001/XMLSchema}boolean"/>
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
        "hasMoreEntries"
    })
    public static class Result
        extends EntryList
    {

        @XmlElement(namespace = "http://emp.mos.ru/schemas/storage/request/entry.xsd")
        protected boolean hasMoreEntries;

        /**
         * Gets the value of the hasMoreEntries property.
         * 
         */
        public boolean isHasMoreEntries() {
            return hasMoreEntries;
        }

        /**
         * Sets the value of the hasMoreEntries property.
         * 
         */
        public void setHasMoreEntries(boolean value) {
            this.hasMoreEntries = value;
        }

    }

}
