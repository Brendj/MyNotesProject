package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListOfGoodsExt", propOrder = {
        "goodsCode", "guid", "deletedState", "orgOwner", "nameOfGood", "fullName",
        "unitsScale", "netWeight", "lifetime", "margin", "createdDate"
})
public class ListOfGoodsExt {

    @XmlAttribute(name = "GoodsCode")
    protected String goodsCode;
    @XmlAttribute(name = "Guid")
    protected String guid;
    @XmlAttribute(name = "DeletedState")
    protected Boolean deletedState;
    @XmlAttribute(name = "OrgOwner")
    protected Long orgOwner;
    @XmlAttribute(name = "NameOfGood")
    protected String nameOfGood;
    @XmlAttribute(name = "FullName")
    protected String fullName;
    @XmlAttribute(name = "UnitsScale")
    protected Integer unitsScale;
    @XmlAttribute(name = "NetWeight")
    protected Long netWeight;
    @XmlAttribute(name = "Lifetime")
    protected Long lifetime;
    @XmlAttribute(name = "Margin")
    protected Long margin;
    @XmlAttribute(name = "CreatedDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar createdDate;

    public String getGoodsCode() {
        return goodsCode;
    }

    public void setGoodsCode(String goodsCode) {
        this.goodsCode = goodsCode;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    public Long getOrgOwner() {
        return orgOwner;
    }

    public void setOrgOwner(Long orgOwner) {
        this.orgOwner = orgOwner;
    }

    public String getNameOfGood() {
        return nameOfGood;
    }

    public void setNameOfGood(String nameOfGood) {
        this.nameOfGood = nameOfGood;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public Integer getUnitsScale() {
        return unitsScale;
    }

    public void setUnitsScale(Integer unitsScale) {
        this.unitsScale = unitsScale;
    }

    public Long getNetWeight() {
        return netWeight;
    }

    public void setNetWeight(Long netWeight) {
        this.netWeight = netWeight;
    }

    public Long getLifetime() {
        return lifetime;
    }

    public void setLifetime(Long lifetime) {
        this.lifetime = lifetime;
    }

    public Long getMargin() {
        return margin;
    }

    public void setMargin(Long margin) {
        this.margin = margin;
    }

    public XMLGregorianCalendar getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(XMLGregorianCalendar createdDate) {
        this.createdDate = createdDate;
    }

}