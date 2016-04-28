/**
 * $RCSfileIXAdESConfig.java,v $
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
 * Служебный интерфейс с описанием контейнера для
 * создания/проверки подписи формата XAdES.
 *
 * @author Copyright 2004-2015 Crypto-Pro. All rights reserved.
 * @.Version
 */
public interface IXAdESConfig {

    /**
     * Провайдер по умолчанию.
     *
     * @return провайдер.
     */
    public String getDefaultProvider();

    /**
     * Тип контейнера.
     *
     * @return тип.
     */
    public String getKeyStoreType();

    /**
     * Контейнер для подписи.
     *
     * @return контейнер.
     */
    public ISignatureContainer getSignatureContainer();

}
