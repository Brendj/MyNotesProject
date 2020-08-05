/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.cardsync.models;

import ru.iteco.cardsync.ActionType;
import ru.iteco.cardsync.audit.AuditEntity;
import ru.iteco.cardsync.audit.AuditEntityListener;
import ru.iteco.cardsync.audit.Auditable;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Entity
@EntityListeners(AuditEntityListener.class)
@Table(name = "cf_cr_cardactionrequests")
public class CardActionRequest implements Auditable {
    @GenericGenerator(
            name = "cf_cr_cardactionrequests_seq",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @Parameter(name = "sequence_name", value = "cf_cr_cardactionrequests_id_seq"),
                    @Parameter(name = "INCREMENT", value = "1"),
                    @Parameter(name = "MINVALUE", value = "1"),
                    @Parameter(name = "MAXVALUE", value = "2147483647"),
                    @Parameter(name = "CACHE", value = "1")
            }
    )

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cf_cr_cardactionrequests_seq")
    @Column(name = "id")
    private Long id;

    @Column(name = "requestId", nullable = false)
    private String requestId;

    @Column("idofclient")
    private Long idOfClient;

    @Column(name = "idoforg", nullable = false)
    private Long idOfOrg;

    @Column(name = "action", nullable = false)
    @Enumerated(EnumType.ORDINAL)
    private ActionType actionType;

    @Column(name = "contingentid", length = 36)
    private String contingentId;

    @Column(name = "staffId", length = 36)
    private String staffId;

    @Embedded
    private AuditEntity auditEntity;

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

    public Long getIdOfClient() {
        return idOfClient;
    }

    public void setIdOfClient(Long idOfClient) {
        this.idOfClient = idOfClient;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
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
}
