/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.technologicalMap;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.Product;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMap;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.option.configurationProvider.product.ProductSelect;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 03.07.12
 * Time: 13:29
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class TechnologicalMapPanel extends BasicPage {

    private final Queue<TechnologicalMapSelect> completeHandlerLists = new LinkedList<TechnologicalMapSelect>();

    private List<TechnologicalMap> technologicalMapList;
    private TechnologicalMap selectTechnologicalMap;
    private String filter;

    @PersistenceContext
    private EntityManager entityManager;

    public void pushCompleteHandler(TechnologicalMapSelect handler) {
        completeHandlerLists.add(handler);
    }

    public Object addTechnologicalMap(){
        completeSelection(true);
        return null;
    }

    private void completeSelection(boolean flag){
        if (!completeHandlerLists.isEmpty()) {
            completeHandlerLists.peek().select(flag?selectTechnologicalMap:null);
            completeHandlerLists.poll();
        }
    }

    public Object cancel(){
        completeSelection(false);
        return null;
    }

    @PostConstruct
    public void reload() throws Exception {
         technologicalMapList = new ArrayList<TechnologicalMap>();
         filter="";
    }

    public Object updateTechnologicalMapSelectPage(){
        technologicalMapList = retrieveTechnologicalMap();
        return null;
    }

    @Transactional
    private List<TechnologicalMap> retrieveTechnologicalMap(){
        String where="";
        if (StringUtils.isNotEmpty(filter)){
            where = "where UPPER(nameOfTechnologicalMap) like '%"+filter.toUpperCase()+"%'";
        }
        String query = "from TechnologicalMap "+ where + " order by id";
        return entityManager.createQuery(query, TechnologicalMap.class).getResultList();
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public List<TechnologicalMap> getTechnologicalMapList() {
        return technologicalMapList;
    }

    public void setTechnologicalMapList(List<TechnologicalMap> technologicalMapList) {
        this.technologicalMapList = technologicalMapList;
    }

    public TechnologicalMap getSelectTechnologicalMap() {
        return selectTechnologicalMap;
    }

    public void setSelectTechnologicalMap(TechnologicalMap selectTechnologicalMap) {
        this.selectTechnologicalMap = selectTechnologicalMap;
    }
}
