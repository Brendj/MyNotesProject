/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.persistence;

/**
 * Created by IntelliJ IDEA.
 * User: rumil
 * Date: 11.11.11
 * Time: 14:22
 * To change this template use File | Settings | File Templates.
 */
public class Option {

    /* TODO: Предпологается применить множество? */
    public enum Options{
        OPERATOR(2),
        NOTIFY_BY_SMS_ABOUT_ENTER_EVENT(3),
        CLEAN_MENU(4),
        MENU_DAYS_FOR_DELETION(5),
        JOURNAL_TRANSACTIONS(6),
        SEND_JOURNAL_TRANSACTIONS_TO_NFP(7),
        NFP_SERVICE_ADDRESS(8),
        PASSWORD_RESTORE_SEED(9),
        NOTIFICATION_TEXT(10),
        CHRONOPAY_SECTION(1000),
        CHRONOPAY_RATE(1001),
        RBK_SECTION(1002),
        RBK_RATE(1003);
        private int value;

        private Options(int value) {
            this.value = value;
        }

        public int getValue() {
            return value;
        }

    }

    public final static int OPTION_WITH_OPERATOR=2,
            OPTION_NOTIFY_BY_SMS_ABOUT_ENTER_EVENT=3, OPTION_CLEAN_MENU=4,
            OPTION_MENU_DAYS_FOR_DELETION=5, OPTION_JOURNAL_TRANSACTIONS=6, OPTION_SEND_JOURNAL_TRANSACTIONS_TO_NFP=7,
            OPTION_NFP_SERVICE_ADDRESS=8,
            OPTION_PASSWORD_RESTORE_SEED =9,
            OPTION_NOTIFICATION_TEXT = 10,
            OPTION_DEFAULT_OVERDRAFT_LIMIT=11,
            OPTION_DEFAULT_EXPENDITURE_LIMIT=12,
            OPTION_SMPP_CLIENT_STATUS=13,
            OPTION_DISABLE_SMSNOTIFY_EDIT_IN_CLIENT_ROOM=14,
            OPTION_CHRONOPAY_SECTION=1000,
            OPTION_CHRONOPAY_RATE=1001,
            OPTION_RBK_SECTION=1002,
            OPTION_RBK_RATE=1003,
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
            OPTION_SEND_PAYMENT_NOTIFY_SMS_ON=100030
    ;
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
            OPTION_SMPP_CLIENT_STATUS, "0",
            OPTION_CHRONOPAY_SECTION, "1", 
            OPTION_RBK_SECTION, "1", 
            OPTION_CHRONOPAY_RATE, "3.3", 
            OPTION_RBK_RATE, "3",
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
            OPTION_MSK_NSI_URL, "http://localhost:2000/nsiws/services/NSIService",
            OPTION_MSK_NSI_USER, "UEK_SOAP",
            OPTION_MSK_NSI_PASSWORD, "la0d6xxw",
            OPTION_MSK_NSI_COMPANY, "dogm_nsi",
            OPTION_IMPORT_RNIP_PAYMENTS_ON, "0",
            OPTION_IMPORT_RNIP_PAYMENTS_TIME, null,
            OPTION_SEND_PAYMENT_NOTIFY_SMS_ON, "0"
};

    private Long idOfOption;
    private String optionText;

    Option() {
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
