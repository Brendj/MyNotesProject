/*
 * Copyright (c) 2015. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * <p>Java class for PurchaseWithDetailsElementExt complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="PurchaseWithDetailsElementExt">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="Name" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="Sum" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="Amount" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="Type" type="{http://www.w3.org/2001/XMLSchema}int" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 *
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "PurchaseWithDetailsElementExt")
public class PurchaseWithDetailsElementExt {
    @XmlAttribute(name = "IdOfOrderDetail")
    protected Long idOfOrderDetail;
    @XmlAttribute(name = "Name")
    protected String name;
    @XmlAttribute(name = "Sum")
    protected Long sum;
    @XmlAttribute(name = "Amount")
    protected Long amount;
    @XmlAttribute(name = "Type")
    protected Integer type;
    @XmlAttribute(name = "MenuType")
    protected Integer menuType;
    //это часть полей из menuDetails
    @XmlAttribute(name = "Price")
    protected Long price;
    @XmlAttribute(name = "Calories")
    protected Double calories;
    @XmlAttribute(name = "Output")
    protected String output;
    @XmlAttribute(name = "Protein")
    protected Double protein;
    @XmlAttribute(name = "Fat")
    protected Double fat;
    @XmlAttribute(name = "Carbohydrates")
    protected Double carbohydrates;
    @XmlAttribute(name = "VitB1")
    protected Double vitB1;
    @XmlAttribute(name = "VitB2")
    protected Double vitB2;
    @XmlAttribute(name = "VitPp")
    private Double vitPp;
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
    @XmlAttribute(name = "FRation")
    private Integer fRation;

    @XmlAttribute(name = "LastUpdateDate")
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastUpdateDate;

    public Long getIdOfOrderDetail() {
        return idOfOrderDetail;
    }

    public void setIdOfOrderDetail(Long idOfOrderDetail) {
        this.idOfOrderDetail = idOfOrderDetail;
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
     * Gets the value of the sum property.
     *
     * @return
     *     possible object is
     *     {@link Long }
     *
     */
    public Long getSum() {
        return sum;
    }

    /**
     * Sets the value of the sum property.
     *
     * @param value
     *     allowed object is
     *     {@link Long }
     *
     */
    public void setSum(Long value) {
        this.sum = value;
    }

    /**
     * Gets the value of the amount property.
     *
     * @return
     *     possible object is
     *     {@link Long }
     *
     */
    public Long getAmount() {
        return amount;
    }

    /**
     * Sets the value of the amount property.
     *
     * @param value
     *     allowed object is
     *     {@link Long }
     *
     */
    public void setAmount(Long value) {
        this.amount = value;
    }

    /**
     * Gets the value of the type property.
     *
     * @return
     *     possible object is
     *     {@link Integer }
     *
     */
    public Integer getType() {
        return type;
    }

    /**
     * Sets the value of the type property.
     *
     * @param value
     *     allowed object is
     *     {@link Integer }
     *
     */
    public void setType(Integer value) {
        this.type = value;
    }

    public Integer getMenuType() {
        return menuType;
    }

    public void setMenuType(Integer menuType) {
        this.menuType = menuType;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Double getCalories() {
        return calories;
    }

    public void setCalories(Double calories) {
        this.calories = calories;
    }

    public String getOutput() {
        return output;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public Double getVitB1() {
        return vitB1;
    }

    public void setVitB1(Double vitB1) {
        this.vitB1 = vitB1;
    }

    public Double getVitB2() {
        return vitB2;
    }

    public void setVitB2(Double vitB2) {
        this.vitB2 = vitB2;
    }

    public Double getVitPp() {
        return vitPp;
    }

    public void setVitPp(Double vitPp) {
        this.vitPp = vitPp;
    }

    public Double getVitC() {
        return vitC;
    }

    public void setVitC(Double vitC) {
        this.vitC = vitC;
    }

    public Double getVitA() {
        return vitA;
    }

    public void setVitA(Double vitA) {
        this.vitA = vitA;
    }

    public Double getVitE() {
        return vitE;
    }

    public void setVitE(Double vitE) {
        this.vitE = vitE;
    }

    public Double getMinCa() {
        return minCa;
    }

    public void setMinCa(Double minCa) {
        this.minCa = minCa;
    }

    public Double getMinP() {
        return minP;
    }

    public void setMinP(Double minP) {
        this.minP = minP;
    }

    public Double getMinMg() {
        return minMg;
    }

    public void setMinMg(Double minMg) {
        this.minMg = minMg;
    }

    public Double getMinFe() {
        return minFe;
    }

    public void setMinFe(Double minFe) {
        this.minFe = minFe;
    }

    /**
     * Gets the value of the protein property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public Double getProtein() {
        return protein;
    }

    /**
     * Sets the value of the protein property.
     *
     * @param protein
     *     allowed object is
     *     {@link String }
     *
     */
    public void setProtein(Double protein) {
        this.protein = protein;
    }

    /**
     * Gets the value of the fat property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public Double getFat() {
        return fat;
    }

    /**
     * Sets the value of the fat property.
     *
     * @param fat
     *     allowed object is
     *     {@link String }
     *
     */
    public void setFat(Double fat) {
        this.fat = fat;
    }

    /**
     * Gets the value of the carbohydrates property.
     *
     * @return
     *     possible object is
     *     {@link String }
     *
     */
    public Double getCarbohydrates() {
        return carbohydrates;
    }

    /**
     * Sets the value of the carbohydrates property.
     *
     * @param carbohydrates
     *     allowed object is
     *     {@link String }
     *
     */
    public void setCarbohydrates(Double carbohydrates) {
        this.carbohydrates = carbohydrates;
    }

    public XMLGregorianCalendar getLastUpdateDate() {
        return lastUpdateDate;
    }

    public void setLastUpdateDate(XMLGregorianCalendar lastUpdateDate) {
        this.lastUpdateDate = lastUpdateDate;
    }

    public Integer getfRation() {
        return fRation;
    }

    public void setfRation(Integer fRation) {
        this.fRation = fRation;
    }
}
