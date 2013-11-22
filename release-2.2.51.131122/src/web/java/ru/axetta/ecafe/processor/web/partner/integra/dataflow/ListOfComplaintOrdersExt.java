package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListOfComplaintOrdersExt", propOrder = {
        "idOfOrderDetail", "menuDetailName", "dateOfOrder",
        "guid", "deletedState", "createdDate", "orgOwner"
})
public class ListOfComplaintOrdersExt {

    @XmlAttribute(name = "IdOfOrderDetail")
    protected Long idOfOrderDetail;
    @XmlAttribute(name = "MenuDetailName")
    protected String menuDetailName;
    @XmlAttribute(name = "DateOfOrder")
    protected XMLGregorianCalendar dateOfOrder;
    @XmlAttribute(name = "Guid")
    protected String guid;
    @XmlAttribute(name = "DeletedState")
    protected Boolean deletedState;
    @XmlAttribute(name = "CreatedDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar createdDate;
    @XmlAttribute(name = "OrgOwner")
    protected Long orgOwner;

    public Long getIdOfOrderDetail() {
        return idOfOrderDetail;
    }

    public void setIdOfOrderDetail(Long idOfOrderDetail) {
        this.idOfOrderDetail = idOfOrderDetail;
    }

    public String getMenuDetailName() {
        return menuDetailName;
    }

    public void setMenuDetailName(String menuDetailName) {
        this.menuDetailName = menuDetailName;
    }

    public XMLGregorianCalendar getDateOfOrder() {
        return dateOfOrder;
    }

    public void setDateOfOrder(XMLGregorianCalendar dateOfOrder) {
        this.dateOfOrder = dateOfOrder;
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

    public XMLGregorianCalendar getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(XMLGregorianCalendar createdDate) {
        this.createdDate = createdDate;
    }

    public Long getOrgOwner() {
        return orgOwner;
    }

    public void setOrgOwner(Long orgOwner) {
        this.orgOwner = orgOwner;
    }

}
