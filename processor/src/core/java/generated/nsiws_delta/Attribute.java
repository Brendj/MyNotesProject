/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package generated.nsiws_delta;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for Attribute complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Attribute">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="value" maxOccurs="unbounded" minOccurs="0">
 *           &lt;complexType>
 *             &lt;simpleContent>
 *               &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
 *                 &lt;attribute name="occurrence" type="{http://www.w3.org/2001/XMLSchema}int" />
 *                 &lt;attribute name="action" type="{http://rstyle.com/nsi/delta}Action" />
 *               &lt;/extension>
 *             &lt;/simpleContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *         &lt;element name="groupValue" type="{http://rstyle.com/nsi/delta}GroupValue" maxOccurs="unbounded" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="type" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Attribute", namespace = "http://rstyle.com/nsi/delta", propOrder = {
    "value",
    "groupValue"
})
public class Attribute {

    @XmlElement(namespace = "")
    protected List<Attribute.Value> value;
    @XmlElement(namespace = "")
    protected List<GroupValue> groupValue;
    @XmlAttribute
    protected String name;
    @XmlAttribute
    protected String type;

    /**
     * Gets the value of the value property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the value property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getValue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Attribute.Value }
     * 
     * 
     */
    public List<Attribute.Value> getValue() {
        if (value == null) {
            value = new ArrayList<Attribute.Value>();
        }
        return this.value;
    }

    /**
     * Gets the value of the groupValue property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the groupValue property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getGroupValue().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link GroupValue }
     * 
     * 
     */
    public List<GroupValue> getGroupValue() {
        if (groupValue == null) {
            groupValue = new ArrayList<GroupValue>();
        }
        return this.groupValue;
    }

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
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;simpleContent>
     *     &lt;extension base="&lt;http://www.w3.org/2001/XMLSchema>string">
     *       &lt;attribute name="occurrence" type="{http://www.w3.org/2001/XMLSchema}int" />
     *       &lt;attribute name="action" type="{http://rstyle.com/nsi/delta}Action" />
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
    public static class Value {

        @XmlValue
        protected String value;
        @XmlAttribute
        protected Integer occurrence;
        @XmlAttribute
        protected Action action;

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
         * Gets the value of the occurrence property.
         * 
         * @return
         *     possible object is
         *     {@link Integer }
         *     
         */
        public Integer getOccurrence() {
            return occurrence;
        }

        /**
         * Sets the value of the occurrence property.
         * 
         * @param value
         *     allowed object is
         *     {@link Integer }
         *     
         */
        public void setOccurrence(Integer value) {
            this.occurrence = value;
        }

        /**
         * Gets the value of the action property.
         * 
         * @return
         *     possible object is
         *     {@link Action }
         *     
         */
        public Action getAction() {
            return action;
        }

        /**
         * Sets the value of the action property.
         * 
         * @param value
         *     allowed object is
         *     {@link Action }
         *     
         */
        public void setAction(Action value) {
            this.action = value;
        }

    }

}
