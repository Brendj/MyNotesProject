package generated.registry.manual_synch;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import java.util.List;

/**
 * <p>Java class for registerClients complex type.
 * <p/>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p/>
 * <pre>
 * &lt;complexType name="registryChangeItemV2">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="list" type="{http://ru.axetta.ecafe}registryChangeItemParam" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "registryChangeItemV2", propOrder = {
        "list"})
public class RegistryChangeItemV2 {

    public List<RegistryChangeItemParam> list;

    /**
     * Gets the value of the registryChangeItemParamList property.
     *
     * @return
     *     possible object is
     *     {@link RegistryChangeItemParam }
     *
     */
    public List<RegistryChangeItemParam> getList() {
        return list;
    }

    /**
     * Sets the value of the orgId property.
     *
     * @param list
     *     allowed object is
     *     {@link RegistryChangeItemParam }
     *
     */
    public void setList(List<RegistryChangeItemParam> list) {
        this.list = list;
    }
}
