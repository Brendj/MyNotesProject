/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.client;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.logic.ProcessorUtils;
import ru.axetta.ecafe.processor.core.mail.File;
import ru.axetta.ecafe.processor.core.persistence.Client;
import ru.axetta.ecafe.processor.core.persistence.Option;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOUtils;
import ru.axetta.ecafe.processor.core.service.EventNotificationService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.util.UriUtils;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.CharEncoding;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.URI;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
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

    private final SessionFactory sessionFactory;

    private List<File> files = new LinkedList<File>();

    public ClientPasswordRecover(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    synchronized String getPasswordSeed(Session session) {
        if (PASS==null) {
            PASS = DAOUtils.getOptionValue(session, Option.OPTION_PASSWORD_RESTORE_SEED, null);
            if (PASS==null) {
                StringBuilder sb = new StringBuilder();
                SecureRandom random = new SecureRandom();
                PASS = new BigInteger(130, random).toString(32);
                DAOUtils.setOptionValue(session, Option.OPTION_PASSWORD_RESTORE_SEED, PASS);
            }
        }
        return PASS;
    }

    public int sendPasswordRecoverURLFromEmail(Long contractId, HttpServletRequest request) throws Exception {
        int succeeded = -1;
        Transaction transaction = null;
        Session session = sessionFactory.openSession();
        try {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            transaction = session.beginTransaction();
            Criteria clientWithSameContractIdCriteria = session.createCriteria(Client.class);
            clientWithSameContractIdCriteria.add(Restrictions.eq("contractId", contractId));
            Client currClient = (Client) clientWithSameContractIdCriteria.uniqueResult();
            succeeded = (null != currClient ? 0 : -1);
            if (null != currClient) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Client with contractId: %s is exist", contractId));
                }
                String clientEmail = currClient.getEmail();
                if (clientEmail == null || clientEmail.isEmpty() || clientEmail.equalsIgnoreCase("")) {
                    succeeded = CONTRACT_HAS_NOT_EMAIL;
                } else {
                    URI url = new URI(request.getRequestURL().toString());
                    url = UriUtils.putParam(url, "page", "recover");
                    String sDate = String.valueOf(System.currentTimeMillis());
                    url = UriUtils.putParam(url, DATE_PARAM, sDate);
                    String sContractId = String.valueOf(currClient.getContractId());
                    url = UriUtils.putParam(url, CONTRACT_ID_PARAM, sContractId);
                    String hash = encryptURL(getPasswordSeed(session) + sDate + sContractId);
                    String strURL = UriUtils.putParam(url, PASS_PARAM, hash).toString();

                    RuntimeContext.getAppContext().getBean(EventNotificationService.class).sendEmail(currClient,
                            EventNotificationService.MESSAGE_RESTORE_PASSWORD, new String[]{"url", strURL});
                    logger.info("Sent recover password URL to '" + clientEmail + "'");
                }
            } else {
                succeeded = NOT_FOUND_CONTRACT_BY_ID;
            }
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return succeeded;
    }


    public int sendPasswordRecoverURLFromEmail(Long contractId, RequestWebParam request) throws Exception {
        int succeeded = -1;
        Transaction transaction = null;
        Session session = sessionFactory.openSession();
        try {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            transaction = session.beginTransaction();
            Criteria clientWithSameContractIdCriteria = session.createCriteria(Client.class);
            clientWithSameContractIdCriteria.add(Restrictions.eq("contractId", contractId));
            Client currClient = (Client) clientWithSameContractIdCriteria.uniqueResult();
            succeeded = (null != currClient ? 0 : -1);
            if (null != currClient) {
                if (logger.isDebugEnabled()) {
                    logger.debug(String.format("Client with contractId: %s is exist", contractId));
                }
                String clientEmail = currClient.getEmail();
                if (clientEmail == null || clientEmail.isEmpty() || clientEmail.equalsIgnoreCase("")) {
                    succeeded = CONTRACT_HAS_NOT_EMAIL;
                } else {
                    URI url = new URI(request.url);
                    url = UriUtils.putParam(url, "page", "recover");
                    String sDate = String.valueOf(System.currentTimeMillis());
                    url = UriUtils.putParam(url, DATE_PARAM, sDate);
                    String sContractId = String.valueOf(currClient.getContractId());
                    url = UriUtils.putParam(url, CONTRACT_ID_PARAM, sContractId);
                    String hash = encryptURL(getPasswordSeed(session) + sDate + sContractId);
                    String strURL = UriUtils.putParam(url, PASS_PARAM, hash).toString();

                    RuntimeContext.getAppContext().getBean(EventNotificationService.class).sendEmail(currClient,
                            EventNotificationService.MESSAGE_RESTORE_PASSWORD, new String[]{"url", strURL});
                    logger.info("Sent recover password URL to '" + clientEmail + "'");
                }
            } else {
                succeeded = NOT_FOUND_CONTRACT_BY_ID;
            }
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return succeeded;
    }

    public int sendPasswordRecoverBySms(Long contractId) throws Exception {
        int succeeded = -1;
        Transaction transaction = null;
        Session session = sessionFactory.openSession();
        try {
            transaction = session.beginTransaction();
            Criteria criteria = session.createCriteria(Client.class);
            criteria.add(Restrictions.eq("contractId", contractId));
            Client currClient = (Client) criteria.uniqueResult();
            succeeded = (null != currClient ? 0 : -1);
            if (null != currClient) {
                String clientMobile = currClient.getMobile();
                if (StringUtils.isEmpty(clientMobile)) {
                    succeeded = CONTRACT_HAS_NOT_EMAIL;
                } else {
                    String plainPassword = ProcessorUtils.generateSmsCode(); //генерируем пароль аналогично алгоритму для входа в бэк-офис (4 цифры)
                    currClient.setPassword(plainPassword);
                    currClient.setUpdateTime(new Date());
                    session.update(currClient);

                    String[] values = generateNotificationParams(currClient, plainPassword);
                    RuntimeContext.getAppContext().getBean(EventNotificationService.class).sendNotificationClientNewPasswordAsync(currClient, values);

                    logger.info("Sent recover password sms code to '" + clientMobile + "'");
                }
            } else {
                succeeded = NOT_FOUND_CONTRACT_BY_ID;
            }
            transaction.commit();
            transaction = null;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return succeeded;

    }

    private String[] generateNotificationParams(Client client, String password) {
        return new String[] {
                "account", client.getContractId().toString(),
                "password", password
        };
    }

    public boolean checkPasswordRestoreRequest(HttpServletRequest request) throws Exception {
        boolean succeeded = false;
        String sContractId = request.getParameter("contractId");
        String sDate = request.getParameter("date");
        String pass = request.getParameter("p");
        Transaction transaction = null;
        Session session = sessionFactory.openSession();
        try {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            transaction = session.beginTransaction();
            sContractId = sContractId.replaceAll("[^0-9]", "");
            sDate = sDate.replaceAll("[^0-9]", "");
            Long contractId = Long.parseLong(sContractId);
            Long currDate = System.currentTimeMillis();
            Long urlDate = Long.parseLong(sDate);
            /* Проверка жизни ссылки */
            if (currDate - urlDate <= LIFETIME_OF_URL) {
                Criteria clientWithSameContractIdCriteria = session.createCriteria(Client.class);
                clientWithSameContractIdCriteria.add(Restrictions.eq("contractId", contractId));
                Client currClient = (Client) clientWithSameContractIdCriteria.uniqueResult();
                String checkPass = encryptURL(getPasswordSeed(session) + sDate + sContractId);
                int l = checkPass.length()-2; // убираем последние два т.к. могут быть = - они могут неправильно декодироваться
                succeeded = (null != currClient && pass.length()>=l && StringUtils.equals(pass.substring(0, l), checkPass.substring(0, l)));
            }
        } catch (Exception e) {
            succeeded = false;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return succeeded;
    }


    public boolean checkPasswordRestoreRequest(RequestWebParam request) throws Exception {
        boolean succeeded = false;
        String sContractId = request.contractIdParam;
        String sDate = request.dateParam;
        String pass = request.pParam;
        Transaction transaction = null;
        Session session = sessionFactory.openSession();
        try {
            RuntimeContext runtimeContext = RuntimeContext.getInstance();
            transaction = session.beginTransaction();
            sContractId = sContractId.replaceAll("[^0-9]", "");
            sDate = sDate.replaceAll("[^0-9]", "");
            Long contractId = Long.parseLong(sContractId);
            Long currDate = System.currentTimeMillis();
            Long urlDate = Long.parseLong(sDate);
            /* Проверка жизни ссылки */
            if (currDate - urlDate <= LIFETIME_OF_URL) {
                Criteria clientWithSameContractIdCriteria = session.createCriteria(Client.class);
                clientWithSameContractIdCriteria.add(Restrictions.eq("contractId", contractId));
                Client currClient = (Client) clientWithSameContractIdCriteria.uniqueResult();
                String checkPass = encryptURL(getPasswordSeed(session) + sDate + sContractId);
                int l = checkPass.length()-2; // убираем последние два т.к. могут быть = - они могут неправильно декодироваться
                succeeded = (null != currClient && pass.length()>=l && StringUtils.equals(pass.substring(0, l), checkPass.substring(0, l)));
            }
        } catch (Exception e) {
            succeeded = false;
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return succeeded;
    }


    public boolean changePassword(Long contractId, String newPassword) throws Exception {
        boolean succeeded = false;
        Transaction transaction = null;
        Session session = sessionFactory.openSession();
        RuntimeContext runtimeContext = RuntimeContext.getInstance();
        try {
            session = runtimeContext.createPersistenceSession();
            transaction = session.beginTransaction();
            Criteria clientCriteria = session.createCriteria(Client.class);
            clientCriteria.add(Restrictions.eq("contractId", contractId));
            Client client = (Client) clientCriteria.uniqueResult();
            client.setPassword(newPassword);
            client.setUpdateTime(new Date());
            session.update(client);
            succeeded = true;
            session.flush();
            transaction.commit();
            //runtimeContext.getSupportEmailSender()
            //        .postSupportEmail(client.getEmail(), "Воcстановление пароля", "Пароль изменен: " + newPassword,
            //                files);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
        return succeeded;
    }

    private static String encryptURL(String url) throws NoSuchAlgorithmException, IOException {
        MessageDigest hash = MessageDigest.getInstance("SHA1");
        ByteArrayInputStream arrayInputStream = new ByteArrayInputStream(url.getBytes());
        DigestInputStream digestInputStream = new DigestInputStream(arrayInputStream, hash);
        ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
        IOUtils.copy(digestInputStream, arrayOutputStream);
        return new String(Base64.encodeBase64(arrayOutputStream.toByteArray()), CharEncoding.US_ASCII);
    }
}
