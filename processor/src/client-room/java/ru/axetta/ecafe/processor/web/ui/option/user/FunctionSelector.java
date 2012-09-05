/*
 * Copyright (c) 2012. Axetta LLC. All Rights Reserved.
 */

package ru.axetta.ecafe.processor.web.ui.option.user;

import ru.axetta.ecafe.processor.core.SpringApplicationContext;
import ru.axetta.ecafe.processor.core.persistence.Function;
import ru.axetta.ecafe.processor.core.persistence.utils.DAOService;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: Developer
 * Date: 18.06.2009
 * Time: 10:20:05
 * To change this template use File | Settings | File Templates.
 */
/*@Component
@Scope("singleton")*/
public class FunctionSelector {
    private static final Logger logger = LoggerFactory.getLogger(FunctionSelector.class);
   /* @PersistenceContext
    private EntityManager entityManager;*/

    public static class Item {

        private boolean selected;
        private final Long idOfFunction;
        private final String functionName;
        private final String functionDesc;

        public boolean isSelected() {
            return selected;
        }

        public void setSelected(boolean selected) {
            this.selected = selected;
        }

        public Long getIdOfFunction() {
            return idOfFunction;
        }

        public String getFunctionName() {
            return functionName;
        }

        public String getFunctionDesc() {
            return functionDesc;
        }

        public Item(Function function) {
            this.selected = false;
            this.idOfFunction = function.getIdOfFunction();
            this.functionName = function.getFunctionName();
            this.functionDesc = "";
                    //Function.getFunctionDesc(functionName);
        }
    }

    private List<Item> items = Collections.emptyList();

    public List<Item> getItems() {
        return items;
    }

    public void fill(Session session) throws Exception {
        List<Item> items = new LinkedList<Item>();
       // Criteria allFunctionsCriteria = session.createCriteria(Function.class);
      //  List allFunctions = allFunctionsCriteria.list();
        // Query q=entityManager.createQuery("from Function");

         DAOService daoService= DAOService.getInstance();

        List allFunctions = daoService.getFunction(null);
        for (Object object : allFunctions) {
            Function function = (Function) object;
            Item item = new Item(function);
            items.add(item);
        }
        this.items = items;
    }

    public void fill(Session session, Set<Function> selectedFunctions) throws Exception {
        List<Item> items = new LinkedList<Item>();
       // Criteria allFunctionsCriteria = session.createCriteria(Function.class);
        //List allFunctions = allFunctionsCriteria.list();
        DAOService daoService= DAOService.getInstance();
       // Query q=entityManager.createQuery("from Function");
        List allFunctions = daoService.getFunction(null);
        for (Object object : allFunctions) {
            Function function = (Function) object;
            Item item = new Item(function);
            if (selectedFunctions.contains(function)) {
                item.setSelected(true);
            }
            items.add(item);
        }
        this.items = items;
    }

    public Set<Function> getSelected(Session session) throws HibernateException {
        logger.info("start getSelected");
       DAOService daoService= DAOService.getInstance();

        Set<Function> selectedFunctions = new HashSet<Function>();
        for (Item item : items) {
            if (item.isSelected()) {
               // Function function = (Function) session.load(Function.class, item.getIdOfFunction());
               // Query q=entityManager.createQuery("from Function where idOfFunction=:idOfFunction");
               // q.setParameter("idOfFunction",item.getIdOfFunction());
                 logger.info("selectedFunction: "+item.functionName);
                Function function =daoService.getFunction(item.getIdOfFunction()).get(0);
                selectedFunctions.add(function);
            }
        }
        return selectedFunctions;
    }
}
