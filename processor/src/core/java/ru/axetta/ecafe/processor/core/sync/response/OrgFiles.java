/*
 * Copyright (c) 2017. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.response;

import ru.axetta.ecafe.processor.core.file.FileUtils;
import ru.axetta.ecafe.processor.core.persistence.OrgFile;
import ru.axetta.ecafe.processor.core.sync.AbstractToElement;
import ru.axetta.ecafe.processor.core.sync.request.OrgFilesRequest;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrgFiles implements AbstractToElement {
    private Map<Long, OrgFilesItem> orgFilesItemMap = new HashMap<Long, OrgFilesItem>();
    private final long resultCode;
    private final String resultDescription;

    public OrgFiles() {
        resultCode = 0;
        resultDescription = "OK";
    }

    public OrgFiles(long resultCode, String resultDescription) {
        this.resultCode = resultCode;
        this.resultDescription = resultDescription;
    }

    public Element toElement(Document document) throws Exception {
        Element element = document.createElement("OrgFiles");
        element.setAttribute("Code", Long.toString(resultCode));
        element.setAttribute("Descr", resultDescription);
        for (OrgFilesItem item : this.orgFilesItemMap.values()) {
            element.appendChild(item.toElement(document));
        }
        return element;
    }

    public void addOrgFilesInfo(List<OrgFile> orgFileList, OrgFilesRequest.Operation operation) {
        for(OrgFile of : orgFileList) {
            OrgFilesItem item = null;

            switch (operation) {
                case LIST:
                    item = new OrgFilesItem(of.getIdOfOrgFile(), of.getName(), of.getExt(), of.getDisplayName(),
                            of.getOrgOwner().getIdOfOrg(), "", of.getDate(), of.getSize());
                    break;
                case ADD:
                    /* nope */
                    break;
                case DOWNLOAD:
                    item = new OrgFilesItem(of.getIdOfOrgFile(), of.getName(), of.getExt(), of.getDisplayName(),
                            of.getOrgOwner().getIdOfOrg(),
                            FileUtils.loadFile(of.getOrgOwner().getIdOfOrg(), of.getName(), of.getExt()),
                            "", of.getDate(), of.getSize());
                    break;
                case DELETE:
                    /* nope */
                    break;
            }
            orgFilesItemMap.put(of.getIdOfOrgFile(), item);
        }
    }
}
