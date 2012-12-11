/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.configurationProvider.good.group;

import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodGroup;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.ProductGroup;
import ru.axetta.ecafe.processor.web.ui.BasicPage;

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
public class GoodGroupItemsPanel extends BasicPage {

    private final Queue<GoodGroupSelect> completeHandlerLists = new LinkedList<GoodGroupSelect>();

    private List<GoodGroup> goodGroupList;
    private GoodGroup selectGoodGroup;
    private String filter;

    @PersistenceContext
    private EntityManager entityManager;

    public void pushCompleteHandler(GoodGroupSelect handler) {
        completeHandlerLists.add(handler);
    }

    public Object addGoodGroup(){
        completeSelection(true);
        return null;
    }

    private void completeSelection(boolean flag){
        if (!completeHandlerLists.isEmpty()) {
            completeHandlerLists.peek().select(flag?selectGoodGroup:null);
            completeHandlerLists.poll();
        }
    }

    public Object cancel(){
        completeSelection(false);
        return null;
    }

    @PostConstruct
    public void reload() throws Exception {
         goodGroupList = new ArrayList<GoodGroup>();
         filter="";
    }

    public Object updateConfigurationProviderSelectPage(){
        goodGroupList = retrieveGoods();
        return null;
    }

    @Transactional
    private List<GoodGroup> retrieveGoods(){
        String where="";
        if (StringUtils.isNotEmpty(filter)){
            where = "where UPPER(NameOfGoodsGroup) like '%"+filter.toUpperCase()+"%'";
        }
        String query = "from GoodGroup "+ where + " order by id";
        return entityManager.createQuery(query, GoodGroup.class).getResultList();
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public List<GoodGroup> getGoodGroupList() {
        return goodGroupList;
    }

    public void setGoodGroupList(List<GoodGroup> goodGroupList) {
        this.goodGroupList = goodGroupList;
    }

    public GoodGroup getSelectGoodGroup() {
        return selectGoodGroup;
    }

    public void setSelectGoodGroup(GoodGroup selectGoodGroup) {
        this.selectGoodGroup = selectGoodGroup;
    }
}
