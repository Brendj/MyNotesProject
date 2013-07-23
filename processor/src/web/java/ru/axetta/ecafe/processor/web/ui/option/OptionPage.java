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
    private String externalURL;
    private Boolean recalculateBenefits;
    private Boolean syncRegisterClients;
    private String syncRegisterURL;
    private String syncRegisterUser;
    private String syncRegisterPassword;
    private String syncRegisterCompany;
    private Boolean disableSMSNotifyEditInClientRoom;
    private Boolean importRNIPPayments;
    private Boolean sendSMSPaymentNotification;
    private String RNIPPaymentsURL;
    private String RNIPPaymentsAlias;
    private String RNIPPaymentsPassword;
    private String RNIPPaymentsStore;
    private Boolean syncRegisterIsTestingService;
    private Boolean syncRegisterLogging;
    private Integer syncRegisterMaxAttempts;
    private Integer syncLimits;
    private Integer retryAfter;
    private String syncRegisterSupportEmail;

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

    public String getExternalURL() {
        return externalURL;
    }

    public void setExternalURL(String externalURL) {
        this.externalURL = externalURL;
    }

    public Boolean getImportMSRLogging() {
        return importMSRLogging;
    }

    public void setImportMSRLogging(Boolean importMSRLogging) {
        this.importMSRLogging = importMSRLogging;
    }

    public Boolean getRecalculateBenefits() {
        return recalculateBenefits;
    }

    public void setRecalculateBenefits(Boolean recalculateBenefits) {
        this.recalculateBenefits = recalculateBenefits;
    }

    public Boolean getSyncRegisterClients() {
        return syncRegisterClients;
    }

    public void setSyncRegisterClients(Boolean syncRegisterClients) {
        this.syncRegisterClients = syncRegisterClients;
    }

    public String getSyncRegisterURL() {
        return syncRegisterURL;
    }

    public void setSyncRegisterURL(String syncRegisterURL) {
        this.syncRegisterURL = syncRegisterURL;
    }

    public String getSyncRegisterUser() {
        return syncRegisterUser;
    }

    public void setSyncRegisterUser(String syncRegisterUser) {
        this.syncRegisterUser = syncRegisterUser;
    }

    public String getSyncRegisterPassword() {
        return syncRegisterPassword;
    }

    public void setSyncRegisterPassword(String syncRegisterPassword) {
        this.syncRegisterPassword = syncRegisterPassword;
    }

    public String getSyncRegisterCompany() {
        return syncRegisterCompany;
    }

    public void setSyncRegisterCompany(String syncRegisterCompany) {
        this.syncRegisterCompany = syncRegisterCompany;
    }

    public Boolean getDisableSMSNotifyEditInClientRoom() {
        return disableSMSNotifyEditInClientRoom;
    }

    public void setDisableSMSNotifyEditInClientRoom(Boolean disableSMSNotifyEditInClientRoom) {
        this.disableSMSNotifyEditInClientRoom = disableSMSNotifyEditInClientRoom;
    }

    public Boolean getImportRNIPPayments() {
        return importRNIPPayments;
    }

    public void setImportRNIPPayments(Boolean importRNIPPayments) {
        this.importRNIPPayments = importRNIPPayments;
    }

    public Boolean getSendSMSPaymentNotification() {
        return sendSMSPaymentNotification;
    }

    public void setSendSMSPaymentNotification(Boolean sendSMSPaymentNotification) {
        this.sendSMSPaymentNotification = sendSMSPaymentNotification;
    }

    public String getRNIPPaymentsURL() {
        return RNIPPaymentsURL;
    }

    public void setRNIPPaymentsURL(String RNIPPaymentsURL) {
        this.RNIPPaymentsURL = RNIPPaymentsURL;
    }

    public String getRNIPPaymentsAlias() {
        return RNIPPaymentsAlias;
    }

    public void setRNIPPaymentsAlias(String RNIPPaymentsAlias) {
        this.RNIPPaymentsAlias = RNIPPaymentsAlias;
    }

    public String getRNIPPaymentsPassword() {
        return RNIPPaymentsPassword;
    }

    public void setRNIPPaymentsPassword(String RNIPPaymentsPassword) {
        this.RNIPPaymentsPassword = RNIPPaymentsPassword;
    }

    public String getRNIPPaymentsStore() {
        return RNIPPaymentsStore;
    }

    public void setRNIPPaymentsStore(String RNIPPaymentsStore) {
        this.RNIPPaymentsStore = RNIPPaymentsStore;
    }

    public Boolean getSyncRegisterIsTestingService() {
        return syncRegisterIsTestingService;
    }

    public void setSyncRegisterIsTestingService(Boolean syncRegisterIsTestingService) {
        this.syncRegisterIsTestingService = syncRegisterIsTestingService;
    }

    public Boolean getSyncRegisterLogging() {
        return syncRegisterLogging;
    }

    public void setSyncRegisterLogging(Boolean syncRegisterLogging) {
        this.syncRegisterLogging = syncRegisterLogging;
    }

    public Integer getSyncRegisterMaxAttempts() {
        return syncRegisterMaxAttempts;
    }

    public void setSyncRegisterMaxAttempts(Integer syncRegisterMaxAttempts) {
        this.syncRegisterMaxAttempts = syncRegisterMaxAttempts;
    }

    public Integer getSyncLimits() {
        return syncLimits;
    }

    public void setSyncLimits(Integer syncLimits) {
        this.syncLimits = syncLimits;
    }

    public Integer getRetryAfter() {
        return retryAfter;
    }

    public void setRetryAfter(Integer retryAfter) {
        this.retryAfter = retryAfter;
    }

    public String getSyncRegisterSupportEmail() {
        return syncRegisterSupportEmail;
    }

    public void setSyncRegisterSupportEmail(String syncRegisterSupportEmail) {
        this.syncRegisterSupportEmail = syncRegisterSupportEmail;
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
        externalURL = runtimeContext.getOptionValueString(Option.OPTION_EXTERNAL_URL);
        recalculateBenefits = runtimeContext.getOptionValueBool(Option.OPTION_BENEFITS_RECALC_ON);
        syncRegisterClients = runtimeContext.getOptionValueBool(Option.OPTION_MSK_NSI_AUTOSYNC_ON);
        syncRegisterURL = runtimeContext.getOptionValueString(Option.OPTION_MSK_NSI_URL);
        syncRegisterUser = runtimeContext.getOptionValueString(Option.OPTION_MSK_NSI_USER);
        syncRegisterPassword = runtimeContext.getOptionValueString(Option.OPTION_MSK_NSI_PASSWORD);
        syncRegisterCompany = runtimeContext.getOptionValueString(Option.OPTION_MSK_NSI_COMPANY);
        disableSMSNotifyEditInClientRoom = runtimeContext.getOptionValueBool(
                Option.OPTION_DISABLE_SMSNOTIFY_EDIT_IN_CLIENT_ROOM);
        importRNIPPayments = runtimeContext.getOptionValueBool(Option.OPTION_IMPORT_RNIP_PAYMENTS_ON);
        sendSMSPaymentNotification = runtimeContext.getOptionValueBool(Option.OPTION_SEND_PAYMENT_NOTIFY_SMS_ON);
        RNIPPaymentsURL = runtimeContext.getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_URL);
        RNIPPaymentsAlias = runtimeContext.getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_ALIAS);
        RNIPPaymentsPassword = runtimeContext.getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_PASSWORD);
        RNIPPaymentsStore = runtimeContext.getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_STORE_NAME);
        syncRegisterIsTestingService = runtimeContext.getOptionValueBool(Option.OPTION_MSK_NSI_USE_TESTING_SERVICE);
        syncRegisterLogging = runtimeContext.getOptionValueBool(Option.OPTION_MSK_NSI_LOG);
        syncRegisterMaxAttempts = runtimeContext.getOptionValueInt(Option.OPTION_MSK_NSI_MAX_ATTEMPTS);
        syncRegisterSupportEmail = runtimeContext.getOptionValueString(Option.OPTION_MSK_NSI_SUPPORT_EMAIL);


        syncLimits = runtimeContext.getOptionValueInt(Option.OPTION_REQUEST_SYNC_LIMITS);
        retryAfter = runtimeContext.getOptionValueInt(Option.OPTION_REQUEST_SYNC_RETRY_AFTER);

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
                BankOptionItem bankOptionItem = new BankOptionItem();
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
            runtimeContext.setOptionValue(Option.OPTION_EXTERNAL_URL, externalURL);

            bankListPage.save();

            runtimeContext.getPartnerChronopayConfig().setShow(chronopaySection);
            runtimeContext.getPartnerRbkMoneyConfig().setShow(rbkSection);

            runtimeContext.setOptionValue(Option.OPTION_DEFAULT_OVERDRAFT_LIMIT, defaultOverdraftLimit);
            runtimeContext.setOptionValue(Option.OPTION_DEFAULT_EXPENDITURE_LIMIT, defaultExpenditureLimit);

            runtimeContext.getPartnerChronopayConfig().setRate(chronopayRate);
            runtimeContext.getPartnerRbkMoneyConfig().setRate(rbkRate);


            runtimeContext.setOptionValue(Option.OPTION_EXPORT_BI_DATA_ON, exportBIData);
            runtimeContext.setOptionValue(Option.OPTION_EXPORT_BI_DATA_DIR, exportBIDataDirectory);
            runtimeContext.setOptionValue(Option.OPTION_PROJECT_STATE_REPORT_ON, exportProjectStateData);
            runtimeContext.setOptionValue(Option.OPTION_MSR_STOPLIST_ON, importMSRData);
            runtimeContext.setOptionValue(Option.OPTION_MSR_STOPLIST_USER, importMSRLogin);
            runtimeContext.setOptionValue(Option.OPTION_MSR_STOPLIST_PSWD, importMSRPassword);
            runtimeContext.setOptionValue(Option.OPTION_MSR_STOPLIST_URL, importMSRURL);
            runtimeContext.setOptionValue(Option.OPTION_MSR_STOPLIST_LOGGING, importMSRLogging);
            runtimeContext.setOptionValue(Option.OPTION_BENEFITS_RECALC_ON, recalculateBenefits);
            runtimeContext.setOptionValue(Option.OPTION_MSK_NSI_AUTOSYNC_ON, syncRegisterClients);
            runtimeContext.setOptionValue(Option.OPTION_MSK_NSI_URL, syncRegisterURL);
            runtimeContext.setOptionValue(Option.OPTION_MSK_NSI_USE_TESTING_SERVICE, syncRegisterIsTestingService);
            runtimeContext.setOptionValue(Option.OPTION_MSK_NSI_USER, syncRegisterUser);
            runtimeContext.setOptionValue(Option.OPTION_MSK_NSI_PASSWORD, syncRegisterPassword);
            runtimeContext.setOptionValue(Option.OPTION_MSK_NSI_COMPANY, syncRegisterCompany);
            runtimeContext.setOptionValue(Option.OPTION_MSK_NSI_LOG, syncRegisterLogging);
            runtimeContext.setOptionValue(Option.OPTION_MSK_NSI_MAX_ATTEMPTS, syncRegisterMaxAttempts);
            runtimeContext.setOptionValue(Option.OPTION_DISABLE_SMSNOTIFY_EDIT_IN_CLIENT_ROOM, disableSMSNotifyEditInClientRoom);
            runtimeContext.setOptionValue(Option.OPTION_IMPORT_RNIP_PAYMENTS_ON, importRNIPPayments);
            runtimeContext.setOptionValue(Option.OPTION_SEND_PAYMENT_NOTIFY_SMS_ON, sendSMSPaymentNotification);
            runtimeContext.setOptionValue(Option.OPTION_IMPORT_RNIP_PAYMENTS_URL, RNIPPaymentsURL);
            runtimeContext.setOptionValue(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_ALIAS, RNIPPaymentsAlias);
            runtimeContext.setOptionValue(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_PASSWORD, RNIPPaymentsPassword);
            runtimeContext.setOptionValue(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_STORE_NAME, RNIPPaymentsStore);

            runtimeContext.setOptionValue(Option.OPTION_REQUEST_SYNC_LIMITS, syncLimits);
            runtimeContext.setOptionValue(Option.OPTION_REQUEST_SYNC_RETRY_AFTER, retryAfter);
            runtimeContext.setOptionValue(Option.OPTION_MSK_NSI_SUPPORT_EMAIL, syncRegisterSupportEmail);


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
