/*
 * Copyright (c) 2020. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report.orghardware;

import ru.axetta.ecafe.processor.core.persistence.*;

public class HardwareSettingsReportItem {

    private static final Integer ADMINISTRATOR = 0;
    private static final Integer CASHIER = 1;
    private static final Integer GUARD = 2;

    //----------------- Данные ОО --------------------//
    private String orgNumberInName;
    private Long idOfOrg;
    private String shortName;//название ПП
    private String shortNameInfoService;//ОО краткое
    private String district;
    private String shortAddress;//краткий адрес
    private OrganizationType type;

    //----------------- ПК. АРМ администратора ОУ --------------------//
    private String clientVersion;//версия ПО
    private Double dataBaseSize;//размер БД
    private String remoteAddressOU;//ip
    private String readerNameOU;
    private String firmwareVersionOU;
    private String osVersionOU;
    private String sqlVersionOU;
    private String dotNetVersionOU;
    private String cpuVersionOU;
    private String ramSizeOU;

    //----------------- ПК. АРМ оператора питания --------------------//
    private String remoteAddressFeeding;//ip
    private String readerNameFeeding;
    private String firmwareVersionFeeding;
    private String osVersionFeeding;
    private String sqlVersionFeeding;
    private String dotNetVersionFeeding;
    private String cpuVersionFeeding;
    private String ramSizeFeeding;

    //----------------- ПК. АРМ контроллер входа (охранника) --------------------//
    private String remoteAddressGuard;//ip
    private String readerNameGuard;
    private String firmwareVersionGuard;
    private String osVersionGuard;
    private String sqlVersionGuard;
    private String dotNetVersionGuard;
    private String cpuVersionGuard;
    private String ramSizeGuard;

    //----------------- ПК. Инфопанель --------------------//
    private String remoteAddressInfo;//ip
    private String readerNameInfo;
    private String firmwareVersionInfo;
    private String osVersionInfo;
    private String sqlVersionInfo;
    private String dotNetVersionInfo;
    private String cpuVersionInfo;
    private String ramSizeInfo;

    //----------------- параметры турникетов --------------------//
    private String turnstileId;//ip/mac
    private Integer numOfEntries;
    private Integer numOfTurnstile;
    private String controllerModel;
    private String controllerFirmwareVersion;
    private Integer isWorkWithLongIds;

    private Boolean mainBuilding;
    private Boolean changed;

    private final String MAIN_BUILDING_STYLE = "mainBuilding";
    private final String NOT_SERVICED_STYLE = "notServiced";

    public HardwareSettingsReportItem(OrgSync orgSync, Boolean setSql) {

        this.orgNumberInName = orgSync.getOrg().getOrgNumberInName();
        this.idOfOrg = orgSync.getOrg().getIdOfOrg();
        this.shortName = orgSync.getOrg().getShortName();
        this.shortNameInfoService = orgSync.getOrg().getShortNameInfoService();
        this.district = orgSync.getOrg().getDistrict();
        this.shortAddress = orgSync.getOrg().getShortAddress();
        this.type = orgSync.getOrg().getType();
        if (setSql) {
            this.sqlVersionOU = orgSync.getSqlServerVersion();
            this.sqlVersionFeeding = orgSync.getSqlServerVersion();
            this.sqlVersionGuard = orgSync.getSqlServerVersion();
            this.sqlVersionInfo = orgSync.getSqlServerVersion();
        }
        this.clientVersion = orgSync.getClientVersion();
        this.dataBaseSize = orgSync.getDatabaseSize();
    }

    public HardwareSettingsReportItem(Org org, OrgSync orgSync, HardwareSettings hardwareSettings,
            HardwareSettingsMT hardwareSettingsMT, HardwareSettingsReaders hardwareSettingsReaders,
            TurnstileSettings turnstileSettings) {
        this.orgNumberInName = org.getOrgNumberInName();
        this.idOfOrg = org.getIdOfOrg();
        this.shortName = org.getShortName();
        this.shortNameInfoService = org.getShortNameInfoService();
        this.district = org.getDistrict();
        this.shortAddress = org.getShortAddress();
        this.type = org.getType();

        //АРМ ОУ
        this.clientVersion = orgSync.getClientVersion();
        this.dataBaseSize = orgSync.getDatabaseSize();
        this.remoteAddressOU = hardwareSettings.getIpHost();
        this.readerNameOU = hardwareSettingsReaders.getReaderName();
        this.firmwareVersionOU = hardwareSettingsReaders.getFirmwareVer();

        this.osVersionOU = hardwareSettings.getoSVer();
        this.sqlVersionOU = orgSync.getSqlServerVersion();
        this.dotNetVersionOU = hardwareSettings.getDotNetVer();
        this.cpuVersionOU = hardwareSettings.getCpuHost();

        this.remoteAddressFeeding = hardwareSettings.getIpHost();
        this.readerNameFeeding = hardwareSettingsReaders.getReaderName();
        this.firmwareVersionFeeding = hardwareSettingsReaders.getFirmwareVer();

        this.osVersionFeeding = hardwareSettings.getoSVer();
        this.sqlVersionFeeding = orgSync.getSqlServerVersion();
        this.dotNetVersionFeeding = hardwareSettings.getDotNetVer();
        this.cpuVersionFeeding = hardwareSettings.getCpuHost();


        this.remoteAddressGuard = hardwareSettings.getIpHost();
        this.readerNameGuard = hardwareSettingsReaders.getReaderName();
        this.firmwareVersionGuard = hardwareSettingsReaders.getFirmwareVer();

        this.osVersionGuard = hardwareSettings.getoSVer();
        this.sqlVersionGuard = orgSync.getSqlServerVersion();
        this.dotNetVersionGuard = hardwareSettings.getDotNetVer();
        this.cpuVersionGuard = hardwareSettings.getCpuHost();


        this.turnstileId = turnstileSettings.getTurnstileId();
        this.numOfEntries = turnstileSettings.getNumOfEntries();
        this.controllerModel = turnstileSettings.getControllerModel();
        this.controllerFirmwareVersion = turnstileSettings.getControllerFirmwareVersion();
        this.isWorkWithLongIds = turnstileSettings.getIsReadsLongIdsIncorrectly();
    }

    public HardwareSettingsReportItem(Org org, HardwareSettings hardwareSettings,
            HardwareSettingsMT hardwareSettingsMT) {

    }

    //public HardwareSettingsReportItem(Org org, HardwareSettings hardwareSettings,
    //        HardwareSettingsReaders hardwareSettingsReaders, OrgSync orgSync, TurnstileSettings turnstileSettings,
    //        HardwareSettingsMT hardwareSettingsMT) {
    //    this.orgNumberInName = org.getOrgNumberInName();
    //    this.idOfOrg = org.getIdOfOrg();
    //    this.shortName = org.getShortName();
    //    this.shortNameInfoService = org.getShortNameInfoService();
    //    this.district = org.getDistrict();
    //    this.shortAddress = org.getShortAddress();
    //    this.type = org.getType();
    //
    //    //АРМ ОУ
    //    if (hardwareSettingsMT.getModuleType().equals(ADMINISTRATOR)) {
    //        this.clientVersion = orgSync.getClientVersion();
    //        this.dataBaseSize = orgSync.getDatabaseSize();
    //        this.remoteAddressOU = hardwareSettings.getIpHost();
    //        this.readerNameOU = hardwareSettingsReaders.getReaderName();
    //        this.firmwareVersionOU = hardwareSettingsReaders.getFirmwareVer();
    //        this.osVersionOU = hardwareSettings.getoSVer();
    //        this.sqlVersionOU = orgSync.getSqlServerVersion();
    //        this.dotNetVersionOU = hardwareSettings.getDotNetVer();
    //        this.cpuVersionOU = hardwareSettings.getCpuHost();
    //    }
    //
    //    if (hardwareSettingsMT.getModuleType().equals(CASHIER)) {
    //        this.remoteAddressFeeding = hardwareSettings.getIpHost();
    //        this.readerNameFeeding = hardwareSettingsReaders.getReaderName();
    //        this.firmwareVersionFeeding = hardwareSettingsReaders.getFirmwareVer();
    //        this.osVersionFeeding = hardwareSettings.getoSVer();
    //        this.sqlVersionFeeding = orgSync.getSqlServerVersion();
    //        this.dotNetVersionFeeding = hardwareSettings.getDotNetVer();
    //        this.cpuVersionFeeding = hardwareSettings.getCpuHost();
    //    }
    //
    //    if(hardwareSettingsMT.getModuleType().equals(GUARD)) {
    //        this.remoteAddressGuard = hardwareSettings.getIpHost();
    //        this.readerNameGuard = hardwareSettingsReaders.getReaderName();
    //        this.firmwareVersionGuard = hardwareSettingsReaders.getFirmwareVer();
    //        this.osVersionGuard = hardwareSettings.getoSVer();
    //        this.sqlVersionGuard = orgSync.getSqlServerVersion();
    //        this.dotNetVersionGuard = hardwareSettings.getDotNetVer();
    //        this.cpuVersionGuard = hardwareSettings.getCpuHost();
    //    }
    //
    //    this.turnstileId = turnstileSettings.getTurnstileId();
    //    this.numOfEntries = turnstileSettings.getNumOfEntries();
    //    this.controllerModel = turnstileSettings.getControllerModel();
    //    this.controllerFirmwareVersion = turnstileSettings.getControllerFirmwareVersion();
    //    this.isWorkWithLongIds = turnstileSettings.getIsReadsLongIdsIncorrectly();
    //}

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

    public String getRemoteAddressOU() {
        return remoteAddressOU;
    }

    public void setRemoteAddressOU(String remoteAddressOU) {
        this.remoteAddressOU = remoteAddressOU;
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

    public String getOsVersionOU() {
        return osVersionOU;
    }

    public void setOsVersionOU(String osVersionOU) {
        this.osVersionOU = osVersionOU;
    }

    public String getSqlVersionOU() {
        return sqlVersionOU;
    }

    public void setSqlVersionOU(String sqlVersionOU) {
        this.sqlVersionOU = sqlVersionOU;
    }

    public String getDotNetVersionOU() {
        return dotNetVersionOU;
    }

    public void setDotNetVersionOU(String dotNetVersionOU) {
        this.dotNetVersionOU = dotNetVersionOU;
    }

    public String getCpuVersionOU() {
        return cpuVersionOU;
    }

    public void setCpuVersionOU(String cpuVersionOU) {
        this.cpuVersionOU = cpuVersionOU;
    }

    public String getRamSizeOU() {
        return ramSizeOU;
    }

    public void setRamSizeOU(String ramSizeOU) {
        this.ramSizeOU = ramSizeOU;
    }

    public String getRemoteAddressFeeding() {
        return remoteAddressFeeding;
    }

    public void setRemoteAddressFeeding(String remoteAddressFeeding) {
        this.remoteAddressFeeding = remoteAddressFeeding;
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

    public String getOsVersionFeeding() {
        return osVersionFeeding;
    }

    public void setOsVersionFeeding(String osVersionFeeding) {
        this.osVersionFeeding = osVersionFeeding;
    }

    public String getSqlVersionFeeding() {
        return sqlVersionFeeding;
    }

    public void setSqlVersionFeeding(String sqlVersionFeeding) {
        this.sqlVersionFeeding = sqlVersionFeeding;
    }

    public String getDotNetVersionFeeding() {
        return dotNetVersionFeeding;
    }

    public void setDotNetVersionFeeding(String dotNetVersionFeeding) {
        this.dotNetVersionFeeding = dotNetVersionFeeding;
    }

    public String getCpuVersionFeeding() {
        return cpuVersionFeeding;
    }

    public void setCpuVersionFeeding(String cpuVersionFeeding) {
        this.cpuVersionFeeding = cpuVersionFeeding;
    }

    public String getRamSizeFeeding() {
        return ramSizeFeeding;
    }

    public void setRamSizeFeeding(String ramSizeFeeding) {
        this.ramSizeFeeding = ramSizeFeeding;
    }

    public String getRemoteAddressGuard() {
        return remoteAddressGuard;
    }

    public void setRemoteAddressGuard(String remoteAddressGuard) {
        this.remoteAddressGuard = remoteAddressGuard;
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

    public String getOsVersionGuard() {
        return osVersionGuard;
    }

    public void setOsVersionGuard(String osVersionGuard) {
        this.osVersionGuard = osVersionGuard;
    }

    public String getSqlVersionGuard() {
        return sqlVersionGuard;
    }

    public void setSqlVersionGuard(String sqlVersionGuard) {
        this.sqlVersionGuard = sqlVersionGuard;
    }

    public String getDotNetVersionGuard() {
        return dotNetVersionGuard;
    }

    public void setDotNetVersionGuard(String dotNetVersionGuard) {
        this.dotNetVersionGuard = dotNetVersionGuard;
    }

    public String getCpuVersionGuard() {
        return cpuVersionGuard;
    }

    public void setCpuVersionGuard(String cpuVersionGuard) {
        this.cpuVersionGuard = cpuVersionGuard;
    }

    public String getRamSizeGuard() {
        return ramSizeGuard;
    }

    public void setRamSizeGuard(String ramSizeGuard) {
        this.ramSizeGuard = ramSizeGuard;
    }

    public String getRemoteAddressInfo() {
        return remoteAddressInfo;
    }

    public void setRemoteAddressInfo(String remoteAddressInfo) {
        this.remoteAddressInfo = remoteAddressInfo;
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

    public String getOsVersionInfo() {
        return osVersionInfo;
    }

    public void setOsVersionInfo(String osVersionInfo) {
        this.osVersionInfo = osVersionInfo;
    }

    public String getSqlVersionInfo() {
        return sqlVersionInfo;
    }

    public void setSqlVersionInfo(String sqlVersionInfo) {
        this.sqlVersionInfo = sqlVersionInfo;
    }

    public String getDotNetVersionInfo() {
        return dotNetVersionInfo;
    }

    public void setDotNetVersionInfo(String dotNetVersionInfo) {
        this.dotNetVersionInfo = dotNetVersionInfo;
    }

    public String getCpuVersionInfo() {
        return cpuVersionInfo;
    }

    public void setCpuVersionInfo(String cpuVersionInfo) {
        this.cpuVersionInfo = cpuVersionInfo;
    }

    public String getRamSizeInfo() {
        return ramSizeInfo;
    }

    public void setRamSizeInfo(String ramSizeInfo) {
        this.ramSizeInfo = ramSizeInfo;
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

    public Integer getIsWorkWithLongIds() {
        return isWorkWithLongIds;
    }

    public void setIsWorkWithLongIds(Integer isWorkWithLongIds) {
        this.isWorkWithLongIds = isWorkWithLongIds;
    }

    public String getOrgNumberInName() {
        return orgNumberInName;
    }

    public void setOrgNumberInName(String orgNumberInName) {
        this.orgNumberInName = orgNumberInName;
    }

    public Integer getNumOfTurnstile() {
        return numOfTurnstile;
    }

    public void setNumOfTurnstile(Integer numOfTurnstile) {
        this.numOfTurnstile = numOfTurnstile;
    }
    //public String getStyle(){
    //    return (this.mainBuilding ? MAIN_BUILDING_STYLE + " " : "")
    //            +  (this.status.equals(Org.STATE_NAMES[Org.INACTIVE_STATE]) ? NOT_SERVICED_STYLE : "" );
    //}
}
