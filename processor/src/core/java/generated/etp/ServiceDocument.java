
/*
 * Copyright (c) 2018. Axetta LLC. All Rights Reserved.
 */

package generated.etp;

import org.w3c.dom.Element;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * ���������
 * 
 * <p>Java class for ServiceDocument complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ServiceDocument">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="DocKind" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}DictionaryItem" minOccurs="0"/>
 *         &lt;element name="DocSubType" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ObjectId" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DocSerie" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DocNumber" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DocDate" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="ValidityPeriod" type="{http://www.w3.org/2001/XMLSchema}dateTime" minOccurs="0"/>
 *         &lt;element name="WhoSign" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="ListCount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="CopyCount" type="{http://www.w3.org/2001/XMLSchema}int" minOccurs="0"/>
 *         &lt;element name="DivisionCode" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *         &lt;element name="DocNotes" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}ArrayOfNote" minOccurs="0"/>
 *         &lt;element name="DocFiles" type="{http://asguf.mos.ru/rkis_gu/coordinate/v6_1/}ArrayOfCoordinateFileReference" minOccurs="0"/>
 *         &lt;element name="CustomAttributes" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;any processContents='lax'/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
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
@XmlType(name = "ServiceDocument", propOrder = {
    "docKind",
    "docSubType",
    "objectId",
    "docSerie",
    "docNumber",
    "docDate",
    "validityPeriod",
    "whoSign",
    "listCount",
    "copyCount",
    "divisionCode",
    "docNotes",
    "docFiles",
    "customAttributes"
})
public class ServiceDocument {

