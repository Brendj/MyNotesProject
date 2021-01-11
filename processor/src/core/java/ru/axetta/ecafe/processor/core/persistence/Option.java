/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

import ru.CryptoPro.JCP.JCP;

import ru.axetta.ecafe.processor.core.service.SMSSubscriptionFeeService;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 11.11.11
 * Time: 14:22
 * To change this template use File | Settings | File Templates.
 */
public class Option {

    public static final String NSI2="НСИ-2";
    public static final String NSI3="НСИ-3";

    public final static int OPTION_WITH_OPERATOR=2,
            OPTION_NOTIFY_BY_SMS_ABOUT_ENTER_EVENT=3,
            OPTION_CLEAN_MENU=4,
            OPTION_MENU_DAYS_FOR_DELETION=5,
            OPTION_JOURNAL_TRANSACTIONS=6,
            OPTION_SEND_JOURNAL_TRANSACTIONS_TO_NFP=7,
            OPTION_NFP_SERVICE_ADDRESS=8,
            OPTION_PASSWORD_RESTORE_SEED =9,
            OPTION_NOTIFICATION_TEXT = 10,
            OPTION_DEFAULT_OVERDRAFT_LIMIT=11,
            OPTION_DEFAULT_EXPENDITURE_LIMIT=12,
            OPTION_SMPP_CLIENT_STATUS=13,
            OPTION_DISABLE_SMSNOTIFY_EDIT_IN_CLIENT_ROOM=14,
            OPTION_REQUEST_SYNC_LIMITS=15,
            OPTION_REQUEST_SYNC_RETRY_AFTER=16,
            OPTION_REQUEST_SYNC_LIMITFILTER=17,
            OPTION_RESTRICT_FULL_SYNC_PERIODS=18,
            OPTION_SIMULTANEOUS_SYNC_THREADS=19,
            OPTION_SIMULTANEOUS_SYNC_TIMEOUT=20,

            OPTION_CHRONOPAY_SECTION=1000,
            OPTION_CHRONOPAY_RATE=1001,
            OPTION_RBK_SECTION=1002,
            OPTION_RBK_RATE=1003,
            OPTION_SRC_ORG_MENU_DAYS_FOR_DELETION=1004,
            OPTION_SMS_PAYMENT_TYPE=1005,
            OPTION_SMS_DEFAULT_SUBSCRIPTION_FEE=1006,
            OPTION_ENABLE_BALANCE_AUTOREFILL=1007,
            OPTION_AUTOREFILL_VALUES=1008,
            OPTION_ENABLE_SUBSCRIPTION_FEEDING =1009,
            OPTION_ENABLE_SUB_BALANCE_OPERATION =1010,
            OPTION_THRESHOLD_VALUES=1011,
            OPTION_TEMP_CARD_VALID_DAYS=1012,
            OPTION_ENABLE_NOTIFICATION_GOOD_REQUEST_CHANGE=1013, // оповещение по изменению заявок
            OPTION_HIDE_MISSED_COL_NOTIFICATION_GOOD_REQUEST_CHANGE =1014, // отображать пустые колонки
            OPTION_MAX_NUM_DAYS_NOTIFICATION_GOOD_REQUEST_CHANGE =1015, //максимальное количество дней для выборки(7-31)
            OPTION_ARRAY_OF_FILTER_TEXT =1016, //Список выражений по фильтраций комплексов АП, выражения разделяются через ';
            OPTION_VALID_REGISTRY_DATE = 1017,
            OPTION_REVISE_LAST_DATE = 1018,
            OPTION_REVISE_LIMIT = 1019,
            OPTION_LAST_DELATED_DATE_MENU = 1020,
            OPTION_LAST_COUNT_CARD_BLOCK = 1021,

