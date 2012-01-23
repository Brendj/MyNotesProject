
package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for MenuItemExt complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="MenuItemExt">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="Group" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Price" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="Calories" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="VitB1" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="VitC" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="VitA" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="VitE" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="MinCa" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="MinP" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="MinMg" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="MinFe" type="{http://www.w3.org/2001/XMLSchema}double" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "MenuItemExt")
public class MenuItemExt {

    @XmlAttribute(name = "Group")
    protected String group;
    @XmlAttribute(name = "Name")
    protected String name;
    @XmlAttribute(name = "Price")
    protected Long price;
    @XmlAttribute(name = "Calories")
    protected Double calories;
    @XmlAttribute(name = "VitB1")
    protected Double vitB1;
    @XmlAttribute(name = "VitC")
    protected Double vitC;
    @XmlAttribute(name = "VitA")
    protected Double vitA;
    @XmlAttribute(name = "VitE")
    protected Double vitE;
    @XmlAttribute(name = "MinCa")
    protected Double minCa;
    @XmlAttribute(name = "MinP")
    protected Double minP;
    @XmlAttribute(name = "MinMg")
    protected Double minMg;
    @XmlAttribute(name = "MinFe")
    protected Double minFe;

    /**
     * Gets the value of the group property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getGroup() {
        return group;
    }

    /**
     * Sets the value of the group property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setGroup(String value) {
        this.group = value;
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
     * Gets the value of the price property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getPrice() {
        return price;
    }

    /**
     * Sets the value of the price property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setPrice(Long value) {
        this.price = value;
    }

    /**
     * Gets the value of the calories property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getCalories() {
        return calories;
    }

    /**
     * Sets the value of the calories property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setCalories(Double value) {
        this.calories = value;
    }

    /**
     * Gets the value of the vitB1 property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getVitB1() {
        return vitB1;
    }

    /**
     * Sets the value of the vitB1 property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setVitB1(Double value) {
        this.vitB1 = value;
    }

    /**
     * Gets the value of the vitC property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getVitC() {
        return vitC;
    }

    /**
     * Sets the value of the vitC property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setVitC(Double value) {
        this.vitC = value;
    }

    /**
     * Gets the value of the vitA property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getVitA() {
        return vitA;
    }

    /**
     * Sets the value of the vitA property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setVitA(Double value) {
        this.vitA = value;
    }

    /**
     * Gets the value of the vitE property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getVitE() {
        return vitE;
    }

    /**
     * Sets the value of the vitE property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setVitE(Double value) {
        this.vitE = value;
    }

    /**
     * Gets the value of the minCa property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getMinCa() {
        return minCa;
    }

    /**
     * Sets the value of the minCa property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMinCa(Double value) {
        this.minCa = value;
    }

    /**
     * Gets the value of the minP property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getMinP() {
        return minP;
    }

    /**
     * Sets the value of the minP property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMinP(Double value) {
        this.minP = value;
    }

    /**
     * Gets the value of the minMg property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getMinMg() {
        return minMg;
    }

    /**
     * Sets the value of the minMg property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMinMg(Double value) {
        this.minMg = value;
    }

    /**
     * Gets the value of the minFe property.
     * 
     * @return
     *     possible object is
     *     {@link Double }
     *     
     */
    public Double getMinFe() {
        return minFe;
    }

    /**
     * Sets the value of the minFe property.
     * 
     * @param value
     *     allowed object is
     *     {@link Double }
     *     
     */
    public void setMinFe(Double value) {
        this.minFe = value;
    }

}
