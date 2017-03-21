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

import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.persistence.PersistenceUnit;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.io.File;

public class RuntimeInitListener implements ServletContextListener {
    // Logger
    private static final Logger logger = LoggerFactory.getLogger(RuntimeInitListener.class);

    // Public constructor is required by servlet spec
    public RuntimeInitListener() {
    }

    @PersistenceUnit(unitName = "processorPU")
    static SessionFactory sessionFactory;

    @PersistenceUnit(unitName = "reportsPU")
    static SessionFactory reportsSessionFactory;

    @PersistenceUnit(unitName = "externalServicesPU")
    static SessionFactory externalServicesSessionFactory;

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static SessionFactory getReportsSessionFactory() {
        return reportsSessionFactory;
    }

    public void contextInitialized(ServletContextEvent servletContextEvent) {
        ServletContext servletContext = servletContextEvent.getServletContext();
        // Retrieve application deploy path
        String contextPath = servletContext.getRealPath(File.separator);
        RuntimeContext.setSessionFactory(sessionFactory);
        RuntimeContext.setReportsSessionFactory(reportsSessionFactory);
        RuntimeContext.setExternalServicesSessionFactory(externalServicesSessionFactory);
    }

    public void contextDestroyed(ServletContextEvent servletContextEvent) {
/*        long runtimeContextRefCount;
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
        }     */
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


}
