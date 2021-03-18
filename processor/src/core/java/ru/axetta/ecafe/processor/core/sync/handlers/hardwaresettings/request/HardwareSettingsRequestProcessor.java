/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request;

import ru.axetta.ecafe.processor.core.persistence.CompositeIdOfHardwareSettings;
import ru.axetta.ecafe.processor.core.persistence.HardwareSettings;
import ru.axetta.ecafe.processor.core.persistence.HardwareSettingsMT;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.sync.AbstractProcessor;
import ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request.items.*;

import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
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

        try {
            boolean errorFound;
            Long orgOwner = hardwareSettingsRequest.getOrgOwner();
            Long nextVersion = DAOUtils.nextVersionByHardwareSettingsRequest(session);
            StringBuilder errorMessage = new StringBuilder();

            for (HardwareSettingsRequestHSItem hsItem : hardwareSettingsRequest.getSectionItem()) {
                HardwareSettings hardwareSettings = null;
                int status = 1;

                hardwareSettings = DAOUtils
                        .getHardwareSettingsByOrgAndHostIP(session, hsItem.getIpItem().getValue(), orgOwner);
                if (null == hardwareSettings) {
                    Org org = (Org) session.get(Org.class, orgOwner);

                    hardwareSettings = new HardwareSettings();
                    hardwareSettings.setOrg(org);

                    CompositeIdOfHardwareSettings compositeIdOfHardwareSettings = new CompositeIdOfHardwareSettings(
                            orgOwner, hsItem.getIpItem().getValue());
                    hardwareSettings.setCompositeIdOfHardwareSettings(compositeIdOfHardwareSettings);
                }
                hardwareSettings.setIdOfHardwareSetting(hsItem.getIdOfHardwareSetting());
                hardwareSettings.setLastUpdateForIPHost(hsItem.getIpItem().getLastUpdate());

                Map<Integer, HardwareSettingsMT> listMT = new HashMap<>();
                for (HardwareSettingsRequestItem item : hsItem.getItems()) {
                    HardwareSettingsRequest.ModuleType moduleType = item.getType();
                    errorFound = !item.getResCode().equals(HardwareSettingsRequestItem.ERROR_CODE_ALL_OK);
                    switch (moduleType) {
                        case MT:
                            if (!errorFound) {
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
                        case DOTNETVER:
                            if (!errorFound) {
                                HardwareSettingsRequestDotNetVerItem dotNetVerItem = (HardwareSettingsRequestDotNetVerItem) item;
                                hardwareSettings.setDotNetVer(dotNetVerItem.getValue());
                                hardwareSettings.setLastUpdateForDotNetVer(dotNetVerItem.getLastUpdate());
                            } else {
                                errorMessage.append("Section DotNetVer not found ");
                                status = 0;
                            }
                            break;
                        case OSVER:
                            if (!errorFound) {
                                HardwareSettingsRequestOsVerItem osVerItem = (HardwareSettingsRequestOsVerItem) item;
                                hardwareSettings.setoSVer(osVerItem.getValue());
                                hardwareSettings.setLastUpdateForOSVer(osVerItem.getLastUpdate());
                            } else {
                                errorMessage.append("Section OsVer not found ");
                                status = 0;
                            }
                            break;
                        case RAM:
                            if (!errorFound) {
                                HardwareSettingsRequestRAMItem ramItem = (HardwareSettingsRequestRAMItem) item;
                                hardwareSettings.setRamSize(ramItem.getValue());
                                hardwareSettings.setLastUpdateForRAMSize(ramItem.getLastUpdate());
                            } else {
                                errorMessage.append("Section RAM not found ");
                                status = 0;
                            }
                            break;
                        case CPU:
                            if (!errorFound) {
                                HardwareSettingsRequestCPUItem cpuItem = (HardwareSettingsRequestCPUItem) item;
                                hardwareSettings.setCpuHost(cpuItem.getValue());
                                hardwareSettings.setLastUpdateForCPUHost(cpuItem.getLastUpdate());
                            } else {
                                errorMessage.append("Section CPU not found ");
                                status = 0;
                            }
                            break;
                        case CR:
                            if (!errorFound) {
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
                    session.flush();

                    for (Map.Entry<Integer, HardwareSettingsMT> entry : listMT.entrySet()) {
                        HardwareSettingsMT hardwareSettingsMT;
                        hardwareSettingsMT = DAOUtils.getHardwareSettingsMTByIdAndModuleType(session,
                                hardwareSettings.getCompositeIdOfHardwareSettings(), entry.getValue().getModuleType());

                        if (null == hardwareSettingsMT) {
                            hardwareSettingsMT = new HardwareSettingsMT();
                        }
                        hardwareSettingsMT.setHardwareSettings(hardwareSettings);
                        hardwareSettingsMT.setModuleType(entry.getValue().getModuleType());
                        hardwareSettingsMT.setInstallStatus(entry.getValue().getInstallStatus());
                        hardwareSettingsMT.setLastUpdate(entry.getValue().getLastUpdate());
                        hardwareSettingsMT.setFirmwareVer(entry.getValue().getFirmwareVer());
                        hardwareSettingsMT.setReaderName(entry.getValue().getReaderName());
                        hardwareSettingsMT.setIdOfHardwareSetting(hardwareSettings.getIdOfHardwareSetting());
                        session.saveOrUpdate(hardwareSettingsMT);
                    }
                }

                result.getItems().add(new ResHardwareSettingsRequestItem(status, errorMessage.toString()));
            }
        } catch (Exception e) {
            logger.error("Error saving HardwareSettingsRequest", e);
            return null;
        }
        return result;
    }
}
