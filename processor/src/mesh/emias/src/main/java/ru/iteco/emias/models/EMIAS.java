/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.emias.models;

import org.hibernate.annotations.GenericGenerator;
import ru.iteco.emias.audit.AuditEntity;
import ru.iteco.emias.audit.AuditEntityListener;
import ru.iteco.emias.audit.Auditable;

import javax.persistence.*;

@Entity
@EntityListeners(AuditEntityListener.class)
@Table(name = "cf_emias")
public class EMIAS implements Auditable {

    public EMIAS() {
        // for Hibernate
    }
    @GenericGenerator(
            name = "cf_emias_id_seqrequest",
            strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {
                    @org.hibernate.annotations.Parameter(name = "sequence_name", value = "cf_emias_id_seq"),
                    @org.hibernate.annotations.Parameter(name = "INCREMENT", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "MINVALUE", value = "1"),
                    @org.hibernate.annotations.Parameter(name = "MAXVALUE", value = "2147483647"),
                    @org.hibernate.annotations.Parameter(name = "CACHE", value = "1")
            }
    )

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "cf_emias_id_seqrequest")
    @Column(name = "id")
    private Long id;

    @Column(name = "guid")
    private String guid;

    @Column(name = "ideventemias")
    private Long ideventemias;

    @Column(name = "dateliberate")
    private Long dateliberate;

    @Column(name = "startdateliberate")
    private Long startdateliberate;

    @Column(name = "enddateliberate")
    private Long enddateliberate;

    @Column(name = "version")
    private Long version;

    @Column(name = "kafka")
    private Boolean kafka;

    @Column(name = "archive")
    private Boolean archive;

    @Column(name = "hazard_level_id")
    private Integer hazard_level_id;

    @Embedded
    private AuditEntity auditEntity;

    @Override
    public AuditEntity getAudit() {
        return auditEntity;
    }

    @Override
    public void setAudit(AuditEntity audit) {
        this.auditEntity = audit;
    }
    public Boolean getKafka() {
        return kafka;
    }

    public void setKafka(Boolean kafka) {
        this.kafka = kafka;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Boolean getArchive() {
        return archive;
    }

    public void setArchive(Boolean archive) {
        this.archive = archive;
    }

    public Integer getHazard_level_id() {
        return hazard_level_id;
    }

    public void setHazard_level_id(Integer hazard_level_id) {
        this.hazard_level_id = hazard_level_id;
    }

    public Long getIdeventemias() {
        return ideventemias;
    }

    public void setIdeventemias(Long ideventemias) {
        this.ideventemias = ideventemias;
    }

    public Long getDateliberate() {
        return dateliberate;
    }

    public void setDateliberate(Long dateliberate) {
        this.dateliberate = dateliberate;
    }

    public Long getStartdateliberate() {
        return startdateliberate;
    }

    public void setStartdateliberate(Long startdateliberate) {
        this.startdateliberate = startdateliberate;
    }

    public Long getEnddateliberate() {
        return enddateliberate;
    }

    public void setEnddateliberate(Long enddateliberate) {
        this.enddateliberate = enddateliberate;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
