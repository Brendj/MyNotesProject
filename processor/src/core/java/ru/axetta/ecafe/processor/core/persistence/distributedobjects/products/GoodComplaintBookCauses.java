package ru.axetta.ecafe.processor.core.persistence.distributedobjects.products;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.DistributedObject;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.manager.DistributedObjectException;

import org.hibernate.Session;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.util.HashMap;

public class GoodComplaintBookCauses extends DistributedObject {

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
            CAUSE_TITLES_MAP.put(ComplaintCauses.badTaste,           "Неприятный вкус");
            CAUSE_TITLES_MAP.put(ComplaintCauses.badSmell,           "Неприятный запах");
            CAUSE_TITLES_MAP.put(ComplaintCauses.malaise,            "Недомогание после употребления");
            CAUSE_TITLES_MAP.put(ComplaintCauses.badQualityProducts, "Подозрение на некачественные продукты в составе блюда");
            CAUSE_TITLES_MAP.put(ComplaintCauses.overdue,            "Просроченность");
            CAUSE_TITLES_MAP.put(ComplaintCauses.highPrice,          "Завышенная цена");
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
        GoodComplaintBook gcb = (GoodComplaintBook) DAOUtils.findDistributedObjectByRefGUID(session, complaintGuid);
        if (gcb == null) throw new DistributedObjectException("Complaint NOT_FOUND_VALUE");
        setComplaint(gcb);

        ComplaintCauses cause = ComplaintCauses.getCauseByNumberNullSafe(causeNumber);
        if (cause == null) throw new DistributedObjectException("Complaint cause NOT_FOUND_VALUE");
        setCause(cause);
    }

    @Override
    protected void appendAttributes(Element element) {
        setAttribute(element, "OrgOwner", orgOwner);
        setAttribute(element, "GuidOfComplaint", complaint.getGuid());
        setAttribute(element, "Cause", cause.getCauseNumber());
    }

    @Override
    protected GoodComplaintBookCauses parseAttributes(Node node) throws Exception {
        Long longOrgOwner = getLongAttributeValue(node, "OrgOwner");
        if (longOrgOwner != null) setOrgOwner(longOrgOwner);
        complaintGuid = getStringAttributeValue(node, "GuidOfComplaint", 36);
        causeNumber = getIntegerAttributeValue(node, "Cause");
        return this;
    }

    @Override
    public void fill(DistributedObject distributedObject) {
        setOrgOwner(((GoodComplaintBookCauses) distributedObject).getOrgOwner());
    }

    private GoodComplaintBook complaint;
    private String complaintGuid;
    private ComplaintCauses cause;
    private Integer causeNumber;

    public GoodComplaintBook getComplaint() {
        return complaint;
    }

    public void setComplaint(GoodComplaintBook complaint) {
        this.complaint = complaint;
    }

    public ComplaintCauses getCause() {
        ComplaintCauses.CAUSE_TITLES_MAP.get(ComplaintCauses.badTaste);
        return cause;
    }

    public void setCause(ComplaintCauses cause) {
        this.cause = cause;
    }

    public String getComplaintGuid() {
        return complaintGuid;
    }

    public void setComplaintGuid(String complaintGuid) {
        this.complaintGuid = complaintGuid;
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
