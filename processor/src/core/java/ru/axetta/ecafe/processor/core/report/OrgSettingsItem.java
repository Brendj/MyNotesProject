/*
 * Copyright (c) 2019. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.report;

import ru.axetta.ecafe.processor.core.persistence.FeedingSetting;
import ru.axetta.ecafe.processor.core.persistence.Org;
import ru.axetta.ecafe.processor.core.persistence.OrganizationStatus;
import ru.axetta.ecafe.processor.core.utils.CollectionUtils;

import org.apache.commons.lang.StringUtils;

public class OrgSettingsItem {

    //----------------- Main info --------------------//
    private Long idOfOrg;
    private String shortName;
    private String district;
    private String shortAddress;
    private String type;
    private OrganizationStatus status;

    //----------------- Реквизиты --------------------//
    private String orgNumberInName;
    private String GUID;
    private Long additionalIdBuilding;
    private Long btiUnom;
    private Long btiUnad;
    private String INN;
    private String typeInternal;
    private String armVersionNumber;
    private String defaultSupplierName;
    private String productionConfig;
    private String orgCategory;

    //----------------- Настройки питания --------------------//
    private Boolean usePaydableSubscriptionFeeding;
    private Boolean variableFeeding;
    private Boolean preordersEnabled;
    private Boolean reverseMonthOfSale;
    private Boolean denyPayPlanForTimeDifference;
    private FeedingSettingInfo info;

    //----------------- Карты --------------------//
    private Boolean oneActiveCard;
    private Boolean enableDuplicateCard;
    private Boolean needVerifyCardSign;
    private Boolean multiCardModeEnabled;

    //----------------- Other --------------------//
    private Boolean requestForVisitsToOtherOrg;
    private Boolean isWorkInSummerTime;

    //----------------- Служебные переменные --------------------//
    private Boolean mainBuilding;
    private Boolean changed;

    private final String MAIN_BUILDING_STYLE = "mainBuilding";
    private final String NOT_SERVICED_STYLE = "notServiced";
    //----------------------------------------------------------//

    public OrgSettingsItem(Org org, FeedingSetting setting) {
        if(org == null){
            info = new FeedingSettingInfo(setting);
            return;
        }
        this.idOfOrg = org.getIdOfOrg();
        this.shortName = org.getShortName();
        this.district = org.getDistrict();
        this.shortAddress = org.getShortAddress();
        this.type = org.getType().getShortType();
        this.status = org.getStatus();

        this.orgNumberInName = org.getOrgNumberInName();
        this.GUID = org.getGuid();
        this.additionalIdBuilding = org.getAdditionalIdBuilding();
        this.btiUnom = org.getBtiUnom();
        this.btiUnad = org.getBtiUnad();
        this.INN = org.getINN();
        this.typeInternal = org.getTypeInitial().getShortType();
        this.defaultSupplierName = org.getDefaultSupplier().getContragentName();
        this.productionConfig = org.getConfigurationProvider() == null ? "Информации о производственной конфигурации нет" : org.getConfigurationProvider().getName();
        this.orgCategory = CollectionUtils.isEmpty(org.getCategories()) ? "Организация не принадлежит ни к одной категории" : StringUtils.join(org.getCategories(), ", ");

        this.usePaydableSubscriptionFeeding = org.getUsePaydableSubscriptionFeeding();
        this.variableFeeding = org.getVariableFeeding();
        this.preordersEnabled = org.getPreordersEnabled();
        this.reverseMonthOfSale = org.getReverseMonthOfSale();
        this.denyPayPlanForTimeDifference = org.getDenyPayPlanForTimeDifference();
        this.info = new FeedingSettingInfo(setting);

        this.oneActiveCard = org.getOneActiveCard();
        this.enableDuplicateCard = org.isCardDuplicateEnabled();
        this.needVerifyCardSign = org.getNeedVerifyCardSign();
        this.multiCardModeEnabled = org.multiCardModeIsEnabled();

        this.requestForVisitsToOtherOrg = org.getRequestForVisitsToOtherOrg();
        this.isWorkInSummerTime = org.getIsWorkInSummerTime();

        this.mainBuilding = org.isMainBuilding();
        this.changed = false;
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

    public OrganizationStatus getStatus() {
        return status;
    }

    public void setStatus(OrganizationStatus status) {
        this.status = status;
    }

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }

    public Long getAdditionalIdBuilding() {
        return additionalIdBuilding;
    }

    public void setAdditionalIdBuilding(Long additionalIdBuilding) {
        this.additionalIdBuilding = additionalIdBuilding;
    }

    public Long getBtiUnom() {
        return btiUnom;
    }

    public void setBtiUnom(Long btiUnom) {
        this.btiUnom = btiUnom;
    }

    public Long getBtiUnad() {
        return btiUnad;
    }

    public void setBtiUnad(Long btiUnad) {
        this.btiUnad = btiUnad;
    }

    public String getINN() {
        return INN;
    }

    public void setINN(String INN) {
        this.INN = INN;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDefaultSupplierName() {
        return defaultSupplierName;
    }

    public void setDefaultSupplierName(String defaultSupplierName) {
        this.defaultSupplierName = defaultSupplierName;
    }

    public String getProductionConfig() {
        return productionConfig;
    }

    public void setProductionConfig(String productionConfig) {
        this.productionConfig = productionConfig;
    }

    public Boolean getChanged() {
        return changed;
    }

    public void setChanged(Boolean changed) {
        this.changed = changed;
    }

    public String getOrgCategory() {
        return orgCategory;
    }

    public void setOrgCategory(String orgCategory) {
        this.orgCategory = orgCategory;
    }

    public Boolean getUsePaydableSubscriptionFeeding() {
        return usePaydableSubscriptionFeeding;
    }

    public void setUsePaydableSubscriptionFeeding(Boolean usePaydableSubscriptionFeeding) {
        this.usePaydableSubscriptionFeeding = usePaydableSubscriptionFeeding;
    }

    public Boolean getVariableFeeding() {
        return variableFeeding;
    }

    public void setVariableFeeding(Boolean variableFeeding) {
        this.variableFeeding = variableFeeding;
    }

    public Boolean getDenyPayPlanForTimeDifference() {
        return denyPayPlanForTimeDifference;
    }

    public void setDenyPayPlanForTimeDifference(Boolean denyPayPlanForTimeDifference) {
        this.denyPayPlanForTimeDifference = denyPayPlanForTimeDifference;
    }

    public Boolean getNeedVerifyCardSign() {
        return needVerifyCardSign;
    }

    public void setNeedVerifyCardSign(Boolean needVerifyCardSign) {
        this.needVerifyCardSign = needVerifyCardSign;
    }

    public Boolean getWorkInSummerTime() {
        return isWorkInSummerTime;
    }

    public void setWorkInSummerTime(Boolean workInSummerTime) {
        isWorkInSummerTime = workInSummerTime;
    }

    public Boolean getMainBuilding() {
        return mainBuilding;
    }

    public void setMainBuilding(Boolean mainBuilding) {
        this.mainBuilding = mainBuilding;
    }

    // For web-page
    public String getStyle(){
        return (this.mainBuilding ? MAIN_BUILDING_STYLE + " " : "")
            +  (!this.status.equals(OrganizationStatus.ACTIVE) ? NOT_SERVICED_STYLE : "" );
    }

    public void isChangedWhenModify(){
        this.changed = true;
    }

    public Boolean getPreordersEnabled() {
        return preordersEnabled;
    }

    public void setPreordersEnabled(Boolean preordersEnabled) {
        this.preordersEnabled = preordersEnabled;
    }

    public Boolean getReverseMonthOfSale() {
        return reverseMonthOfSale;
    }

    public void setReverseMonthOfSale(Boolean reverseMonthOfSale) {
        this.reverseMonthOfSale = reverseMonthOfSale;
    }

    public Boolean getOneActiveCard() {
        return oneActiveCard;
    }

    public void setOneActiveCard(Boolean oneActiveCard) {
        this.oneActiveCard = oneActiveCard;
    }

    public Boolean getMultiCardModeEnabled() {
        return multiCardModeEnabled;
    }

    public void setMultiCardModeEnabled(Boolean multiCardModeEnabled) {
        this.multiCardModeEnabled = multiCardModeEnabled;
    }

    public Boolean getRequestForVisitsToOtherOrg() {
        return requestForVisitsToOtherOrg;
    }

    public void setRequestForVisitsToOtherOrg(Boolean requestForVisitsToOtherOrg) {
        this.requestForVisitsToOtherOrg = requestForVisitsToOtherOrg;
    }

    public Boolean getEnableDuplicateCard() {
        return enableDuplicateCard;
    }

    public void setEnableDuplicateCard(Boolean enableDuplicateCard) {
        this.enableDuplicateCard = enableDuplicateCard;
    }

    public String getTypeInternal() {
        return typeInternal;
    }

    public void setTypeInternal(String typeInternal) {
        this.typeInternal = typeInternal;
    }

    @Override
    public boolean equals(Object o){
        if(this == o){
            return true;
        }
        if (!(o instanceof OrgSettingsItem)) {
            return false;
        }
        final OrgSettingsItem item = (OrgSettingsItem) o;
        return idOfOrg.equals(item.getIdOfOrg());
    }

    @Override
    public int hashCode() {
        return idOfOrg.hashCode();
    }

    public FeedingSettingInfo getInfo() {
        return info;
    }

    public void setInfo(FeedingSettingInfo info) {
        this.info = info;
    }

    public String getArmVersionNumber() {
        return armVersionNumber;
    }

    public void setArmVersionNumber(String armVersionNumber) {
        this.armVersionNumber = armVersionNumber;
    }

    public class FeedingSettingInfo{
        private Long idOfSetting = -1L;
        private Long limit = 0L;
        private String settingName = "Лимит не установлен";

        public FeedingSettingInfo(FeedingSetting feedingSetting){
            if(feedingSetting == null){
                return;
            }
            this.idOfSetting = feedingSetting.getIdOfSetting();
            this.limit = feedingSetting.getLimit();
            this.settingName = feedingSetting.getSettingName();
        }

        public Long getIdOfSetting() {
            return idOfSetting;
        }

        public void setIdOfSetting(Long idOfSetting) {
            this.idOfSetting = idOfSetting;
        }

        public Long getLimit() {
            return limit;
        }

        public void setLimit(Long limit) {
            this.limit = limit;
        }

        public String getSettingName() {
            return settingName;
        }

        public void setSettingName(String settingName) {
            this.settingName = settingName;
        }
    }
}
