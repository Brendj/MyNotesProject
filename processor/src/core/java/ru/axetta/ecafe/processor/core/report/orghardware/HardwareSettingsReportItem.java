/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.orghardware;

import ru.axetta.ecafe.processor.core.persistence.HardwareSettings;
import ru.axetta.ecafe.processor.core.persistence.OrgSync;
import ru.axetta.ecafe.processor.core.persistence.TurnstileSettings;

import org.hibernate.Query;
import org.hibernate.Session;

import java.util.Date;

public class HardwareSettingsReportItem {

    //----------------- Данные ОО --------------------//
    private String orgNumberInName;
    private Long idOfOrg;
    private String shortName;//название ПП
    private String shortNameInfoService;//ОО краткое
    private String district;
    private String shortAddress;//краткий адрес
    private String typeOU;
    private String moduleType; //модуль
    private Date lastUpdate; //последнее изменение

    //----------------- ПК --------------------//
    private String clientVersion;//версия ПО
    private Double dataBaseSize;//размер БД
    private String remoteAddress;//ip
    private String osVersion;
    private String sqlVersion;
    private String dotNetVersion;
    private String cpuVersion;
    private String ramSize;
    private String readerName;
    private String firmwareVersion;

    //----------------- параметры турникетов --------------------//
    private String turnstileId;//ip/mac
    private Integer numOfEntries;
    private String controllerModel;
    private String controllerFirmwareVersion;
    private String isWorkWithLongIds;
    private Double timeCoefficient;

    public HardwareSettingsReportItem(HardwareSettings settings, OrgSync orgSync, String moduleType, String readerName,
            String firmwareVersion, Boolean isAdministrator, Session persistenceSession) {
        setOrgNumberInName(orgSync.getOrg().getOrgNumberInName());
        setIdOfOrg(orgSync.getIdOfOrg());
        setShortName(orgSync.getOrg().getShortName());
        setShortNameInfoService(orgSync.getOrg().getShortNameInfoService());
        setDistrict(orgSync.getOrg().getDistrict());
        setShortAddress(orgSync.getOrg().getShortAddress());
        Integer codeOU = orgSync.getOrg().getType().getCode();
        switch (codeOU) {
            case 0:
                setTypeOU("СОШ");
                break;
            case 1:
                setTypeOU("ДОУ");
                break;
            case 2:
                setTypeOU("ПП");
                break;
            case 3:
                setTypeOU("СПО");
                break;
            case 4:
                setTypeOU("ДО");
                break;
        }

        setModuleType(moduleType);

        if (isAdministrator) {
            setSqlVersion(orgSync.getSqlServerVersion());
            setDataBaseSize(orgSync.getDatabaseSize());
        }
        setClientVersion(orgSync.getClientVersion());

        setRemoteAddress(settings.getIpHost());
        setDotNetVersion(settings.getDotNetVer());
        setOsVersion(settings.getoSVer());
        setRamSize(settings.getRamSize());
        setCpuVersion(settings.getCpuHost());
        setReaderName(readerName);
        setFirmwareVersion(firmwareVersion);

        Query query = persistenceSession.createSQLQuery(
                "select "
                + "(select to_timestamp(max(date)/1000) "
                + "from (values(lastupdateforiphost), (lastupdatefordotnetver), (lastupdateforosver), "
                + "(lastupdateforramsize), (lastupdateforcpuhost)) as updatedate (date)) as date"
                + " from cf_hardware_settings "
                + "where ipHost = :ipHost ");
        query.setParameter("ipHost", settings.getCompositeIdOfHardwareSettings().getIpHost());
        setLastUpdate((Date) query.setMaxResults(1).uniqueResult());
    }

