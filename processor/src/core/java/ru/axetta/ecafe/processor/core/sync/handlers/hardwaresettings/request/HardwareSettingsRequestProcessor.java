/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sync.handlers.hardwaresettings.request;

import ru.axetta.ecafe.processor.core.persistence.HardwareSettingsMT;
import ru.axetta.ecafe.processor.core.persistence.HardwareSettingsReaders;
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
            StringBuilder errorMessage = new StringBuilder();
            int status = 1;

            for (List<HardwareSettingsRequestItem> sectionItem : hardwareSettingsRequest.getSectionItem()) {
                ru.axetta.ecafe.processor.core.persistence.HardwareSettings hardwareSettings = null;
                ru.axetta.ecafe.processor.core.persistence.HardwareSettingsMT hardwareSettingsMT = new ru.axetta.ecafe.processor.core.persistence.HardwareSettingsMT();
                ru.axetta.ecafe.processor.core.persistence.HardwareSettingsReaders hardwareSettingsReaders = new ru.axetta.ecafe.processor.core.persistence.HardwareSettingsReaders();
                List<HardwareSettingsMT> tempMT = new ArrayList<>();
                List<HardwareSettingsReaders> tempReaders = new ArrayList<>();

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
                                }
                                hardwareSettings.setIdOfHardwareSetting(hsItem.getIdOfHardwareSetting());
                            } else {
                                errorMessage.append("Section HS not found ");
                                status = 0;
                            }
                            break;
                        case "MT":
                            if (!errorFound && hardwareSettings != null) {
                                HardwareSettingsRequestMTItem mtItem = (HardwareSettingsRequestMTItem) item;
                                hardwareSettingsMT.setModuleType(mtItem.getValue());
                                hardwareSettingsMT.setInstallStatus(mtItem.getInstallStatus());
                                hardwareSettingsMT.setLastUpdate(mtItem.getLastUpdate());
                                tempMT.add(hardwareSettingsMT);
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
                                hardwareSettingsReaders.setUsedByModule(crItem.getUsedByModule());
                                hardwareSettingsReaders.setReaderName(crItem.getReaderName());
                                hardwareSettingsReaders.setFirmwareVer(crItem.getFirmwareVer());
                                hardwareSettingsReaders.setLastUpdateForReader(crItem.getLastUpdate());
                                tempReaders.add(hardwareSettingsReaders);
                            } else {
                                errorMessage.append("Section CR not found ");
                                status = 0;
                            }
                            break;
                    }
                }
                hardwareSettings.setVersion(nextVersion);
                session.save(hardwareSettings);
                for (HardwareSettingsMT mt : tempMT) {
                    hardwareSettingsMT = DAOUtils
                            .getHardwareSettingsMTByIdAndModuleType(session, hardwareSettings.getIdOfHardwareSetting(),
                                    mt.getModuleType());

                    if (null == hardwareSettingsMT) {
                        hardwareSettingsMT = new ru.axetta.ecafe.processor.core.persistence.HardwareSettingsMT();
                    }
                    hardwareSettingsMT.setHardwareSettings(hardwareSettings);
                    hardwareSettingsMT.setModuleType(mt.getModuleType());
                    hardwareSettingsMT.setInstallStatus(mt.getInstallStatus());
                    hardwareSettingsMT.setLastUpdate(mt.getLastUpdate());
                    session.save(hardwareSettingsMT);
                }

                for (HardwareSettingsReaders readers : tempReaders) {
                    hardwareSettingsReaders = DAOUtils.getHardwareSettingsReadersByIdAndUsedByModule(session,
                            hardwareSettings.getIdOfHardwareSetting(), readers.getUsedByModule());
                    if (null == hardwareSettingsReaders) {
                        hardwareSettingsReaders = new ru.axetta.ecafe.processor.core.persistence.HardwareSettingsReaders();
                    }
                    hardwareSettingsReaders.setHardwareSettings(hardwareSettings);
                    hardwareSettingsReaders.setUsedByModule(readers.getUsedByModule());
                    hardwareSettingsReaders.setReaderName(readers.getReaderName());
                    hardwareSettingsReaders.setFirmwareVer(readers.getFirmwareVer());
                    hardwareSettingsReaders.setLastUpdateForReader(readers.getLastUpdateForReader());
                    session.save(hardwareSettingsReaders);
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
