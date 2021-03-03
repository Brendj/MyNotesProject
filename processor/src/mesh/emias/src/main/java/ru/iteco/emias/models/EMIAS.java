/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.emias.models;

import org.hibernate.annotations.GenericGenerator;
import ru.iteco.emias.audit.AuditEntity;
import ru.iteco.emias.audit.AuditEntityListener;

import javax.persistence.*;

@Entity
@EntityListeners(AuditEntityListener.class)
@Table(name = "cf_emias")
public class EMIAS {

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

    @Column(name = "errormessage")
    private String errormessage;

    @Column(name = "idemias")
    private String idemias;

    @Embedded
    private AuditEntity auditEntity;

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

    public String getErrormessage() {
        return errormessage;
    }

    public void setErrormessage(String errormessage) {
        this.errormessage = errormessage;
    }

    public AuditEntity getAuditEntity() {
        return auditEntity;
    }

    public void setAuditEntity(AuditEntity auditEntity) {
        this.auditEntity = auditEntity;
    }

    public String getIdemias() {
        return idemias;
    }

    public void setIdemias(String idemias) {
        this.idemias = idemias;
    }
}
