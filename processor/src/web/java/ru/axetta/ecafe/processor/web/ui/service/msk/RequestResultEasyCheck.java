/*
 * Copyright (c) 2014. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.service.msk;

/**
 * Created with IntelliJ IDEA.
 * User: anvarov
 * Date: 30.12.14
 * Time: 12:46
 */

public class RequestResultEasyCheck {

    private int errorCode;
    private String errorDesc;
    private SubscriptionListEasyCheck subscriptionListEasyCheck;

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDesc() {
        return errorDesc;
    }

    public void setErrorDesc(String errorDesc) {
        this.errorDesc = errorDesc;
    }

    public SubscriptionListEasyCheck getSubscriptionListEasyCheck() {
        return subscriptionListEasyCheck;
    }

    public void setSubscriptionListEasyCheck(SubscriptionListEasyCheck subscriptionListEasyCheck) {
        this.subscriptionListEasyCheck = subscriptionListEasyCheck;
    }
}
