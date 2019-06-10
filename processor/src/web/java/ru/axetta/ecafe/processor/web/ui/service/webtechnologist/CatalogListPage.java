/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.webtechnologist;

import ru.axetta.ecafe.processor.core.persistence.webtechnologist.WebTechnologistCatalog;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("session")
public class CatalogListPage extends BasicWorkspacePage {
    private static Logger logger = LoggerFactory.getLogger(CatalogListPage.class);

    private String catalogNameFilter;
    private String GUIDfilter;
    private List<WebTechnologistCatalog> itemList;
    private WebTechnologistCatalog selectedItem;

    public String getCatalogNameFilter() {
        return catalogNameFilter;
    }

    public void setCatalogNameFilter(String catalogNameFilter) {
        this.catalogNameFilter = catalogNameFilter;
    }

    public String getGUIDfilter() {
        return GUIDfilter;
    }

    public void setGUIDfilter(String GUIDfilter) {
        this.GUIDfilter = GUIDfilter;
    }

    public void updateCatalogList() {
    }

    public void dropAndReloadCatalogList() {
        catalogNameFilter = "";
        GUIDfilter = "";

    }

    public List<WebTechnologistCatalog> getItemList() {
        return itemList;
    }

    public void setItemList(List<WebTechnologistCatalog> itemList) {
        this.itemList = itemList;
    }

    public WebTechnologistCatalog getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(WebTechnologistCatalog selectedItem) {
        this.selectedItem = selectedItem;
    }
}
