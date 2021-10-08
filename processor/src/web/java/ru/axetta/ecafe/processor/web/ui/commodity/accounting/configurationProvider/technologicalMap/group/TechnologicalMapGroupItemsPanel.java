/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap.group;

import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMapGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
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
public class TechnologicalMapGroupItemsPanel extends BasicPage {

    private final static Logger logger = LoggerFactory.getLogger(TechnologicalMapGroupItemsPanel.class);
    private final Queue<TechnologicalMapGroupSelect> completeHandlerLists = new LinkedList<TechnologicalMapGroupSelect>();

    private List<TechnologicalMapGroup> technologicalMapGroupList;
    private TechnologicalMapGroup selectTechnologicalMapGroup;
    private String filter;
    @Autowired
    private DAOReadonlyService daoService;
    @Autowired
    private ContextDAOServices contextDAOServices;

    public void pushCompleteHandler(TechnologicalMapGroupSelect handler) {
        completeHandlerLists.add(handler);
    }

    public Object addTechnologicalMapGroup(){
        completeSelection(true);
        return null;
    }

    private void completeSelection(boolean flag){
        if (!completeHandlerLists.isEmpty()) {
            completeHandlerLists.peek().select(flag?selectTechnologicalMapGroup:null);
            completeHandlerLists.poll();
        }
    }

    public Object cancel(){
        completeSelection(false);
        return null;
    }

    @PostConstruct
    public void postConstruct() {
        technologicalMapGroupList = new ArrayList<TechnologicalMapGroup>();
        selectTechnologicalMapGroup = new TechnologicalMapGroup();
        filter="";
    }

    public void reload() throws Exception {
         technologicalMapGroupList = new ArrayList<TechnologicalMapGroup>();
         selectTechnologicalMapGroup = new TechnologicalMapGroup();
         filter="";
        retrieveTechnologicalMap();
    }

    public Object updateConfigurationProviderSelectPage(){
        try {
            retrieveTechnologicalMap();
        } catch (Exception e) {
            printError("Ошибка при загрузке страницы: "+e.getMessage());
            logger.error("Error load page", e);
        }
        return null;
    }

    private void retrieveTechnologicalMap() throws Exception {
        User user = MainPage.getSessionInstance().getCurrentUser();
        List<Long> orgOwners = contextDAOServices.findOrgOwnersByContragentSet(user.getIdOfUser());
        if(orgOwners==null || orgOwners.isEmpty()){
            if (StringUtils.isEmpty(filter)){
                technologicalMapGroupList = daoService.findTechnologicalMapGroupByConfigurationProvider(filter);
            } else {
                technologicalMapGroupList = daoService.findTechnologicalMapGroupByConfigurationProvider(false);
            }
        } else {
            if (StringUtils.isEmpty(filter)){
                technologicalMapGroupList = daoService.findTechnologicalMapGroupByConfigurationProvider(orgOwners, filter);
            } else {
                technologicalMapGroupList = daoService.findTechnologicalMapGroupByConfigurationProvider(orgOwners, false);
            }
        }
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

    public void printError(String msg) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }
}
