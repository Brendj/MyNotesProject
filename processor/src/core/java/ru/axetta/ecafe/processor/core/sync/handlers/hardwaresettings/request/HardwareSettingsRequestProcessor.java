/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request;

import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items.*;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class HardwareSettingsRequestProcessor extends AbstractProcessor<ResHardwareSettingsRequest> {

    private static final Logger logger = LoggerFactory.getLogger(HardwareSettingsRequestProcessor.class);
    private final HardwareSettingsRequest hardwareSettingsRequest;

    public HardwareSettingsRequestProcessor(Session persistenceSession,
            HardwareSettingsRequest hardwareSettingsRequest) {
        super(persistenceSession);
        this.hardwareSettingsRequest = hardwareSettingsRequest;
    }

    public ResHardwareSettingsRequest process() {
        ResHardwareSettingsRequest result = new ResHardwareSettingsRequest();

        List<ResHardwareSettingsRequestItem> items = new ArrayList<ResHardwareSettingsRequestItem>();
        try {
            ResHardwareSettingsRequestItem resItem;
            boolean errorFound;
            Long orgOwner = hardwareSettingsRequest.getOrgOwner();
            Long nextVersion = DAOUtils.nextVersionByHardwareSettingsRequest(session);
            Long id = hardwareSettingsRequest.getIdOfHardwareSetting();

            for (List<HardwareSettingsRequestItem> sectionItem : hardwareSettingsRequest.getSectionItem()) {
                ru.axetta.ecafe.processor.core.persistence.HardwareSettings hardwareSettings = DAOUtils
                        .getHardwareSettingsRequestByOrgAndIdOfHardwareSetting(session, orgOwner, id);
                ru.axetta.ecafe.processor.core.persistence.HardwareSettingsMT hardwareSettingsMT = null;
                ru.axetta.ecafe.processor.core.persistence.HardwareSettingsReaders hardwareSettingsReaders = null;

                if (null == hardwareSettings) {
                    hardwareSettings = new ru.axetta.ecafe.processor.core.persistence.HardwareSettings();
                    Org org = (Org) session.get(Org.class, orgOwner);
                    hardwareSettings.setOrg(org);
                }

                for (HardwareSettingsRequestItem item : sectionItem) {
                    String moduleType = item.getType();
                    errorFound = !item.getResCode().equals(HardwareSettingsRequestItem.ERROR_CODE_ALL_OK);
                    switch (moduleType) {
                        case "HS":
                            if (!errorFound) {
                                HardwareSettingsRequestHSItem hsItem = (HardwareSettingsRequestHSItem) item;
                                hardwareSettings.setIdOfHardwareSetting(hsItem.getIdOfHardwareSetting());
                                resItem = new ResHardwareSettingsRequestHSItem(hardwareSettings, item.getResCode());
                            } else {
                                resItem = new ResHardwareSettingsRequestHSItem(item.getResCode(),
                                        item.getErrorMessage());
                            }
                            items.add(resItem);
                            break;
                        case "MT":
                            if (!errorFound) {
                                HardwareSettingsRequestMTItem mtItem = (HardwareSettingsRequestMTItem) item;
                                hardwareSettingsMT = DAOUtils
                                        .getHardwareSettingsMTByIdAndModuleType(session, id, mtItem.getValue());
                                if (null == hardwareSettingsMT) {
                                    hardwareSettingsMT = new ru.axetta.ecafe.processor.core.persistence.HardwareSettingsMT();
                                }
                                hardwareSettingsMT.setModuleType(mtItem.getValue());
                                hardwareSettingsMT.setInstallStatus(mtItem.getInstallStatus());
                                hardwareSettingsMT.setLastUpdate(mtItem.getLastUpdate());
                                hardwareSettingsMT.setHardwareSettings(hardwareSettings);

                                resItem = new ResHardwareSettingsRequestMTItem(hardwareSettingsMT, mtItem.getResCode());
                            } else {
                                resItem = new ResHardwareSettingsRequestMTItem(item.getResCode(),
                                        item.getErrorMessage());
                            }
                            items.add(resItem);
                            break;
                        case "IP":
                            if (!errorFound) {

                                HardwareSettingsRequestIPItem ipItem = (HardwareSettingsRequestIPItem) item;
                                hardwareSettings.setIpHost(ipItem.getValue());
                                hardwareSettings.setLastUpdateForIPHost(ipItem.getLastUpdate());

                                resItem = new ResHardwareSettingsRequestIPItem(hardwareSettings, ipItem.getResCode());

                            } else {
                                resItem = new ResHardwareSettingsRequestIPItem(item.getResCode(),
                                        item.getErrorMessage());
                            }
                            items.add(resItem);
                            break;
                        case "DotNetVer":
                            if (!errorFound) {
                                HardwareSettingsRequestDotNetVerItem dotNetVerItem = (HardwareSettingsRequestDotNetVerItem) item;
                                hardwareSettings.setDotNetVer(dotNetVerItem.getValue());
                                hardwareSettings.setLastUpdateForDotNetVer(dotNetVerItem.getLastUpdate());

                                resItem = new ResHardwareSettingsRequestDotNetVerItem(hardwareSettings,
                                        dotNetVerItem.getResCode());
                            } else {
                                resItem = new ResHardwareSettingsRequestDotNetVerItem(item.getResCode(),
                                        item.getErrorMessage());
                            }
                            items.add(resItem);
                            break;
                        case "OsVer":
                            if (!errorFound) {
                                HardwareSettingsRequestOsVerItem osVerItem = (HardwareSettingsRequestOsVerItem) item;
                                hardwareSettings.setoSVer(osVerItem.getValue());
                                hardwareSettings.setLastUpdateForOSVer(osVerItem.getLastUpdate());

                                resItem = new ResHardwareSettingsRequestOsVerItem(hardwareSettings,
                                        osVerItem.getResCode());
                            } else {
                                resItem = new ResHardwareSettingsRequestOsVerItem(item.getResCode(),
                                        item.getErrorMessage());
                            }
                            items.add(resItem);
                            break;
                        case "RAM":
                            if (!errorFound) {
                                HardwareSettingsRequestRAMItem ramItem = (HardwareSettingsRequestRAMItem) item;
                                hardwareSettings.setRamSize(ramItem.getValue());
                                hardwareSettings.setLastUpdateForRAMSize(ramItem.getLastUpdate());

                                resItem = new ResHardwareSettingsRequestRAMItem(hardwareSettings, ramItem.getResCode());
                            } else {
                                resItem = new ResHardwareSettingsRequestRAMItem(item.getResCode(),
                                        item.getErrorMessage());
                            }
                            items.add(resItem);
                            break;
                        case "CPU":
                            if (!errorFound) {
                                HardwareSettingsRequestCPUItem cpuItem = (HardwareSettingsRequestCPUItem) item;
                                hardwareSettings.setCpuHost(cpuItem.getValue());
                                hardwareSettings.setLastUpdateForCPUHost(cpuItem.getLastUpdate());

                                resItem = new ResHardwareSettingsRequestCPUItem(hardwareSettings, cpuItem.getResCode());
                            } else {
                                resItem = new ResHardwareSettingsRequestCPUItem(item.getResCode(),
                                        item.getErrorMessage());
                            }
                            items.add(resItem);
                            break;
                        case "CR":
                            if (!errorFound) {
                                HardwareSettingsRequestCRItem crItem = (HardwareSettingsRequestCRItem) item;
                                hardwareSettingsReaders = DAOUtils
                                        .getHardwareSettingsReadersByIdAndUsedByModule(session,
                                                hardwareSettings.getIdOfHardwareSetting(), crItem.getUsedByModule());
                                if (null == hardwareSettingsReaders) {
                                    hardwareSettingsReaders = new ru.axetta.ecafe.processor.core.persistence.HardwareSettingsReaders();
                                }
                                hardwareSettingsReaders.setUsedByModule(crItem.getUsedByModule());
                                hardwareSettingsReaders.setReaderName(crItem.getReaderName());
                                hardwareSettingsReaders.setFirmwareVer(crItem.getFirmwareVer());
                                hardwareSettingsReaders.setLastUpdateForReader(crItem.getLastUpdate());
                                hardwareSettingsReaders.setHardwareSettings(hardwareSettings);

                                resItem = new ResHardwareSettingsRequestCRItem(hardwareSettingsReaders,
                                        crItem.getResCode());
                            } else {
                                resItem = new ResHardwareSettingsRequestCRItem(item.getResCode(),
                                        item.getErrorMessage());
                            }
                            items.add(resItem);
                            break;
                    }
                }
                hardwareSettings.setVersion(nextVersion);
                session.save(hardwareSettings);
                session.save(hardwareSettingsMT);
                session.save(hardwareSettingsReaders);
            }
        } catch (Exception e) {
            logger.error("Error saving HardwareSettingsRequest", e);
            return null;
        }
        result.setItems(items);
        return result;
    }
}
