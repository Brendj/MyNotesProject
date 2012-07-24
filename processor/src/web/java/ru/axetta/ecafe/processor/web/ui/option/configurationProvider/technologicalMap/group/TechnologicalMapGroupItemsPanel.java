/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap.group;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.ProductGroup;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.TechnologicalMapGroup;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.group.ProductGroupSelect;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.07.12
 * Time: 13:29
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class TechnologicalMapGroupItemsPanel extends BasicPage {

    private final Stack<TechnologicalMapGroupSelect> completeHandlerLists = new Stack<TechnologicalMapGroupSelect>();

    private List<TechnologicalMapGroup> technologicalMapGroupList;
    private TechnologicalMapGroup selectTechnologicalMapGroup;
    private String filter;

    @PersistenceContext
    private EntityManager entityManager;

    public void pushCompleteHandler(TechnologicalMapGroupSelect handler) {
        completeHandlerLists.push(handler);
    }

    public Object addTechnologicalMapGroup(){
        if (!completeHandlerLists.empty()) {
            completeHandlerLists.peek().select(selectTechnologicalMapGroup);
            completeHandlerLists.pop();
        }
        return null;
    }

    @PostConstruct
    public void reload() throws Exception {
         technologicalMapGroupList = new ArrayList<TechnologicalMapGroup>();
         selectTechnologicalMapGroup = new TechnologicalMapGroup();
         filter="";
    }

    public Object updateConfigurationProviderSelectPage(){
        technologicalMapGroupList = retrieveTechnologicalMap();
        return null;
    }

    @Transactional
    private List<TechnologicalMapGroup> retrieveTechnologicalMap(){
        String where="";
        if (StringUtils.isNotEmpty(filter)){
            where = "where UPPER(nameOfGroup) like '%"+filter.toUpperCase()+"%'";
        }
        String query = "from TechnologicalMapGroup "+ where + " order by id";
        return entityManager.createQuery(query, TechnologicalMapGroup.class).getResultList();
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public TechnologicalMapGroup getSelectTechnologicalMapGroup() {
        return selectTechnologicalMapGroup;
    }

    public void setSelectTechnologicalMapGroup(TechnologicalMapGroup selectTechnologicalMapGroup) {
        this.selectTechnologicalMapGroup = selectTechnologicalMapGroup;
    }

    public List<TechnologicalMapGroup> getTechnologicalMapGroupList() {
        return technologicalMapGroupList;
    }

    public void setTechnologicalMapGroupList(List<TechnologicalMapGroup> technologicalMapGroupList) {
        this.technologicalMapGroupList = technologicalMapGroupList;
    }
}
