package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListOfProductGroupsExt", propOrder = {
        "nameOfGroup", "classificationCode", "deletedState", "guid", "orgOwner", "createdDate", "products"
})
public class ListOfProductGroupsExt {

    @XmlAttribute(name = "NameOfGroup")
    protected String nameOfGroup;
    @XmlAttribute(name = "ClassificationCode")
    protected String classificationCode;
    @XmlAttribute(name = "DeletedState")
    protected Boolean deletedState;
    @XmlAttribute(name = "Guid")
    protected String guid;
    @XmlAttribute(name = "OrgOwner")
    protected Long orgOwner;
    @XmlAttribute(name = "CreatedDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar createdDate;
    @XmlElement(name = "Products")
    protected List<ListOfProducts> products;

    public String getNameOfGroup() {
        return nameOfGroup;
    }

    public void setNameOfGroup(String nameOfGroup) {
        this.nameOfGroup = nameOfGroup;
    }

    public Boolean getDeletedState() {
        return deletedState;
    }

    public void setDeletedState(Boolean deletedState) {
        this.deletedState = deletedState;
    }

    public String getClassificationCode() {
        return classificationCode;
    }

    public void setClassificationCode(String classificationCode) {
        this.classificationCode = classificationCode;
    }

    public Long getOrgOwner() {
        return orgOwner;
    }

    public void setOrgOwner(Long orgOwner) {
        this.orgOwner = orgOwner;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public XMLGregorianCalendar getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(XMLGregorianCalendar createdDate) {
        this.createdDate = createdDate;
    }

    public List<ListOfProducts> getProducts() {
        if (products == null) {
            products = new ArrayList<ListOfProducts>();
        }
        return this.products;
    }

    public void setProducts(List<ListOfProducts> products) {
        this.products = products;
    }

}
