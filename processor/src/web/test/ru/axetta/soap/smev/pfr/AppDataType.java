
package ru.axetta.soap.smev.pfr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for AppDataType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AppDataType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{http://data.service.pfr.socit.ru}Type"/>
 *         &lt;element ref="{http://data.service.pfr.socit.ru}Properties"/>
 *         &lt;element ref="{http://data.service.pfr.socit.ru}FilePFR"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AppDataType", propOrder = {
    "type",
    "properties",
    "filePFR"
})
public class AppDataType {

    @XmlElement(name = "Type", namespace = "http://data.service.pfr.socit.ru", required = true)
    protected Type type;
    @XmlElement(name = "Properties", namespace = "http://data.service.pfr.socit.ru", required = true)
    protected Properties properties;
    @XmlElement(name = "FilePFR", namespace = "http://data.service.pfr.socit.ru", required = true)
    protected byte[] filePFR;

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link Type }
     *     
     */
    public Type getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link Type }
     *     
     */
    public void setType(Type value) {
        this.type = value;
    }

    /**
     * Gets the value of the properties property.
     * 
     * @return
     *     possible object is
     *     {@link Properties }
     *     
     */
    public Properties getProperties() {
        return properties;
    }

    /**
     * Sets the value of the properties property.
     * 
     * @param value
     *     allowed object is
     *     {@link Properties }
     *     
     */
    public void setProperties(Properties value) {
        this.properties = value;
    }

    /**
     * Gets the value of the filePFR property.
     * 
     * @return
     *     possible object is
     *     byte[]
     */
    public byte[] getFilePFR() {
        return filePFR;
    }

    /**
     * Sets the value of the filePFR property.
     * 
     * @param value
     *     allowed object is
     *     byte[]
     */
    public void setFilePFR(byte[] value) {
        this.filePFR = ((byte[]) value);
    }

}
