
package generated.etp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;


/**
 * ���� ���������
 * 
 * <p>Java class for CoordinateFile complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="CoordinateFile">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="Id" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="FileIdInStore" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="StoreName" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="FileName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="CmsSignature" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *         &lt;choice>
 *           &lt;element name="FileHash" type="{http://www.w3.org/2001/XMLSchema}base64Binary" minOccurs="0"/>
 *           &lt;element name="FileDigest" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}DigestType" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/choice>
 *         &lt;element name="FileLink" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CoordinateFile", propOrder = {
    "id",
    "fileIdInStore",
    "storeName",
    "fileName",
    "cmsSignature",
    "fileHash",
    "fileDigest",
    "fileLink"
})
public class CoordinateFile {

    @XmlElement(name = "Id", required = true)
    protected String id;
    @XmlElement(name = "FileIdInStore", required = true)
    protected String fileIdInStore;
    @XmlElement(name = "StoreName")
    protected String storeName;
    @XmlElement(name = "FileName", required = true)
    protected String fileName;
    @XmlElement(name = "CmsSignature")
    protected byte[] cmsSignature;
    @XmlElement(name = "FileHash")
    protected byte[] fileHash;
    @XmlElement(name = "FileDigest")
    protected List<DigestType> fileDigest;
    @XmlElement(name = "FileLink")
    protected String fileLink;

    /**
     * Gets the value of the id property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the id property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setId(String value) {
        this.id = value;
    }

    /**
     * Gets the value of the fileIdInStore property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileIdInStore() {
        return fileIdInStore;
    }

    /**
     * Sets the value of the fileIdInStore property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileIdInStore(String value) {
        this.fileIdInStore = value;
    }

    /**
     * Gets the value of the storeName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getStoreName() {
        return storeName;
    }

    /**
     * Sets the value of the storeName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setStoreName(String value) {
        this.storeName = value;
    }

    /**
     * Gets the value of the fileName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * Sets the value of the fileName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileName(String value) {
        this.fileName = value;
    }

    /**
     * Gets the value of the cmsSignature property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getCmsSignature() {
        return cmsSignature;
    }

    /**
     * Sets the value of the cmsSignature property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setCmsSignature(byte[] value) {
        this.cmsSignature = value;
    }

    /**
     * Gets the value of the fileHash property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getFileHash() {
        return fileHash;
    }

    /**
     * Sets the value of the fileHash property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setFileHash(byte[] value) {
        this.fileHash = value;
    }

    /**
     * Gets the value of the fileDigest property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the fileDigest property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getFileDigest().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link DigestType }
     * 
     * 
     */
    public List<DigestType> getFileDigest() {
        if (fileDigest == null) {
            fileDigest = new ArrayList<DigestType>();
        }
        return this.fileDigest;
    }

    /**
     * Gets the value of the fileLink property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getFileLink() {
        return fileLink;
    }

    /**
     * Sets the value of the fileLink property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setFileLink(String value) {
        this.fileLink = value;
    }

}
