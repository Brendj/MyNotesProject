/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import ru.axetta.ecafe.processor.core.RuntimeContext;
import ru.axetta.ecafe.processor.core.persistence.Function;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;
import ru.axetta.ecafe.processor.core.utils.HibernateUtils;

import org.hibernate.Session;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 11.06.2009
 * Time: 17:01:58
 * To change this template use File | Settings | File Templates.
 */
/*@Component
@Scope("singleton")*/
public class FunctionSetConverter implements Converter {
  /*  @PersistenceContext
    private EntityManager entityManager;*/
    private static final Logger logger = LoggerFactory.getLogger(FunctionSetConverter.class);
    private static final int MAX_LEN = 32;
    private static final String TAIL_FILL = "...";

    public Object getAsObject(FacesContext facesContext, UIComponent uiComponent, String string) {
        return null;
    }

    public String getAsString(FacesContext facesContext, UIComponent uiComponent, Object object) {
        DAOService daoService= DAOService.getInstance();
        StringBuilder stringBuilder = new StringBuilder();
      ///  RuntimeContext runtimeContext = null;
      //  Session persistenceSession = null;
       // Transaction persistenceTransaction = null;
        try {
          //  runtimeContext = RuntimeContext.getInstance();
          //  persistenceSession = runtimeContext.createPersistenceSession();
          //  persistenceTransaction = persistenceSession.beginTransaction();
            boolean first = true;
            for (Object currObject : (Set) object) {
                if (!first) {
                    stringBuilder.append(", ");
                }
                Long idOfFunction = (Long) currObject;

               // Query q=entityManager.createQuery("from Function where idOfFunction=:idOfFunction");
               // q.setParameter("idOfFunction",idOfFunction);
                Function function = daoService.getFunction(idOfFunction).get(0);
               // Function function = (Function) persistenceSession.load(Function.class, idOfFunction);
                stringBuilder.append(function.getFunctionName());
                first = false;
                if (stringBuilder.length() > MAX_LEN) {
                    break;
                }
            }
          //  persistenceTransaction.commit();
         //   persistenceTransaction = null;
        } catch (Exception e) {
            logger.error("Failed to convert function set", e);
            return "";
        } finally {
          //  HibernateUtils.rollback(persistenceTransaction, logger);
           // HibernateUtils.close(persistenceSession, logger);
        }
        int len = stringBuilder.length();
        if (len > MAX_LEN) {
            return stringBuilder.substring(0, MAX_LEN - TAIL_FILL.length()) + TAIL_FILL;
        }
        return stringBuilder.toString();
    }

}