package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.SendToAssociatedOrgs;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.sql.JoinType;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.List;

public class GoodComplaintCauses extends DistributedObject {

    private GoodComplaintIterations complaintIteration;
    private String guidOfComplaintIteration;
    private GoodComplaintPossibleCauses cause;
    private Integer causeNumber;

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("complaintIteration","ci", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("cause","c", JoinType.LEFT_OUTER_JOIN);

        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("c.causeNumber"), "causeNumber");

        projectionList.add(Projections.property("ci.guid"), "guidOfComplaintIteration");
        criteria.setProjection(projectionList);
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        return toSelfProcess(session, idOfOrg, currentMaxVersion, currentLastGuid, currentLimit);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
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
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        if(causeNumber!=null){
            XMLUtils.setAttributeIfNotNull(element, "Cause", causeNumber);
        }
        if(StringUtils.isNotEmpty(guidOfComplaintIteration)){
            XMLUtils.setAttributeIfNotNull(element, "GuidOfComplaintIteration", guidOfComplaintIteration);
        }
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setCause(((GoodComplaintCauses) distributedObject).getCause());
        setComplaintIteration(((GoodComplaintCauses) distributedObject).getComplaintIteration());
    }

    @Override
    protected GoodComplaintCauses parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null){
            setOrgOwner(longOrgOwner);
        } else {
            throw new DistributedObjectException("OrgOwner is empty");
        }
        guidOfComplaintIteration = XMLUtils.getStringAttributeValue(node, "GuidOfComplaintIteration", 36);
        causeNumber = XMLUtils.getIntegerAttributeValue(node, "Cause");
        setSendAll(SendToAssociatedOrgs.SendToAll);
        return this;
    }

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
