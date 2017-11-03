/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response;

import ru.axetta.ecafe.processor.core.persistence.OrgFile;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.Date;

public class ResOrgFilesItem {

    private Long idOfOrgFile;
    private Long idOfOrg;
    private String fileData;
    private Integer resCode;
    private String errorMessage;
    private String name;
    private String fileExt;
    private String displayName;
    private Long size;
    private Long idOfArm;
    private Date date;

    public ResOrgFilesItem() {

    }

    public ResOrgFilesItem(OrgFile orgFile) {
        this.idOfOrg = orgFile.getOrgOwner().getIdOfOrg();
        this.name = orgFile.getName();
        this.idOfOrgFile = orgFile.getIdOfOrgFile();
        this.fileExt = orgFile.getExt();
        this.displayName = orgFile.getDisplayName();
        this.date = orgFile.getDate();
        this.size = orgFile.getSize();
        this.idOfArm = orgFile.getIdOfArm();
    }

    public Element toElement(Document document, String elementName) throws Exception {
        Element element = document.createElement(elementName);
        XMLUtils.setAttributeIfNotNull(element, "id", idOfOrgFile);
        XMLUtils.setAttributeIfNotNull(element, "idOfOrg", idOfOrg);
        //XMLUtils.setAttributeIfNotNull(element, "name", name);
        XMLUtils.setAttributeIfNotNull(element, "data", fileData);
        //XMLUtils.setAttributeIfNotNull(element, "ext", fileExt);
        XMLUtils.setAttributeIfNotNull(element, "displayname", displayName);
        XMLUtils.setAttributeIfNotNull(element, "Res", resCode);

        if (resCode != null && resCode != 0) {
            XMLUtils.setAttributeIfNotNull(element, "Error", errorMessage);
        }

        XMLUtils.setAttributeIfNotNull(element, "size", size.toString());
        XMLUtils.setAttributeIfNotNull(element, "date", date.toString());
        XMLUtils.setAttributeIfNotNull(element, "idOfArm", idOfArm.toString());

        return element;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getFileData() {
        return fileData;
    }

    public void setFileData(String fileData) {
        this.fileData = fileData;
    }

    public Integer getResCode() {
        return resCode;
    }

    public void setResCode(Integer resCode) {
        this.resCode = resCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getIdOfOrgFile() {
        return idOfOrgFile;
    }

    public void setIdOfOrgFile(Long idOfOrgFile) {
        this.idOfOrgFile = idOfOrgFile;
    }

    public String getFileExt() {
        return fileExt;
    }

    public void setFileExt(String fileExt) {
        this.fileExt = fileExt;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getIdOfArm() {
        return idOfArm;
    }

    public void setIdOfArm(Long idOfArm) {
        this.idOfArm = idOfArm;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}