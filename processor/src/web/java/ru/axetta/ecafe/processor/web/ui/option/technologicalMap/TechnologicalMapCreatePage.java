/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.technologicalMap;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Products;
import ru.axetta.ecafe.processor.core.persistence.TechnologicalMap;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;
import ru.axetta.ecafe.processor.web.ui.BasicWorkspacePage;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.LoggerFactory;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by IntelliJ IDEA.
 * User: Frozen
 * Date: 01.06.12
 * Time: 14:36
 * To change this template use File | Settings | File Templates.
 */
public class TechnologicalMapCreatePage extends BasicWorkspacePage {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(TechnologicalMapCreatePage.class);
    private TechnologicalMap technologicalMap = new TechnologicalMap();

    public TechnologicalMapCreatePage() {
        technologicalMap.setProducts(new ArrayList<Products>());
    }

    public String getPageFilename() {
        return "option/technologicalMap/create";
    }

    public TechnologicalMap getTechnologicalMap() {
        return technologicalMap;
    }

    public void createTechnologicalMap(Session session) throws Exception {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        RuntimeContext runtimeContext = null;
        Session persistenceSession = null;
        Transaction persistenceTransaction = null;
        try {
            runtimeContext = RuntimeContext.getInstance();
            persistenceSession = runtimeContext.createPersistenceSession();
            persistenceTransaction = persistenceSession.beginTransaction();

            persistenceSession.save(technologicalMap);

            persistenceTransaction.commit();
            technologicalMap.setIdOfTechnologicalMap(null);
            persistenceTransaction = null;
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Новая технологическая карта создана успешно.",
                            null));
        } catch (Exception e) {
            logger.error("Failed create product.", e);
            facesContext.addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Ошибка при создании новой технологической карты.",
                            null));
        } finally {
            HibernateUtils.rollback(persistenceTransaction, logger);
            HibernateUtils.close(persistenceSession, logger);

        }
    }

    public Object addProduct() {
        //TODO continue
        return null;
    }
}
