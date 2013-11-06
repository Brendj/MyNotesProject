/*
 * Copyright (c) 2013. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.core.test.synch;

import ru.axetta.ecafe.processor.core.RuntimeContext;

import org.hibernate.Session;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.util.Properties;
import java.util.Scanner;

/**
 * Created with IntelliJ IDEA.
 * User: chirikov
 * Date: 16.07.13
 * Time: 18:58
 * To change this template use File | Settings | File Templates.
 */
public class TestDBInit {
    @PersistenceContext
    private EntityManager em;

    private PlatformTransactionManager platformTransactionManager;

    public PlatformTransactionManager getPlatformTransactionManager() {
        return platformTransactionManager;
    }

    public void setPlatformTransactionManager(PlatformTransactionManager platformTransactionManager) {
        this.platformTransactionManager = platformTransactionManager;
    }

    @PostConstruct
    public void init() throws Exception {
        new TransactionTemplate(platformTransactionManager).execute(new TransactionCallback<Object>() {
            @Override
            public Object doInTransaction(TransactionStatus transactionStatus) {
                try {
                    executeStartup();
                } catch (Exception e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                return null;
            }
        });


        RuntimeContext.setSessionFactory(((Session)em.getDelegate()).getSessionFactory());
    }

    private void executeStartup () throws Exception {
        TestConfigUtil.runSQL(RegisterSynchSingletonBeanTest.class, null, TestConstant.SCRIPT_SYSTEMS_DROP, em);
        TestConfigUtil.runSQL(RegisterSynchSingletonBeanTest.class, null, TestConstant.SCRIPT_SYSTEM_CREATE, em);
        TestConfigUtil.runSQL(RegisterSynchSingletonBeanTest.class, null, TestConstant.SCRIPT_SYSTEM_DATA, em);
        String content = new Scanner(TestDBInit.class.getResourceAsStream("/META-INF/test.properties")).useDelimiter("\\Z").next();
        em.createNativeQuery("UPDATE CF_Options set OptionText=:p where IdOfOption=1").setParameter("p", content).executeUpdate();
    }
}
