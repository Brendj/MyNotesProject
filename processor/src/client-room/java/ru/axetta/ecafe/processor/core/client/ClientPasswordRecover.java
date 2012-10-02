/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.client;

/*import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;*/
/*import ru.axetta.ecafe.processor.core.service.EventNotificationService;*/
/*import HibernateUtils;*/

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;
        /*import org.hibernate.Criteria;*/
/*import org.hibernate.Session;*/
/*import org.hibernate.SessionFactory;*/
/*import org.hibernate.Transaction;*/
/*import org.hibernate.criterion.Restrictions;*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

        import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
        import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
        import java.util.LinkedList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: damir
 * Date: 17.04.12
 * Time: 15:51
 * To change this template use File | Settings | File Templates.
 * Класс для организации востановления пароля пользователя.
 */
public class ClientPasswordRecover {

    public final static int CONTRACT_SEND_RECOVER_PASSWORD = 0;
    public final static int NOT_FOUND_CONTRACT_BY_ID = 1;
    public final static int CONTRACT_HAS_NOT_EMAIL = 2;
    private static String PASS = null;

    final String CONTRACT_ID_PARAM = "contractId";
    final String DATE_PARAM = "date";
    final String PASS_PARAM = "p";

    /* Время существования ссылки */
    private final static long LIFETIME_OF_URL = 1000 * 60 * 60 * 24;

    private static final Logger logger = LoggerFactory.getLogger(ClientPasswordRecover.class);



    private List<ru.axetta.ecafe.processor.core.mail.File> files = new LinkedList<ru.axetta.ecafe.processor.core.mail.File>();


    private static String encryptURL(String url) throws NoSuchAlgorithmException, IOException {
        MessageDigest hash = MessageDigest.getInstance("SHA1");
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(url.getBytes());
        DigestInputStream digestInputStream = new DigestInputStream(arrayInputStream, hash);
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(digestInputStream, arrayOutputStream);
        return new String(Base64.encodeBase64(arrayOutputStream.toByteArray()), CharEncoding.US_ASCII);
    }
}
