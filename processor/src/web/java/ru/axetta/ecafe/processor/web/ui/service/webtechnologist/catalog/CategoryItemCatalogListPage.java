/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.webtechnologist.catalog;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtCategoryItem;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.services.WtCatalogService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
@Scope("session")
@DependsOn("runtimeContext")
public class CategoryItemCatalogListPage extends BasicWorkspacePage {
    private static final Logger logger = LoggerFactory.getLogger(CategoryItemCatalogListPage.class);

    private String descriptionFilter;
    private String descriptionForNewItem;
    private List<WtCategoryItem> catalogListItem;
    private WtCategoryItem selectedItem;

    private WtCatalogService service;

    @Autowired
    public void setService(WtCatalogService service) {
        this.service = service;
    }

    @Override
    public void onShow() throws Exception {
        catalogListItem = service.getAllActiveCategoryItem();
        descriptionForNewItem = "";
        descriptionFilter = "";
    }

    public void clearDescriptionForNewCatalog() {
        descriptionForNewItem = "";
    }

    public void createNewItem() {
        if(StringUtils.isBlank(descriptionForNewItem)){
            printError("Введите описание элемента");
        }
        try {
            User currentUser = MainPage.getSessionInstance().getCurrentUser();
            WtCategoryItem item =  service.createCategoryItem(descriptionForNewItem, currentUser);
            catalogListItem.add(item);
        } catch (Exception e){
            logger.error("Can't create new element: ", e);
            printError("Ошибка при попытке создать элемент: " + e.getMessage());
        } finally {
            descriptionForNewItem = "";
        }
    }

    public void applyChanges() {
        Session session = null;
        Transaction transaction = null;
        try {
            if (CollectionUtils.isEmpty(catalogListItem)) {
                throw new IllegalArgumentException("Element collection is null or is empty");
            }
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            Long nextVersion = service.getLastVersionCategoryItem(session) + 1L;
            Date updateDate = new Date();

            for (WtCategoryItem item : catalogListItem) {
                if(service.catalogItemIsChange(item, session)) {
                    item.setLastUpdate(updateDate);
                    item.setVersion(nextVersion);
                    session.merge(item);
                }
            }

            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            printError("Не удалось обновить элементы: " + e.getMessage());
            logger.error("Can't update elements", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public void refreshItems() {
        Session session = null;
        Transaction transaction = null;
        try {
            if (CollectionUtils.isEmpty(catalogListItem)) {
                throw new IllegalArgumentException("Element collection is null or is empty");
            }
            session = RuntimeContext.getInstance().createReportPersistenceSession();
            transaction = session.beginTransaction();

            for (WtCategoryItem item : catalogListItem) {
                session.refresh(item);
            }

            transaction.commit();
            transaction = null;
        } catch (Exception e) {
            printError("Не удалось восстановить элементы: " + e.getMessage());
            logger.error("Can't restore elements", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public void deleteItem() {
        try {
            if(selectedItem == null){
                throw new IllegalArgumentException("Selected item is null");
            }
            selectedItem.setDeleteState(WtCategoryItem.DELETE);
        } catch (Exception e){
            printError("Не удалось удалить элемент: " + e.getMessage());
            logger.error("Can't delete element", e);
        }
    }

    public void reestablishItem(){
        try {
            if(selectedItem == null){
                throw new IllegalArgumentException("Selected item is null");
            }
            selectedItem.setDeleteState(WtCategoryItem.ACTIVE);
        } catch (Exception e){
            printError("Не удалось удалить элемент: " + e.getMessage());
            logger.error("Can't delete element", e);
        }
    }

    public List<WtCategoryItem> getCatalogListItem() {
        return catalogListItem;
    }

    public void setCatalogListItem(List<WtCategoryItem> catalogListItem) {
        this.catalogListItem = catalogListItem;
    }

    public String getDescriptionFilter() {
        return descriptionFilter;
    }

    public void setDescriptionFilter(String descriptionFilter) {
        this.descriptionFilter = descriptionFilter;
    }

    public void updateCatalogList() {
        try{
            catalogListItem = service.findCategoryItemByDescription(descriptionFilter);
        }catch (Exception e){
            printError("Не удалось найти элементы: " + e.getMessage());
            logger.error("", e);
        }
    }

    public void dropAndReloadCatalogList() {
        descriptionFilter = "";
        catalogListItem = service.getAllActiveCategoryItem();
    }

    public String getDescriptionForNewItem() {
        return descriptionForNewItem;
    }

    public void setDescriptionForNewItem(String descriptionForNewItem) {
        this.descriptionForNewItem = descriptionForNewItem;
    }

    @Override
    public String getPageFilename() {
        return "service/webtechnologist/catalog_category_item_page";
    }

    public WtCategoryItem getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(WtCategoryItem selectedItem) {
        this.selectedItem = selectedItem;
    }
}
