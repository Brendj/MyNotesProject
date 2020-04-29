/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.webtechnologist.catalog;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtAgeGroupItem;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.services.WtCatalogService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Scope("session")
@DependsOn("runtimeContext")
public class AgeGroupCatalogListPage extends BasicWorkspacePage {
    private static final Logger logger = LoggerFactory.getLogger(AgeGroupCatalogListPage.class);

    private String descriptionFilter;
    private String descriptionForNewItem;
    private List<WtAgeGroupItem> catalogListItem;
    private WtAgeGroupItem selectedItem;

    private WtCatalogService service;

    @Autowired
    public void setService(WtCatalogService service) {
        this.service = service;
    }

    @Override
    public void onShow() throws Exception {
        catalogListItem = service.getAllAgeGroupItems();
        descriptionForNewItem = "";
        descriptionFilter = "";
    }

    public void clearDescriptionForNewCatalog() {
        descriptionForNewItem = "";
    }

    public void createNewItem() {
        if (StringUtils.isBlank(descriptionForNewItem)) {
            printError("ВВедите описание элемента");
        }
        try {
            User currentUser = MainPage.getSessionInstance().getCurrentUser();
            WtAgeGroupItem item = service.createAgeGroupItem(descriptionForNewItem, currentUser);
            catalogListItem.add(item);
        } catch (Exception e) {
            logger.error("Can't create new element: ", e);
            printError("Ошибка при попытке создать элемент: " + e.getMessage());
        } finally {
            descriptionForNewItem = "";
        }
    }

    public List<WtAgeGroupItem> getCatalogListItem() {
        return catalogListItem;
    }

    public void setCatalogListItem(List<WtAgeGroupItem> catalogListItem) {
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
            catalogListItem = service.findAgeGroupItemsByDescription(descriptionFilter);
        } catch (Exception e){
            printError("Не удалось нати элементы: " + e.getMessage());
            logger.error("", e);
        }
    }

    public void deleteItem() {
        Session session = null;
        Transaction transaction = null;
        try {
            if(selectedItem == null){
                throw new IllegalArgumentException("Selected item is null");
            }
            session = RuntimeContext.getInstance().createPersistenceSession();
            transaction = session.beginTransaction();

            Query query = session.createQuery("DELETE WtAgeGroupItem WHERE idOfAgeGroupItem = :idOfAgeGroupItem");
            query.setParameter("idOfAgeGroupItem", selectedItem.getIdOfAgeGroupItem());
            query.executeUpdate();
            catalogListItem.remove(selectedItem);

            transaction.commit();
            transaction = null;

        } catch (Exception e){
            printError("Не удалось удалить элемент: " + e.getMessage());
            logger.error("Can't delete element", e);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
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

            Long nextVersion = service.getLastVersionAgeGroup(session) + 1L;

            for (WtAgeGroupItem item : catalogListItem) {
                item.setVersion(nextVersion);
                session.merge(item);
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

            for (WtAgeGroupItem item : catalogListItem) {
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

    public void dropAndReloadCatalogList() {
        descriptionFilter = "";
        catalogListItem = service.getAllAgeGroupItems();
    }

    public String getDescriptionForNewItem() {
        return descriptionForNewItem;
    }

    public void setDescriptionForNewItem(String descriptionForNewItem) {
        this.descriptionForNewItem = descriptionForNewItem;
    }

    @Override
    public String getPageFilename() {
        return "service/webtechnologist/catalog_age_group_page";
    }

    public WtAgeGroupItem getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(WtAgeGroupItem selectedItem) {
        this.selectedItem = selectedItem;
    }
}
