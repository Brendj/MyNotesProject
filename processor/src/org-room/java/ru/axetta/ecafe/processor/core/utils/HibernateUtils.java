/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.slf4j.Logger;

import javax.persistence.EntityTransaction;
import java.math.BigInteger;

/**
 * Created by IntelliJ IDEA.
 * User: chirikov
 * Date: 30.07.13
 * Time: 14:15
 * To change this template use File | Settings | File Templates.
 */
public class HibernateUtils {

    private HibernateUtils() {

    }

    public static void rollback(Transaction transaction, Logger logger) {
        if (null != transaction) {
            try {
                transaction.rollback();
            } catch (Exception e) {
                logger.error("Failed to rollback transaction", e);
            }
        }
    }

    public static void rollback(EntityTransaction transaction, Logger logger) {
        if (null != transaction) {
            try {
                transaction.rollback();
            } catch (Exception e) {
                logger.error("Failed to rollback transaction", e);
            }
        }
    }

    public static void close(Session session, Logger logger) {
        if (null != session && session.isOpen()) {
            try {
                session.close();
            } catch (Exception e) {
                logger.error("Failed to close persistance session", e);
            }
        }
    }

    public static Criteria addAscOrder(Criteria criteria, String orderField) {
        return criteria.addOrder(Order.asc(orderField));
    }

    public static String getDbString(Object obj) {
        if (obj == null) {
            return "";
        }
        try {
            return ((String) obj).trim();
        } catch (Exception e) {
            return obj.toString();
        }
    }

    public static Long getDbLong(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return ((BigInteger) obj).longValue();
        } catch (Exception e) {
            return null;
        }
    }

    public static Integer getDbInt(Object obj) {
        if (obj == null) {
            return null;
        }
        try {
            return ((Integer) obj).intValue();
        } catch (Exception e) {
            return null;
        }
    }

    public static Boolean getDbBoolean(Object obj) {
        if (obj == null) {
            return null;
        }
        if (obj == null) {
            return null;
        }
        try {
            return ((Integer) obj).intValue() == 1 ? true : false;
        } catch (Exception e) {
            return null;
        }
    }
}