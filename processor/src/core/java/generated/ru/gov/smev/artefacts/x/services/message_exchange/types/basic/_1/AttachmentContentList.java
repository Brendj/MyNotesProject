
package generated.ru.gov.smev.artefacts.x.services.message_exchange.types.basic._1;

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
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="AttachmentContent" type="{urn://x-artefacts-smev-gov-ru/services/message-exchange/types/basic/1.2}AttachmentContentType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "attachmentContent"
})
@XmlRootElement(name = "AttachmentContentList")
public class AttachmentContentList {

    @XmlElement(name = "AttachmentContent", required = true)
    protected List<AttachmentContentType> attachmentContent;

    /**
     * Gets the value of the attachmentContent property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attachmentContent property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttachmentContent().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AttachmentContentType }
     * 
     * 
     */
    public List<AttachmentContentType> getAttachmentContent() {
        if (attachmentContent == null) {
            attachmentContent = new ArrayList<AttachmentContentType>();
        }
        return this.attachmentContent;
    }

}
