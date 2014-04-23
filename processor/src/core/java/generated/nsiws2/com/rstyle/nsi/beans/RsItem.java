
package generated.nsiws2.com.rstyle.nsi.beans;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for RsItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="RsItem">
 *   &lt;complexContent>
 *     &lt;extension base="{http://rstyle.com/nsi/beans}RsEntry">
 *       &lt;sequence>
 *         &lt;element name="containerName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RsItem", propOrder = {
    "containerName"
})
public class RsItem
    extends RsEntry
{

    protected String containerName;

    /**
     * Gets the value of the containerName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getContainerName() {
        return containerName;
    }

    /**
     * Sets the value of the containerName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContainerName(String value) {
        this.containerName = value;
    }

}
