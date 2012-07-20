/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.group;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ProductGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 01.06.12
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class ProductGroupCreatePage extends BasicWorkspacePage {

    private static final Logger logger = LoggerFactory.getLogger(ProductGroupCreatePage.class);
    private ProductGroup productGroup;
    @Autowired
    private DAOService daoService;

    @Override
    public void onShow() throws Exception {
        productGroup = new ProductGroup();
    }

    public Object onSave(){
        try {
            if(productGroup.getNameOfGroup() == null || productGroup.getNameOfGroup().equals("")){
                printError("Поле 'Наименование группы' обязательное.");
                return null;
            }
            productGroup.setCreatedDate(new Date());
            productGroup.setDeletedState(false);
            productGroup.setGuid(UUID.randomUUID().toString());

            productGroup.setGlobalVersion(daoService.getVersionByDistributedObjects(ProductGroup.class));
            daoService.persistEntity(productGroup);
            printMessage("Группа сохранена успешно.");
        } catch (Exception e) {
            printError("Ошибка при созданиии группы.");
            logger.error("Error create product group",e);
        }
        return null;
    }

    public String getPageFilename() {
        return "option/configuration_provider/product/group/create";
    }

    public ProductGroup getProductGroup() {
        return productGroup;
    }

    public void setProductGroup(ProductGroup productGroup) {
        this.productGroup = productGroup;
    }
}
