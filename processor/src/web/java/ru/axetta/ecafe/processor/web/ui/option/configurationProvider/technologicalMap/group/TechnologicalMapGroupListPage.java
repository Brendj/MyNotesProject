/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.group;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMapGroup;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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
public class TechnologicalMapGroupListPage extends BasicWorkspacePage {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TechnologicalMapGroupCreatePage.class);
    private List<TechnologicalMapGroup> technologicalMapGroupList;
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void onShow() {
        try {
            RuntimeContext.getAppContext().getBean(getClass()).reload();
        } catch (Exception e) {
            printError("Ошибка при загрузке списка групп.");
        }
    }

    @Transactional
    private void reload() throws Exception{
        technologicalMapGroupList = entityManager.createQuery("FROM TechnologicalMapGroup ORDER BY globalId",TechnologicalMapGroup.class).getResultList();
    }

    public String getPageFilename() {
        return "option/technologicalMap/group/list";
    }

    public String getPageTitle() {
        return super.getPageTitle() + String.format(" (%d)", technologicalMapGroupList.size());
    }

    public List<TechnologicalMapGroup> getTechnologicalMapGroupList() {
        return technologicalMapGroupList;
    }

    public void setTechnologicalMapGroupList(List<TechnologicalMapGroup> technologicalMapGroupList) {
        this.technologicalMapGroupList = technologicalMapGroupList;
    }

    @Transactional
    private boolean getNullOrEmpty(){
        List<Long> tMGl = entityManager.createQuery("SELECT id FROM TechnologicalMapGroup",Long.class).getResultList();
        return tMGl==null || tMGl.isEmpty();
    }

    public boolean getNullOrEmptyGroup(){
        return getNullOrEmpty();
    }
}
