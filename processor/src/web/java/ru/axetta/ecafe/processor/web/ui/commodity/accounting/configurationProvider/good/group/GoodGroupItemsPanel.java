/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.commodity.accounting.configurationProvider.good.group;

import ru.axetta.ecafe.processor.core.daoservices.context.ContextDAOServices;
import ru.axetta.ecafe.processor.core.persistence.User;
import ru.axetta.ecafe.processor.core.persistence.distributedobjects.products.GoodGroup;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicPage;
import ru.axetta.ecafe.processor.web.ui.MainPage;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
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

    private final static Logger logger = LoggerFactory.getLogger(GoodGroupItemsPanel.class);
    private final Queue<GoodGroupSelect> completeHandlerLists = new LinkedList<GoodGroupSelect>();

    private List<GoodGroup> goodGroupList;
    private GoodGroup selectGoodGroup;
    private String filter;

    @Autowired
    private DAOService daoService;
    @Autowired
    private ContextDAOServices contextDAOServices;

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
        return null;
    }

    @PostConstruct
    public void reload() throws Exception {
         goodGroupList = new ArrayList<GoodGroup>();
         filter="";
    }

    public Object updateConfigurationProviderSelectPage(){
        try {
            retrieveGoods();
        } catch (Exception e) {
            printError("Ошибка при загрузке страницы: "+e.getMessage());
            logger.error("Error load page", e);
        }
        return null;
    }

    private void retrieveGoods() throws Exception {
        User user = MainPage.getSessionInstance().getCurrentUser();
        List<Long> orgOwners = contextDAOServices.findOrgOwnersByContragentSet(user.getIdOfUser());
        if(orgOwners==null || orgOwners.isEmpty()){
            if (StringUtils.isEmpty(filter)){
                goodGroupList = daoService.findGoodGroupBySuplifier(false);
            } else{
                goodGroupList = daoService.findGoodGroupBySuplifier(filter);
            }
        } else {
            if (StringUtils.isEmpty(filter)){
                goodGroupList = daoService.findGoodGroupBySuplifier(orgOwners,false);
            } else{
                goodGroupList = daoService.findGoodGroupBySuplifier(orgOwners, filter);
            }
        }
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

    public void printError(String msg) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null,
                new FacesMessage(FacesMessage.SEVERITY_ERROR, msg, null));
    }
}
