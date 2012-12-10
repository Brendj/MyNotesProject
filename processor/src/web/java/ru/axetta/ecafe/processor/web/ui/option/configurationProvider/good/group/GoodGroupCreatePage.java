/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.good.group;

import ru.axetta.ecafe.processor.core.persistence.ConfigurationProvider;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodGroup;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.ProductGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderItemsPanel;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.ConfigurationProviderSelect;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Session;
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
public class GoodGroupCreatePage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler {

    private static final Logger logger = LoggerFactory.getLogger(GoodGroupCreatePage.class);
    private GoodGroup goodGroup;
    private Org org;
    @Autowired
    private DAOService daoService;

    @Override
    public void onShow() throws Exception {
        goodGroup = new GoodGroup();
    }

    public Object onSave(){
        try {
            if(org==null){
                printError("Поле 'Организация поставщик' обязательное.");
                return null;
            }
            if(goodGroup.getNameOfGoodsGroup() == null || goodGroup.getNameOfGoodsGroup().equals("")){
                printError("Поле 'Наименование группы' обязательное.");
                return null;
            }
            goodGroup.setCreatedDate(new Date());
            goodGroup.setDeletedState(false);
            goodGroup.setGuid(UUID.randomUUID().toString());
            goodGroup.setOrgOwner(org.getIdOfOrg());
            goodGroup.setGlobalVersion(daoService.updateVersionByDistributedObjects(GoodGroup.class.getSimpleName()));
            daoService.persistEntity(goodGroup);
            printMessage("Группа сохранена успешно.");
        } catch (Exception e) {
            printError("Ошибка при созданиии группы.");
            logger.error("Error create good group",e);
        }
        return null;
    }

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (null != idOfOrg) {
            org = daoService.findOrById(idOfOrg);
        }
    }

    public String getPageFilename() {
        return "option/configuration_provider/good/group/create";
    }

    public String getShortName() {
        return (org == null?"":this.org.getShortName());
    }

    public GoodGroup getGoodGroup() {
        return goodGroup;
    }

    public void setGoodGroup(GoodGroup goodGroup) {
        this.goodGroup = goodGroup;
    }
}
