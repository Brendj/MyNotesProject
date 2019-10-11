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
            Date syncData = new Date();
            Long maxVersionFromARM = request.getMaxVersion();
            Long idOfOrg = request.getOwner();
            Long nextVersion = SyncSettingManager.getNextVersion(session);
            List<SyncSettings> settingFromDB = manager.getSettingByIdOfOrg(session, idOfOrg);

            syncSettingsSection = new SyncSettingsSection();
            resSyncSettingsSection = new ResSyncSettingsSection();

            //build resSyncSettingsSection
            for (SyncSettingsSectionItem item : request.getItemList()){
                SyncSettings currentSetting = findSettingByContentType(settingFromDB, item.getContentType());
                ResSyncSettingsItem result = null;
                if(currentSetting == null) {
                    result = manager.saveFromSync(session, item, idOfOrg, syncData, nextVersion);
                } else if(item.getVersion() != null && item.getVersion() >= currentSetting.getVersion()){
                    result = manager.changeFromSync(session, currentSetting, item, syncData, nextVersion);
                } else {
                    result = new ResSyncSettingsItem();
                    result.setContentTypeInt(item.getContentType());
                    result.setVersion(currentSetting.getVersion());
                    result.setResult(ProcessResultEnum.OK);
                }
                resSyncSettingsSection.getItemList().add(result);
            }
            session.flush();

            //build syncSettingsSection
            settingFromDB = manager.getSettingByIdOfOrgAndGreaterThenVersion(session, idOfOrg, maxVersionFromARM);
            for(SyncSettings setting : settingFromDB){
                SyncSettingsSectionItem item = new SyncSettingsSectionItem(setting);
                syncSettingsSection.getItemList().add(item);
            }
        } else {
            throw new Exception("Can't get SyncSettingManager from app context");
        }
        return null;
    }

    private SyncSettings findSettingByContentType(List<SyncSettings> settingFromDB, Integer contentType) {
        for(SyncSettings item : settingFromDB){
            if(item.getContentType().getTypeCode().equals(contentType)){
                return item;
            }
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
