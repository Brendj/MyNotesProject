/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.webtechnologist;

import ru.axetta.ecafe.processor.core.persistence.webtechnologist.WebTechnologistCatalog;
import ru.axetta.ecafe.processor.core.persistence.webtechnologist.WebTechnologistCatalogItem;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Set;

@Component("webTechnologistCatalogViewPage")
@Scope("session")
public class CatalogViewPage extends BasicWorkspacePage {
    private static Logger logger = LoggerFactory.getLogger(CatalogViewPage.class);

    private Long targetIdOfCatalog;
    private String catalogName;
    private String GUID;
    private Date createDate;
    private Date lastUpdate;
    private String deleteState;
    private String userCreator;
    private Set<WebTechnologistCatalogItem> items;

    @Override
    public void fill(Session session) throws Exception{
        WebTechnologistCatalog catalog = (WebTechnologistCatalog) session.get(WebTechnologistCatalog.class, targetIdOfCatalog);
        catalogName = catalog.getCatalogName();
        GUID = catalog.getGUID();
        createDate = catalog.getCreateDate();
        lastUpdate = catalog.getLastUpdate();
        deleteState = catalog.getDeleteStateAsString();
        userCreator = catalog.getUserCreator().getUserName();
        items = catalog.getItems();
    }

    public Long getTargetIdOfCatalog() {
        return targetIdOfCatalog;
    }

    public void setTargetIdOfCatalog(Long targetIdOfCatalog) {
        this.targetIdOfCatalog = targetIdOfCatalog;
    }

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getDeleteState() {
        return deleteState;
    }

    public void setDeleteState(String deleteState) {
        this.deleteState = deleteState;
    }

    public String getUserCreator() {
        return userCreator;
    }

    public void setUserCreator(String userCreator) {
        this.userCreator = userCreator;
    }

    public Set<WebTechnologistCatalogItem> getItems() {
        return items;
    }

    public void setItems(Set<WebTechnologistCatalogItem> items) {
        this.items = items;
    }

    @Override
    public String getPageFilename(){
        return "service/webtechnologist/catalog_view";
    }
}
