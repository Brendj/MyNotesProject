package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProhibitionExclusionsListExt", propOrder = {
        "guid", "deletedState", "createdDate", "guidOfGood", "guidOfGoodsGroup"
})
public class ProhibitionExclusionsListExt {

    @XmlAttribute(name = "Guid")
    protected String guid;
    @XmlAttribute(name = "DeletedState")
    protected Boolean deletedState;
    @XmlAttribute(name = "CreatedDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar createdDate;
    @XmlAttribute(name = "GuidOfGood")
    protected String guidOfGood;
    @XmlAttribute(name = "GuidOfGoodsGroup")
    protected String guidOfGoodsGroup;

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

    public XMLGregorianCalendar getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(XMLGregorianCalendar createdDate) {
        this.createdDate = createdDate;
    }

    public String getGuidOfGood() {
        return guidOfGood;
    }

    public void setGuidOfGood(String guidOfGood) {
        this.guidOfGood = guidOfGood;
    }

    public String getGuidOfGoodsGroup() {
        return guidOfGoodsGroup;
    }

    public void setGuidOfGoodsGroup(String guidOfGoodsGroup) {
        this.guidOfGoodsGroup = guidOfGoodsGroup;
    }

}
