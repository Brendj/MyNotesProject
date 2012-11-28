/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Bank;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.banks.BankListPage;
import ru.axetta.ecafe.processor.web.ui.option.banks.BankOptionItem;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 11.11.11
 * Time: 13:45
 * To change this template use File | Settings | File Templates.
 */
@Component
@Scope("session")
public class OptionPage extends BasicWorkspacePage {

    final Logger logger = LoggerFactory.getLogger(BasicWorkspacePage.class);
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
    private Boolean smppClientStatus;
    private Boolean exportBIData;
    private String exportBIDataDirectory;
    private Boolean exportProjectStateData;
    private Boolean importMSRData;
    private String importMSRURL;
    private String importMSRLogin;
    private String importMSRPassword;
    private Boolean importMSRLogging;

    @PersistenceContext
    private EntityManager entityManager;

    private List<BankOptionItem> banks;


    public List<BankOptionItem> getBanks() {
        return banks;
    }

    public void setBanks(List<BankOptionItem> banks) {
        this.banks = banks;
    }

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

    public Boolean getSmppClientStatus() {
        return smppClientStatus;
    }

    public void setSmppClientStatus(Boolean smppClientStatus) {
        this.smppClientStatus = smppClientStatus;
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

    public Boolean getExportBIData() {
        return exportBIData;
    }

    public void setExportBIData(Boolean exportBIData) {
        this.exportBIData = exportBIData;
    }

    public String getExportBIDataDirectory() {
        return exportBIDataDirectory;
    }

    public void setExportBIDataDirectory(String exportBIDataDirectory) {
        this.exportBIDataDirectory = exportBIDataDirectory;
    }

    public Boolean getExportProjectStateData() {
        return exportProjectStateData;
    }

    public void setExportProjectStateData(Boolean exportProjectStateData) {
        this.exportProjectStateData = exportProjectStateData;
    }

    public Boolean getImportMSRData() {
        return importMSRData;
    }

    public void setImportMSRData(Boolean importMSRData) {
        this.importMSRData = importMSRData;
    }

    public String getImportMSRURL() {
        return importMSRURL;
    }

    public void setImportMSRURL(String importMSRURL) {
        this.importMSRURL = importMSRURL;
    }

    public String getImportMSRLogin() {
        return importMSRLogin;
    }

    public void setImportMSRLogin(String importMSRLogin) {
        this.importMSRLogin = importMSRLogin;
    }

    public String getImportMSRPassword() {
        return importMSRPassword;
    }

    public void setImportMSRPassword(String importMSRPassword) {
        this.importMSRPassword = importMSRPassword;
    }

    public Boolean getImportMSRLogging() {
        return importMSRLogging;
    }

    public void setImportMSRLogging(Boolean importMSRLogging) {
        this.importMSRLogging = importMSRLogging;
    }

    public String getPageFilename() {
        return "option/option";
    }

    @Autowired
    RuntimeContext runtimeContext;

    @Autowired
    private BankListPage bankListPage;

    @Override
    public void onShow() throws Exception {
        withOperator = runtimeContext.getOptionValueBool(Option.OPTION_WITH_OPERATOR);
        notifyBySMSAboutEnterEvent = runtimeContext.getOptionValueBool(Option.OPTION_NOTIFY_BY_SMS_ABOUT_ENTER_EVENT);
        cleanMenu = runtimeContext.getOptionValueBool(Option.OPTION_CLEAN_MENU);
        menuDaysForDeletion = runtimeContext.getOptionValueInt(Option.OPTION_MENU_DAYS_FOR_DELETION);
        journalTransactions = runtimeContext.getOptionValueBool(Option.OPTION_JOURNAL_TRANSACTIONS);
        sendJournalTransactionsToNFP = runtimeContext.getOptionValueBool(Option.OPTION_SEND_JOURNAL_TRANSACTIONS_TO_NFP);
        nfpServiceAddress = runtimeContext.getOptionValueString(Option.OPTION_NFP_SERVICE_ADDRESS);
        chronopaySection = runtimeContext.getOptionValueBool(Option.OPTION_CHRONOPAY_SECTION);
        rbkSection = runtimeContext.getOptionValueBool(Option.OPTION_RBK_SECTION);
        rbkRate = runtimeContext.getOptionValueDouble(Option.OPTION_RBK_RATE);
        chronopayRate = runtimeContext.getOptionValueDouble(Option.OPTION_CHRONOPAY_RATE);
        smppClientStatus = runtimeContext.getOptionValueBool(Option.OPTION_SMPP_CLIENT_STATUS);
        exportBIData = runtimeContext.getOptionValueBool(Option.OPTION_EXPORT_BI_DATA_ON);
        exportBIDataDirectory = runtimeContext.getOptionValueString(Option.OPTION_EXPORT_BI_DATA_DIR);
        exportProjectStateData = runtimeContext.getOptionValueBool(Option.OPTION_PROJECT_STATE_REPORT_ON);
        importMSRData = runtimeContext.getOptionValueBool(Option.OPTION_MSR_STOPLIST_ON);
        importMSRLogin = runtimeContext.getOptionValueString(Option.OPTION_MSR_STOPLIST_USER);
        importMSRPassword = runtimeContext.getOptionValueString(Option.OPTION_MSR_STOPLIST_PSWD);
        importMSRURL = runtimeContext.getOptionValueString(Option.OPTION_MSR_STOPLIST_URL);
        importMSRLogging = runtimeContext.getOptionValueBool(Option.OPTION_MSR_STOPLIST_LOGGING);

        bankListPage.onShow();

        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            Criteria banksCriteria = persistenceSession.createCriteria(Bank.class);
            List<Bank> banksList = (List<Bank>) banksCriteria.list();
            banks = new ArrayList<BankOptionItem>();
            for (Bank bank : banksList) {
                BankOptionItem bankOptionItem = new BankOptionItem(entityManager);
                bankOptionItem.setEnrollmentType(bank.getEnrollmentType());
                bankOptionItem.setLogoUrl(bank.getLogoUrl());
                bankOptionItem.setMinRate(bank.getMinRate());
                bankOptionItem.setName(bank.getName());
                bankOptionItem.setTerminalsUrl(bank.getTerminalsUrl());
                bankOptionItem.setRate(bank.getRate());
                bankOptionItem.setIdOfBank(bank.getIdOfBank());
                banks.add(bankOptionItem);
            }
            persistenceSession.flush();
            persistenceTransaction.commit();
            persistenceTransaction = null;
        } catch (Exception e) {

            logger.error("error in banks: ", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }

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
            runtimeContext.setOptionValue(Option.OPTION_CHRONOPAY_SECTION, chronopaySection);
            runtimeContext.setOptionValue(Option.OPTION_RBK_SECTION, rbkSection);
            runtimeContext.setOptionValue(Option.OPTION_RBK_RATE, rbkRate);
            runtimeContext.setOptionValue(Option.OPTION_CHRONOPAY_RATE, chronopayRate);
            runtimeContext.setOptionValue(Option.OPTION_SMPP_CLIENT_STATUS, smppClientStatus);

            bankListPage.save();

            runtimeContext.getPartnerChronopayConfig().setShow(chronopaySection);
            runtimeContext.getPartnerRbkMoneyConfig().setShow(rbkSection);

            runtimeContext.setOptionValue(Option.OPTION_DEFAULT_OVERDRAFT_LIMIT, defaultOverdraftLimit);
            runtimeContext.setOptionValue(Option.OPTION_DEFAULT_EXPENDITURE_LIMIT, defaultExpenditureLimit);

            runtimeContext.getPartnerChronopayConfig().setRate(chronopayRate);
            runtimeContext.getPartnerRbkMoneyConfig().setRate(rbkRate);


            /*runtimeContext.setOptionValueWithSave (Option.OPTION_EXPORT_BI_DATA_ON, exportBIData);
            runtimeContext.setOptionValueWithSave (Option.OPTION_EXPORT_BI_DATA_DIR, exportBIDataDirectory);
            runtimeContext.setOptionValueWithSave (Option.OPTION_PROJECT_STATE_REPORT_ON, exportProjectStateData);
            runtimeContext.setOptionValueWithSave (Option.OPTION_MSR_STOPLIST_ON, importMSRData);
            runtimeContext.setOptionValueWithSave (Option.OPTION_MSR_STOPLIST_USER, importMSRLogin);
            runtimeContext.setOptionValueWithSave (Option.OPTION_MSR_STOPLIST_PSWD, importMSRPassword);
            runtimeContext.setOptionValueWithSave (Option.OPTION_MSR_STOPLIST_URL, importMSRURL);
            runtimeContext.setOptionValueWithSave (Option.OPTION_MSR_STOPLIST_LOGGING, importMSRLogging);*/
            runtimeContext.setOptionValue(Option.OPTION_EXPORT_BI_DATA_ON, exportBIData);
            runtimeContext.setOptionValue(Option.OPTION_EXPORT_BI_DATA_DIR, exportBIDataDirectory);
            runtimeContext.setOptionValue(Option.OPTION_PROJECT_STATE_REPORT_ON, exportProjectStateData);
            runtimeContext.setOptionValue(Option.OPTION_MSR_STOPLIST_ON, importMSRData);
            runtimeContext.setOptionValue(Option.OPTION_MSR_STOPLIST_USER, importMSRLogin);
            runtimeContext.setOptionValue(Option.OPTION_MSR_STOPLIST_PSWD, importMSRPassword);
            runtimeContext.setOptionValue(Option.OPTION_MSR_STOPLIST_URL, importMSRURL);
            runtimeContext.setOptionValue(Option.OPTION_MSR_STOPLIST_LOGGING, importMSRLogging);

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
