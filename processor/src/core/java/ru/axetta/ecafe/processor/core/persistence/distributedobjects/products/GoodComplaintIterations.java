/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

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
import java.util.Set;

public class GoodComplaintIterations extends DistributedObject {

    private GoodComplaintBook complaint;
    private String guidOfComplaint;
    private Integer iterationNumber;
    private GoodComplaintIterationStatus goodComplaintIterationStatus;
    private Integer iterationStatusNumber;
    private String problemDescription;
    private String conclusion;

    private Set<GoodComplaintOrders> goodComplaintOrdersInternal;
    private Set<GoodComplaintCauses> goodComplaintCausesInternal;

    @Override
    public void createProjections(Criteria criteria) {
        criteria.createAlias("complaint","c", JoinType.LEFT_OUTER_JOIN);
        criteria.createAlias("goodComplaintIterationStatus","gc", JoinType.LEFT_OUTER_JOIN);

        ProjectionList projectionList = Projections.projectionList();
        addDistributedObjectProjectionList(projectionList);

        projectionList.add(Projections.property("gc.statusNumber"), "iterationStatusNumber");

        projectionList.add(Projections.property("c.guid"), "guidOfComplaint");
        criteria.setProjection(projectionList);
    }

    @Override
    public List<DistributedObject> process(Session session, Long idOfOrg, Long currentMaxVersion,
            String currentLastGuid, Integer currentLimit) throws Exception {
        return toSelfProcess(session, idOfOrg, currentMaxVersion, currentLastGuid, currentLimit);
    }

    @Override
    public void preProcess(Session session, Long idOfOrg) throws DistributedObjectException {
        GoodComplaintBook gcb = DAOUtils.findDistributedObjectByRefGUID(GoodComplaintBook.class, session, guidOfComplaint);
        if (gcb == null) throw new DistributedObjectException("Complaint NOT_FOUND_VALUE");
        setComplaint(gcb);

        try {
            GoodComplaintIterationStatus is = GoodComplaintIterationStatus.getStatusByNumberNullSafe(iterationStatusNumber);
            if (is == null) throw new Exception("Iteration status NOT_FOUND_VALUE");
            setGoodComplaintIterationStatus(is);
        } catch (Exception e) {
            throw new DistributedObjectException(e.getMessage());
        }
    }

    @Override
    protected void appendAttributes(Element element) {
        XMLUtils.setAttributeIfNotNull(element, "OrgOwner", orgOwner);
        XMLUtils.setAttributeIfNotNull(element, "IterationNumber", iterationNumber);
        XMLUtils.setAttributeIfNotNull(element, "ProblemDescription", problemDescription);
        XMLUtils.setAttributeIfNotNull(element, "Conclusion", conclusion);

        if(iterationStatusNumber!=null){
            XMLUtils.setAttributeIfNotNull(element, "Status", iterationStatusNumber);
        }
        if(StringUtils.isNotEmpty(guidOfComplaint)){
            XMLUtils.setAttributeIfNotNull(element, "GuidOfComplaint", guidOfComplaint);
        }
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setIterationNumber(((GoodComplaintIterations)distributedObject).getIterationNumber());
        setProblemDescription(((GoodComplaintIterations) distributedObject).getProblemDescription());
        setConclusion(((GoodComplaintIterations) distributedObject).getConclusion());
        setIterationStatusNumber(((GoodComplaintIterations) distributedObject).getIterationStatusNumber());
        setComplaint(((GoodComplaintIterations) distributedObject).getComplaint());
    }

    @Override
    protected GoodComplaintIterations parseAttributes(Node node) throws Exception {
        Long longOrgOwner = XMLUtils.getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null){
            setOrgOwner(longOrgOwner);
        } else {
            throw new DistributedObjectException("OrgOwner is empty");
        }
        guidOfComplaint = XMLUtils.getStringAttributeValue(node, "GuidOfComplaint", 36);
        iterationNumber = XMLUtils.getIntegerAttributeValue(node, "IterationNumber");
        iterationStatusNumber = XMLUtils.getIntegerAttributeValue(node, "Status");
        problemDescription = XMLUtils.getStringAttributeValue(node, "ProblemDescription", 512);
        conclusion = XMLUtils.getStringAttributeValue(node, "Conclusion", 512);
        setSendAll(SendToAssociatedOrgs.SendToAll);
        return this;
    }

    public GoodComplaintBook getComplaint() {
        return complaint;
    }

    public void setComplaint(GoodComplaintBook complaint) {
        this.complaint = complaint;
    }

    public String getGuidOfComplaint() {
        return guidOfComplaint;
    }

    public void setGuidOfComplaint(String guidOfComplaint) {
        this.guidOfComplaint = guidOfComplaint;
    }

    public Integer getIterationNumber() {
        return iterationNumber;
    }

    public void setIterationNumber(Integer iterationNumber) {
        this.iterationNumber = iterationNumber;
    }

    public GoodComplaintIterationStatus getGoodComplaintIterationStatus() {
        return goodComplaintIterationStatus;
    }

    public void setGoodComplaintIterationStatus(GoodComplaintIterationStatus goodComplaintIterationStatus) {
        this.goodComplaintIterationStatus = goodComplaintIterationStatus;
    }

    public Integer getIterationStatusNumber() {
        return iterationStatusNumber;
    }

    public void setIterationStatusNumber(Integer iterationStatusNumber) {
        this.iterationStatusNumber = iterationStatusNumber;
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

    public Set<GoodComplaintCauses> getGoodComplaintCausesInternal() {
        return goodComplaintCausesInternal;
    }

    public void setGoodComplaintCausesInternal(Set<GoodComplaintCauses> goodComplaintCausesInternal) {
        this.goodComplaintCausesInternal = goodComplaintCausesInternal;
    }

    public Set<GoodComplaintOrders> getGoodComplaintOrdersInternal() {
        return goodComplaintOrdersInternal;
    }

    public void setGoodComplaintOrdersInternal(Set<GoodComplaintOrders> goodComplaintOrdersInternal) {
        this.goodComplaintOrdersInternal = goodComplaintOrdersInternal;
    }

}
