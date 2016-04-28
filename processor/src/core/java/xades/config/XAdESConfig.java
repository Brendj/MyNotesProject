/**
 * $RCSfileXAdESConfig.java,v $
 * version $Revision: 36379 $
 * created 04.06.2015 9:19 by afevma
 * last modified $Date: 2012-05-30 12:19:27 +0400 (Ср, 30 май 2012) $ by $Author: afevma $
 *
 * Copyright 2004-2015 Crypto-Pro. All rights reserved.
 * Этот файл содержит информацию, являющуюся
 * собственностью компании Крипто-Про.
 *
 * Любая часть этого файла не может быть скопирована,
 * исправлена, переведена на другие языки,
 * локализована или модифицирована любым способом,
 * откомпилирована, передана по сети с или на
 * любую компьютерную систему без предварительного
 * заключения соглашения с компанией Крипто-Про.
 */
package xades.config;

import xades.config.container.ISignatureContainer;

/**
 * Служебный класс конфигурации для создания/проверки
 * подписи формата XAdES.
 *
 * @author Copyright 2004-2015 Crypto-Pro. All rights reserved.
 * @.Version
 */
public class XAdESConfig implements IXAdESConfig {

    /**
     * Провайдер по умолчанию.
     */
    private final String defaultProvider;

    /**
     * Тип контейнера.
     */
    private final String keyStoreType;

    /**
     * Контейнер подписи.
     */
    private final ISignatureContainer signatureContainer;

    /**
     * Конструктор.
     *
     * @param provider Провайдер по умолчанию.
     * @param type Тип контейнера.
     * @param container Контейнер подписи.
     */
    public XAdESConfig(String provider, String type, ISignatureContainer container) {

        defaultProvider = provider;
        keyStoreType = type;
        signatureContainer = container;

    }

    @Override
    public String getDefaultProvider() {
        return defaultProvider;
    }

    @Override
    public String getKeyStoreType() {
        return keyStoreType;
    }

    @Override
    public ISignatureContainer getSignatureContainer() {
        return signatureContainer;
    }

    /**
     * Контейнер подписи ГОСТ Р 34.10-2012 (256).
     */
    // public static final IXAdESConfig CONFIG_2012_S = new XAdESConfig(
    //    JCP.PROVIDER_NAME, JCP.HD_STORE_NAME, new Container2012_256());

    /**
     * Контейнер подписи ГОСТ Р 34.10-2012 (512).
     */
    // public static final IXAdESConfig CONFIG_2012_L = new XAdESConfig(
    //    JCP.PROVIDER_NAME, JCP.HD_STORE_NAME, new Container2012_512());

    /**
     * Контейнер подписи ГОСТ Р 34.10-2001 с отозванным сертификатом
     * (промежуточного УЦ).
     */
    // public static final IXAdESConfig CONFIG_2001_R = new XAdESConfig(
    //    JCP.PROVIDER_NAME, JCP.HD_STORE_NAME, new RevokedContainer2001());

}
