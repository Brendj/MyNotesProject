/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.report.NSIRegistryStatReport;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component
@Scope("session")
public class NSIOrgsRegistryStatPage extends BasicWorkspacePage {

    Logger logger = LoggerFactory.getLogger(NSIOrgsRegistryStatPage.class);
    private NSIRegistryStatReport nsiRegistryStatReport;

    protected Long selectedRevision = -1L;
    protected List<Long> revisions;
    private static final DateFormat df = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    public String getPageFilename() {
        return "service/msk/nsi_registry_stat_page";
    }

    public NSIRegistryStatReport getNsiRegistryStatReport() {
        return nsiRegistryStatReport;
    }

    public void setNsiRegistryStatReport(NSIRegistryStatReport nsiRegistryStatReport) {
        this.nsiRegistryStatReport = nsiRegistryStatReport;
    }

    public long getSelectedRevision() {
        return selectedRevision;
    }

    public void setSelectedRevision(long selectedRevision) {
        this.selectedRevision = selectedRevision;
    }

    public List<SelectItem> getRevisions() {
        if (revisions == null || revisions.size() < 1) {
            loadRevisions();
        }

        List<SelectItem> items = new ArrayList<SelectItem>();
        for (long date : revisions) {
            if (items.size() < 1) {
                items.add(new SelectItem(date, df.format(new Date(date)) + " - Последняя"));
            } else {
                items.add(new SelectItem(date, df.format(new Date(date))));
            }
        }
        return items;
    }
    //Получаем даты обновлений реестров
    protected void loadRevisions() {
        try {
            revisions = DAOService.getInstance().getOrgRegistryChangeRevisionsList();
        } catch (Exception e) {
            getLogger().error("Failed to load revisions", e);
            revisions = Collections.EMPTY_LIST;
        }
    }

    public NSIOrgsRegistryStatPage() {
        super();
        nsiRegistryStatReport = new NSIRegistryStatReport();
    }

    public Object buildStatReport() {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createReportPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            nsiRegistryStatReport.buildReport(persistenceSession, selectedRevision);
            persistenceTransaction.commit();
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Подготовка отчета завершена успешно", null));
        } catch (Exception e) {
            logger.error("Failed to build NSI registry stat report", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при подготовке отчета: " + e.getMessage(),
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
        return null;
    }
}
