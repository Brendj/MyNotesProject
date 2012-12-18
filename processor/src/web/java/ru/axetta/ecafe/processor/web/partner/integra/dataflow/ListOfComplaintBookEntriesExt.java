package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListOfComplaintBookEntriesExt", propOrder = {
        "guid", "deletedState", "createdDate", "orgOwner", "contractId", "idOfGood",
        "comment", "causes"
})
public class ListOfComplaintBookEntriesExt {

    @XmlAttribute(name = "Guid")
    protected String guid;
    @XmlAttribute(name = "DeletedState")
    protected Boolean deletedState;
    @XmlAttribute(name = "CreatedDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar createdDate;
    @XmlSchemaType(name = "OrgOwner")
    protected Long orgOwner;
    @XmlAttribute(name = "ContractId")
    protected Long contractId;
    @XmlAttribute(name = "IdOfGood")
    protected Long idOfGood;
    @XmlElement(name = "Comment")
    protected String comment;
    @XmlElement(name = "Causes")
    protected List<ListOfComplaintBookCauses> causes;

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

    public Long getContractId() {
        return contractId;
    }

    public void setContractId(Long contractId) {
        this.contractId = contractId;
    }

    public Long getIdOfGood() {
        return idOfGood;
    }

    public void setIdOfGood(Long idOfGood) {
        this.idOfGood = idOfGood;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<ListOfComplaintBookCauses> getCauses() {
        if (causes == null) {
            causes = new ArrayList<ListOfComplaintBookCauses>();
        }
        return causes;
    }

    public void setCauses(List<ListOfComplaintBookCauses> causes) {
        this.causes = causes;
    }

}
