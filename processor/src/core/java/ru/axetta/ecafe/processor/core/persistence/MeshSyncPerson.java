/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

public class MeshSyncPerson {
    private String meshId;
    private String personguid;
    private Date createdate;
    private Date lastupdate;
    private Date lastupdateRest;
    private Date birthdate;
    private String classname;
    private String classuid;
    private Boolean deletestate;
    private String firstname;
    private Integer genderid;
    private String lastname;
    private Long organizationid;
    private Integer parallelid;
    private String patronymic;
    private Boolean invaliddata;
    private Integer educationstageid;
    private String comment;
    private String guidnsi;
    private String training_end_at;
    private String idIsPp;

    public MeshSyncPerson() {

    }

    public MeshSyncPerson(String personguid) {
        this.personguid = personguid;
        createdate = new Date();
        lastupdate = new Date();
        invaliddata = false;
    }

    public String getPersonguid() {
        return personguid;
    }

    public void setPersonguid(String personguid) {
        this.personguid = personguid;
    }

    public Date getCreatedate() {
        return createdate;
    }

    public void setCreatedate(Date createdate) {
        this.createdate = createdate;
    }

    public Date getLastupdate() {
        return lastupdate;
    }

    public void setLastupdate(Date lastupdate) {
        this.lastupdate = lastupdate;
    }

    public Date getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(Date birthdate) {
        this.birthdate = birthdate;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getClassuid() {
        return classuid;
    }

    public void setClassuid(String classuid) {
        this.classuid = classuid;
    }

    public Boolean getDeletestate() {
        return deletestate;
    }

    public void setDeletestate(Boolean deletestate) {
        this.deletestate = deletestate;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public Integer getGenderid() {
        return genderid;
    }

    public void setGenderid(Integer genderid) {
        this.genderid = genderid;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public Long getOrganizationid() {
        return organizationid;
    }

    public void setOrganizationid(Long organizationid) {
        this.organizationid = organizationid;
    }

    public Integer getParallelid() {
        return parallelid;
    }

    public void setParallelid(Integer parallelid) {
        this.parallelid = parallelid;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public Boolean getInvaliddata() {
        return invaliddata;
    }

    public void setInvaliddata(Boolean invaliddata) {
        this.invaliddata = invaliddata;
    }

    public Integer getEducationstageid() {
        return educationstageid;
    }

    public void setEducationstageid(Integer educationstageid) {
        this.educationstageid = educationstageid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getGuidnsi() {
        return guidnsi;
    }

    public void setGuidnsi(String guidnsi) {
        this.guidnsi = guidnsi;
    }

    public Date getLastupdateRest() {
        return lastupdateRest;
    }

    public void setLastupdateRest(Date lastupdateRest) {
        this.lastupdateRest = lastupdateRest;
    }

    public String getMeshId() {
        return meshId;
    }

    public void setMeshId(String meshId) {
        this.meshId = meshId;
    }

    public String getTraining_end_at() {
        return training_end_at;
    }

    public void setTraining_end_at(String training_end_at) {
        this.training_end_at = training_end_at;
    }

    public String getIdIsPp() {
        return idIsPp;
    }

    public void setIdIsPp(String idIsPp) {
        this.idIsPp = idIsPp;
    }
}
