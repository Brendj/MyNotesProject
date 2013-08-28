/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.repository;


import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.ReportInfo;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.abstractpage.AbstractListPage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.model.SelectItem;
import java.io.File;
import java.util.List;

@Component
@Scope("session")
public class ReportRepositoryListPage extends AbstractListPage<ReportInfo, ReportRepositoryItem> {
    BasicWorkspacePage groupPage = new BasicWorkspacePage();
    ReportRepositoryItem.Filter filter = new ReportRepositoryItem.Filter();
    
    File fileToDownload;
    private ReportRepositoryItem selectedItem;

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
        List<String> l = DAOService.getInstance().getReportHandleRuleNames();
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
}