            OPTION_NSI_LAST_SYNC_TIME=10001,
            OPTION_STOP_LIST_LAST_UPDATE=10010,
            OPTION_MSR_STOPLIST_UPD_TIME=10011,
            OPTION_MSR_STOPLIST_ON=10012,
            OPTION_EXPORT_BI_DATA_ON=10013,
            OPTION_EXPORT_BI_DATA_DIR=10014,
            OPTION_MSR_STOPLIST_URL=10015,
            OPTION_MSR_STOPLIST_USER=10016,
            OPTION_MSR_STOPLIST_PSWD=10017,
            OPTION_MSR_STOPLIST_LOGGING=10018,
            OPTION_PROJECT_STATE_REPORT_ON=10019,
            OPTION_EXTERNAL_URL=10020,
            OPTION_BENEFITS_RECALC_ON=10021,
            OPTION_MSK_NSI_AUTOSYNC_ON =10022,
            OPTION_MSK_NSI_AUTOSYNC_UPD_TIME =10023,
            OPTION_MSK_NSI_URL=100024,
            OPTION_MSK_NSI_USER=100025,
            OPTION_MSK_NSI_PASSWORD=100026,
            OPTION_MSK_NSI_COMPANY=100027,
            OPTION_IMPORT_RNIP_PAYMENTS_ON=100028,
            OPTION_IMPORT_RNIP_PAYMENTS_TIME=100029,
            OPTION_SEND_PAYMENT_NOTIFY_SMS_ON=100030,

            OPTION_IMPORT_RNIP_PAYMENTS_URL=100031,
            OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_STORE_NAME=100032,
            OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_ALIAS=100033,
            OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_PASSWORD=100034,
            OPTION_MSK_NSI_USE_TESTING_SERVICE=100035,
            OPTION_MSK_NSI_LOG=100036,
            OPTION_MSK_NSI_MAX_ATTEMPTS=100037,
            OPTION_MSK_NSI_SUPPORT_EMAIL=100038,
            OPTION_THIN_CLIENT_MIN__CLAIMS_EDITABLE_DAYS=100039,
            OPTION_THIN_CLIENT_PRE_POST_DATE=100040,
            OPTION_MSK_NSI_REGISTRY_CHANGE_DAYS_TIMEOUT=100041,
            OPTION_MSK_MONITORING_ALLOWED_TAGS=100042,
            OPTION_MSK_CLEANUP_REPOSITORY_REPORTS=100043,
            OPTION_EXPORT_BI_DATA_LAST_UPDATE=100044,
            OPTION_RECONCILIATION_SETTING=100045,
            OPTION_FRON_CONTROLLER_REQ_IP_MASK=100046,

            OPTION_SYNCH_CLEANUP_ON=100047,
            OPTION_MSK_NSI_WSDL_URL=100048,
            OPTION_MSK_NSI_LOGGING_FOLDER=100049,
            OPTION_EMP_CHANGE_SEQUENCE=100050,
            OPTION_EMP_CLIENTS_PER_PACKAGE=100051,
            OPTION_EMP_PROCESSOR_INSTANCE=100052,
            OPTION_EMP_BINDED_CLIENTS_COUNT=100053,
            OPTION_EMP_NOT_BINDED_CLIENTS_COUNT=100054,
            OPTION_EMP_BIND_WAITING_CLIENTS_COUNT=100055,
            OPTION_EMP_COUNTER=100056,

