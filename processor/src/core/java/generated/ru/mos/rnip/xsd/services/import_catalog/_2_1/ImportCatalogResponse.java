
package generated.ru.mos.rnip.xsd.services.import_catalog._2_1;

import generated.ru.mos.rnip.xsd.common._2_1.ResponseType;
import generated.ru.mos.rnip.xsd.common._2_1.SingleImportProtocolType;

import javax.xml.bind.annotation.*;


/**
 * <p>Java class for anonymous complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;extension base="{http://rnip.mos.ru/xsd/Common/2.1.1}ResponseType">
 *       &lt;sequence>
 *         &lt;element name="ImportProtocol" type="{http://rnip.mos.ru/xsd/Common/2.1.1}SingleImportProtocolType"/>
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
@XmlRootElement(name = "ImportCatalogResponse")
public class ImportCatalogResponse
    extends ResponseType
{

    @XmlElement(name = "ImportProtocol", required = true)
    protected SingleImportProtocolType importProtocol;

    /**
     * Gets the value of the importProtocol property.
     * 
     * @return
     *     possible object is
     *     {@link SingleImportProtocolType }
     *     
     */
    public SingleImportProtocolType getImportProtocol() {
        return importProtocol;
    }

    /**
     * Sets the value of the importProtocol property.
     * 
     * @param value
     *     allowed object is
     *     {@link SingleImportProtocolType }
     *     
     */
    public void setImportProtocol(SingleImportProtocolType value) {
        this.importProtocol = value;
    }

}
