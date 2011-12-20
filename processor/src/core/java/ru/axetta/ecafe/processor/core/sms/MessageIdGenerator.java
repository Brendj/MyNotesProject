/*
 * Copyright (c) 2010. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.sms;

import ru.axetta.ecafe.processor.core.persistence.Registry;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.apache.commons.lang.StringUtils;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 13.10.2009
 * Time: 15:42:35
 * To change this template use File | Settings | File Templates.
 */
public class MessageIdGenerator {

    private static final Logger logger = LoggerFactory.getLogger(MessageIdGenerator.class);

    private static final String MESSAGE_ID_ALPHABET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int MESSAGE_ID_LENGTH = 16;
    private static final int MESSAGE_ID_STEP = 1;

    private static class AdditionResult {

        private final int overflow;
        private final char result;

        public AdditionResult(int overflow, char result) {
            this.overflow = overflow;
            this.result = result;
        }

        public int getOverflow() {
            return overflow;
        }

        public char getResult() {
            return result;
        }
    }

    private final SessionFactory sessionFactory;

    public MessageIdGenerator(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /**
     * Warning: has to be threadsafe
     *
     * @return
     * @throws Exception
     */
    public synchronized String generate() throws Exception {
        Transaction transaction = null;
        Session session = sessionFactory.openSession();
        try {
            transaction = session.beginTransaction();
            Registry registry = (Registry) session.get(Registry.class, Registry.THE_ONLY_INSTANCE_ID);
            String timePart = StringUtils.leftPad(Long.toHexString(new Date().getTime()), 16, '0');
            String smsId = add(registry.getSmsId(), MESSAGE_ID_STEP);
            registry.setSmsId(smsId);
            session.update(registry);
            session.flush();
            transaction.commit();
            transaction = null;
            return smsId + StringUtils.reverse(timePart);
        } finally {
            HibernateUtils.rollback(transaction, logger);
            HibernateUtils.close(session, logger);
        }
    }

    public static String add(String argument, int addition) throws Exception {
        String adoptedArgument = adopt(argument);
        int currentAddition = addition;
        StringBuilder stringBuilder = new StringBuilder(MESSAGE_ID_LENGTH);
        for (int i = 0; i <= MESSAGE_ID_LENGTH - 1; ++i) {
            char currentChar = adoptedArgument.charAt(i);
            AdditionResult additionResult = add(currentChar, currentAddition);
            stringBuilder.append(additionResult.getResult());
            currentAddition = additionResult.getOverflow();
        }
        return stringBuilder.toString();
    }

    private static String adopt(String argument) throws IllegalArgumentException {
        String adoptedArgument = argument;
        if (StringUtils.isEmpty(adoptedArgument)) {
            return StringUtils.repeat(String.valueOf(MESSAGE_ID_ALPHABET.charAt(0)), MESSAGE_ID_LENGTH);
        }
        int argumentLength = StringUtils.length(adoptedArgument);
        if (argumentLength < MESSAGE_ID_LENGTH) {
            adoptedArgument = StringUtils
                    .repeat(String.valueOf(MESSAGE_ID_ALPHABET.charAt(0)), MESSAGE_ID_LENGTH - argumentLength)
                    + adoptedArgument;
        } else if (argumentLength > MESSAGE_ID_LENGTH) {
            throw new IllegalArgumentException();
        }
        return adoptedArgument.replace(' ', MESSAGE_ID_ALPHABET.charAt(0));
    }

    private static AdditionResult add(char argument, int addition) throws IllegalArgumentException {
        int index = MESSAGE_ID_ALPHABET.indexOf(argument);
        if (-1 == index) {
            throw new IllegalArgumentException();
        }
        index += addition;
        int overflow = index / MESSAGE_ID_ALPHABET.length();
        char result = MESSAGE_ID_ALPHABET.charAt(index % MESSAGE_ID_ALPHABET.length());
        return new AdditionResult(overflow, result);
    }
}