/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.daoservices.org.OrgShortItem;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;
import ru.axetta.ecafe.processor.web.ui.org.OrganizationListSelect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 24.09.13
 * Time: 13:38
 */

@Component
@Scope("session")
public class FullSyncRequestPage extends BasicWorkspacePage implements OrgListSelectPage.CompleteHandlerList {

    private static Logger logger = LoggerFactory.getLogger(FullSyncRequestPage.class);
    private List<Long> idOfOrgList = new ArrayList<Long>(0);

    @Autowired
    private DAOService daoService;

    private String filter = "Не выбрано";

    @Override
    public void onShow() throws Exception {
        filter = "Не выбрано";
        idOfOrgList.clear();
    }

    public Object applyFullSyncOperation(){
        try {
            daoService.applyFullSyncOperationByOrgList(idOfOrgList);
            printMessage("Запрос отправлен");
        } catch (Exception e){
            printError("Ошибка при сохранении данных: "+e.getMessage());
            logAndPrintMessage("Error by update full sync param",e);
        }
        return null;
    }

/*    @Override
    public void select(List<OrgShortItem> orgShortItem) {
        idOfOrgList.clear();
        if(orgShortItem.isEmpty()){
            filter = "Не выбрано";
        } else {
            for (OrgShortItem item: orgShortItem){
                idOfOrgList.add(item.getIdOfOrg());
                filter = filter.concat(item.getShortName() + "; ");
            }
        }
    }*/

    @Override
    public void completeOrgListSelection(Map<Long, String> orgMap) throws Exception {
        if (orgMap != null) {
            idOfOrgList = new ArrayList<Long>(orgMap.size());
            if (orgMap.isEmpty())
                filter = "Не выбрано";
            else {
                filter = "";
                for(Long idOfOrg : orgMap.keySet()) {
                    idOfOrgList.add(idOfOrg);
                    filter = filter.concat(orgMap.get(idOfOrg) + "; ");
                }
                filter = filter.substring(0, filter.length() - 2);
            }
        }
    }

    @Override
    public String getPageFilename() {
        return "service/full_sync_request";
    }

    public List<Long> getIdOfOrgList() {
        return idOfOrgList;
    }

    public void setIdOfOrgList(List<Long> idOfOrgList) {
        this.idOfOrgList = idOfOrgList;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

}
