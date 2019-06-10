/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.webtechnologist;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.webtechnologist.WebTechnologistCatalogService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component("webTechnologistCatalogCreatePage")
@Scope("session")
public class CatalogCreatePage extends BasicWorkspacePage {
    private static Logger logger = LoggerFactory.getLogger(CatalogCreatePage.class);

    private String catalogName;
    private Long idOfNewCatalog;

    public String getCatalogName() {
        return catalogName;
    }

    public void setCatalogName(String catalogName) {
        this.catalogName = catalogName;
    }

    public void createCatalog() {
        if(StringUtils.isEmpty(catalogName)){
            printError("Введите имя справочника");
            return;
        }
        try {
            User currentUser = MainPage.getSessionInstance().getCurrentUser();
            WebTechnologistCatalogService service = RuntimeContext.getAppContext()
                    .getBean(WebTechnologistCatalogService.class);
            idOfNewCatalog = service.createNewCatalog(catalogName, currentUser);
        } catch (Exception e){
            logger.error("Can't create new catalog: ", e);
            printError("Не удалось создать новый справочник: " + e.getMessage());
        }
    }

    public Long getIdOfNewCatalog() {
        return idOfNewCatalog;
    }

    public void setIdOfNewCatalog(Long idOfNewCatalog) {
        this.idOfNewCatalog = idOfNewCatalog;
    }

    @Override
    public String getPageFilename(){
        return "service/webtechnologist/catalog_create";
    }
}
