package ru.axetta.ecafe.processor.core.sync.request;

import ru.axetta.ecafe.processor.core.sync.LoadContext;

import org.w3c.dom.Node;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 17.01.14
 * Time: 17:55
 * To change this template use File | Settings | File Templates.
 */
public class EnterEventItem {

    private final long idOfEnterEvent;
    private final String enterName;
    private final String turnstileAddr;
    private final int passDirection;
    private final int eventCode;
    private final Long idOfCard;
    private final Long idOfClient;
    private final Long idOfTempCard;
    private final Date evtDateTime;
    private final Long idOfVisitor;
    private final String visitorFullName;
    private final Integer docType;
    private final String docSerialNum;
    private final Date issueDocDate;
    private final Date visitDateTime;
    private final Long guardianId;
    private final Integer childPassChecker;
    private final Long childPassCheckerId;
    private final Long longCardId;
    //private final Long idOfClientGroup;

    public static EnterEventItem build(Node enterEventNode, LoadContext loadContext) throws Exception{
        long idOfEnterEvent = Long
                .parseLong(enterEventNode.getAttributes().getNamedItem("IdOfEnterEvent").getTextContent());
        String enterName = enterEventNode.getAttributes().getNamedItem("EnterName").getTextContent();
        String turnstileAddr = enterEventNode.getAttributes().getNamedItem("TurnstileAddr")
                .getTextContent();
        int passDirection = Integer
                .parseInt(enterEventNode.getAttributes().getNamedItem("PassDirection").getTextContent());
        int eventCode = Integer
                .parseInt(enterEventNode.getAttributes().getNamedItem("EventCode").getTextContent());
        Long idOfCard = null;
        if (enterEventNode.getAttributes().getNamedItem("IdOfCard") != null) {
            idOfCard = Long
                    .parseLong(enterEventNode.getAttributes().getNamedItem("IdOfCard").getTextContent());
        }
        Long idOfClient = null;
        //Long idOfClientGroup = null;
        if (enterEventNode.getAttributes().getNamedItem("IdOfClient") != null) {
            idOfClient = Long
                    .parseLong(enterEventNode.getAttributes().getNamedItem("IdOfClient").getTextContent());
            //idOfClientGroup = DAOService.getInstance().getClientGroupByClientId(idOfClient);
        }
        Long idOfTempCard = null;
        if (enterEventNode.getAttributes().getNamedItem("IdOfTempCard") != null) {
            idOfTempCard = Long.parseLong(
                    enterEventNode.getAttributes().getNamedItem("IdOfTempCard").getTextContent());
        }

        //TimeZone localTimeZone = RuntimeContext.getInstance().getLocalTimeZone(null);//TimeZone.getTimeZone("Europe/Moscow");
        //DateFormat timeFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        //timeFormat.setTimeZone(localTimeZone);
        Date evtDateTime = loadContext.getTimeFormat()
                .parse(enterEventNode.getAttributes().getNamedItem("EvtDateTime").getTextContent());
        Long idOfVisitor = null;
        if (enterEventNode.getAttributes().getNamedItem("IdOfVisitor") != null) {
            idOfVisitor = Long
                    .parseLong(enterEventNode.getAttributes().getNamedItem("IdOfVisitor").getTextContent());
        }
        String visitorFullName = null;
        if (enterEventNode.getAttributes().getNamedItem("VisitorFullName") != null) {
            visitorFullName = enterEventNode.getAttributes().getNamedItem("VisitorFullName")
                    .getTextContent();
        }
        Integer docType = null;
        if (enterEventNode.getAttributes().getNamedItem("DocType") != null) {
            docType = Integer
                    .parseInt(enterEventNode.getAttributes().getNamedItem("DocType").getTextContent());
        }
        String docSerialNum = null;
        if (enterEventNode.getAttributes().getNamedItem("DocSerialNum") != null) {
            docSerialNum = enterEventNode.getAttributes().getNamedItem("DocSerialNum").getTextContent();
        }
        Date issueDocDate = null;
        if (enterEventNode.getAttributes().getNamedItem("IssueDocDate") != null) {
            issueDocDate = loadContext.getDateOnlyFormat()
                    .parse(enterEventNode.getAttributes().getNamedItem("IssueDocDate").getTextContent());
        }
        Date visitDateTime = null;
        if (enterEventNode.getAttributes().getNamedItem("VisitDateTime") != null) {
            visitDateTime = loadContext.getTimeFormat()
                    .parse(enterEventNode.getAttributes().getNamedItem("VisitDateTime").getTextContent());
        }
        Long guardianId = null;
        if (enterEventNode.getAttributes().getNamedItem("PassWithGuardian") != null) {
            guardianId = Long.parseLong(
                    enterEventNode.getAttributes().getNamedItem("PassWithGuardian").getTextContent());
        }
        Integer childPassChecker = null;
        if (enterEventNode.getAttributes().getNamedItem("ChildPassChecker") != null) {
            childPassChecker = Integer.parseInt(
                    enterEventNode.getAttributes().getNamedItem("ChildPassChecker").getTextContent());
        }
        Long childPassCheckerId = null;
        if (enterEventNode.getAttributes().getNamedItem("ChildPassCheckerId") != null) {
            childPassCheckerId = Long.parseLong(
                    enterEventNode.getAttributes().getNamedItem("ChildPassCheckerId").getTextContent());
        }
        Long longCardId = null;
        if (enterEventNode.getAttributes().getNamedItem("LongCardId") != null) {
            childPassCheckerId = Long.parseLong(
                    enterEventNode.getAttributes().getNamedItem("LongCardId").getTextContent());
        }
        return new EnterEventItem(idOfEnterEvent, enterName, turnstileAddr, passDirection, eventCode,
                idOfCard, idOfClient, idOfTempCard, evtDateTime, idOfVisitor, visitorFullName, docType,
                docSerialNum, issueDocDate, visitDateTime, guardianId, childPassChecker, childPassCheckerId, longCardId);
    }

