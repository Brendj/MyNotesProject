
package generated.etp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * ����� � ������� ���������
 * 
 * <p>Java class for SetFilesAndStatusesData complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="SetFilesAndStatusesData">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ServiceNumber" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="Statuses" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}ArrayOfRequestStatus" minOccurs="0"/>
 *         &lt;element name="Result" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}RequestResult" minOccurs="0"/>
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
@XmlType(name = "SetFilesAndStatusesData", propOrder = {
    "serviceNumber",
    "statuses",
    "result",
    "documents"
})
public class SetFilesAndStatusesData {

    @XmlElement(name = "ServiceNumber", required = true)
    protected String serviceNumber;
    @XmlElement(name = "Statuses")
    protected ArrayOfRequestStatus statuses;
    @XmlElement(name = "Result")
    protected RequestResult result;
    @XmlElement(name = "Documents")
    protected ArrayOfServiceDocument documents;

    /**
     * Gets the value of the serviceNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getServiceNumber() {
        return serviceNumber;
    }

    /**
     * Sets the value of the serviceNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setServiceNumber(String value) {
        this.serviceNumber = value;
    }

    /**
     * Gets the value of the statuses property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfRequestStatus }
     *     
     */
    public ArrayOfRequestStatus getStatuses() {
        return statuses;
    }

    /**
     * Sets the value of the statuses property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfRequestStatus }
     *     
     */
    public void setStatuses(ArrayOfRequestStatus value) {
        this.statuses = value;
    }

    /**
     * Gets the value of the result property.
     * 
     * @return
     *     possible object is
     *     {@link RequestResult }
     *     
     */
    public RequestResult getResult() {
        return result;
    }

    /**
     * Sets the value of the result property.
     * 
     * @param value
     *     allowed object is
     *     {@link RequestResult }
     *     
     */
    public void setResult(RequestResult value) {
        this.result = value;
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

}
