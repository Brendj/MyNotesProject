/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.technologicalMap.group;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMap;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMapGroup;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMapProduct;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.technologicalMap.technologicalMapProduct.ProductItemsPanel;
import ru.axetta.ecafe.processor.web.ui.option.technologicalMap.technologicalMapProduct.ProductSelect;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
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
public class TechnologicalMapGroupCreatePage extends BasicWorkspacePage {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TechnologicalMapGroupCreatePage.class);
    private TechnologicalMapGroup technologicalMapGroup = new TechnologicalMapGroup();
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void onShow() throws Exception {}

    public Object onSave(){
        try {
            technologicalMapGroup.setCreatedDate(new Date());
            technologicalMapGroup.setDeletedState(false);
            technologicalMapGroup.setGuid(UUID.randomUUID().toString());
            technologicalMapGroup.setGlobalVersion(0L);
            DAOService.getInstance().persistEntity(technologicalMapGroup);
            printMessage("Группа технологических карт сохранена успешно.");
        } catch (Exception e) {
            printError("Ошибка при созданиии группы технологических карт.");
            logger.trace("Error create Technological Map Group",e);
        }
        return null;
    }

    public String getPageFilename() {
        return "option/technologicalMap/group/create";
    }

    public TechnologicalMapGroup getTechnologicalMapGroup() {
        return technologicalMapGroup;
    }

    public void setTechnologicalMapGroup(TechnologicalMapGroup technologicalMapGroup) {
        this.technologicalMapGroup = technologicalMapGroup;
    }
}
