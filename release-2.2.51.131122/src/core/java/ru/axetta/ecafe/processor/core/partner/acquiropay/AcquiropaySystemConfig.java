/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.acquiropay;

import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: r.kalimullin
 * Date: 08.10.13
 * Time: 18:21
 */

public class AcquiropaySystemConfig {

    public static final String ACQUIROPAY_PARAM_BASE = ".acquiropay";

    public static final String LINKING_URL_PARAM = ".linkingUrl";
    public static final String PAYMENT_URL_PARAM = ".paymentUrl";
    public static final String PRODUCT_ID_PARAM = ".productId";
    public static final String MERCHANT_ID_PARAM = ".merchantId";
    public static final String SECRET_WORD_PARAM = ".secretWord";

    private String linkingUrl;
    private String paymentUrl;
    private long productId;
    private long merchantId;
    private String secretWord;

    public AcquiropaySystemConfig(Properties properties, String paramBaseName) {
        String linkingUrlKey = paramBaseName + ACQUIROPAY_PARAM_BASE + LINKING_URL_PARAM;
        linkingUrl = properties.getProperty(linkingUrlKey, "https://secure.acquiropay.com");
        String paymentUrlKey = paramBaseName + ACQUIROPAY_PARAM_BASE + PAYMENT_URL_PARAM;
        paymentUrl = properties.getProperty(paymentUrlKey, "https://gateway.acquiropay.com");
        String secretWordKey = paramBaseName + ACQUIROPAY_PARAM_BASE + SECRET_WORD_PARAM;
        secretWord = properties.getProperty(secretWordKey, "YunW2hD8Zs4");
        String productIdKey = paramBaseName + ACQUIROPAY_PARAM_BASE + PRODUCT_ID_PARAM;
        productId = Long.parseLong(properties.getProperty(productIdKey, "3814"));
        String merchantIdKey = paramBaseName + ACQUIROPAY_PARAM_BASE + MERCHANT_ID_PARAM;
        merchantId = Long.parseLong(properties.getProperty(merchantIdKey, "516"));
    }

    public String getLinkingUrl() {
        return linkingUrl;
    }

    public String getPaymentUrl() {
        return paymentUrl;
    }

    public long getProductId() {
        return productId;
    }

    public long getMerchantId() {
        return merchantId;
    }

    public String getSecretWord() {
        return secretWord;
    }
}
