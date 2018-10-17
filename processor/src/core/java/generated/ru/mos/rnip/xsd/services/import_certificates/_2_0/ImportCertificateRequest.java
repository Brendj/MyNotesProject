
package generated.ru.mos.rnip.xsd.services.import_certificates._2_0;

import generated.ru.mos.rnip.xsd.common._2_0.ImportCertificateEntryType;
import generated.ru.mos.rnip.xsd.common._2_0.RequestType;

import javax.xml.bind.annotation.*;
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
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Common/2.0.1}RequestType">
 *       &lt;sequence>
 *         &lt;element name="RequestEntry" type="{http://rnip.mos.ru/xsd/Common/2.0.1}ImportCertificateEntryType" maxOccurs="100"/>
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
    "requestEntry"
})
@XmlRootElement(name = "ImportCertificateRequest")
public class ImportCertificateRequest
    extends RequestType
{

    @XmlElement(name = "RequestEntry", required = true)
    protected List<ImportCertificateEntryType> requestEntry;

    /**
     * Gets the value of the requestEntry property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the requestEntry property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRequestEntry().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ImportCertificateEntryType }
     * 
     * 
     */
    public List<ImportCertificateEntryType> getRequestEntry() {
        if (requestEntry == null) {
            requestEntry = new ArrayList<ImportCertificateEntryType>();
        }
        return this.requestEntry;
    }

}
