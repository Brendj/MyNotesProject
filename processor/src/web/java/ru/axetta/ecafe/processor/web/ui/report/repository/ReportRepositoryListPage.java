/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.repository;


import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ReportInfo;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractListPage;
import ru.axetta.ecafe.processor.web.ui.org.OrgListSelectPage;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.model.SelectItem;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Scope("session")
public class ReportRepositoryListPage extends AbstractListPage<ReportInfo, ReportRepositoryItem> implements OrgListSelectPage.CompleteHandlerList  {
    BasicWorkspacePage groupPage = new BasicWorkspacePage();
    ReportRepositoryItem.Filter filter = new ReportRepositoryItem.Filter();
    
    File fileToDownload;
    private ReportRepositoryItem selectedItem;
    private String displayedError;

    @Override
    protected String getPageFileName() {
        return "report/repository/report_repository_list";
    }

    @Override
    protected Class<ReportInfo> getEntityClass() {
        return ReportInfo.class;
    }

    @Override
    protected ReportRepositoryItem createItem() {
        return new ReportRepositoryItem();
    }

    @Override
    protected String getSortField() {
        return "createdDate";
    }

    public BasicWorkspacePage getGroupPage() {
        return groupPage;
    }

    @Override
    public ReportRepositoryItem.Filter getFilter() {
        return filter;
    }

    public SelectItem[] getRuleNameItems() {
        List<String> l = DAOReadonlyService.getInstance().getReportHandleRuleNames();
        SelectItem[] items = new SelectItem[l.size()];
        int n=0;
        for (String s : l) {
            items[n]=new SelectItem(s, s);
            ++n;
        }                                 
        return items;
    }

    public Object downloadReportFile() {
        return "downloadReportFile";
    }

    public File getFileToDownload() {
        return new File(RuntimeContext.getInstance().getAutoReportGenerator().getReportPath()+selectedItem.getReportFile());
    }

    public void setSelectedItem(ReportRepositoryItem selectedItem) {
        this.selectedItem = selectedItem;
    }

    public ReportRepositoryItem getSelectedItem() {
        return selectedItem;
    }

    public boolean isCanDelete() throws Exception {
        return ru.axetta.ecafe.processor.web.ui.MainPage.getSessionInstance().isEligibleToEditReports();
    }



    protected List<Long> idOfOrgList = new ArrayList<Long>();

    public List<Long> getIdOfOrgList() {
        return idOfOrgList;
    }

    protected String orgsFilter = "Не выбрано";

    public String getOrgsFilter() {
        return orgsFilter;
    }

    public void completeOrgListSelection(Map<Long, String> orgMap) throws HibernateException {
        if (orgMap != null) {
            idOfOrgList = new ArrayList<Long>();
            if (orgMap.isEmpty())
                orgsFilter = "Не выбрано";
            else {
                orgsFilter = "";
                for(Long idOfOrg : orgMap.keySet()) {
                    idOfOrgList.add(idOfOrg);
                    orgsFilter = orgsFilter.concat(orgMap.get(idOfOrg) + "; ");
                }
                filter.setIdOfOrgList(idOfOrgList);
                orgsFilter = orgsFilter.substring(0, orgsFilter.length() - 1);
            }
        }
    }


    public String getGetStringIdOfOrgList() {
        return idOfOrgList.toString().replaceAll("[^0-9,]","");
    }

    public String getDisplayedError() {
        return displayedError;
    }

    public void setDisplayedError(String displayedError) {
        this.displayedError = displayedError;
    }
}
