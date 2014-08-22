/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;

import org.apache.commons.collections.CollectionUtils;
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
    private List<Long> idOfOrgList = new ArrayList<Long>();

    @Autowired
    private DAOService daoService;

    private String filter = "Не выбрано";

    @Override
    public void onShow() throws Exception {
//        filter = "Не выбрано";
//        idOfOrgList.clear();
    }

    public Object applyFullSyncOperation(){
        try {
            if (!CollectionUtils.isEmpty(idOfOrgList)) {
                daoService.applyFullSyncOperationByOrgList(idOfOrgList);
                printMessage("Запрос отправлен");
            } else {
                printError("Не выбрана организация");
            }
        } catch (Exception e){
            printError("Ошибка при сохранении данных: "+e.getMessage());
            logAndPrintMessage("Error by update full sync param",e);
        }
        return null;
    }

    @Override
    public void completeOrgListSelection(Map<Long, String> orgMap) throws Exception {
        if (orgMap != null) {
            idOfOrgList = new ArrayList<>();
            if (orgMap.isEmpty())
                filter = "Не выбрано";
            else {
                StringBuilder stringBuilder = new StringBuilder();
                for(Long idOfOrg : orgMap.keySet()) {
                    idOfOrgList.add(idOfOrg);
                    stringBuilder.append(orgMap.get(idOfOrg)).append("; ");
                }
                filter = stringBuilder.substring(0, stringBuilder.length() - 2);
            }
        }
    }

    public String getGetStringIdOfOrgList() {
        return idOfOrgList.toString().replaceAll("[^0-9]]", "");
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
