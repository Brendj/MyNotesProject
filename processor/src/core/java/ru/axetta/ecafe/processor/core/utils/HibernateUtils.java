/*
 * Copyright (c) 2009. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.utils;

import org.hibernate.Criteria;
import org.hibernate.Transaction;
import org.hibernate.classic.Session;
import org.hibernate.criterion.Order;
import org.slf4j.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 10.09.2009
 * Time: 12:33:27
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

    public static void close(Session session, Logger logger) {
        if (null != session && session.isOpen()) {
            try {
                session.close();
            } catch (Exception e) {
                logger.error("Failed to close persistance session", e);
            }
        }
    }

    public static void close(org.hibernate.Session session, Logger logger) {
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
}