    EnterEventItem(long idOfEnterEvent, String enterName, String turnstileAddr, int passDirection, int eventCode,
            Long idOfCard, Long idOfClient, Long idOfTempCard, Date evtDateTime, Long idOfVisitor,
            String visitorFullName, Integer docType, String docSerialNum, Date issueDocDate, Date visitDateTime,
            Long guardianId, Integer childPassChecker, Long childPassCheckerId, Long longCardId) {
        this.idOfEnterEvent = idOfEnterEvent;
        this.enterName = enterName;
        this.turnstileAddr = turnstileAddr;
        this.passDirection = passDirection;
        this.eventCode = eventCode;
        this.idOfCard = idOfCard;
        this.idOfClient = idOfClient;
        this.idOfTempCard = idOfTempCard;
        this.evtDateTime = evtDateTime;
        this.idOfVisitor = idOfVisitor;
        this.visitorFullName = visitorFullName;
        this.docType = docType;
        this.docSerialNum = docSerialNum;
        this.issueDocDate = issueDocDate;
        this.visitDateTime = visitDateTime;
        this.guardianId = guardianId;
        this.childPassChecker = childPassChecker;
        this.childPassCheckerId = childPassCheckerId;
        this.longCardId = longCardId;
        //this.idOfClientGroup = idOfClientGroup;
    }

    public long getIdOfEnterEvent() {
        return idOfEnterEvent;
    }

    public String getEnterName() {
        return enterName;
    }

    public String getTurnstileAddr() {
        return turnstileAddr;
    }

    public int getPassDirection() {
        return passDirection;
    }

    public int getEventCode() {
        return eventCode;
    }

    public Long getIdOfCard() {
        return idOfCard;
    }

    public Long getIdOfClient() {
        return idOfClient;
    }

    public Long getIdOfTempCard() {
        return idOfTempCard;
    }

    public Date getEvtDateTime() {
        return evtDateTime;
    }

    public Long getIdOfVisitor() {
        return idOfVisitor;
    }

    public String getVisitorFullName() {
        return visitorFullName;
    }

    public Integer getDocType() {
        return docType;
    }

    public String getDocSerialNum() {
        return docSerialNum;
    }

    public Date getIssueDocDate() {
        return issueDocDate;
    }

    public Date getVisitDateTime() {
        return visitDateTime;
    }

    public Long getGuardianId() {
        return guardianId;
    }

    public Integer getChildPassChecker() {
        return childPassChecker;
    }

    public Long getChildPassCheckerId() {
        return childPassCheckerId;
    }

    public Long getLongCardId() {
        return longCardId;
    }

    /*public Long getIdOfClientGroup() {
        return idOfClientGroup;
    }*/
}
