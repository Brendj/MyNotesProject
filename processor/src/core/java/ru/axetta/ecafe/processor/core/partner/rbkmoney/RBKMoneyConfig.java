/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.rbkmoney;

import org.apache.commons.lang.StringUtils;

import java.net.URI;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 04.10.2010
 * Time: 15:39:48
 * To change this template use File | Settings | File Templates.
 */
public class RBKMoneyConfig {

    private static final String RBK_PARAM_BASE = ".rbk";
    private static final String ESHOP_ID_PARAM = RBK_PARAM_BASE + ".eshopId";
    private static final String SERVICE_NAME_PARAM = RBK_PARAM_BASE + ".serviceName";
    private static final String CONTRAGENT_NAME_PARAM = RBK_PARAM_BASE + ".contragentName";
    private static final String PURCHASE_URI_NAME_PARAM = RBK_PARAM_BASE + ".purchaseUri";
    private static final String SECRET_KEY_PARAM = RBK_PARAM_BASE + ".secretKey";
    //private static final String RATE_PARAM = RBK_PARAM_BASE + ".rate";

    private final String eshopId;
    private final String serviceName;
    private final String contragentName;
    private final URI purchaseUri;
    private final String secretKey;
    private  Double rate;
    private Boolean show;

    public RBKMoneyConfig(Properties properties, String paramBaseName,Double rate,Boolean show) throws Exception {
        String eshopIdParam = paramBaseName + ESHOP_ID_PARAM;
        String serviceNameParam = paramBaseName + SERVICE_NAME_PARAM;
        String contragentNameParam = paramBaseName + CONTRAGENT_NAME_PARAM;
        String purchaseUriNameParam = paramBaseName + PURCHASE_URI_NAME_PARAM;
        String secretKeyParam = paramBaseName + SECRET_KEY_PARAM;
        //String rateParam = paramBaseName + RATE_PARAM;

        this.eshopId = properties.getProperty(eshopIdParam);
        if (StringUtils.isEmpty(eshopId)) {
            throw new IllegalArgumentException(String.format("Parameter \"%s\" missing", eshopIdParam));
        }
        this.secretKey = properties.getProperty(secretKeyParam);
        this.serviceName = properties
                .getProperty(serviceNameParam, "Перечисление средств на карту питания \"Новая школа\"");
        this.contragentName = properties.getProperty(contragentNameParam, "RBK Money");
        this.purchaseUri = new URI(
                properties.getProperty(purchaseUriNameParam, "https://rbkmoney.ru/acceptpurchase.aspx"));
        //this.rate = Double.valueOf(properties.getProperty(rateParam, "1"));
        this.rate=rate;
        this.show=show;


    }

    public String getEshopId() {
        return eshopId;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getContragentName() {
        return contragentName;
    }

    public URI getPurchaseUri() {
        return purchaseUri;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Boolean getShow() {
        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }
}
