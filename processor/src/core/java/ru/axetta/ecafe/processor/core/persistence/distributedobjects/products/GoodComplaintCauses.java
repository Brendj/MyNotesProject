package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.HashMap;

public class GoodComplaintCauses extends DistributedObject {

    public static enum ComplaintCauses {

        badTaste(0),
        badSmell(1),
        malaise(2),
        badQualityProducts(3),
        overdue(4),
        highPrice(5);

        private static final HashMap<ComplaintCauses, String> CAUSE_TITLES_MAP;

        static {
            CAUSE_TITLES_MAP = new HashMap<ComplaintCauses, String>(ComplaintCauses.values().length);
            CAUSE_TITLES_MAP.put(badTaste,           "Неприятный вкус");
            CAUSE_TITLES_MAP.put(badSmell,           "Неприятный запах");
            CAUSE_TITLES_MAP.put(malaise,            "Недомогание после употребления");
            CAUSE_TITLES_MAP.put(badQualityProducts, "Подозрение на некачественные продукты в составе блюда");
            CAUSE_TITLES_MAP.put(overdue,            "Просроченность");
            CAUSE_TITLES_MAP.put(highPrice,          "Завышенная цена");
        }

        private Integer causeNumber;

        private ComplaintCauses(Integer causeNumber) {
            this.causeNumber = causeNumber;
        }

        public Integer getCauseNumber() {
            return causeNumber;
        }

        public String getTitle() {
            return CAUSE_TITLES_MAP.get(this);
        }

        public static ComplaintCauses getCauseByNumberNullSafe(Integer causeNumber) {
            if (causeNumber == null) {
                return null;
            }
            for (ComplaintCauses cause : ComplaintCauses.values()) {
                if (causeNumber.equals(cause.getCauseNumber())) {
                    return cause;
                }
            }
            return null;
        }

    }

    @Override
    public void preProcess(Session session) throws DistributedObjectException {
        GoodComplaintIterations gci = (GoodComplaintIterations) DAOUtils.findDistributedObjectByRefGUID(session, guidOfComplaintIteration);
        if (gci == null) throw new DistributedObjectException("Complaint iteration NOT_FOUND_VALUE");
        setComplaintIteration(gci);

        try {
            ComplaintCauses cause = ComplaintCauses.getCauseByNumberNullSafe(causeNumber);
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
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(distributedObject.getOrgOwner());
    }

    private GoodComplaintIterations complaintIteration;
    private String guidOfComplaintIteration;
    private ComplaintCauses cause;
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

    public ComplaintCauses getCause() {
        return cause;
    }

    public void setCause(ComplaintCauses cause) {
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
