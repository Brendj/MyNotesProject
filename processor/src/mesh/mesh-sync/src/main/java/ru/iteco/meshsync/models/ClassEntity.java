/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.iteco.meshsync.models;

import ru.iteco.meshsync.audit.AuditEntity;
import ru.iteco.meshsync.audit.AuditEntityListener;
import ru.iteco.meshsync.audit.Auditable;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "cf_mh_classes")
@EntityListeners(AuditEntityListener.class)
public class ClassEntity implements Auditable {
    @Id
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "uid", length = 36, updatable = false)
    private String uid;

    @Column(name = "organizationid")
    private Long organizationId;

    @Column(name = "name")
    private String name;

    @Column(name = "parallelid")
    private Integer parallelId;

    @Column(name = "educationstageid")
    private Integer educationStageId;

    @Embedded
    private AuditEntity audit;

    public ClassEntity() {
    }

    @Override
    public AuditEntity getAudit() {
        return audit;
    }

    @Override
    public void setAudit(AuditEntity audit) {
        this.audit = audit;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public Integer getParallelId() {
        return parallelId;
    }

    public void setParallelId(Integer parallelId) {
        this.parallelId = parallelId;
    }

    public Integer getEducationStageId() {
        return educationStageId;
    }

    public void setEducationStageId(Integer educationStageId) {
        this.educationStageId = educationStageId;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClassEntity that = (ClassEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(uid, that.uid) && Objects
                .equals(organizationId, that.organizationId) && Objects.equals(name, that.name) && Objects
                .equals(parallelId, that.parallelId) && Objects.equals(educationStageId, that.educationStageId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, uid, organizationId, name, parallelId, educationStageId);
    }
}
