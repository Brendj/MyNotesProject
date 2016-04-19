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
public class OptionsSecurityPage extends BasicWorkspacePage {

    private Integer periodBlockLoginReUse;
    private Integer periodBlockUnusedLogin;
    private Integer periodSmsCodeAlive;

    public void fill(Session session) {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        periodBlockLoginReUse = runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_PERIOD_BLOCK_LOGIN_REUSE);
        periodBlockUnusedLogin = runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_PERIOD_BLOCK_UNUSED_LOGIN_AFTER);
        periodSmsCodeAlive = runtimeContext.getOptionValueInt(Option.OPTION_SECURITY_PERIOD_SMS_CODE_ALIVE);
    }

    public void save() {
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        runtimeContext.setOptionValue(Option.OPTION_SECURITY_PERIOD_BLOCK_LOGIN_REUSE,
                periodBlockLoginReUse);
        runtimeContext.setOptionValue(Option.OPTION_SECURITY_PERIOD_BLOCK_UNUSED_LOGIN_AFTER, periodBlockUnusedLogin);
        runtimeContext.setOptionValue(Option.OPTION_SECURITY_PERIOD_SMS_CODE_ALIVE, periodSmsCodeAlive);
        RuntimeContext.getInstance().saveOptionValues();
        printMessage("Настройки сохранены. Для применения необходим перезапуск");
    }

    public Integer getPeriodBlockLoginReUse() {
        return periodBlockLoginReUse;
    }

    public void setPeriodBlockLoginReUse(Integer periodBlockLoginReUse) {
        this.periodBlockLoginReUse = periodBlockLoginReUse;
    }

    @Override
    public String getPageFilename() {
        return "option/security/options";
    }

    public Integer getPeriodBlockUnusedLogin() {
        return periodBlockUnusedLogin;
    }

    public void setPeriodBlockUnusedLogin(Integer periodBlockUnusedLogin) {
        this.periodBlockUnusedLogin = periodBlockUnusedLogin;
    }

    public Integer getPeriodSmsCodeAlive() {
        return periodSmsCodeAlive;
    }

    public void setPeriodSmsCodeAlive(Integer periodSmsCodeAlive) {
        this.periodSmsCodeAlive = periodSmsCodeAlive;
    }
}
