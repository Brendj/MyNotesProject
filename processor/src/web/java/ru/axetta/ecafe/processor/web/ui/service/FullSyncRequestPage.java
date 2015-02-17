/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service;

import ru.axetta.ecafe.processor.core.daoservices.contragent.ContragentDAOService;
import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.MainPage;
import ru.axetta.ecafe.processor.web.ui.ccaccount.CCAccountFilter;
import ru.axetta.ecafe.processor.web.ui.contragent.ContragentSelectPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;
import ru.axetta.ecafe.processor.web.ui.report.repository.ReportRepositoryItem;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.Arrays;
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
public class FullSyncRequestPage extends BasicWorkspacePage implements OrgListSelectPage.CompleteHandlerList, ContragentSelectPage.CompleteHandler {

    private static Logger logger = LoggerFactory.getLogger(FullSyncRequestPage.class);

    @PersistenceContext(unitName = "reportsPU")
    private EntityManager entityManager;

    private List<Long> idOfOrgList = new ArrayList<Long>();
    private ContragentDAOService contragentDAOService = new ContragentDAOService();

    private Contragent defaultSupplier;

    public Contragent getDefaultSupplier() {
        return defaultSupplier;
    }

    private boolean selectReceiver;

    public boolean isSelectReceiver() {
        return selectReceiver;
    }

    public void setSelectReceiver(boolean selectReceiver) {
        this.selectReceiver = selectReceiver;
    }

    ReportRepositoryItem.Filter filterReceiver = new ReportRepositoryItem.Filter();

    public ReportRepositoryItem.Filter getFilterReceiver() {
        return filterReceiver;
    }

    @Autowired
    private DAOService daoService;

    private String filter = "Не выбрано";

    @Override
    public void onShow() throws Exception {
        contragentDAOService.setSession((Session) entityManager.getDelegate());
//        filter = "Не выбрано";
//        idOfOrgList.clear();
    }

    public Object applyFullSyncOperation(){
        try {
            if (!CollectionUtils.isEmpty(idOfOrgList)||defaultSupplier != null) {
                List<Org> orgItems = null;
                if (!CollectionUtils.isEmpty(idOfOrgList)) {
                daoService.applyFullSyncOperationByOrgList(idOfOrgList);
                printMessage("Запрос отправлен");
                } else if (defaultSupplier != null) {
                    orgItems = contragentDAOService.findDistributionOrganizationByDefaultSupplier(defaultSupplier);
                    List<Long> idOfOrgItemList = new ArrayList<Long>();
                    for (Org orgItem: orgItems) {
                        idOfOrgItemList.add(orgItem.getIdOfOrg());
                    }
                    daoService.applyFullSyncOperationByOrgList(idOfOrgItemList);
                    printMessage("Запрос отправлен");

                }
            } else {
                printError("Не выбран Поставщик или Организация ");
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
            idOfOrgList = new ArrayList<Long>();
            if (orgMap.isEmpty())  {
                filter = "Не выбрано";
            } else {
                filter = "";
                StringBuilder stringBuilder = new StringBuilder();
                for(Long idOfOrg : orgMap.keySet()) {
                    idOfOrgList.add(idOfOrg);
                    stringBuilder.append(orgMap.get(idOfOrg)).append("; ");
                }
                filter = stringBuilder.substring(0, stringBuilder.length() - 1);
            }
        }
    }

    @Override
    public String getPageFilename() {
        return "service/full_sync_request";
    }

    public String getIdOfOrgList() {
        return idOfOrgList.toString().replaceAll("[^0-9,]","");
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

    @Override
    public void completeContragentSelection(Session session, Long idOfContragent, int multiContrFlag, String classTypes)
            throws Exception {
        if (idOfContragent != null) {
            this.defaultSupplier = (Contragent) session.get(Contragent.class, idOfContragent);
            filterReceiver.setIdOfContragentReceiver(idOfContragent);
        }
    }

    public Object showOrgListSelectPage() {
        if (defaultSupplier != null) {
            MainPage.getSessionInstance().setIdOfContragentList(Arrays.asList(defaultSupplier.getIdOfContragent()));
        }
        MainPage.getSessionInstance().showOrgListSelectPage();
        return null;
    }
}
