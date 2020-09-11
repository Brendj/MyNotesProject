/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request;

import ru.axetta.ecafe.processor.core.persistence.CompositeIdOfHardwareSettings;
import ru.axetta.ecafe.processor.core.persistence.HardwareSettingsMT;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items.*;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
            boolean errorFound;
            Long orgOwner = hardwareSettingsRequest.getOrgOwner();
            Long nextVersion = DAOUtils.nextVersionByHardwareSettingsRequest(session);
            StringBuilder errorMessage = new StringBuilder();

            for (List<HardwareSettingsRequestItem> sectionItem : hardwareSettingsRequest.getSectionItem()) {
                ru.axetta.ecafe.processor.core.persistence.HardwareSettings hardwareSettings = null;
                int status = 1;
                HashMap<Integer, HardwareSettingsMT> listMT = new HashMap<Integer, HardwareSettingsMT>();
                for (HardwareSettingsRequestItem item : sectionItem) {
                    String moduleType = item.getType();
                    errorFound = !item.getResCode().equals(HardwareSettingsRequestItem.ERROR_CODE_ALL_OK);
                    switch (moduleType) {
                        case "HS":
                            if (!errorFound) {
                                HardwareSettingsRequestHSItem hsItem = (HardwareSettingsRequestHSItem) item;
                                hardwareSettings = DAOUtils
                                        .getHardwareSettingsRequestByOrgAndIdOfHardwareSetting(session,
                                                hsItem.getIdOfHardwareSetting(), orgOwner);
                                if (null == hardwareSettings) {
                                    hardwareSettings = new ru.axetta.ecafe.processor.core.persistence.HardwareSettings();
                                    Org org = (Org) session.get(Org.class, orgOwner);
                                    hardwareSettings.setOrg(org);
                                    CompositeIdOfHardwareSettings compositeIdOfHardwareSettings = new ru.axetta.ecafe.processor.core.persistence.CompositeIdOfHardwareSettings(
                                            orgOwner, hsItem.getIdOfHardwareSetting());
                                    hardwareSettings.setCompositeIdOfHardwareSettings(compositeIdOfHardwareSettings);
                                }
                            } else {
                                errorMessage.append("Section HS not found ");
                                status = 0;
                            }
                            break;
                        case "MT":
                            if (!errorFound && hardwareSettings != null) {
                                HardwareSettingsRequestMTItem mtItem = (HardwareSettingsRequestMTItem) item;
                                HardwareSettingsMT settingsMT = new HardwareSettingsMT();
                                settingsMT.setModuleType(mtItem.getValue());
                                settingsMT.setInstallStatus(mtItem.getInstallStatus());
                                settingsMT.setLastUpdate(mtItem.getLastUpdate());
                                listMT.put(settingsMT.getModuleType(), settingsMT);
                            } else {
                                errorMessage.append("Section MT not found ");
                                status = 0;
                            }

                            break;
                        case "IP":
                            if (!errorFound && hardwareSettings != null) {
                                HardwareSettingsRequestIPItem ipItem = (HardwareSettingsRequestIPItem) item;
                                hardwareSettings.setIpHost(ipItem.getValue());
                                hardwareSettings.setLastUpdateForIPHost(ipItem.getLastUpdate());

                            } else {
                                errorMessage.append("Section IP not found ");
                                status = 0;
                            }
                            break;
                        case "DotNetVer":
                            if (!errorFound && hardwareSettings != null) {
                                HardwareSettingsRequestDotNetVerItem dotNetVerItem = (HardwareSettingsRequestDotNetVerItem) item;
                                hardwareSettings.setDotNetVer(dotNetVerItem.getValue());
                                hardwareSettings.setLastUpdateForDotNetVer(dotNetVerItem.getLastUpdate());
                            } else {
                                errorMessage.append("Section DotNetVer not found ");
                                status = 0;
                            }
                            break;
                        case "OsVer":
                            if (!errorFound && hardwareSettings != null) {
                                HardwareSettingsRequestOsVerItem osVerItem = (HardwareSettingsRequestOsVerItem) item;
                                hardwareSettings.setoSVer(osVerItem.getValue());
                                hardwareSettings.setLastUpdateForOSVer(osVerItem.getLastUpdate());
                            } else {
                                errorMessage.append("Section OsVer not found ");
                                status = 0;
                            }
                            break;
                        case "RAM":
                            if (!errorFound && hardwareSettings != null) {
                                HardwareSettingsRequestRAMItem ramItem = (HardwareSettingsRequestRAMItem) item;
                                hardwareSettings.setRamSize(ramItem.getValue());
                                hardwareSettings.setLastUpdateForRAMSize(ramItem.getLastUpdate());
                            } else {
                                errorMessage.append("Section RAM not found ");
                                status = 0;
                            }
                            break;
                        case "CPU":
                            if (!errorFound && hardwareSettings != null) {
                                HardwareSettingsRequestCPUItem cpuItem = (HardwareSettingsRequestCPUItem) item;
                                hardwareSettings.setCpuHost(cpuItem.getValue());
                                hardwareSettings.setLastUpdateForCPUHost(cpuItem.getLastUpdate());
                            } else {
                                errorMessage.append("Section CPU not found ");
                                status = 0;
                            }
                            break;
                        case "CR":
                            if (!errorFound && hardwareSettings != null) {
                                HardwareSettingsRequestCRItem crItem = (HardwareSettingsRequestCRItem) item;

                                HardwareSettingsMT tempCR = listMT
                                        .get(((HardwareSettingsRequestCRItem) item).getUsedByModule());
                                tempCR.setReaderName(crItem.getReaderName());
                                tempCR.setFirmwareVer(crItem.getFirmwareVer());
                                listMT.put(crItem.getUsedByModule(), tempCR);
                            } else {
                                errorMessage.append("Section CR not found ");
                                status = 0;
                            }
                            break;
                    }
                }
                hardwareSettings.setVersion(nextVersion);
                if (status == 1) {
                    session.saveOrUpdate(hardwareSettings);
                    for (Map.Entry<Integer, HardwareSettingsMT> entry : listMT.entrySet()) {
                        ru.axetta.ecafe.processor.core.persistence.HardwareSettingsMT hardwareSettingsMT;
                        hardwareSettingsMT = DAOUtils.getHardwareSettingsMTByIdAndModuleType(session,
                                hardwareSettings.getCompositeIdOfHardwareSettings(), entry.getValue().getModuleType());

                        if (null == hardwareSettingsMT) {
                            hardwareSettingsMT = new ru.axetta.ecafe.processor.core.persistence.HardwareSettingsMT();
                        }
                        hardwareSettingsMT.setHardwareSettings(hardwareSettings);
                        hardwareSettingsMT.setModuleType(entry.getValue().getModuleType());
                        hardwareSettingsMT.setInstallStatus(entry.getValue().getInstallStatus());
                        hardwareSettingsMT.setLastUpdate(entry.getValue().getLastUpdate());
                        hardwareSettingsMT.setFirmwareVer(entry.getValue().getFirmwareVer());
                        hardwareSettingsMT.setReaderName(entry.getValue().getReaderName());
                        session.saveOrUpdate(hardwareSettingsMT);
                    }
                }

                items.add(new ResHardwareSettingsRequestItem(status, errorMessage.toString()));
            }
        } catch (Exception e) {
            logger.error("Error saving HardwareSettingsRequest", e);
            return null;
        }
        result.setItems(items);
        return result;
    }
}
