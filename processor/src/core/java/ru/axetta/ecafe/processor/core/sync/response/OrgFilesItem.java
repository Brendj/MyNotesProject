/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.sync.request.OrgFilesRequest;
import ru.axetta.ecafe.processor.core.utils.XMLUtils;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrgFilesItem {
    public static final Integer ERROR_CODE_ALL_OK = 0;
    public static final Integer ERROR_CODE_NOT_VALID_ATTRIBUTE = 100;
    public static final Integer ERROR_CODE_FILE_NOT_SAVED = 110;
    public static final Integer ERROR_CODE_FILE_NOT_DELETED = 111;
    public static final Integer ERROR_CODE_OUT_OF_SPACE = 112;
    public static final Integer ERROR_CODE_FILE_IS_TOO_BIG = 113;

    private final Long idOfOrgFile;
    private final String fileName;
    private final String fileExt;
    private final String fileData;
    private final String displayName;
    private final Long idOfOrg;
    private String errorMessage;
    private Integer resCode;
    private Long size;
    private Date date;

    public static OrgFilesItem build(Node nodeItem, Long idOfOrgOwner, OrgFilesRequest.Operation operation) throws Exception {
        String fileName;
        String fileData;
        String fileExt;
        String displayName;
        Long idOfFile;
        Long idOfOrg;

        EMSetter emSetter = new EMSetter("");

        idOfFile = XMLUtils.getLongAttributeValue(nodeItem, "id");
        fileName = XMLUtils.getAttributeValue(nodeItem, "name");
        fileExt = XMLUtils.getAttributeValue(nodeItem, "ext");
        displayName = XMLUtils.getAttributeValue(nodeItem, "displayName");

        idOfOrg = XMLUtils.getLongAttributeValue(nodeItem, "idOfOrg");
        if(idOfOrg != null) {
            Org org = DAOService.getInstance().findOrgById(idOfOrg);
            if (org == null) {
                emSetter.setCompositeErrorMessage(String.format("Организация с ИД=%s не найдена", idOfOrg));
            } else {
                List<Long> orgsIds = DAOReadonlyService.getInstance().findFriendlyOrgsIds(idOfOrgOwner);
                if(!orgsIds.contains(idOfOrg)){
                    emSetter.setCompositeErrorMessage(String.format("Организация с ИД=%s не принадлежит организации с ИД=%s", idOfOrg, idOfOrgOwner));
                }
            }
        }

        fileData = XMLUtils.getAttributeValue(nodeItem, "data");

        OrgFilesItem orgFilesItem = null;

        switch (operation) {
            case LIST:
                orgFilesItem = new OrgFilesItem(idOfFile, fileName, fileExt, displayName, idOfOrg, emSetter.getStr(),
                        null, -1L);
                break;
            case ADD:
                orgFilesItem = new OrgFilesItem(idOfFile, fileName, fileExt, displayName, idOfOrg, fileData,
                        emSetter.getStr(), null, -1L);
                break;
            case DOWNLOAD:
                orgFilesItem = new OrgFilesItem(idOfFile, fileName, fileExt, displayName, idOfOrg, emSetter.getStr(),
                        null, -1L);
                break;
            case DELETE:
                orgFilesItem = new OrgFilesItem(idOfFile, "", "", "", idOfOrg,
                        emSetter.getStr(), null, -1L);
                break;
        }
        return orgFilesItem;
    }

    public OrgFilesItem(Long idOfOrgFile, String fileName, String fileExt, String displayName, Long idOfOrg,
            String errorMessage, Date date, Long size) {
        this.idOfOrgFile = idOfOrgFile;
        this.fileName = fileName;
        this.fileExt = fileExt;
        this.displayName = displayName;
        this.idOfOrg = idOfOrg;
        this.fileData = "";
        this.errorMessage = errorMessage;
        this.date = date;
        this.size = size;

        if(errorMessage == null || errorMessage.isEmpty()){
            this.resCode = ERROR_CODE_ALL_OK;
        } else {
            this.resCode = ERROR_CODE_NOT_VALID_ATTRIBUTE;
        }
    }

    public OrgFilesItem(Long idOfOrgFile, String fileName, String fileExt, String displayName, Long idOfOrg,
            String fileData, String errorMessage, Date date, Long size) {
        this.idOfOrgFile = idOfOrgFile;
        this.fileName = fileName;
        this.fileExt = fileExt;
        this.displayName = displayName;
        this.idOfOrg = idOfOrg;
        this.fileData = fileData;
        this.errorMessage = errorMessage;
        this.date = date;
        this.size = size;

        if(errorMessage == null || errorMessage.isEmpty()){
            this.resCode = ERROR_CODE_ALL_OK;
        } else {
            this.resCode = ERROR_CODE_NOT_VALID_ATTRIBUTE;
        }
    }

    public Element toElement(Document document) throws Exception{
        Element element = document.createElement("OF");
        element.setAttribute("id", idOfOrgFile.toString());
        element.setAttribute("idOfOrg", idOfOrg.toString());
        //element.setAttribute("name", fileName);
        element.setAttribute("displayName", displayName);
        element.setAttribute("ext", fileExt);

        if (!fileData.isEmpty()) {
            element.setAttribute("data", fileData);
        }

        element.setAttribute("size", size.toString());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        element.setAttribute("date", dateFormat.format(date));

        return element;
    }

    public Long getIdOfOrgFile() {
        return idOfOrgFile;
    }

    public String getFileName() {
        return fileName;
    }

    public String getFileExt() {
        return fileExt;
    }

    public String getFileData() {
        return fileData;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public Integer getResCode() {
        return resCode;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public void setResCode(Integer resCode) {
        this.resCode = resCode;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }


    private static class EMSetter implements ISetErrorMessage {
        private String str;
        public EMSetter(String str) {
            this.setStr(str);
        }
        @Override
        public void setCompositeErrorMessage(String message) {
            setStr(str + message + ", ");
        }

        public String getStr() {
            if(str.length() > 2){
                return str.substring(0, str.length() - 2);
            } else {
                return str;
            }
        }

        public void setStr(String str) {
            this.str = str;
        }
    }

    private interface ISetErrorMessage {
        public void setCompositeErrorMessage(String message);
    }
}
