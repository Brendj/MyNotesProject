/*
 * Copyright (c) 2021. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;
import java.util.Objects;

public class MeshClass {
    private Long id;
    private String uid;
    private Long organizationId;
    private String name;
    private Integer parallelId;
    private Integer educationStageId;
    private Date lastUpdate;
    private Date createDate;

    public MeshClass() {

    }

    public MeshClass(Integer id, String uid) {
        this.uid = uid;
        this.id = id.longValue();
        this.createDate = new Date();
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

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MeshClass meshClass = (MeshClass) o;
        return Objects.equals(id, meshClass.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
