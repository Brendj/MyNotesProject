/*
 * Copyright (c) 2011. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web; /**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 21.10.2010
 * Time: 12:18:55
 * To change this template use File | Settings | File Templates.
 */

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.*;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.metamodel.MetadataSources;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;
import java.io.StringReader;
import java.util.*;

public class RuntimeInitListener implements ServletContextListener {
    // Logger
    private static final Logger logger = LoggerFactory.getLogger(RuntimeInitListener.class);

    // Public constructor is required by servlet spec
    public RuntimeInitListener() {
    }

    @PersistenceUnit
    static SessionFactory sessionFactory;

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        // Retrieve application deploy path
        String contextPath = servletContext.getRealPath(File.separator);
        // Configure runtime context
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            SessionFactory sessionFactory = createSessionFactory();
            persistenceSession = sessionFactory.openSession();
            persistenceTransaction = persistenceSession.beginTransaction();
            // Get configuration from CF_Options
            Criteria criteria = persistenceSession.createCriteria(Option.class);
            criteria.add(Restrictions.eq("idOfOption", 1L));
            Option option = (Option)criteria.uniqueResult();
            String optionText = option.getOptionText();
            // Check for Operator, Budget, Client type of contragents existence
            boolean operatorExists = false;
            boolean budgetExists = false;
            boolean clientExists = false;
            criteria = persistenceSession.createCriteria(Contragent.class);
            criteria.add(Restrictions.or(Restrictions.eq("classId", 3),
                         Restrictions.or(Restrictions.eq("classId", 4), Restrictions.eq("classId", 5))));
            List contragentList = criteria.list();
            // Create if not
            for (Object object : contragentList) {
                Contragent contragent = (Contragent) object;
                if (contragent.getClassId().equals(3)) {
                    operatorExists = true;
                    logger.info("Contragent with class \"Operator\" exists, name \"" + contragent.getContragentName() + "\"");
                }
                if (contragent.getClassId().equals(4)) {
                    budgetExists = true;
                    logger.info("Contragent with class \"Budget\" exists, name \"" + contragent.getContragentName() + "\"");
                }
                if (contragent.getClassId().equals(5)) {
                    clientExists = true;
                    logger.info("Contragent with class \"Client\" exists, name \"" + contragent.getContragentName() + "\"");
                }
            }

            if (!operatorExists)
                createContragent(persistenceSession, "Айкьютек", 3);
            if (!budgetExists)
                createContragent(persistenceSession, "Бюджет", 4);
            if (!clientExists)
                createContragent(persistenceSession, "Клиент", 5);

            HashMap<Long, String> initCategory = new HashMap<Long, String>();
                initCategory.put(Long.valueOf(-90),"Начальные классы");
                initCategory.put(Long.valueOf(-91),"Средние классы");
                initCategory.put(Long.valueOf(-92),"Старшие классы");
                initCategory.put(Long.valueOf(-101),"1 класс");
                initCategory.put(Long.valueOf(-102),"2 класс");
                initCategory.put(Long.valueOf(-103),"3 класс");
                initCategory.put(Long.valueOf(-104),"4 класс");
                initCategory.put(Long.valueOf(-105),"5 класс");
                initCategory.put(Long.valueOf(-106),"6 класс");
                initCategory.put(Long.valueOf(-107),"7 класс");
                initCategory.put(Long.valueOf(-108),"8 класс");
                initCategory.put(Long.valueOf(-109),"9 класс");
                initCategory.put(Long.valueOf(-110),"10 класс");
                initCategory.put(Long.valueOf(-111),"11 класс");

            Criteria categoryDiscountCriteria;
            Set set=initCategory.entrySet();
            Iterator i = set.iterator();
            while(i.hasNext()){
                Map.Entry me = (Map.Entry)i.next();
                categoryDiscountCriteria = persistenceSession.createCriteria(CategoryDiscount.class);
                categoryDiscountCriteria.add(
                        Restrictions.eq("idOfCategoryDiscount",me.getKey())
                );
                if ( categoryDiscountCriteria.list().size()==0 ) {
                    createCategoryDiscount(persistenceSession, (Long) me.getKey(), String.valueOf(me.getValue()), "");
                }
            }

            persistenceTransaction.commit();
            persistenceTransaction = null;
            RuntimeContext.initializeInstance(contextPath, loadConfig(optionText), sessionFactory);
        } catch (Exception e) {
            logger.error("Failed to init application.", e);
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);
        }
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        long runtimeContextRefCount;
        try {
            // Try to destroy global runtime context and check for success
            runtimeContextRefCount = RuntimeContext.destroyInstance();
        } catch (RuntimeContext.NotInitializedException e) {
            if (logger.isDebugEnabled()) {
                logger.debug("Failed to destroy runtime context instance.", e);
            }
            return;
        }
        // If global runtime context destruction fails...
        if (runtimeContextRefCount != 0) {
            // ... add log notification
            logger.error("Runtime context was not destroyed because it was locked by something else.");
        }
    }

    private static Properties loadConfig(String configString) throws Exception {
        StringReader stringReader = new StringReader(configString);
        try {
            Properties properties = new Properties();
            properties.load(stringReader);
            return properties;
        } finally {
            stringReader.close();
        }
    }

    private static SessionFactory createSessionFactory() throws Exception {
        if (logger.isDebugEnabled()) {
            logger.debug("Creating application-wide Hibernate session factory.");
        }
        return sessionFactory;
    }

    private static void close(Context context) {
        if (null != context) {
            try {
                context.close();
            } catch (NamingException e) {
                logger.error("Failed to close JNDI context", e);
            }
        }
    }

    public void createCategoryDiscount(Session session, Long idOfCategoryDiscount, String categoryName, String description){
        Date currentTime = new Date();
        CategoryDiscount categoryDiscount = new CategoryDiscount(idOfCategoryDiscount, categoryName, description, currentTime, currentTime);

        session.save(categoryDiscount);
        logger.info("Category with name \"" + categoryName + "\" created");
    }

    public void createContragent(Session session, String contragentName, Integer classId) throws Exception {
        Person contactPerson = new Person("Иван", "Иванов", "");
        session.save(contactPerson);
        Date currentTime = new Date();
        Contragent contragent = new Contragent(contactPerson, contragentName, classId, 1, "",
                "", currentTime, currentTime, "", false);
        session.save(contragent);
        String className = classId.equals(3) ? "Оператор" : classId.equals(4) ? "Бюджет" : "Клиент";
        logger.info("Contragent with class \"" + className + "\" created, name \"" + contragentName + "\"");
    }

}
