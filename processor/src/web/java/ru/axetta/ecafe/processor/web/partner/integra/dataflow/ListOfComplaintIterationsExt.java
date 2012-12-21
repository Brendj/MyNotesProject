package ru.axetta.ecafe.processor.web.partner.integra.dataflow;

import javax.xml.bind.annotation.*;
import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ListOfComplaintIterationsExt", propOrder = {
        "iterationNumber", "iterationStatus", "problemDescription",
        "conclusion", "guid", "deletedState", "createdDate", "orgOwner",
        "orders", "causes"
})
public class ListOfComplaintIterationsExt {

    @XmlAttribute(name = "IterationNumber")
    protected Integer iterationNumber;
    @XmlAttribute(name = "IterationStatus")
    protected Integer iterationStatus;
    @XmlAttribute(name = "ProblemDescription")
    protected String problemDescription;
    @XmlAttribute(name = "Conclusion")
    protected String conclusion;
    @XmlAttribute(name = "Guid")
    protected String guid;
    @XmlAttribute(name = "DeletedState")
    protected Boolean deletedState;
    @XmlAttribute(name = "CreatedDate")
    @XmlSchemaType(name = "date")
    protected XMLGregorianCalendar createdDate;
    @XmlAttribute(name = "OrgOwner")
    protected Long orgOwner;

    @XmlElement(name = "Orders")
    protected List<ListOfComplaintOrders> orders;
    @XmlElement(name = "Causes")
    protected List<ListOfComplaintCauses> causes;

    public Integer getIterationNumber() {
        return iterationNumber;
    }

    public void setIterationNumber(Integer iterationNumber) {
        this.iterationNumber = iterationNumber;
    }

    public Integer getIterationStatus() {
        return iterationStatus;
    }

    public void setIterationStatus(Integer iterationStatus) {
        this.iterationStatus = iterationStatus;
    }

    public String getProblemDescription() {
        return problemDescription;
    }

    public void setProblemDescription(String problemDescription) {
        this.problemDescription = problemDescription;
    }

    public String getConclusion() {
        return conclusion;
    }

    public void setConclusion(String conclusion) {
        this.conclusion = conclusion;
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

    public List<ListOfComplaintOrders> getOrders() {
        if (orders == null) {
            orders = new ArrayList<ListOfComplaintOrders>();
        }
        return orders;
    }

    public List<ListOfComplaintCauses> getCauses() {
        if (causes == null) {
            causes = new ArrayList<ListOfComplaintCauses>();
        }
        return causes;
    }

}