            OPTION_SMS_RESENDING_ON=100057,
            OPTION_SMS_FAILURE_TESTING_MODE=100058,
            OPTION_IMPORT_RNIP_PROCESSOR_INSTANCE=100059,
            OPTION_IMPORT_RNIP_PAYMENTS_URL_V116 = 100060,
            OPTION_IMPORT_RNIP_PAYMENTS_WORKING_VERSION = 100061,
            OPTION_NOTIFY_BY_PUSH_NEW_CLIENTS = 100062,
            OPTION_DAYS_RESTRICTION_PAYMENT_DATE_IMPORT = 100063,
            OPTION_SAVE_SYNC_CALC = 100064,
            OPTION_IMPORT_RNIP_SENDER_CODE = 100065,
            OPTION_IMPORT_RNIP_SENDER_NAME = 100066,
            OPTION_SECURITY_PERIOD_BLOCK_LOGIN_REUSE = 100067,
            OPTION_SECURITY_PERIOD_BLOCK_UNUSED_LOGIN_AFTER = 100068,
            OPTION_SECURITY_PERIOD_SMS_CODE_ALIVE = 100069,
            OPTION_IMPORT_RNIP_TSA_SERVER = 100070,
            OPTION_SECURITY_CLIENT_PERIOD_BLOCK_LOGIN_REUSE = 100071,
            OPTION_SECURITY_CLIENT_PERIOD_BLOCK_UNUSED_LOGIN_AFTER = 100072,
            OPTION_SECURITY_CLIENT_PERIOD_PASSWORD_CHANGE = 100073,
            OPTION_SECURITY_CLIENT_MAX_AUTH_FAULT_COUNT = 100074,
            OPTION_SECURITY_CLIENT_TMP_BLOCK_ACC_TIME = 100075,
            OPTION_SECURITY_PERIOD_PASSWORD_CHANGE = 100076,
            OPTION_SECURITY_MAX_AUTH_FAULT_COUNT = 100077,
            OPTION_SECURITY_TMP_BLOCK_ACC_TIME = 100078,
            OPTION_IMPORT_RNIP_USE_XADEST_ON=100079,
            OPTION_DISABLE_EMAIL_EDIT=100080,
            OPTION_SVERKA_ENABLED=100081,
            OPTION_NOTIFY_BY_EMAIL_NEW_CLIENTS = 100082,
            OPTION_ENABLE_NOTIFICATIONS_ON_BALANCES_AND_EE = 100083,
            OPTION_REGIONS_FROM_NSI = 100084,
            OPTION_FOUNDER_FROM_NSI = 100085,
            OPTION_INDUSTRY_FROM_NSI = 100086,
            OPTION_LAST_CHANGE_FROM_NSI = 100087,
            OPTION_READER_FOR_WEB_STRING = 100088,
            OPTION_EXTERNAL_SYSTEM_ENABLED = 100089,
            OPTION_EXTERNAL_SYSTEM_URL  = 100090,
            OPTION_EXTERNAL_SYSTEM_TYPES  = 100091,
            OPTION_EXTERNAL_SYSTEM_OPERATION_TYPES  = 100092,
            OPTION_LAST_ORG_CHANGE_PROCESS = 100093,
            OPTION_REVISE_DATA_SOURCE = 100095,
            OPTION_REVISE_DELTA = 100096,
            OPTION_LOG_INFOSERVICE = 100097,
            OPTION_METHODS_INFOSERVICE = 100098,
            OPTION_IMPORT_RNIP_PAYMENTS_URL_V20 = 100099,
            OPTION_REGULAR_PAYMENT_CERT_PATH = 100100,
            OPTION_REGULAR_PAYMENT_CERT_PASSWORD = 100101,
            OPTION_FULL_SYNC_EXPRESSION = 100200,
            OPTION_ORG_SETTING_SYNC_EXPRESSION = 100201,
            OPTION_CLIENT_DATA_SYNC_EXPRESSION = 100202,
            OPTION_MENU_SYNC_EXPRESSION = 100203,
            OPTION_PHOTO_SYNC_EXPRESSION = 100204,
            OPTION_LIB_SYNC_EXPRESSION = 100205,
            OPTION_PERIOD_OF_EXTENSION_CARDS = 101101,
            OPTION_NSI_VERSION = 100206,
            OPTION_CARD_AUTOBLOCK = 100210,
            OPTION_CARD_AUTOBLOCK_NODE = 100211,
            OPTION_CARD_AUTOBLOCK_DAYS = 100212,
            OPTION_ENABLE_NOTIFICATIONS_SPECIAL = 100213,
            OPTION_IMPORT_RNIP_PAYMENTS_URL_V22 = 100300;

