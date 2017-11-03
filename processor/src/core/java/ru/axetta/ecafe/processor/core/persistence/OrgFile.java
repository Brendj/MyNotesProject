/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

public class OrgFile {
    private Long idOfOrgFile;
    private String name;
    private Org orgOwner;
    private String ext;
    private String displayName;
    private Date date;
    private Long idOfArm;
    private Long size;

    public OrgFile() {

    }

    public OrgFile(String name, String ext, String displayName, Org org, Date date, Long idOfArm, Long size) {
        this.name = name;
        this.orgOwner = org;
        this.ext = ext;
        this.displayName = displayName;
        this.date = date;
        this.idOfArm = idOfArm;
        this.size = size;
    }

    public Long getIdOfOrgFile() {
        return idOfOrgFile;
    }

    public void setIdOfOrgFile(Long idOfOrgFile) {
        this.idOfOrgFile = idOfOrgFile;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Org getOrgOwner() {
        return orgOwner;
    }

    public void setOrgOwner(Org orgOwner) {
        this.orgOwner = orgOwner;
    }

    public String getExt() {
        return ext;
    }

    public void setExt(String ext) {
        this.ext = ext;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getIdOfArm() {
        return idOfArm;
    }

    public void setIdOfArm(Long idOfArm) {
        this.idOfArm = idOfArm;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
