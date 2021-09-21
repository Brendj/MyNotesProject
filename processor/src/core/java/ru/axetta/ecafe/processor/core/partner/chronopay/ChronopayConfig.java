/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.partner.chronopay;

import ru.axetta.ecafe.processor.core.persistence.Contragent;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOReadonlyService;

import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: timur
 * Date: 04.07.12
 * Time: 13:29
 * To change this template use File | Settings | File Templates.
 */

/**
 * Конфигурация Chronopay
 */
public class ChronopayConfig {


    public static final String CHRONOPAY_PARAM_BASE = ".chronopay";
    public static final String SHARED_SEC_PARAM=CHRONOPAY_PARAM_BASE+".sharedSec";
   // public static final String RATE_PARAM = CHRONOPAY_PARAM_BASE + ".rate";
    public static final String IP_PARAM=CHRONOPAY_PARAM_BASE+".ip";
    public static final String CONTRAGENT_NAME_PARAM = CHRONOPAY_PARAM_BASE + ".contragentName";
    public static final String PURCHASE_URI_NAME_PARAM = CHRONOPAY_PARAM_BASE + ".purchaseUri";
    public static final String CALLBACK_URL_PARAM=CHRONOPAY_PARAM_BASE+".callbackUrl";


    /**
     * параметр sharedSec, известный только Chronopay и itech
     */
  private final String sharedSec;
    /**
     * Комиссия
     */
  private  Double rate;
    /**
     * ip адрес хоста c которого отправляются уведомления о платеже
     */
  private final String ip;
    /**
     * Имя контрагента, вданном случае - Chronopay
     */
  private final String contragentName;
    /**
     * Url платежной страницы Chronopay
     */
  private final String purchaseUri;
    /**
     * Адрес сервлета, который принимает уведомления о платеже
     */
  private  final String callbackUrl;

    private Boolean show;

    /**
     * Берет параметры конфигурации - поля класса из свойств properties, которые извлекаются из таблицы
     * cf_options в базе процессинга
     * @param properties свойства из таблицы cf_options в базе процессинга
     * @param paramBaseName база имен параметров в объекте properties
     * @throws Exception
     */
    public ChronopayConfig(Properties properties, String paramBaseName,Double rate,Boolean show) throws Exception {
           String sharedSecParam=paramBaseName+SHARED_SEC_PARAM;
           //String rateParam=paramBaseName+RATE_PARAM;
           String ipParam=paramBaseName+IP_PARAM;
           String contragentNameParam=paramBaseName+CONTRAGENT_NAME_PARAM;
           String purchaseUriParam=paramBaseName+PURCHASE_URI_NAME_PARAM;
           String callbackUrlParam=paramBaseName+CALLBACK_URL_PARAM;

        this.sharedSec=properties.getProperty(sharedSecParam,"Babdkgfj03586FSEW#*fglsq[mc");
        //this.rate=Double.parseDouble(properties.getProperty(rateParam, "0"));
        this.rate=rate;
        this.ip=properties.getProperty(ipParam,"");
        this.contragentName=properties.getProperty(contragentNameParam,"Chronopay");
        this.purchaseUri=properties.getProperty(purchaseUriParam,"https://payments.chronopay.com/");
        this.callbackUrl=properties.getProperty(callbackUrlParam,"https://78.46.34.200:9999/processor/chronopay/acceptpay");
         this.show=show;
    }


    public static String getHash(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
        //String s="f78spx";
        //String s="muffin break";
        MessageDigest m = MessageDigest.getInstance("MD5");
        m.reset();
        // передаем в MessageDigest байт-код строки
        m.update(str.getBytes("utf-8"));
        // получаем MD5-хеш строки без лидирующих нулей
        String s2 = new BigInteger(1, m.digest()).toString(16);
        StringBuilder sb = new StringBuilder(32);
        // дополняем нулями до 32 символов, в случае необходимости
        //System.out.println(32 - s2.length());
        for (int i = 0, count = 32 - s2.length(); i < count; i++) {
            sb.append("0");
        }
        // возвращаем MD5-хеш
        return sb.append(s2).toString();
    }

    public String getSharedSec() {
        return sharedSec;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public Double getRate() {
        return rate;
    }

    public String getIp() {
        return ip;
    }

    public String getContragentName() {
        return contragentName;
    }

    public String getPurchaseUri() {
        return purchaseUri;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public Boolean getShow() {
        DAOReadonlyService daoReadonlyService = DAOReadonlyService.getInstance();
        Contragent contragent = daoReadonlyService.getContragentByName("Chronopay");
        if(contragent==null)return false;

        return show;
    }

    public void setShow(Boolean show) {
        this.show = show;
    }
}