    @XmlElement(name = "DocKind")
    protected DictionaryItem docKind;
    @XmlElement(name = "DocSubType")
    protected String docSubType;
    @XmlElement(name = "ObjectId")
    protected String objectId;
    @XmlElement(name = "DocSerie")
    protected String docSerie;
    @XmlElement(name = "DocNumber")
    protected String docNumber;
    @XmlElementRef(name = "DocDate", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", type = JAXBElement.class)
    protected JAXBElement<XMLGregorianCalendar> docDate;
    @XmlElementRef(name = "ValidityPeriod", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", type = JAXBElement.class)
    protected JAXBElement<XMLGregorianCalendar> validityPeriod;
    @XmlElement(name = "WhoSign")
    protected String whoSign;
    @XmlElementRef(name = "ListCount", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", type = JAXBElement.class)
    protected JAXBElement<Integer> listCount;
    @XmlElementRef(name = "CopyCount", namespace = "http://asguf.mos.ru/rkis_gu/coordinate/v6_1/", type = JAXBElement.class)
    protected JAXBElement<Integer> copyCount;
    @XmlElement(name = "DivisionCode")
    protected String divisionCode;
    @XmlElement(name = "DocNotes")
    protected ArrayOfNote docNotes;
    @XmlElement(name = "DocFiles")
    protected ArrayOfCoordinateFileReference docFiles;
    @XmlElement(name = "CustomAttributes")
    protected CustomAttributes customAttributes;

    /**
     * Gets the value of the docKind property.
     * 
     * @return
     *     possible object is
     *     {@link DictionaryItem }
     *     
     */
    public DictionaryItem getDocKind() {
        return docKind;
    }

    /**
     * Sets the value of the docKind property.
     * 
     * @param value
     *     allowed object is
     *     {@link DictionaryItem }
     *     
     */
    public void setDocKind(DictionaryItem value) {
        this.docKind = value;
    }

    /**
     * Gets the value of the docSubType property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocSubType() {
        return docSubType;
    }

    /**
     * Sets the value of the docSubType property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocSubType(String value) {
        this.docSubType = value;
    }

    /**
     * Gets the value of the objectId property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getObjectId() {
        return objectId;
    }

    /**
     * Sets the value of the objectId property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setObjectId(String value) {
        this.objectId = value;
    }

    /**
     * Gets the value of the docSerie property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocSerie() {
        return docSerie;
    }

    /**
     * Sets the value of the docSerie property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocSerie(String value) {
        this.docSerie = value;
    }

    /**
     * Gets the value of the docNumber property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDocNumber() {
        return docNumber;
    }

    /**
     * Sets the value of the docNumber property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDocNumber(String value) {
        this.docNumber = value;
    }

    /**
     * Gets the value of the docDate property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getDocDate() {
        return docDate;
    }

    /**
     * Sets the value of the docDate property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setDocDate(JAXBElement<XMLGregorianCalendar> value) {
        this.docDate = value;
    }

    /**
     * Gets the value of the validityPeriod property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public JAXBElement<XMLGregorianCalendar> getValidityPeriod() {
        return validityPeriod;
    }

    /**
     * Sets the value of the validityPeriod property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link XMLGregorianCalendar }{@code >}
     *     
     */
    public void setValidityPeriod(JAXBElement<XMLGregorianCalendar> value) {
        this.validityPeriod = value;
    }

    /**
     * Gets the value of the whoSign property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getWhoSign() {
        return whoSign;
    }

    /**
     * Sets the value of the whoSign property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setWhoSign(String value) {
        this.whoSign = value;
    }

    /**
     * Gets the value of the listCount property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getListCount() {
        return listCount;
    }

    /**
     * Sets the value of the listCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setListCount(JAXBElement<Integer> value) {
        this.listCount = value;
    }

    /**
     * Gets the value of the copyCount property.
     * 
     * @return
     *     possible object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public JAXBElement<Integer> getCopyCount() {
        return copyCount;
    }

    /**
     * Sets the value of the copyCount property.
     * 
     * @param value
     *     allowed object is
     *     {@link JAXBElement }{@code <}{@link Integer }{@code >}
     *     
     */
    public void setCopyCount(JAXBElement<Integer> value) {
        this.copyCount = value;
    }

    /**
     * Gets the value of the divisionCode property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDivisionCode() {
        return divisionCode;
    }

    /**
     * Sets the value of the divisionCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDivisionCode(String value) {
        this.divisionCode = value;
    }

    /**
     * Gets the value of the docNotes property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfNote }
     *     
     */
    public ArrayOfNote getDocNotes() {
        return docNotes;
    }

    /**
     * Sets the value of the docNotes property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfNote }
     *     
     */
    public void setDocNotes(ArrayOfNote value) {
        this.docNotes = value;
    }

    /**
     * Gets the value of the docFiles property.
     * 
     * @return
     *     possible object is
     *     {@link ArrayOfCoordinateFileReference }
     *     
     */
    public ArrayOfCoordinateFileReference getDocFiles() {
        return docFiles;
    }

    /**
     * Sets the value of the docFiles property.
     * 
     * @param value
     *     allowed object is
     *     {@link ArrayOfCoordinateFileReference }
     *     
     */
    public void setDocFiles(ArrayOfCoordinateFileReference value) {
        this.docFiles = value;
    }

    /**
     * Gets the value of the customAttributes property.
     * 
     * @return
     *     possible object is
     *     {@link CustomAttributes }
     *     
     */
    public CustomAttributes getCustomAttributes() {
        return customAttributes;
    }

    /**
     * Sets the value of the customAttributes property.
     * 
     * @param value
     *     allowed object is
     *     {@link CustomAttributes }
     *     
     */
    public void setCustomAttributes(CustomAttributes value) {
        this.customAttributes = value;
    }


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
     *         &lt;any processContents='lax'/>
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
        "any"
    })
    public static class CustomAttributes {

        @XmlAnyElement(lax = true)
        protected Object any;

        /**
         * Gets the value of the any property.
         * 
         * @return
         *     possible object is
         *     {@link Object }
         *     {@link Element }
         *     
         */
        public Object getAny() {
            return any;
        }

        /**
         * Sets the value of the any property.
         * 
         * @param value
         *     allowed object is
         *     {@link Object }
         *     {@link Element }
         *     
         */
        public void setAny(Object value) {
            this.any = value;
        }

    }

}
