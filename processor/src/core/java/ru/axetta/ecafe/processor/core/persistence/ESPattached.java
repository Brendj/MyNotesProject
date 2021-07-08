/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import java.util.Date;

public class ESPattached {
    private Long idespattached;
    private ESP esp;
    private String filename;
    private String path;
    private Integer number;
    private Date createDate;
    private String linkinfos;

   public ESPattached(){}


    public Long getIdespattached() {
        return idespattached;
    }

    public void setIdespattached(Long idespattached) {
        this.idespattached = idespattached;
    }

    public ESP getEsp() {
        return esp;
    }

    public void setEsp(ESP esp) {
        this.esp = esp;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public String getLinkinfos() {
        return linkinfos;
    }

    public void setLinkinfos(String linkinfos) {
        this.linkinfos = linkinfos;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