    public HardwareSettingsReportItem(TurnstileSettings ts, OrgSync orgSync) {

        setOrgNumberInName(orgSync.getOrg().getOrgNumberInName());
        setIdOfOrg(orgSync.getIdOfOrg());
        setShortName(orgSync.getOrg().getShortName());
        setShortNameInfoService(orgSync.getOrg().getShortNameInfoService());
        setDistrict(orgSync.getOrg().getDistrict());
        setShortAddress(orgSync.getOrg().getShortAddress());
        Integer codeOU = orgSync.getOrg().getType().getCode();
        switch (codeOU) {
            case 0:
                setTypeOU("СОШ");
                break;
            case 1:
                setTypeOU("ДОУ");
                break;
            case 2:
                setTypeOU("ПП");
                break;
            case 3:
                setTypeOU("СПО");
                break;
            case 4:
                setTypeOU("ДО");
                break;
        }

        setModuleType("Турникет");
        setTurnstileId(ts.getTurnstileId());
        setNumOfEntries(ts.getNumOfEntries());
        setControllerModel(ts.getControllerModel());
        setControllerFirmwareVersion(ts.getControllerFirmwareVersion());
        if (ts.getIsReadsLongIdsIncorrectly() == 1) {
            setIsWorkWithLongIds("Да");
        } else {
            setIsWorkWithLongIds("Нет");
        }
        setTimeCoefficient(ts.getTimeCoefficient());
        setLastUpdate(ts.getLastUpdateForTurnstile());
    }

    public String getOrgNumberInName() {
        return orgNumberInName;
    }

    public void setOrgNumberInName(String orgNumberInName) {
        this.orgNumberInName = orgNumberInName;
    }

    public Long getIdOfOrg() {
        return idOfOrg;
    }

    public void setIdOfOrg(Long idOfOrg) {
        this.idOfOrg = idOfOrg;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getShortNameInfoService() {
        return shortNameInfoService;
    }

    public void setShortNameInfoService(String shortNameInfoService) {
        this.shortNameInfoService = shortNameInfoService;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getShortAddress() {
        return shortAddress;
    }

    public void setShortAddress(String shortAddress) {
        this.shortAddress = shortAddress;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public Double getDataBaseSize() {
        return dataBaseSize;
    }

    public void setDataBaseSize(Double dataBaseSize) {
        this.dataBaseSize = dataBaseSize;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddress) {
        this.remoteAddress = remoteAddress;
    }

    public String getOsVersion() {
        return osVersion;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getSqlVersion() {
        return sqlVersion;
    }

    public void setSqlVersion(String sqlVersion) {
        this.sqlVersion = sqlVersion;
    }

    public String getDotNetVersion() {
        return dotNetVersion;
    }

    public void setDotNetVersion(String dotNetVersion) {
        this.dotNetVersion = dotNetVersion;
    }

    public String getCpuVersion() {
        return cpuVersion;
    }

    public void setCpuVersion(String cpuVersion) {
        this.cpuVersion = cpuVersion;
    }

    public String getRamSize() {
        return ramSize;
    }

    public void setRamSize(String ramSize) {
        this.ramSize = ramSize;
    }

    public String getTurnstileId() {
        return turnstileId;
    }

    public void setTurnstileId(String turnstileId) {
        this.turnstileId = turnstileId;
    }

    public Integer getNumOfEntries() {
        return numOfEntries;
    }

    public void setNumOfEntries(Integer numOfEntries) {
        this.numOfEntries = numOfEntries;
    }

    public String getControllerModel() {
        return controllerModel;
    }

    public void setControllerModel(String controllerModel) {
        this.controllerModel = controllerModel;
    }

    public String getControllerFirmwareVersion() {
        return controllerFirmwareVersion;
    }

    public void setControllerFirmwareVersion(String controllerFirmwareVersion) {
        this.controllerFirmwareVersion = controllerFirmwareVersion;
    }

    public String getIsWorkWithLongIds() {
        return isWorkWithLongIds;
    }

    public void setIsWorkWithLongIds(String isWorkWithLongIds) {
        this.isWorkWithLongIds = isWorkWithLongIds;
    }

    public Double getTimeCoefficient() {
        return timeCoefficient;
    }

    public void setTimeCoefficient(Double timeCoefficient) {
        this.timeCoefficient = timeCoefficient;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public String getTypeOU() {
        return typeOU;
    }

    public void setTypeOU(String typeOU) {
        this.typeOU = typeOU;
    }

    public String getModuleType() {
        return moduleType;
    }

    public void setModuleType(String moduleType) {
        this.moduleType = moduleType;
    }

    public String getReaderName() {
        return readerName;
    }

    public void setReaderName(String readerName) {
        this.readerName = readerName;
    }

    public String getFirmwareVersion() {
        return firmwareVersion;
    }

    public void setFirmwareVersion(String firmwareVersion) {
        this.firmwareVersion = firmwareVersion;
    }
}
