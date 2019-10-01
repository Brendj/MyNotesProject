/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.syncsettings.request;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.SyncSettingManager;
import ru.axetta.ecafe.processor.core.persistence.orgsettings.syncSettings.SyncSettings;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;

import org.hibernate.Session;

import java.util.Date;
import java.util.List;

public class SyncSettingProcessor extends AbstractProcessor<SyncSettingsSection> {
    private SyncSettingsSection syncSettingsSection;
    private ResSyncSettingsSection resSyncSettingsSection;
    private SyncSettingsRequest request;

    public SyncSettingProcessor(Session session, SyncSettingsRequest syncSettingsRequest) {
        super(session);
        this.request = syncSettingsRequest;
    }

    @Override
    public SyncSettingsSection process() throws Exception {
        if(request == null){
            throw new Exception("Request is NULL");
        }
        SyncSettingManager manager = RuntimeContext.getAppContext().getBean(SyncSettingManager.class);

        if(manager != null) {
            Long maxVersionFromARM = request.getMaxVersion();
            Date syncData = new Date();
            Long idOfOrg = request.getOwner();
            Long nextVersion = SyncSettingManager.getNextVersion(session);
            List<SyncSettings> settingFromDB = manager.getSettingByIdOfOrgAndVersion(session, idOfOrg);

            syncSettingsSection = new SyncSettingsSection();
            resSyncSettingsSection = new ResSyncSettingsSection();

            for (SyncSettingsSectionItem item : request.getItemList()){
                ResSyncSettingsItem result = manager.saveFromSync(session, item, idOfOrg, syncData, maxVersionFromARM, nextVersion);
                resSyncSettingsSection.getItemList().add(result);
            }
        } else {
            throw new Exception("Can't get SyncSettingManager from app context");
        }


        return null;
    }

    public SyncSettingsSection getSyncSettingsSection() {
        return syncSettingsSection;
    }

    public void setSyncSettingsSection(SyncSettingsSection syncSettingsSection) {
        this.syncSettingsSection = syncSettingsSection;
    }

    public ResSyncSettingsSection getResSyncSettingsSection() {
        return resSyncSettingsSection;
    }

    public void setResSyncSettingsSection(ResSyncSettingsSection resSyncSettingsSection) {
        this.resSyncSettingsSection = resSyncSettingsSection;
    }
}
