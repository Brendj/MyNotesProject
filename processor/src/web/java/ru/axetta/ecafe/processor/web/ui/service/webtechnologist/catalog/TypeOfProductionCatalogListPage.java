/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.webtechnologist.catalog;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.webTechnologist.WtTypeOfProductionItem;
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
public class TypeOfProductionCatalogListPage extends BasicWorkspacePage {
    private static final Logger logger = LoggerFactory.getLogger(TypeOfProductionCatalogListPage.class);

    private String descriptionFilter;
    private String descriptionForNewItem;
    private List<WtTypeOfProductionItem> catalogListItem;
    private WtTypeOfProductionItem selectedItem;

    private WtCatalogService service;

    @Autowired
    public void setService(WtCatalogService service) {
        this.service = service;
    }

    @Override
    public void onShow() throws Exception {
        catalogListItem = service.getAllTypeOfProductionItems();
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
            WtTypeOfProductionItem item =  service.createProductTypeItem(descriptionForNewItem, currentUser);
            catalogListItem.add(item);
        } catch (Exception e){
            logger.error("Can't create new element: ", e);
            printError("Ошибка при попытке создать элемент: " + e.getMessage());
        } finally {
            descriptionForNewItem = "";
        }
    }

    public List<WtTypeOfProductionItem> getCatalogListItem() {
        return catalogListItem;
    }

    public void setCatalogListItem(List<WtTypeOfProductionItem> catalogListItem) {
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
            catalogListItem = service.findProductionTypeItemsByDescription(descriptionFilter);
        }catch (Exception e){
            printError("Не удалось найти элементы: " + e.getMessage());
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

            Query query = session.createQuery("DELETE WtTypeOfProductionItem WHERE idOfTypeProductionItem = :idOfTypeProductionItem");
            query.setParameter("idOfTypeProductionItem", selectedItem.getIdOfTypeProductionItem());
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

            Long nextVersion = service.getLastVersionProductionType(session) + 1L;

            for (WtTypeOfProductionItem item : catalogListItem) {
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

            for (WtTypeOfProductionItem item : catalogListItem) {
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
    }

    public String getDescriptionForNewItem() {
        return descriptionForNewItem;
    }

    public void setDescriptionForNewItem(String descriptionForNewItem) {
        this.descriptionForNewItem = descriptionForNewItem;
    }

    @Override
    public String getPageFilename() {
        return "service/webtechnologist/catalog_type_of_prod_page";
    }

    public WtTypeOfProductionItem getSelectedItem() {
        return selectedItem;
    }

    public void setSelectedItem(WtTypeOfProductionItem selectedItem) {
        this.selectedItem = selectedItem;
    }
}
