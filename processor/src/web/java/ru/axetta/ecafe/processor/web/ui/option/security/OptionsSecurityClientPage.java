/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.security;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

/**
 * Created with IntelliJ IDEA.
 * User: i.semenov
 * Date: 15.04.16
 * Time: 13:44
 * To change this template use File | Settings | File Templates.
 */
public class OptionsSecurityClientPage extends BasicWorkspacePage {

    private Integer clientPeriodBlockLoginReUse;
    private Integer clientPeriodBlockUnusedLogin;
    private Integer clientPeriodPasswordChange;
    private Integer clientMaxAuthFaultCount;
    private Integer clientTmpBlockAccTime;
    private Integer armAdminUserIdleTimeout;
    private Integer armCashierUserIdleTimeout;
    private Integer armSecurityUserIdleTimeout;
    private Integer armLibraryUserIdleTimeout;
    private Boolean armAdminAuthWithoutCardForAdmin;
    private Boolean armAdminAuthWithoutCardForOther;
    private Boolean armCashierAuthWithoutCard;
    private Boolean armSecurityAuthWithoutCard;
    private Boolean armLibraryAuthWithoutCard;

    public void fill(Session session) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        clientPeriodBlockLoginReUse = runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_CLIENT_PERIOD_BLOCK_LOGIN_REUSE);
        clientPeriodBlockUnusedLogin = runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_CLIENT_PERIOD_BLOCK_UNUSED_LOGIN_AFTER);
        clientPeriodPasswordChange = runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_CLIENT_PERIOD_PASSWORD_CHANGE);
        clientMaxAuthFaultCount = runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_CLIENT_MAX_AUTH_FAULT_COUNT);
        clientTmpBlockAccTime = runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_CLIENT_TMP_BLOCK_ACC_TIME);

        armAdminUserIdleTimeout = runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_CLIENT_USER_IDLE_TIMEOUT_ARM_ADMIN);
        armCashierUserIdleTimeout = runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_CLIENT_USER_IDLE_TIMEOUT_ARM_CASHIER);
        armSecurityUserIdleTimeout = runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_CLIENT_USER_IDLE_TIMEOUT_ARM_SECURITY);
        armLibraryUserIdleTimeout = runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_CLIENT_USER_IDLE_TIMEOUT_ARM_LIBRARY);

        armAdminAuthWithoutCardForAdmin = runtimeContext.getOptionValueBool(Option.OPTION_SECURITY_CLIENT_AUTH_WITHOUT_CARD_ARM_ADMIN_FOR_ADMIN);
        armAdminAuthWithoutCardForOther = runtimeContext.getOptionValueBool(Option.OPTION_SECURITY_CLIENT_AUTH_WITHOUT_CARD_ARM_ADMIN_FOR_OTHER);
        armCashierAuthWithoutCard = runtimeContext.getOptionValueBool(Option.OPTION_SECURITY_CLIENT_AUTH_WITHOUT_CARD_ARM_CASHIER);
        armSecurityAuthWithoutCard = runtimeContext.getOptionValueBool(Option.OPTION_SECURITY_CLIENT_AUTH_WITHOUT_CARD_ARM_SECURITY);
        armLibraryAuthWithoutCard = runtimeContext.getOptionValueBool(Option.OPTION_SECURITY_CLIENT_AUTH_WITHOUT_CARD_ARM_LIBRARY);
    }

    public void save() {
        if (armAdminUserIdleTimeout < 15 || armCashierUserIdleTimeout < 5 || armSecurityUserIdleTimeout < 15 || armLibraryUserIdleTimeout < 15) {
            printError("Время автоматического выхода из УЗ пользователя должно быть не менее 15 минут в каждом модуле");
            return;
        }
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        runtimeContext.setOptionValue(Option.OPTION_SECURITY_CLIENT_PERIOD_BLOCK_LOGIN_REUSE, clientPeriodBlockLoginReUse);
        runtimeContext.setOptionValue(Option.OPTION_SECURITY_CLIENT_PERIOD_BLOCK_UNUSED_LOGIN_AFTER, clientPeriodBlockUnusedLogin);
        runtimeContext.setOptionValue(Option.OPTION_SECURITY_CLIENT_PERIOD_PASSWORD_CHANGE, clientPeriodPasswordChange);
        runtimeContext.setOptionValue(Option.OPTION_SECURITY_CLIENT_MAX_AUTH_FAULT_COUNT, clientMaxAuthFaultCount);
        runtimeContext.setOptionValue(Option.OPTION_SECURITY_CLIENT_TMP_BLOCK_ACC_TIME, clientTmpBlockAccTime);
        runtimeContext.setOptionValue(Option.OPTION_SECURITY_CLIENT_USER_IDLE_TIMEOUT_ARM_ADMIN, armAdminUserIdleTimeout);
        runtimeContext.setOptionValue(Option.OPTION_SECURITY_CLIENT_USER_IDLE_TIMEOUT_ARM_CASHIER, armCashierUserIdleTimeout);
        runtimeContext.setOptionValue(Option.OPTION_SECURITY_CLIENT_USER_IDLE_TIMEOUT_ARM_SECURITY, armSecurityUserIdleTimeout);
        runtimeContext.setOptionValue(Option.OPTION_SECURITY_CLIENT_USER_IDLE_TIMEOUT_ARM_LIBRARY, armLibraryUserIdleTimeout);
        runtimeContext.setOptionValue(Option.OPTION_SECURITY_CLIENT_AUTH_WITHOUT_CARD_ARM_ADMIN_FOR_ADMIN, armAdminAuthWithoutCardForAdmin);
        runtimeContext.setOptionValue(Option.OPTION_SECURITY_CLIENT_AUTH_WITHOUT_CARD_ARM_ADMIN_FOR_OTHER, armAdminAuthWithoutCardForOther);
        runtimeContext.setOptionValue(Option.OPTION_SECURITY_CLIENT_AUTH_WITHOUT_CARD_ARM_CASHIER, armCashierAuthWithoutCard);
        runtimeContext.setOptionValue(Option.OPTION_SECURITY_CLIENT_AUTH_WITHOUT_CARD_ARM_SECURITY, armSecurityAuthWithoutCard);
        runtimeContext.setOptionValue(Option.OPTION_SECURITY_CLIENT_AUTH_WITHOUT_CARD_ARM_LIBRARY, armLibraryAuthWithoutCard);
        RuntimeContext.getInstance().saveOptionValues();
        printMessage("Настройки сохранены. Для применения необходим перезапуск");
    }

    @Override
    public String getPageFilename() {
        return "option/security/options_client";
    }

    public Integer getClientPeriodBlockLoginReUse() {
        return clientPeriodBlockLoginReUse;
    }

    public void setClientPeriodBlockLoginReUse(Integer clientPeriodBlockLoginReUse) {
        this.clientPeriodBlockLoginReUse = clientPeriodBlockLoginReUse;
    }

    public Integer getClientPeriodBlockUnusedLogin() {
        return clientPeriodBlockUnusedLogin;
    }

    public void setClientPeriodBlockUnusedLogin(Integer clientPeriodBlockUnusedLogin) {
        this.clientPeriodBlockUnusedLogin = clientPeriodBlockUnusedLogin;
    }

    public Integer getClientPeriodPasswordChange() {
        return clientPeriodPasswordChange;
    }

    public void setClientPeriodPasswordChange(Integer clientPeriodPasswordChange) {
        this.clientPeriodPasswordChange = clientPeriodPasswordChange;
    }

    public Integer getClientMaxAuthFaultCount() {
        return clientMaxAuthFaultCount;
    }

    public void setClientMaxAuthFaultCount(Integer clientMaxAuthFaultCount) {
        this.clientMaxAuthFaultCount = clientMaxAuthFaultCount;
    }

    public Integer getClientTmpBlockAccTime() {
        return clientTmpBlockAccTime;
    }

    public void setClientTmpBlockAccTime(Integer clientTmpBlockAccTime) {
        this.clientTmpBlockAccTime = clientTmpBlockAccTime;
    }

    public Integer getArmAdminUserIdleTimeout() {
        return armAdminUserIdleTimeout;
    }

    public void setArmAdminUserIdleTimeout(Integer armAdminUserIdleTimeout) {
        this.armAdminUserIdleTimeout = armAdminUserIdleTimeout;
    }

    public Integer getArmCashierUserIdleTimeout() {
        return armCashierUserIdleTimeout;
    }

    public void setArmCashierUserIdleTimeout(Integer armCashierUserIdleTimeout) {
        this.armCashierUserIdleTimeout = armCashierUserIdleTimeout;
    }

    public Integer getArmSecurityUserIdleTimeout() {
        return armSecurityUserIdleTimeout;
    }

    public void setArmSecurityUserIdleTimeout(Integer armSecurityUserIdleTimeout) {
        this.armSecurityUserIdleTimeout = armSecurityUserIdleTimeout;
    }

    public Integer getArmLibraryUserIdleTimeout() {
        return armLibraryUserIdleTimeout;
    }

    public void setArmLibraryUserIdleTimeout(Integer armLibraryUserIdleTimeout) {
        this.armLibraryUserIdleTimeout = armLibraryUserIdleTimeout;
    }

    public Boolean getArmAdminAuthWithoutCardForAdmin() {
        return armAdminAuthWithoutCardForAdmin;
    }

    public void setArmAdminAuthWithoutCardForAdmin(Boolean armAdminAuthWithoutCardForAdmin) {
        this.armAdminAuthWithoutCardForAdmin = armAdminAuthWithoutCardForAdmin;
    }

    public Boolean getArmAdminAuthWithoutCardForOther() {
        return armAdminAuthWithoutCardForOther;
    }

    public void setArmAdminAuthWithoutCardForOther(Boolean armAdminAuthWithoutCardForOther) {
        this.armAdminAuthWithoutCardForOther = armAdminAuthWithoutCardForOther;
    }

    public Boolean getArmCashierAuthWithoutCard() {
        return armCashierAuthWithoutCard;
    }

    public void setArmCashierAuthWithoutCard(Boolean armCashierAuthWithoutCard) {
        this.armCashierAuthWithoutCard = armCashierAuthWithoutCard;
    }

    public Boolean getArmSecurityAuthWithoutCard() {
        return armSecurityAuthWithoutCard;
    }

    public void setArmSecurityAuthWithoutCard(Boolean armSecurityAuthWithoutCard) {
        this.armSecurityAuthWithoutCard = armSecurityAuthWithoutCard;
    }

    public Boolean getArmLibraryAuthWithoutCard() {
        return armLibraryAuthWithoutCard;
    }

    public void setArmLibraryAuthWithoutCard(Boolean armLibraryAuthWithoutCard) {
        this.armLibraryAuthWithoutCard = armLibraryAuthWithoutCard;
    }
}
