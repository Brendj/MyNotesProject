
package generated.nsiws2.com.rstyle.nsi.beans;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;


/**
 * <p>Java class for AttributeDef complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="AttributeDef">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="name" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="type" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *         &lt;element name="length" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="localizedName" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="locale" type="{http://www.w3.org/2001/XMLSchema}string" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="lookup_table" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="minOccurs" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="maxOccurs" type="{http://www.w3.org/2001/XMLSchema}int"/>
 *         &lt;element name="attributeDef" type="{http://rstyle.com/nsi/beans}AttributeDef" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="ref_catalog" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;element name="possible_value" type="{http://www.w3.org/2001/XMLSchema}string" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "AttributeDef", propOrder = {
    "name",
    "type",
    "length",
    "localizedName",
    "lookupTable",
    "minOccurs",
    "maxOccurs",
    "attributeDef",
    "refCatalog",
    "possibleValue"
})
public class AttributeDef {

    @XmlElement(required = true)
    protected String name;
    @XmlElement(required = true)
    protected String type;
    protected int length;
    protected List<AttributeDef.LocalizedName> localizedName;
    @XmlElement(name = "lookup_table")
    protected String lookupTable;
    protected int minOccurs;
    protected int maxOccurs;
    protected List<AttributeDef> attributeDef;
    @XmlElement(name = "ref_catalog")
    protected List<String> refCatalog;
    @XmlElement(name = "possible_value")
    protected List<String> possibleValue;

    /**
     * Gets the value of the name property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the value of the name property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setName(String value) {
        this.name = value;
    }

    /**
     * Gets the value of the type property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setType(String value) {
        this.type = value;
    }

    /**
     * Gets the value of the length property.
     * 
     */
    public int getLength() {
        return length;
    }

    /**
     * Sets the value of the length property.
     * 
     */
    public void setLength(int value) {
        this.length = value;
    }

    /**
     * Gets the value of the localizedName property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the localizedName property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getLocalizedName().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AttributeDef.LocalizedName }
     * 
     * 
     */
    public List<AttributeDef.LocalizedName> getLocalizedName() {
        if (localizedName == null) {
            localizedName = new ArrayList<AttributeDef.LocalizedName>();
        }
        return this.localizedName;
    }

    /**
     * Gets the value of the lookupTable property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getLookupTable() {
        return lookupTable;
    }

    /**
     * Sets the value of the lookupTable property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setLookupTable(String value) {
        this.lookupTable = value;
    }

    /**
     * Gets the value of the minOccurs property.
     * 
     */
    public int getMinOccurs() {
        return minOccurs;
    }

    /**
     * Sets the value of the minOccurs property.
     * 
     */
    public void setMinOccurs(int value) {
        this.minOccurs = value;
    }

    /**
     * Gets the value of the maxOccurs property.
     * 
     */
    public int getMaxOccurs() {
        return maxOccurs;
    }

    /**
     * Sets the value of the maxOccurs property.
     * 
     */
    public void setMaxOccurs(int value) {
        this.maxOccurs = value;
    }

    /**
     * Gets the value of the attributeDef property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attributeDef property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttributeDef().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link AttributeDef }
     * 
     * 
     */
    public List<AttributeDef> getAttributeDef() {
        if (attributeDef == null) {
            attributeDef = new ArrayList<AttributeDef>();
        }
        return this.attributeDef;
    }

    /**
     * Gets the value of the refCatalog property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the refCatalog property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getRefCatalog().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getRefCatalog() {
        if (refCatalog == null) {
            refCatalog = new ArrayList<String>();
        }
        return this.refCatalog;
    }

    /**
     * Gets the value of the possibleValue property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the possibleValue property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getPossibleValue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link String }
     * 
     * 
     */
    public List<String> getPossibleValue() {
        if (possibleValue == null) {
            possibleValue = new ArrayList<String>();
        }
        return this.possibleValue;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *       &lt;attribute name="locale" type="{http://www.w3.org/2001/XMLSchema}string" />
     *     &lt;/extension>
     *   &lt;/simpleContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "value"
    })
    public static class LocalizedName {

        @XmlValue
        protected String value;
        @XmlAttribute(name = "locale")
        protected String locale;

        /**
         * Gets the value of the value property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getValue() {
            return value;
        }

        /**
         * Sets the value of the value property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setValue(String value) {
            this.value = value;
        }

        /**
         * Gets the value of the locale property.
         * 
         * @return
         *     possible object is
         *     {@link String }
         *     
         */
        public String getLocale() {
            return locale;
        }

        /**
         * Sets the value of the locale property.
         * 
         * @param value
         *     allowed object is
         *     {@link String }
         *     
         */
        public void setLocale(String value) {
            this.locale = value;
        }

    }

}
