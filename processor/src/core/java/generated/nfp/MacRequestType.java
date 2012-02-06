
package generated.nfp;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * Описание данных для запроса подписи
 * 
 * <p>Java class for MacRequestType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MacRequestType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="algType" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="key">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}int">
 *               &lt;minInclusive value="0"/>
 *               &lt;maxInclusive value="255"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="contId">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}hexBinary">
 *               &lt;length value="2"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *         &lt;element name="dataForMacSign" minOccurs="0">
 *           &lt;simpleType>
 *             &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *               &lt;maxLength value="255"/>
 *             &lt;/restriction>
 *           &lt;/simpleType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MacRequestType", propOrder = {
    "algType",
    "key",
    "contId",
    "dataForMacSign"
})
public class MacRequestType {

    @XmlElement(required = true)
    protected String algType;
    protected int key;
    @XmlElement(required = true, type = String.class)
    @XmlJavaTypeAdapter(HexBinaryAdapter.class)
    protected byte[] contId;
    protected String dataForMacSign;

    /**
     * Gets the value of the algType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getAlgType() {
        return algType;
    }

    /**
     * Sets the value of the algType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setAlgType(String value) {
        this.algType = value;
    }

    /**
     * Gets the value of the key property.
     * 
     */
    public int getKey() {
        return key;
    }

    /**
     * Sets the value of the key property.
     * 
     */
    public void setKey(int value) {
        this.key = value;
    }

    /**
     * Gets the value of the contId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public byte[] getContId() {
        return contId;
    }

    /**
     * Sets the value of the contId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setContId(byte[] value) {
        this.contId = ((byte[]) value);
    }

    /**
     * Gets the value of the dataForMacSign property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDataForMacSign() {
        return dataForMacSign;
    }

    /**
     * Sets the value of the dataForMacSign property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDataForMacSign(String value) {
        this.dataForMacSign = value;
    }

}
