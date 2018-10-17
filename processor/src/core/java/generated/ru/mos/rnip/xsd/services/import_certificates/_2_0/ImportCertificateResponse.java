
package generated.ru.mos.rnip.xsd.services.import_certificates._2_0;

import generated.ru.mos.rnip.xsd.common._2_0.ImportProtocolType;
import generated.ru.mos.rnip.xsd.common._2_0.ResponseType;

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
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Common/2.0.1}ResponseType">
 *       &lt;sequence>
 *         &lt;element name="ImportProtocol" type="{http://rnip.mos.ru/xsd/Common/2.0.1}ImportProtocolType" maxOccurs="100"/>
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
    "importProtocol"
})
@XmlRootElement(name = "ImportCertificateResponse")
public class ImportCertificateResponse
    extends ResponseType
{

    @XmlElement(name = "ImportProtocol", required = true)
    protected List<ImportProtocolType> importProtocol;

    /**
     * Gets the value of the importProtocol property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the importProtocol property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getImportProtocol().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link ImportProtocolType }
     * 
     * 
     */
    public List<ImportProtocolType> getImportProtocol() {
        if (importProtocol == null) {
            importProtocol = new ArrayList<ImportProtocolType>();
        }
        return this.importProtocol;
    }

}
