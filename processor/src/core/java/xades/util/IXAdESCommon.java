/**
 * $RCSfileIXAdESCommon.java,v $
 * version $Revision: 36379 $
 * created 11.06.2015 15:50 by afevma
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
package xades.util;

import java.io.File;

/**
 * Служебный интерфейс с константами.
 *
 * @author Copyright 2004-2015 Crypto-Pro. All rights reserved.
 * @.Version
 */
public interface IXAdESCommon {

    /**
     * Рабочая папка.
     */
    public static final String TRUST_DIR = System.getProperty("user.dir") +
            File.separator + "data" + File.separator;

    /**
     * Рабочая папка.
     */
    public static final String WORK_DIR = System.getProperty("user.dir") +
            File.separator + "temp" + File.separator;

    /**
     * Хранилище корневых сертификатов для построения
     * и проверки цепочек.
     */
    public static final String TRUST_STORE = TRUST_DIR + "xadesTrustStore";

    /**
     * Пароль к хранилищу.
     */
    public static final char[] TRUST_PASSWORD = "1".toCharArray();

    /**
     * Класс провайдера XMLDSigRI.
     */
    public static String providerName = "ru.CryptoPro.JCPxml.dsig.internal.dom.XMLDSigRI";

    /**
     * Провайдер XMLDSigRI.
     */
    //public static final Provider xmlDSigRi = new ru.CryptoPro.JCPxml.dsig.internal.dom.XMLDSigRI();

    /**
     * Идентификатор Security.
     */
    public static final String ACTOR = "http://smev.gosuslugi.ru/actors/smev";

    /**
     * Идентификатор отправителя.
     */
    public static final String SENDER_EXAMPLE_1 = "000147";

    /**
     * Идентификатор отправителя (другой).
     */
    public static final String SENDER_EXAMPLE_2 = "0000a1";

    /**
     * Идентификатор подписываемого узла.
     */
    public static final String SIGNING_ID = "P_a1234567-bcf8-90de-f123-4567890abcde";

    /**
     * Идентификатор запроса.
     */
    public static final String REQUEST_MESSAGE_ID = "P_a7654321-8bcf-de90-123f-abcde0987654";

}
