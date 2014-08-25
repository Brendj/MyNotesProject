package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;


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
 *         &lt;element name="ClientSummary" type="{}ClientSummary" minOccurs="0"/>
 *         &lt;element name="ClientSummaryExt" type="{}ClientSummaryExt" minOccurs="0"/>
 *         &lt;element name="PurchaseList" type="{}PurchaseList" minOccurs="0"/>
 *         &lt;element name="PurchaseListExt" type="{}PurchaseListExt" minOccurs="0"/>
 *         &lt;element name="PaymentList" type="{}PaymentList" minOccurs="0"/>
 *         &lt;element name="MenuList" type="{}MenuList" minOccurs="0"/>
 *         &lt;element name="MenuListExt" type="{}MenuListExt" minOccurs="0"/>
 *         &lt;element name="CardList" type="{}CardList" minOccurs="0"/>
 *         &lt;element name="EnterEventList" type="{}EnterEventList" minOccurs="0"/>
 *         &lt;element name="EnterEventWithRepList" type="{}EnterEventWithRepList" minOccurs="0"/>
 *         &lt;element name="CirculationItemList" type="{}CirculationItemList" minOccurs="0"/>
 *       &lt;/sequence>
 *       &lt;attribute name="IdOfContract" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="ResultCode" type="{http://www.w3.org/2001/XMLSchema}long" />
 *       &lt;attribute name="Description" type="{http://www.w3.org/2001/XMLSchema}string" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = {
    "clientSummary",
    "clientSummaryExt",
    "purchaseList",
    "purchaseListExt",
    "paymentList",
    "menuList",
    "menuListExt",
    "cardList",
    "enterEventList",
    "enterEventWithRepList",
    "circulationItemList",
    "complexDateList",
    "questionaryList"
})
@XmlRootElement(name = "Data")
public class Data {

    @XmlElement(name = "ClientSummary")
    protected ClientSummary clientSummary;
    @XmlElement(name = "ClientSummaryExt")
    protected ClientSummaryExt clientSummaryExt;
    @XmlElement(name = "PurchaseList")
    protected PurchaseList purchaseList;
    @XmlElement(name = "PurchaseListExt")
    protected PurchaseListExt purchaseListExt;
    @XmlElement(name = "PaymentList")
    protected PaymentList paymentList;
    @XmlElement(name = "MenuList")
    protected MenuList menuList;
    @XmlElement(name = "MenuListExt")
    protected MenuListExt menuListExt;
    @XmlElement(name = "ComplexDateList")
    protected ComplexDateList complexDateList;
    @XmlElement(name = "CardList")
    protected CardList cardList;
    @XmlElement(name = "EnterEventList")
    protected EnterEventList enterEventList;
    @XmlElement(name = "EnterEventWithRepList")
    protected EnterEventWithRepList enterEventWithRepList;
    @XmlElement(name = "CirculationItemList")
    protected CirculationItemList circulationItemList;
    @XmlAttribute(name = "IdOfContract")
    protected Long idOfContract;
    @XmlAttribute(name = "ResultCode")
    protected Long resultCode;
    @XmlAttribute(name = "Description")
    protected String description;
    @XmlElement(name = "QuestionaryList")
    protected QuestionaryList questionaryList;


    public ComplexDateList getComplexDateList() {
        return complexDateList;
    }

    public void setComplexDateList(ComplexDateList complexDateList) {
        this.complexDateList = complexDateList;
    }

    /**
     * Gets the value of the clientSummary property.
     * 
     * @return
     *     possible object is
     *     {@link ClientSummary }
     *     
     */
    public ClientSummary getClientSummary() {
        return clientSummary;
    }

    /**
     * Sets the value of the clientSummary property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClientSummary }
     *     
     */
    public void setClientSummary(ClientSummary value) {
        this.clientSummary = value;
    }

    /**
     * Gets the value of the clientSummaryExt property.
     * 
     * @return
     *     possible object is
     *     {@link ClientSummaryExt }
     *     
     */
    public ClientSummaryExt getClientSummaryExt() {
        return clientSummaryExt;
    }

    /**
     * Sets the value of the clientSummaryExt property.
     * 
     * @param value
     *     allowed object is
     *     {@link ClientSummaryExt }
     *     
     */
    public void setClientSummaryExt(ClientSummaryExt value) {
        this.clientSummaryExt = value;
    }

    /**
     * Gets the value of the purchaseList property.
     * 
     * @return
     *     possible object is
     *     {@link PurchaseList }
     *     
     */
    public PurchaseList getPurchaseList() {
        return purchaseList;
    }

    /**
     * Sets the value of the purchaseList property.
     * 
     * @param value
     *     allowed object is
     *     {@link PurchaseList }
     *     
     */
    public void setPurchaseList(PurchaseList value) {
        this.purchaseList = value;
    }

