package generated.ru.mos.rnip.xsd.services.export_charges._2_4;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for OffenseType complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="OffenseType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="offenseDate" use="required" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="offensePlace" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;maxLength value="255"/>
 *             &lt;pattern value="0"/>
 *             &lt;pattern value="\S+([\S\s]*\S+)*"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="legalAct" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;maxLength value="255"/>
 *             &lt;pattern value="0"/>
 *             &lt;pattern value="\S+([\S\s]*\S+)*"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="digitalLink" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;maxLength value="2000"/>
 *             &lt;pattern value="0"/>
 *             &lt;pattern value="\S+([\S\s]*\S+)*"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="departmentName" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;maxLength value="255"/>
 *             &lt;pattern value="0"/>
 *             &lt;pattern value="\S+([\S\s]*\S+)*"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "OffenseType", namespace = "http://rnip.mos.ru/xsd/Charge/2.4.0")
public class OffenseType {

    @XmlAttribute(name = "offenseDate", required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar offenseDate;
    @XmlAttribute(name = "offensePlace", required = true)
    protected String offensePlace;
    @XmlAttribute(name = "legalAct", required = true)
    protected String legalAct;
    @XmlAttribute(name = "digitalLink", required = true)
    protected String digitalLink;
    @XmlAttribute(name = "departmentName", required = true)
    protected String departmentName;

    /**
     * Gets the value of the offenseDate property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getOffenseDate() {
        return offenseDate;
    }

    /**
     * Sets the value of the offenseDate property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setOffenseDate(XMLGregorianCalendar value) {
        this.offenseDate = value;
    }

    /**
     * Gets the value of the offensePlace property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getOffensePlace() {
        return offensePlace;
    }

    /**
     * Sets the value of the offensePlace property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setOffensePlace(String value) {
        this.offensePlace = value;
    }

    /**
     * Gets the value of the legalAct property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getLegalAct() {
        return legalAct;
    }

    /**
     * Sets the value of the legalAct property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setLegalAct(String value) {
        this.legalAct = value;
    }

    /**
     * Gets the value of the digitalLink property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDigitalLink() {
        return digitalLink;
    }

    /**
     * Sets the value of the digitalLink property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDigitalLink(String value) {
        this.digitalLink = value;
    }

    /**
     * Gets the value of the departmentName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getDepartmentName() {
        return departmentName;
    }

    /**
     * Sets the value of the departmentName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setDepartmentName(String value) {
        this.departmentName = value;
    }

}