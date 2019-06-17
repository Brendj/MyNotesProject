/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.webtechnologist;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.webtechnologist.WebTechnologistCatalog;
import ru.axetta.ecafe.processor.core.persistence.webtechnologist.WebTechnologistCatalogItem;
import ru.axetta.ecafe.processor.core.persistence.webtechnologist.WebTechnologistCatalogService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component("webTechnologistCatalogListPage")
@Scope("session")
@DependsOn("runtimeContext")
public class CatalogListPage extends BasicWorkspacePage {

    private static Logger logger = LoggerFactory.getLogger(CatalogListPage.class);

    private String catalogNameFilter;
    private String GUIDfilter;
    private List<WebTechnologistCatalog> itemList = Collections.emptyList();
    private WebTechnologistCatalog selectedItem;
    private WebTechnologistCatalogItem selectedCatalogElement;
    private Boolean showOnlyActive = false;
    private String nameForNewCatalog;
    private String descriptionForNewElement;

    public void updateCatalogList() {
        WebTechnologistCatalogService service = RuntimeContext.getAppContext()
                .getBean(WebTechnologistCatalogService.class);
        try {
            itemList.clear();
            itemList = service.getItemsListByCatalogNameOrGUID(catalogNameFilter, GUIDfilter, showOnlyActive);
        } catch (Exception e) {
            logger.error("Не удалось загрузить список справочников по фильтрам: ", e);
            printError("Не удалось загрузить список справочников по фильтрам: " + e.getMessage());
        }
    }

    public void dropAndReloadCatalogList() {
        Session session = null;
        Transaction transaction = null;
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            fill(session);
        } catch (Exception e) {
            logger.error("Can't load catalogs from DB: ", e);
            printError("Не удалось загрузить список справочников: " + e.getMessage());
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public void deleteItem() {
        if (selectedItem == null) {
            printError("Ошибка при попытке удаль элемент: элемент не был выбран");
            return;
        }
        WebTechnologistCatalogService service = RuntimeContext.getAppContext()
                .getBean(WebTechnologistCatalogService.class);
        try {
            service.deleteItem(selectedItem);
        } catch (Exception e) {
            logger.error("Не удалось обновить состояние каталога GUID " + selectedItem.getGUID() + ", ошибка: ", e);
            printMessage("Не удалось обновить состояние каталога GUID " + selectedItem.getGUID() + ", ошибка: " + e
                    .getMessage());
        }
    }

    public void createNewCatalog() {
        if (StringUtils.isEmpty(nameForNewCatalog)) {
            printError("Введите имя справочника");
            return;
        }
        try {
            User currentUser = MainPage.getSessionInstance().getCurrentUser();
            WebTechnologistCatalogService service = RuntimeContext.getAppContext()
                    .getBean(WebTechnologistCatalogService.class);
            WebTechnologistCatalog newCatalog = service.createNewCatalog(nameForNewCatalog, currentUser);
            itemList.add(newCatalog);
        } catch (Exception e) {
            logger.error("Can't create new catalog: ", e);
            printError("Не удалось создать новый справочник: " + e.getMessage());
        }
    }

    @Override
    public void onShow() throws Exception {
        Session session = null;
        catalogNameFilter = "";
        GUIDfilter = "";
        selectedItem = null;
        selectedCatalogElement = null;
        itemList.clear();
        try {
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            WebTechnologistCatalogService service = RuntimeContext.getAppContext()
                    .getBean(WebTechnologistCatalogService.class);
            itemList = service.getAllCatalogs(session);
        } catch (Exception e) {
            logger.error("Exception when prepared the CatalogListPage: ", e);
            throw e;
        } finally {
            HibernateUtils.close(session, logger);
        }
    }

    public void deleteCatalogElement() {
        if (selectedCatalogElement == null) {
            printError("Ошибка при попытке удаль элемент: элемент не был выбран");
            return;
        }
        WebTechnologistCatalogService service = RuntimeContext.getAppContext()
                .getBean(WebTechnologistCatalogService.class);
        try {
            service.deleteCatalogElement(selectedItem, selectedCatalogElement);

        } catch (Exception e) {
            logger.error("Не удалось обновить состояние элемента каталога GUID " + selectedCatalogElement.getGUID()
                    + ", ошибка: ", e);
            printError("Не удалось обновить состояние элемента каталога GUID " + selectedCatalogElement.getGUID()
                    + ", ошибка: " + e.getMessage());
        }
    }

    public void addElementToSelectedCatalog() {
        if (StringUtils.isBlank(descriptionForNewElement)) {
            printError("Введите описание элемента справочника");
            return;
        }
        WebTechnologistCatalogService service = RuntimeContext.getAppContext()
                .getBean(WebTechnologistCatalogService.class);
        try {
            WebTechnologistCatalogItem newItem = service
                    .createNewElementOfCatalog(selectedItem, descriptionForNewElement);
        } catch (Exception e) {
            logger.error("Не удалось создать элемент для справочника GUID:" + selectedItem.getGUID() + ", ошибка: ", e);
            printError("Не удалось создать элемент для справочника GUID:" + selectedItem.getGUID() + ", ошибка: " + e
                    .getMessage());
        }
    }

    public void applyChange() {
        WebTechnologistCatalogService service = RuntimeContext.getAppContext()
                .getBean(WebTechnologistCatalogService.class);
        try{
            service.applyChange(selectedItem);
        } catch (Exception e){
            logger.error("Не удалось применить изменения для справочника GUID:" + selectedItem.getGUID()
                    + ", ошибка: ", e);
            printError("Не удалось применить изменения элемент для справочника GUID:" + selectedItem.getGUID()
                    + ", ошибка: " + e.getMessage());
        }
    }

    public String getNameForNewCatalog() {
        return nameForNewCatalog;
    }

    public void setNameForNewCatalog(String nameForNewCatalog) {
        this.nameForNewCatalog = nameForNewCatalog;
    }

    public String getDescriptionForNewElement() {
        return descriptionForNewElement;
    }

    public void setDescriptionForNewElement(String descriptionForNewElement) {
        this.descriptionForNewElement = descriptionForNewElement;
    }

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

    @Override
    public String getPageFilename() {
        return "service/webtechnologist/catalog_list";
    }

    public Boolean getShowOnlyActive() {
        return showOnlyActive;
    }

    public void setShowOnlyActive(Boolean showOnlyActive) {
        this.showOnlyActive = showOnlyActive;
    }

    public WebTechnologistCatalogItem getSelectedCatalogElement() {
        return selectedCatalogElement;
    }

    public void setSelectedCatalogElement(WebTechnologistCatalogItem selectedCatalogElement) {
        this.selectedCatalogElement = selectedCatalogElement;
    }
}