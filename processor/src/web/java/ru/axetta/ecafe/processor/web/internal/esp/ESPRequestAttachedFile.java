/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

/**
 * Created by a.voinov on 16.06.2021.
 */

package ru.axetta.ecafe.processor.web.internal.esp;

public class ESPRequestAttachedFile {
    private String attached_filename; //Имя прикрепляемого файла
    private String attached_filedata; //Данные файла

    public String getAttached_filename() {
        return attached_filename;
    }

    public void setAttached_filename(String attached_filename) {
        this.attached_filename = attached_filename;
    }

    public String getAttached_filedata() {
        return attached_filedata;
    }

    public void setAttached_filedata(String attached_filedata) {
        this.attached_filedata = attached_filedata;
    }
}
