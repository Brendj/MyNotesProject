/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Bank;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.RNIPVersion;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.CalendarUtils;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;
import ru.axetta.ecafe.processor.web.ui.option.banks.BankListPage;
import ru.axetta.ecafe.processor.web.ui.option.banks.BankOptionItem;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import javax.faces.model.SelectItem;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private DateFormat biDataDateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private final Logger logger = LoggerFactory.getLogger(BasicWorkspacePage.class);
    private static final Pattern SYNC_EXPRESSION_PATTERN =
            Pattern.compile("^(!?([0-1][0-9]|2[0-3]):[0-5][0-9]-([0-1][0-9]|2[0-3]):[0-5][0-9];)*(!?([0-1][0-9]|2[0-3]):[0-5][0-9]-([0-1][0-9]|2[0-3]):[0-5][0-9]);?$");
    private Boolean notifyBySMSAboutEnterEvent;
    private Boolean withOperator;
    private Boolean cleanMenu;
    private Integer menuDaysForDeletion;
    private Integer srcOrgMenuDaysForDeletion;
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
    private Boolean reportOn;
    private Boolean syncRegisterClients;
    private String syncRegisterURL;
    private String syncRegisterWSDL;
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
    private Integer simultaneousSyncThreads;
    private Integer simultaneousSyncTimeout;
    private Integer simultaneousAccIncSyncTimeout;
    private Integer retryAfter;
    private Integer syncLimitFilter;
    private String syncRestrictFullSyncPeriods;
    private String syncRegisterSupportEmail;
    private Integer thinClientMinClaimsEditableDays;
    private String readerForWebInterfaceString;
    private int smsPaymentType;
    private Long smsDefaultSubscriptionFee;
    private Boolean enableBalanceAutoRefill;
    private String autoRefillValues;
    private String thresholdValues;
    private Integer syncRegisterDaysTimeout;
    private String monitoringAllowedTags;
    private Boolean cleanupRepositoryReports;
    private Boolean enableSubscriptionFeeding;
    private Boolean enableSubBalanceOperation;
    private Integer tempCardValidDays;
    private String lastBIDataUpdate;
    private Boolean enableNotificationGoodRequestChange;
    private Boolean hideMissedColumnsNotificationGoodRequestChange;
    private Integer maxNumDaysNotificationGoodRequestChange;
    private String frontControllerRequestIpMask;
    private Boolean synchCleanup;
    private String arrayOfFilterText;
    private String synchLoggingFolder;
    private Boolean smsResending;
    private Boolean smsFailureTestingMode;
    private String rnipProcessorInstance;
    private String RNIPPaymentsURL_v116;
    private String RNIPPaymentsURL_v20;
    private String RNIPPaymentsURL_v22;
    private String RNIPPaymentsURL_v24;
    private String RNIPPaymentsWorkingVersion;
    private Boolean NotifyByPushNewClients;
    private Boolean NotifyByEmailNewClients;
    private Boolean enableNotificationsOnBalancesAndEE;
    private Boolean enableNotificationsSpecial;
    private Integer DaysRestrictionPaymentDateImport;
    private String RNIPSenderCode;
    private String RNIPSenderName;
    private String RNIPTSAServer;
    private Boolean useXadesT;
    private Boolean disableEmailEdit;
    private Date validRegistryDate;
    private Integer reviseSourceType;
    private Integer reviseDelta;
    private Integer reviseLimit;
    private String reviseLastDate;
    private Boolean logInfoService;
    private String methodsInfoService;
    private String regularPaymentCertPath;
    private String regularPaymentCertPassword;
    private String fullSyncExpressions;
    private String orgSettingSyncExpressions;
    private String clientDataSyncExpressions;
    private String menuSyncExpressions;
    private String photoSyncExpressions;
    private String libSyncExpressions;
    private Integer periodOfExtensionCards;
    private String cardAutoBlockCron;
    private String cardAutoBlockNode;
    private Integer cardAutoBlockDays;

    private String[] rnipVersions = new String[] {RNIPVersion.RNIP_V115.toString(), RNIPVersion.RNIP_V116.toString(),
                                                  RNIPVersion.RNIP_V21.toString(), RNIPVersion.RNIP_V22.toString(),
                                                  RNIPVersion.RNIP_V24.toString()};

    private List<BankOptionItem> banks;

    public String getFullSyncExpressions() {
        return fullSyncExpressions;
    }

    public void setFullSyncExpressions(String fullSyncExpressions) {
        this.fullSyncExpressions = fullSyncExpressions;
    }

    public String getOrgSettingSyncExpressions() {
        return orgSettingSyncExpressions;
    }

    public void setOrgSettingSyncExpressions(String orgSettingSyncExpressions) {
        this.orgSettingSyncExpressions = orgSettingSyncExpressions;
    }

    public String getClientDataSyncExpressions() {
        return clientDataSyncExpressions;
    }

    public void setClientDataSyncExpressions(String clientDataSyncExpressions) {
        this.clientDataSyncExpressions = clientDataSyncExpressions;
    }

    public String getMenuSyncExpressions() {
        return menuSyncExpressions;
    }

    public void setMenuSyncExpressions(String menuSyncExpressions) {
        this.menuSyncExpressions = menuSyncExpressions;
    }

    public String getPhotoSyncExpressions() {
        return photoSyncExpressions;
    }

    public void setPhotoSyncExpressions(String photoSyncExpressions) {
        this.photoSyncExpressions = photoSyncExpressions;
    }

    public String getLibSyncExpressions() {
        return libSyncExpressions;
    }

    public void setLibSyncExpressions(String libSyncExpressions) {
        this.libSyncExpressions = libSyncExpressions;
    }

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

    public Integer getSrcOrgMenuDaysForDeletion() {
        return srcOrgMenuDaysForDeletion;
    }

    public void setSrcOrgMenuDaysForDeletion(Integer srcOrgMenuDaysForDeletion) {
        this.srcOrgMenuDaysForDeletion = srcOrgMenuDaysForDeletion;
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

    public String getSyncRegisterWSDL() {
        return syncRegisterWSDL;
    }

    public void setSyncRegisterWSDL(String syncRegisterWSDL) {
        this.syncRegisterWSDL = syncRegisterWSDL;
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

    public Boolean getUseXadesT() {
        return useXadesT;
    }

    public void setUseXadesT(Boolean useXadesT) {
        this.useXadesT = useXadesT;
    }

    public Boolean getDisableEmailEdit() { return disableEmailEdit; }

    public void setDisableEmailEdit(Boolean disableEmailEdit) {
        this.disableEmailEdit = disableEmailEdit;
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

    public Boolean getReportOn() {
        return reportOn;
    }

    public void setReportOn(Boolean reportOn) {
        this.reportOn = reportOn;
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

    public Integer getSyncLimitFilter() {
        return syncLimitFilter;
    }

    public void setSyncLimitFilter(Integer syncLimitFilter) {
        this.syncLimitFilter = syncLimitFilter;
    }

    public String getSyncRestrictFullSyncPeriods() {
        return syncRestrictFullSyncPeriods;
    }

    public void setSyncRestrictFullSyncPeriods(String syncRestrictFullSyncPeriods) throws InvalidPropertiesFormatException {
        if (StringUtils.isEmpty(syncRestrictFullSyncPeriods) || isValidRestrictFormat(syncRestrictFullSyncPeriods)) {
            this.syncRestrictFullSyncPeriods = syncRestrictFullSyncPeriods;
        }
        else {
            throw new InvalidPropertiesFormatException("Неверный формат строки запрета полной синхронизации");
        }
    }

    private boolean isValidRestrictFormat(String option) {
        try {
            String[] arr = option.split(";");
            for (String period : arr) {
                String[] time = period.split("-");
                Date dt1 = CalendarUtils.parseTime(time[0]);
                Date dt2 = CalendarUtils.parseTime(time[1]);
                if (dt1.after(dt2)) {
                    return false;
                }
            }
            return true;
        }
        catch(Exception e) {
            return false;
        }

    }

    public String getSyncRegisterSupportEmail() {
        return syncRegisterSupportEmail;
    }

    public void setSyncRegisterSupportEmail(String syncRegisterSupportEmail) {
        this.syncRegisterSupportEmail = syncRegisterSupportEmail;
    }

    public Integer getThinClientMinClaimsEditableDays() {
        return thinClientMinClaimsEditableDays;
    }

    public void setThinClientMinClaimsEditableDays(Integer thinClientMinClaimsEditableDays) {
        this.thinClientMinClaimsEditableDays = thinClientMinClaimsEditableDays;
    }

    public String getReaderForWebInterfaceString() {
        return readerForWebInterfaceString;
    }

    public void setReaderForWebInterfaceString(String readerForWebInterfaceString) {
        this.readerForWebInterfaceString = readerForWebInterfaceString;
    }

    public int getSmsPaymentType() {
        return smsPaymentType;
    }

    public void setSmsPaymentType(int smsPaymentType) {
        this.smsPaymentType = smsPaymentType;
    }

    public Long getSmsDefaultSubscriptionFee() {
        return smsDefaultSubscriptionFee;
    }

    public void setSmsDefaultSubscriptionFee(Long smsDefaultSubscriptionFee) {
        this.smsDefaultSubscriptionFee = smsDefaultSubscriptionFee;
    }

    public Boolean getEnableBalanceAutoRefill() {
        return enableBalanceAutoRefill;
    }

    public void setEnableBalanceAutoRefill(Boolean enableBalanceAutoRefill) {
        this.enableBalanceAutoRefill = enableBalanceAutoRefill;
    }

    public String getAutoRefillValues() {
        return autoRefillValues;
    }

    public void setAutoRefillValues(String autoRefillValues) {
        this.autoRefillValues = autoRefillValues;
    }

    public String getThresholdValues() {
        return thresholdValues;
    }

    public void setThresholdValues(String thresholdValues) {
        this.thresholdValues = thresholdValues;
    }


    public Integer getTempCardValidDays() {
        return tempCardValidDays;
    }

    public void setTempCardValidDays(Integer tempCardValidDays) {
        this.tempCardValidDays = tempCardValidDays;
    }

    public Integer getSyncRegisterDaysTimeout() {
        return syncRegisterDaysTimeout;
    }

    public void setSyncRegisterDaysTimeout(Integer syncRegisterDaysTimeout) {
        this.syncRegisterDaysTimeout = syncRegisterDaysTimeout;
    }

    public String getMonitoringAllowedTags() {
        return monitoringAllowedTags;
    }

    public void setMonitoringAllowedTags(String monitoringAllowedTags) {
        this.monitoringAllowedTags = monitoringAllowedTags;
    }

    public Boolean getCleanupRepositoryReports() {
        return cleanupRepositoryReports;
    }

    public void setCleanupRepositoryReports(Boolean cleanupRepositoryReports) {
        this.cleanupRepositoryReports = cleanupRepositoryReports;
    }

    public Boolean getEnableSubscriptionFeeding() {
        return enableSubscriptionFeeding;
    }

    public void setEnableSubscriptionFeeding(Boolean enableSubscriptionFeeding) {
        enableSubBalanceOperation = true;
        this.enableSubscriptionFeeding = enableSubscriptionFeeding;
    }

    public Boolean getEnableSubBalanceOperation() {
        return enableSubBalanceOperation;
    }

    public void setEnableSubBalanceOperation(Boolean enableSubBalanceOperation) {
        this.enableSubBalanceOperation = enableSubBalanceOperation;
    }

    public String getLastBIDataUpdate() {
        return lastBIDataUpdate;
    }

    public void setLastBIDataUpdate(String lastBIDataUpdate) {
        this.lastBIDataUpdate = lastBIDataUpdate;
    }

    public Boolean getEnableNotificationGoodRequestChange() {
        return enableNotificationGoodRequestChange;
    }

    public void setEnableNotificationGoodRequestChange(Boolean enableNotificationGoodRequestChange) {
        if(!enableNotificationGoodRequestChange) {
            hideMissedColumnsNotificationGoodRequestChange=false;
        }
        this.enableNotificationGoodRequestChange = enableNotificationGoodRequestChange;
    }

    public Boolean getHideMissedColumnsNotificationGoodRequestChange() {
        return hideMissedColumnsNotificationGoodRequestChange;
    }

    public void setHideMissedColumnsNotificationGoodRequestChange(
            Boolean hideMissedColumnsNotificationGoodRequestChange) {
        this.hideMissedColumnsNotificationGoodRequestChange = hideMissedColumnsNotificationGoodRequestChange;
    }

    public Integer getMaxNumDaysNotificationGoodRequestChange() {
        return maxNumDaysNotificationGoodRequestChange;
    }

    public void setMaxNumDaysNotificationGoodRequestChange(Integer maxNumDaysNotificationGoodRequestChange) {
        this.maxNumDaysNotificationGoodRequestChange = maxNumDaysNotificationGoodRequestChange;
    }

    public String getFrontControllerRequestIpMask() {
        return frontControllerRequestIpMask;
    }

    public void setFrontControllerRequestIpMask(String frontControllerRequestIpMask) {
        this.frontControllerRequestIpMask = frontControllerRequestIpMask;
    }

    public Boolean getSynchCleanup() {
        return synchCleanup;
    }

    public void setSynchCleanup(Boolean synchCleanup) {
        this.synchCleanup = synchCleanup;
    }

    public String getArrayOfFilterText() {
        return arrayOfFilterText;
    }

    public void setArrayOfFilterText(String arrayOfFilterText) {
        this.arrayOfFilterText = arrayOfFilterText;
    }

    public String getSynchLoggingFolder() {
        return synchLoggingFolder;
    }

    public void setSynchLoggingFolder(String synchLoggingFolder) {
        this.synchLoggingFolder = synchLoggingFolder;
    }

    public Boolean getSmsResending() {
        return smsResending;
    }

    public void setSmsResending(Boolean smsResending) {
        this.smsResending = smsResending;
    }

    public Boolean getSmsFailureTestingMode() {
        return smsFailureTestingMode;
    }

    public void setSmsFailureTestingMode(Boolean smsFailureTestingMode) {
        this.smsFailureTestingMode = smsFailureTestingMode;
    }

    public String getRnipProcessorInstance() {
        return rnipProcessorInstance;
    }

    public void setRnipProcessorInstance(String rnipProcessorInstance) {
        this.rnipProcessorInstance = rnipProcessorInstance;
    }

    public String getRNIPPaymentsURL_v116() {
        return RNIPPaymentsURL_v116;
    }

    public void setRNIPPaymentsURL_v116(String RNIPPaymentsURL_v116) {
        this.RNIPPaymentsURL_v116 = RNIPPaymentsURL_v116;
    }

    public String getRNIPPaymentsWorkingVersion() {
        return RNIPPaymentsWorkingVersion;
    }

    public void setRNIPPaymentsWorkingVersion(String RNIPPaymentsWorkingVersion) {
        this.RNIPPaymentsWorkingVersion = RNIPPaymentsWorkingVersion;
    }

    public SelectItem[] getRNIPWorkingVersions() {
        SelectItem[] items = new SelectItem[rnipVersions.length];
        for (int i = 0; i < items.length; ++i) {
            items[i] = new SelectItem(rnipVersions[i], rnipVersions[i]);
        }
        return items;
    }


    public Boolean getNotifyByPushNewClients() {
        return NotifyByPushNewClients;
    }

    public void setNotifyByPushNewClients(Boolean notifyByPushNewClients) {
        NotifyByPushNewClients = notifyByPushNewClients;
    }

    public Boolean getNotifyByEmailNewClients() {
        return NotifyByEmailNewClients;
    }

    public void setNotifyByEmailNewClients(Boolean notifyByEmailNewClients) {
        NotifyByEmailNewClients = notifyByEmailNewClients;
    }

    public Boolean getEnableNotificationsOnBalancesAndEE() {
        return enableNotificationsOnBalancesAndEE;
    }

    public void setEnableNotificationsOnBalancesAndEE(Boolean enableNotificationsOnBalancesAndEE) {
        this.enableNotificationsOnBalancesAndEE = enableNotificationsOnBalancesAndEE;
    }

    public Integer getDaysRestrictionPaymentDateImport() {
        return DaysRestrictionPaymentDateImport;
    }

    public void setDaysRestrictionPaymentDateImport(Integer daysRestrictionPaymentDateImport) {
        DaysRestrictionPaymentDateImport = daysRestrictionPaymentDateImport;
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
        srcOrgMenuDaysForDeletion = runtimeContext.getOptionValueInt(Option.OPTION_SRC_ORG_MENU_DAYS_FOR_DELETION);
        smsPaymentType = runtimeContext.getOptionValueInt(Option.OPTION_SMS_PAYMENT_TYPE);
        smsDefaultSubscriptionFee = runtimeContext.getOptionValueLong(Option.OPTION_SMS_DEFAULT_SUBSCRIPTION_FEE);
        enableBalanceAutoRefill = runtimeContext.getOptionValueBool(Option.OPTION_ENABLE_BALANCE_AUTOREFILL);
        autoRefillValues = runtimeContext.getOptionValueString(Option.OPTION_AUTOREFILL_VALUES);
        thresholdValues = runtimeContext.getOptionValueString(Option.OPTION_THRESHOLD_VALUES);

        tempCardValidDays = runtimeContext.getOptionValueInt(Option.OPTION_TEMP_CARD_VALID_DAYS);

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
        syncRegisterWSDL = runtimeContext.getOptionValueString(Option.OPTION_MSK_NSI_WSDL_URL);
        syncRegisterUser = runtimeContext.getOptionValueString(Option.OPTION_MSK_NSI_USER);
        syncRegisterPassword = runtimeContext.getOptionValueString(Option.OPTION_MSK_NSI_PASSWORD);
        syncRegisterCompany = runtimeContext.getOptionValueString(Option.OPTION_MSK_NSI_COMPANY);
        disableSMSNotifyEditInClientRoom = runtimeContext.getOptionValueBool(
                Option.OPTION_DISABLE_SMSNOTIFY_EDIT_IN_CLIENT_ROOM);
        importRNIPPayments = runtimeContext.getOptionValueBool(Option.OPTION_IMPORT_RNIP_PAYMENTS_ON);
        useXadesT = runtimeContext.getOptionValueBool(Option.OPTION_IMPORT_RNIP_USE_XADEST_ON);
        sendSMSPaymentNotification = runtimeContext.getOptionValueBool(Option.OPTION_SEND_PAYMENT_NOTIFY_SMS_ON);
        RNIPPaymentsURL = runtimeContext.getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_URL);
        RNIPPaymentsAlias = runtimeContext.getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_ALIAS);
        RNIPPaymentsPassword = runtimeContext.getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_PASSWORD);
        RNIPPaymentsStore = runtimeContext.getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_STORE_NAME);
        syncRegisterIsTestingService = runtimeContext.getOptionValueBool(Option.OPTION_MSK_NSI_USE_TESTING_SERVICE);
        syncRegisterLogging = runtimeContext.getOptionValueBool(Option.OPTION_MSK_NSI_LOG);
        syncRegisterMaxAttempts = runtimeContext.getOptionValueInt(Option.OPTION_MSK_NSI_MAX_ATTEMPTS);
        syncRegisterSupportEmail = runtimeContext.getOptionValueString(Option.OPTION_MSK_NSI_SUPPORT_EMAIL);
        thinClientMinClaimsEditableDays = runtimeContext.getOptionValueInt(Option.OPTION_THIN_CLIENT_MIN__CLAIMS_EDITABLE_DAYS);

        syncRegisterDaysTimeout = runtimeContext.getOptionValueInt(Option.OPTION_MSK_NSI_REGISTRY_CHANGE_DAYS_TIMEOUT);
        monitoringAllowedTags = runtimeContext.getOptionValueString(Option.OPTION_MSK_MONITORING_ALLOWED_TAGS);
        cleanupRepositoryReports = runtimeContext.getOptionValueBool(Option.OPTION_MSK_CLEANUP_REPOSITORY_REPORTS);
        enableSubscriptionFeeding = runtimeContext.getOptionValueBool(Option.OPTION_ENABLE_SUBSCRIPTION_FEEDING);
        enableSubBalanceOperation = runtimeContext.getOptionValueBool(Option.OPTION_ENABLE_SUB_BALANCE_OPERATION);
        syncLimits = runtimeContext.getOptionValueInt(Option.OPTION_REQUEST_SYNC_LIMITS);
        simultaneousSyncThreads = runtimeContext.getOptionValueInt(Option.OPTION_SIMULTANEOUS_SYNC_THREADS);
        simultaneousSyncTimeout = runtimeContext.getOptionValueInt(Option.OPTION_SIMULTANEOUS_SYNC_TIMEOUT);
        simultaneousAccIncSyncTimeout = runtimeContext.getOptionValueInt(Option.OPTION_SIMULTANEOUS_ACCINC_SYNC_TIMEOUT);
        retryAfter = runtimeContext.getOptionValueInt(Option.OPTION_REQUEST_SYNC_RETRY_AFTER);
        syncLimitFilter = runtimeContext.getOptionValueInt(Option.OPTION_REQUEST_SYNC_LIMITFILTER);
        arrayOfFilterText = runtimeContext.getOptionValueString(Option.OPTION_ARRAY_OF_FILTER_TEXT);
        syncRestrictFullSyncPeriods = runtimeContext.getOptionValueString(Option.OPTION_RESTRICT_FULL_SYNC_PERIODS);
        reportOn = runtimeContext.getOptionValueBool(Option.OPTION_SAVE_SYNC_CALC);
        disableEmailEdit = runtimeContext.getOptionValueBool(Option.OPTION_DISABLE_EMAIL_EDIT);

        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(runtimeContext.getOptionValueLong(Option.OPTION_EXPORT_BI_DATA_LAST_UPDATE));
        lastBIDataUpdate = biDataDateFormat.format(cal.getTime());

        enableNotificationGoodRequestChange = runtimeContext.getOptionValueBool(Option.OPTION_ENABLE_NOTIFICATION_GOOD_REQUEST_CHANGE);
        hideMissedColumnsNotificationGoodRequestChange = runtimeContext.getOptionValueBool(Option.OPTION_HIDE_MISSED_COL_NOTIFICATION_GOOD_REQUEST_CHANGE);
        maxNumDaysNotificationGoodRequestChange = runtimeContext.getOptionValueInt(
                Option.OPTION_MAX_NUM_DAYS_NOTIFICATION_GOOD_REQUEST_CHANGE);
        frontControllerRequestIpMask = runtimeContext.getOptionValueString(Option.OPTION_FRON_CONTROLLER_REQ_IP_MASK);
        synchCleanup = runtimeContext.getOptionValueBool(Option.OPTION_SYNCH_CLEANUP_ON);
        synchLoggingFolder = runtimeContext.getOptionValueString(Option.OPTION_MSK_NSI_LOGGING_FOLDER);
        smsResending = runtimeContext.getOptionValueBool(Option.OPTION_SMS_RESENDING_ON);
        smsFailureTestingMode = runtimeContext.getOptionValueBool(Option.OPTION_SMS_FAILURE_TESTING_MODE);
        rnipProcessorInstance = runtimeContext.getOptionValueString(Option.OPTION_IMPORT_RNIP_PROCESSOR_INSTANCE);
        RNIPPaymentsURL_v116 = runtimeContext.getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_URL_V116);
        RNIPPaymentsURL_v20 = runtimeContext.getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_URL_V20);
        RNIPPaymentsURL_v22 = runtimeContext.getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_URL_V22);
        RNIPPaymentsURL_v24 = runtimeContext.getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_URL_V24);
        RNIPPaymentsWorkingVersion = runtimeContext.getOptionValueString(Option.OPTION_IMPORT_RNIP_PAYMENTS_WORKING_VERSION);
        setNotifyByPushNewClients(runtimeContext.getOptionValueBool(Option.OPTION_NOTIFY_BY_PUSH_NEW_CLIENTS));
        setNotifyByEmailNewClients(runtimeContext.getOptionValueBool(Option.OPTION_NOTIFY_BY_EMAIL_NEW_CLIENTS));
        setEnableNotificationsOnBalancesAndEE(runtimeContext.getOptionValueBool(Option.OPTION_ENABLE_NOTIFICATIONS_ON_BALANCES_AND_EE));
        setEnableNotificationsSpecial(runtimeContext.getOptionValueBool(Option.OPTION_ENABLE_NOTIFICATIONS_SPECIAL));
        DaysRestrictionPaymentDateImport = runtimeContext.getOptionValueInt((Option.OPTION_DAYS_RESTRICTION_PAYMENT_DATE_IMPORT));
        RNIPSenderCode = runtimeContext.getOptionValueString(Option.OPTION_IMPORT_RNIP_SENDER_CODE);
        RNIPSenderName = runtimeContext.getOptionValueString(Option.OPTION_IMPORT_RNIP_SENDER_NAME);
        RNIPTSAServer = runtimeContext.getOptionValueString(Option.OPTION_IMPORT_RNIP_TSA_SERVER);
        logInfoService = runtimeContext.getOptionValueBool(Option.OPTION_LOG_INFOSERVICE);
        methodsInfoService = runtimeContext.getOptionValueString(Option.OPTION_METHODS_INFOSERVICE);
        regularPaymentCertPath = runtimeContext.getOptionValueString(Option.OPTION_REGULAR_PAYMENT_CERT_PATH);
        regularPaymentCertPassword = runtimeContext.getOptionValueString(Option.OPTION_REGULAR_PAYMENT_CERT_PASSWORD);

        readerForWebInterfaceString = runtimeContext.getOptionValueString(Option.OPTION_READER_FOR_WEB_STRING);

       fullSyncExpressions = runtimeContext.getOptionValueString(Option.OPTION_FULL_SYNC_EXPRESSION);
       orgSettingSyncExpressions = runtimeContext.getOptionValueString(Option.OPTION_ORG_SETTING_SYNC_EXPRESSION);
       clientDataSyncExpressions = runtimeContext.getOptionValueString(Option.OPTION_CLIENT_DATA_SYNC_EXPRESSION);
       menuSyncExpressions = runtimeContext.getOptionValueString(Option.OPTION_MENU_SYNC_EXPRESSION);
       photoSyncExpressions = runtimeContext.getOptionValueString(Option.OPTION_PHOTO_SYNC_EXPRESSION);
       libSyncExpressions = runtimeContext.getOptionValueString(Option.OPTION_LIB_SYNC_EXPRESSION);

        periodOfExtensionCards = runtimeContext.getOptionValueInt(Option.OPTION_PERIOD_OF_EXTENSION_CARDS);

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

        Long timeStamp = runtimeContext.getOptionValueLong(Option.OPTION_VALID_REGISTRY_DATE);
        validRegistryDate = new Date(timeStamp == null? 0L : timeStamp);

        reviseSourceType = runtimeContext.getOptionValueInt(Option.OPTION_REVISE_DATA_SOURCE);
        reviseDelta = runtimeContext.getOptionValueInt(Option.OPTION_REVISE_DELTA);
        reviseLimit = runtimeContext.getOptionValueInt(Option.OPTION_REVISE_LIMIT);
        reviseLastDate = DAOReadonlyService.getInstance().getReviseLastDate();

        cardAutoBlockCron = runtimeContext.getOptionValueString(Option.OPTION_CARD_AUTOBLOCK);
        cardAutoBlockNode = runtimeContext.getOptionValueString(Option.OPTION_CARD_AUTOBLOCK_NODE);
        cardAutoBlockDays = runtimeContext.getOptionValueInt(Option.OPTION_CARD_AUTOBLOCK_DAYS);
    }

    private void validateSyncExpressions() throws Exception {
        Matcher m;
        if(StringUtils.isNotBlank(fullSyncExpressions)){
            m = SYNC_EXPRESSION_PATTERN.matcher(fullSyncExpressions);
            if(!m.find()){
                throw new IllegalArgumentException("Выражение для полной синхранизации задано не верно");
            } else if(invalidExpressions(fullSyncExpressions)){
                throw new IllegalArgumentException("Выражение для полной синхранизации задано не верно,"
                        + " выражение должно состоять либо только из запрещенных промежутков, либо только из разрешенных");
            }
        } if(StringUtils.isNotBlank(orgSettingSyncExpressions)){
            m = SYNC_EXPRESSION_PATTERN.matcher(orgSettingSyncExpressions);
            if(!m.find()){
                throw new IllegalArgumentException("Выражение для синхранизации настроек ОО задано не верно");
            } else if(invalidExpressions(orgSettingSyncExpressions)){
                throw new IllegalArgumentException("Выражение для синхранизации настроек ОО задано не верно,"
                        + " выражение должно состоять либо только из запрещенных промежутков, либо только из разрешенных");
            }
        } if(StringUtils.isNotBlank(clientDataSyncExpressions)){
            m = SYNC_EXPRESSION_PATTERN.matcher(clientDataSyncExpressions);
            if(!m.find()){
                throw new IllegalArgumentException("Выражение для синхранизации данных по клиентам задано не верно");
            } else if(invalidExpressions(clientDataSyncExpressions)){
                throw new IllegalArgumentException("Выражение для синхранизации данных по клиентам задано не верно,"
                        + " выражение должно состоять либо только из запрещенных промежутков, либо только из разрешенных");
            }
        } if(StringUtils.isNotBlank(menuSyncExpressions)){
            m = SYNC_EXPRESSION_PATTERN.matcher(menuSyncExpressions);
            if(!m.find()){
                throw new IllegalArgumentException("Выражение синхранизации меню задано не верно");
            } else if(invalidExpressions(menuSyncExpressions)){
                throw new IllegalArgumentException("Выражение синхранизации меню задано не верно,"
                        + " выражение должно состоять либо только из запрещенных промежутков, либо только из разрешенных");
            }
        } if(StringUtils.isNotBlank(photoSyncExpressions)){
            m = SYNC_EXPRESSION_PATTERN.matcher(photoSyncExpressions);
            if(!m.find()){
                throw new IllegalArgumentException("Выражение для синхранизации фотографий задано не верно");
            } else if(invalidExpressions(photoSyncExpressions)){
                throw new IllegalArgumentException("Выражение для синхранизации фотографий задано не верно,"
                        + " выражение должно состоять либо только из запрещенных промежутков, либо только из разрешенных");
            }
        } if(StringUtils.isNotBlank(libSyncExpressions)){
            m = SYNC_EXPRESSION_PATTERN.matcher(libSyncExpressions);
            if(!m.find()){
                throw new IllegalArgumentException("Выражение для синхранизации библиотеки задано не верно");
            } else if(invalidExpressions(libSyncExpressions)){
                throw new IllegalArgumentException("Выражение для синхранизации библиотеки задано не верно,"
                        + " выражение должно состоять либо только из запрещенных промежутков, либо только из разрешенных");
            }
        }
    }

    private boolean invalidExpressions(String expressions) {
        boolean haveAllowedExpression = false;
        boolean haveForbiddenExpression = false;
        for(String expression : expressions.split(";")){
            if(expression.contains("!")){
                haveForbiddenExpression = true;
            } else {
                haveAllowedExpression = true;
            }
        }
        return haveAllowedExpression && haveForbiddenExpression;
    }

    public Object save() {
        try {
            validateSyncExpressions();
            runtimeContext.setOptionValue(Option.OPTION_WITH_OPERATOR, withOperator);
            runtimeContext.setOptionValue(Option.OPTION_NOTIFY_BY_SMS_ABOUT_ENTER_EVENT, notifyBySMSAboutEnterEvent);
            runtimeContext.setOptionValue(Option.OPTION_CLEAN_MENU, cleanMenu);
            runtimeContext.setOptionValue(Option.OPTION_MENU_DAYS_FOR_DELETION, menuDaysForDeletion);
            runtimeContext.setOptionValue(Option.OPTION_SRC_ORG_MENU_DAYS_FOR_DELETION, srcOrgMenuDaysForDeletion);
            runtimeContext.setOptionValue(Option.OPTION_SMS_PAYMENT_TYPE, smsPaymentType);
            runtimeContext.setOptionValue(Option.OPTION_SMS_DEFAULT_SUBSCRIPTION_FEE, smsDefaultSubscriptionFee);
            runtimeContext.setOptionValue(Option.OPTION_ENABLE_BALANCE_AUTOREFILL, enableBalanceAutoRefill);
            runtimeContext.setOptionValue(Option.OPTION_AUTOREFILL_VALUES, autoRefillValues);
            runtimeContext.setOptionValue(Option.OPTION_THRESHOLD_VALUES, thresholdValues);

            runtimeContext.setOptionValue(Option.OPTION_TEMP_CARD_VALID_DAYS, tempCardValidDays);
            //tempCardValidDays = runtimeContext.getOptionValueInt(Option.OPTION_TEMP_CARD_VALID_DAYS);
            runtimeContext.setOptionValue(Option.OPTION_JOURNAL_TRANSACTIONS, journalTransactions);
            runtimeContext.setOptionValue(Option.OPTION_SEND_JOURNAL_TRANSACTIONS_TO_NFP, sendJournalTransactionsToNFP);
            runtimeContext.setOptionValue(Option.OPTION_NFP_SERVICE_ADDRESS, nfpServiceAddress);
            runtimeContext.setOptionValue(Option.OPTION_CHRONOPAY_SECTION, chronopaySection);
            runtimeContext.setOptionValue(Option.OPTION_RBK_SECTION, rbkSection);
            runtimeContext.setOptionValue(Option.OPTION_RBK_RATE, rbkRate);
            runtimeContext.setOptionValue(Option.OPTION_CHRONOPAY_RATE, chronopayRate);
            runtimeContext.setOptionValue(Option.OPTION_SMPP_CLIENT_STATUS, smppClientStatus);
            runtimeContext.setOptionValue(Option.OPTION_EXTERNAL_URL, externalURL);
            runtimeContext.setOptionValue(Option.OPTION_ENABLE_SUBSCRIPTION_FEEDING, enableSubscriptionFeeding);
            runtimeContext.setOptionValue(Option.OPTION_ENABLE_SUB_BALANCE_OPERATION, enableSubBalanceOperation);

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
            runtimeContext.setOptionValue(Option.OPTION_MSK_NSI_WSDL_URL, syncRegisterWSDL);
            runtimeContext.setOptionValue(Option.OPTION_MSK_NSI_USE_TESTING_SERVICE, syncRegisterIsTestingService);
            runtimeContext.setOptionValue(Option.OPTION_MSK_NSI_USER, syncRegisterUser);
            runtimeContext.setOptionValue(Option.OPTION_MSK_NSI_PASSWORD, syncRegisterPassword);
            runtimeContext.setOptionValue(Option.OPTION_MSK_NSI_COMPANY, syncRegisterCompany);
            runtimeContext.setOptionValue(Option.OPTION_MSK_NSI_LOG, syncRegisterLogging);
            runtimeContext.setOptionValue(Option.OPTION_MSK_NSI_MAX_ATTEMPTS, syncRegisterMaxAttempts);
            runtimeContext.setOptionValue(Option.OPTION_DISABLE_SMSNOTIFY_EDIT_IN_CLIENT_ROOM, disableSMSNotifyEditInClientRoom);
            runtimeContext.setOptionValue(Option.OPTION_IMPORT_RNIP_PAYMENTS_ON, importRNIPPayments);
            runtimeContext.setOptionValue(Option.OPTION_IMPORT_RNIP_USE_XADEST_ON, useXadesT);
            runtimeContext.setOptionValue(Option.OPTION_SEND_PAYMENT_NOTIFY_SMS_ON, sendSMSPaymentNotification);
            runtimeContext.setOptionValue(Option.OPTION_IMPORT_RNIP_PAYMENTS_URL, RNIPPaymentsURL);
            runtimeContext.setOptionValue(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_ALIAS, RNIPPaymentsAlias);
            runtimeContext.setOptionValue(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_PASSWORD, RNIPPaymentsPassword);
            runtimeContext.setOptionValue(Option.OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_STORE_NAME, RNIPPaymentsStore);

            runtimeContext.setOptionValue(Option.OPTION_THIN_CLIENT_MIN__CLAIMS_EDITABLE_DAYS, thinClientMinClaimsEditableDays);

            runtimeContext.setOptionValue(Option.OPTION_REQUEST_SYNC_LIMITS, syncLimits);
            runtimeContext.setOptionValue(Option.OPTION_SIMULTANEOUS_SYNC_THREADS, simultaneousSyncThreads);
            runtimeContext.setOptionValue(Option.OPTION_SIMULTANEOUS_SYNC_TIMEOUT, simultaneousSyncTimeout);
            runtimeContext.setOptionValue(Option.OPTION_SIMULTANEOUS_ACCINC_SYNC_TIMEOUT, simultaneousAccIncSyncTimeout);
            runtimeContext.setOptionValue(Option.OPTION_REQUEST_SYNC_RETRY_AFTER, retryAfter);
            runtimeContext.setOptionValue(Option.OPTION_REQUEST_SYNC_LIMITFILTER, syncLimitFilter);
            runtimeContext.setOptionValue(Option.OPTION_RESTRICT_FULL_SYNC_PERIODS, syncRestrictFullSyncPeriods);
            runtimeContext.setOptionValue(Option.OPTION_MSK_NSI_SUPPORT_EMAIL, syncRegisterSupportEmail);
            runtimeContext.setOptionValue(Option.OPTION_MSK_NSI_REGISTRY_CHANGE_DAYS_TIMEOUT, syncRegisterDaysTimeout);
            runtimeContext.setOptionValue(Option.OPTION_MSK_MONITORING_ALLOWED_TAGS, monitoringAllowedTags);
            runtimeContext.setOptionValue(Option.OPTION_MSK_CLEANUP_REPOSITORY_REPORTS, cleanupRepositoryReports);

            runtimeContext.setOptionValue(Option.OPTION_ENABLE_NOTIFICATION_GOOD_REQUEST_CHANGE,
                    enableNotificationGoodRequestChange);
            runtimeContext.setOptionValue(Option.OPTION_HIDE_MISSED_COL_NOTIFICATION_GOOD_REQUEST_CHANGE,
                    hideMissedColumnsNotificationGoodRequestChange);
            runtimeContext.setOptionValue(Option.OPTION_MAX_NUM_DAYS_NOTIFICATION_GOOD_REQUEST_CHANGE,
                    maxNumDaysNotificationGoodRequestChange);

            runtimeContext.setOptionValue(Option.OPTION_FRON_CONTROLLER_REQ_IP_MASK, frontControllerRequestIpMask);
            runtimeContext.setOptionValue(Option.OPTION_SYNCH_CLEANUP_ON, synchCleanup);
            runtimeContext.setOptionValue(Option.OPTION_ARRAY_OF_FILTER_TEXT, arrayOfFilterText);
            runtimeContext.setOptionValue(Option.OPTION_MSK_NSI_LOGGING_FOLDER, synchLoggingFolder);
            runtimeContext.setOptionValue(Option.OPTION_SMS_RESENDING_ON, smsResending);
            runtimeContext.setOptionValue(Option.OPTION_SMS_FAILURE_TESTING_MODE, smsFailureTestingMode);
            runtimeContext.setOptionValue(Option.OPTION_IMPORT_RNIP_PROCESSOR_INSTANCE, rnipProcessorInstance);
            runtimeContext.setOptionValue(Option.OPTION_IMPORT_RNIP_PAYMENTS_URL_V116, RNIPPaymentsURL_v116);
            runtimeContext.setOptionValue(Option.OPTION_IMPORT_RNIP_PAYMENTS_URL_V20, RNIPPaymentsURL_v20);
            runtimeContext.setOptionValue(Option.OPTION_IMPORT_RNIP_PAYMENTS_URL_V22, RNIPPaymentsURL_v22);
            runtimeContext.setOptionValue(Option.OPTION_IMPORT_RNIP_PAYMENTS_URL_V24, RNIPPaymentsURL_v24);
            runtimeContext.setOptionValue(Option.OPTION_IMPORT_RNIP_PAYMENTS_WORKING_VERSION, RNIPPaymentsWorkingVersion);
            runtimeContext.setOptionValue(Option.OPTION_NOTIFY_BY_PUSH_NEW_CLIENTS, getNotifyByPushNewClients());
            runtimeContext.setOptionValue(Option.OPTION_NOTIFY_BY_EMAIL_NEW_CLIENTS, getNotifyByEmailNewClients());
            runtimeContext.setOptionValue(Option.OPTION_ENABLE_NOTIFICATIONS_ON_BALANCES_AND_EE, getEnableNotificationsOnBalancesAndEE());
            runtimeContext.setOptionValue(Option.OPTION_ENABLE_NOTIFICATIONS_SPECIAL, getEnableNotificationsSpecial());
            runtimeContext.setOptionValue(Option.OPTION_DAYS_RESTRICTION_PAYMENT_DATE_IMPORT, getDaysRestrictionPaymentDateImport());
            runtimeContext.setOptionValue(Option.OPTION_SAVE_SYNC_CALC, reportOn);
            runtimeContext.setOptionValue(Option.OPTION_IMPORT_RNIP_SENDER_CODE, RNIPSenderCode);
            runtimeContext.setOptionValue(Option.OPTION_IMPORT_RNIP_SENDER_NAME, RNIPSenderName);
            runtimeContext.setOptionValue(Option.OPTION_IMPORT_RNIP_TSA_SERVER, RNIPTSAServer);
            runtimeContext.setOptionValue(Option.OPTION_DISABLE_EMAIL_EDIT, disableEmailEdit);

            runtimeContext.setOptionValue(Option.OPTION_READER_FOR_WEB_STRING, readerForWebInterfaceString);

            runtimeContext.setOptionValue(Option.OPTION_VALID_REGISTRY_DATE, validRegistryDate.getTime());

            runtimeContext.setOptionValue(Option.OPTION_REVISE_DATA_SOURCE, reviseSourceType);
            runtimeContext.setOptionValue(Option.OPTION_REVISE_DELTA, reviseDelta);
            runtimeContext.setOptionValue(Option.OPTION_REVISE_LIMIT, reviseLimit);

            runtimeContext.setOptionValue(Option.OPTION_LOG_INFOSERVICE, logInfoService);
            runtimeContext.setOptionValue(Option.OPTION_METHODS_INFOSERVICE, methodsInfoService);
            runtimeContext.setOptionValue(Option.OPTION_REGULAR_PAYMENT_CERT_PATH, regularPaymentCertPath);
            runtimeContext.setOptionValue(Option.OPTION_REGULAR_PAYMENT_CERT_PASSWORD, regularPaymentCertPassword);

            runtimeContext.setOptionValue(Option.OPTION_FULL_SYNC_EXPRESSION, fullSyncExpressions);
            runtimeContext.setOptionValue(Option.OPTION_ORG_SETTING_SYNC_EXPRESSION, orgSettingSyncExpressions);
            runtimeContext.setOptionValue(Option.OPTION_CLIENT_DATA_SYNC_EXPRESSION, clientDataSyncExpressions);
            runtimeContext.setOptionValue(Option.OPTION_MENU_SYNC_EXPRESSION, menuSyncExpressions);
            runtimeContext.setOptionValue(Option.OPTION_PHOTO_SYNC_EXPRESSION, photoSyncExpressions);
            runtimeContext.setOptionValue(Option.OPTION_LIB_SYNC_EXPRESSION, libSyncExpressions);

            runtimeContext.setOptionValue(Option.OPTION_PERIOD_OF_EXTENSION_CARDS, periodOfExtensionCards);

            runtimeContext.setOptionValue(Option.OPTION_CARD_AUTOBLOCK, cardAutoBlockCron);
            runtimeContext.setOptionValue(Option.OPTION_CARD_AUTOBLOCK_NODE, cardAutoBlockNode);
            runtimeContext.setOptionValue(Option.OPTION_CARD_AUTOBLOCK_DAYS, cardAutoBlockDays);

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

    public String getRNIPSenderCode() {
        return RNIPSenderCode;
    }

    public void setRNIPSenderCode(String RNIPSenderCode) {
        this.RNIPSenderCode = RNIPSenderCode;
    }

    public String getRNIPSenderName() {
        return RNIPSenderName;
    }

    public void setRNIPSenderName(String RNIPSenderName) {
        this.RNIPSenderName = RNIPSenderName;
    }

    public String getRNIPTSAServer() {
        return RNIPTSAServer;
    }

    public void setRNIPTSAServer(String RNIPTSAServer) {
        this.RNIPTSAServer = RNIPTSAServer;
    }

    public Boolean isSverkaEnabled() {
        return DAOReadonlyService.getInstance().isSverkaEnabled();
    }

    public String isSverkaEnabledString() {
        return DAOReadonlyService.getInstance().isSverkaEnabled() ? "Включено" : "Выключено";
    }

    public void turnOnSverka() {
        DAOService.getInstance().setSverkaEnabled(true);
    }

    public void turnOffSverka() {
        DAOService.getInstance().setSverkaEnabled(false);
    }

    public Date getValidRegistryDate() {
        return validRegistryDate;
    }

    public void setValidRegistryDate(Date validRegistryDate) {
        if(validRegistryDate == null){
            this.validRegistryDate = new Date(0L);
        } else {
            this.validRegistryDate = validRegistryDate;
        }
    }

    public String getRNIPPaymentsURL_v20() {
        return RNIPPaymentsURL_v20;
    }

    public void setRNIPPaymentsURL_v20(String RNIPPaymentsURL_v20) {
        this.RNIPPaymentsURL_v20 = RNIPPaymentsURL_v20;
    }

    public Integer getReviseSourceType() {
        return reviseSourceType;
    }

    public void setReviseSourceType(Integer reviseSourceType) {
        this.reviseSourceType = reviseSourceType;
    }

    public Integer getReviseDelta() {
        return reviseDelta;
    }

    public void setReviseDelta(Integer reviseDelta) {
        this.reviseDelta = reviseDelta;
    }

    public Boolean getLogInfoService() {
        return logInfoService;
    }

    public void setLogInfoService(Boolean logInfoService) {
        this.logInfoService = logInfoService;
    }

    public String getMethodsInfoService() {
        return methodsInfoService;
    }

    public void setMethodsInfoService(String methodsInfoService) {
        this.methodsInfoService = methodsInfoService;
    }

    public Integer getReviseLimit() {
        return reviseLimit;
    }

    public void setReviseLimit(Integer reviseLimit) {
        this.reviseLimit = reviseLimit;
    }

    public String getReviseLastDate() {
        return reviseLastDate;
    }

    public void setReviseLastDate(String reviseLastDate) {
        this.reviseLastDate = reviseLastDate;
    }

    public String getRegularPaymentCertPath() {
        return regularPaymentCertPath;
    }

    public void setRegularPaymentCertPath(String regularPaymentCertPath) {
        this.regularPaymentCertPath = regularPaymentCertPath;
    }

    public String getRegularPaymentCertPassword() {
        return regularPaymentCertPassword;
    }

    public void setRegularPaymentCertPassword(String regularPaymentCertPassword) {
        this.regularPaymentCertPassword = regularPaymentCertPassword;
    }

    public Integer getPeriodOfExtensionCards() {
        return periodOfExtensionCards;
    }

    public void setPeriodOfExtensionCards(Integer periodOfExtensionCards) {
        this.periodOfExtensionCards = periodOfExtensionCards;
    }

    public Integer getSimultaneousSyncThreads() {
        return simultaneousSyncThreads;
    }

    public void setSimultaneousSyncThreads(Integer simultaneousSyncThreads) {
        this.simultaneousSyncThreads = simultaneousSyncThreads;
    }

    public Integer getSimultaneousSyncTimeout() {
        return simultaneousSyncTimeout;
    }

    public void setSimultaneousSyncTimeout(Integer simultaneousSyncTimeout) {
        this.simultaneousSyncTimeout = simultaneousSyncTimeout;
    }

    public String getCardAutoBlockCron() {
        return cardAutoBlockCron;
    }

    public void setCardAutoBlockCron(String cardAutoBlockCron) {
        this.cardAutoBlockCron = cardAutoBlockCron;
    }

    public String getCardAutoBlockNode() {
        return cardAutoBlockNode;
    }

    public void setCardAutoBlockNode(String cardAutoBlockNode) {
        this.cardAutoBlockNode = cardAutoBlockNode;
    }

    public Integer getCardAutoBlockDays() {
        return cardAutoBlockDays;
    }

    public void setCardAutoBlockDays(Integer cardAutoBlockDays) {
        this.cardAutoBlockDays = cardAutoBlockDays;
    }

    public Boolean getEnableNotificationsSpecial() {
        return enableNotificationsSpecial;
    }

    public void setEnableNotificationsSpecial(Boolean enableNotificationsSpecial) {
        this.enableNotificationsSpecial = enableNotificationsSpecial;
    }

    public Integer getSimultaneousAccIncSyncTimeout() {
        return simultaneousAccIncSyncTimeout;
    }

    public void setSimultaneousAccIncSyncTimeout(Integer simultaneousAccIncSyncTimeout) {
        this.simultaneousAccIncSyncTimeout = simultaneousAccIncSyncTimeout;
    }

    public String getRNIPPaymentsURL_v22() {
        return RNIPPaymentsURL_v22;
    }

    public void setRNIPPaymentsURL_v22(String RNIPPaymentsURL_v22) {
        this.RNIPPaymentsURL_v22 = RNIPPaymentsURL_v22;
    }

    public String getRNIPPaymentsURL_v24() {
        return RNIPPaymentsURL_v24;
    }

    public void setRNIPPaymentsURL_v24(String RNIPPaymentsURL_v24) {
        this.RNIPPaymentsURL_v24 = RNIPPaymentsURL_v24;
    }
}
