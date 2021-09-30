/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.technologicalMap;

import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.TechnologicalMap;
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
public class TechnologicalMapPanel extends BasicPage {

    private static final Logger logger = LoggerFactory.getLogger(TechnologicalMapPanel.class);
    private final Queue<TechnologicalMapSelect> completeHandlerLists = new LinkedList<TechnologicalMapSelect>();

    private List<TechnologicalMap> technologicalMapList;
    private TechnologicalMap selectTechnologicalMap;
    private String filter;

    @Autowired
    private DAOReadonlyService daoService;
    @Autowired
    private ContextDAOServices contextDAOServices;

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
    public void postConstruct() {
        technologicalMapList = new ArrayList<TechnologicalMap>();
        filter="";
    }

    public void reload() throws Exception {
         technologicalMapList = new ArrayList<TechnologicalMap>();
         filter="";
        retrieveTechnologicalMap();
    }

    public Object updateTechnologicalMapSelectPage(){
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
                technologicalMapList = daoService.findTechnologicalMapByConfigurationProvider(filter);
            }else {
                technologicalMapList = daoService.findTechnologicalMapByConfigurationProvider(false);
            }
        } else {
            if (StringUtils.isEmpty(filter)){
                technologicalMapList = daoService.findTechnologicalMapByConfigurationProvider(orgOwners, filter);
            }else{
                technologicalMapList = daoService.findTechnologicalMapByConfigurationProvider(orgOwners, false);
            }
        }
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

    public void printError(String msg) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }
}
