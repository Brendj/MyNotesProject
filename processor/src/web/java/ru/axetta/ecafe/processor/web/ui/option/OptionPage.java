/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 11.11.11
 * Time: 13:45
 * To change this template use File | Settings | File Templates.
 */
@Component
public class OptionPage extends BasicWorkspacePage {

    private Boolean notifyBySMSAboutEnterEvent;
    private Boolean withOperator;
    private Boolean cleanMenu;
    private Integer menuDaysForDeletion;

    public Boolean getWithOperator() {
        return withOperator;
    }

    public void setWithOperator(Boolean withOperator) {
        this.withOperator = withOperator;
    }

    public Boolean getNotifyBySMSAboutEnterEvent() {
        return notifyBySMSAboutEnterEvent;
    }

    public void setNotifyBySMSAboutEnterEvent(Boolean notifyBySMSAboutEnterEvent) {
        this.notifyBySMSAboutEnterEvent = notifyBySMSAboutEnterEvent;
    }

    public Boolean getCleanMenu() {
        return cleanMenu;
    }

    public void setCleanMenu(Boolean cleanMenu) {
        this.cleanMenu = cleanMenu;
    }

    public Integer getMenuDaysForDeletion() {
        return menuDaysForDeletion;
    }

    public void setMenuDaysForDeletion(Integer menuDaysForDeletion) {
        this.menuDaysForDeletion = menuDaysForDeletion;
    }

    public String getPageFilename() {
        return "option/option";
    }

    @Autowired
    RuntimeContext runtimeContext;

    @Override
    public void onShow() throws Exception {
        withOperator = runtimeContext.getOptionValueBool(Option.OPTION_WITH_OPERATOR);
        notifyBySMSAboutEnterEvent = runtimeContext.getOptionValueBool(Option.OPTION_NOTIFY_BY_SMS_ABOUT_ENTER_EVENT);
        cleanMenu = runtimeContext.getOptionValueBool(Option.OPTION_CLEAN_MENU);
        menuDaysForDeletion = runtimeContext.getOptionValueInt(Option.OPTION_MENU_DAYS_FOR_DELETION);
    }

    public Object save() {
        try {
            runtimeContext.setOptionValue(Option.OPTION_WITH_OPERATOR, withOperator);
            runtimeContext.setOptionValue(Option.OPTION_NOTIFY_BY_SMS_ABOUT_ENTER_EVENT, notifyBySMSAboutEnterEvent);
            runtimeContext.setOptionValue(Option.OPTION_CLEAN_MENU, cleanMenu);
            runtimeContext.setOptionValue(Option.OPTION_MENU_DAYS_FOR_DELETION, menuDaysForDeletion);
            runtimeContext.saveOptionValues();
            printMessage("Настройки сохранены. Для применения необходим перезапуск");
        } catch (Exception e) {
            logAndPrintMessage("Ошибка при сохранении", e);
        }
        return null;
    }

    public Object cancel() throws Exception {
        onShow();
        return null;
    }
}
