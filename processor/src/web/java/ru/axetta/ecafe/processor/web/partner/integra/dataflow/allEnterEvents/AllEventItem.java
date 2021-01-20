/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow.allEnterEvents;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for EnterEventItem complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="EnterEventItem">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="DateTime" type="{http://www.w3.org/2001/XMLSchema}dateTime" />
 *       &lt;attribute name="Day" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="EnterName" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Direction" type="{http://www.w3.org/2001/XMLSchema}int" />
 *       &lt;attribute name="TemporaryCard" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class AllEventItem {

    @XmlAttribute(name = "DateTime")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar dateTime;
    @XmlAttribute(name = "Day")
    protected Integer day;
    @XmlAttribute(name = "Direction")
    protected Integer direction;
    @XmlAttribute(name = "DirectionText")
    protected String directionText;
    @XmlAttribute(name = "Address")
    protected String address;
    @XmlAttribute(name = "ShortNameInfoService")
    protected String shortNameInfoService;
    @XmlAttribute(name = "ChildPassChecker")
    protected String childPassChecker;
    @XmlAttribute(name = "ChildPassCheckerMethod")
    protected Integer childPassCheckerMethod;

    /**
     * Gets the value of the dateTime property.
     *
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public XMLGregorianCalendar getDateTime() {
        return dateTime;
    }

    /**
     * Sets the value of the dateTime property.
     *
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *
     */
    public void setDateTime(XMLGregorianCalendar value) {
        this.dateTime = value;
    }

    /**
     * Gets the value of the day property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getDay() {
        return day;
    }

    /**
     * Sets the value of the day property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setDay(Integer value) {
        this.day = value;
    }

    /**
     * Gets the value of the direction property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getDirection() {
        return direction;
    }

    /**
     * Sets the value of the direction property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setDirection(Integer value) {
        this.direction = value;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getShortNameInfoService() {
        return shortNameInfoService;
    }

    public void setShortNameInfoService(String shortNameInfoService) {
        this.shortNameInfoService = shortNameInfoService;
    }

    public String getChildPassChecker() {
        return childPassChecker;
    }

    public void setChildPassChecker(String childPassChecker) {
        this.childPassChecker = childPassChecker;
    }

    public Integer getChildPassCheckerMethod() {
        return childPassCheckerMethod;
    }

    public void setChildPassCheckerMethod(Integer childPassCheckerMethod) {
        this.childPassCheckerMethod = childPassCheckerMethod;
    }

    public String getDirectionText() {
        return directionText;
    }

    public void setDirectionText(String directionText) {
        this.directionText = directionText;
    }
}
