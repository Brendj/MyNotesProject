/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.models;

import org.hibernate.annotations.GenericGenerator;
import ru.iteco.cardsync.audit.AuditEntity;
import ru.iteco.cardsync.audit.AuditEntityListener;
import ru.iteco.cardsync.audit.Auditable;
import ru.iteco.cardsync.enums.ActionType;
import ru.iteco.cardsync.kafka.dto.BlockPersonEntranceRequest;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Entity
@EntityListeners(AuditEntityListener.class)
@Table(name = "cf_cr_cardactionrequests")
public class CardActionRequest implements Auditable {

    @GenericGenerator(
            name = "cf_cr_cardactionrequests_idcardactionrequest",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "cf_cr_cardactionrequests_idcardactionrequest_seq"),
                    @org.hibernate.annotations.Parameter(name = "INCREMENT", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "MINVALUE", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "MAXVALUE", value = "2147483647"),
                    @org.hibernate.annotations.Parameter(name = "CACHE", value = "1")
            }
    )

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cf_cr_cardactionrequests_idcardactionrequest")
    @Column(name = "idcardactionrequest")
    private Long id;

    @OneToMany(mappedBy="cardActionRequest", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<CardActionClient> cardActionClients;

    @Column(name = "requestid", length = 128, nullable = false)
    private String requestId;

    @Column(name = "contingentid", length = 36)
    private String contingentId;

    @Column(name = "staffid", length = 36)
    private String staffId;

    @Column(name = "firstname", length = 36)
    private String firstname;

    @Column(name = "lastname", length = 36)
    private String lastname;

    @Column(name = "middlename", length = 36)
    private String middlename;

    @Column(name = "birthday")
    private Date birthdate;

    @Column(name = "organizationIds")
    private String organizationIds;

    @Column(name = "action", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ActionType actionType;

    @Column(name = "comment", nullable = false)
    private String comment;

    @Column(name = "processed", nullable = false)
    private Boolean processed;


    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name="previdcardrequest")
    private CardActionRequest cardActionRequest;

    @Embedded
    private AuditEntity auditEntity;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "idofclient")
    private Client client;

    public CardActionRequest() {
        // for Hibernate
    }

    //Сохраняем то, что пришло от МЭШ
    public static CardActionRequest buildCardActionRequest(BlockPersonEntranceRequest blockPersonEntranceRequest) {
        CardActionRequest request = new CardActionRequest();
        request.setRequestId(blockPersonEntranceRequest.getId());
        request.setContingentId(blockPersonEntranceRequest.getContingentId());
        request.setStaffId(blockPersonEntranceRequest.getStaffId());
        request.setFirstname(blockPersonEntranceRequest.getFirstName());
        request.setLastname(blockPersonEntranceRequest.getLastName());
        request.setMiddlename(blockPersonEntranceRequest.getMiddleName());
        request.setBirthdate(new Date(blockPersonEntranceRequest.getBirthdate()));
        request.setOrganizationIds(ConvertListToString(blockPersonEntranceRequest.getOrganizationIds()));
        request.setActionType(blockPersonEntranceRequest.getAction());

        return request;
    }

    static private String ConvertListToString(List<Long> data)
    {
        String rezult = "";
        if (!data.isEmpty()) {
            for (Long val : data) {
                rezult = rezult + val + " ";
            }
            rezult = rezult.substring(0, rezult.length() - 1);
        }
        return rezult;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Boolean getProcessed() {
        return processed;
    }

    public void setProcessed(Boolean processed) {
        this.processed = processed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public String getContingentId() {
        return contingentId;
    }

    public void setContingentId(String contingentId) {
        this.contingentId = contingentId;
    }

    public String getStaffId() {
        return staffId;
    }

    public void setStaffId(String staffId) {
        this.staffId = staffId;
    }

    @Override
    public AuditEntity getAudit() {
        return auditEntity;
    }

    @Override
    public void setAudit(AuditEntity audit) {
        this.auditEntity = audit;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMiddlename() {
        return middlename;
    }

    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getOrganizationIds() {
        return organizationIds;
    }

    public void setOrganizationIds(String organizationIds) {
        this.organizationIds = organizationIds;
    }

    public List<CardActionClient> getCardActionClients() {
        return cardActionClients;
    }

    public void setCardActionClients(List<CardActionClient> cardActionClients) {
        this.cardActionClients = cardActionClients;
    }

    public CardActionRequest getCardActionRequest() {
        return cardActionRequest;
    }

    public void setCardActionRequest(CardActionRequest cardActionRequest) {
        this.cardActionRequest = cardActionRequest;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }
}
