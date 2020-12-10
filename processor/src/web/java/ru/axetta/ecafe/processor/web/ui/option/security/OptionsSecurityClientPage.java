/*
 * Copyright (c) 2016. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.security;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;

import java.util.ArrayList;
import java.util.List;

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
    private Integer userIdleTimeout;
    private List<SecurityClientAuthorizationItem> securityClientAuthorizationItems = new ArrayList<>();

    public void fill(Session session) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        clientPeriodBlockLoginReUse = runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_CLIENT_PERIOD_BLOCK_LOGIN_REUSE);
        clientPeriodBlockUnusedLogin = runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_CLIENT_PERIOD_BLOCK_UNUSED_LOGIN_AFTER);
        clientPeriodPasswordChange = runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_CLIENT_PERIOD_PASSWORD_CHANGE);
        clientMaxAuthFaultCount = runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_CLIENT_MAX_AUTH_FAULT_COUNT);
        clientTmpBlockAccTime = runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_CLIENT_TMP_BLOCK_ACC_TIME);
        userIdleTimeout = runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_CLIENT_USER_IDLE_TIMEOUT);
        fillSecurityClientAuthorizationItems(runtimeContext);
    }

    private void fillSecurityClientAuthorizationItems(RuntimeContext runtimeContext) {
        securityClientAuthorizationItems.clear();
        securityClientAuthorizationItems.add(new SecurityClientAuthorizationItem(Option.OPTION_SECURITY_CLIENT_AUTH_WITHOUT_CARD_ARM_ADMIN,
                runtimeContext.getOptionValueBool(Option.OPTION_SECURITY_CLIENT_AUTH_WITHOUT_CARD_ARM_ADMIN), "АРМ администратора ОО"));
        securityClientAuthorizationItems.add(new SecurityClientAuthorizationItem(Option.OPTION_SECURITY_CLIENT_AUTH_WITHOUT_CARD_ARM_CASHIER,
                runtimeContext.getOptionValueBool(Option.OPTION_SECURITY_CLIENT_AUTH_WITHOUT_CARD_ARM_CASHIER), "АРМ кассира (АРМ быстрые продажи)"));
        securityClientAuthorizationItems.add(new SecurityClientAuthorizationItem(Option.OPTION_SECURITY_CLIENT_AUTH_WITHOUT_CARD_ARM_SECURITY,
                runtimeContext.getOptionValueBool(Option.OPTION_SECURITY_CLIENT_AUTH_WITHOUT_CARD_ARM_SECURITY), "АРМ охранника"));
        securityClientAuthorizationItems.add(new SecurityClientAuthorizationItem(Option.OPTION_SECURITY_CLIENT_AUTH_WITHOUT_CARD_ARM_LIBRARY,
                runtimeContext.getOptionValueBool(Option.OPTION_SECURITY_CLIENT_AUTH_WITHOUT_CARD_ARM_LIBRARY), "АРМ библиотекаря"));
    }

    public void save() {
        if (userIdleTimeout < 15) {
            printError("Время автоматического выхода из УЗ пользователя должно быть не менее 15 минут");
            return;
        }
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        runtimeContext.setOptionValue(Option.OPTION_SECURITY_CLIENT_PERIOD_BLOCK_LOGIN_REUSE, clientPeriodBlockLoginReUse);
        runtimeContext.setOptionValue(Option.OPTION_SECURITY_CLIENT_PERIOD_BLOCK_UNUSED_LOGIN_AFTER, clientPeriodBlockUnusedLogin);
        runtimeContext.setOptionValue(Option.OPTION_SECURITY_CLIENT_PERIOD_PASSWORD_CHANGE, clientPeriodPasswordChange);
        runtimeContext.setOptionValue(Option.OPTION_SECURITY_CLIENT_MAX_AUTH_FAULT_COUNT, clientMaxAuthFaultCount);
        runtimeContext.setOptionValue(Option.OPTION_SECURITY_CLIENT_TMP_BLOCK_ACC_TIME, clientTmpBlockAccTime);
        runtimeContext.setOptionValue(Option.OPTION_SECURITY_CLIENT_USER_IDLE_TIMEOUT, userIdleTimeout);
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

    public Integer getUserIdleTimeout() {
        return userIdleTimeout;
    }

    public void setUserIdleTimeout(Integer userIdleTimeout) {
        this.userIdleTimeout = userIdleTimeout;
    }

    public List<SecurityClientAuthorizationItem> getSecurityClientAuthorizationItems() {
        return securityClientAuthorizationItems;
    }

    public void setSecurityClientAuthorizationItems(
            List<SecurityClientAuthorizationItem> securityClientAuthorizationItems) {
        this.securityClientAuthorizationItems = securityClientAuthorizationItems;
    }
}
