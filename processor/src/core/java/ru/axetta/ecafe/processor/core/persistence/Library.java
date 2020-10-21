/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

public class Library {
    private Long id;
    private String guid;
    private Long libraryCode;
    private String libraryName;
    private String libraryAdress;
    private Date accessTime;
    private Date createDate;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public Long getLibraryCode() {
        return libraryCode;
    }

    public void setLibraryCode(Long libraryCode) {
        this.libraryCode = libraryCode;
    }

    public String getLibraryName() {
        return libraryName;
    }

    public void setLibraryName(String libraryName) {
        this.libraryName = libraryName;
    }

    public String getLibraryAdress() {
        return libraryAdress;
    }

    public void setLibraryAdress(String libraryAdress) {
        this.libraryAdress = libraryAdress;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(Date accessTime) {
        this.accessTime = accessTime;
    }
}
