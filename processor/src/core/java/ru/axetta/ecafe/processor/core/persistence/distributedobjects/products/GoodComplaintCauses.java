package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class GoodComplaintCauses extends DistributedObject {

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        GoodComplaintIterations gci = DAOUtils.findDistributedObjectByRefGUID(GoodComplaintIterations.class, session, guidOfComplaintIteration);
        if (gci == null) throw new DistributedObjectException("Complaint iteration NOT_FOUND_VALUE");
        setComplaintIteration(gci);

        try {
            GoodComplaintPossibleCauses cause = GoodComplaintPossibleCauses.getCauseByNumberNullSafe(causeNumber);
            if (cause == null) throw new DistributedObjectException("Complaint cause NOT_FOUND_VALUE");
            setCause(cause);
        } catch (Exception e) {
            throw new DistributedObjectException(e.getMessage());
        }
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element, "GuidOfComplaintIteration", complaintIteration.getGuid());
        setAttribute(element, "Cause", cause.getCauseNumber());
    }

    @Override
    protected GoodComplaintCauses parseAttributes(Node node) throws Exception {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null) setOrgOwner(longOrgOwner);
        guidOfComplaintIteration = getStringAttributeValue(node, "GuidOfComplaintIteration", 36);
        causeNumber = getIntegerAttributeValue(node, "Cause");
        setSendAll(SendToAssociatedOrgs.SendToAll);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
    }

    private GoodComplaintIterations complaintIteration;
    private String guidOfComplaintIteration;
    private GoodComplaintPossibleCauses cause;
    private Integer causeNumber;

    public GoodComplaintIterations getComplaintIteration() {
        return complaintIteration;
    }

    public void setComplaintIteration(GoodComplaintIterations complaintIteration) {
        this.complaintIteration = complaintIteration;
    }

    public String getGuidOfComplaintIteration() {
        return guidOfComplaintIteration;
    }

    public void setGuidOfComplaintIteration(String guidOfComplaintIteration) {
        this.guidOfComplaintIteration = guidOfComplaintIteration;
    }

    public GoodComplaintPossibleCauses getCause() {
        return cause;
    }

    public void setCause(GoodComplaintPossibleCauses cause) {
        this.cause = cause;
    }

    public Integer getCauseNumber() {
        return causeNumber;
    }

    public void setCauseNumber(Integer causeNumber) {
        this.causeNumber = causeNumber;
    }

    public String getTitle() {
        return this.cause.getTitle();
    }

}