    public final static Object[] OPTIONS_INITIALIZER = new Object[]{
            OPTION_WITH_OPERATOR, "0",
            OPTION_NOTIFY_BY_SMS_ABOUT_ENTER_EVENT, "0",
            OPTION_CLEAN_MENU, "1", 
            OPTION_MENU_DAYS_FOR_DELETION, "30", 
            OPTION_JOURNAL_TRANSACTIONS, "0", 
            OPTION_SEND_JOURNAL_TRANSACTIONS_TO_NFP, "1", 
            OPTION_NFP_SERVICE_ADDRESS, "http://193.47.154.34:7002/uec-service-war/TransactionService", 
            OPTION_NOTIFICATION_TEXT, "",
            OPTION_DEFAULT_OVERDRAFT_LIMIT, "0",
            OPTION_DEFAULT_EXPENDITURE_LIMIT, "20000",
            OPTION_DISABLE_SMSNOTIFY_EDIT_IN_CLIENT_ROOM, "0",
            OPTION_REQUEST_SYNC_LIMITS, "100",
            OPTION_REQUEST_SYNC_RETRY_AFTER, "3600",
            OPTION_REQUEST_SYNC_LIMITFILTER, "200",
            OPTION_RESTRICT_FULL_SYNC_PERIODS, "",
            OPTION_SIMULTANEOUS_SYNC_THREADS, "50",
            OPTION_SIMULTANEOUS_SYNC_TIMEOUT, "20",
            OPTION_SMPP_CLIENT_STATUS, "0",
            OPTION_CHRONOPAY_SECTION, "1", 
            OPTION_RBK_SECTION, "1", 
            OPTION_CHRONOPAY_RATE, "3.3", 
            OPTION_RBK_RATE, "3",
            OPTION_SRC_ORG_MENU_DAYS_FOR_DELETION, "730",
            OPTION_SMS_PAYMENT_TYPE, String.valueOf(SMSSubscriptionFeeService.SMS_PAYMENT_BY_THE_PIECE),
            OPTION_SMS_DEFAULT_SUBSCRIPTION_FEE, "0",
            OPTION_ENABLE_BALANCE_AUTOREFILL, "",
            OPTION_AUTOREFILL_VALUES, "0",
            OPTION_ENABLE_SUBSCRIPTION_FEEDING, "0",
            OPTION_ENABLE_SUB_BALANCE_OPERATION, "0",
            OPTION_THRESHOLD_VALUES, "0",
            OPTION_TEMP_CARD_VALID_DAYS, "0",
            OPTION_ENABLE_NOTIFICATION_GOOD_REQUEST_CHANGE, "0", // по умолчанию отключено
            OPTION_HIDE_MISSED_COL_NOTIFICATION_GOOD_REQUEST_CHANGE, "0", // по умолчанию отображать пустые колонки
            OPTION_MAX_NUM_DAYS_NOTIFICATION_GOOD_REQUEST_CHANGE, "7", // по умолчанию неделя максимально до 31 дня
            OPTION_ARRAY_OF_FILTER_TEXT, "сотрудник;",
            OPTION_VALID_REGISTRY_DATE, "0",
            OPTION_REVISE_LAST_DATE, "",
            OPTION_REVISE_LIMIT, "10000",
            OPTION_NSI_LAST_SYNC_TIME, null,
            OPTION_STOP_LIST_LAST_UPDATE, null,
            OPTION_MSR_STOPLIST_ON, "0",
            OPTION_EXPORT_BI_DATA_ON, "0",
            OPTION_EXPORT_BI_DATA_DIR, null,
            OPTION_MSR_STOPLIST_URL, "http://localhost:2000/gateway/services/SID0003025?wsdl",
            OPTION_MSR_STOPLIST_USER, "user",
            OPTION_MSR_STOPLIST_PSWD, "password",
            OPTION_MSR_STOPLIST_LOGGING, "0",
            OPTION_PROJECT_STATE_REPORT_ON, "0",
            OPTION_EXTERNAL_URL, "http://localhost:8080",
            OPTION_BENEFITS_RECALC_ON, "0", OPTION_MSK_NSI_AUTOSYNC_ON, "0", OPTION_MSK_NSI_AUTOSYNC_UPD_TIME, null,
            OPTION_MSK_NSI_URL, "http://10.126.216.2:4422/em/nsiws/v2/services/NSIService",
            OPTION_MSK_NSI_WSDL_URL, "http://10.126.216.2:4422/em/nsiws/v2/services/NSIService/WEB-INF/wsdl/NSIService.wsdl",
            OPTION_MSK_NSI_USER, "UEK_SOAP",
            OPTION_MSK_NSI_PASSWORD, "la0d6xxw",
            OPTION_MSK_NSI_COMPANY, "dogm_nsi",
            OPTION_IMPORT_RNIP_PAYMENTS_ON, "0",
            OPTION_IMPORT_RNIP_PAYMENTS_TIME, null,
            OPTION_SEND_PAYMENT_NOTIFY_SMS_ON, "0",
            OPTION_IMPORT_RNIP_PAYMENTS_URL, "http://193.47.154.2:7003/UnifoSecProxy_WAR/SmevUnifoService",
            OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_STORE_NAME, JCP.HD_STORE_NAME,
            OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_ALIAS, "test",
            OPTION_IMPORT_RNIP_PAYMENTS_CRYPTO_PASSWORD, "test",
            OPTION_MSK_NSI_USE_TESTING_SERVICE, "0",
            OPTION_MSK_NSI_LOG, "0",
            OPTION_MSK_NSI_MAX_ATTEMPTS, "5",
            OPTION_MSK_NSI_SUPPORT_EMAIL, "",
            OPTION_THIN_CLIENT_MIN__CLAIMS_EDITABLE_DAYS, "2",
            OPTION_THIN_CLIENT_PRE_POST_DATE, "2",
            OPTION_MSK_NSI_REGISTRY_CHANGE_DAYS_TIMEOUT, "180",
            OPTION_MSK_MONITORING_ALLOWED_TAGS, "2013",
            OPTION_MSK_CLEANUP_REPOSITORY_REPORTS, "1",
            OPTION_EXPORT_BI_DATA_LAST_UPDATE,"1386374400000",
            OPTION_RECONCILIATION_SETTING, "",
            OPTION_FRON_CONTROLLER_REQ_IP_MASK, "127\\.0\\.0\\.1",
            OPTION_SYNCH_CLEANUP_ON, "1",
            OPTION_MSK_NSI_LOGGING_FOLDER, "",
            OPTION_EMP_CHANGE_SEQUENCE, "1",
            OPTION_EMP_CLIENTS_PER_PACKAGE, "100",
            OPTION_EMP_PROCESSOR_INSTANCE, "2",
            OPTION_EMP_BINDED_CLIENTS_COUNT, "0",
            OPTION_EMP_NOT_BINDED_CLIENTS_COUNT, "0",
            OPTION_EMP_BIND_WAITING_CLIENTS_COUNT, "0",
            OPTION_EMP_COUNTER, "",
            OPTION_SMS_RESENDING_ON, "0",
            OPTION_SMS_FAILURE_TESTING_MODE, "0",
            OPTION_IMPORT_RNIP_PROCESSOR_INSTANCE, "0",
            OPTION_IMPORT_RNIP_PAYMENTS_URL_V116, "http://212.45.30.126:9084/UnifoSecProxy_WAR/RsmevRnipService",
            OPTION_IMPORT_RNIP_PAYMENTS_WORKING_VERSION, "1.15",
            OPTION_NOTIFY_BY_PUSH_NEW_CLIENTS, "0",
            OPTION_DAYS_RESTRICTION_PAYMENT_DATE_IMPORT, "7",
            OPTION_SAVE_SYNC_CALC, "1",
            OPTION_IMPORT_RNIP_SENDER_CODE, "000000001",
            OPTION_IMPORT_RNIP_SENDER_NAME, "External Organization",
            OPTION_SECURITY_PERIOD_BLOCK_LOGIN_REUSE, "365",
            OPTION_SECURITY_PERIOD_BLOCK_UNUSED_LOGIN_AFTER, "90",
            OPTION_SECURITY_PERIOD_SMS_CODE_ALIVE, "30",
            OPTION_IMPORT_RNIP_TSA_SERVER, "http://www.cryptopro.ru/tsp/tsp.srf",
            OPTION_SECURITY_CLIENT_PERIOD_BLOCK_LOGIN_REUSE, "365",
            OPTION_SECURITY_CLIENT_PERIOD_BLOCK_UNUSED_LOGIN_AFTER, "90",
            OPTION_SECURITY_CLIENT_PERIOD_PASSWORD_CHANGE, "120",
            OPTION_SECURITY_CLIENT_MAX_AUTH_FAULT_COUNT, "10",
            OPTION_SECURITY_CLIENT_TMP_BLOCK_ACC_TIME, "5",
            OPTION_SECURITY_PERIOD_PASSWORD_CHANGE, "120",
            OPTION_SECURITY_MAX_AUTH_FAULT_COUNT, "10",
            OPTION_SECURITY_TMP_BLOCK_ACC_TIME, "5",
            OPTION_IMPORT_RNIP_USE_XADEST_ON, "0",
            OPTION_DISABLE_EMAIL_EDIT, "0",
            OPTION_SVERKA_ENABLED, "1",
            OPTION_NOTIFY_BY_EMAIL_NEW_CLIENTS, "0",
            OPTION_ENABLE_NOTIFICATIONS_ON_BALANCES_AND_EE, "0",
            OPTION_REGIONS_FROM_NSI, "Восточный,Западный,Зеленоградский,Московская область,Новомосковский,Северный,"
                    + "Северо-Восточный,Северо-Западный,Троицкий,Троицкий и Новомосковский,Центральный,Юго-Восточный,Юго-Западный,Южный",
            OPTION_FOUNDER_FROM_NSI, "Департамент образования города Москвы",
            OPTION_INDUSTRY_FROM_NSI, "",
            OPTION_LAST_CHANGE_FROM_NSI, "",
            OPTION_READER_FOR_WEB_STRING, "",
            OPTION_EXTERNAL_SYSTEM_ENABLED, "",
            OPTION_EXTERNAL_SYSTEM_URL, "",
            OPTION_EXTERNAL_SYSTEM_TYPES, "",
            OPTION_EXTERNAL_SYSTEM_OPERATION_TYPES, "",
            OPTION_LAST_ORG_CHANGE_PROCESS, "2777058000000",
            OPTION_REVISE_DATA_SOURCE, "1",
            OPTION_REVISE_DELTA, "24",
            OPTION_LOG_INFOSERVICE, "0",
            OPTION_METHODS_INFOSERVICE, "",
            OPTION_IMPORT_RNIP_PAYMENTS_URL_V20, "http://test.rnip.mos.ru/frontend-service/MainService",
            OPTION_REGULAR_PAYMENT_CERT_PATH, "",
            OPTION_REGULAR_PAYMENT_CERT_PASSWORD, "12345678",
            OPTION_FULL_SYNC_EXPRESSION, "!22:00-05:00;!07:00-16:00",
            OPTION_ORG_SETTING_SYNC_EXPRESSION, "04:00-11:00;11:00-18:00",
            OPTION_CLIENT_DATA_SYNC_EXPRESSION, "04:00-09:00;09:00-14:00",
            OPTION_MENU_SYNC_EXPRESSION, "04:00-13:00;13:00-22:00",
            OPTION_PHOTO_SYNC_EXPRESSION, "04:00-07:00",
            OPTION_LIB_SYNC_EXPRESSION, "16:00-22:00",
            OPTION_PERIOD_OF_EXTENSION_CARDS, "12",
            OPTION_NSI_VERSION, NSI2,
            OPTION_CARD_AUTOBLOCK, "0 0 3 ? * 1",
            OPTION_CARD_AUTOBLOCK_NODE, "1001",
            OPTION_CARD_AUTOBLOCK_DAYS, "180",
            OPTION_ENABLE_NOTIFICATIONS_SPECIAL, "1",
            OPTION_IMPORT_RNIP_PAYMENTS_URL_V22, "https://test.rnip.mos.ru:9090/frontend-service/MainService"
};

    private Long idOfOption;
    private String optionText;

    protected Option() {
        // For Hibernate
    }

    public Option(Long idOfOption, String optionText) {
        this.idOfOption = idOfOption;
        this.optionText = optionText;
    }

    public Long getIdOfOption() {
        return idOfOption;
    }

    public void setIdOfOption(Long idOfOption) {
        this.idOfOption = idOfOption;
    }

    public String getOptionText() {
        return optionText;
    }

    public void setOptionText(String optionText) {
        this.optionText = optionText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Option option = (Option) o;

        if (!idOfOption.equals(option.idOfOption)) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return idOfOption.hashCode();
    }

    @Override
    public String toString() {
        return "Option{" + "idOfOption=" + idOfOption + ", optionText='" + optionText + '\'' + '}';
    }

}
