package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProhibitionsListExt")
public class ProhibitionsListExt {

    @XmlAttribute(name = "Guid")
    protected String guid;
    @XmlAttribute(name = "DeletedState")
    protected Boolean deletedState;
    @XmlAttribute(name = "CreatedDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar createdDate;
    @XmlAttribute(name = "ContractId")
    protected Long contractId;
    @XmlAttribute(name = "GuidOfProducts")
    protected String guidOfProducts;
    @XmlAttribute(name = "GuidOfProductGroups")
    protected String guidOfProductGroups;
    @XmlAttribute(name = "GuidOfGood")
    protected String guidOfGood;
    @XmlAttribute(name = "GuidOfGoodsGroup")
    protected String guidOfGoodsGroup;
    @XmlElement(name = "Exclusions")
    protected List<ProhibitionExclusionsList> exclusions;

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

    public Long getContractId() {
        return contractId;
    }

    public void setContactId(Long contactId) {
        this.contractId = contactId;
    }

    public String getGuidOfProducts() {
        return guidOfProducts;
    }

    public void setGuidOfProducts(String guidOfProducts) {
        this.guidOfProducts = guidOfProducts;
    }

    public String getGuidOfProductGroups() {
        return guidOfProductGroups;
    }

    public void setGuidOfProductGroups(String guidOfProductGroups) {
        this.guidOfProductGroups = guidOfProductGroups;
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

    public List<ProhibitionExclusionsList> getExclusions() {
        return exclusions;
    }

    public void setExclusions(List<ProhibitionExclusionsList> exclusions) {
        this.exclusions = exclusions;
    }

}
