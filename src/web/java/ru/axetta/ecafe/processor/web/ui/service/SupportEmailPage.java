/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.mail.File;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 11:33:54
 * To change this template use File | Settings | File Templates.
 */
public class SupportEmailPage extends BasicWorkspacePage {

    private String address;
    private String subject;
    private String text="Добрый день!\n\n\n\nСпасибо за обращение!\n\nС уважением,\nСлужба поддержки Новая школа";
    private ArrayList<File> files = new ArrayList<File>();
    private int uploadsAvailable = 5;

    public String getPageFilename() {
        return "service/support_email";
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getUploadsAvailable() {
        return uploadsAvailable;
    }

    public void setUploadsAvailable(int uploadsAvailable) {
        this.uploadsAvailable = uploadsAvailable;
    }

    public ArrayList<File> getFiles() {
        return files;
    }

    public void setFiles(ArrayList<File> files) {
        this.files = files;
    }

    public void fill(Session session) throws Exception {

    }

    public void sendSupportEmail(Session session) throws Exception {
        RuntimeContext runtimeContext = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            runtimeContext.getSupportEmailSender().postSupportEmail(address, subject, text, files);
        } finally {
            RuntimeContext.release(runtimeContext);
        }
    }

    public String clearUploadData() {
        files.clear();
        setUploadsAvailable(5);
        return null;
    }

    public void loadFiles(File file) {
        files.add(file);
        uploadsAvailable--;
    }

    public int getSize() {
        if (getFiles().size()>0){
            return getFiles().size();
        }else
        {
            return 0;
        }
    }
}