    /**
     * Gets the value of the purchaseListExt property.
     * 
     * @return
     *     possible object is
     *     {@link PurchaseListExt }
     *     
     */
    public PurchaseListExt getPurchaseListExt() {
        return purchaseListExt;
    }

    /**
     * Sets the value of the purchaseListExt property.
     * 
     * @param value
     *     allowed object is
     *     {@link PurchaseListExt }
     *     
     */
    public void setPurchaseListExt(PurchaseListExt value) {
        this.purchaseListExt = value;
    }

    /**
     * Gets the value of the paymentList property.
     * 
     * @return
     *     possible object is
     *     {@link PaymentList }
     *     
     */
    public PaymentList getPaymentList() {
        return paymentList;
    }

    /**
     * Sets the value of the paymentList property.
     * 
     * @param value
     *     allowed object is
     *     {@link PaymentList }
     *     
     */
    public void setPaymentList(PaymentList value) {
        this.paymentList = value;
    }

    /**
     * Gets the value of the menuList property.
     * 
     * @return
     *     possible object is
     *     {@link MenuList }
     *     
     */
    public MenuList getMenuList() {
        return menuList;
    }

    /**
     * Sets the value of the menuList property.
     * 
     * @param value
     *     allowed object is
     *     {@link MenuList }
     *     
     */
    public void setMenuList(MenuList value) {
        this.menuList = value;
    }

    /**
     * Gets the value of the menuListExt property.
     * 
     * @return
     *     possible object is
     *     {@link MenuListExt }
     *     
     */
    public MenuListExt getMenuListExt() {
        return menuListExt;
    }

    /**
     * Sets the value of the menuListExt property.
     * 
     * @param value
     *     allowed object is
     *     {@link MenuListExt }
     *     
     */
    public void setMenuListExt(MenuListExt value) {
        this.menuListExt = value;
    }

    /**
     * Gets the value of the cardList property.
     * 
     * @return
     *     possible object is
     *     {@link CardList }
     *     
     */
    public CardList getCardList() {
        return cardList;
    }

    /**
     * Sets the value of the cardList property.
     * 
     * @param value
     *     allowed object is
     *     {@link CardList }
     *     
     */
    public void setCardList(CardList value) {
        this.cardList = value;
    }

    /**
     * Gets the value of the enterEventList property.
     * 
     * @return
     *     possible object is
     *     {@link EnterEventList }
     *     
     */
    public EnterEventList getEnterEventList() {
        return enterEventList;
    }

    /**
     * Sets the value of the enterEventList property.
     * 
     * @param value
     *     allowed object is
     *     {@link EnterEventList }
     *     
     */
    public void setEnterEventList(EnterEventList value) {
        this.enterEventList = value;
    }

    /**
     * Gets the value of the enterEventWithRepList property.
     *
     * @return
     *     possible object is
     *     {@link EnterEventWithRepList }
     *
     */
    public EnterEventWithRepList getEnterEventWithRepList() {
        return enterEventWithRepList;
    }

    /**
     * Sets the value of the enterEventWithRepList property.
     *
     * @param value
     *     allowed object is
     *     {@link EnterEventWithRepList }
     *
     */
    public void setEnterEventWithRepList(EnterEventWithRepList value) {
        this.enterEventWithRepList = value;
    }

    /**
     * Gets the value of the circulationItemList property.
     * 
     * @return
     *     possible object is
     *     {@link CirculationItemList }
     *     
     */
    public CirculationItemList getCirculationItemList() {
        return circulationItemList;
    }

    /**
     * Sets the value of the circulationItemList property.
     * 
     * @param value
     *     allowed object is
     *     {@link CirculationItemList }
     *     
     */
    public void setCirculationItemList(CirculationItemList value) {
        this.circulationItemList = value;
    }

    /**
     * Gets the value of the idOfContract property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getIdOfContract() {
        return idOfContract;
    }

    /**
     * Sets the value of the idOfContract property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setIdOfContract(Long value) {
        this.idOfContract = value;
    }

    /**
     * Gets the value of the resultCode property.
     * 
     * @return
     *     possible object is
     *     {@link Long }
     *     
     */
    public Long getResultCode() {
        return resultCode;
    }

    /**
     * Sets the value of the resultCode property.
     * 
     * @param value
     *     allowed object is
     *     {@link Long }
     *     
     */
    public void setResultCode(Long value) {
        this.resultCode = value;
    }

    /**
     * Gets the value of the description property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the value of the description property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setDescription(String value) {
        this.description = value;
    }

    public QuestionaryList getQuestionaryList() {
        return questionaryList;
    }

    public void setQuestionaryList(QuestionaryList questionaryList) {
        this.questionaryList = questionaryList;
    }
}
