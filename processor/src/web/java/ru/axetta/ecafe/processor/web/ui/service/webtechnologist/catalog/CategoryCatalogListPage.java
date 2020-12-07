/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.webtechnologist.catalog;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtCategory;
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

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

@Component
@Scope("session")
@DependsOn("runtimeContext")
public class CategoryCatalogListPage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(CategoryCatalogListPage.class);

    private String descriptionFilter;
    private String descriptionForNewItem;
    private String descriptionForNewCategory;
    private List<WtCategory> catalogListItem;
    private List<WtCategoryItem> categoryItemsSelectedCategory;
    private WtCategory selectedItem;
    private WtCategoryItem selectedCategoryItem;

    private WtCatalogService service;

    @Autowired
    public void setService(WtCatalogService service) {
        this.service = service;
    }

    @Override
    public void onShow() throws Exception {
        catalogListItem = service.getAllActiveCategory();
        descriptionForNewItem = "";
        descriptionFilter = "";
    }

    public void createNewCatalog() {
        if (StringUtils.isBlank(descriptionForNewCategory)) {
            printError("Введите описание категории");
            return;
        }
        descriptionForNewCategory = deleteExcessWhitespace(descriptionForNewCategory);
        try {
            User currentUser = MainPage.getSessionInstance().getCurrentUser();
            WtCategory item = WtCategory.build(descriptionForNewCategory, currentUser);
            catalogListItem.add(item);
        } catch (Exception e) {
            logger.error("Can't create new element: ", e);
            printError("Ошибка при попытке создать категорию: " + e.getMessage());
        } finally {
            descriptionForNewCategory = "";
        }
    }


    public void createNewItem() {
        if (StringUtils.isBlank(descriptionForNewItem)) {
            printError("Введите описание элемента");
            return;
        }
        descriptionForNewItem = deleteExcessWhitespace(descriptionForNewItem);
        try {
            User currentUser = MainPage.getSessionInstance().getCurrentUser();
            WtCategoryItem item = WtCategoryItem.build(descriptionForNewItem, selectedItem, currentUser);
            selectedItem.getCategoryItems().add(item);
        } catch (Exception e) {
            logger.error("Can't create new element: ", e);
            printError("Ошибка при попытке создать элемент: " + e.getMessage());
        } finally {
            descriptionForNewItem = "";
        }
    }

    private String deleteExcessWhitespace(String str) {
        str = str.trim();
        return str.replaceAll("\\s+", " ");
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

            Long nextVersionForCategory = service.getLastVersionCategory(session) + 1L;
            Long nextVersionForItem = service.getLastVersionCategoryItem(session) + 1L;
            Date updateDate = new Date();

            for (WtCategory item : catalogListItem) {
                if (item.getIdOfCategory() == null && item.getDeleteState().equals(WtCategory.ACTIVE)) {
                    item.setVersion(nextVersionForCategory);
                    for (WtCategoryItem categoryItem : item.getCategoryItems()){
                        categoryItem.setVersion(nextVersionForCategory);
                    }
                    session.save(item);
                } else {
                    WtCategory categoryFromBD = (WtCategory) session.get(WtCategory.class, item.getIdOfCategory());
                    if (!categoryFromBD.equals(item)) {
                        for (WtCategoryItem categoryItem : item.getCategoryItems()) {
                            if (categoryItem.getIdOfCategoryItem() == null && categoryItem.getDeleteState()
                                    .equals(WtCategoryItem.ACTIVE)) {
                                categoryItem.setVersion(nextVersionForCategory);
                                session.save(categoryItem);
                            } else {
                                WtCategoryItem itemFromBD = (WtCategoryItem) session
                                        .get(WtCategoryItem.class, categoryItem.getIdOfCategoryItem());
                                if (!itemFromBD.equals(categoryItem)) {
                                    categoryItem.setLastUpdate(updateDate);
                                    categoryItem.setVersion(nextVersionForItem);
                                    session.merge(categoryItem);
                                }
                            }
                        }
                        item.setLastUpdate(updateDate);
                        item.setVersion(nextVersionForCategory);
                        session.merge(item);
                    }
                }
            }

            transaction.commit();
            transaction = null;
            catalogListItem = service.getAllActiveCategory();
            categoryItemsSelectedCategory = Collections.emptyList();
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

            for (WtCategory item : catalogListItem) {
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

    public List<WtCategoryItem> getItemsForSelectedItem() {
        if (selectedItem == null) {
            categoryItemsSelectedCategory = Collections.emptyList();
        } else {
            categoryItemsSelectedCategory = new LinkedList<>(selectedItem.getCategoryItems());
        }
        return categoryItemsSelectedCategory;
    }

    public void deleteCategory() {
        try {
            if (selectedItem == null) {
                throw new IllegalArgumentException("Selected item is null");
            }
            selectedItem.setDeleteState(WtCategory.DELETE);
            for (WtCategoryItem categoryItem : selectedItem.getCategoryItems()){
                categoryItem.setDeleteState(WtCategoryItem.DELETE);
            }
        } catch (Exception e) {
            printError("Не удалось удалить элемент: " + e.getMessage());
            logger.error("Can't delete element", e);
        }
    }

    public void reestablishCategory() {
        try {
            if (selectedItem == null) {
                throw new IllegalArgumentException("Selected item is null");
            }
            selectedItem.setDeleteState(WtCategory.ACTIVE);
        } catch (Exception e) {
            printError("Не удалось удалить элемент: " + e.getMessage());
            logger.error("Can't delete element", e);
        }
    }

    public List<WtCategory> getCatalogListItem() {
        return catalogListItem;
    }

    public void setCatalogListItem(List<WtCategory> catalogListItem) {
        this.catalogListItem = catalogListItem;
    }

    public String getDescriptionFilter() {
        return descriptionFilter;
    }

    public void setDescriptionFilter(String descriptionFilter) {
        this.descriptionFilter = descriptionFilter;
    }

    public void updateCatalogList() {
        try {
            catalogListItem = service.findCategoryByDescription(descriptionFilter);
        } catch (Exception e) {
            printError("Не удалось найти элементы: " + e.getMessage());
            logger.error("", e);
        }
    }

    public void dropAndReloadCatalogList() {
        descriptionFilter = "";
        catalogListItem = service.getAllActiveCategory();
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

    public WtCategory getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(WtCategory selectedItem) {
        this.selectedItem = selectedItem;
    }

    public String getDescriptionForNewCategory() {
        return descriptionForNewCategory;
    }

    public void setDescriptionForNewCategory(String descriptionForNewCategory) {
        this.descriptionForNewCategory = descriptionForNewCategory;
    }

    public List<WtCategoryItem> getCategoryItemsSelectedCategory() {
        return categoryItemsSelectedCategory;
    }

    public void setCategoryItemsSelectedCategory(List<WtCategoryItem> categoryItemsSelectedCategory) {
        this.categoryItemsSelectedCategory = categoryItemsSelectedCategory;
    }

    public void deleteItem() {
        try {
            if (selectedCategoryItem == null) {
                throw new IllegalArgumentException("Selected item is null");
            }
            selectedCategoryItem.setDeleteState(WtCategoryItem.DELETE);
        } catch (Exception e) {
            printError("Не удалось удалить элемент: " + e.getMessage());
            logger.error("Can't delete element", e);
        }
    }

    public void reestablishItem() {
        try {
            if (selectedCategoryItem == null) {
                throw new IllegalArgumentException("Selected item is null");
            }
            selectedCategoryItem.setDeleteState(WtCategoryItem.ACTIVE);
        } catch (Exception e) {
            printError("Не удалось удалить элемент: " + e.getMessage());
            logger.error("Can't delete element", e);
        }
    }

    public WtCategoryItem getSelectedCategoryItem() {
        return selectedCategoryItem;
    }

    public void setSelectedCategoryItem(WtCategoryItem selectedCategoryItem) {
        this.selectedCategoryItem = selectedCategoryItem;
    }
}
