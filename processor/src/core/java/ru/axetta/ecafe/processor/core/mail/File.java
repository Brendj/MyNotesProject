/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.mail;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 09.11.11
 * Time: 16:24
 * To change this template use File | Settings | File Templates.
 */
public class File {
    private java.io.File file;
    private String fileName = "";
    private String contentType = "";

    public java.io.File getFile() {
        return file;
    }

    public void setFile(java.io.File file) {
        this.file = file;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
