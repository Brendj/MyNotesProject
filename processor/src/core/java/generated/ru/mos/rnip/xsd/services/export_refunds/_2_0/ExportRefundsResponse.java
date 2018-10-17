
package generated.ru.mos.rnip.xsd.services.export_refunds._2_0;

import generated.ru.mos.rnip.xsd.common._2_0.ResponseType;
import generated.ru.mos.rnip.xsd.refund._2_0.RefundType;

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
 *         &lt;element name="Refund" type="{http://rnip.mos.ru/xsd/Refund/2.0.1}RefundType" maxOccurs="100"/>
 *       &lt;/sequence>
 *       &lt;attribute name="hasMore" use="required" type="{http://www.w3.org/2001/XMLSchema}boolean" />
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "refund"
})
@XmlRootElement(name = "ExportRefundsResponse")
public class ExportRefundsResponse
    extends ResponseType
{

    @XmlElement(name = "Refund", required = true)
    protected List<RefundType> refund;
    @XmlAttribute(name = "hasMore", required = true)
    protected boolean hasMore;

    /**
     * Gets the value of the refund property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the refund property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRefund().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link RefundType }
     * 
     * 
     */
    public List<RefundType> getRefund() {
        if (refund == null) {
            refund = new ArrayList<RefundType>();
        }
        return this.refund;
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

}
