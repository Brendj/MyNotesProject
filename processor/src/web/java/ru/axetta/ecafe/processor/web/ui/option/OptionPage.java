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
    private Boolean journalTransactions;
    private Boolean sendJournalTransactionsToNFP;
    private String nfpServiceAddress;
    private Boolean chronopaySection;
    private Boolean rbkSection;
    private Double chronopayRate;
    private Double rbkRate;
    private Long defaultOverdraftLimit, defaultExpenditureLimit;

    public Double getRbkRate() {
        return rbkRate;
    }

    public void setRbkRate(Double rbkRate) {
        this.rbkRate = rbkRate;
    }

    public Double getChronopayRate() {
        return chronopayRate;
    }

    public void setChronopayRate(Double chronopayRate) {
        this.chronopayRate = chronopayRate;
    }

    public Boolean getChronopaySection() {
        return chronopaySection;
    }

    public void setChronopaySection(Boolean chronopaySection) {
        this.chronopaySection = chronopaySection;
    }

    public Boolean getRbkSection() {
        return rbkSection;
    }

    public void setRbkSection(Boolean rbkSection) {
        this.rbkSection = rbkSection;
    }

    public Boolean getSendJournalTransactionsToNFP() {
        return sendJournalTransactionsToNFP;
    }

    public void setSendJournalTransactionsToNFP(Boolean sendJournalTransactionsToNFP) {
        this.sendJournalTransactionsToNFP = sendJournalTransactionsToNFP;
    }

    public String getNfpServiceAddress() {
        return nfpServiceAddress;
    }

    public void setNfpServiceAddress(String nfpServiceAddress) {
        this.nfpServiceAddress = nfpServiceAddress;
    }

    public Boolean getJournalTransactions() {
        return journalTransactions;
    }

    public void setJournalTransactions(Boolean journalTransactions) {
        this.journalTransactions = journalTransactions;
    }

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

    public Long getDefaultExpenditureLimit() {
        return defaultExpenditureLimit;
    }

    public void setDefaultExpenditureLimit(Long defaultExpenditureLimit) {
        this.defaultExpenditureLimit = defaultExpenditureLimit;
    }

    public Long getDefaultOverdraftLimit() {
        return defaultOverdraftLimit;
    }

    public void setDefaultOverdraftLimit(Long defaultOverdraftLimit) {
        this.defaultOverdraftLimit = defaultOverdraftLimit;
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
        journalTransactions = runtimeContext.getOptionValueBool(Option.OPTION_JOURNAL_TRANSACTIONS);
        sendJournalTransactionsToNFP = runtimeContext.getOptionValueBool(Option.OPTION_SEND_JOURNAL_TRANSACTIONS_TO_NFP);
        nfpServiceAddress = runtimeContext.getOptionValueString(Option.OPTION_NFP_SERVICE_ADDRESS);
        chronopaySection=runtimeContext.getOptionValueBool(Option.OPTION_CHRONOPAY_SECTION);
        rbkSection=runtimeContext.getOptionValueBool(Option.OPTION_RBK_SECTION);
        rbkRate=runtimeContext.getOptionValueDouble(Option.OPTION_RBK_RATE);
        chronopayRate=runtimeContext.getOptionValueDouble(Option.OPTION_CHRONOPAY_RATE);
        defaultOverdraftLimit = runtimeContext.getOptionValueLong(Option.OPTION_DEFAULT_OVERDRAFT_LIMIT);
        defaultExpenditureLimit = runtimeContext.getOptionValueLong(Option.OPTION_DEFAULT_EXPENDITURE_LIMIT);
    }

    public Object save() {
        try {
            runtimeContext.setOptionValue(Option.OPTION_WITH_OPERATOR, withOperator);
            runtimeContext.setOptionValue(Option.OPTION_NOTIFY_BY_SMS_ABOUT_ENTER_EVENT, notifyBySMSAboutEnterEvent);
            runtimeContext.setOptionValue(Option.OPTION_CLEAN_MENU, cleanMenu);
            runtimeContext.setOptionValue(Option.OPTION_MENU_DAYS_FOR_DELETION, menuDaysForDeletion);
            runtimeContext.setOptionValue(Option.OPTION_JOURNAL_TRANSACTIONS, journalTransactions);
            runtimeContext.setOptionValue(Option.OPTION_SEND_JOURNAL_TRANSACTIONS_TO_NFP, sendJournalTransactionsToNFP);
            runtimeContext.setOptionValue(Option.OPTION_NFP_SERVICE_ADDRESS, nfpServiceAddress);
            runtimeContext.setOptionValue(Option.OPTION_CHRONOPAY_SECTION,chronopaySection);
            runtimeContext.setOptionValue(Option.OPTION_RBK_SECTION, rbkSection);
            runtimeContext.setOptionValue(Option.OPTION_RBK_RATE, rbkRate);
            runtimeContext.setOptionValue(Option.OPTION_CHRONOPAY_RATE, chronopayRate);
            runtimeContext.setOptionValue(Option.OPTION_DEFAULT_OVERDRAFT_LIMIT, defaultOverdraftLimit);
            runtimeContext.setOptionValue(Option.OPTION_DEFAULT_EXPENDITURE_LIMIT, defaultExpenditureLimit);
            runtimeContext.getPartnerChronopayConfig().setRate(chronopayRate);
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
