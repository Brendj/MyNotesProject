/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.orghardware;

import ru.axetta.ecafe.processor.core.persistence.OrganizationType;

public class HardwareSettingsReportItem {

    //----------------- Данные ОО --------------------//
    private String orgNumberInName;
    private Long idOfOrg;
    private String shortName;//название ПП
    private String shortNameInfoService;//ОО краткое
    private String district;
    private String shortAddress;//краткий адрес
    private OrganizationType type;
    private String orgType;

    //----------------- ПК --------------------//
    private String clientVersion;//версия ПО
    private Double dataBaseSize;//размер БД
    private String remoteAddress;//ip
    private String osVersion;
    private String sqlVersion;
    private String dotNetVersion;
    private String cpuVersion;
    private String ramSize;

    private String readerNameOU;
    private String firmwareVersionOU;
    private String readerNameFeeding;
    private String firmwareVersionFeeding;
    private String readerNameGuard;
    private String firmwareVersionGuard;
    private String readerNameInfo;
    private String firmwareVersionInfo;

    //----------------- параметры турникетов --------------------//
    private String turnstileId;//ip/mac
    private Integer numOfEntries;
    private Integer numOfTurnstile;
    private String controllerModel;
    private String controllerFirmwareVersion;
    private Integer isWorkWithLongIds;
    private Double timeCoefficient;

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

    public OrganizationType getType() {
        return type;
    }

    public void setType(OrganizationType type) {
        this.type = type;
    }

    public String getOrgType() {
        return orgType;
    }

    public void setOrgType(String orgType) {
        this.orgType = orgType;
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

    public String getReaderNameOU() {
        return readerNameOU;
    }

    public void setReaderNameOU(String readerNameOU) {
        this.readerNameOU = readerNameOU;
    }

    public String getFirmwareVersionOU() {
        return firmwareVersionOU;
    }

    public void setFirmwareVersionOU(String firmwareVersionOU) {
        this.firmwareVersionOU = firmwareVersionOU;
    }

    public String getReaderNameFeeding() {
        return readerNameFeeding;
    }

    public void setReaderNameFeeding(String readerNameFeeding) {
        this.readerNameFeeding = readerNameFeeding;
    }

    public String getFirmwareVersionFeeding() {
        return firmwareVersionFeeding;
    }

    public void setFirmwareVersionFeeding(String firmwareVersionFeeding) {
        this.firmwareVersionFeeding = firmwareVersionFeeding;
    }

    public String getReaderNameGuard() {
        return readerNameGuard;
    }

    public void setReaderNameGuard(String readerNameGuard) {
        this.readerNameGuard = readerNameGuard;
    }

    public String getFirmwareVersionGuard() {
        return firmwareVersionGuard;
    }

    public void setFirmwareVersionGuard(String firmwareVersionGuard) {
        this.firmwareVersionGuard = firmwareVersionGuard;
    }

    public String getReaderNameInfo() {
        return readerNameInfo;
    }

    public void setReaderNameInfo(String readerNameInfo) {
        this.readerNameInfo = readerNameInfo;
    }

    public String getFirmwareVersionInfo() {
        return firmwareVersionInfo;
    }

    public void setFirmwareVersionInfo(String firmwareVersionInfo) {
        this.firmwareVersionInfo = firmwareVersionInfo;
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

    public Integer getNumOfTurnstile() {
        return numOfTurnstile;
    }

    public void setNumOfTurnstile(Integer numOfTurnstile) {
        this.numOfTurnstile = numOfTurnstile;
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

    public Integer getIsWorkWithLongIds() {
        return isWorkWithLongIds;
    }

    public void setIsWorkWithLongIds(Integer isWorkWithLongIds) {
        this.isWorkWithLongIds = isWorkWithLongIds;
    }

    public Double getTimeCoefficient() {
        return timeCoefficient;
    }

    public void setTimeCoefficient(Double timeCoefficient) {
        this.timeCoefficient = timeCoefficient;
    }
}
