package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListOfGoodGroupsExt", propOrder = {
        "nameOfGoodsGroup", "guid", "deletedState", "orgOwner", "createdDate", "goods"
})
public class ListOfGoodGroupsExt {

    @XmlAttribute(name = "NameOfGoodsGroup")
    protected String nameOfGoodsGroup;
    @XmlAttribute(name = "Guid")
    protected String guid;
    @XmlAttribute(name = "DeletedState")
    protected Boolean deletedState;
    @XmlAttribute(name = "OrgOwner")
    protected Long orgOwner;
    @XmlAttribute(name = "CreatedDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar createdDate;
    @XmlElement(name = "Goods")
    protected List<ListOfGoods> goods;

    public String getNameOfGoodsGroup() {
        return nameOfGoodsGroup;
    }

    public void setNameOfGoodsGroup(String nameOfGoodsGroup) {
        this.nameOfGoodsGroup = nameOfGoodsGroup;
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

    public XMLGregorianCalendar getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(XMLGregorianCalendar createdDate) {
        this.createdDate = createdDate;
    }

    public List<ListOfGoods> getGoods() {
        if (goods == null) {
            goods = new ArrayList<ListOfGoods>();
        }
        return this.goods;
    }

    public void setGoods(List<ListOfGoods> goods) {
        this.goods = goods;
    }

}
