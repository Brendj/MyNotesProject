/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

/**
 * Created by a.voinov on 27.10.2020.
 */

package ru.axetta.ecafe.processor.web.partner.library;
import org.codehaus.jackson.map.annotate.JsonDeserialize;
import java.util.Date;

@JsonDeserialize(using = JsonLibraryDeSerializer.class)
public class LibraryRequest {
    private String guid;
    private String libraryCode;
    private String libraryName;
    private String libraryAdress;
    private Date accessTime;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getLibraryCode() {
        return libraryCode;
    }

    public void setLibraryCode(String libraryCode) {
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

    public Date getAccessTime() {
        return accessTime;
    }

    public void setAccessTime(Date accessTime) {
        this.accessTime = accessTime;
    }
}
