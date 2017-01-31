/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package generated.nsiws_delta;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;


/**
 * <p>Java class for Item complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Item">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;sequence>
 *           &lt;element name="attribute" type="{http://rstyle.com/nsi/delta}Attribute" maxOccurs="unbounded" minOccurs="0"/>
 *         &lt;/sequence>
 *       &lt;/sequence>
 *       &lt;attribute name="primaryKey" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="action" type="{http://rstyle.com/nsi/delta}Action" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Item", namespace = "http://rstyle.com/nsi/delta", propOrder = {
    "attribute"
})
public class Item {

    @XmlElement(namespace = "")
    protected List<Attribute> attribute;
    @XmlAttribute(name = "notificationId")
    protected String notificationId;
    @XmlAttribute(name = "GUID")
    protected String guid;
    @XmlAttribute
    protected String primaryKey;
    @XmlAttribute
    protected Action action;

    /**
     * Gets the value of the attribute property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the attribute property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getAttribute().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link Attribute }
     * 
     * 
     */
    public List<Attribute> getAttribute() {
        if (attribute == null) {
            attribute = new ArrayList<Attribute>();
        }
        return this.attribute;
    }

    /**
     * Gets the value of the primaryKey property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getPrimaryKey() {
        return primaryKey;
    }

    /**
     * Gets the value of the guid property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public String getGUID() {
        return guid;
    }

    /**
     * Sets the value of the guid property.
     *
     * @param value
     *     allowed object is
     *     {@link String }
     *
     */
    public void setGUID(String value) {
        this.guid = value;
    }

    /**
     * Sets the value of the primaryKey property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setPrimaryKey(String value) {
        this.primaryKey = value;
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

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    @Override
    public String toString() {
        return "Item{" +
                "attribute=" + attribute +
                ", GUID='" + guid + '\'' +
                ", primaryKey='" + primaryKey + '\'' +
                ", action=" + action +
                '}';
    }
}
