/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.technologicalMap;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMap;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMapProduct;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.ConfirmDeletePage;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 01.06.12
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class TechnologicalMapListPage extends BasicWorkspacePage implements ConfirmDeletePage.Listener  {

    @Override
    public void onConfirmDelete(ConfirmDeletePage confirmDeletePage) {
        DAOService.getInstance().deleteEntity(confirmDeletePage.getEntity());
        RuntimeContext.getAppContext().getBean(getClass()).reload();
    }

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TechnologicalMapListPage.class);
    private TechnologicalMap technologicalMap = new TechnologicalMap();
    private List<TechnologicalMap> technologicalMapList;

    public List<TechnologicalMap> getTechnologicalMapList() {
        return technologicalMapList;
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", technologicalMapList.size());
    }

    @PersistenceContext
    EntityManager em;

    @Override
    public void onShow() throws Exception {
        RuntimeContext.getAppContext().getBean(getClass()).reload();
    }

    public void reload() {
        technologicalMapList = DAOService.getInstance().getDistributedObjects(TechnologicalMap.class);
    }


    public String getPageFilename() {
        return "option/technologicalMap/list";
    }

    public TechnologicalMap getTechnologicalMap() {
        return technologicalMap;
    }

}
