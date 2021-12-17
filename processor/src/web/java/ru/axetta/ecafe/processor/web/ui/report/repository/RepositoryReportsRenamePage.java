/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.report.repository;

import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 05.11.13
 * Time: 13:59
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope(value = "session")
public class RepositoryReportsRenamePage extends BasicWorkspacePage {
    private String currentRuleName;
    private String newRuleName;
    private String customNewRuleName;
    
    @Override
    public String getPageFilename() {
        return "report/repository/reports_rename";
    }

    @Override
    public void onShow() throws Exception {
        doReset();
    }

    public SelectItem[] getRuleNameItems() {
        List<String> l = DAOReadonlyService.getInstance().getCurrentRepositoryReportNames();
        SelectItem[] items = new SelectItem[l.size()];
        int n=0;
        for (String s : l) {
            items[n]=new SelectItem(s, s);
            ++n;
        }
        return items;
    }

    public SelectItem[] getAllowedRuleNameItems() {
        List<String> l = DAOReadonlyService.getInstance().getReportHandleRuleNames();
        SelectItem[] items = new SelectItem[l.size()];
        int n=0;
        for (String s : l) {
            items[n]=new SelectItem(s, s);
            ++n;
        }
        return items;
    }

    public String getCurrentRuleName() {
        return currentRuleName;
    }

    public void setCurrentRuleName(String currentRuleName) {
        this.currentRuleName = currentRuleName;
    }

    public String getNewRuleName() {
        return newRuleName;
    }

    public void setNewRuleName(String newRuleName) {
        this.newRuleName = newRuleName;
    }

    public String getCustomNewRuleName() {
        return customNewRuleName;
    }

    public void setCustomNewRuleName(String customNewRuleName) {
        this.customNewRuleName = customNewRuleName;
    }

    public void doApply() {
        if (currentRuleName == null || currentRuleName.trim().length() < 1) {
            sendError("Не удалось обновить наименования отчетов: необходимо выбрать текущее наименование отчетов");
            return;
        }
        if ((newRuleName == null || newRuleName.trim().length() < 1) &&
            (customNewRuleName == null || customNewRuleName.trim().length() < 1)) {
            sendError("Не удалось обновить наименования отчетов: необходимо указать новое наименование отчетов");
            return;
        }
        String newName = newRuleName;
        if (customNewRuleName != null && customNewRuleName.trim().length() > 0) {
            newName = customNewRuleName;
        }

        try {
            DAOService.getInstance().renameRepositoryReports(currentRuleName, newName);
            doReset();
        } catch (Exception e) {
            sendError("Не удалось обновить наименования отчетов: " + e.getMessage());
            return;
        }
    }

    private void sendError(String message) {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, message, null));
    }

    public void doReset() {
        currentRuleName = "";
        newRuleName = "";
        customNewRuleName = "";
    }
}
