
package generated.etp;

import javax.xml.bind.annotation.*;


/**
 * ��������� (������� ���)
 * 
 * <p>Java class for BaseDeclarant complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="BaseDeclarant">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Type" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}ContactType"/>
 *         &lt;element name="Documents" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}ArrayOfServiceDocument" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "BaseDeclarant", propOrder = {
    "type",
    "documents"
})
@XmlSeeAlso({
    RequestContact.class,
    RequestAccount.class
})
public abstract class BaseDeclarant {

    @XmlElement(name = "Type", required = true)
    protected ContactType type;
    @XmlElement(name = "Documents")
    protected ArrayOfServiceDocument documents;
    @XmlElement(name = "SsoId")
    protected String SsoId;


    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link ContactType }
     *     
     */
    public ContactType getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link ContactType }
     *     
     */
    public void setType(ContactType value) {
        this.type = value;
    }

    /**
     * Gets the value of the documents property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfServiceDocument }
     *     
     */
    public ArrayOfServiceDocument getDocuments() {
        return documents;
    }

    /**
     * Sets the value of the documents property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfServiceDocument }
     *     
     */
    public void setDocuments(ArrayOfServiceDocument value) {
        this.documents = value;
    }


    public String getSsoId() {
        return SsoId;
    }

    public void setSsoId(String SsoId) {
        this.SsoId = SsoId;
    }
}
