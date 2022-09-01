package ru.axetta.ecafe.processor.core.push.model;

public class EnterEventData extends AbstractPushData {
    public static final String ENTRANCE_KEY = "entrance";
    public static final String ENTRANCE_AGENT_KEY = "entrance_agent";
    public static final String ENTRANCE_STAFF_KEY = "entrance_staff";

    private Integer actionType;
    private String occurredAt;
    private String personId;
    private Integer staffId;
    private Integer admittedByStaffId;
    private String agentId;
    private Integer organizationId;
    private String organizationName;
    private String ticketStatus;

    @Override
    public String toString() {
        return "EnterEventData{" +
                "actionType=" + actionType +
                ", occurredAt='" + occurredAt + '\'' +
                ", personId='" + personId + '\'' +
                ", staffId=" + staffId +
                ", admittedByStaffId=" + admittedByStaffId +
                ", agentId=" + agentId +
                ", organizationId=" + organizationId +
                ", organizationName='" + organizationName + '\'' +
                ", ticketStatus='" + ticketStatus + '\'' +
                '}';
    }

    public Integer getActionType() {
        return actionType;
    }

    public void setActionType(Integer actionType) {
        this.actionType = actionType;
    }

    public String getOccurredAt() {
        return occurredAt;
    }

    public void setOccurredAt(String occurredAt) {
        this.occurredAt = occurredAt;
    }

    public String getPersonId() {
        return personId;
    }

    public void setPersonId(String personId) {
        this.personId = personId;
    }

    public Integer getStaffId() {
        return staffId;
    }

    public void setStaffId(Integer staffId) {
        this.staffId = staffId;
    }

    public Integer getAdmittedByStaffId() {
        return admittedByStaffId;
    }

    public void setAdmittedByStaffId(Integer admittedByStaffId) {
        this.admittedByStaffId = admittedByStaffId;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getTicketStatus() {
        return ticketStatus;
    }

    public void setTicketStatus(String ticketStatus) {
        this.ticketStatus = ticketStatus;
    }
}
