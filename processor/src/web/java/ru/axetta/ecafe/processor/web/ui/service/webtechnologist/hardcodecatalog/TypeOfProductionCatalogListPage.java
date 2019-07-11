/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.webtechnologist.hardcodecatalog;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.webtechnologist.catalogs.hardcodecatalog.HardCodeCatalogService;
import ru.axetta.ecafe.processor.core.persistence.webtechnologist.catalogs.hardcodecatalog.WTTypeOfProductionItem;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private String GUIDfilter;
    private String descriptionForNewItem;
    private List<WTTypeOfProductionItem> catalogListItem;

    @Override
    public void onShow() throws Exception {
        HardCodeCatalogService service = RuntimeContext.getAppContext().getBean(HardCodeCatalogService.class);
        catalogListItem = service.getAllTypeOfProductionItems();
        GUIDfilter = "";
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
            HardCodeCatalogService service = RuntimeContext.getAppContext().getBean(HardCodeCatalogService.class);
            User currentUser = MainPage.getSessionInstance().getCurrentUser();
            WTTypeOfProductionItem item =  service.createProductTypeItem(descriptionForNewItem, currentUser);
            catalogListItem.add(item);
        } catch (Exception e){
            logger.error("Can't create new element: ", e);
            printError("Ошибка при попытке создать элемент: " + e.getMessage());
        }
    }

    public List<WTTypeOfProductionItem> getCatalogListItem() {
        return catalogListItem;
    }

    public void setCatalogListItem(List<WTTypeOfProductionItem> catalogListItem) {
        this.catalogListItem = catalogListItem;
    }

    public String getDescriptionFilter() {
        return descriptionFilter;
    }

    public void setDescriptionFilter(String descriptionFilter) {
        this.descriptionFilter = descriptionFilter;
    }

    public String getGUIDfilter() {
        return GUIDfilter;
    }

    public void setGUIDfilter(String GUIDfilter) {
        this.GUIDfilter = GUIDfilter;
    }

    public void updateCatalogList() {
        try{
            HardCodeCatalogService service = RuntimeContext.getAppContext().getBean(HardCodeCatalogService.class);
            catalogListItem = service.findProductionTypeItemsByDescriptionOrGUID(descriptionFilter, GUIDfilter);
        }catch (Exception e){
            printError("Не удалось найти элементы: " + e.getMessage());
            logger.error("", e);
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
}
