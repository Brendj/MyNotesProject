/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.HashMap;

public class GoodComplaintIterations extends DistributedObject {

    public enum IterationStatus {

        creation(0, "Создание"),
        consideration(1, "Рассмотрение"),
        investigation(2, "Расследование"),
        conclusion(3, "Заключение");

        private Integer statusNumber;
        private String title;

        private IterationStatus(Integer statusNumber, String title) {
            this.statusNumber = statusNumber;
            this.title = title;
        }

        public Integer getStatusNumber() {
            return statusNumber;
        }

        public String getTitle() {
            return title;
        }

        public static IterationStatus getStatusByNumberNullSafe(Integer statusNumber) {
            if (statusNumber == null) {
                return null;
            }
            for (IterationStatus status : IterationStatus.values()) {
                if (statusNumber.equals(status.getStatusNumber())) {
                    return status;
                }
            }
            return null;
        }

    }

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        GoodComplaintBook gcb = (GoodComplaintBook) DAOUtils.findDistributedObjectByRefGUID(session, guidOfComplaint);
        if (gcb == null) throw new DistributedObjectException("Complaint NOT_FOUND_VALUE");
        setComplaint(gcb);

        try {
            IterationStatus is = IterationStatus.getStatusByNumberNullSafe(iterationStatusNumber);
            if (is == null) throw new Exception("Iteration status NOT_FOUND_VALUE");
            setIterationStatus(is);
        } catch (Exception e) {
            throw new DistributedObjectException(e.getMessage());
        }
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element, "GuidOfComplaint", complaint.getGuid());
        setAttribute(element, "IterationNumber", iterationNumber);
        setAttribute(element, "Status", iterationStatus.getStatusNumber());
        setAttribute(element, "ProblemDescription", problemDescription);
        setAttribute(element, "Conclusion", conclusion);
    }

    @Override
    protected GoodComplaintIterations parseAttributes(Node node) throws Exception {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null) setOrgOwner(longOrgOwner);
        guidOfComplaint = getStringAttributeValue(node, "GuidOfComplaint", 36);
        iterationNumber = getIntegerAttributeValue(node, "IterationNumber");
        iterationStatusNumber = getIntegerAttributeValue(node, "Status");
        problemDescription = getStringAttributeValue(node, "ProblemDescription", 512);
        conclusion = getStringAttributeValue(node, "Conclusion", 512);
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
        setIterationNumber(((GoodComplaintIterations)distributedObject).getIterationNumber());
        setProblemDescription(((GoodComplaintIterations) distributedObject).getProblemDescription());
        setConclusion(((GoodComplaintIterations) distributedObject).getConclusion());
    }

    private GoodComplaintBook complaint;
    private String guidOfComplaint;
    private Integer iterationNumber;
    private IterationStatus iterationStatus;
    private Integer iterationStatusNumber;
    private String problemDescription;
    private String conclusion;

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

    public IterationStatus getIterationStatus() {
        return iterationStatus;
    }

    public void setIterationStatus(IterationStatus iterationStatus) {
        this.iterationStatus = iterationStatus;
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

}
