/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.service.ImportRegisterClientsService;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.org.OrgSelectPage;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("session")
public class NSIRegistrySyncPage extends BasicWorkspacePage implements OrgSelectPage.CompleteHandler {

    Logger logger = LoggerFactory.getLogger(NSIRegistrySyncPage.class);

    Org org;
    String orgName;
    String syncLog;
    boolean performChanges;

    public String getPageFilename() {
        return "service/msk/nsi_registry_sync_page";
    }

    public String getOrgName() {
        return orgName;
    }

    public String getSyncLog() {
        return syncLog;
    }

    public boolean isPerformChanges() {
        return performChanges;
    }

    public void setPerformChanges(boolean performChanges) {
        this.performChanges = performChanges;
    }

    @Override
    public void completeOrgSelection(Session session, Long idOfOrg) throws Exception {
        if (idOfOrg == null) {
            this.orgName = null;
        } else {
            this.org = (Org) session.load(Org.class, idOfOrg);
            this.orgName = org.getShortName();
        }
    }

    public void performSync() {
        if (org == null) {
            printError("Не выбрана организация");
            return;
        }
        try {
            StringBuffer log = RuntimeContext.getAppContext().getBean("importRegisterClientsService", ImportRegisterClientsService.class)
                    .runSyncForOrg(org.getIdOfOrg(), performChanges);
            syncLog = log == null ? "" : log.toString();
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при выполнении синхронизации с Реестрами", e);
        }
    }
}
