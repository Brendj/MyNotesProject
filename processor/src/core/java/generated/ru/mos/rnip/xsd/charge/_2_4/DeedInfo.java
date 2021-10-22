/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package generated.ru.mos.rnip.xsd.charge._2_4;

import java.math.BigInteger;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for anonymous complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="IDType" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}integer">
 *             &lt;enumeration value="1"/>
 *             &lt;enumeration value="3"/>
 *             &lt;enumeration value="4"/>
 *             &lt;enumeration value="5"/>
 *             &lt;enumeration value="7"/>
 *             &lt;enumeration value="10"/>
 *             &lt;enumeration value="11"/>
 *             &lt;enumeration value="13"/>
 *             &lt;enumeration value="16"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="idDocNo" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;minLength value="1"/>
 *             &lt;maxLength value="25"/>
 *             &lt;pattern value="\S+([\S\s]*\S+)*"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="idDocDate" use="required" type="{http://www.w3.org/2001/XMLSchema}date" />
 *       &lt;attribute name="subjCode" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;pattern value="\d{7}"/>
 *             &lt;length value="7"/>
 *           &lt;/restriction>
 *         &lt;/simpleType>
 *       &lt;/attribute>
 *       &lt;attribute name="subjName" use="required">
 *         &lt;simpleType>
 *           &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *             &lt;minLength value="1"/>
 *             &lt;maxLength value="1000"/>
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
@XmlType(name = "")
@XmlRootElement(name = "DeedInfo")
public class DeedInfo {

    @XmlAttribute(name = "IDType", required = true)
    protected BigInteger idType;
    @XmlAttribute(name = "idDocNo", required = true)
    protected String idDocNo;
    @XmlAttribute(name = "idDocDate", required = true)
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar idDocDate;
    @XmlAttribute(name = "subjCode", required = true)
    protected String subjCode;
    @XmlAttribute(name = "subjName", required = true)
    protected String subjName;

    /**
     * Gets the value of the idType property.
     *
     * @return
     *     possible object is
     *     {@link BigInteger }
     *
     */
    public BigInteger getIDType() {
        return idType;
    }

    /**
     * Sets the value of the idType property.
     *
     * @param value
     *     allowed object is
     *     {@link BigInteger }
     *
     */
    public void setIDType(BigInteger value) {
        this.idType = value;
    }

    /**
     * Gets the value of the idDocNo property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getIdDocNo() {
        return idDocNo;
    }

    /**
     * Sets the value of the idDocNo property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setIdDocNo(String value) {
        this.idDocNo = value;
    }

    /**
     * Gets the value of the idDocDate property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getIdDocDate() {
        return idDocDate;
    }

    /**
     * Sets the value of the idDocDate property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setIdDocDate(XMLGregorianCalendar value) {
        this.idDocDate = value;
    }

    /**
     * Gets the value of the subjCode property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSubjCode() {
        return subjCode;
    }

    /**
     * Sets the value of the subjCode property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSubjCode(String value) {
        this.subjCode = value;
    }

    /**
     * Gets the value of the subjName property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getSubjName() {
        return subjName;
    }

    /**
     * Sets the value of the subjName property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setSubjName(String value) {
        this.subjName = value;
    }

}
